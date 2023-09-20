package com.jica.pts.Community_Fragmenet;

import android.view.View;

import com.jica.pts.Community_Fragmenet.BoardAdapter;

//class가 아닌 interface로 생성했다.
public interface OnPersonItemClickListener {

    public void onItemClick(BoardAdapter.BoardViewHolder viewHolder, View view, int position);

}
