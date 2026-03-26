package edu.uph.m23si1.microgreens;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    EditText emailInput, passwordInput;
    Button registerBtn;
    TextView goLogin;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        registerBtn = findViewById(R.id.registerBtn);
        goLogin = findViewById(R.id.goLogin);

        auth = FirebaseAuth.getInstance();

        registerBtn.setOnClickListener(v -> registerUser());

        setupClickableText();
    }

    private void setupClickableText() {
        String text = goLogin.getText().toString();
        String target = "Login";

        SpannableString ss = new SpannableString(text);
        int start = text.indexOf(target);

        if (start >= 0) {
            int end = start + target.length();

            ss.setSpan(new ClickableSpan() {
                @Override
                public void onClick(@NonNull View widget) {
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                }
            }, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        goLogin.setText(ss);
        goLogin.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void registerUser() {

        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Isi semua field", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Register berhasil", Toast.LENGTH_SHORT).show();

                        startActivity(new Intent(this, LoginActivity.class));
                        finish();

                    } else {
                        Toast.makeText(this, "Register gagal", Toast.LENGTH_SHORT).show();
                    }

                });
    }
}