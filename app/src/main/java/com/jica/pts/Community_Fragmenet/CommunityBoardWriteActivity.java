package com.jica.pts.Community_Fragmenet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jica.pts.Bean.Board;
import com.jica.pts.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommunityBoardWriteActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PICK_IMAGES = 101;
    private List<Uri> selectedImageUris = new ArrayList<>();
    private RecyclerView photoRecyclerView;
    private PhotoAdapter photoAdapter;


    private StorageReference storageReference;


    //UI 객체 선언
    Spinner spBoardList;
    TextView tvCheck, tvBoardSelect, selectedImageCountTextView;
    EditText etTitle, etContent;
    ImageView imgBack;
    ImageButton selectImagesButton;

    //DB 객체 선언
    FirebaseFirestore db;
    FirebaseStorage storage;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_board_write);

        //UI 객체 찾기 1
        spBoardList = findViewById(R.id.spBoardList);
        tvCheck = findViewById(R.id.tvcheck);
        tvBoardSelect = findViewById(R.id.tvBoardSelect);
        etTitle = findViewById(R.id.etTitle);
        etContent = findViewById(R.id.etContent);
        imgBack = findViewById(R.id.imgBack);

        //Image UI 객체 찾기 2
        selectedImageCountTextView = findViewById(R.id.tvImageCount);
        photoRecyclerView = findViewById(R.id.rcPhotoWrite);
        selectImagesButton = findViewById(R.id.imgbtnSelect);


        //DB(FireStore) 연동
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();


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


        //게시글 목록 리스트 생성
        String items[] = {"게시판을 선택해주세요", "새식물 자랑", "가드닝 질문", "식물 놀이터"};

        //게시글 목록 리스트 연결
        //                                                                                                  어댑터 형식,           원본 데이터
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, items);


        //Spinner 클릭시 드롭다운되어 보여질 목록들에 대한 xml 레이아웃지정
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //Spinner와 어댑터 연결
        spBoardList.setAdapter(adapter);


        //Spinner 이벤트 핸들러
        spBoardList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            boolean isFirst = true;

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (isFirst) {
                    isFirst = false;
                    tvBoardSelect.setText("게시판을 선택해주세요");

                } else if (!(tvBoardSelect.equals(""))) {
                    tvBoardSelect.setText(items[i]);
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
        addTextWatcherForMaxLength(etTitle, 15);
        addTextWatcherForMaxLength(etContent, 500);


        //뒤로가기 버튼 이벤트 핸들러
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CommunityProfileActivity.class);
                startActivity(intent);
                finish();
            }
        });


        // 완료 버튼 이벤트 핸들러
        // 유효성 검사 및 DB 저장
        tvCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //유효성 검사
                if (tvBoardSelect.getText().equals("게시판을 선택해주세요")) {
                    Toast.makeText(getApplicationContext(), "게시판을 선택해주세요", Toast.LENGTH_SHORT).show();

                } else if (etTitle.getText().length() < 3) {
                    Toast.makeText(getApplicationContext(), "제목을 3글자이상 입력해주세요", Toast.LENGTH_SHORT).show();


                } else if (etContent.getText().length() < 10) {
                    Toast.makeText(getApplicationContext(), "내용을 10글자이상 입력해주세요", Toast.LENGTH_SHORT).show();


                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(CommunityBoardWriteActivity.this);
                    builder.setMessage("글을 작성하시겠습니까?");

                    //긍정적 성격의 버튼 이벤트 핸들러              ButtonPositive(-1)
                    builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //DB 저장(!!) 및 엑티비티 이동
                            saveBoardDataWithNextDocumentId();

                            Intent intent = new Intent(getApplicationContext(), CommunityProfileActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });

                    //부정적 성격의 버튼                ButtonNegative(-2)
                    builder.setNegativeButton("아니오", null);

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

    // 이미지를 Firebase Storage에 업로드하는 함수
    private void uploadImage(Uri imageUri, int nextDocumentId, int i) {
        // 업로드할 파일 이름 설정 (여기서는 현재 시간을 파일 이름으로 사용)
        String fileName = "photo" + System.currentTimeMillis() + ".jpg";

        // 이미지를 업로드할 경로와 파일 이름 설정
        String uploadPath = "images/PTS/"+nextDocumentId+"/" + fileName;

        // StorageReference를 사용하여
        // DB(Storage)에 경로 및 파일 이름 설정
        StorageReference imageRef = storageReference.child(uploadPath);

        // 위에 선언한 경로 및 파일 이름으로
        // 이미지를 DB(Storage)에 업로드
        UploadTask uploadTask = imageRef.putFile(imageUri);

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

                        Log.d("i", "두번쨰 I:"+ i);

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

    private void updateBoardDownloadUrl(int nextDocumentId, String downloadUrl, int i) {
      /*  Board board = new Board();

        board.setBoard_photo1(downloadUrl);*/

        Map<String, Object> like = new HashMap<>();
        like.put("board_photo"+i, downloadUrl);


        db.collection("Board").document(String.valueOf(nextDocumentId)).update(like)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){

                        Log.d("i", "세번 째 I:"+i);

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

                addTextWatcherForMaxLengths(selectedImageCountTextView, 5);
                // ------------------------------------------------------------------------


                }
            }
        }
    }

    // 선택한 이미지 개수를 표시하는 메서드
    public void updateSelectedImageCount(int currentSelectedCount, int maxSelectableCount) {
        String countText = currentSelectedCount + "/" + maxSelectableCount;
        selectedImageCountTextView.setText(countText);
    }

    private void saveBoardDataWithNextDocumentId() {
        // "Board" 컬렉션 생성 및 참조
        CollectionReference boardCollection = db.collection("Board");

        // 쿼리문 생성
        // "Board" 컬렉션을 날짜 필드(board_number)를 기준으로 내림차순으로 정렬하고(Query.Direction.DESCENDING),
        // 가장 최근(최신) 문서 1개를 가져와서(limit(1)) 확인한다. ==> [0] -> [1] -> [2] ->[3]
        boardCollection.orderBy("board_number", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {

                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        int i = 1;
                        //queryDocumentSnapshots: (document) 목록을 나타내는 객체
                        // 컬렉션(board)에 문서(document)가 하나 이상 있는 경우
                        if (!queryDocumentSnapshots.isEmpty()) {
                            //쿼리문(날짜 필드를 기준으로 내림차순으로 정렬 후 최근 문서 1개를 가져온다)==> [0] -> [1] -> [2] ->[3]
                            DocumentSnapshot lastDocument = queryDocumentSnapshots.getDocuments().get(0);
                            long currentDocumentId = lastDocument.getLong("board_number");

                            // 현재 문서(document) ID에 1을 더하여 다음 문서(document)의 ID를 설정
                            long nextDocumentId = currentDocumentId + 1;


                            // 이미지를 하나씩 업로드
                            // selectedImageUris 저장된 이미지를 Uri imageUri 객체로 선언하여 하나씩 업로드
                            for (Uri imageUri : selectedImageUris) {
                                uploadImage(imageUri, (int)nextDocumentId, i);
                              //  Log.d("i", "첫번째 I:"+i);
                                i++;

                            }

                            // DB(firestore)에 저장
                            saveBoardData(nextDocumentId);

                        } else {
                            //Board 컬렉션에 문서(document)가 없는 경우, 문서 id 초기값을 1로 설정합니다.
                            saveBoardData(1);

                            // 이미지를 하나씩 업로드
                            // selectedImageUris 저장된 이미지의 uri를 Uri 객체로 선언하여 하나씩 업로드
                            for (Uri imageUri : selectedImageUris) {
                                uploadImage(imageUri,1, i);
                                i++;
                            }


                        }
                        Log.d("BoardWriteDB", "Query 정상 작동");
                    }

                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Log.d("BoardWriteDB", "Query 설정하는 중에 오류가 발생했습니다.");

                    }
                });
        finish();
    }

    private void saveBoardData(long documentId) {
        //FirebaseAuth db 연동
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        //현재 접속된 id를 가져온다.
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        //Board 객체 생성
        Board board = new Board();

        //날짜 객체 생성
        Timestamp date = Timestamp.now();

        //saveBoardDataWithNextDocumentId() 메서드에서 전달 받은 nextDocumentId 저장
        long setBoard = documentId;


        // Board 객체에 데이터 저장
        // Bean에 있는 Board 객체에 선언하지 않을 경우 기본값으로 String null, int는 0으로 db에 저장된다, boolean 초기값은 false이다.
        board.setBoard_title(etTitle.getText().toString());
        board.setBoard_content(etContent.getText().toString());
        board.setBoard_name(spBoardList.getSelectedItem().toString());
        board.setUser_id(currentUser != null ? currentUser.getEmail() : ""); // currentUser가 null이 아닌지 확인 후 getEmail 호출
        board.setBoard_date(date);
        board.setBoard_number((int) documentId);



        // 컬렉션(Board)에 사용자가 정의한 문서(document) 이름(식별자) 및 필드(field) 데이터 추가
        db.collection("Board")
                .document(String.valueOf(documentId)) //사용자가 정의한 문서(document) 이름(식별자)
                .set(board)                 //board 객체에 저장한 데이터를 db(fireStore) 저장
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // EditText 초기화
                        etTitle.setText("");
                        etContent.setText("");

                        Log.d("BoardWriteDB", setBoard + "게시판에 저장을 완료했습니다.");

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("BoardWriteDB", "저장을 실패했습니다.");

                    }
                });
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
                    tvCheck.setClickable(false);

                } else {
                    editText.setError(null);
                    //버튼 기능 true
                    tvCheck.setClickable(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }


    // 선택한 이미지 개수를 표시하는 메서드
    public void addTextWatcherForMaxLengths(final TextView textView, final int maxLength) {
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