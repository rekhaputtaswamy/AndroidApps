package com.epita.soldesserver.main;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

public class Application {

	public static void main(String[] args) throws IOException {
		FileInputStream serviceAccount = new FileInputStream("/google-services.json");

		FirebaseOptions options = new FirebaseOptions.Builder()
		    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
		    .setDatabaseUrl("https://soldesapp-bb0df.firebaseio.com/")
		    .build();

		FirebaseApp.initializeApp(options);

	}

}
