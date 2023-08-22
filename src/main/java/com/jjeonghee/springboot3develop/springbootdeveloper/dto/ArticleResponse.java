package com.jjeonghee.springboot3develop.springbootdeveloper.dto;

import com.jjeonghee.springboot3develop.springbootdeveloper.domain.Article;
import lombok.Getter;

@Getter
public class ArticleResponse {

    private final String title;
    private final String content;

    public ArticleResponse(String title, String content) {
        this.title = title;
        this.content = content;
    }
    public ArticleResponse(Article article){
        this.title = article.getTitle();
        this.content = article.getContent();
    }
}
