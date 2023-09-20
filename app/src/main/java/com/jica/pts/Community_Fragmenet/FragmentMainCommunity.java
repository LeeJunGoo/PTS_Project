package com.jica.pts.Community_Fragmenet;

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


public class FragmentMainCommunity extends Fragment {
    RecyclerView rcNewCM;
    RecyclerView rcHotCM;
    MainBoardAdapter NewBoardAdapter;
    MainBoardAdapter HotBoardAdapter;
    ArrayList<Board> NewArrayList;
    ArrayList<Board> HotArrayList;
    FirebaseFirestore db;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.fragment_home_community, container, false);

       rcNewCM = view.findViewById(R.id.rcNewCM);
       rcHotCM = view.findViewById(R.id.rcHotCM);

        NewArrayList = new ArrayList<>();
        HotArrayList = new ArrayList<>();

        db = FirebaseFirestore.getInstance();




        return view ;
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        rcNewCM.setHasFixedSize(true);
        rcNewCM.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        //리사이클러뷰의 틀을 만들어놓고 아래에서 데이터를 삽입한다.
        NewBoardAdapter = new MainBoardAdapter(NewArrayList, getActivity());
        rcNewCM.setAdapter(NewBoardAdapter);
        loadBoardData();

        rcHotCM.setHasFixedSize(true);
        rcHotCM.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        HotBoardAdapter = new MainBoardAdapter(HotArrayList, getActivity());
        rcHotCM.setAdapter(HotBoardAdapter);
        HotBoarData();
    }





    private void HotBoarData() {
         db.collection("Board").whereEqualTo("board_del", false).orderBy("board_great", Query.Direction.DESCENDING)
                .limit(5)
                .get()
                .addOnCompleteListener(task -> {
                    HotArrayList.clear();
                    if(task.isSuccessful()){
                        for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                            Board board = documentSnapshot.toObject(Board.class);
                            HotArrayList.add(board);
                        }
                        HotBoardAdapter.notifyDataSetChanged();
                    }else {
                        Log.d("TAG","메인 게시글 로딩 실패", task.getException());
                    }
                });
    }

    private void loadBoardData() {
        db.collection("Board").whereEqualTo("board_del", false).orderBy("board_number", Query.Direction.DESCENDING)
                .limit(5)
                .get()
                .addOnCompleteListener(task -> {
                    NewArrayList.clear();
                    if(task.isSuccessful()){
                        for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                            Board board = documentSnapshot.toObject(Board.class);
                            NewArrayList.add(board);
                        }
                        NewBoardAdapter.notifyDataSetChanged();
                    }else {
                        Log.d("TAG","메인 게시글 로딩 실패", task.getException());
                    }
                });

    }
}