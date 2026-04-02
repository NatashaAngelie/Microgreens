package edu.uph.m23si1.microgreens;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.uph.m23si1.microgreens.data.AppFirebaseDatabase;
import edu.uph.m23si1.microgreens.data.MicrogreensSnapshot;

/** Profile: Firebase Auth + Realtime DB {@code users/{uid}}, same pattern as {@link PlantFormActivity}. */
public class ProfileActivity extends AppCompatActivity {

    public static final String PREFS_NAME = "microgreens_profile";
    public static final String KEY_NAME = "profile_name";
    public static final String KEY_EMAIL = "profile_email";

    private static final String PROFILE_PHOTO_FILE = "profile_photo.jpg";
    private static final int AVATAR_PADDING_DP = 28;

    private DatabaseReference userProfileRef;

    private EditText inputName;
    private EditText inputEmail;
    private ImageView imageProfilePhoto;
    private FrameLayout photoContainer;
    private MaterialButton btnSave;

    /** Dari node RTDB {@code photoUrl} atau Auth; dipakai saat simpan bila tidak ada foto di file lokal. */
    @Nullable
    private String cachedRemotePhotoUrl;

    private final ExecutorService imageLoadExecutor = Executors.newSingleThreadExecutor();

    private final ActivityResultLauncher<String> pickImage = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            this::onPickedImage);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseUser current = FirebaseAuth.getInstance().getCurrentUser();
        if (current == null) {
            Toast.makeText(this, R.string.profile_not_signed_in, Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        Uri authPhoto = current.getPhotoUrl();
        cachedRemotePhotoUrl = authPhoto != null ? authPhoto.toString() : null;

        userProfileRef = AppFirebaseDatabase.get()
                .getReference(MicrogreensSnapshot.REF_USERS)
                .child(current.getUid());

        setContentView(R.layout.activity_profile);

        View buttonBar = findViewById(R.id.profileButtonBar);
        ViewCompat.setOnApplyWindowInsetsListener(buttonBar, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            int extra = (int) (8 * getResources().getDisplayMetrics().density);
            v.setPadding(v.getPaddingLeft(), v.getPaddingTop(), v.getPaddingRight(), bars.bottom + extra);
            return insets;
        });

        MaterialToolbar toolbar = findViewById(R.id.toolbarProfile);
        toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        inputName = findViewById(R.id.inputProfileName);
        inputEmail = findViewById(R.id.inputProfileEmail);
        imageProfilePhoto = findViewById(R.id.imageProfilePhoto);
        photoContainer = findViewById(R.id.profilePhotoContainer);
        btnSave = findViewById(R.id.btnProfileSave);

        loadFieldsFromPrefs();
        applyPhotoToView();
        loadProfileFromDatabase();

        photoContainer.setOnClickListener(v -> pickImage.launch("image/*"));

        btnSave.setOnClickListener(v -> saveProfile());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        imageLoadExecutor.shutdown();
    }

    private SharedPreferences profilePrefs() {
        return getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    private void loadFieldsFromPrefs() {
        SharedPreferences p = profilePrefs();
        String name = p.getString(KEY_NAME, "");
        String email = p.getString(KEY_EMAIL, "");

        if (name.isEmpty() || email.isEmpty()) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                if (name.isEmpty()) {
                    String dn = user.getDisplayName();
                    if (dn != null && !dn.trim().isEmpty()) {
                        name = dn.trim();
                    }
                }
                if (email.isEmpty()) {
                    String ue = user.getEmail();
                    if (ue != null) {
                        email = ue;
                    }
                }
            }
        }

        inputName.setText(name);
        inputEmail.setText(email);
    }

    private void loadProfileFromDatabase() {
        userProfileRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    return;
                }
                String n = snapshot.child("displayName").getValue(String.class);
                String e = snapshot.child("email").getValue(String.class);
                String url = snapshot.child("photoUrl").getValue(String.class);
                if (n != null && !n.trim().isEmpty()) {
                    inputName.setText(n.trim());
                }
                if (e != null && !e.trim().isEmpty()) {
                    inputEmail.setText(e.trim());
                }
                if (url != null && !url.trim().isEmpty()) {
                    cachedRemotePhotoUrl = url.trim();
                }
                File local = new File(getFilesDir(), PROFILE_PHOTO_FILE);
                if ((!local.exists() || local.length() == 0) && url != null && !url.trim().isEmpty()) {
                    loadPhotoFromUrl(url.trim());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // tetap pakai prefs / lokal
            }
        });
    }

    private void loadPhotoFromUrl(String urlString) {
        imageLoadExecutor.execute(() -> {
            HttpURLConnection connection = null;
            InputStream in = null;
            try {
                URL url = new URL(urlString);
                connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(15_000);
                connection.setReadTimeout(15_000);
                connection.connect();
                in = connection.getInputStream();
                Bitmap bmp = BitmapFactory.decodeStream(in);
                if (bmp != null) {
                    runOnUiThread(() -> {
                        imageProfilePhoto.setPadding(0, 0, 0, 0);
                        imageProfilePhoto.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        imageProfilePhoto.setImageBitmap(bmp);
                    });
                }
            } catch (IOException ignored) {
                // biarkan placeholder
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException ignored) {
                    }
                }
                if (connection != null) {
                    connection.disconnect();
                }
            }
        });
    }

    private void applyPhotoToView() {
        File f = new File(getFilesDir(), PROFILE_PHOTO_FILE);
        int pad = (int) (AVATAR_PADDING_DP * getResources().getDisplayMetrics().density);
        if (f.exists() && f.length() > 0) {
            imageProfilePhoto.setPadding(0, 0, 0, 0);
            imageProfilePhoto.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageProfilePhoto.setImageBitmap(BitmapFactory.decodeFile(f.getAbsolutePath()));
        } else {
            imageProfilePhoto.setPadding(pad, pad, pad, pad);
            imageProfilePhoto.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            imageProfilePhoto.setImageResource(R.drawable.ic_camera_24);
        }
    }

    private void onPickedImage(@Nullable Uri uri) {
        if (uri == null) {
            return;
        }
        try {
            copyUriToProfilePhoto(uri);
            applyPhotoToView();
            Toast.makeText(this, R.string.profile_photo_updated, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, R.string.profile_photo_save_failed, Toast.LENGTH_SHORT).show();
        }
    }

    private void copyUriToProfilePhoto(Uri uri) throws IOException {
        try (InputStream in = getContentResolver().openInputStream(uri);
             OutputStream out = new FileOutputStream(new File(getFilesDir(), PROFILE_PHOTO_FILE))) {
            if (in == null) {
                throw new IOException("openInputStream null");
            }
            byte[] buf = new byte[8192];
            int r;
            while ((r = in.read(buf)) != -1) {
                out.write(buf, 0, r);
            }
        }
    }

    private void saveProfile() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, R.string.profile_not_signed_in, Toast.LENGTH_LONG).show();
            return;
        }

        String name = inputName.getText() != null ? inputName.getText().toString().trim() : "";
        String email = inputEmail.getText() != null ? inputEmail.getText().toString().trim() : "";

        btnSave.setEnabled(false);

        // Tanpa Firebase Storage: URL foto = yang sudah ada di Auth atau di RTDB (bukan upload file lokal).
        String photoUrl = existingPhotoUrlString(user);
        if (photoUrl == null || photoUrl.isEmpty()) {
            photoUrl = cachedRemotePhotoUrl;
        }

        runAuthEmailAndDatabase(user, name, email, photoUrl);
    }

    @Nullable
    private static String existingPhotoUrlString(FirebaseUser user) {
        Uri u = user.getPhotoUrl();
        return u != null ? u.toString() : null;
    }

    private void runAuthEmailAndDatabase(
            @NonNull FirebaseUser user,
            @NonNull String name,
            @NonNull String email,
            @Nullable String photoUrl) {
        UserProfileChangeRequest.Builder b = new UserProfileChangeRequest.Builder()
                .setDisplayName(name);
        if (photoUrl != null && !photoUrl.isEmpty()) {
            b.setPhotoUri(Uri.parse(photoUrl));
        }

        String currentEmail = user.getEmail() != null ? user.getEmail() : "";

        user.updateProfile(b.build()).addOnCompleteListener(profileDone -> {
            if (!profileDone.isSuccessful()) {
                btnSave.setEnabled(true);
                Toast.makeText(this, R.string.profile_auth_update_failed, Toast.LENGTH_LONG).show();
                return;
            }
            if (!email.equals(currentEmail)) {
                user.updateEmail(email)
                        .addOnCompleteListener(emailDone -> {
                            if (!emailDone.isSuccessful()) {
                                Toast.makeText(this, R.string.profile_email_update_failed, Toast.LENGTH_LONG).show();
                            }
                            persistLocalPrefs(name, email);
                            writeUserProfileToRtdb(name, email, photoUrl);
                        });
            } else {
                persistLocalPrefs(name, email);
                writeUserProfileToRtdb(name, email, photoUrl);
            }
        });
    }

    private void persistLocalPrefs(String name, String email) {
        profilePrefs().edit()
                .putString(KEY_NAME, name)
                .putString(KEY_EMAIL, email)
                .apply();
    }

    private void writeUserProfileToRtdb(
            @NonNull String name,
            @NonNull String email,
            @Nullable String photoUrl) {
        Map<String, Object> map = new HashMap<>();
        map.put("displayName", name);
        map.put("email", email);
        map.put("photoUrl", photoUrl != null ? photoUrl : "");

        userProfileRef.setValue(map)
                .addOnCompleteListener(task -> {
                    btnSave.setEnabled(true);
                    if (task.isSuccessful()) {
                        Toast.makeText(this, R.string.profile_saved, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, R.string.profile_database_save_failed, Toast.LENGTH_LONG).show();
                    }
                });
    }
}
