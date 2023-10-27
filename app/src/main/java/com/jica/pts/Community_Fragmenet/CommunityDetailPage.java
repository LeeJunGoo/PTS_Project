package com.jica.pts.Community_Fragmenet;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.jica.pts.Bean.Board;
import com.jica.pts.Bean.Reply;
import com.jica.pts.Login.UserTotalLoginActivity;
import com.jica.pts.MainFragment.BottomTabActivity;
import com.jica.pts.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CommunityDetailPage extends AppCompatActivity {
    private boolean isProcessingClick = false;
    private boolean isSubReply = false;


    //UI 객체 선언
    TextView tvDetailTitle, tvDetailId, tvDetailTime, tvDetailContent, tvDetailCount, tvDetailCount2;
    EditText etReply, etSubReply;
    ImageButton imgGreat, imgReplyCheck, imgbtnReply, imgSubReplyCheck;
    Button btnDelect;
    LinearLayout LinearReply, LinearSubReply;


    //이미지 객체 선언 1)
    private ViewPager2 sliderViewPager;
    private LinearLayout layoutIndicator;
    private ImageSliderAdapter adapter;
    private ArrayList<String> photoUris = new ArrayList<>();



    //댓글 객체 선언1)
    private RecyclerView rcReply;
    private ReplyAdapter replyAdapter;
    private ArrayList<Reply> replyArrayList;


    //DB 객체 선언
    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;
    FirebaseUser CurrentUser;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_detail_page);

        //UI 객체 찾기
        tvDetailTitle = findViewById(R.id.tvDetailTitle);
        tvDetailId = findViewById(R.id.tvDetailId);
        tvDetailTime = findViewById(R.id.tvDetailTime);
        tvDetailContent = findViewById(R.id.tvDetailContent);
        tvDetailCount = findViewById(R.id.tvDetailCount);
        tvDetailCount2 = findViewById(R.id.tvDetailCount2);
        etReply = findViewById(R.id.etReply);
        etSubReply = findViewById(R.id.etSubReply);
        imgGreat = findViewById(R.id.imgGreat);
        btnDelect = findViewById(R.id.btnDelect);
        imgReplyCheck = findViewById(R.id.imgReplyCheck);
        imgSubReplyCheck= findViewById(R.id.imgSubReplyCheck);
        imgbtnReply= findViewById(R.id.imgbtnReply);
        LinearReply= findViewById(R.id.LinearReply);
        LinearSubReply= findViewById(R.id.LinearSubReply);



        //댓글 객체 찾기
        rcReply = findViewById(R.id.rcReply);
        rcReply.setHasFixedSize(true);
        rcReply.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        replyArrayList = new ArrayList<>();
        replyAdapter = new ReplyAdapter(replyArrayList, getApplicationContext());
        rcReply.setAdapter(replyAdapter);


        //이미지 객체 찾기
        sliderViewPager = findViewById(R.id.sliderViewPager);
        layoutIndicator = findViewById(R.id.layoutIndicators);
        sliderViewPager.setOffscreenPageLimit(1);


        //이미지 틀 생성
        adapter = new ImageSliderAdapter(this, photoUris);
        sliderViewPager.setAdapter(adapter);
        sliderViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setCurrentIndicator(position);
            }
        });

        //DB 연결
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        CurrentUser = firebaseAuth.getCurrentUser();

        //FragmentPlayGroundCommunity에서 저장(putExtra)한 문서(document)번호를 intent 객체로 불러오기
        Intent BoardIntent = getIntent();
        int Board_number = Integer.valueOf(BoardIntent.getStringExtra("Board_number"));


        //Firebase Storage에서 파일 목록 불러오기
        loadPhotosFromFirebaseStorage(Board_number);


        //DB에 저장된 Board 객체 읽어오기
        readDataFromFirestore(Board_number);

        //DB에 저장된 Reply 객체 읽어오기
        LoadReply(Board_number);


        //로그인 유효성 검사 1
        // if(CurrentUser != null)  ==> CurrentUser.getUid()를 가져올 때 로그인 되지 않은 상태에서 불러오면 오류가 발생하므로 로그인 여부 확인 후 가져오기
        // checkLike(Board_number); ==>  하위 컬랙션 데이터 생성
        // checkUID(Board_number);  ==>  좋아요 초기 설정
        if (CurrentUser != null) {
            checkLike(Board_number);
            checkUID(Board_number);
        }


        //답글 버튼 이벤트 핸들러
        replyAdapter.setOnItemClickListener(new OnReplyClickListener() {
            @Override
            public void onItemClick(ReplyAdapter.ReplyViewHolder viewHolder, View view, int position) {
                handleSubReplyButtonClick();

            }
        });


        //삭제 버튼 이벤트 핸들러
        btnDelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleDeleteButtonClick(Board_number);
            }
        });



        //댓글 유효성 이벤트 핸들러
        imgbtnReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleReplyButtonClick();
            }
        });



        //EditText) 댓글 입력 버튼 이벤트 핸들러
        imgReplyCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CurrentUser != null) {
                    saveReply(Board_number, Integer.valueOf(tvDetailCount2.getText().toString()));
                    // 입력 후 키패드 내리기
                    InputMethodManager manager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                    manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    LinearReply.setVisibility(View.GONE);

                }
            }
        });



        //좋아요 버튼 이벤트 핸들러
        imgGreat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleLikeButtonClick(Board_number);
            }
        });


    }





    private void handleLikeButtonClick(int Board_number) {
        // 로그인 유효성 검사 2
        //"좋아요" 이미지 클릭 시 팝업창("로그인하기")을 띄우기
        // 만약 위(로그인 유혀성 검사1)에서 "로그인하기" 팝업창 띄우면 현재 페이지를 들어올 때마다 "로그인하기" 팝업창이 등장하므로 부자연스럽다.
        if (CurrentUser != null) {
            //좋아요 버튼 유효성 검사
            // !isProcessingClick을 통해 postDelayed의 정상작동을 확인 후 좋아요 기능 수행
            if(!isProcessingClick){
                isProcessingClick = true;
                imgGreat.setEnabled(false);   //버튼 클릭 후 비활성화
                if (imgGreat.isSelected()) {
                    imgGreat.setImageResource(R.drawable.icon_like_normal);
                    setGreatdown(Board_number, Integer.valueOf(tvDetailCount.getText().toString()));
                } else {
                    imgGreat.setImageResource(R.drawable.icon_like);
                    setGreatUp(Board_number, Integer.valueOf(tvDetailCount.getText().toString()));
                }

                //버튼 클릭의 지연 핸들러
                imgGreat.postDelayed(new Runnable() {
                    @Override
                    //지연 시킨 이후 실행 할 명령문
                    public void run() {
                        isProcessingClick = false;
                        imgGreat.setEnabled(true);  //버튼 활성화
                        imgGreat.setSelected(!imgGreat.isSelected());    // 현재 선택 상태를 반전시킵니다. 즉, true는 false로, false는 true로 바뀝니다.

                    }
                }, 500);

            }

        } else {
                LoginDialog();
        }


    }


    //댓글 이미지 버튼 클릭 시 댓글 입력창 VISIBLE 및 GONE 형태 만들기
    //댓글 입력창 VISIBLE일 경우 답글 입력창 GONE 형태 만들기
    private void handleReplyButtonClick() {
        if (CurrentUser != null) {
            if (imgbtnReply.isSelected()) {
               LinearReply.setVisibility(View.GONE);
            } else {
                LinearReply.setVisibility(View.VISIBLE);
                LinearSubReply.setVisibility(View.GONE);
                //댓글 입력창을 닫지않고 바로 답글작성 버튼 클릭 시 기존 isSubReply 값인 true가 존재하여
                //버튼이 한번 작동하지않는 형태로 보인다. 그러므로 답글 변환값인 false을 주어야한다.
                isSubReply = false;
            }
             imgbtnReply.setSelected(!imgbtnReply.isSelected());
        } else {
            LoginDialog();
        }

    }

    private void handleSubReplyButtonClick() {
        if (CurrentUser != null) {
            if (isSubReply != false) {
                LinearSubReply.setVisibility(View.GONE);
                isSubReply = false;
            } else {
                LinearSubReply.setVisibility(View.VISIBLE);
                LinearReply.setVisibility(View.GONE);
                //댓글 버튼 변환값 설정
                imgbtnReply.setSelected(false);
                isSubReply = true;
            }
        } else {
            LoginDialog();
        }

    }


    private void LoginDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(CommunityDetailPage.this);
        builder.setMessage("로그인 후 사용가능합니다.");
        builder.setPositiveButton("로그인 하기", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(getApplicationContext(), UserTotalLoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        builder.setNegativeButton("취소", null);
        builder.setCancelable(false);
        // true일 경우  : 대화상자 버튼이 아닌 배경 및 back 버튼 눌렀을때도 종료하도록 하는 기능
        // false일 경우 : 대화상자의 버튼으로만 대화상자가 종료하도록 하는 기능

        //대화상자 생성
        //주의사항 : 대화상자가 생성된 이후에 코드가 실행된다.
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }



    private void handleDeleteButtonClick(int Board_number) {
        Timestamp date = Timestamp.now();
        Map<String, Object> delect = new HashMap<>();
        delect.put("board_del", true);
        delect.put("board_del_date", date);

        AlertDialog.Builder builder = new AlertDialog.Builder(CommunityDetailPage.this);
        builder.setMessage("정말로 삭제하시겠습니까?");
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                db.collection("Board").document(String.valueOf(Board_number)).update(delect)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    // Handle success
                                }
                            }
                        });
                 /*엑티비티에서 프래그먼트 이동 공식
                FragmentCommunity fragmentCommunity = new FragmentCommunity();
                getSupportFragmentManager().beginTransaction().replace(R.id.container1, fragmentCommunity).commit(); 이지만 error 발생...
                시간 관계상 프로젝트 완성을 위해 아래의 방법으로 실행하였다.
                */
                Intent intent = new Intent(getApplicationContext(), BottomTabActivity.class);
                intent.putExtra("move", "DetailPage");
                startActivity(intent);

            }
        });

        builder.setNegativeButton("아니오", null);
        builder.setCancelable(false);
        // true일 경우  : 대화상자 버튼이 아닌 배경 및 back 버튼 눌렀을때도 종료하도록 하는 기능
        // false일 경우 : 대화상자의 버튼으로만 대화상자가 종료하도록 하는 기능

        //대화상자 생성
        //주의사항 : 대화상자가 생성된 이후에 코드가 실행된다.
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    //쿼리문 해석: 컬랙션("Board")의 필드(board_number)에 값이 x인 문서(document)를 불러온다.
    private void readDataFromFirestore(int Board_number) {
        db.collection("Board").whereEqualTo("board_number", Board_number)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Board 객체에서 저장
                                Board board = document.toObject(Board.class);
                                updateUIWithBoardData(board);
                            }
                        } else {
                            Log.d("TAG", "CommunityDetailPage의  readDataFromFirestore에서 오류 발생", task.getException());
                        }
                    }
                });
    }

    //위의 readDataFromFirestore에서 가공한 데이터들을 현재의 UI 객체에 저장한다.
    //주의 사항) DB에서 숫자 데이터를 불러올 때 에러가 발생하면 String형으로 형 변환 시키기
    private void updateUIWithBoardData(Board board) {
        tvDetailTitle.setText(board.getBoard_title());
        tvDetailContent.setText(board.getBoard_content());
        tvDetailId.setText(board.getUser_id());
        tvDetailCount.setText(String.valueOf(board.getBoard_great()));
        tvDetailCount2.setText(String.valueOf(board.getBoard_reply()));

        Timestamp timestamp = board.getBoard_date();
        //Date 객체로 형변환 이유==> FireStore.txt에 설명
        Date date = timestamp.toDate();
        //SimpleDateFormat 기능==> FireStore.txt에 설명
        SimpleDateFormat dateFormat = new SimpleDateFormat("yy.MM.dd");
        String board_date = dateFormat.format(date);
        tvDetailTime.setText(board_date);

        // 현재 접속ID와 글 작성ID 일치 여부 후 삭제 버튼 Visible 및 gone 형태 유지
        if (CurrentUser != null && CurrentUser.getEmail().equals(tvDetailId.getText())) {
            btnDelect.setVisibility(View.VISIBLE);
        }
    }



    //
    private void LoadReply(int BoardNumber) {
        db.collection("Board").document(String.valueOf(BoardNumber))
                .collection("Reply").orderBy("reply_date", Query.Direction.DESCENDING).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        replyArrayList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Reply reply = document.toObject(Reply.class);
                            replyArrayList.add(reply);
                        }
                        //변경된 내용을 화면에 표시
                        replyAdapter.notifyDataSetChanged();


                    } else {
                        Log.d("TAG", "CommunityDetailPage의 LoadReply에서 오류 발생", task.getException());
                    }
                });

    }

    //1. Board 컬렉션에 board_reply(댓글 수) 증가
    //2. Reply 컬렉션에 입력한 댓글정보 저장
    //3. 입력한 댓글 내용 화면에 뿌려주기
    private void saveReply(int Board_number, int reply_Count){
        db.collection("Board").document(String.valueOf(Board_number))
                .update("board_reply", reply_Count+1).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                       //댓글 정보를 DB에 저장하기
                        saveReplyDataWithNextDocumentId(Board_number);
                        //댓글 수 읽어오기
                        ReplyUpCount(Board_number);

                    }
                });

    }

    // 쿼리문 해석:
    // 1) 상위 컬랙션인 "Board"의 현재 Document에서
    //    하위 컬랙션인 "Reply" 컬랙션의 문서 조회 및 생성  ==> 조회 및 생성 초기값이 없으면 자동 생성된다.
    // 2) "reply_number"인 필드값을 기준으로 내림차순으로 정렬하고(Query.Direction.DESCENDING), ==> 문서 값 변화: 4 -> 3 -> 2 -> 1
    //    가장 최근(최신) 문서 1개를 가져와서(limit(1)) 문서 값을 계속 증가 시킨다.
    private void saveReplyDataWithNextDocumentId(int Board_number) {
        db.collection("Board").document(String.valueOf(Board_number)).collection("Reply")
                .orderBy("reply_number", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        //queryDocumentSnapshots: (document) 목록을 나타내는 객체
                        // 컬렉션(Reply)에 문서(document)가 하나 이상 있는 경우
                        if (!queryDocumentSnapshots.isEmpty()) {
                            //쿼리문(날짜 필드를 기준으로 내림차순으로 정렬 후 최근 문서 1개를 가져온다)==> [0] -> [1] -> [2] ->[3]
                            //                                                                   4      3      2     1
                            DocumentSnapshot lastDocument = queryDocumentSnapshots.getDocuments().get(0);
                            long currentDocumentId = lastDocument.getLong("reply_number");

                            // 현재 문서(document) ID에 1을 더하여 다음 문서(document)의 ID를 설정
                            long nextDocumentId = currentDocumentId + 1;

                            // DB(firestore)에 저장
                            saveReplyData(Board_number, nextDocumentId);

                        } else {
                            //Reply 컬렉션에 문서(document)가 없는 경우, 문서 id 초기값을 1로 설정합니다.
                            saveReplyData(Board_number, 1);
                        }
                    }

                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("TAG", "CommunityDetailPage의 saveReplyDataWithNextDocumentId 쿼리문에서 error 발생", e);
                    }
                });

    }


    private void saveReplyData(long Board_number, long nextDocumentId) {
        Reply reply = new Reply();
        Timestamp date = Timestamp.now();

        // Bean 객체에 선언하지 않을 경우 기본값으로 String null, int는 0으로 db에 저장된다, boolean 초기값은 false이다.
        reply.setUser_id(CurrentUser.getEmail());
        if (etReply.getText().toString().isEmpty()) {
            Toast.makeText(this, "댓글내용을 입력해주세요", Toast.LENGTH_SHORT).show();
            return;
        }
        reply.setReply_content(etReply.getText().toString());
        reply.setReply_date(date);
        reply.setReply_number((int) nextDocumentId);
        reply.setReply_check_revel(String.valueOf(nextDocumentId));


        //쿼리문 해석: 상위컬랙션인 Board 컬랙션의 문서 번호가 Board_number인 곳의 하위컬랙션인 Reply에 데이터 저장
        db.collection("Board").document(String.valueOf(Board_number))
                .collection("Reply").document(String.valueOf(nextDocumentId))
                .set(reply)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        LoadReply((int) Board_number);
                        etReply.setText("");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("TAG", "CommunityDetailPage의 saveReplyData 쿼리문에서 error 발생", e);

                    }
                });
        // finish();
    }


    private void loadPhotosFromFirebaseStorage(int document) {
        // Firebase Storage 초기화
        FirebaseStorage storage = FirebaseStorage.getInstance();

        // Firebase Storage의 "images/" 경로에 있는 파일 목록을 가져옵니다.
        storage.getReference().child("images/PTS/" + document).listAll().addOnCompleteListener(new OnCompleteListener<ListResult>() {
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
                   Log.d("TAG", "CommunityDetailPage의 loadPhotosFromFirebaseStorage 쿼리문에서 error 발생", task.getException());
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


    private void checkLike(int board_number) {
        //하위 컬랙션(Like)에 저장할 데이터 선언
        Map<String, Object> like = new HashMap<>();
        like.put("Board_number", board_number);
        //CurrentUser.getUid()를 가져올 때 로그인 되지 않은 상태에서 불러오면 오류가 발생하므로 위에서 로그인 유효성 검사 후 선언
        like.put(CurrentUser.getUid(), false);

        db.collection("Board").document(String.valueOf(board_number)).collection("Like").document(CurrentUser.getUid()).get()
                .addOnCompleteListener(task -> {
                    //데이터 조회를 성공 시 수행
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        // 특정 문서가 Firestore에 존재하는지 여부
                        if (!document.exists()) {
                            db.collection("Board")
                                    .document(String.valueOf(board_number))
                                    .collection("Like")
                                    .document(CurrentUser.getUid())
                                    .set(like);
                        }
                    }else {
                        Log.d("TAG","CommunityDetailPage의 checkLike 쿼리문에서 오류 발생", task.getException());
                    }
                });
    }


    //"좋아요 버튼" 초기 설정 ==>  중복 체크 제한을 위한 유효성 검사
    // true= 좋아요 누른 상태
    // false= 좋아요 누르지 않은 상태
    private void checkUID(int board_number) {
        // db.collection("Board").document().collection("Like").whereEqualTo(CurrentUser.getUid(), true).get()
        // ==>  document()부분을 설정하지 않으면 뒤(하위 컬랙션)의 쿼리문에서 error가 발생한다.
        db.collection("Board").document(String.valueOf(board_number)).collection("Like").whereEqualTo(CurrentUser.getUid(), true).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                           //쿼리 결과가 존재하지 않을 경우
                        if (querySnapshot.isEmpty()) {
                            //Toast.makeText(getApplicationContext(), "좋아요 누르지 않은 상태", Toast.LENGTH_SHORT).show();
                            imgGreat.setSelected(false); // 좋아요 버튼 해제
                            imgGreat.setImageResource(R.drawable.icon_like_normal); // 좋아요 버튼 이미지 변경
                        } else {
                            //쿼리 결과가 존재하는 경우
                            //Toast.makeText(getApplicationContext(), "좋아요 누른 상태", Toast.LENGTH_SHORT).show();
                            imgGreat.setSelected(true); // 좋아요 버튼 선택
                            imgGreat.setImageResource(R.drawable.icon_like); // 좋아요 버튼 이미지 변경
                        }
                    } else {
                       Log.d("TAG", "CommunityDetailPage의 checkUID 쿼리문에서 오류 발생", task.getException());
                    }
                });
    }


    //좋아요 증가 메서드
    //board_number: 문서 번호, board_count: 좋아요 개수
    private void setGreatUp(int board_number, int board_count) {
        db.collection("Board").document(String.valueOf(board_number))
                .update("board_great", board_count + 1)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        db.collection("Board").document(String.valueOf(board_number)).collection("Like").document(CurrentUser.getUid())
                                .update(CurrentUser.getUid(), true)
                                .addOnCompleteListener(task -> {
                                    if(task.isSuccessful()){
                                        GreatUpCount(board_number);
                                    }else {
                                        Log.d("TAG", "CommunuityDetailPage의 setGreatUp update 쿼리문에서 error 발생", task.getException());
                                    }
                                });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("TAG", "CommunuityDetailPage의 setGreatUp 쿼리문에서 error 발생", e);
                    }
                });


    }

    //좋아요 감소 메서드
    //board_number: 문서 번호, board_count: 좋아요 개수
    private void setGreatdown(int board_number, int board_count) {

        //Map<String, Object> updateData = new HashMap<>();
        //updateData.put("board_great", board_count - 1);
        //updateData.put(CurrentUser.getUid(), false);
        // 위의 형식으로 사용하지 않은 이유
        //"board_great"의 필드 위치는 board 컬랙션에 존재하고 CurrentUser.getUid()의 필드의 위치는 하위 컬랙션인 "Like"에 존재하므로 아래의 쿼리문(update)에서 false 값이 정상적으로 적용되지 않는다.
        // 위의 형식으로 저장하면 board 컬랙션의 필드에 새로운 값인 CurrentUser.getUid(), false가 추가된다.

        db.collection("Board").document(String.valueOf(board_number))
                .update("board_great", board_count - 1)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //Toast.makeText(getApplicationContext(), "setGreatDown 실행", Toast.LENGTH_SHORT).show();
                        db.collection("Board").document(String.valueOf(board_number)).collection("Like").document(CurrentUser.getUid())
                                .update(CurrentUser.getUid(), false)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        GreatUpCount(board_number);
                                    } else {
                                        Log.d("TAG", "CommunuityDetailPage의 setGreatDown update 쿼리문에서 error 발생", task.getException());
                                    }
                                });

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("TAG", "CommunuityDetailPage의 setGreatDown 쿼리문에서 error 발생", e);
                    }
                });

    }


  //DB에 새로 저장한 좋아요 개수를 현재 UI화면에 적용하기(뿌려주기)
    private void GreatUpCount(int board_number) {
        String documentId = String.valueOf(board_number);
        DocumentReference docRef = db.collection("Board").document(documentId);

        // 실시간 업데이트 리스너 등록
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    // 에러 처리
                    return;
                }
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    // 문서가 존재하고 업데이트되었을 때 실행됩니다.
                    Long GreatUp = documentSnapshot.getLong("board_great");
                    if (GreatUp != null) {
                        // board_great 필드가 null이 아닌 경우
                        tvDetailCount.setText(String.valueOf(GreatUp));

                    }
                } else {


                }
            }
        });

    }


  //DB에 새로 저장한 댓글 개수를 실시간으로 UI화면에 적용하기(뿌려주기)
    private void ReplyUpCount(int board_number) {
        String documentId = String.valueOf(board_number);
        DocumentReference DocRef =db.collection("Board").document(String.valueOf(board_number));

        DocRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if(e != null){
                            // 에러 처리
                            return;
                        }
                        if (documentSnapshot != null && documentSnapshot.exists()){
                            // 문서가 존재하고 업데이트되었을 때 실행됩니다.
                            Long ReplyCount = documentSnapshot.getLong("board_reply");
                            if (ReplyCount != null){
                                tvDetailCount2.setText(String.valueOf(ReplyCount));
                            }
                        }

                    }
                });
    }






      /*  db.collection("Board").document(String.valueOf(board_number)).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            //"long" ==> "Long"으로 쓰기/ "long으로 쓸 경우 에러 발생"
                            Long GreatUp = documentSnapshot.getLong("board_great");
                            if (GreatUp != null) {
                                tvDetailCount.setText(String.valueOf(GreatUp));
                            }

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("TAG", "CommunityDetailPage의 GreatUpCount 쿼리문에서 error 발생", e);
                    }
                });*/




}