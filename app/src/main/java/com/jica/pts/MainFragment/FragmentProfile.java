package com.jica.pts.MainFragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.jica.pts.Login.UserTotalLoginActivity;
import com.jica.pts.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class FragmentProfile extends Fragment {

    //UI 객체 선언
    ListView lvProfile;
    TextView tvProfileID, tvBoardWrite;

    Button btnProfileLogin;

    ArrayList<String> arrayList;
    ArrayAdapter adapter;

    //DB 객체 선언
    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;
    FirebaseUser CurrentUser;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main_profile, container, false);


        //UI 찾기
        tvProfileID= view.findViewById(R.id.tvProfileID);
        tvBoardWrite= view.findViewById(R.id.tvBoardWrite);
        lvProfile = view.findViewById(R.id.lvProfile);
        btnProfileLogin = view.findViewById(R.id.btnProfileLogin);



        //DB 연결
        db= FirebaseFirestore.getInstance();
        firebaseAuth= FirebaseAuth.getInstance();
        CurrentUser= firebaseAuth.getCurrentUser();



        arrayList = new ArrayList<String>();
        arrayList.add("알림 센터");
        arrayList.add("전문가 뱃지 등록");
        arrayList.add("공지 사항");
        arrayList.add("고객 센터");

        //어탭터 생성
        adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, arrayList);
        //ListView에 어탭터 연결
        lvProfile.setAdapter(adapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //로그인 여부 확인 
        if(CurrentUser != null){
            btnProfileLogin.setText("로그아웃");
            tvProfileID.setText(CurrentUser.getEmail());

            //Board 컬랙션의 board_del의 필드값이 false인 문서 개수 찾기
            db.collection("Board").whereEqualTo("board_del", false).get()
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            QuerySnapshot querySnapshot= task.getResult();
                            tvBoardWrite.setText(String.valueOf(querySnapshot.size()));
                        }

                    });

            //로그아웃 버튼 이벤트 핸들러
            //버튼 하나를 이용하여 현재 로그인 여부를 파악 후 서로 다른 이벤트핸들러 실행
            btnProfileLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    firebaseAuth.signOut();


                   //프래그먼트 이동은 되지만 바텀네비게이션바 이동하는방법을 구현 못함
                   /* FragmentCommunity fragmentCommunity = new FragmentCommunity();
                    getFragmentManager().beginTransaction().replace(R.id.container1, fragmentCommunity).commit();*/


                    Intent intent = new Intent(getContext(), BottomTabActivity.class);
                    intent.putExtra("move", "Profile");
                    startActivity(intent);

                }
            });


        }else {
            //로그인 버튼 이벤트 핸들러(버튼 하나를 이용하여 현재 로그인 여부를 파악 후 서로 다른 이벤트핸들러 실행)
            btnProfileLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(), UserTotalLoginActivity.class);
                    startActivity(intent);
                }
            });

        }



       //리스트뷰 항목 클릭시 이벤트 핸들러
        lvProfile.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            //View view: 클릭된 항목(View)
            //int position: 클릭된 항목 위치(position)
            //long id: 클릭된 항목의 고유한 ID => 잘 사용하지 않는다.
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });






    }
}