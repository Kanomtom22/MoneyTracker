package com.example.moneytracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.moneytracker.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    public static final String EXTRA_TEXT = "";
    ActivityLoginBinding binding;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();

        binding.goToSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                try {
                    startActivity(intent);
                } catch (Exception e) {

                }
            }
        });

        binding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = binding.emaillogin.getText().toString().trim();
                String password = binding.passwordLogin.getText().toString().trim();

                if (email.length()<=0 || password.length()<=0) {
                    return;
                }
                firebaseAuth.signInWithEmailAndPassword(email, password)
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                Toast.makeText(LoginActivity.this, "Sign in success", Toast.LENGTH_SHORT).show();
                                //if login success go to home screen

                                // Now, create an Intent for the AccountActivity
                                Intent accountIntent = new Intent(LoginActivity.this, AccountActivity.class);

                                // Pass the email as an extra to the AccountActivity
                                Bundle extras = new Bundle();
                                extras.putString("EXTRA_TEXT", email);
                                accountIntent.putExtras(extras);

                                // Start the AccountActivity
                                startActivity(accountIntent);
                                Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);

                                // Start the MainActivity
                                startActivity(mainIntent);


                                // Finish the LoginActivity if needed
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }
}