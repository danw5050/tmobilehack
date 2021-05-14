#include <Servo.h>

Servo servoV;
Servo servoH;
Servo servoA;

const int blinkingLED = 13;
const int relayLED = 8;

String input = "";


void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
  pinMode(blinkingLED, OUTPUT);
  pinMode(relayLED, OUTPUT);
  servoV.attach(3);
  servoH.attach(4);
  servoA.attach(5);
}

void loop() {
  // put your main code here, to run repeatedly:
  if (Serial.available())
  {
    digitalWrite(blinkingLED, HIGH);
    delay(20); // wait a bit for the entire message to arrive
    input = ""; //clear the previous input

    while (Serial.available() > 0)
    {

      char character = Serial.read(); //read the values in the serial port

      input.concat(character);//add the new character to the string


    }
    digitalWrite(blinkingLED, LOW);

    //Serial.println("Here1");
    if (input.length() > 1)
    {
  //    if (input == "zz")
//{ digitalWrite(relayLED, HIGH);}
 //     else if (input = "yy")
//{ digitalWrite(relayLED, LOW);}
      
      //Serial.write("Input: ");
      //Serial.print(input);
      //char a = input[0];
      //Serial.println(a);

      float value1 = int(input[0]);
      float value2 = int(input[1]);
      float value3 = int(input[2]);

     // Serial.println((value1));
     // Serial.println((value2));
      servoV.write(value1 * 1.4);
      servoH.write(value2*1.4);
      servoA.write(value3*2);
      delay(10);
      input = "";
    }
  }

  //loop delay
  delay(10);
}
