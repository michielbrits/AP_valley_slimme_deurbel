int val = 0;       // variable to store the value coming from the sensor
int potPin = A0;    // select the input pin for the potentiometer
unsigned long previousMillis = 0;
const long interval = 2000;
char c = "";
String serialread = "";
const int temperaturePin = 0;
int testled = 9;

void setup()
{
  Serial.begin(57600);
  pinMode(5, OUTPUT);
  pinMode(testled, OUTPUT);
  Serial.println("");
  Serial.println("Remember to to set Both NL & CR in the serial monitor.");
  Serial.println("Ready");
  Serial.println("");
  //digitalWrite(5, HIGH);
  digitalWrite(9, HIGH);
}
float getVoltage(int pin)
{
  return (analogRead(pin) * 0.004882814);
}
void loop()
{
 /* if ( Serial.available() )
  {
    serialread = Serial.readString();
    serialread.trim();
    if (serialread == "ON")
    {
      digitalWrite(5, HIGH);
    }
    else
    {
      digitalWrite(5, LOW);
    }
  } */
  unsigned long currentMillis = millis();
  if (currentMillis - previousMillis >= interval)
  {
    previousMillis = currentMillis;
    float voltage, degreesC, degreesF;
    voltage = getVoltage(temperaturePin);
    degreesC = (voltage - 0.5) * 100.0;
    //Serial.println(serialread);
    //Serial.println("voltage:");
    //Serial.println(voltage);
    Serial.print("deg C: ");
    Serial.println(degreesC);
  }
}


