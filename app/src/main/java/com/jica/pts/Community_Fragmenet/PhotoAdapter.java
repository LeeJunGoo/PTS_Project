package com.jica.pts.Community_Fragmenet;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.jica.pts.R;

import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {
    private List<Uri> photoUris;
    private TextView selectedImageCountTextView; // 이미지 개수를 나타내는 TextView
    int maxSelectableCount = 5;

    public PhotoAdapter(List<Uri> photoUris, TextView selectedImageCountTextView) {
        this.photoUris = photoUris;
        this.selectedImageCountTextView = selectedImageCountTextView;
    }

    // 이미지 개수를 업데이트하는 메서드
    private void updatePhotoCountText() {
        int photoCount = photoUris.size();
        selectedImageCountTextView.setText(String.valueOf(photoCount) + "/" + maxSelectableCount);

    }


    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View photoView = inflater.inflate(R.layout.photo_item, parent, false);
        return new PhotoViewHolder(photoView);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        final Uri photoUri = photoUris.get(position);

        // 이미지뷰에 이미지 설정
        holder.photoImageView.setImageURI(photoUri);

        // "X" 버튼 클릭 이벤트 처리
        holder.removeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 선택한 이미지 목록에서 해당 이미지 제거
                int positionToRemove = photoUris.indexOf(photoUri);
                if (positionToRemove != -1) {
                    photoUris.remove(positionToRemove);
                    notifyItemRemoved(positionToRemove);
                    updatePhotoCountText(); // 이미지 개수 업데이트

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return photoUris.size();
    }

    public class PhotoViewHolder extends RecyclerView.ViewHolder {

        //UI 객체 선언
        ImageView photoImageView;
        ImageView removeImageView;

        public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);

            //UI 객체 찾기
            photoImageView = itemView.findViewById(R.id.photoImageView);
            removeImageView = itemView.findViewById(R.id.removeImageView);

        }
    }
}
