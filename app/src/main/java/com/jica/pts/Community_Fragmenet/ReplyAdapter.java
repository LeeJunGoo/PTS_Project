package com.jica.pts.Community_Fragmenet;


import android.content.Context;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.jica.pts.Bean.Reply;
import com.jica.pts.R;

import java.util.ArrayList;
import java.util.Date;

//변경된 부분 3
public class ReplyAdapter extends RecyclerView.Adapter<ReplyAdapter.ReplyViewHolder> implements OnReplyClickListener {

    ArrayList<Reply> replyArrayList;
    Context context;

    //변경된 부분4
    //클릭 이벤트 처리를 위한 listener
    OnReplyClickListener listener;

    public ReplyAdapter(ArrayList<Reply> replyArrayList, Context context){
        this.replyArrayList = replyArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ReplyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.reply_item, parent, false);
        return new ReplyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReplyViewHolder holder, int position) {
        holder.tvReplyContent.setText(replyArrayList.get(position).getReply_content());
        holder.tvReplyID.setText(replyArrayList.get(position).getUser_id());
        // Timestamp형으로 저장된 시간을 여기서 쓸수 있게 string형으로 변환
        Timestamp timestamp = replyArrayList.get(position).getReply_date();
        // Timestamp를 Date로 변환
        Date date = timestamp.toDate();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yy.MM.dd");
        String board_date = dateFormat.format(date);
        holder.tvReplyDate.setText(board_date);

        //대댓글 일경우
        //왼쪽 topMargin 효과 및 답글작성 버튼 비활성화
        if(replyArrayList.get(position).getReply_revel() == 2){
            holder.leftMargin.setText("1");
            holder.tvReply.setVisibility(View.GONE);
        }
        //댓글일 경우
        //왼쪽 topMargin 효과 비활성화 및 답글작성 버튼 활성화, 위쪽 topMargin 효과 활성화
        if(replyArrayList.get(position).getReply_revel() == 1){
            holder.leftMargin.setText("");
            holder.tvReply.setVisibility(View.VISIBLE);
            holder.topMargin.setVisibility(View.VISIBLE);


            //첫번째 댓글일 경우
            //위쪽 topMargin 효과 비활성화
            if(replyArrayList.get(position).getReply_number() == 1){
                holder.topMargin.setVisibility(View.GONE);
            }
        }

    }


    // Count 필수!!
    @Override
    public int getItemCount() {
        return (replyArrayList != null ? replyArrayList.size() : 0);
    }


    //변경된 부분6
    public void setOnItemClickListener(OnReplyClickListener listener) {
        this.listener = listener;

    }


    //변경된 부분5
    @Override
    public void onItemClick(ReplyViewHolder viewHolder, View view, int position) {
        if (listener != null) {
            // 현재 Adapter객체의 멤버변수인 listener의 onItemclick()메서드를 작동시킨다.
            // 그런데 listener멤버변수는 아래의 setOnItemClickListener()메서드의 인자로 전달된
            // 외부에서 만들어진 익명의 클래스 객체이다.
            listener.onItemClick(viewHolder, view, position);

        }
    }


    //변경된 부분 1
    //final OnPersonItemClickListener listener==> final(상수) 선언으로 인해 내부에서 변경 불가
    public class ReplyViewHolder extends RecyclerView.ViewHolder  {
        //UI 객체 선언
        TextView tvReplyContent, tvReplyID, tvReplyDate,leftMargin;
        ImageView tvReply;
        View topMargin;

        public ReplyViewHolder(@NonNull View itemView) {
            super(itemView);
            //UI 객체 찾기
            tvReplyContent = itemView.findViewById(R.id.tvReplyContent);
            tvReplyID = itemView.findViewById(R.id.tvReplyID);
            tvReplyDate = itemView.findViewById(R.id.tvReplyDate);
            tvReply= itemView.findViewById(R.id.tvReply);
            topMargin= itemView.findViewById(R.id.topMargin);
            leftMargin=itemView.findViewById(R.id.leftMargin);



            //변경된 부분 2
            tvReply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (listener != null) {

                        //listener는 현재의 Adpapter 객체를 의미한다.
                        //그러므로 아래의 코드는 현재 Adapter객체의 onItemClick()메서드를 호출한다.
                        //현재의 게시글 위치 전송
                        listener.onItemClick(ReplyViewHolder.this, view, position);

                    }
                }
            });




        }
    }
}
