package com.jica.pts.Login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.jica.pts.MainFragment.BottomTabActivity;
import com.jica.pts.R;

public class MainActivity extends AppCompatActivity {
    Button btn1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_loding);

        btn1= findViewById(R.id.button1);


        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //로그인(true), 비로그인(false) 정보
                boolean isLogin = checkLoginStatus();

                //로그인 됐을경우 로그아웃 버튼 보이기
                if(isLogin){
                     Intent intent = new Intent(getApplicationContext(), BottomTabActivity.class);
                     startActivity(intent);

                }else {
                    Intent intent = new Intent(getApplicationContext(), UserTotalLoginActivity.class);
                    startActivity(intent);

                }

            }
        });


    }

    //로그인 여부 확인하기
    private boolean checkLoginStatus() {
        //Firebase 연결
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        //현재 로그인 정보
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            return true;
        } else {
            return false;
        }
    }
}