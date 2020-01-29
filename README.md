# Contains the following applications
- Kotlin app using retrofit lib for REST services

- Kavach application for public service which includes google voice and location and sms services.

  You need to set up a AWS service for this. Once done you can login with your credentials. A service will be running in the     background which is integrated to the google voice, you need to provide voice input by calling out "help me". The mobile       number using which the user registers to this application will receive an sms with the google map link to track the user of   the app.
  
- SoldesApp for finding sales which uses firebase to get the sales update through push notification in shopping malls.

  You have to set up a Firebase account for this and register the application with it. using the keys and json file generated   in Firebase, you can send push notification to the mobile having this mobile app.
  The server code is available in the below link.
  AndroidApps/soldesserver/
  
- LaundryApp to manage laundry for customers

- ShieldMobApp showing user interface using constraint layout.
  The server side code is maintained by the clients so you can only view the user interfaces i.e., xml files in layout to see   the designs implemented using constraints layout.
