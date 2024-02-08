package com.example.moneytracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.Locale;

public class LanguagesActivity extends AppCompatActivity {
    private Button btnEnglish;
    private Button btnThai;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_languages);

        btnEnglish = findViewById(R.id.btn_english);
        btnThai = findViewById(R.id.btn_thai);

        btnEnglish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLanguage("en");
                restartActivity("en");
            }
        });

        btnThai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLanguage("th");
                restartActivity("th");
            }
        });
    }

    public void setLanguage(String languageCode) {
        Resources resources = this.getResources();
        Configuration configuration = resources.getConfiguration();
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        configuration.setLocale(locale);
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
    }

    private void restartActivity(String languageCode) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("language_code", languageCode);
        startActivity(intent);
        finish();
    }

    protected void onResume() {
        super.onResume();
//        updateNavigationBarLanguage();
    }
}