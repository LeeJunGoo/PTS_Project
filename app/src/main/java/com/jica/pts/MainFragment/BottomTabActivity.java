package com.jica.pts.MainFragment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.jica.pts.R;

public class BottomTabActivity extends AppCompatActivity {
    private final long finishtimeed = 1000;
    private long presstime = 0;


    FragmentPlantManagement fragmentPlantManagement;
    FragmentCalendar fragmentCalendar;
    FragmentCommunity fragmentCommunity;
    FragmentProfile fragmentProfile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);

        fragmentPlantManagement = new FragmentPlantManagement();
        fragmentCalendar = new FragmentCalendar();
        fragmentCommunity = new FragmentCommunity();
        fragmentProfile = new FragmentProfile();

// 송신 액티비티에서 전달된 intent를 가져옵니다.
        Intent intent = getIntent();
// getStringExtra 메서드를 사용하여 정보를 추출합니다.
        String receivedData = intent.getStringExtra("move");

        if (receivedData != null) {

            if (receivedData.equals("DetailPage")) {
                getSupportFragmentManager().beginTransaction().replace(R.id.container1, fragmentCommunity).commit();
                BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
                bottomNavigationView.setSelectedItemId(R.id.community);

            }else if(receivedData.equals("Profile")){
                    getSupportFragmentManager().beginTransaction().replace(R.id.container1, fragmentCommunity).commit();
                    BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
                    bottomNavigationView.setSelectedItemId(R.id.community);
            }
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.container1, fragmentCommunity).commit();
            BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
            bottomNavigationView.setSelectedItemId(R.id.community);
        }

        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {


            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int curId = item.getItemId();
                if (curId == R.id.home) {
                    //replace(프래그먼트를 띄워줄 frameLayout, 교체할 fragment 객체)
                    getSupportFragmentManager().beginTransaction().replace(R.id.container1, fragmentPlantManagement).commit();
                    return true;

                } else if (curId == R.id.calendar) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.container1, fragmentCalendar).commit();

                    return true;

                } else if (curId == R.id.community) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.container1, fragmentCommunity).commit();
                    return true;

                } else if (curId == R.id.profile) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.container1, fragmentProfile).commit();

                    return true;


                }

                return false;
            }
        });

    }

    //뒤로가기 2번 클릭시 앱 종료 기능
    @Override
    public void onBackPressed() {

        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - presstime;


        if (0 <= intervalTime && finishtimeed >= intervalTime) {
            finishAffinity();         // 해당 어플리케이션의 루트 액티비티를 종료시키는 것 입니다.
            System.runFinalization(); // 현재 구동중인 쓰레드가 다 종료되면 종료시키는 것 입니다.
            System.exit(0);     // 현재의 액티비티를 종료시키는 것 입니다.
        } else {
            presstime = tempTime;
            Toast.makeText(getApplicationContext(), "한번더 누르시면 앱이 종료됩니다", Toast.LENGTH_SHORT).show();

        }
    }
}