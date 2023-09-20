package com.jica.pts.Login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.jica.pts.MainFragment.BottomTabActivity;
import com.jica.pts.R;

public class UserRegisterCheckActivity extends AppCompatActivity {
    private final long finishtimeed = 1000;
    private long presstime = 0;


    Button btnstart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register_check);

        btnstart= findViewById(R.id.btnstart);

        btnstart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), BottomTabActivity.class);
                startActivity(intent);
            }
        });
    }


    //뒤로가기 2번 클릭시 앱 종료
    public void onBackPressed() {
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - presstime;

        if (0 <= intervalTime && finishtimeed >= intervalTime) {
            this.finish();
        } else {
            presstime = tempTime;
            Toast.makeText(getApplicationContext(), "한번더 누르시면 앱이 종료됩니다", Toast.LENGTH_SHORT).show();
        }
    };
}