package com.jica.pts.MainFragment;

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.jica.pts.Abc.AlarmCenterActivity;
import com.jica.pts.Abc.PlantRegisterActivity1;
import com.jica.pts.Login.UserTotalLoginActivity;
import com.jica.pts.R;

import java.util.Date;

public class FragmentPlantManagement extends Fragment {
    private final long finishtimeed = 1000;
    private long presstime = 0;

    //로그인(true), 비로그인(false) 정보
    boolean isLogin = checkLoginStatus();


    ImageButton btnclose;
    ImageView imgmenu;
    LinearLayout sidemenu;
    Button btnplantrg, btnsmLogin;
    TextView tvalarm, tvprofile, tvcalendar, tvsetting, tvpmdate;
    ConstraintLayout tvsmLogout;

    private FirebaseAuth firebaseAuth;


    //슬라이드 열기/닫기 플래그
    boolean isPageOpen = false;
    //슬라이드 열기 애니메이션
    Animation translateLeftAnim;
    //슬라이드 닫기 애니메이션
    Animation translateRightAnim;


    // 출력 형식 지정 (yyyy-MM-dd hh:mm:ss을 원하는 포맷으로 적절히 변경하시면 됩니다)
    SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy. MM. dd");

    // 날짜 저장 변수
    String dateData = new String();



    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //Fragment 자체의 xml layout file을 전개
        View fragmentView = inflater.inflate(R.layout.fragment_main_plant_management, container, false);



        //firebase 연결
        firebaseAuth = FirebaseAuth.getInstance();

        //Fragment 내부의 UI객체 찾기
        imgmenu = fragmentView.findViewById(R.id.imgmenu);
        sidemenu = fragmentView.findViewById(R.id.sidemenu);
        btnclose = fragmentView.findViewById(R.id.btnclose);
        btnplantrg = fragmentView.findViewById(R.id.btnpltrg);
        tvpmdate = fragmentView.findViewById(R.id.tvpmdate);

        //사이드바 내부 UI객체 찾기
        tvalarm = fragmentView.findViewById(R.id.tvalarm);
        tvprofile = fragmentView.findViewById(R.id.tvprofile);
        tvcalendar = fragmentView.findViewById(R.id.tvcalendar);
        tvsetting = fragmentView.findViewById(R.id.tvsetting);
        btnsmLogin = fragmentView.findViewById(R.id.btnsmLogin);
        tvsmLogout = fragmentView.findViewById(R.id.tvsmLogout);




        //date 생성
        Date date = new Date();
        dateData = simpleDate.format(date);

        //date 화면 출력
        tvpmdate.setText(String.valueOf(dateData));


        if (!(isLogin)) {
            //비로그인 상태
            btnsmLogin.setVisibility(View.VISIBLE);
            tvsmLogout.setVisibility(View.INVISIBLE);
        } else {
            //로그인 상태
            btnsmLogin.setVisibility(View.INVISIBLE);
            tvsmLogout.setVisibility(View.VISIBLE);

        }


        //이벤트 핸들러 설정
        ButtonHandler buttonHandler = new ButtonHandler();

        //1. 사이드바 열기 이벤트 핸들러
        imgmenu.setOnClickListener(buttonHandler);

        //2. 사이드바 닫기 이벤트 핸들러
        btnclose.setOnClickListener(buttonHandler);

        //3. 화분 등록 이벤트 핸들러
        btnplantrg.setOnClickListener(buttonHandler);

        //4. 사이드바 메뉴 이벤트 핸들러
        tvalarm.setOnClickListener(buttonHandler);
        tvprofile.setOnClickListener(buttonHandler);
        tvcalendar.setOnClickListener(buttonHandler);
        tvsetting.setOnClickListener(buttonHandler);
        btnsmLogin.setOnClickListener(buttonHandler);
        tvsmLogout.setOnClickListener(buttonHandler);


        //애니메이션
        translateLeftAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.translate_left);
        translateRightAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.translate_right);


        //애니메이션 리스너 설정
        SlidingPageAnimationListener animationListener = new SlidingPageAnimationListener();
        translateLeftAnim.setAnimationListener(animationListener);
        translateRightAnim.setAnimationListener(animationListener);


        return fragmentView;
    }

    //뒤로가기 2번 클릭시 앱 종료
    public void onBackPressed() {
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - presstime;

        if (0 <= intervalTime && finishtimeed >= intervalTime) {
            getActivity().finish();
        } else {
            presstime = tempTime;
            Toast.makeText(getActivity(), "한번더 누르시면 앱이 종료됩니다", Toast.LENGTH_SHORT).show();
        }
    }

    //이벤트 핸들러 설정
    //LayoutExample 강의 참조하기
    class ButtonHandler implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            int curId = view.getId();
            //1. 사이드바 이벤트 핸들러
            if (curId == R.id.imgmenu) {
                isPageOpen = false;
                //닫기
                if (isPageOpen) {
                    sidemenu.startAnimation(translateRightAnim);
                    //열기
                } else {
                    sidemenu.setVisibility(View.VISIBLE);
                    sidemenu.startAnimation(translateLeftAnim);

                }
                ;
                //2. 사이드바 이벤트 핸들러
            } else if (curId == R.id.btnclose) {
                //닫기
                if (isPageOpen) {
                    sidemenu.setVisibility(View.VISIBLE);
                    sidemenu.startAnimation(translateRightAnim);

                    //열기
                } else {
                    sidemenu.startAnimation(translateLeftAnim);

                }
                //식물 등록 이벤트 핸들러
            } else if (curId == R.id.btnpltrg) {
                if (!(isLogin)) {
                    //비로그인 상태
                    Toast.makeText(getActivity(), "로그인 후에 등록할 수 있어요", Toast.LENGTH_SHORT).show();
                    //Intent intent = new Intent(getActivity(), UserTotalLoginActivity.class);
                    //startActivity(intent);

                } else {
                    //로그인 상태
                    Intent intent = new Intent(getActivity(), PlantRegisterActivity1.class);
                    startActivity(intent);

                }






                //사이드바 내부 이벤트 핸들러
                //1. 알림센터
            } else if (curId == R.id.tvalarm) {
                Intent intent = new Intent(getActivity(), AlarmCenterActivity.class);
                startActivity(intent);

                //2. 프로필
            } else if (curId == R.id.tvprofile) {
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                FragmentProfile fragmentProfile = new FragmentProfile();
                transaction.replace(R.id.container, fragmentProfile).commit();

                BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottom_navigation);
                bottomNavigationView.setSelectedItemId(R.id.profile);

                // 3. 로그인
            } else if (curId == R.id.btnsmLogin) {
                Intent intent = new Intent(getActivity(), UserTotalLoginActivity.class);
                startActivity(intent);

                //4. 로그아웃
            } else if (curId == R.id.tvsmLogout) {
                firebaseAuth.signOut();
                Intent intent = new Intent(getActivity(), BottomTabActivity.class);
                startActivity(intent);

                //5. 캘린더
            } else if (curId == R.id.tvcalendar) {

                //6. 환경설정
            } else if (curId == R.id.tvsetting) {

            }
        }
    }


    private class SlidingPageAnimationListener implements Animation.AnimationListener {
        @Override
        public void onAnimationEnd(Animation animation) {
            //슬라이드 열기->닫기
            if (isPageOpen) {
                //imgmenu.setVisibility(View.VISIBLE);
                imgmenu.setClickable(true);
                sidemenu.setVisibility(View.VISIBLE);
                isPageOpen = false;
            }
            //슬라이드 닫기->열기
            else {
                //imgmenu.setVisibility(View.INVISIBLE);
                imgmenu.setClickable(false);
                sidemenu.setVisibility(View.VISIBLE);
                isPageOpen = true;
            }
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }

        @Override
        public void onAnimationStart(Animation animation) {

        }
    }

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