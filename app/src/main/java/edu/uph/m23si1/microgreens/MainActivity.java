package edu.uph.m23si1.microgreens;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import edu.uph.m23si1.microgreens.ui.control.ControlFragment;
import edu.uph.m23si1.microgreens.ui.history.HistoryFragment;
import edu.uph.m23si1.microgreens.ui.history.HistoryLogFragment;
import edu.uph.m23si1.microgreens.ui.home.HomeFragment;
import edu.uph.m23si1.microgreens.ui.plants.ManagePlantsFragment;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_OPEN_MANAGE_PLANTS = "open_manage_plants";

    DrawerLayout drawerLayout;
    BottomNavigationView bottomNav;
    NavigationView navDrawer;
    MaterialToolbar toolbar;
    private ActionBarDrawerToggle drawerToggle;

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
        drawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.open,
                R.string.close
        );
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        toolbar.setNavigationOnClickListener(v -> {
            FragmentManager fm = getSupportFragmentManager();
            if (fm.getBackStackEntryCount() > 0) {
                fm.popBackStack();
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        getSupportFragmentManager().addOnBackStackChangedListener(this::updateToolbarForTopFragment);

        // ===== BOTTOM NAVIGATION =====
        bottomNav.setOnItemSelectedListener(item -> {

            if (item.getItemId() == R.id.navigation_home) {
                setToolbarTitle(getString(R.string.toolbar_home));
                loadFragment(new HomeFragment());
            }
            else if (item.getItemId() == R.id.navigation_control) {
                setToolbarTitle(getString(R.string.title_control));
                loadFragment(new ControlFragment());
            }
            else if (item.getItemId() == R.id.navigation_history) {
                setToolbarTitle(getString(R.string.toolbar_history));
                loadFragment(new HistoryFragment());
            }

            return true;
        });

        // ===== DRAWER MENU (PROFILE & PLANTS) =====
        // ===== DRAWER MENU =====
        NavigationView navMenu = findViewById(R.id.navMenu);

        navMenu.setNavigationItemSelectedListener(item -> {

            int id = item.getItemId();

            if (id == R.id.menu_profile) {
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
            }
            else if (id == R.id.menu_plants) {
                setToolbarTitle(getString(R.string.toolbar_manage_plants));
                loadFragment(new ManagePlantsFragment());
            }
            else if (id == R.id.menu_plant_specs) {
                startActivity(new Intent(MainActivity.this, PlantSpecsActivity.class));
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });


        // ===== LOGOUT BUTTON =====
        Button btnLogout = findViewById(R.id.btnLogout);

        btnLogout.setOnClickListener(v -> {

            FirebaseAuth.getInstance().signOut();

            getSharedPreferences("user_pref", MODE_PRIVATE)
                    .edit()
                    .clear()
                    .apply();

            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        });

        if (consumeOpenManagePlantsFromIntent(getIntent())) {
            // Sudah buka Manage Plants dari intent (mis. balik dari PlantFormActivity)
        } else if (savedInstanceState == null) {
            setToolbarTitle(getString(R.string.toolbar_home));
            loadFragment(new HomeFragment());
        }
        updateToolbarForTopFragment();
    }

    private void updateToolbarForTopFragment() {
        if (toolbar == null || drawerToggle == null) {
            return;
        }
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
        if (f instanceof HistoryLogFragment) {
            drawerToggle.setDrawerIndicatorEnabled(false);
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
            setToolbarTitle(getString(R.string.history_log_title));
        } else if (f instanceof ManagePlantsFragment) {
            drawerToggle.setDrawerIndicatorEnabled(true);
            drawerToggle.syncState();
            setToolbarTitle(getString(R.string.toolbar_manage_plants));
        } else {
            drawerToggle.setDrawerIndicatorEnabled(true);
            drawerToggle.syncState();
            applyToolbarTitleForSelectedNav();
        }
    }

    private void applyToolbarTitleForSelectedNav() {
        int id = bottomNav.getSelectedItemId();
        if (id == R.id.navigation_home) {
            setToolbarTitle(getString(R.string.toolbar_home));
        } else if (id == R.id.navigation_control) {
            setToolbarTitle(getString(R.string.title_control));
        } else if (id == R.id.navigation_history) {
            setToolbarTitle(getString(R.string.toolbar_history));
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        consumeOpenManagePlantsFromIntent(intent);
    }

    /** @return true jika layar Manage Plants sudah dimuat dari intent */
    private boolean consumeOpenManagePlantsFromIntent(Intent intent) {
        if (intent == null) {
            return false;
        }
        if (!intent.getBooleanExtra(EXTRA_OPEN_MANAGE_PLANTS, false)) {
            return false;
        }
        intent.removeExtra(EXTRA_OPEN_MANAGE_PLANTS);
        setToolbarTitle(getString(R.string.toolbar_manage_plants));
        loadFragment(new ManagePlantsFragment());
        return true;
    }

    // ===== FUNCTION PINDAH FRAGMENT =====
    void loadFragment(Fragment fragment) {
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
        updateToolbarForTopFragment();
    }

    public void setToolbarTitle(CharSequence title) {
        if (toolbar != null) {
            toolbar.setTitle(title);
        }
    }

    /** Sembunyikan toolbar utama (mis. saat Manage Plants punya toolbar sendiri). */
    public void setMainToolbarVisible(boolean visible) {
        if (toolbar != null) {
            toolbar.setVisibility(visible ? View.VISIBLE : View.GONE);
        }
    }

    /** Tombol back dari layar Manage Plants → Home. */
    public void navigateToHomeFromManagePlants() {
        setMainToolbarVisible(true);
        setToolbarTitle(getString(R.string.toolbar_home));
        loadFragment(new HomeFragment());
        bottomNav.setSelectedItemId(R.id.navigation_home);
    }
}