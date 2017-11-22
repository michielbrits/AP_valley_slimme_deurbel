#include <ESP8266HTTPClient.h>
#include <ESP8266WiFi.h>
#include <SoftwareSerial.h>
String userid = "AAA000";
String serialread = "";
const int ARRAYSIZE = 10;
String data[ARRAYSIZE];
void setup() {
 
  Serial.begin(57600);
  while (!Serial) {
    ; // wait for serial port to connect. Needed for native USB port only
  }                                 
  WiFi.begin("iot", "iot12345");   //WiFi connection
 
  while (WiFi.status() != WL_CONNECTED) {  //Wait for the WiFI connection completion
 
    delay(500);
    Serial.println("Waiting for connection");
 
  }
 
}
 
void loop() {

 if(WiFi.status()== WL_CONNECTED){   //Check WiFi connection status

  //TODO serial proberen werkend te krijgen
  /* while ( Serial.available() ){
  string = Serial.readString();
 } */
     if (Serial.available()) {
    //Serial.write(mySerial.read());
   serialread = Serial.readString();
   serialread.trim();
   
  // for(int i=1; i<=3; i++) {
   //     data[i] = Serial.readStringUntil(':');
   // }
   HTTPClient http;    //Declare object of class HTTPClient
   http.begin("http://michielserver.com/AP_valley/Addtimestamps.php");      //Specify request destination
   http.addHeader("Content-Type", "application/x-www-form-urlencoded");  //Specify content-type header
 
   int httpCode = http.POST("userid="+userid);   //Send the request
   String payload = http.getString();                  //Get the response payload
 
   //Serial.println(httpCode);   //Print HTTP return code usefull for debug purpose
   Serial.print(payload);    //Print request response payload 
 
   http.end();  //Close connection
  }
 
 
 }else{
 
    Serial.println("Error in WiFi connection");   
 
 }
 
  //delay(1000);  //Send a request every 5 seconds 
 
}
