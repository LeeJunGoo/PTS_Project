package com.jica.pts.Community_Fragmenet;

import android.view.View;

//class가 아닌 interface로 생성했다.
public interface OnMainBoardClickListeners {

    public void onItemClick(MainBoardAdapter.MainBoardViewHolder viewHolder, View view, int position);

}
