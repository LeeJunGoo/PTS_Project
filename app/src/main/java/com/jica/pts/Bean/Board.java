package com.jica.pts.Bean;


import com.google.firebase.Timestamp;
import com.jica.pts.test.SubItem;

import java.util.Collection;
import java.util.List;

public class Board {
    private int board_number;
    private String board_name;
    private String board_title;
    private String board_content;
    private String board_tag;
    private String user_id;
    private boolean board_del;
    private String board_photo1;
    private String board_photo2;
    private String board_photo3;

    private String board_photo4;

    private String board_photo5;
    private Timestamp board_date;
    private Timestamp board_del_date;
    private int board_great;
    private String board_thumbnail;

    private String itemTitle;





    public Board() {
    }

    public Board(int board_number, String board_name, String board_title, String board_content, String board_tag, String user_id, String board_photo, Timestamp board_date, int board_great) {
        this.board_number = board_number;
        this.board_name = board_name;
        this.board_title = board_title;
        this.board_content = board_content;
        this.board_tag = board_tag;
        this.user_id = user_id;
        this.board_photo1 = board_photo;
        this.board_date = board_date;
        this.board_great = board_great;
    }

    public boolean isBoard_del() {
        return board_del;
    }

    public void setBoard_del(boolean board_del) {
        this.board_del = board_del;
    }

    public Timestamp getBoard_del_date() {
        return board_del_date;
    }

    public void setBoard_del_date(Timestamp board_del_date) {
        this.board_del_date = board_del_date;
    }

    public String getBoard_photo2() {
        return board_photo2;
    }

    public void setBoard_photo2(String board_photo2) {
        this.board_photo2 = board_photo2;
    }

    public String getBoard_photo3() {
        return board_photo3;
    }

    public void setBoard_photo3(String board_photo3) {
        this.board_photo3 = board_photo3;
    }

    public String getBoard_photo4() {
        return board_photo4;
    }

    public void setBoard_photo4(String board_photo4) {
        this.board_photo4 = board_photo4;
    }

    public String getBoard_photo5() {
        return board_photo5;
    }

    public void setBoard_photo5(String board_photo5) {
        this.board_photo5 = board_photo5;
    }

    public String getItemTitle() {
        return itemTitle;
    }

    public void setItemTitle(String itemTitle) {
        this.itemTitle = itemTitle;
    }


    public String getBoard_photo1() {
        return board_photo1;
    }

    public void setBoard_photo1(String board_photo) {
        this.board_photo1 = board_photo;
    }

    public int getBoard_number() {
        return board_number;
    }

    public void setBoard_number(int board_number) {
        this.board_number = board_number;
    }

    public String getBoard_name() {
        return board_name;
    }

    public void setBoard_name(String board_name) {
        this.board_name = board_name;
    }

    public String getBoard_title() {
        return board_title;
    }

    public void setBoard_title(String board_title) {
        this.board_title = board_title;
    }

    public String getBoard_content() {
        return board_content;
    }

    public void setBoard_content(String board_content) {
        this.board_content = board_content;
    }

    public String getBoard_tag() {
        return board_tag;
    }

    public void setBoard_tag(String board_tag) {
        this.board_tag = board_tag;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public Timestamp getBoard_date() {
        return board_date;
    }

    public void setBoard_date(Timestamp board_date) {
        this.board_date = board_date;
    }

    public int getBoard_great() {
        return board_great;
    }

    public void setBoard_great(int board_great) {
        this.board_great = board_great;
    }

    public String getBoard_thumbnail() {
        return board_thumbnail;
    }

    public void setBoard_thumbnail(String board_thumbnail) {
        this.board_thumbnail = board_thumbnail;
    }

    @Override
    public String toString() {
        return "Board{" +
                "board_number=" + board_number +
                ", board_name='" + board_name + '\'' +
                ", board_title='" + board_title + '\'' +
                ", board_content='" + board_content + '\'' +
                ", board_tag='" + board_tag + '\'' +
                ", user_id='" + user_id + '\'' +
                ", board_date=" + board_date +
                ", board_great=" + board_great +
                '}';
    }


}
