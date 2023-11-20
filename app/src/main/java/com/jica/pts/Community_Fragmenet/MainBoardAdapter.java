package com.jica.pts.Community_Fragmenet;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.jica.pts.Bean.Board;
import com.jica.pts.R;

import java.util.ArrayList;

public class MainBoardAdapter extends RecyclerView.Adapter<MainBoardAdapter.MainBoardViewHolder> implements OnMainBoardClickListeners {
    ArrayList<Board> NewArrayList;
    Context context;
    OnMainBoardClickListeners listener;


    public MainBoardAdapter(ArrayList<Board> NewArrayList, Context context) {
        this.NewArrayList = NewArrayList;
        this.context = context;
    }


    @NonNull
    @Override
    public MainBoardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_community_item, parent, false);


        return new MainBoardViewHolder(view, this);
    }


    @Override
    public void onBindViewHolder(@NonNull MainBoardViewHolder holder, int position) {
        if (NewArrayList.get(position).getBoard_photo1() != null) {
            Glide.with(holder.itemView)
                    .load(NewArrayList.get(position).getBoard_photo1())
                    .into(holder.imgPhoto1);
        } else {
            holder.imgPhoto1.setImageResource(R.drawable.background_no_image);
        }

        //Board 객체에서 가공하여 holder에 저장
        holder.tvTitle.setText(NewArrayList.get(position).getBoard_title());

        switch (NewArrayList.get(position).getBoard_name()) {
            case "새식물 자랑":
                holder.tvBoardType.setBackgroundResource(R.drawable.shape_tetragon13);
                break;
            case "가드닝 질문":
                holder.tvBoardType.setBackgroundResource(R.drawable.shape_tetragon14);
                break;
            case "식물 놀이터":
                holder.tvBoardType.setBackgroundResource(R.drawable.shape_tetragon15);
                break;
        }
        holder.tvBoardType.setText(NewArrayList.get(position).getBoard_name());


    }

    @Override
    public int getItemCount() {


        return (NewArrayList != null ? NewArrayList.size() : 0);
    }

    //변경된 부분4
    @Override
    public void onItemClick(MainBoardViewHolder viewHolder, View view, int position) {
        //현재의 Adapter클래스에서 OnPersonClickListener을 구현하였으므로 아래의 메서드를 재정의한다.
        if (listener != null) {
            // 현재 Adapter객체의 멤버변수인 listener의 onItemclick()메서드를 작동시킨다.
            // 그런데 listener멤버변수는 아래의 setOnItemClickListener()메서드의 인자로 전달된
            // 외부에서 만들어진 익명의 클래스 객체이다.
            listener.onItemClick(viewHolder, view, position);
        }

    }

    //변경된 부분6
    public void setOnItemClickListener(OnMainBoardClickListeners listener) {
        this.listener = listener;

    }


    static class MainBoardViewHolder extends RecyclerView.ViewHolder {
        TextView tvBoardType, tvTitle;
        ImageView imgPhoto1;


        //변경된 부분 1
        //UI 객체 찾기
        //final OnPersonItemClickListener listener==> final(상수) 선언으로 인해 내부에서 변경 불가
        public MainBoardViewHolder(@NonNull View itemView, final OnMainBoardClickListeners listener) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvCmName);
            imgPhoto1 = itemView.findViewById(R.id.imgPhoto);
            tvBoardType = itemView.findViewById(R.id.tvBoardType);


            //1. 특정 게시판의 아이템인 "더보기" 이벤트 핸들러 설정
            imgPhoto1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //현재의 게시글 위치를 설정
                    int position = getAdapterPosition();
                    if (listener != null) {
                        //listener는 현재의 Adpapter 객체를 의미한다.
                        //그러므로 아래의 코드는 현재 Adapter객체의 onItemClick()메서드를 호출한다.
                        //현재의 게시글 위치 전송

                        listener.onItemClick(MainBoardViewHolder.this, view, position);

                    }
                }
            });
        }
    }
}

