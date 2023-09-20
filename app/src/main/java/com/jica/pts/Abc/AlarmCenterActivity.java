package com.jica.pts.Abc;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.jica.pts.MainFragment.BottomTabActivity;
import com.jica.pts.R;

public class AlarmCenterActivity extends AppCompatActivity {
    ImageView imgclose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_center);

        //UI 객체 찾기
        imgclose= findViewById(R.id.imgclose);

        imgclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), BottomTabActivity.class);
                startActivity(intent);
            }
        });

    }
}