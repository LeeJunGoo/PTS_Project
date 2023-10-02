package com.jica.pts.Login;
//import android.support.annotation.NonNull;
import androidx.annotation.NonNull;
//import android.support.v7.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.jica.pts.R;



public class UserRegisterActivity extends AppCompatActivity {

    //FirebasAuth 객체 선언
    private FirebaseAuth firebaseAuth;
    //UI 객체 선언
    private EditText editTextEmail;
    private EditText etpassWord;
    private EditText etpassWordck;
    private Button buttonJoin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register);

        // FirebaseAuth 객체 초기화(데이터베이스 연결)
        firebaseAuth = FirebaseAuth.getInstance();

        //UI 객체 찾기
        editTextEmail = findViewById(R.id.etemail);
        etpassWord = findViewById(R.id.etpassWord);
        etpassWordck = findViewById(R.id.etpassWordck);
        buttonJoin =  findViewById(R.id.btn_join);
        
        
        //유효성 검사 이벤트 핸들러 설정
        buttonJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 이메일과 비밀번호가 공백이 아닌 경우
                if (!editTextEmail.getText().toString().equals("") && !etpassWord.getText().toString().equals("") &&  !etpassWordck.getText().toString().equals("")) {
                    createUser(editTextEmail.getText().toString(), etpassWord.getText().toString(), etpassWordck.getText().toString());
                } else {
                    // 이메일과 비밀번호가 공백인 경우
                    Toast.makeText(UserRegisterActivity.this, "계정과 비밀번호를 입력하세요.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void createUser(String email, String password, String name) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // 회원가입 성공시
                            Toast.makeText(UserRegisterActivity.this, "회원가입 성공", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), UserRegisterCheckActivity.class);

                            startActivity(intent);

                        } else {
                            // 회원가입 실패시(데이터 유효성 검사: 아이디 중복 및 비밀번호 일치 여부 및 비밀번호 6글자 이상)
                            Toast.makeText(UserRegisterActivity.this, "회원가입 실패", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}