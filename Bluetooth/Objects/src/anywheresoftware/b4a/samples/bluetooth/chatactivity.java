package anywheresoftware.b4a.samples.bluetooth;


import anywheresoftware.b4a.B4AMenuItem;
import android.app.Activity;
import android.os.Bundle;
import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.BALayout;
import anywheresoftware.b4a.B4AActivity;
import anywheresoftware.b4a.ObjectWrapper;
import anywheresoftware.b4a.objects.ActivityWrapper;
import java.lang.reflect.InvocationTargetException;
import anywheresoftware.b4a.B4AUncaughtException;
import anywheresoftware.b4a.debug.*;
import java.lang.ref.WeakReference;

public class chatactivity extends Activity implements B4AActivity{
	public static chatactivity mostCurrent;
	static boolean afterFirstLayout;
	static boolean isFirst = true;
    private static boolean processGlobalsRun = false;
	BALayout layout;
	public static BA processBA;
	BA activityBA;
    ActivityWrapper _activity;
    java.util.ArrayList<B4AMenuItem> menuItems;
	public static final boolean fullScreen = true;
	public static final boolean includeTitle = false;
    public static WeakReference<Activity> previousOne;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        mostCurrent = this;
		if (processBA == null) {
			processBA = new BA(this.getApplicationContext(), null, null, "anywheresoftware.b4a.samples.bluetooth", "anywheresoftware.b4a.samples.bluetooth.chatactivity");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
            
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (chatactivity).");
				p.finish();
			}
		}
        processBA.setActivityPaused(true);
        processBA.runHook("oncreate", this, null);
		if (!includeTitle) {
        	this.getWindow().requestFeature(android.view.Window.FEATURE_NO_TITLE);
        }
        if (fullScreen) {
        	getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN,   
        			android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
		
        processBA.sharedProcessBA.activityBA = null;
		layout = new BALayout(this);
		setContentView(layout);
		afterFirstLayout = false;
        WaitForLayout wl = new WaitForLayout();
        if (anywheresoftware.b4a.objects.ServiceHelper.StarterHelper.startFromActivity(this, processBA, wl, true))
		    BA.handler.postDelayed(wl, 5);

	}
	static class WaitForLayout implements Runnable {
		public void run() {
			if (afterFirstLayout)
				return;
			if (mostCurrent == null)
				return;
            
			if (mostCurrent.layout.getWidth() == 0) {
				BA.handler.postDelayed(this, 5);
				return;
			}
			mostCurrent.layout.getLayoutParams().height = mostCurrent.layout.getHeight();
			mostCurrent.layout.getLayoutParams().width = mostCurrent.layout.getWidth();
			afterFirstLayout = true;
			mostCurrent.afterFirstLayout();
		}
	}
	private void afterFirstLayout() {
        if (this != mostCurrent)
			return;
		activityBA = new BA(this, layout, processBA, "anywheresoftware.b4a.samples.bluetooth", "anywheresoftware.b4a.samples.bluetooth.chatactivity");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.isShellModeRuntimeCheck(processBA)) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "anywheresoftware.b4a.samples.bluetooth.chatactivity", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density, mostCurrent);
			_activity.reinitializeForShell(activityBA, "activity");
		}
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (chatactivity) Create, isFirst = " + isFirst + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (chatactivity) Resume **");
        processBA.raiseEvent(null, "activity_resume");
        if (android.os.Build.VERSION.SDK_INT >= 11) {
			try {
				android.app.Activity.class.getMethod("invalidateOptionsMenu").invoke(this,(Object[]) null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	public void addMenuItem(B4AMenuItem item) {
		if (menuItems == null)
			menuItems = new java.util.ArrayList<B4AMenuItem>();
		menuItems.add(item);
	}
	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		super.onCreateOptionsMenu(menu);
        try {
            if (processBA.subExists("activity_actionbarhomeclick")) {
                Class.forName("android.app.ActionBar").getMethod("setHomeButtonEnabled", boolean.class).invoke(
                    getClass().getMethod("getActionBar").invoke(this), true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (processBA.runHook("oncreateoptionsmenu", this, new Object[] {menu}))
            return true;
		if (menuItems == null)
			return false;
		for (B4AMenuItem bmi : menuItems) {
			android.view.MenuItem mi = menu.add(bmi.title);
			if (bmi.drawable != null)
				mi.setIcon(bmi.drawable);
            if (android.os.Build.VERSION.SDK_INT >= 11) {
				try {
                    if (bmi.addToBar) {
				        android.view.MenuItem.class.getMethod("setShowAsAction", int.class).invoke(mi, 1);
                    }
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			mi.setOnMenuItemClickListener(new B4AMenuItemsClickListener(bmi.eventName.toLowerCase(BA.cul)));
		}
        
		return true;
	}   
 @Override
 public boolean onOptionsItemSelected(android.view.MenuItem item) {
    if (item.getItemId() == 16908332) {
        processBA.raiseEvent(null, "activity_actionbarhomeclick");
        return true;
    }
    else
        return super.onOptionsItemSelected(item); 
}
@Override
 public boolean onPrepareOptionsMenu(android.view.Menu menu) {
    super.onPrepareOptionsMenu(menu);
    processBA.runHook("onprepareoptionsmenu", this, new Object[] {menu});
    return true;
    
 }
 protected void onStart() {
    super.onStart();
    processBA.runHook("onstart", this, null);
}
 protected void onStop() {
    super.onStop();
    processBA.runHook("onstop", this, null);
}
    public void onWindowFocusChanged(boolean hasFocus) {
       super.onWindowFocusChanged(hasFocus);
       if (processBA.subExists("activity_windowfocuschanged"))
           processBA.raiseEvent2(null, true, "activity_windowfocuschanged", false, hasFocus);
    }
	private class B4AMenuItemsClickListener implements android.view.MenuItem.OnMenuItemClickListener {
		private final String eventName;
		public B4AMenuItemsClickListener(String eventName) {
			this.eventName = eventName;
		}
		public boolean onMenuItemClick(android.view.MenuItem item) {
			processBA.raiseEventFromUI(item.getTitle(), eventName + "_click");
			return true;
		}
	}
    public static Class<?> getObject() {
		return chatactivity.class;
	}
    private Boolean onKeySubExist = null;
    private Boolean onKeyUpSubExist = null;
	@Override
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
        if (processBA.runHook("onkeydown", this, new Object[] {keyCode, event}))
            return true;
		if (onKeySubExist == null)
			onKeySubExist = processBA.subExists("activity_keypress");
		if (onKeySubExist) {
			if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK &&
					android.os.Build.VERSION.SDK_INT >= 18) {
				HandleKeyDelayed hk = new HandleKeyDelayed();
				hk.kc = keyCode;
				BA.handler.post(hk);
				return true;
			}
			else {
				boolean res = new HandleKeyDelayed().runDirectly(keyCode);
				if (res)
					return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	private class HandleKeyDelayed implements Runnable {
		int kc;
		public void run() {
			runDirectly(kc);
		}
		public boolean runDirectly(int keyCode) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keypress", false, keyCode);
			if (res == null || res == true) {
                return true;
            }
            else if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK) {
				finish();
				return true;
			}
            return false;
		}
		
	}
    @Override
	public boolean onKeyUp(int keyCode, android.view.KeyEvent event) {
        if (processBA.runHook("onkeyup", this, new Object[] {keyCode, event}))
            return true;
		if (onKeyUpSubExist == null)
			onKeyUpSubExist = processBA.subExists("activity_keyup");
		if (onKeyUpSubExist) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keyup", false, keyCode);
			if (res == null || res == true)
				return true;
		}
		return super.onKeyUp(keyCode, event);
	}
	@Override
	public void onNewIntent(android.content.Intent intent) {
        super.onNewIntent(intent);
		this.setIntent(intent);
        processBA.runHook("onnewintent", this, new Object[] {intent});
	}
    @Override 
	public void onPause() {
		super.onPause();
        if (_activity == null)
            return;
        if (this != mostCurrent)
			return;
		anywheresoftware.b4a.Msgbox.dismiss(true);
        BA.LogInfo("** Activity (chatactivity) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
        if (mostCurrent != null)
            processBA.raiseEvent2(_activity, true, "activity_pause", false, activityBA.activity.isFinishing());		
        processBA.setActivityPaused(true);
        mostCurrent = null;
        if (!activityBA.activity.isFinishing())
			previousOne = new WeakReference<Activity>(this);
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        processBA.runHook("onpause", this, null);
	}

	@Override
	public void onDestroy() {
        super.onDestroy();
		previousOne = null;
        processBA.runHook("ondestroy", this, null);
	}
    @Override 
	public void onResume() {
		super.onResume();
        mostCurrent = this;
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (activityBA != null) { //will be null during activity create (which waits for AfterLayout).
        	ResumeMessage rm = new ResumeMessage(mostCurrent);
        	BA.handler.post(rm);
        }
        processBA.runHook("onresume", this, null);
	}
    private static class ResumeMessage implements Runnable {
    	private final WeakReference<Activity> activity;
    	public ResumeMessage(Activity activity) {
    		this.activity = new WeakReference<Activity>(activity);
    	}
		public void run() {
            chatactivity mc = mostCurrent;
			if (mc == null || mc != activity.get())
				return;
			processBA.setActivityPaused(false);
            BA.LogInfo("** Activity (chatactivity) Resume **");
            if (mc != mostCurrent)
                return;
		    processBA.raiseEvent(mc._activity, "activity_resume", (Object[])null);
		}
    }
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
	      android.content.Intent data) {
		processBA.onActivityResult(requestCode, resultCode, data);
        processBA.runHook("onactivityresult", this, new Object[] {requestCode, resultCode});
	}
	private static void initializeGlobals() {
		processBA.raiseEvent2(null, true, "globals", false, (Object[])null);
	}
    public void onRequestPermissionsResult(int requestCode,
        String permissions[], int[] grantResults) {
        for (int i = 0;i < permissions.length;i++) {
            Object[] o = new Object[] {permissions[i], grantResults[i] == 0};
            processBA.raiseEventFromDifferentThread(null,null, 0, "activity_permissionresult", true, o);
        }
            
    }

public anywheresoftware.b4a.keywords.Common __c = null;
public static anywheresoftware.b4a.randomaccessfile.AsyncStreams _astream = null;
public static anywheresoftware.b4a.objects.collections.Map _sensorsmap = null;
public anywheresoftware.b4a.objects.EditTextWrapper _txtinput = null;
public anywheresoftware.b4a.objects.EditTextWrapper _txtlog = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnsend = null;
public anywheresoftware.b4a.objects.collections.Map _sensorslabels = null;
public static int _val = 0;
public static int _val2 = 0;
public static float _horizontal = 0f;
public static float _vertical = 0f;
public anywheresoftware.b4a.objects.LabelWrapper _lab = null;
public anywheresoftware.b4a.objects.LabelWrapper _lab2 = null;
public anywheresoftware.b4a.objects.Timer _time = null;
public anywheresoftware.b4a.objects.ButtonWrapper _reset = null;
public anywheresoftware.b4a.objects.WebViewWrapper _webview1 = null;
public uk.co.martinpearman.b4a.webviewextras.WebViewExtras _we = null;
public static String _iphtml = "";
public anywheresoftware.b4a.objects.SeekBarWrapper _seekbar1 = null;
public static int _seekbarvalue = 0;
public anywheresoftware.b4a.objects.ImageViewWrapper _icon = null;
public anywheresoftware.b4a.samples.bluetooth.main _main = null;

public static void initializeProcessGlobals() {
             try {
                Class.forName(BA.applicationContext.getPackageName() + ".main").getMethod("initializeProcessGlobals").invoke(null, null);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
}
public static class _sensordata{
public boolean IsInitialized;
public String Name;
public boolean ThreeValues;
public void Initialize() {
IsInitialized = true;
Name = "";
ThreeValues = false;
}
@Override
		public String toString() {
			return BA.TypeToString(this, false);
		}}
public static String  _activity_create(boolean _firsttime) throws Exception{
anywheresoftware.b4a.phone.Phone.PhoneSensors _ps = null;
int _i = 0;
anywheresoftware.b4a.objects.LabelWrapper _lbl = null;
String _url1 = "";
uk.co.martinpearman.b4a.webviewextras.WebViewExtras _wve = null;
 //BA.debugLineNum = 34;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 35;BA.debugLine="Activity.LoadLayout(\"2\")";
mostCurrent._activity.LoadLayout("2",mostCurrent.activityBA);
 //BA.debugLineNum = 36;BA.debugLine="If AStream.IsInitialized = False Then";
if (_astream.IsInitialized()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 37;BA.debugLine="AStream.Initialize(Main.serial1.InputStream, Mai";
_astream.Initialize(processBA,mostCurrent._main._serial1 /*anywheresoftware.b4a.objects.Serial*/ .getInputStream(),mostCurrent._main._serial1 /*anywheresoftware.b4a.objects.Serial*/ .getOutputStream(),"AStream");
 };
 //BA.debugLineNum = 39;BA.debugLine="txtLog.Width = 100%x";
mostCurrent._txtlog.setWidth(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA));
 //BA.debugLineNum = 42;BA.debugLine="If FirstTime Then";
if (_firsttime) { 
 //BA.debugLineNum = 43;BA.debugLine="SensorsMap.Initialize";
_sensorsmap.Initialize();
 //BA.debugLineNum = 44;BA.debugLine="Dim ps As PhoneSensors 'This object is only used";
_ps = new anywheresoftware.b4a.phone.Phone.PhoneSensors();
 //BA.debugLineNum = 46;BA.debugLine="AddSensor(ps.TYPE_GYROSCOPE, \"GYROSCOPE\", True)";
_addsensor(_ps.TYPE_GYROSCOPE,"GYROSCOPE",anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 49;BA.debugLine="SensorsLabels.Initialize 'SensorsLabels is not a";
mostCurrent._sensorslabels.Initialize();
 //BA.debugLineNum = 50;BA.debugLine="For i = 0 To SensorsMap.Size - 1";
{
final int step12 = 1;
final int limit12 = (int) (_sensorsmap.getSize()-1);
_i = (int) (0) ;
for (;_i <= limit12 ;_i = _i + step12 ) {
 //BA.debugLineNum = 51;BA.debugLine="Dim ps As PhoneSensors";
_ps = new anywheresoftware.b4a.phone.Phone.PhoneSensors();
 //BA.debugLineNum = 52;BA.debugLine="ps = SensorsMap.GetKeyAt(i)";
_ps = (anywheresoftware.b4a.phone.Phone.PhoneSensors)(_sensorsmap.GetKeyAt(_i));
 //BA.debugLineNum = 53;BA.debugLine="Dim lbl As Label";
_lbl = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 54;BA.debugLine="lbl.Initialize(\"\")";
_lbl.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 55;BA.debugLine="lbl.TextColor = Colors.White";
_lbl.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 56;BA.debugLine="Activity.AddView(lbl, 10dip, 10dip + 50dip * i,";
mostCurrent._activity.AddView((android.view.View)(_lbl.getObject()),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (10)),(int) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (10))+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (50))*_i),(int) (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA)-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (10))),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (45)));
 //BA.debugLineNum = 57;BA.debugLine="SensorsLabels.Put(ps, lbl)";
mostCurrent._sensorslabels.Put((Object)(_ps),(Object)(_lbl.getObject()));
 }
};
 //BA.debugLineNum = 61;BA.debugLine="txtInput.Visible = False";
mostCurrent._txtinput.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 62;BA.debugLine="txtLog.Visible = False";
mostCurrent._txtlog.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 63;BA.debugLine="btnSend.Visible = False";
mostCurrent._btnsend.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 65;BA.debugLine="lab.Initialize(\"\")";
mostCurrent._lab.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 66;BA.debugLine="Activity.AddView(lab,0%x,90%y,20%x,10%y)";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._lab.getObject()),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (0),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (90),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (20),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (10),mostCurrent.activityBA));
 //BA.debugLineNum = 67;BA.debugLine="lab.Text = \"ready\"";
mostCurrent._lab.setText(BA.ObjectToCharSequence("ready"));
 //BA.debugLineNum = 68;BA.debugLine="lab.textColor = Colors.White";
mostCurrent._lab.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 69;BA.debugLine="lab2.Initialize(\"\")";
mostCurrent._lab2.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 70;BA.debugLine="Activity.AddView(lab2,30%x,90%y,20%x,10%y)";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._lab2.getObject()),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (30),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (90),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (20),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (10),mostCurrent.activityBA));
 //BA.debugLineNum = 71;BA.debugLine="lab2.Text = \"ready\"";
mostCurrent._lab2.setText(BA.ObjectToCharSequence("ready"));
 //BA.debugLineNum = 72;BA.debugLine="lab2.Text = Colors.White";
mostCurrent._lab2.setText(BA.ObjectToCharSequence(anywheresoftware.b4a.keywords.Common.Colors.White));
 //BA.debugLineNum = 74;BA.debugLine="reset.Left = 0%x";
mostCurrent._reset.setLeft(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (0),mostCurrent.activityBA));
 //BA.debugLineNum = 75;BA.debugLine="reset.Top = 0%y";
mostCurrent._reset.setTop(anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (0),mostCurrent.activityBA));
 //BA.debugLineNum = 76;BA.debugLine="reset.Width = 10%x";
mostCurrent._reset.setWidth(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (10),mostCurrent.activityBA));
 //BA.debugLineNum = 77;BA.debugLine="reset.Height = 10%y";
mostCurrent._reset.setHeight(anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (10),mostCurrent.activityBA));
 //BA.debugLineNum = 80;BA.debugLine="WebView1.Width = 100%x";
mostCurrent._webview1.setWidth(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA));
 //BA.debugLineNum = 81;BA.debugLine="WebView1.Height = 100%y";
mostCurrent._webview1.setHeight(anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (100),mostCurrent.activityBA));
 //BA.debugLineNum = 82;BA.debugLine="WebView1.Top = 0%y";
mostCurrent._webview1.setTop(anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (0),mostCurrent.activityBA));
 //BA.debugLineNum = 83;BA.debugLine="WebView1.Left = 0%x";
mostCurrent._webview1.setLeft(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (0),mostCurrent.activityBA));
 //BA.debugLineNum = 85;BA.debugLine="time.Initialize(\"time\",40)";
mostCurrent._time.Initialize(processBA,"time",(long) (40));
 //BA.debugLineNum = 86;BA.debugLine="time.Enabled = True";
mostCurrent._time.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 89;BA.debugLine="WebView1.Visible = True";
mostCurrent._webview1.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 90;BA.debugLine="WebView1.Enabled = True";
mostCurrent._webview1.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 92;BA.debugLine="Dim url1 As String = iphtml.Replace(\"ipval\",Main.";
_url1 = mostCurrent._iphtml.replace("ipval",mostCurrent._main._ipaddress /*String*/ );
 //BA.debugLineNum = 94;BA.debugLine="Dim wve As WebViewExtras";
_wve = new uk.co.martinpearman.b4a.webviewextras.WebViewExtras();
 //BA.debugLineNum = 95;BA.debugLine="wve.addWebChromeClient(WebView1, \"WVE\")";
_wve.addWebChromeClient(mostCurrent.activityBA,(android.webkit.WebView)(mostCurrent._webview1.getObject()),"WVE");
 //BA.debugLineNum = 96;BA.debugLine="WebView1.LoadUrl(\"http://192.168.0.107:8080//vide";
mostCurrent._webview1.LoadUrl("http://192.168.0.107:8080//video");
 //BA.debugLineNum = 100;BA.debugLine="SeekBar1.Left = 0%x";
mostCurrent._seekbar1.setLeft(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (0),mostCurrent.activityBA));
 //BA.debugLineNum = 101;BA.debugLine="SeekBar1.Width = 100%x";
mostCurrent._seekbar1.setWidth(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA));
 //BA.debugLineNum = 102;BA.debugLine="SeekBar1.Top = 92%y";
mostCurrent._seekbar1.setTop(anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (92),mostCurrent.activityBA));
 //BA.debugLineNum = 103;BA.debugLine="SeekBar1.Height = 8%y";
mostCurrent._seekbar1.setHeight(anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (8),mostCurrent.activityBA));
 //BA.debugLineNum = 105;BA.debugLine="icon.Left = 95%x";
mostCurrent._icon.setLeft(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (95),mostCurrent.activityBA));
 //BA.debugLineNum = 106;BA.debugLine="icon.top = 0%y";
mostCurrent._icon.setTop(anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (0),mostCurrent.activityBA));
 //BA.debugLineNum = 107;BA.debugLine="icon.Width = 5%x";
mostCurrent._icon.setWidth(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (5),mostCurrent.activityBA));
 //BA.debugLineNum = 108;BA.debugLine="icon.Height = 5%x";
mostCurrent._icon.setHeight(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (5),mostCurrent.activityBA));
 //BA.debugLineNum = 109;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
int _i = 0;
anywheresoftware.b4a.phone.Phone.PhoneSensors _ps = null;
 //BA.debugLineNum = 146;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 147;BA.debugLine="If UserClosed Then";
if (_userclosed) { 
 //BA.debugLineNum = 148;BA.debugLine="AStream.Close";
_astream.Close();
 };
 //BA.debugLineNum = 151;BA.debugLine="For i = 0 To SensorsMap.Size - 1";
{
final int step4 = 1;
final int limit4 = (int) (_sensorsmap.getSize()-1);
_i = (int) (0) ;
for (;_i <= limit4 ;_i = _i + step4 ) {
 //BA.debugLineNum = 152;BA.debugLine="Dim ps As PhoneSensors";
_ps = new anywheresoftware.b4a.phone.Phone.PhoneSensors();
 //BA.debugLineNum = 153;BA.debugLine="ps = SensorsMap.GetKeyAt(i)";
_ps = (anywheresoftware.b4a.phone.Phone.PhoneSensors)(_sensorsmap.GetKeyAt(_i));
 //BA.debugLineNum = 154;BA.debugLine="ps.StopListening";
_ps.StopListening(processBA);
 }
};
 //BA.debugLineNum = 156;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
int _i = 0;
anywheresoftware.b4a.phone.Phone.PhoneSensors _ps = null;
anywheresoftware.b4a.samples.bluetooth.chatactivity._sensordata _sd = null;
anywheresoftware.b4a.objects.LabelWrapper _lbl = null;
 //BA.debugLineNum = 127;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 130;BA.debugLine="For i = 0 To SensorsMap.Size - 1";
{
final int step1 = 1;
final int limit1 = (int) (_sensorsmap.getSize()-1);
_i = (int) (0) ;
for (;_i <= limit1 ;_i = _i + step1 ) {
 //BA.debugLineNum = 131;BA.debugLine="Dim ps As PhoneSensors";
_ps = new anywheresoftware.b4a.phone.Phone.PhoneSensors();
 //BA.debugLineNum = 132;BA.debugLine="Dim sd As SensorData";
_sd = new anywheresoftware.b4a.samples.bluetooth.chatactivity._sensordata();
 //BA.debugLineNum = 133;BA.debugLine="Dim lbl As Label";
_lbl = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 134;BA.debugLine="ps = SensorsMap.GetKeyAt(i)";
_ps = (anywheresoftware.b4a.phone.Phone.PhoneSensors)(_sensorsmap.GetKeyAt(_i));
 //BA.debugLineNum = 135;BA.debugLine="sd = SensorsMap.GetValueAt(i)";
_sd = (anywheresoftware.b4a.samples.bluetooth.chatactivity._sensordata)(_sensorsmap.GetValueAt(_i));
 //BA.debugLineNum = 136;BA.debugLine="lbl = SensorsLabels.Get(ps)";
_lbl.setObject((android.widget.TextView)(mostCurrent._sensorslabels.Get((Object)(_ps))));
 //BA.debugLineNum = 137;BA.debugLine="If ps.StartListening(\"Sensor\") = False Then";
if (_ps.StartListening(processBA,"Sensor")==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 138;BA.debugLine="lbl.Text = sd.Name & \" is not supported.\"";
_lbl.setText(BA.ObjectToCharSequence(_sd.Name /*String*/ +" is not supported."));
 //BA.debugLineNum = 139;BA.debugLine="Log(sd.Name & \" is not supported.\")";
anywheresoftware.b4a.keywords.Common.LogImpl("81179660",_sd.Name /*String*/ +" is not supported.",0);
 };
 }
};
 //BA.debugLineNum = 144;BA.debugLine="End Sub";
return "";
}
public static anywheresoftware.b4a.samples.bluetooth.chatactivity._sensordata  _addsensor(int _sensortype,String _name,boolean _threevalues) throws Exception{
anywheresoftware.b4a.samples.bluetooth.chatactivity._sensordata _sd = null;
anywheresoftware.b4a.phone.Phone.PhoneSensors _ps = null;
 //BA.debugLineNum = 173;BA.debugLine="Sub AddSensor(SensorType As Int, Name As String, T";
 //BA.debugLineNum = 174;BA.debugLine="Dim sd As SensorData";
_sd = new anywheresoftware.b4a.samples.bluetooth.chatactivity._sensordata();
 //BA.debugLineNum = 175;BA.debugLine="sd.Initialize";
_sd.Initialize();
 //BA.debugLineNum = 176;BA.debugLine="sd.Name = Name";
_sd.Name /*String*/  = _name;
 //BA.debugLineNum = 177;BA.debugLine="sd.ThreeValues = ThreeValues";
_sd.ThreeValues /*boolean*/  = _threevalues;
 //BA.debugLineNum = 178;BA.debugLine="Dim ps As PhoneSensors";
_ps = new anywheresoftware.b4a.phone.Phone.PhoneSensors();
 //BA.debugLineNum = 179;BA.debugLine="ps.Initialize(SensorType)";
_ps.Initialize(_sensortype);
 //BA.debugLineNum = 180;BA.debugLine="SensorsMap.Put(ps, sd)";
_sensorsmap.Put((Object)(_ps),(Object)(_sd));
 //BA.debugLineNum = 181;BA.debugLine="Log(Name & \" MaxValue = \" & ps.MaxValue)";
anywheresoftware.b4a.keywords.Common.LogImpl("81507336",_name+" MaxValue = "+BA.NumberToString(_ps.getMaxValue()),0);
 //BA.debugLineNum = 182;BA.debugLine="End Sub";
return null;
}
public static String  _astream_error() throws Exception{
 //BA.debugLineNum = 117;BA.debugLine="Sub AStream_Error";
 //BA.debugLineNum = 118;BA.debugLine="ToastMessageShow(\"Connection is broken.\", True)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Connection is broken."),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 119;BA.debugLine="btnSend.Enabled = False";
mostCurrent._btnsend.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 120;BA.debugLine="txtInput.Enabled = False";
mostCurrent._txtinput.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 121;BA.debugLine="End Sub";
return "";
}
public static String  _astream_newdata(byte[] _buffer) throws Exception{
 //BA.debugLineNum = 113;BA.debugLine="Sub AStream_NewData (Buffer() As Byte)";
 //BA.debugLineNum = 114;BA.debugLine="LogMessage(\"You\", BytesToString(Buffer, 0, Buffer";
_logmessage("You",anywheresoftware.b4a.keywords.Common.BytesToString(_buffer,(int) (0),_buffer.length,"UTF8"));
 //BA.debugLineNum = 115;BA.debugLine="End Sub";
return "";
}
public static String  _astream_terminated() throws Exception{
 //BA.debugLineNum = 123;BA.debugLine="Sub AStream_Terminated";
 //BA.debugLineNum = 124;BA.debugLine="AStream_Error";
_astream_error();
 //BA.debugLineNum = 125;BA.debugLine="End Sub";
return "";
}
public static String  _btnsend_click() throws Exception{
 //BA.debugLineNum = 161;BA.debugLine="Sub btnSend_Click";
 //BA.debugLineNum = 162;BA.debugLine="AStream.Write(txtInput.Text.GetBytes(\"UTF8\"))";
_astream.Write(mostCurrent._txtinput.getText().getBytes("UTF8"));
 //BA.debugLineNum = 163;BA.debugLine="txtInput.SelectAll";
mostCurrent._txtinput.SelectAll();
 //BA.debugLineNum = 164;BA.debugLine="txtInput.RequestFocus";
mostCurrent._txtinput.RequestFocus();
 //BA.debugLineNum = 165;BA.debugLine="LogMessage(\"Me\", txtInput.Text)";
_logmessage("Me",mostCurrent._txtinput.getText());
 //BA.debugLineNum = 166;BA.debugLine="End Sub";
return "";
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 13;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 14;BA.debugLine="Dim txtInput As EditText";
mostCurrent._txtinput = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 15;BA.debugLine="Dim txtLog As EditText";
mostCurrent._txtlog = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 16;BA.debugLine="Dim btnSend As Button";
mostCurrent._btnsend = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 17;BA.debugLine="Dim SensorsLabels As Map";
mostCurrent._sensorslabels = new anywheresoftware.b4a.objects.collections.Map();
 //BA.debugLineNum = 18;BA.debugLine="Dim val, val2 As Int = 0";
_val = 0;
_val2 = (int) (0);
 //BA.debugLineNum = 19;BA.debugLine="val2 = 40";
_val2 = (int) (40);
 //BA.debugLineNum = 20;BA.debugLine="Dim horizontal, vertical As Float = 0";
_horizontal = 0f;
_vertical = (float) (0);
 //BA.debugLineNum = 21;BA.debugLine="Dim lab, lab2 As Label";
mostCurrent._lab = new anywheresoftware.b4a.objects.LabelWrapper();
mostCurrent._lab2 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 22;BA.debugLine="Dim time As Timer";
mostCurrent._time = new anywheresoftware.b4a.objects.Timer();
 //BA.debugLineNum = 23;BA.debugLine="Private reset As Button";
mostCurrent._reset = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 24;BA.debugLine="Private WebView1 As WebView";
mostCurrent._webview1 = new anywheresoftware.b4a.objects.WebViewWrapper();
 //BA.debugLineNum = 25;BA.debugLine="Dim we As WebViewExtras";
mostCurrent._we = new uk.co.martinpearman.b4a.webviewextras.WebViewExtras();
 //BA.debugLineNum = 26;BA.debugLine="Dim iphtml As String = \"<html><head><title>Androi";
mostCurrent._iphtml = "<html><head><title>Android Webcam Server</title><script type="+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (34)))+"text/javascript"+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (34)))+"></script></head><body><img id="+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (34)))+"img1"+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (34)))+" onclick="+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (34)))+"swBAspect()"+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (34)))+" src="+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (34)))+"http://ipval:8080//video"+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (34)))+" alt="+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (34)))+"Video feed"+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (34)))+"/></body></html>";
 //BA.debugLineNum = 28;BA.debugLine="Private SeekBar1 As SeekBar";
mostCurrent._seekbar1 = new anywheresoftware.b4a.objects.SeekBarWrapper();
 //BA.debugLineNum = 30;BA.debugLine="Dim seekbarvalue As Int= 45";
_seekbarvalue = (int) (45);
 //BA.debugLineNum = 31;BA.debugLine="Private icon As ImageView";
mostCurrent._icon = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 32;BA.debugLine="End Sub";
return "";
}
public static String  _logmessage(String _from,String _msg) throws Exception{
 //BA.debugLineNum = 168;BA.debugLine="Sub LogMessage(From As String, Msg As String)";
 //BA.debugLineNum = 169;BA.debugLine="txtLog.Text = txtLog.Text & From & \": \" & Msg & C";
mostCurrent._txtlog.setText(BA.ObjectToCharSequence(mostCurrent._txtlog.getText()+_from+": "+_msg+anywheresoftware.b4a.keywords.Common.CRLF));
 //BA.debugLineNum = 170;BA.debugLine="txtLog.SelectionStart = txtLog.Text.Length";
mostCurrent._txtlog.setSelectionStart(mostCurrent._txtlog.getText().length());
 //BA.debugLineNum = 171;BA.debugLine="End Sub";
return "";
}
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 7;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 8;BA.debugLine="Dim AStream As AsyncStreams";
_astream = new anywheresoftware.b4a.randomaccessfile.AsyncStreams();
 //BA.debugLineNum = 9;BA.debugLine="Dim SensorsMap As Map";
_sensorsmap = new anywheresoftware.b4a.objects.collections.Map();
 //BA.debugLineNum = 10;BA.debugLine="Type SensorData (Name As String, ThreeValues As B";
;
 //BA.debugLineNum = 11;BA.debugLine="End Sub";
return "";
}
public static String  _reset_click() throws Exception{
 //BA.debugLineNum = 266;BA.debugLine="Sub reset_Click";
 //BA.debugLineNum = 267;BA.debugLine="val = 0";
_val = (int) (0);
 //BA.debugLineNum = 268;BA.debugLine="val2 =  40";
_val2 = (int) (40);
 //BA.debugLineNum = 269;BA.debugLine="ToastMessageShow(\"Values have been reset.\",False)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Values have been reset."),anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 270;BA.debugLine="End Sub";
return "";
}
public static String  _seekbar1_valuechanged(int _value,boolean _userchanged) throws Exception{
 //BA.debugLineNum = 272;BA.debugLine="Sub SeekBar1_ValueChanged (Value As Int, UserChang";
 //BA.debugLineNum = 273;BA.debugLine="seekbarvalue = Value";
_seekbarvalue = _value;
 //BA.debugLineNum = 274;BA.debugLine="End Sub";
return "";
}
public static String  _sensor_sensorchanged(float[] _values) throws Exception{
anywheresoftware.b4a.phone.Phone.PhoneSensors _ps = null;
anywheresoftware.b4a.samples.bluetooth.chatactivity._sensordata _sd = null;
anywheresoftware.b4a.objects.LabelWrapper _lbl = null;
 //BA.debugLineNum = 184;BA.debugLine="Sub Sensor_SensorChanged (Values() As Float)";
 //BA.debugLineNum = 185;BA.debugLine="Dim ps As PhoneSensors";
_ps = new anywheresoftware.b4a.phone.Phone.PhoneSensors();
 //BA.debugLineNum = 186;BA.debugLine="Dim sd As SensorData";
_sd = new anywheresoftware.b4a.samples.bluetooth.chatactivity._sensordata();
 //BA.debugLineNum = 187;BA.debugLine="Dim lbl As Label";
_lbl = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 189;BA.debugLine="ps = Sender";
_ps = (anywheresoftware.b4a.phone.Phone.PhoneSensors)(anywheresoftware.b4a.keywords.Common.Sender(mostCurrent.activityBA));
 //BA.debugLineNum = 190;BA.debugLine="sd = SensorsMap.Get(ps) 'Get the associated Senso";
_sd = (anywheresoftware.b4a.samples.bluetooth.chatactivity._sensordata)(_sensorsmap.Get((Object)(_ps)));
 //BA.debugLineNum = 191;BA.debugLine="lbl = SensorsLabels.Get(ps) 'Get the associated L";
_lbl.setObject((android.widget.TextView)(mostCurrent._sensorslabels.Get((Object)(_ps))));
 //BA.debugLineNum = 192;BA.debugLine="If sd.ThreeValues Then";
if (_sd.ThreeValues /*boolean*/ ) { 
 //BA.debugLineNum = 193;BA.debugLine="lbl.Text = sd.Name & \" X=\" & NumberFormat(Values";
_lbl.setText(BA.ObjectToCharSequence(_sd.Name /*String*/ +" X="+anywheresoftware.b4a.keywords.Common.NumberFormat(_values[(int) (0)],(int) (0),(int) (3))+", Y="+anywheresoftware.b4a.keywords.Common.NumberFormat(_values[(int) (1)],(int) (0),(int) (3))+", Z="+anywheresoftware.b4a.keywords.Common.NumberFormat(_values[(int) (2)],(int) (0),(int) (3))));
 //BA.debugLineNum = 195;BA.debugLine="horizontal = NumberFormat(Values(0), 0, 3)";
_horizontal = (float)(Double.parseDouble(anywheresoftware.b4a.keywords.Common.NumberFormat(_values[(int) (0)],(int) (0),(int) (3))));
 //BA.debugLineNum = 196;BA.debugLine="vertical = NumberFormat(Values(1), 0, 3)";
_vertical = (float)(Double.parseDouble(anywheresoftware.b4a.keywords.Common.NumberFormat(_values[(int) (1)],(int) (0),(int) (3))));
 }else {
 //BA.debugLineNum = 198;BA.debugLine="lbl.Text = sd.Name & \" = \" & NumberFormat(Values";
_lbl.setText(BA.ObjectToCharSequence(_sd.Name /*String*/ +" = "+anywheresoftware.b4a.keywords.Common.NumberFormat(_values[(int) (0)],(int) (0),(int) (3))));
 };
 //BA.debugLineNum = 200;BA.debugLine="End Sub";
return "";
}
public static String  _time_tick() throws Exception{
String _sendingvalue = "";
int _firstint = 0;
int _secondint = 0;
int _thirdint = 0;
 //BA.debugLineNum = 201;BA.debugLine="Sub time_tick";
 //BA.debugLineNum = 202;BA.debugLine="Try";
try { //BA.debugLineNum = 204;BA.debugLine="If Abs(horizontal) > 0.3 Then";
if (anywheresoftware.b4a.keywords.Common.Abs(_horizontal)>0.3) { 
 //BA.debugLineNum = 205;BA.debugLine="val = val + horizontal*8";
_val = (int) (_val+_horizontal*8);
 }else if(anywheresoftware.b4a.keywords.Common.Abs(_horizontal)>0.2) { 
 //BA.debugLineNum = 207;BA.debugLine="val = val + horizontal*4";
_val = (int) (_val+_horizontal*4);
 };
 //BA.debugLineNum = 210;BA.debugLine="If Abs(vertical) > 0.3 Then";
if (anywheresoftware.b4a.keywords.Common.Abs(_vertical)>0.3) { 
 //BA.debugLineNum = 211;BA.debugLine="val2 = val2 + vertical*8";
_val2 = (int) (_val2+_vertical*8);
 }else if(anywheresoftware.b4a.keywords.Common.Abs(_vertical)>0.2) { 
 //BA.debugLineNum = 213;BA.debugLine="val2 = val2 + vertical*4";
_val2 = (int) (_val2+_vertical*4);
 };
 } 
       catch (Exception e13) {
			processBA.setLastException(e13); };
 //BA.debugLineNum = 232;BA.debugLine="lab.Text = val";
mostCurrent._lab.setText(BA.ObjectToCharSequence(_val));
 //BA.debugLineNum = 233;BA.debugLine="lab2.text = val2";
mostCurrent._lab2.setText(BA.ObjectToCharSequence(_val2));
 //BA.debugLineNum = 236;BA.debugLine="Dim sendingValue As String";
_sendingvalue = "";
 //BA.debugLineNum = 240;BA.debugLine="If val < -360 Then";
if (_val<-360) { 
 //BA.debugLineNum = 241;BA.debugLine="val = -360";
_val = (int) (-360);
 }else if(_val>360) { 
 //BA.debugLineNum = 243;BA.debugLine="val = 360";
_val = (int) (360);
 };
 //BA.debugLineNum = 246;BA.debugLine="If val2 < -360 Then";
if (_val2<-360) { 
 //BA.debugLineNum = 247;BA.debugLine="val2 = -360";
_val2 = (int) (-360);
 }else if(_val2>360) { 
 //BA.debugLineNum = 249;BA.debugLine="val2 = 360";
_val2 = (int) (360);
 };
 //BA.debugLineNum = 252;BA.debugLine="Dim firstInt, secondInt, thirdInt As Int";
_firstint = 0;
_secondint = 0;
_thirdint = 0;
 //BA.debugLineNum = 253;BA.debugLine="firstInt = Ceil(val2/6.5)+45";
_firstint = (int) (anywheresoftware.b4a.keywords.Common.Ceil(_val2/(double)6.5)+45);
 //BA.debugLineNum = 254;BA.debugLine="secondInt = Ceil(val/6)+45";
_secondint = (int) (anywheresoftware.b4a.keywords.Common.Ceil(_val/(double)6)+45);
 //BA.debugLineNum = 255;BA.debugLine="thirdInt = seekbarvalue";
_thirdint = _seekbarvalue;
 //BA.debugLineNum = 258;BA.debugLine="firstInt = 90 - firstInt";
_firstint = (int) (90-_firstint);
 //BA.debugLineNum = 261;BA.debugLine="sendingValue = Chr(firstInt)&Chr(secondInt)&Chr(t";
_sendingvalue = BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr(_firstint))+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr(_secondint))+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr(_thirdint));
 //BA.debugLineNum = 263;BA.debugLine="AStream.Write(sendingValue.GetBytes(\"UTF8\"))";
_astream.Write(_sendingvalue.getBytes("UTF8"));
 //BA.debugLineNum = 264;BA.debugLine="End Sub";
return "";
}
public static String  _txtinput_enterpressed() throws Exception{
 //BA.debugLineNum = 158;BA.debugLine="Sub txtInput_EnterPressed";
 //BA.debugLineNum = 159;BA.debugLine="If btnSend.Enabled = True Then btnSend_Click";
if (mostCurrent._btnsend.getEnabled()==anywheresoftware.b4a.keywords.Common.True) { 
_btnsend_click();};
 //BA.debugLineNum = 160;BA.debugLine="End Sub";
return "";
}
}
