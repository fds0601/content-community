package com.example.community.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.community.common.Result;
import com.example.community.dto.ArticleDTO;
import com.example.community.dto.CommentDTO;
import com.example.community.entity.Article;
import com.example.community.service.ArticleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "文章接口")
@RestController
@RequestMapping("/article")
public class ArticleController {

    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @Operation(summary = "发布文章")
    @PostMapping
    public Result<Void> publish(@Valid @RequestBody ArticleDTO dto) {
        articleService.publish(dto);
        return Result.success();
    }

    @Operation(summary = "文章详情（含评论，带Redis缓存）")
    @GetMapping("/{id}")
    public Result<Map<String, Object>> getById(@PathVariable Long id) {
        return Result.success(articleService.getById(id));
    }

    @Operation(summary = "文章分页列表")
    @GetMapping("/list")
    public Result<Page<Article>> list(@RequestParam(defaultValue = "1") int page,
                                      @RequestParam(defaultValue = "10") int size,
                                      @RequestParam(required = false) String category) {
        return Result.success(articleService.list(page, size, category));
    }

    @Operation(summary = "修改文章")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody ArticleDTO dto) {
        articleService.update(id, dto);
        return Result.success();
    }

    @Operation(summary = "删除文章")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        articleService.delete(id);
        return Result.success();
    }

    @Operation(summary = "点赞文章")
    @PostMapping("/{id}/like")
    public Result<String> like(@PathVariable Long id) {
        return Result.success(articleService.like(id));
    }

    @Operation(summary = "发表评论")
    @PostMapping("/{id}/comment")
    public Result<Void> comment(@PathVariable Long id, @RequestBody CommentDTO dto) {
        articleService.comment(id, dto);
        return Result.success();
    }
}
