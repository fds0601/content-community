package com.example.community.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.community.dto.ArticleDTO;
import com.example.community.entity.Article;
import com.example.community.entity.ArticleLike;
import com.example.community.entity.Comment;
import com.example.community.mapper.ArticleLikeMapper;
import com.example.community.mapper.ArticleMapper;
import com.example.community.mapper.CommentMapper;
import com.example.community.util.UserContext;
import com.example.community.dto.CommentDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;


import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Service
public class ArticleService {

    private static final Logger log = LoggerFactory.getLogger(ArticleService.class);
    private final ArticleMapper articleMapper;
    private final CommentMapper commentMapper;
    private final ArticleLikeMapper articleLikeMapper;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String CACHE_PREFIX = "article:";

    public ArticleService(ArticleMapper articleMapper, CommentMapper commentMapper,
                          ArticleLikeMapper articleLikeMapper, StringRedisTemplate redisTemplate) {
        this.articleMapper = articleMapper;
        this.commentMapper = commentMapper;
        this.articleLikeMapper = articleLikeMapper;
        this.redisTemplate = redisTemplate;
    }

    public void publish(ArticleDTO dto) {
        Article article = new Article();
        article.setTitle(dto.getTitle());
        article.setSummary(dto.getSummary());
        article.setContent(dto.getContent());
        article.setCategory(dto.getCategory());
        article.setUserId(UserContext.getUserId());
        article.setViewCount(0);
        article.setLikeCount(0);
        article.setStatus(1);
        articleMapper.insert(article);
    }

    public Map<String, Object> getById(Long id) {
        String cacheKey = CACHE_PREFIX + id;
        Article article = null;

        try {
            String json = redisTemplate.opsForValue().get(cacheKey);
            if (json != null) {
                article = objectMapper.readValue(json, Article.class);
                log.info("命中Redis缓存: articleId={}", id);
            }
        } catch (Exception e) {
            log.warn("Redis缓存读取失败，降级查数据库", e);
        }

        if (article == null) {
            article = articleMapper.selectById(id);
            if (article == null) throw new RuntimeException("文章不存在");
            try {
                redisTemplate.opsForValue().set(cacheKey,
                        objectMapper.writeValueAsString(article), Duration.ofMinutes(10));
            } catch (Exception e) {
                log.warn("Redis缓存写入失败", e);
            }
        }

        articleMapper.update(null,
                Wrappers.<Article>lambdaUpdate()
                        .eq(Article::getId, id)
                        .setSql("view_count = view_count + 1"));
        article.setViewCount(article.getViewCount() + 1);

        Map<String, Object> result = new HashMap<>();
        result.put("article", article);
        result.put("comments", commentMapper.selectList(
                new LambdaQueryWrapper<Comment>().eq(Comment::getArticleId, id)));
        return result;
    }

    public Page<Article> list(int page, int size, String category) {
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Article::getStatus, 1);
        if (category != null && !category.isEmpty()) {
            wrapper.eq(Article::getCategory, category);
        }
        wrapper.orderByDesc(Article::getCreateTime);
        return articleMapper.selectPage(new Page<>(page, size), wrapper);
    }

    public void update(Long id, ArticleDTO dto) {
        Article article = articleMapper.selectById(id);
        if (article == null) throw new RuntimeException("文章不存在");
        if (!article.getUserId().equals(UserContext.getUserId())) {
            throw new RuntimeException("只能修改自己的文章");
        }
        article.setTitle(dto.getTitle());
        article.setSummary(dto.getSummary());
        article.setContent(dto.getContent());
        article.setCategory(dto.getCategory());
        articleMapper.updateById(article);
        redisTemplate.delete(CACHE_PREFIX + id);
    }

    public void delete(Long id) {
        Article article = articleMapper.selectById(id);
        if (article == null) throw new RuntimeException("文章不存在");
        if (!article.getUserId().equals(UserContext.getUserId())
                && !"ADMIN".equals(UserContext.getRole())) {
            throw new RuntimeException("无权限删除");
        }
        articleMapper.deleteById(id);
        redisTemplate.delete(CACHE_PREFIX + id);
    }

    public String like(Long id) {
        Long userId = UserContext.getUserId();
        Long exists = articleLikeMapper.selectCount(
                new LambdaQueryWrapper<ArticleLike>()
                        .eq(ArticleLike::getArticleId, id)
                        .eq(ArticleLike::getUserId, userId));
        if (exists > 0) {
            throw new RuntimeException("已经点过赞了");
        }
        ArticleLike like = new ArticleLike();
        like.setArticleId(id);
        like.setUserId(userId);
        articleLikeMapper.insert(like);
        articleMapper.update(null,
                Wrappers.<Article>lambdaUpdate()
                        .eq(Article::getId, id)
                        .setSql("like_count = like_count + 1"));
        redisTemplate.delete(CACHE_PREFIX + id);
        return "点赞成功";
    }

    public void comment(Long id, CommentDTO dto) {
        Comment comment = new Comment();
        comment.setArticleId(id);
        comment.setUserId(UserContext.getUserId());
        comment.setContent(dto.getContent());
        commentMapper.insert(comment);
    }
}
