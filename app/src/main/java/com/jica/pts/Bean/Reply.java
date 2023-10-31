package com.jica.pts.Bean;

import com.google.firebase.Timestamp;

// Bean 객체를 사용하지 않고 선언할 경우
// 기본값으로 String null, int는 0, boolean false로 db에 저장된다.
public class Reply {
    private String user_id;
    private String reply_content;
    private Timestamp reply_date;

    private int reply_number;  //댓글 번호
    private int reply_revel;   //댓글 레벨 1,2,3
    private String reply_check_revel;  // 댓글 핵심 부분이다.!!
                                       // 형식: "댓글시간" "-" "대댓글 시간" (날짜를 문자 형식으로 받아온다.)
                                       // reply_number 및 날짜로 정렬을 안하는 이유는 댓글 및 대댓글 정렬 기준으로 맞지 않다
                                       // 그래서 (정렬 조건)을 reply_check_revel로 한다.


    public Reply() {
    }

    public Reply(String user_id, String reply_content, Timestamp reply_date, int reply_number, int reply_revel, String reply_check_revel) {
        this.user_id = user_id;
        this.reply_content = reply_content;
        this.reply_date = reply_date;
        this.reply_number = reply_number;
        this.reply_revel = reply_revel;
        this.reply_check_revel = reply_check_revel;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getReply_content() {
        return reply_content;
    }

    public void setReply_content(String reply_content) {
        this.reply_content = reply_content;
    }

    public Timestamp getReply_date() {
        return reply_date;
    }

    public void setReply_date(Timestamp reply_date) {
        this.reply_date = reply_date;
    }

    public int getReply_number() {
        return reply_number;
    }

    public void setReply_number(int reply_number) {
        this.reply_number = reply_number;
    }

    public int getReply_revel() {
        return reply_revel;
    }

    public void setReply_revel(int reply_revel) {
        this.reply_revel = reply_revel;
    }

    public String getReply_check_revel() {
        return reply_check_revel;
    }

    public void setReply_check_revel(String reply_check_revel) {
        this.reply_check_revel = reply_check_revel;
    }

    @Override
    public String toString() {
        return "Reply{" +
                "user_id='" + user_id + '\'' +
                ", reply_content='" + reply_content + '\'' +
                ", reply_date=" + reply_date +
                ", reply_number=" + reply_number +
                ", reply_revel=" + reply_revel +
                ", reply_check_revel='" + reply_check_revel + '\'' +
                '}';
    }
}


