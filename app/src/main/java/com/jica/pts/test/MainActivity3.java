package com.jica.pts.test;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.jica.pts.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MainActivity3 extends AppCompatActivity {
    private RecyclerView recyclerView;
    private PhotoAdapter adapter;
    private List<Uri> photoUris = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PhotoAdapter(photoUris);
        recyclerView.setAdapter(adapter);

        // Firebase Storage에서 파일 목록을 불러오는 메서드 호출
        loadPhotosFromFirebaseStorage();
    }

    private void loadPhotosFromFirebaseStorage() {
        // Firebase Storage 초기화
        FirebaseStorage storage = FirebaseStorage.getInstance();

        // Firebase Storage의 "images/" 경로에 있는 파일 목록을 가져옵니다.
        storage.getReference().child("images/999").listAll().addOnCompleteListener(new OnCompleteListener<ListResult>() {
            @Override
            public void onComplete(@NonNull Task<ListResult> task) {
                if (task.isSuccessful()) {
                    // 가져온 파일 목록을 순회하면서 URI를 추출하여 리스트에 추가합니다.
                    for (com.google.firebase.storage.StorageReference item : task.getResult().getItems()) {
                        item.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                // URI를 성공적으로 가져왔을 때 처리
                                photoUris.add(uri);
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


    private static class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {
        private List<Uri> photoUris;

        public PhotoAdapter(List<Uri> photoUris) {
            this.photoUris = photoUris;
        }

        @NonNull
        @Override
        public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            View itemView = LayoutInflater.from(context).inflate(R.layout.photo_item, parent, false);
            return new PhotoViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
            Uri photoUri = photoUris.get(position);

            // Picasso 또는 Glide와 같은 이미지 로딩 라이브러리를 사용하여 이미지를 표시할 수 있습니다.
            Picasso.get().load(photoUri).into(holder.photoImageView);

          /*  // 파일 이름 표시
            String photoFileName = "Photo " + (position + 1);
            holder.removeImageView.setVisibility(View.GONE);*/

        }

        @Override
        public int getItemCount() {
            return photoUris.size();
        }

        public class PhotoViewHolder extends RecyclerView.ViewHolder {
            ImageView photoImageView;
            ImageView removeImageView;

            public PhotoViewHolder(@NonNull View itemView) {
                super(itemView);
                photoImageView = itemView.findViewById(R.id.photoImageView);

                removeImageView = itemView.findViewById(R.id.removeImageView);
            }
        }
    }
}
