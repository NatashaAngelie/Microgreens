package edu.uph.m23si1.microgreens;

import android.app.Application;

import com.google.firebase.FirebaseApp;

/** Memastikan Firebase diinisialisasi sebelum akses Realtime Database. */
public class MicrogreensApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
    }
}
