package com.example.community.dto;

import jakarta.validation.constraints.NotBlank;

public class ArticleDTO {
    @NotBlank(message = "标题不能为空")
    private String title;
    private String summary;
    @NotBlank(message = "正文不能为空")
    private String content;
    private String category;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}
