package com.jica.pts.Community_Fragmenet;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.jica.pts.Bean.Board;
import com.jica.pts.R;

import java.util.ArrayList;


public class FragmenetQuestionCommunity extends Fragment {
    private RecyclerView recyclerView;
    private BoardAdapter adapter;
    private ArrayList<Board> arrayList;
    private FirebaseFirestore db;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_question_community, container, false);
        recyclerView = view.findViewById(R.id.rvQuestionCm);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        //데이터 검색 이전 틀을 미리 만들어 놓고 검색 후 바로 대입 방식
        arrayList = new ArrayList<>();
        adapter = new BoardAdapter(arrayList,getActivity());
        recyclerView.setAdapter(adapter);

        //DB 연결
        db= FirebaseFirestore.getInstance();

        //데이터 가져오기
        loadBoardData();

        //2. 게시판의 "더보기" 이벤트 핸들러 설정
        adapter.setOnItemClickListener(new OnBoardClickListener() {
            @Override
            public void onItemClick(BoardAdapter.BoardViewHolder viewHolder, View view, int position) {
                Intent intent = new Intent(getContext(), CommunityDetailPage.class);
                intent.putExtra("Board_number", arrayList.get(position).getBoard_number()+ "");
                startActivity(intent);
            }
        });

        return view;
    }


    private void loadBoardData() {
        // 컬랙션(Board)의 모든 문서(document)에서 필드(board_name)중 필드 값이"가드닝 질문"인 문서를 읽어온다.
        // 정렬 board_date의 날짜를 기준으로 내림차순
        // 주의) 필드의 문자 값을 넣을때는 띄어쓰기까지 생각해야한다.
        db.collection("Board").whereEqualTo("board_name", "가드닝 질문").whereEqualTo("board_del", false).orderBy("board_number", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                            if(task.isSuccessful()){
                                arrayList.clear();
                                //firestore에  저장된 문서(가드닝 질문)를 리스트에 저장
                                for(QueryDocumentSnapshot document : task.getResult()){
                                    Board board = document.toObject(Board.class);
                                    arrayList.add(board);
                                }
                                //변경된 내용을 화면에 표시
                                adapter.notifyDataSetChanged();

                            }else {
                                Log.e("TAG","Document(가드닝 질문)의 데이터 get과정에서 Error 발생", task.getException());

                            }
                        });


    }

}