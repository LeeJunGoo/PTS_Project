package com.jica.pts.test;
import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.jica.pts.R;

import java.util.ArrayList;
import java.util.List;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        // 상위 리사이클러뷰 설정
        RecyclerView rvItem = findViewById(R.id.rv_item);
        LinearLayoutManager layoutManager = new LinearLayoutManager(TestActivity.this);
        ItemAdapter itemAdapter = new ItemAdapter(buildItemList());
        rvItem.setAdapter(itemAdapter);
        rvItem.setLayoutManager(layoutManager);
    }

    // 상위아이템 큰박스 아이템을 10개 만듭니다.
    private List<Item> buildItemList() {
        List<Item> itemList = new ArrayList<>();
        for (int i=0; i<10; i++) {
            Item item = new Item("Item "+i, buildSubItemList());
            itemList.add(item);
        }
        return itemList;
    }
    // 그안에 존재하는 하위 아이템 박스(3개씩 보이는 아이템들)
    private List<SubItem> buildSubItemList() {
        List<SubItem> subItemList = new ArrayList<>();
        for (int i=0; i<3; i++) {
            SubItem subItem = new SubItem("Sub Item "+i, "Description "+i);
            subItemList.add(subItem);
        }
        return subItemList;
    }
}