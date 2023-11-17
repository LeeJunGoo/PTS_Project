package com.jica.pts.Community_Fragmenet;

import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.Timestamp;
import com.jica.pts.Bean.Board;
import com.jica.pts.R;

import java.util.ArrayList;
import java.util.Date;


//변경된 부분3
//RecyclerView의 어댑터는 반드시 RecyclerView.Adapter 클래스를 상속받아야 한다.
public class BoardAdapter extends RecyclerView.Adapter<BoardAdapter.BoardViewHolder> implements OnBoardClickListener {
    ArrayList<Board> arrayList;
    Context context;

    //변경된 부분5
    //클릭 이벤트 처리를 위한 listener
    OnBoardClickListener listener;


    public BoardAdapter(ArrayList<Board> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }


    @NonNull
    @Override
    public BoardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);


        return new BoardViewHolder(view, this);
    }


    @Override
    public void onBindViewHolder(@NonNull BoardViewHolder holder, int position) {

        if (arrayList.get(position).getBoard_photo1() != null) {
            Glide.with(holder.itemView)
                    .load(arrayList.get(position).getBoard_photo1())
                    .into(holder.imgPhoto1);
        } else {
            holder.imgPhoto1.setImageResource(R.drawable.background_no_image);
        }


        if (arrayList.get(position).getBoard_photo2() != null) {
            Glide.with(holder.itemView)
                    .load(arrayList.get(position).getBoard_photo2())
                    .into(holder.imgPhoto2);
        } else {
            holder.imgPhoto2.setVisibility(View.GONE);
        }


        if (arrayList.get(position).getBoard_photo3() != null) {
            Glide.with(holder.itemView)
                    .load(arrayList.get(position).getBoard_photo3())
                    .into(holder.imgPhoto3);
        } else {
            holder.imgPhoto3.setVisibility(View.GONE);
        }

        if (arrayList.get(position).getBoard_photo4() != null) {
            Glide.with(holder.itemView)
                    .load(arrayList.get(position).getBoard_photo4())
                    .into(holder.imgPhoto4);
        } else {
            holder.imgPhoto4.setVisibility(View.GONE);
        }

        if (arrayList.get(position).getBoard_photo5() != null) {
            Glide.with(holder.itemView)
                    .load(arrayList.get(position).getBoard_photo5())
                    .into(holder.imgPhoto5);
        } else {
            holder.imgPhoto5.setVisibility(View.GONE);
        }



/*        Glide.with(holder.itemView)
                .load(arrayList.get(position).getBoard_photo3())
                .into(holder.imgPhoto3);

        Glide.with(holder.itemView)
                .load(arrayList.get(position).getBoard_photo4())
                .into(holder.imgPhoto4);

        Glide.with(holder.itemView)
                .load(arrayList.get(position).getBoard_photo5())
                .into(holder.imgPhoto5);*/



    //Board 객체에서 가공하여 holder에 저장
        holder.tvId.setText(arrayList.get(position).

    getUser_id());
        holder.tvTitle.setText(arrayList.get(position).

    getBoard_title());


    String sampleText = "";
        if(arrayList.get(position).

    getBoard_content().

    length() >=20)

    {
        sampleText = arrayList.get(position).getBoard_content().substring(0, 20) + " ...";
    } else

    {
        sampleText = arrayList.get(position).getBoard_content();
    }
        holder.tvContent.setText(sampleText);


    // Timestamp형으로 저장된 시간을 여기서 쓸수 있게 string형으로 변환
    Timestamp timestamp = arrayList.get(position).getBoard_date();
    // Timestamp를 Date로 변환
    Date date = timestamp.toDate();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yy.MM.dd");
    String board_date = dateFormat.format(date);
        holder.tvTime.setText(board_date);


}

    @Override
    public int getItemCount() {
        return (arrayList != null ? arrayList.size() : 0);
    }

    //변경된 부분4
    @Override
    public void onItemClick(BoardViewHolder viewHolder, View view, int position) {
        //현재의 Adapter클래스에서 OnPersonClickListener을 구현하였으므로 아래의 메서드를 재정의한다.
        if (listener != null) {
            // 현재 Adapter객체의 멤버변수인 listener의 onItemclick()메서드를 작동시킨다.
            // 그런데 listener멤버변수는 아래의 setOnItemClickListener()메서드의 인자로 전달된
            // 외부에서 만들어진 익명의 클래스 객체이다.
            listener.onItemClick(viewHolder, view, position);

        }

    }

    //변경된 부분6
    public void setOnItemClickListener(OnBoardClickListener listener) {
        this.listener = listener;

    }


    static class BoardViewHolder extends RecyclerView.ViewHolder {
        TextView tvId, tvTitle, tvTime, tvContent, tvBoardDetailPage;
        ImageView imgPhoto1, imgPhoto2, imgPhoto3, imgPhoto4, imgPhoto5;




        //변경된 부분 1
        //UI 객체 찾기
        //final OnPersonItemClickListener listener==> final(상수) 선언으로 인해 내부에서 변경 불가
        public BoardViewHolder(@NonNull View itemView, final OnBoardClickListener listener) {
            super(itemView);
            tvId = itemView.findViewById(R.id.tvId);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvContent = itemView.findViewById(R.id.tvContent);
            imgPhoto1 = itemView.findViewById(R.id.imgPhoto1);
            imgPhoto2 = itemView.findViewById(R.id.imgPhoto2);
            imgPhoto3 = itemView.findViewById(R.id.imgPhoto3);
            imgPhoto4 = itemView.findViewById(R.id.imgPhoto4);
            imgPhoto5 = itemView.findViewById(R.id.imgPhoto5);

            tvBoardDetailPage = itemView.findViewById(R.id.tvBoardDetailPage);


        /*    //변경된 부분2
               // 전체 아이템 클릭 리스너
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (listener != null) {
                        //listener는 현재의 Adpapter 객체를 의미한다.
                        //그러므로 아래의 코드는 현재 Adapter객체의 onItemClick()메서드를 호출한다.
                        listener.onItemClick(BoardViewHolder.this, view, position);

                    }
                    //여기에 구체적인 이벤트 처리 코드를 작성하자.

                }
            });*/


         //1. 특정 게시판의 아이템인 "더보기" 이벤트 핸들러 설정
            tvBoardDetailPage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //현재의 게시글 위치를 설정
                    int position = getAdapterPosition();
                    if (listener != null) {
                        //listener는 현재의 Adpapter 객체를 의미한다.
                        //그러므로 아래의 코드는 현재 Adapter객체의 onItemClick()메서드를 호출한다.
                        //현재의 게시글 위치 전송

                        listener.onItemClick(BoardViewHolder.this, view, position);


                    }

                }
            });

        }
    }


}




