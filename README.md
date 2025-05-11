# SOS Application - Client

<p>
<img src="https://img.shields.io/badge/Kotlin-purple" alt="Kotlin 2.0.10"> 
<img src="https://img.shields.io/badge/Jetpack_Compose-1.4.3-purple?color=5C2D91" alt="Jetpack Compose 1.4.3"> 
<img src="https://img.shields.io/badge/minSdk_33-%233DDC84" alt="minSdk 33"> 
<img src="https://img.shields.io/badge/targetSdk_34-%23008B02" alt="targetSdk 34"> 
<img src="https://img.shields.io/badge/HTTPS-Secure-green?color=008B02" alt="HTTPS Secure"> 
<img src="https://img.shields.io/badge/WebSocket-WSS-blue?color=1E90FF" alt="WebSocket WSS">
<img src="https://img.shields.io/badge/JWT-Secure-blue?color=008B8B" alt="JWT Secure">
<img src="https://img.shields.io/badge/Retrofit-2.9.0-orange?color=FF4500" alt="Retrofit 2.9.0"> 
<img src="https://img.shields.io/badge/ViewModel-Android-green?color=3DDC84" alt="ViewModel Android"> 
<img src="https://img.shields.io/badge/Location_Service-Background-%23008B02" alt="Location Service Background"> 
<img src="https://img.shields.io/badge/Notifications-Android-purple?color=5C2D91" alt="Notifications Android"> 
</p>

### **Purpose:**
The SOS application is designed to enhance the efficiency of security companies by automating emergency request processes. It ensures quick, effective assistance in critical situations.

It integrates with the <a href="https://github.com/VoidSamuraj/SOS_Server" target="_blank">SOS Server</a> and <a href="https://github.com/VoidSamuraj/SOS_Guard_App" target="_blank">SOS Guard</a> applications.

![Client](https://github.com/user-attachments/assets/bf74424c-8580-411a-ad5f-e62a9904d54a)
![watch](https://github.com/user-attachments/assets/56626353-005d-4759-8d10-57b3f69cc37e)
<a href="https://drive.google.com/file/d/1KK_gapHuLEk1Xl45qQ2u7odBc6tHN9-Z/view?usp=drive_link" target="_blank">
  <img src="https://github.com/user-attachments/assets/3ac1f827-0db8-45a4-9767-167c3020b527" alt="demonstration" />
</a>




### **Key Features:**
- **SOS Button**: Instantly alerts the security team with a press of the red SOS button.
- **Location Updates**: Real-time location tracking for faster response.
- **Cancel or Direct Call**: Cancel the SOS alert or directly contact the alarm center.
- **Smartwatch Integration**: Compatible with Wear OS for seamless integration with smartwatches via Bluetooth.

### **Technologies:**
- **Kotlin**: A modern, statically-typed programming language compatible with Java.
- **Jetpack Compose**: A declarative UI framework for building Android apps.
- **WebSocket**: Real-time communication with the server.
- **Retrofit**: Simplifies server communication.

### **How it Works:**
1. **SOS Alert**: Press the SOS button to send a distress signal to the dispatcher and security team.
2. **Real-Time Location**: Location is updated in real-time for immediate response.
3. **Cancel or Call**: Cancel the SOS request or directly contact the alarm center for further guidance.

### **Requirements:**
Before running the app, configure `supportPhoneNumber` and `address` in the `MainActivity` file:
```kotlin
val supportPhoneNumber = "+48123456789"
const val address = "10.0.2.2:8443" // default local address (ensure it uses the same port as the server)
```
You probably also wanna to enable keystore cert checking (temporaty turned off) in `NetworkClient`

## Getting Started:
  1. Install the application.
  2. Register with your details.
  3. Enable location services for real-time tracking.

## License

---

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

