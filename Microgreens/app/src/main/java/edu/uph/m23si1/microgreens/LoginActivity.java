package edu.uph.m23si1.microgreens;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    EditText emailInput, passwordInput;
    Button loginBtn;
    TextView goRegister;
    CheckBox rememberMe;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginBtn = findViewById(R.id.loginBtn);
        goRegister = findViewById(R.id.goRegister);
        rememberMe = findViewById(R.id.rememberMe);

        auth = FirebaseAuth.getInstance();

        // Auto login
        SharedPreferences pref = getSharedPreferences("user_pref", MODE_PRIVATE);
        boolean remember = pref.getBoolean("remember", false);

        if (auth.getCurrentUser() != null && remember) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

        loginBtn.setOnClickListener(v -> loginUser());

        setupClickableText();
    }

    private void setupClickableText() {
        String text = goRegister.getText().toString();
        String target = "Register now";

        SpannableString ss = new SpannableString(text);
        int start = text.indexOf(target);

        if (start >= 0) {
            int end = start + target.length();

            ss.setSpan(new ClickableSpan() {
                @Override
                public void onClick(@NonNull View view) {
                    startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                }

                @Override
                public void updateDrawState(@NonNull TextPaint ds) {
                    ds.setColor(Color.parseColor("#5C6BC0"));
                    ds.setUnderlineText(false);
                }
            }, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        goRegister.setText(ss);
        goRegister.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void loginUser() {

        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Isi semua field", Toast.LENGTH_SHORT).show();
            return;
        }

        // 🔥 INI YANG CEK KE FIREBASE AUTH
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        getSharedPreferences("user_pref", MODE_PRIVATE)
                                .edit()
                                .putBoolean("remember", rememberMe.isChecked())
                                .apply();

                        startActivity(new Intent(this, MainActivity.class));
                        finish();

                    } else {
                        Toast.makeText(this, "Akun tidak ditemukan / salah password", Toast.LENGTH_SHORT).show();
                    }

                });
    }
}