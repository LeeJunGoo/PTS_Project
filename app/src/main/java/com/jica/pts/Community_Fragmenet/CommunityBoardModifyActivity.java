package com.jica.pts.Community_Fragmenet;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.LongDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.protobuf.StringValue;
import com.jica.pts.Bean.Board;
import com.jica.pts.MainFragment.BottomTabActivity;
import com.jica.pts.R;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommunityBoardModifyActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PICK_IMAGES = 101;
    int maxSelectableCount; // 최대 선택 가능한 이미지 개수
    private List<Uri> selectedImageUri = new ArrayList<>();
    private ArrayList<String> board_photo = new ArrayList<>();
    private RecyclerView photoRecyclerView;
    private PhotoAdapter photoAdapter;
    private StorageReference storageReference;

    private boolean isFirst = true;


    //UI 객체 선언
    EditText edtTitle, edtContent;
    TextView tvModify, tvBoardTitle, tvImgCount;
    Spinner spBoardTitleList;
    ImageButton imgBtnSelect;
    ImageView imgCancle;


    //DB 객체 선언
    FirebaseFirestore db;
    FirebaseStorage storage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_board_modify);

        //UI 객체 찾기
        spBoardTitleList = findViewById(R.id.spBoardTitleList);
        tvModify = findViewById(R.id.tvModify);
        tvBoardTitle = findViewById(R.id.tvBoardTitle);
        edtTitle = findViewById(R.id.edtTitle);
        edtContent = findViewById(R.id.edtContent);
        imgCancle = findViewById(R.id.imgCancle);


        //Image UI 객체 찾기
        tvImgCount = findViewById(R.id.tvImgCount);
        photoRecyclerView = findViewById(R.id.rcPhotoModify);
        imgBtnSelect = findViewById(R.id.imgBtnSelect);


        //DB(FireStore) 연동
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();


        //게시글 상세페이지에서 전달 받은 항목들 변수에 저장
        Intent BoardIntent = getIntent();
        int Board_number = Integer.valueOf(BoardIntent.getStringExtra("Board_number"));
        String board_title = BoardIntent.getStringExtra("board_title");
        String board_content = BoardIntent.getStringExtra("board_content");
        String board_name = BoardIntent.getStringExtra("board_name");
        board_photo = BoardIntent.getStringArrayListExtra("board_photo");


        edtTitle.setText(board_title);
        edtContent.setText(board_content);


        // 리사이클러뷰의 틀을 미리 만들어 놓고 추후 데이터를 삽입하여
        //                               선택한 이미지 uri    선택한 이미지 개수
        photoAdapter = new PhotoAdapter(selectedImageUri, tvImgCount);
        // LinearLayoutManager: 아이템을 세로 또는 가로로 일렬로 배치하는 레이아웃 관리자입니다.
        // 세로 스크롤 목록 또는 가로 스크롤 목록에 사용됩니다.
        //                                                                                                                            true 일경우:  오른쪽에서 왼쪽 또는 아래에서 위로
        //                                                                         레이아웃 관리자의 방향을 설정    역방향 스크롤 여부를 설정  false 일경우: 왼쪽에서 오른쪽 또는 위에서 아래로
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        photoRecyclerView.setLayoutManager(layoutManager);
        //리사이클러뷰에 전달받은 어댑터의 내용을 적용
        photoRecyclerView.setAdapter(photoAdapter);

        //최대 선택 가능한 이미지 개수를 Adapter에서 받아 사용한다.
        //이유) 개수의 초기상태 및 전체 이미지 개수를 하나로 연동하여 사용
        maxSelectableCount = photoAdapter.maxSelectableCount;

        //Storage에 저장된 이미지 파일 불러오기
           for (String imageUrl : board_photo) {
                   downloadAndAddImage(imageUrl);

           }



        imgBtnSelect.setOnClickListener(new View.OnClickListener() {
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


        //게시글 목록 리스트 생성
        String items[] = {"게시판을 선택해주세요", "새식물 자랑", "가드닝 질문", "식물 놀이터"};

        //게시글 목록 리스트 연결
        //                                                                                                  어댑터 형식,           원본 데이터
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, items);

        //Spinner 클릭시 드롭다운되어 보여질 목록들에 대한 xml 레이아웃지정
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //Spinner와 어댑터 연결
        spBoardTitleList.setAdapter(adapter);


        //Spinner 이벤트 핸들러
        spBoardTitleList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (isFirst) {
                    isFirst = false;
                    tvBoardTitle.setText(board_name);
                    if (board_name.equals("새식물 자랑")) {
                        spBoardTitleList.setSelection(1);
                    } else if (board_name.equals("가드닝 질문")) {
                        spBoardTitleList.setSelection(2);

                    } else if (board_name.equals("식물 놀이터")) {
                        spBoardTitleList.setSelection(3);
                    }

                } else if (!(tvBoardTitle.equals(""))) {
                    tvBoardTitle.setText(items[i]);
                    // 컬러 변경
                    // tvBoardSelect.setTextColor(Color.parseColor("#C5E1A5"));

                    Log.d("BoardWrite", "게시판 목록에서 " + +i + "를 선택함");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Log.d("BoardWrite", "게시판 목록에서 선택한 항목이 없음");
            }
        });


        //editText의 텍스트 변화에 대한 이벤트 핸들러
        addTextWatcherForMaxLength(edtTitle, 15);
        addTextWatcherForMaxLength(edtContent, 500);


        //뒤로가기 버튼 이벤트 핸들러
        imgCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CommunityDetailPage.class);
                intent.putExtra("Board_number", Board_number + "");
                startActivity(intent);
                finish();
            }
        });

        // 완료 버튼 이벤트 핸들러
        // 유효성 검사 및 DB 저장
        tvModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //유효성 검사
                if (tvBoardTitle.getText().equals("게시판을 선택해주세요")) {
                    Toast.makeText(getApplicationContext(), "게시판을 선택해주세요", Toast.LENGTH_SHORT).show();

                } else if (edtTitle.getText().length() < 3) {
                    Toast.makeText(getApplicationContext(), "제목을 3글자이상 입력해주세요", Toast.LENGTH_SHORT).show();


                } else if (edtContent.getText().length() < 10) {
                    Toast.makeText(getApplicationContext(), "내용을 10글자이상 입력해주세요", Toast.LENGTH_SHORT).show();


                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(CommunityBoardModifyActivity.this);
                    builder.setMessage("글을 작성하시겠습니까?");

                    //긍정적 성격의 버튼 이벤트 핸들러              ButtonPositive(-1)
                    builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Log.d("TAG", selectedImageUri + "");

                            // 기존 Storage에 저장된 사진 삭제
                            deleteFileFromStorage("images/PTS/" + Board_number + "/");

                            //DB 저장(!!) 및 엑티비티 이동
                            saveBoardDataWithNextDocumentId(Board_number);


                        }
                    });

                    //부정적 성격의 버튼                ButtonNegative(-2)
                    builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Log.d("TAG", selectedImageUri + "");
                        }
                    });

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

    }

    //Storage에 저장된 파일을 휴대폰 데이터에 임시 저장
    private void downloadAndAddImage(String imageUrl) {
        // 이미지 다운로드
        StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);
        File localFile = null;

        try {
            // 이미지를 저장할 로컬 파일을 생성
            localFile = File.createTempFile("images", "jpg");

            // 다운로드한 이미지를 로컬 파일로 저장
            File finalLocalFile = localFile;
            storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    // 이미지 다운로드 및 로컬 저장 성공 시
                    Uri localUri = Uri.fromFile(finalLocalFile);

                    //Uri 리스트 및 RecyclerView에 이미지 추가 및 변경
                    selectedImageUri.add(localUri);
                    photoAdapter.notifyDataSetChanged();


                    //이미지 버튼 비활성화
                    if (selectedImageUri.size() >= maxSelectableCount) {
                        imgBtnSelect.setVisibility(View.GONE);
                    }
                    //이미지 버튼 활성화
                    addTextWatcherForMaxLengths(tvImgCount, maxSelectableCount);
                    // 이미지 선택 개수 업데이트
                    updateSelectedImageCount(selectedImageUri.size(), maxSelectableCount);
                    // 디버깅을 위한 로그
                    Log.d("ImageDownload", "Image downloaded successfully: " + localUri.toString());
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // 이미지 다운로드 실패 시 처리
                    Toast.makeText(getApplicationContext(), "이미지 다운로드 실패", Toast.LENGTH_SHORT).show();

                    // 디버깅을 위한 로그
                    Log.e("ImageDownload", "Image download failed: " + e.getMessage());
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Storage의 폴더 삭제(데이터 비우기)
    private void deleteFileFromStorage(String filePath) {
        // 폴더 내의 모든 파일 목록을 가져오는 Task를 생성합니다.
        storageReference.child(filePath).listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                // 폴더 내의 모든 파일을 순회하며 삭제합니다.
                for (StorageReference item : listResult.getItems()) {
                    item.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // 파일 삭제 성공
                            String deletedFileName = item.getName();
                            // 삭제한 파일에 대한 처리 (예: 로그 기록)
                            Log.d("StorageDelete", "Deleted file: " + deletedFileName);
                        }
                    });
                }
            }
        });
    }

    // 이미지를 Firebase Storage에 업로드하는 메서드
    private void uploadStorageImage(Uri imageUri, int nextDocumentId, int i) {
        // 업로드할 파일 이름 설정 (여기서는 현재 시간을 파일 이름으로 사용)
        String fileName = "photo" + System.currentTimeMillis() + ".jpg";

        // 이미지를 업로드할 경로와 파일 이름 설정
        String uploadPath = "images/PTS/" + nextDocumentId + "/" + fileName;

        // StorageReference를 사용하여
        // DB(Storage)에 경로 및 파일 이름 설정
        StorageReference imageRef = storageReference.child(uploadPath);

        // 위에 선언한 경로 및 파일 이름으로
        // 이미지를 DB(Storage)에 업로드
        UploadTask uploadTask = imageRef.putFile(imageUri);
        Log.d("TAG", "확인" + imageUri);

        // 업로드 성공 또는 실패에 대한 리스너 설정
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // 업로드 성공 시 처리 (예: 성공 메시지 표시)
                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String downloadUrl = uri.toString();
                        updateBoardDownloadUrl(nextDocumentId, downloadUrl, i);

                        Log.d("i", "두번쨰 I:" + i);

                    }

                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // 업로드 실패 시 처리 (예: 실패 메시지 표시)
                Toast.makeText(getApplicationContext(), "이미지 업로드 실패", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void ResetBoardDownloadUrl(int nextDocumentId) {
        Map<String, Object> like = new HashMap<>();

        for (int j = 1; j <= 5; j++) {
            like.put("board_photo" + j, null);
        }
        db.collection("Board").document(String.valueOf(nextDocumentId)).update(like)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                    }
                });

    }

    private void updateBoardDownloadUrl(int nextDocumentId, String downloadUrl, int i) {
        Map<String, Object> like = new HashMap<>();
        like.put("board_photo" + i, downloadUrl);


        db.collection("Board").document(String.valueOf(nextDocumentId)).update(like)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        Log.d("i", "세번 째 I:" + i);

                    }
                });
    }


    //위에서 intent 객체에 저장한 이미지 데이터를 사용하는 메서드
    //다른 액티비티에서 실행된 작업이 완료된 후 호출됩니다.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_PICK_IMAGES && resultCode == RESULT_OK) {
            if (data != null) {
                // 현재 선택된 이미지 갯수
                int currentSelectedCount = selectedImageUri.size();

                // 선택한 이미지 URI를 가져와서 리스트에 추가
                if (data.getClipData() != null) {
                    int count = data.getClipData().getItemCount();
                    for (int i = 0; i < count && currentSelectedCount < maxSelectableCount; i++) {
                        Uri imageUri = data.getClipData().getItemAt(i).getUri();
                        selectedImageUri.add(imageUri);
                        if (selectedImageUri.size() >= maxSelectableCount) {
                            Toast.makeText(getApplicationContext(), "이미지는 최대" + maxSelectableCount + "개까지 업로드 가능합니다", Toast.LENGTH_SHORT).show();
                        }
                        currentSelectedCount++;
                    }
                } else if (data.getData() != null && currentSelectedCount < maxSelectableCount) {
                    Uri imageUri = data.getData();
                    selectedImageUri.add(imageUri);
                }

                // RecyclerView에 변경 사항을 알림
                photoAdapter.notifyDataSetChanged();

                // 선택한 이미지 개수를 표시하는 메서드 호출
                updateSelectedImageCount(currentSelectedCount, maxSelectableCount);

                // ------------------------------------------------------------------------

                if (selectedImageUri.size() == maxSelectableCount) {

                    imgBtnSelect.setVisibility(View.GONE);

                    addTextWatcherForMaxLengths(tvImgCount, maxSelectableCount);
                    // ------------------------------------------------------------------------


                }
            }
        }
    }

    // 선택한 이미지 개수를 표시하는 메서드
    public void updateSelectedImageCount(int currentSelectedCount, int maxSelectableCount) {
        String countText = currentSelectedCount + "/" + maxSelectableCount;
        tvImgCount.setText(countText);
    }

    private void saveBoardDataWithNextDocumentId(int Board_number) {
        Map<String, Object> BoardUpdate = new HashMap<>();
        BoardUpdate.put("board_title", edtTitle.getText().toString());
        BoardUpdate.put("board_content", edtContent.getText().toString());
        BoardUpdate.put("board_name", tvBoardTitle.getText().toString());


        // DB에 업데이트
        db.collection("Board").document(String.valueOf(Board_number)).update(BoardUpdate)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("TAG", "게시판에 저장을 완료했습니다.");

                        //수정 후 초기화
                        edtTitle.setText("");
                        edtContent.setText("");
                        spBoardTitleList.setSelection(0);
                        tvBoardTitle.setText("게시판을 선택해주세요");

                        ResetBoardDownloadUrl(Board_number);

                        int i = 1;
                        for (Uri imageUri : selectedImageUri) {
                            uploadStorageImage(imageUri, Board_number, i);
                            i++;
                        }
                    }
                });

        Intent intent = new Intent(getApplicationContext(), BottomTabActivity.class);
        startActivity(intent);
        finish();

    }


    //EditText 이벤트 핸들러 메서드
    //                                              EditText          , 제한 길이 문자열
    public void addTextWatcherForMaxLength(final EditText editText, final int maxLength) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > maxLength) {
                    editText.setError("최대 " + maxLength + "자까지 입력 가능합니다.");
                    //버튼 기능 false
                    tvModify.setClickable(false);

                } else {
                    editText.setError(null);
                    //버튼 기능 true
                    tvModify.setClickable(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }


    // 선택한 이미지 개수를 표시하는 메서드
    // 이미지 개수가 5개 미만일 경우 이미지 버튼 활성화하기
    public void addTextWatcherForMaxLengths(final TextView textView, final int maxLength) {
        textView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int count = Integer.valueOf(charSequence.toString().substring(0, charSequence.toString().indexOf("/")));
                if (count != maxLength) {
                    imgBtnSelect.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }


}