package com.example.moneytracker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class AccountActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        Bundle extras =getIntent().getExtras();
        String getName = extras.getString("EXTRA_TEXT");
        TextView email = findViewById(R.id.email);
        email.setText(getName) ;
    }
}