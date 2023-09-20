package com.jica.pts.Community_Fragmenet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.jica.pts.Community_Fragmenet.CommunityBoardWriteActivity;
import com.jica.pts.R;

public class CommunityProfileActivity extends AppCompatActivity {

    //UI 객체 선언
    Button btnpost;
   TextView tvBoardCount;
   TextView tvBoardID;

   FirebaseFirestore db;
   FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_profile);


        //UI 객체 찾기
        btnpost= findViewById(R.id.btnpost);
        tvBoardCount = findViewById(R.id.tvBoardCount);
        tvBoardID = findViewById(R.id.tvBoardID);

        db = FirebaseFirestore.getInstance();
        firebaseAuth= FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();



        //아이디 불러오기
        tvBoardID.setText(user.getEmail());


        //게시글 수 불러오기
        db.collection("Board").whereEqualTo("user_id", user.getEmail()).get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        QuerySnapshot querySnapshot = task.getResult();
                        String Board_cnt = String.valueOf(querySnapshot.size());
                        tvBoardCount.setText(Board_cnt);

                    }

                });

        //게시글 작성 이벤트 핸들러
        btnpost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CommunityBoardWriteActivity.class);
                startActivity(intent);
                finish();
            }
        });




    }
}