# MerdekaBle10b

The goal of this project is to develop an android application which detects other smartphones with the application active via Bluetooth Low Energy (BLE) technology 
and perform a coordinated action, which is playing the sound snippet “Merdeka!” in unison. This action is to be performed when there is more than one devices with our app
intalled. The time to play the sound is decided by the application. The list of devices detected is stored in an online database.

Even though the current implementation of this concept serves little purpose, the concept can be used for more useful purposes such as social distancing and contact 
tracing. This implementation follows a peer-to-peer like network, so two devices do not necessarily need to be within range of each other as long as there is a common 
device in between them.

# User Manual

Pre-requisite for code development: Android Studio
Code Development
1.	Download this repository and import into Android Studio
2.	Build and run the application on Android Studio

# Using Merdeka App
1.	Download the APK file onto your desired Android Device. APK file can be found here.
2.	Once the APK file is done downloading on the device, launch the app and the user will be brought to the Home Page of the app.

 ![image](https://user-images.githubusercontent.com/93644714/165025392-6765995f-5554-400a-93d9-a207cf75d11f.png)

3.	In the Home page the user will 3 buttons being displayed
4.	The topmost button is the information page button. Clicking the button will lead the user to the information page where it will play a video during the Independence Day in Malaysia and a brief description about “Merdeka” below the video.

 ![image](https://user-images.githubusercontent.com/93644714/165025601-47a30e3e-b11a-4a0b-b9e2-ca2b10a3f055.png)

5.	The middle button is the connect button, clicking it will lead the user to the Connection page. Upon entering the page, the user shall be requested to enable Bluetooth.

![image](https://user-images.githubusercontent.com/93644714/165025638-20f914bb-70bc-406c-9b25-a18e32f09b43.png)

6.	press the “Start Scanning” button to make the app start scanning for devices that have this app installed. During scanning the button will be disabled.

7.	After that the above the button will show how many devices have been found, for example the picture below shows it discovered 2 devices.

 ![image](https://user-images.githubusercontent.com/93644714/165025680-d824b04f-6cb1-4680-807b-61e9c5b2e9fb.png)


8.	As soon as it discovers the devices, a the merdeka sound snippet will play from each of the devices. For this to happen it will require more than 1 devices to be discovered.
9.	Right after that, the number of devices in the database will turn to 0 and user can restart the whole process

10.	The third button is for the settings page. On Settings page, the user shall be able to control the permissions that the application requires such as the Bluetooth, WIFI, and Location.
 ![image](https://user-images.githubusercontent.com/93644714/165025700-e976364d-37e1-4f73-83e4-2536b47220b0.png)


