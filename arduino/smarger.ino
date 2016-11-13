//I found most of this code online. Only some of it is my work
 
#include <SoftwareSerial.h> //Serial library
 
/**
 * Arduino connection HC-05 connection: 
 * HC-05  | Arduino
 * TX     | 1
 * RX     | 0
*/

SoftwareSerial bt (0,1);  //RX, TX (Switched on the Bluetooth - RX -> TX | TX -> RX)
int LEDPin = 13; //LED PIN on Arduino
int btdata; // the data given from the computer
 
void setup() {
  bt.begin(9600); //Open the serial port
  pinMode (LEDPin, OUTPUT);
  digitalWrite(LEDPin, HIGH); //HIGH will cause Vgs = 0
  arduino_FastPWM();
}
 
void loop() {
  if (bt.available()) { //if serial is available
    btdata = bt.read(); //read from the serial connection
    if (btdata == '1') { //Vgs = 5V
    digitalWrite (LEDPin, LOW);// LOW causes Vgs = whatever
      bt.println ("Start charging!");
    }
    if (btdata == '0') { //if we received 0, Vgs = 0
      digitalWrite (LEDPin, HIGH);
      bt.println ("Stop charging!");
    }
  }
  delay (100); //prepare for data
}

void arduino_FastPWM() {
  // This will activate a PWM frequency of 62500 Hz on the 
  // PWM pins associated with Timer1
  // Arduino UNO ==> pin-9 and pin-10
  // Arduino MEGA ==>  pin-11 and pin-12
#if defined(__AVR_ATmega328P__)
  analogWrite(9,127); // let Arduino do PWM timer and pin initialization
#elif defined(__AVR_ATmega1280__) || defined(__AVR_ATmega2560__)  
  analogWrite(11,100); // // let Arduino do PWM timer and pin initialization
#else
  *** wrong board ***
#endif  
  // finally set fast-PWM at highest possible frequency
  TCCR1A = _BV(COM1A1) | _BV(WGM10);
  TCCR1B = _BV(CS10) | _BV(WGM12);
}
