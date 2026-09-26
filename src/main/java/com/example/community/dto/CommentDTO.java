package com.example.community.dto;

import jakarta.validation.constraints.NotBlank;

public class CommentDTO {
    @NotBlank(message = "评论不能为空")
    private String content;

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

}
