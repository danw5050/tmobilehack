Group=Default Group
ModulesStructureVersion=1
Type=Activity
Version=3.2
@EndOfDesignText@
#Region Module Attributes
	#FullScreen: true
	#IncludeTitle: false
#End Region

'Activity module
Sub Process_Globals
	Dim AStream As AsyncStreams
	Dim SensorsMap As Map
	Type SensorData (Name As String, ThreeValues As Boolean)
End Sub

Sub Globals
	Dim txtInput As EditText
	Dim txtLog As EditText
	Dim btnSend As Button
		Dim SensorsLabels As Map
	Dim val, val2 As Int = 0
	val = -40
	Dim horizontal, vertical As Float = 0
	Dim lab, lab2 As Label
	Dim time As Timer
	Private reset As Button
	Private WebView1 As WebView
	Dim we As WebViewExtras
	Dim iphtml As String = "<html><head><title>Android Webcam Server</title><script type="&Chr(34)&"text/javascript"&Chr(34)&"></script></head><body><img id="&Chr(34)&"img1"&Chr(34)&" onclick="&Chr(34)&"swBAspect()"&Chr(34)&" src="&Chr(34)&"http://ipval:8080//video"&Chr(34)&" alt="&Chr(34)&"Video feed"&Chr(34)&"/></body></html>"

	Private SeekBar1 As SeekBar
	
	Dim seekbarvalue As Int= 45
	Private icon As ImageView
End Sub

Sub Activity_Create(FirstTime As Boolean)
	Activity.LoadLayout("2")
	If AStream.IsInitialized = False Then
		AStream.Initialize(Main.serial1.InputStream, Main.serial1.OutputStream, "AStream")
	End If
	txtLog.Width = 100%x
	
	
	If FirstTime Then
		SensorsMap.Initialize
		Dim ps As PhoneSensors 'This object is only used to access the type constants.

		AddSensor(ps.TYPE_GYROSCOPE, "GYROSCOPE", True)
		'AddSensor(ps.TYPE_ORIENTATION, "ORIENTATION", True)
	End If
	SensorsLabels.Initialize 'SensorsLabels is not a Process_Globals object so we need to create it each time
	For i = 0 To SensorsMap.Size - 1
		Dim ps As PhoneSensors
		ps = SensorsMap.GetKeyAt(i)
		Dim lbl As Label
		lbl.Initialize("")
		lbl.TextColor = Colors.White 
		Activity.AddView(lbl, 10dip, 10dip + 50dip * i, 100%x - 10dip, 45dip)
		SensorsLabels.Put(ps, lbl)
	Next
	
	'make all unneccessru stuff invisible
	txtInput.Visible = False
	txtLog.Visible = False
	btnSend.Visible = False
	
	lab.Initialize("")
	Activity.AddView(lab,0%x,90%y,20%x,10%y)
	lab.Text = "ready"
	lab.textColor = Colors.White
	lab2.Initialize("")
	Activity.AddView(lab2,30%x,90%y,20%x,10%y)
	lab2.Text = "ready"
	lab2.Text = Colors.White
	
	reset.Left = 0%x
	reset.Top = 0%y
	reset.Width = 10%x
	reset.Height = 10%y
	
	'make webview full screen
	WebView1.Width = 100%x
	WebView1.Height = 100%y
	WebView1.Top = 0%y
	WebView1.Left = 0%x
	
	time.Initialize("time",40)
	time.Enabled = True
	
	'load the webpage
	WebView1.Visible = True
	WebView1.Enabled = True
	
	Dim url1 As String = iphtml.Replace("ipval",Main.ipAddress)
	'we.addJavascriptInterface(WebView1,"B4A")
	Dim wve As WebViewExtras
	wve.addWebChromeClient(WebView1, "WVE")
	WebView1.LoadUrl("http://192.168.0.107:8080//video")
'	we.executeJavascript(WebView1,"javascript: doCallBack();")


   SeekBar1.Left = 0%x
   SeekBar1.Width = 100%x
   SeekBar1.Top = 92%y
   SeekBar1.Height = 8%y
   
   icon.Left = 95%x
   icon.top = 0%y
   icon.Width = 5%x
   icon.Height = 5%x
End Sub



Sub AStream_NewData (Buffer() As Byte)
	LogMessage("You", BytesToString(Buffer, 0, Buffer.Length, "UTF8"))
End Sub

Sub AStream_Error
	ToastMessageShow("Connection is broken.", True)
	btnSend.Enabled = False
	txtInput.Enabled = False
End Sub

Sub AStream_Terminated
	AStream_Error
End Sub

Sub Activity_Resume
	
	
	For i = 0 To SensorsMap.Size - 1
		Dim ps As PhoneSensors
		Dim sd As SensorData
		Dim lbl As Label
		ps = SensorsMap.GetKeyAt(i)
		sd = SensorsMap.GetValueAt(i)
		lbl = SensorsLabels.Get(ps)
		If ps.StartListening("Sensor") = False Then
			lbl.Text = sd.Name & " is not supported."
			Log(sd.Name & " is not supported.")
		End If
	Next
	
	
End Sub

Sub Activity_Pause (UserClosed As Boolean)
	If UserClosed Then
		AStream.Close
	End If
	
	For i = 0 To SensorsMap.Size - 1
		Dim ps As PhoneSensors
		ps = SensorsMap.GetKeyAt(i)
		ps.StopListening
	Next
End Sub

Sub txtInput_EnterPressed
	If btnSend.Enabled = True Then btnSend_Click
End Sub
Sub btnSend_Click
	AStream.Write(txtInput.Text.GetBytes("UTF8"))
	txtInput.SelectAll
	txtInput.RequestFocus
	LogMessage("Me", txtInput.Text)
End Sub

Sub LogMessage(From As String, Msg As String)
	txtLog.Text = txtLog.Text & From & ": " & Msg & CRLF
	txtLog.SelectionStart = txtLog.Text.Length
End Sub

Sub AddSensor(SensorType As Int, Name As String, ThreeValues As Boolean) As SensorData
	Dim sd As SensorData
	sd.Initialize
	sd.Name = Name
	sd.ThreeValues = ThreeValues
	Dim ps As PhoneSensors
	ps.Initialize(SensorType)
	SensorsMap.Put(ps, sd)
	Log(Name & " MaxValue = " & ps.MaxValue)
End Sub

Sub Sensor_SensorChanged (Values() As Float)
	Dim ps As PhoneSensors
	Dim sd As SensorData
	Dim lbl As Label
	'Get the PhoneSensors object that raised this event.
	ps = Sender
	sd = SensorsMap.Get(ps) 'Get the associated SensorData obejct
	lbl = SensorsLabels.Get(ps) 'Get the associated Label.
	If sd.ThreeValues Then
		lbl.Text = sd.Name & " X=" & NumberFormat(Values(0), 0, 3) & ", Y=" & NumberFormat(Values(1), 0, 3) _ 
			& ", Z=" & NumberFormat(Values(2), 0, 3)	
		horizontal = NumberFormat(Values(0), 0, 3)
		vertical = NumberFormat(Values(1), 0, 3)
	Else
		lbl.Text = sd.Name & " = " & NumberFormat(Values(0), 0, 3)
	End If
End Sub
Sub time_tick
Try

If Abs(horizontal) > 0.3 Then
val = val + horizontal*8
Else If Abs(horizontal)>0.2 Then
val = val + horizontal*4
End If
 
If Abs(vertical) > 0.3 Then
val2 = val2 + vertical*8
Else If Abs(vertical)>0.2 Then
val2 = val2 + vertical*4
End If 
Catch
End Try

'If Abs(horizontal) > 0.3 Then
'val = val + horizontal*10
'Else If Abs(horizontal)>0.2 Then
'val = val + horizontal*5
'End If
 
'If Abs(vertical) > 0.3 Then
'val2 = val2 + vertical*10
'Else If Abs(vertical)>0.2 Then
'val2 = val2 + vertical*5
'End If 
'Catch
'End Try

lab.Text = val
lab2.text = val2

'assemble the string that is used to send a value
Dim sendingValue As String



If val < -360 Then
val = -360
Else If val > 360 Then
val = 360
End If 

If val2 < -360 Then
val2 = -360
Else If val2 > 360 Then
val2 = 360
End If 

Dim firstInt, secondInt, thirdInt As Int
firstInt = Ceil(val2/8)+45
secondInt = Ceil(val/6)+45
thirdInt = seekbarvalue

'make it reverse
firstInt = 90 - firstInt


	sendingValue = Chr(firstInt)&Chr(secondInt)&Chr(thirdInt)

AStream.Write(sendingValue.GetBytes("UTF8"))
End Sub

Sub reset_Click
	val = -40
	val2 =  0
	ToastMessageShow("Values have been reset.",False)
End Sub

Sub SeekBar1_ValueChanged (Value As Int, UserChanged As Boolean)
	seekbarvalue = Value
End Sub