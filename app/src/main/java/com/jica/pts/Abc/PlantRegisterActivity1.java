package com.jica.pts.Abc;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.jica.pts.R;

public class PlantRegisterActivity1 extends AppCompatActivity {
   ImageView imgsearch;
   Button btnpltnm1, btnpltnm2, btnpltnm3, btnpltnm4, btnpltnm5, btnpltnm6, btnpltnm7, btnpltnm8,
           btnpltnm9, btnpltnm10, btnpltnm11, btnpltnm12;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_register1);

        //UI 찾기
        btnpltnm1=findViewById(R.id.btnpltnm1);
        btnpltnm2=findViewById(R.id.btnpltnm2);
        btnpltnm3=findViewById(R.id.btnpltnm3);
        btnpltnm4=findViewById(R.id.btnpltnm4);
        btnpltnm5=findViewById(R.id.btnpltnm5);
        btnpltnm6=findViewById(R.id.btnpltnm6);
        btnpltnm7=findViewById(R.id.btnpltnm7);
        btnpltnm8=findViewById(R.id.btnpltnm8);
        btnpltnm9=findViewById(R.id.btnpltnm9);
        btnpltnm10=findViewById(R.id.btnpltnm10);
        btnpltnm11=findViewById(R.id.btnpltnm11);
        btnpltnm12=findViewById(R.id.btnpltnm12);

        imgsearch= findViewById(R.id.imgsearch);


        //이벤트 핸들러 설정
        ButtonHandler buttonHandler = new ButtonHandler();
        btnpltnm1.setOnClickListener(buttonHandler);
        btnpltnm2.setOnClickListener(buttonHandler);
        btnpltnm3.setOnClickListener(buttonHandler);
        btnpltnm4.setOnClickListener(buttonHandler);
        btnpltnm5.setOnClickListener(buttonHandler);
        btnpltnm6.setOnClickListener(buttonHandler);
        btnpltnm7.setOnClickListener(buttonHandler);
        btnpltnm8.setOnClickListener(buttonHandler);
        btnpltnm9.setOnClickListener(buttonHandler);
        btnpltnm10.setOnClickListener(buttonHandler);
        btnpltnm11.setOnClickListener(buttonHandler);
        btnpltnm12.setOnClickListener(buttonHandler);


    }

    class ButtonHandler implements View.OnClickListener{
    //LayoutExample 참조하기
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getApplicationContext(), PlantRegisterActivity2.class);
            startActivity(intent);
        }
    }
}

