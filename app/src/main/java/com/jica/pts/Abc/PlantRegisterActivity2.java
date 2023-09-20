package com.jica.pts.Abc;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.jica.pts.MainFragment.BottomTabActivity;
import com.jica.pts.R;

public class PlantRegisterActivity2 extends AppCompatActivity {

    Button btnpltrg2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_register2);


        //UI 객체 찾기
        btnpltrg2= findViewById(R.id.btnpltrg2);


        //이벤트 핸들러
        btnpltrg2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(getApplicationContext(), BottomTabActivity.class);
                startActivity(intent);

            }
        });


    }
}