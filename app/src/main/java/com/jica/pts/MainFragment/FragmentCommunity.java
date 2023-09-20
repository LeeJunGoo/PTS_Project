package com.jica.pts.MainFragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.jica.pts.Community_Fragmenet.CommunityProfileActivity;
import com.jica.pts.Community_Fragmenet.FragmenetQuestionCommunity;
import com.jica.pts.Community_Fragmenet.FragmentMainCommunity;
import com.jica.pts.Community_Fragmenet.FragmentPlaygroundCommunity;
import com.jica.pts.Community_Fragmenet.FragmentProudCommunity;
import com.jica.pts.Login.UserTotalLoginActivity;
import com.jica.pts.R;

//ViewPager2를 사용하여 탭으로 스와이프 뷰 만들기 추후에 하기

public class FragmentCommunity extends Fragment {

    ImageView imgCmProfile;


    FragmentMainCommunity fragmentMainCommunity;
    FragmentProudCommunity fragmentProudCommunity;
    FragmenetQuestionCommunity fragmenetQuestionCommunity;
    FragmentPlaygroundCommunity fragmentPlaygroundCommunity;

    FirebaseAuth firebaseAuth;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.fragment_main_community, container, false);

        imgCmProfile= fragmentView.findViewById(R.id.imgCmProfile);

        firebaseAuth = FirebaseAuth.getInstance();


        imgCmProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                // 현재 로그인된 사용자의 정보를 반환
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null){
                    Intent intent = new Intent(getActivity(), CommunityProfileActivity.class);
                    startActivity(intent);

                }else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("대화상자 제목");
                    builder.setIcon(android.R.drawable.ic_dialog_alert); //안드로이드에서 제공하는 아이콘 이미지 사용
                    builder.setMessage("로그인 후 사용가능합니다.");

                    //긍정적 성격의 버튼 이벤트 핸들러              ButtonPositive(-1)
                    builder.setPositiveButton("로그인 하기", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(getActivity(), UserTotalLoginActivity.class);
                            startActivity(intent);
                        }
                    });
                    //부정적 성격의 버튼                ButtonNegative(-2)
                    builder.setNegativeButton("취소", null);
                    // true일 경우  : 대화상자 버튼이 아닌 배경 및 back 버튼 눌렀을때도 종료하도록 하는 기능
                    // false일 경우 : 대화상자의 버튼으로만 대화상자가 종료하도록 하는 기능
                    builder.setCancelable(false);

                    //대화상자 만들기
                    //주의사항 : 대화상자가 보여진 이후의 코드가 실행된다.
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show(); //대화상자 보이기

                }


            }
        });

        return fragmentView;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fragmentMainCommunity = new FragmentMainCommunity();
        fragmentProudCommunity = new FragmentProudCommunity();
        fragmenetQuestionCommunity = new FragmenetQuestionCommunity();
        fragmentPlaygroundCommunity = new FragmentPlaygroundCommunity();




        //첫번째로 보여지는 Fragment설정
        getChildFragmentManager().beginTransaction().replace(R.id.container, fragmentMainCommunity).commit();

        //TabLayout UI객체 찿기
        TabLayout tabs = view.findViewById(R.id.tabs);
        tabs.addTab(tabs.newTab().setText("  메 인  "));
        tabs.addTab(tabs.newTab().setText("새식물 자랑"));
        tabs.addTab(tabs.newTab().setText("가드닝 질문"));
        tabs.addTab(tabs.newTab().setText("식물 놀이터"));
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                Log.d("TAG", "선택된 탭 : " + position);

                Fragment selectedFragment = null;
                if (position == 0) {
                    selectedFragment = fragmentMainCommunity;
                } else if (position == 1) {
                    selectedFragment = fragmentProudCommunity;
                } else if (position == 2) {
                    selectedFragment = fragmenetQuestionCommunity;
                } else if (position == 3) {
                    selectedFragment =  fragmentPlaygroundCommunity;

                }
                getChildFragmentManager().beginTransaction().replace(R.id.container, selectedFragment).commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                Log.d("TAG", "onTabUnselected() : " + tab);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                Log.d("TAG", "onTabReselected() : " + tab);
            }
        });
    }
}