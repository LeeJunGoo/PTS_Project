package com.jica.pts.test;

import android.net.Uri;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.jica.pts.Community_Fragmenet.ImageSliderAdapter;
import com.jica.pts.R;

import java.util.ArrayList;

public class MainActivity5 extends AppCompatActivity {

    private ViewPager2 sliderViewPager;
    private LinearLayout layoutIndicator;
    private ImageSliderAdapter adapter;

    private ArrayList<String> photoUris = new ArrayList<>();

  /*  private String[] images = new String[] {
            "https://cdn.pixabay.com/photo/2019/12/26/10/44/horse-4720178_1280.jpg",
            "https://cdn.pixabay.com/photo/2020/11/04/15/29/coffee-beans-5712780_1280.jpg",
            "https://cdn.pixabay.com/photo/2020/03/08/21/41/landscape-4913841_1280.jpg",
            "https://cdn.pixabay.com/photo/2020/09/02/18/03/girl-5539094_1280.jpg",
            "https://cdn.pixabay.com/photo/2014/03/03/16/15/mosque-279015_1280.jpg"
    };*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);

        sliderViewPager = findViewById(R.id.sliderViewPager);
        layoutIndicator = findViewById(R.id.layoutIndicators);
        sliderViewPager.setOffscreenPageLimit(1);


        // Firebase Storage에서 파일 목록을 불러오는 메서드 호출
        loadPhotosFromFirebaseStorage();

        adapter = new ImageSliderAdapter(this,photoUris);
        sliderViewPager.setAdapter(adapter);

        sliderViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setCurrentIndicator(position);
            }
        });

      //  setupIndicators(photoUris.size());

    }




    private void loadPhotosFromFirebaseStorage() {
        // Firebase Storage 초기화
        FirebaseStorage storage = FirebaseStorage.getInstance();

        // Firebase Storage의 "images/" 경로에 있는 파일 목록을 가져옵니다.
        storage.getReference().child("images/999").listAll().addOnCompleteListener(new OnCompleteListener<ListResult>() {
            @Override
            public void onComplete(@NonNull Task<ListResult> task) {
                if (task.isSuccessful()) {
                    // 가져온 파일 목록을 순회하면서 URI를 추출하여 리스트에 추가합니다.
                    for (com.google.firebase.storage.StorageReference item : task.getResult().getItems()) {
                        item.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                // URI를 String 형변환 후 대입 성공적으로 가져왔을 때 처리
                                photoUris.add(uri.toString());
                               setupIndicators(photoUris.size());
                                adapter.notifyDataSetChanged();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // URI를 가져오지 못했을 때 처리
                                e.printStackTrace();
                            }
                        });
                    }
                } else {
                    // 파일 목록을 가져오는 데 실패한 경우 처리
                }
            }
        });
    }


    private void setupIndicators(int count) {
        //기존 인디케이터를 제거한다.
        layoutIndicator.removeAllViews();

        ImageView[] indicators = new ImageView[count];
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        params.setMargins(16, 8, 16, 8);

        for (int i = 0; i < indicators.length; i++) {
            indicators[i] = new ImageView(this);
            indicators[i].setImageDrawable(ContextCompat.getDrawable(this,
                    R.drawable.bg_indicator_inactive));
            indicators[i].setLayoutParams(params);
            layoutIndicator.addView(indicators[i]);
        }
        setCurrentIndicator(0);
    }

    private void setCurrentIndicator(int position) {
        int childCount = layoutIndicator.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageView imageView = (ImageView) layoutIndicator.getChildAt(i);
            if (i == position) {
                imageView.setImageDrawable(ContextCompat.getDrawable(
                        this,
                        R.drawable.bg_indicator_active
                ));
            } else {
                imageView.setImageDrawable(ContextCompat.getDrawable(
                        this,
                        R.drawable.bg_indicator_inactive
                ));
            }
        }
    }
}
