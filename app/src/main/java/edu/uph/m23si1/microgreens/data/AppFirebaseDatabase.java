package edu.uph.m23si1.microgreens.data;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Realtime Database di region Singapore.
 * Path data daftar tanaman: {@code plants} (bukan URL ini + "/plants" di konstruktor —
 * path ditambah lewat {@code getReference("plants")}).
 */
public final class AppFirebaseDatabase {

    /**
     * Basis URL sama persis seperti di Firebase Console (tanpa path, tanpa slash di akhir).
     * Contoh buka di browser: …/plants meminta login Google — itu normal; app pakai SDK + rules.
     */
    public static final String DATABASE_URL =
            "https://microgreens-682b8-default-rtdb.asia-southeast1.firebasedatabase.app";

    private static volatile FirebaseDatabase instance;

    private AppFirebaseDatabase() {}

    public static FirebaseDatabase get() {
        if (instance == null) {
            synchronized (AppFirebaseDatabase.class) {
                if (instance == null) {
                    FirebaseApp app = FirebaseApp.getInstance();
                    instance = FirebaseDatabase.getInstance(app, DATABASE_URL);
                }
            }
        }
        return instance;
    }
}
