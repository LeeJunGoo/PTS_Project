package com.jica.pts.test;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jica.pts.Community_Fragmenet.PhotoAdapter;
import com.jica.pts.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivity2 extends AppCompatActivity {
    private static final int REQUEST_CODE_PICK_IMAGES = 101;
    Button selectImagesButton;
    private Button uploadButton;
    private List<Uri> selectedImageUris = new ArrayList<>();
    private RecyclerView photoRecyclerView;
    private PhotoAdapter photoAdapter;
    private StorageReference storageReference; // Firebase Storage 참조
    private FirebaseStorage storage;
    private TextView selectedImageCountTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        // DB 연동
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        //UI 객체 찾기
        selectImagesButton = findViewById(R.id.selectImagesButton);
        uploadButton = findViewById(R.id.uploadButton);
        photoRecyclerView = findViewById(R.id.photoRecyclerView);
        selectedImageCountTextView = findViewById(R.id.selectedImageCountTextView);

        // 리사이클러뷰의 틀을 만들어 놓고 아래에서 데이터를 삽입한다.
        //                                선택한 이미지 uri        선택한 이미지 개수
        photoAdapter = new PhotoAdapter(selectedImageUris, selectedImageCountTextView);

        // LinearLayoutManager: 아이템을 세로 또는 가로로 일렬로 배치하는 레이아웃 관리자입니다.
        // 세로 스크롤 목록 또는 가로 스크롤 목록에 사용됩니다.
        //                                                                                                                            true 일경우:  오른쪽에서 왼쪽 또는 아래에서 위로
        //                                                                         레이아웃 관리자의 방향을 설정    역방향 스크롤 여부를 설정  false 일경우: 왼쪽에서 오른쪽 또는 위에서 아래로
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        photoRecyclerView.setLayoutManager(layoutManager);

        //리사이클러뷰에 전달받은 어댑터의 내용을 적용
        photoRecyclerView.setAdapter(photoAdapter);



        selectImagesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //갤러리에서 이미지 선택을 위한 Intent를 생성
                Intent intent = new Intent();

                // "*/*" 파일 및 사진 등 모든 문서 보기
                // "image/*" 갤러리 앱에서 이미지 파일만
                // "image/png": 갤러리 앱에서 이미지 파일중 png 파일만 적용  /jpeg 등등
                intent.setType("image/*");

                // Intent.ACTION_GET_CONTENT를 사용하여 갤러리나 파일 탐색기와 상호 작용하여 컨텐츠(이미지)를 가져올 수 있도록 함
                intent.setAction(Intent.ACTION_GET_CONTENT);

                // 다중 선택(multiple selection)을 허용하는 옵션
                // EXTRA_ALLOW_MULTIPLE  true일 경우: 다중 선택
                //                       false일 경우: 단일 선택
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);

                // 이미지 선택 화면으로 이동(?)
                // "사진을 선택해주세요"는 다이얼로그의 타이틀(제목)로 표시 ==> 현재는 표시되지 않고 있다.
                // 현재 기능에서는 intent 객체를 전달하는 기능만 수행
                startActivityForResult(Intent.createChooser(intent, "사진을 선택해주세요"), REQUEST_CODE_PICK_IMAGES);
            }
        });

        // Firebase Storage에 업로드 버튼 이벤트 핸들러
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 이미지를 하나씩 업로드
                // selectedImageUris 저장된 이미지의 uri를 Uri 객체로 선언하여 하나씩 업로드
                for (Uri imageUri : selectedImageUris) {
                    uploadImage(imageUri);
                }
            }
        });
    }

    //다른 액티비티에서 실행된 작업이 완료된 후 호출됩니다.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_PICK_IMAGES && resultCode == RESULT_OK) {
            if (data != null) {
                // 현재 선택된 이미지 갯수
                int currentSelectedCount = selectedImageUris.size();

                // 최대 선택 가능한 이미지 갯수
                int maxSelectableCount = 5;

                // 선택한 이미지 URI를 가져와서 리스트에 추가
                if (data.getClipData() != null) {
                    int count = data.getClipData().getItemCount();
                    if (count > 6) {
                        Toast.makeText(getApplicationContext(), "경고", Toast.LENGTH_SHORT).show();
                    }
                    for (int i = 0; i < count && currentSelectedCount < maxSelectableCount; i++) {
                        Uri imageUri = data.getClipData().getItemAt(i).getUri();
                        selectedImageUris.add(imageUri);
                        currentSelectedCount++;
                    }
                } else if (data.getData() != null && currentSelectedCount < maxSelectableCount) {
                    Uri imageUri = data.getData();
                    selectedImageUris.add(imageUri);
                } else {
                    // 선택 가능한 이미지 갯수를 초과한 경우 메시지 표시 또는 처리
                    // 여기에 처리 코드를 추가하세요.
                    Toast.makeText(this, "이미지는 최대 5개까지 업로드 가능합니다", Toast.LENGTH_SHORT).show();
                }

                // RecyclerView에 변경 사항을 알림
                photoAdapter.notifyDataSetChanged();

                // 선택한 이미지 개수를 표시하는 메서드 호출
                updateSelectedImageCount(currentSelectedCount, maxSelectableCount);

                // ------------------------------------------------------------------------
                if (selectedImageUris.size() == 5) {

                    selectImagesButton.setVisibility(View.GONE);
                }
                addTextWatcherForMaxLength(selectedImageCountTextView, 5);
                // ------------------------------------------------------------------------
            }
        }
    }

    // 이미지를 Firebase Storage에 업로드하는 함수
    private void uploadImage(Uri imageUri) {
        // 업로드할 파일 이름 설정 (여기서는 현재 시간을 파일 이름으로 사용)
        String fileName = "photo" + System.currentTimeMillis() + ".jpg";

        // 이미지를 업로드할 경로와 파일 이름 설정
        String uploadPath = "images/999/" + fileName;

        // StorageReference를 사용하여
        // DB(Storage)에 경로 및 파일 이름 설정
        StorageReference imageRef = storageReference.child(uploadPath);

        // 위에 선언한 경로 및 파일 이름으로
        // 이미지를 DB(Storage)에 업로드
        UploadTask uploadTask = imageRef.putFile(imageUri);

        // 업로드 성공 또는 실패에 대한 리스너 설정
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            // 업로드 성공 시 처리 (예: 성공 메시지 표시)
            Toast.makeText(MainActivity2.this, "이미지 업로드 성공", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            // 업로드 실패 시 처리 (예: 실패 메시지 표시)
            Toast.makeText(MainActivity2.this, "이미지 업로드 실패", Toast.LENGTH_SHORT).show();
        });
    }

    // 선택한 이미지 개수를 표시하는 메서드
    public void updateSelectedImageCount(int currentSelectedCount, int maxSelectableCount) {
        String countText = currentSelectedCount + "/" + maxSelectableCount;
        selectedImageCountTextView.setText(countText);
    }

    public void addTextWatcherForMaxLength(final TextView textView, final int maxLength) {
        textView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int count = Integer.valueOf(charSequence.toString().substring(0, charSequence.toString().indexOf("/")));
                if (count != maxLength) {
                    selectImagesButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
}
