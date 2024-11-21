# SOS Application - Client

<p>
<img src="https://img.shields.io/badge/Kotlin-purple" alt="Kotlin 2.0.10"> 
<img src="https://img.shields.io/badge/Jetpack_Compose-1.4.3-purple?color=5C2D91" alt="Jetpack Compose 1.4.3"> 
<img src="https://img.shields.io/badge/minSdk_33-%233DDC84" alt="minSdk 33"> 
<img src="https://img.shields.io/badge/targetSdk_34-%23008B02" alt="targetSdk 34 "> 
<img src="https://img.shields.io/badge/HTTPS-Secure-green?color=008B02" alt="HTTPS Secure"> 
<img src="https://img.shields.io/badge/WebSocket-WSS-blue?color=1E90FF" alt="WebSocket WSS">
<img src="https://img.shields.io/badge/JWT-Secure-blue?color=008B8B" alt="JWT Secure">
<img src="https://img.shields.io/badge/Retrofit-2.9.0-orange?color=FF4500" alt="Retrofit 2.9.0"> 
<img src="https://img.shields.io/badge/ViewModel-Android-green?color=3DDC84" alt="ViewModel Android"> 
<img src="https://img.shields.io/badge/Location_Service-Background-%23008B02" alt="Location Service Background"> 
<img src="https://img.shields.io/badge/Notifications-Android-purple?color=5C2D91" alt="Notifications Android"> 
</p>

## Purpose:
The SOS application is designed to support the operations of a security company by automating and speeding up processes related to handling emergency requests. It ensures that users get quick and effective help in critical situations.
It works with <a href="https://github.com/VoidSamuraj/SOS_Server" target="_blank">SOS Server</a> and <a href="https://github.com/VoidSamuraj/SOS_Guard_App" target="_blank">SOS Guard</a>

![client](https://github.com/user-attachments/assets/cfb9ec30-0d2a-44ed-ab9a-646be7b1a207)

### Key Features:
- **SOS Button**: The client can send an emergency request by pressing the red SOS button, which will immediately alert the security team.
- **Location Updates**: The client’s location is updated at short intervals for accurate tracking and quick response.
- **Cancel or Direct Call**: The client can cancel the SOS alert or directly call the alarm center if needed.

## Technologies:
- **Kotlin**: A statically typed programming language compatible with Java, ideal for cross-platform development.
- **Jetpack Compose**: A framework for building modern UIs for Android apps in a declarative way.
- **WebSocket**: For real-time communication with the server.
- **Retrofit**: For simple server communication.

### How it Works:
1. **Sending an SOS Alert**: The client clicks the SOS button, which immediately sends a request for help to the dispatcher and security team.
2. **Real-Time Location Tracking**: The client’s location is shared in real-time with the security team, ensuring fast assistance.
3. **Canceling or Calling the Alarm Center**: If the situation changes, the client can cancel the alert or directly call the alarm center for further instructions.

## Requirements
Before running the project, set supportPhoneNumber to valid one and address on which you host server in `MainActivity` file:
```
val supportPhoneNumber = "+48123456789"
const val address="10.0.2.2:8443" //default local intelij adress(make sure it uses the same port as the server)
```
You probably also wanna to enable keystore cert checking (temporaty turned off) in `NetworkClient`

## Getting Started:
To start using the application, install it and register with your details. Make sure your location services are enabled for real-time tracking and navigation.
