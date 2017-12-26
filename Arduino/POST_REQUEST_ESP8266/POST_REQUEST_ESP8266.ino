#include <ESP8266HTTPClient.h>
#include <ESP8266WiFi.h>
#include <SoftwareSerial.h>
//WiFiClient espClient;
//PubSubClient client(espClient);
String userid = "AAA000";
String serialread = "";
const int ARRAYSIZE = 10;
bool iotinrange = false;
String data[ARRAYSIZE];
void setup() {

  Serial.begin(57600);
  Serial.println("hzzzy");
  Serial.begin(57600);
  while (!Serial) {
    ; // wait for serial port to connect. Needed for native USB port only
  }
  int n = WiFi.scanNetworks();
  for (int i = 0; i < n; ++i)
  {
    if (WiFi.SSID(i) == "iot")
    {
      iotinrange = true;
      Serial.println("iot in range");
    }
  }
  if (iotinrange == true)
  {
    WiFi.begin("iot", "iot12345");   //WiFi connection
    while (WiFi.status() != WL_CONNECTED) {  //Wait for the WiFI connection completion
    delay(500);
    Serial.println("Waiting for connection");
    }
  }
  else
  {
    Serial.println("iot not found");
    connectwifi();
  }
  /* if(iotinrange == false)
    {
       connectwifi();
    } */
  /*  int n = WiFi.scanNetworks();
    for (int i = 0; i < n; ++i)
    {
      // Print SSID and RSSI for each network found
      if (WiFi.SSID(i) == "iot")
      { //enter the ssid which you want to search
        iotinrange = true;
        WiFi.begin("iot", "iot12345");   //WiFi connection
        while (WiFi.status() != WL_CONNECTED)
        { //Wait for the WiFI connection completion
          delay(500);
          Serial.println("Waiting for connection");
        }
      }
    }

    delay(5000); */

}
void connectwifi() {
  /*WiFi.begin("iot", "iot12345");   //WiFi connection
      while (WiFi.status() != WL_CONNECTED)
      { //Wait for the WiFI connection completion
        delay(500);
        Serial.println("Waiting for connection");
      } */
  Serial.println("\nPress WPS button on your router ...");
  delay(5000);
  startWPSPBC();
}
bool startWPSPBC() {
  //Serial.println("WPS config start");
  // WPS works in STA (Station mode) only -> not working in WIFI_AP_STA !!!
  WiFi.mode(WIFI_STA);
  delay(1000);
  WiFi.begin("foobar", ""); // make a failed connection
  while (WiFi.status() == WL_DISCONNECTED) {
    delay(500);
    Serial.print(".");
  }
  bool wpsSuccess = WiFi.beginWPSConfig();
  if (wpsSuccess) {
    // Well this means not always success :-/ in case of a timeout we have an empty ssid
    String newSSID = WiFi.SSID();
    if (newSSID.length() > 0) {
      // WPSConfig has already connected in STA mode successfully to the new station.
      Serial.printf("WPS finished. Connected successfull to SSID '%s'", newSSID.c_str());
      // save to config and use next time or just use - WiFi.begin(WiFi.SSID().c_str(),WiFi.psk().c_str());
      //qConfig.wifiSSID = newSSID;
      //qConfig.wifiPWD = WiFi.psk();
      //saveConfig();
    } else {
      wpsSuccess = false;
      //delay(5000);
      connectwifi();
    }
  }
  return wpsSuccess;
}

void loop() {

  if (WiFi.status() == WL_CONNECTED) { //Check WiFi connection status
    Serial.println("Connected");


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

      int httpCode = http.POST("userid=" + userid); //Send the request
      String payload = http.getString();                  //Get the response payload

      //Serial.println(httpCode);   //Print HTTP return code usefull for debug purpose
      Serial.print(payload);    //Print request response payload

      http.end();  //Close connection
    }


  } else {

    Serial.println("Error in WiFi connection");
    //setup();
    //connectwifi();

  }

  //delay(1000);  //Send a request every 5 seconds

}

