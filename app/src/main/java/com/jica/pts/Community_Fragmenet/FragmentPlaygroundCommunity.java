package com.jica.pts.Community_Fragmenet;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.jica.pts.Bean.Board;
import com.jica.pts.R;

import java.util.ArrayList;


public class FragmentPlaygroundCommunity extends Fragment {
    RecyclerView recyclerView;
    BoardAdapter adapter;
    ArrayList<Board> arrayList;
    FirebaseFirestore db;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playground_community, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.rvPlayCm);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        //리사이클러뷰의 틀을 만들어놓고 아래에서 데이터를 삽입한다.
        arrayList = new ArrayList<>();
        adapter = new BoardAdapter(arrayList, getActivity());
        recyclerView.setAdapter(adapter);

        //이후 데이터를 가져와 미리 만들어 놓은 틀에 저장한다.
        db = FirebaseFirestore.getInstance();
        loadBoardData();


        //2. 게시판의 "더보기" 이벤트 핸들러 설정
        adapter.setOnItemClickListener(new OnPersonItemClickListener() {
            @Override
            public void onItemClick(BoardAdapter.BoardViewHolder viewHolder, View view, int position) {
                Intent intent = new Intent(getContext(), CommunityDetailPage.class);
                intent.putExtra("Board_number", arrayList.get(position).getBoard_number()+ "");
                startActivity(intent);
            }
        });
    }


    private void loadBoardData() {
            // 컬랙션(Board)의 모든 문서(document)에서 필드(board_name)중 필드 값이"식물 놀이터"인 문서를 읽어온다.
            // 정렬 board_date의 날짜를 기준으로 내림차순
            // 주의)필드의 문자 값을 넣을때는 띄어쓰기까지 생각해야한다.
            db.collection("Board").whereEqualTo("board_name", "식물 놀이터").whereEqualTo("board_del", false).orderBy("board_number", Query.Direction.DESCENDING)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            //리스트 초기화 ==> 기존 데이터 제거
                            arrayList.clear();
                            // firestore에 저장된 문서("식물 놀이터)를 리스트에 저장
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Board board = document.toObject(Board.class);
                                arrayList.add(board);
                            }

                            //변경된 내용을 화면에 표시
                            adapter.notifyDataSetChanged();
                        } else {
                            Log.e("TAG", "Error getting documents", task.getException());
                        }

                    });
        }


}

