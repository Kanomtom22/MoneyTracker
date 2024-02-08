package com.example.moneytracker;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import android.annotation.SuppressLint;
import android.view.View;

import com.example.moneytracker.databinding.ActivityMainBinding;

@SuppressLint("NonConstantResourceId")
public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Open LoginActivity when the app starts
        replaceFragment(new HomeFragment());
        binding.bottomNavigationView.setBackground(null);

        /*Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();*/

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    replaceFragment(new HomeFragment());
                    break;

                case R.id.navigation_dashboard:
                    replaceFragment(new DashboardFragment());
                    break;

                case R.id.navigation_history:
                    replaceFragment(new HistoryFragment());
                    break;

                case R.id.navigation_setting:
                    replaceFragment(new SettingFragment());
                    break;
            }


            return true;

        });
    }

    protected void onResume() {
        super.onResume();
        updateNavigationBarLanguage();
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    private void updateNavigationBarLanguage() {
        String titleHome = getString(R.string.title_home);
        String titleDashboard = getString(R.string.title_dashboard);
        String titleHistory = getString(R.string.title_history);
        String titleSetting = getString(R.string.title_setting);

        binding.bottomNavigationView.getMenu().findItem(R.id.navigation_home).setTitle(titleHome);
        binding.bottomNavigationView.getMenu().findItem(R.id.navigation_dashboard).setTitle(titleDashboard);
        binding.bottomNavigationView.getMenu().findItem(R.id.navigation_history).setTitle(titleHistory);
        binding.bottomNavigationView.getMenu().findItem(R.id.navigation_setting).setTitle(titleSetting);
    }


    public void addNew(View view) {
        replaceFragment(new AddFragment());
    }

}