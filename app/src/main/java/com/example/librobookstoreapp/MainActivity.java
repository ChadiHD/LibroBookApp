package com.example.librobookstoreapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.librobookstoreapp.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    View headerView;
    Toolbar toolbar;
    ActivityMainBinding binding;
    ActionBarDrawerToggle drawerToggle;
    FirebaseAuth fAuth;
    FirebaseUser fUser;
    String userId;
    TextView navUsername;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        headerView = navigationView.getHeaderView(0);
        toolbar = findViewById(R.id.toolbar);
        navUsername = headerView.findViewById(R.id.username_text);
        fAuth = FirebaseAuth.getInstance();
        fUser = fAuth.getCurrentUser();
        db = new DatabaseHelper(this);

        loadFragment(new BookListFragment());
        setSupportActionBar(toolbar);

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.OpenDrawer, R.string.CloseDrawer) {
            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                super.onDrawerOpened(drawerView);
                if (fUser != null && db.checkUsername(fUser.getEmail())) {
                    userId = fUser.getUid();
                    Menu menu = navigationView.getMenu();
                    menu.findItem(R.id.nav_login).setVisible(false);
                    menu.findItem(R.id.nav_logout).setVisible(true);
                    menu.findItem(R.id.book_manager).setVisible(true);
                    menu.findItem(R.id.book_list).setVisible(true);
                    navUsername.setText(fUser.getEmail());
                } else {
                    Menu menu = navigationView.getMenu();
                    menu.findItem(R.id.nav_login).setVisible(true);
                    menu.findItem(R.id.nav_logout).setVisible(false);
                    menu.findItem(R.id.book_manager).setVisible(false);
                    menu.findItem(R.id.book_list).setVisible(false);
                    navUsername.setText("Guest");
                }
            }
        };
        drawerLayout.addDrawerListener(drawerToggle);

        drawerToggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1){
            recreate();
        }
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem){
        switch (menuItem.getItemId()){
            case R.id.nav_login:
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                break;
            case R.id.book_list:
                loadFragment(new BookListFragment());
                break;
            case R.id.book_manager:
                loadFragment(new BookManagerFragment());
                break;
            case R.id.nav_logout:
                fAuth.signOut();
                recreate();
                break;
            default:
                recreate();
                break;
        }
        return false;
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        //This is necessary to change the icon of the Drawer Toggle upon state change.
        drawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
            drawerLayout.setVisibility(View.GONE);
        }else {
            super.onBackPressed();
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}