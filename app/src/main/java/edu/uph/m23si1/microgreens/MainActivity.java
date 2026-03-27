package edu.uph.m23si1.microgreens;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import edu.uph.m23si1.microgreens.ui.control.ControlFragment;
import edu.uph.m23si1.microgreens.ui.history.HistoryFragment;
import edu.uph.m23si1.microgreens.ui.home.HomeFragment;

public class MainActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    BottomNavigationView bottomNav;
    NavigationView navDrawer;
    MaterialToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ===== INIT VIEW =====
        drawerLayout = findViewById(R.id.drawerLayout);
        bottomNav = findViewById(R.id.bottomNav);
        navDrawer = findViewById(R.id.navDrawer);
        toolbar = findViewById(R.id.toolbar);

        // ===== TOOLBAR =====
        setSupportActionBar(toolbar);

        // ===== DRAWER TOGGLE (HAMBURGER ICON) =====
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.open,
                R.string.close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // ===== DEFAULT FRAGMENT =====
        loadFragment(new HomeFragment());

        // ===== BOTTOM NAVIGATION =====
        bottomNav.setOnItemSelectedListener(item -> {

            if (item.getItemId() == R.id.navigation_home) {
                loadFragment(new HomeFragment());
            }
            else if (item.getItemId() == R.id.navigation_control) {
                loadFragment(new ControlFragment());
            }
            else if (item.getItemId() == R.id.navigation_history) {
                loadFragment(new HistoryFragment());
            }

            return true;
        });

        // ===== DRAWER MENU (PROFILE & PLANTS) =====
        navDrawer.setNavigationItemSelectedListener(item -> {

//            if (item.getItemId() == R.id.menu_profile) {
//                loadFragment(new ProfileFragment());
//            }
//            else if (item.getItemId() == R.id.menu_plants) {
//                loadFragment(new PlantsFragment());
//            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        // ===== LOGOUT BUTTON (DARI HEADER) =====
        View headerView = navDrawer.getHeaderView(0);
        Button btnLogout = headerView.findViewById(R.id.btnLogout);

        btnLogout.setOnClickListener(v -> {

            // Logout Firebase
            FirebaseAuth.getInstance().signOut();

            // Hapus session (remember me)
            getSharedPreferences("user_pref", MODE_PRIVATE)
                    .edit()
                    .clear()
                    .apply();

            // Pindah ke login
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        });
    }

    // ===== FUNCTION PINDAH FRAGMENT =====
    void loadFragment(Fragment fragment){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }
}