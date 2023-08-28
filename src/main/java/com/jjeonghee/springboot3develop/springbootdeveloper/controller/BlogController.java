package com.jjeonghee.springboot3develop.springbootdeveloper.controller;

import com.jjeonghee.springboot3develop.springbootdeveloper.domain.Article;
import com.jjeonghee.springboot3develop.springbootdeveloper.dto.AddArticleRequest;
import com.jjeonghee.springboot3develop.springbootdeveloper.dto.ArticleResponse;
import com.jjeonghee.springboot3develop.springbootdeveloper.dto.UpdateArticleRequest;
import com.jjeonghee.springboot3develop.springbootdeveloper.service.BlogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class BlogController {

    private final BlogService blogService;

    @PostMapping("/api/articles")
    public ResponseEntity<Article> addArticle(@RequestBody @Validated AddArticleRequest request, Principal principal){
        Article saveArticle = blogService.save(request, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(saveArticle);
    }

    @GetMapping("/api/articles")
    public ResponseEntity<List<ArticleResponse>> findAllArticles() {
        List<ArticleResponse> articles = blogService.findAll().stream()
                .map(e->new ArticleResponse(e.getTitle(),e.getContent()))
                .toList();
        return ResponseEntity.ok().body(articles);
    }

    @GetMapping("/api/articles/{id}")
    public ResponseEntity<ArticleResponse> findArticle(@PathVariable long id) {
        Article article = blogService.findById(id);

        return ResponseEntity.ok().body(new ArticleResponse(article));
    }

    @DeleteMapping("/api/articles/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable long id){
        blogService.delete(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/api/articles/{id}")
    public ResponseEntity<ArticleResponse> updateArticle(@PathVariable long id, @RequestBody UpdateArticleRequest request) {
        Article updatedArticle = blogService.update(id, request);
        return ResponseEntity.ok().body(new ArticleResponse(updatedArticle));
    }
}
