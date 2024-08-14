package com.project.bms.model;

public class CommentDTO {
    private Long bookid;
    private Long userid;
    private String content;

    public Long getBookid() {
        return bookid;
    }

    public void setBookid(Long bookid) {
        this.bookid = bookid;
    }

    public Long getUserid() {
        return userid;
    }

    public void setUserid(Long userid) {
        this.userid = userid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public CommentDTO(Long bookid, Long userid, String content) {
        this.bookid = bookid;
        this.userid = userid;
        this.content = content;
    }

    public CommentDTO() {
    }

}
