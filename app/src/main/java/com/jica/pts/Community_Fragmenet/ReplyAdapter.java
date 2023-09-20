package com.jica.pts.Community_Fragmenet;


import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.jica.pts.Bean.Reply;
import com.jica.pts.R;

import java.util.ArrayList;
import java.util.Date;

public class ReplyAdapter extends RecyclerView.Adapter<ReplyAdapter.ReplyViewHolder>{

    ArrayList<Reply> replyArrayList;
    Context context;


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

    }


    // Count 필수!!1
    @Override
    public int getItemCount() {
        return (replyArrayList != null ? replyArrayList.size() : 0);
    }


    public class ReplyViewHolder extends RecyclerView.ViewHolder {
        TextView tvReplyContent, tvReplyID, tvReplyDate;

        public ReplyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvReplyContent = itemView.findViewById(R.id.tvReplyContent);
            tvReplyID = itemView.findViewById(R.id.tvReplyID);
            tvReplyDate = itemView.findViewById(R.id.tvReplyDate);



        }
    }
}
