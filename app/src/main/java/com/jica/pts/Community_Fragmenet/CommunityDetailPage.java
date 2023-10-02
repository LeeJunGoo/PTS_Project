package com.jica.pts.Community_Fragmenet;

import android.content.DialogInterface;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
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

    //UI 객체 선언
    TextView tvDetailTitle, tvDetailId, tvDetailTime, tvDetailContent, tvDetailCount;
    EditText etReply;
    ImageButton imgGreat, imgReplyCheck, imgbtnReply;
    Button btnDelect;
    LinearLayout LinearReply;


    //DB 객체 선언
    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;
    FirebaseUser CurrentUser;


    //이미지 객체 선언 1)
    private ViewPager2 sliderViewPager;
    private LinearLayout layoutIndicator;
    private ImageSliderAdapter adapter;
    private ArrayList<String> photoUris = new ArrayList<>();


    //댓글 객체 선언1)
    private RecyclerView rcReply;
    private ReplyAdapter replyAdapter;
    private ArrayList<Reply> replyArrayList;


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
        etReply = findViewById(R.id.etReply);
        imgGreat = findViewById(R.id.imgGreat);
        btnDelect = findViewById(R.id.btnDelect);
        imgReplyCheck = findViewById(R.id.imgReplyCheck);
        imgbtnReply= findViewById(R.id.imgbtnReply);
        LinearReply= findViewById(R.id.LinearReply);


        //댓글 객체 찾기 2)
        rcReply = findViewById(R.id.rcReply);
        rcReply.setHasFixedSize(true);
        rcReply.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        replyArrayList = new ArrayList<>();
        replyAdapter = new ReplyAdapter(replyArrayList, getApplicationContext());
        rcReply.setAdapter(replyAdapter);


        //이미지 객체 찾기 2)
        sliderViewPager = findViewById(R.id.sliderViewPager);
        layoutIndicator = findViewById(R.id.layoutIndicators);
        sliderViewPager.setOffscreenPageLimit(1);


        //이미지 틀 생성 3)
        adapter = new ImageSliderAdapter(this, photoUris);
        sliderViewPager.setAdapter(adapter);
        sliderViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setCurrentIndicator(position);
            }
        });

     /*   //이미지 초기 설정
        if(photoUris != null){

        }*/

        //DB 연결
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        CurrentUser = firebaseAuth.getCurrentUser();

        //FragmentPlayGroundCommunity에서 저장(putExtra)한 문서(document)번호를 intent 객체로 불러오기
        Intent BoardIntent = getIntent();
        int Board_number = Integer.valueOf(BoardIntent.getStringExtra("Board_number"));
        Toast.makeText(this, Board_number +"", Toast.LENGTH_SHORT).show();



        // Firebase Storage에서 파일 목록을 불러오는 메서드 호출 4)
        loadPhotosFromFirebaseStorage(Board_number);


        // 순서 1) DB에 저장된 값들 UI 객체에 읽어오기
        //쿼리문 해석: 컬랙션("Board")의 필드(board_number)에 값이 x인 문서(document)를 불러온다.
        db.collection("Board").whereEqualTo("board_number", Board_number)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // FireStore에 저장된 데이터(문서(document))를 리스트에 저장
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Board 객체에서 저장
                            Board board = document.toObject(Board.class);

                            //위의 board에서 가공한 데이터들의 값을 현재의 UI 객체에 읽어온다.
                            tvDetailTitle.setText(board.getBoard_title());
                            tvDetailContent.setText(board.getBoard_content());
                            tvDetailId.setText(board.getUser_id());

                            //주의 사항) 숫자 데이터를 불러올 때 에러가 발생하면 String형으로 형 변환 시키기
                            tvDetailCount.setText(String.valueOf(board.getBoard_great()));

                            // board에서 가공하여 Timestamp형으로 저장된 board_date(시간)를
                            // 현재의 UI객체에 저장하기 위해서 string형으로 형 변환
                            Timestamp timestamp = board.getBoard_date();
                            //Date 객체로 형변환 이유==> FireStore.txt에 설명
                            Date date = timestamp.toDate();
                            //SimpleDateFormat 기능==> FireStore.txt에 설명
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yy.MM.dd");
                            String board_date = dateFormat.format(date);
                            tvDetailTime.setText(board_date);

                            // 현재 접속 ID 및 글 작성 ID 확인 여부 후 삭제 버튼 Visible 및 gone 형태 유지
                            // 현재 위치에 선언한 이유: db 선언 당시에만 tvDetailId값을 읽어올 수 있기 때문이다. 만약
                            // 현재위치가 아닌 OnCreate 다른 부분에서 찾을 경우 tvDeatilId.getText() 및 데이터인 board.getBoard_Delect하면 null 값 및 기존 xml에 지정한 임시 text가 불러와진다.
                            // Log.d("Delect", CurrentUser.getEmail() + "," + tvDetailId); 확인해보기
                            if (CurrentUser != null) {
                                if (CurrentUser.getEmail().equals(tvDetailId.getText())) {
                                    btnDelect.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    } else {
                        Log.e("TAG", "Document 가공 과정에서 Error 발생", task.getException());
                    }
                });


        LoadReply(Board_number);


        //순서 2) 로그인 유효성 검사 1
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

            }
        });


        //삭제 버튼 이벤트 핸들러
        btnDelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Timestamp date = Timestamp.now();

                //삭제 데이터
                Map<String, Object> delect = new HashMap<>();
                delect.put("board_del", true);
                delect.put("board_del_date", date);

                AlertDialog.Builder builder = new AlertDialog.Builder(CommunityDetailPage.this);
                builder.setMessage("정말로 삭제하시겠습니까?");
                //긍정적 성격의 버튼 이벤트 핸들러              ButtonPositive(-1)
                builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        db.collection("Board").document(String.valueOf(Board_number)).update(delect)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {

                                    }
                                });
                        Intent intent = new Intent(getApplicationContext(), BottomTabActivity.class);
                        intent.putExtra("move", "DetailPage");
                        startActivity(intent);
                        //엑티비티에서 프래그먼트 이동 공식
                        //무슨 이유인지 모르겠는데 오류 발생;....
                        /*FragmentCommunity fragmentCommunity = new FragmentCommunity();
                        getSupportFragmentManager().beginTransaction().replace(R.id.container1, fragmentCommunity).commit();*/


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
        });
        imgbtnReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(imgbtnReply.isSelected()){
                    LinearReply.setVisibility(View.GONE);
                }else {
                    LinearReply.setVisibility(View.VISIBLE);
                }
                // isSelected() 메서드의 반환값은 true 또는 false 중 하나입니다.
                // "!" 연산자를 사용하여 현재 선택 상태를 반전시킵니다. 즉, true는 false로, false는 true로 바뀝니다.
                imgbtnReply.setSelected(!imgbtnReply.isSelected());
            }
        });


        // 댓글 버튼 이벤트 핸들러
        imgReplyCheck.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (CurrentUser != null) {
                    saveReplyDataWithNextDocumentId(Board_number);
                }
            }
        });


        //순서 3) 좋아요 버튼 이벤트 핸들러
        imgGreat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 로그인 유효성 검사 2
                // 로그인 유효성 검사를 1,2로 나눈 이유는 "좋아요" 이미지 클릭 시 팝업창("로그인하기")을 띄우기 위해 유효성 검사를 나누었다.
                // 만약 위에서(유혀성 검사1) "로그인하기" 팝업창 기능을 띄우면 현재 페이지를 들어올 때 팝업창이 등장하므로 부자연스럽다.
                if (CurrentUser != null) {

                    //순서 5) 좋아요 해제
                    if (imgGreat.isSelected()) {
                        imgGreat.setImageResource(R.drawable.icon_like_normal);
                        //좋아요 감소:   문서 번호    , 현재 좋아요 개수
                        setGreatdown(Board_number, Integer.valueOf(tvDetailCount.getText().toString()));

                    } else {
                        //순서 5) 좋아요 선택
                        imgGreat.setImageResource(R.drawable.icon_like);
                        //좋아요 증가:  문서 번호    ,  현재 좋아요 개수
                        setGreatUp(Board_number, Integer.valueOf(tvDetailCount.getText().toString()));

                    }
                    // isSelected() 메서드의 반환값은 true 또는 false 중 하나입니다.
                    // "!" 연산자를 사용하여 현재 선택 상태를 반전시킵니다. 즉, true는 false로, false는 true로 바뀝니다.
                    imgGreat.setSelected(!imgGreat.isSelected());


                } else {
                    //순서 4)
                    AlertDialog.Builder builder = new AlertDialog.Builder(CommunityDetailPage.this);
                    builder.setMessage("로그인 후 사용가능합니다.");
                    //긍정적 성격의 버튼 이벤트 핸들러              ButtonPositive(-1)
                    builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //DB 저장(!!) 및 엑티비티 이동
                            //saveBoardDataWithNextDocumentId();
                            Intent intent = new Intent(getApplicationContext(), UserTotalLoginActivity.class);
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
                        Log.d("Reply", "Error 댓글 이벤트 핸들러 에러", task.getException());
                    }
                });

    }


    private void saveReplyDataWithNextDocumentId(int Board_number) {
        // 쿼리문 해석:
        // 1) 상위 컬랙션인 "Board"의 현재 Document에서
        //    하위 컬랙션인 "Reply" 컬랙션의 문서 조회 및 생성  ==> 조회 및 생성 초기값이 없으면 자동 생성된다.
        // 2) "reply_number"인 필드값을 기준으로 내림차순으로 정렬하고(Query.Direction.DESCENDING), ==> 문서 값 변화: 1 -> 2 -> 3 -> 4
        //    가장 최근(최신) 문서 1개를 가져와서(limit(1)) 문서 값을 계속 증가 시킨다.
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
                            DocumentSnapshot lastDocument = queryDocumentSnapshots.getDocuments().get(0);
                            long currentDocumentId = lastDocument.getLong("reply_number");

                            // 현재 문서(document) ID에 1을 더하여 다음 문서(document)의 ID를 설정
                            long nextDocumentId = currentDocumentId + 1;


                            // DB(firestore)에 저장
                            saveBoardData(Board_number, nextDocumentId);
                            Log.d("Reply", String.valueOf(nextDocumentId));


                        } else {
                            //Board 컬렉션에 문서(document)가 없는 경우, 문서 id 초기값을 1로 설정합니다.
                            saveBoardData(Board_number, 1);
                            Log.d("Reply", String.valueOf(1));


                        }

                    }

                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Log.d("ReplyWriteDB", "Query 설정하는 중에 오류가 발생했습니다.");

                    }
                });

    }
    private void saveBoardData(long Board_number, long nextDocumentId) {
        //Reply 객체 생성
        Reply reply = new Reply();
        //날짜 객체 생성
        Timestamp date = Timestamp.now();

        // Board 객체에 데이터 저장
        // Bean에 있는 Board 객체에 선언하지 않을 경우 기본값으로 String null, int는 0으로 db에 저장된다, boolean 초기값은 false이다.
        reply.setUser_id(CurrentUser.getEmail());
        if (etReply.getText().toString().isEmpty()) {
            Toast.makeText(this, "댓글내용을 입력해주세요", Toast.LENGTH_SHORT).show();
            return;
        }
        reply.setReply_content(etReply.getText().toString());
        reply.setReply_date(date);
        reply.setReply_number((int) nextDocumentId);
        reply.setReply_check_revel(String.valueOf(nextDocumentId));


        //해석: 상위컬랙션인 Board 컬랙션의 문서 번호가 Board_number인 곳의 하위컬랙션인 Reply에 데이터 저장
        db.collection("Board").document(String.valueOf(Board_number)).collection("Reply").document(String.valueOf(nextDocumentId))//사용자가 정의한 문서(document) 이름(식별자)
                .set(reply)                 //board 객체에 저장한 데이터를 db(fireStore) 저장
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
                        Log.d("ReplyWriteDB", "저장을 실패했습니다.");

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


    // 순서 2)
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
                        // 특정 문서가 Firestore에 존재하는 지 여부
                        if (!document.exists()) {
                            db.collection("Board")
                                    .document(String.valueOf(board_number))
                                    .collection("Like")
                                    .document(CurrentUser.getUid())
                                    .set(like);
                        }
                    }
                });
    }


    //순서 2)
    //"좋아요 버튼" 초기 설정 ==> 중복 체크 제한을 위한 유효성 검사
    // true= 좋아요 누른 상태
    // false= 좋아요 누르지 않은 상태
    private void checkUID(int board_number) {
        // db.collection("Board").document().collection("Like").whereEqualTo(CurrentUser.getUid(), true).get()  ==> // 특정 값 없이 document()부분을 설정하지 않으면 뒤의 쿼리문에서 인식을 못하여 에러가 발생한다.
        db.collection("Board").document(String.valueOf(board_number)).collection("Like").whereEqualTo(CurrentUser.getUid(), true).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();

                        //쿼리 결과가 비어 있는 경우
                        if (querySnapshot.isEmpty()) {
                            Toast.makeText(getApplicationContext(), "좋아요 누르지 않은 상태", Toast.LENGTH_SHORT).show();
                            imgGreat.setSelected(false); // 좋아요 버튼 해제
                            imgGreat.setImageResource(R.drawable.icon_like_normal); // 좋아요 버튼 이미지 변경
                        } else {
                            //쿼리 결과가 존재하는 경우
                            Toast.makeText(getApplicationContext(), "좋아요 누른 상태", Toast.LENGTH_SHORT).show();
                            imgGreat.setSelected(true); // 좋아요 버튼 선택
                            imgGreat.setImageResource(R.drawable.icon_like); // 좋아요 버튼 이미지 변경
                        }
                    } else {
                        // 쿼리 실패 시
                        Toast.makeText(getApplicationContext(), "checkUID 에러 발생", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    //순서 6)
    //좋아요 증가 메서드          문서 번호         현재 좋아요 개수
    private void setGreatUp(int board_number, int board_count) {
        db.collection("Board").document(String.valueOf(board_number))
                .update("board_great", board_count + 1)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "setGreatUp 실행", Toast.LENGTH_SHORT).show();
                        //순서 7)
                        GreatUpCount(board_number);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "setGreatUp 에러 발생", Toast.LENGTH_SHORT).show();
                    }
                });

        db.collection("Board").document(String.valueOf(board_number)).collection("Like").document(CurrentUser.getUid()).update(CurrentUser.getUid(), true);
    }


    //순서 5)
    //좋아요 감소 메서드             문서 번호         현재 좋아요 개수
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
                        // 쓰기 성공
                        Toast.makeText(getApplicationContext(), "setGreatDown 실행", Toast.LENGTH_SHORT).show();
                        //순서 6)
                        GreatUpCount(board_number);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // 쓰기 실패
                        Toast.makeText(getApplicationContext(), "setGreatDown 에러 발생", Toast.LENGTH_SHORT).show();
                    }
                });

        db.collection("Board").document(String.valueOf(board_number)).collection("Like").document(CurrentUser.getUid())
                .update(CurrentUser.getUid(), false)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                    } else {
                        // 쿼리 실패 시
                        Toast.makeText(getApplicationContext(), "setGreatdown 쿼리 실패", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    //순서 6)
    //DB에 새로 저장한 좋아요 개수를 현재 UI화면에 적용하기(뿌려주기)
    private void GreatUpCount(int board_number) {
        db.collection("Board").document(String.valueOf(board_number)).get()
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
                        // 쓰기 실패
                        Toast.makeText(getApplicationContext(), "GreatUpCount 에러발생", Toast.LENGTH_SHORT).show();
                    }
                });


    }


}