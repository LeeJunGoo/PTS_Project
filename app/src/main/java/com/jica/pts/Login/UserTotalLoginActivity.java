package com.jica.pts.Login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.jica.pts.MainFragment.BottomTabActivity;
import com.jica.pts.R;

public class UserTotalLoginActivity extends AppCompatActivity {
    private final long finishtimeed = 1000;
    private long presstime = 0;



    Button btntotalSkip;
    TextView tvLogin, tvRegister;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_total_login);

        tvLogin = findViewById(R.id.tvLogin);
        tvRegister = findViewById(R.id.tvRegister);
        btntotalSkip = findViewById(R.id.btntotalSkip);



        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), UserLoginActivity.class);
                startActivity(intent);

            }
        });

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), UserRegisterActivity.class);
                startActivity(intent);
            }
        });

        btntotalSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), BottomTabActivity.class);
                startActivity(intent);
            }
        });


    }



    //뒤로가기 2번 클릭시 앱 종료 기능
    @Override
    public void onBackPressed() {

        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - presstime;


        if (0 <= intervalTime && finishtimeed >= intervalTime) {
            finishAffinity();          // 해당 어플리케이션의 루트 액티비티를 종료시키는 것 입니다.
            System.runFinalization();  // 현재 구동중인 쓰레드가 다 종료되면 종료시키는 것 입니다.
            System.exit(0);      // 현재의 액티비티를 종료시키는 것 입니다.
        } else {
            presstime = tempTime;
            Toast.makeText(getApplicationContext(), "한번더 누르시면 앱이 종료됩니다", Toast.LENGTH_SHORT).show();

        }
    }
}