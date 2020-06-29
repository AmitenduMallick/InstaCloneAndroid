package com.example.instaclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import Fragments.HomeFragment;
import Fragments.NotificationFragment;
import Fragments.ProfileFragment;
import Fragments.ProfileViewFragment;
import Fragments.SearchFragment;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    Fragment selectFragment;
    FirebaseAuth auth=FirebaseAuth.getInstance();

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.logoutmenu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()){
            case R.id.logout:
                auth.signOut();
                startActivity(new Intent(MainActivity.this,StartActivity.class));
                finish();
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigationView=findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.nav_home:
                        selectFragment=new HomeFragment();
                        break;
                    case R.id.nav_search:
                        selectFragment=new SearchFragment();
                        break;
                    case R.id.nav_add:
                        selectFragment=null;
                        startActivity(new Intent(MainActivity.this,PostActivity.class));
                        break;
                    case R.id.nav_heart:
                        selectFragment=new NotificationFragment();
                        break;
                    case R.id.nav_profile:
                        selectFragment=new ProfileFragment();
                        break;
                }
                if(selectFragment!=null){
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectFragment).commit();
                }
                return true;
            }
        });
        Bundle intent=getIntent().getExtras();
        if(intent!=null){
            String profileId=intent.getString("publisherID");
            getSharedPreferences("Profile",MODE_PRIVATE).edit().putString("profileID",profileId).apply();
            //getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ProfileViewFragment()).commit();
            startActivity(new Intent(getApplicationContext(),ProfileViewingActivity.class));
            finish();
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
        }else {

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();

        }



    }
}