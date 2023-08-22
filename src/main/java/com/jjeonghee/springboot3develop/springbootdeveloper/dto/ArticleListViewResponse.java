package com.jjeonghee.springboot3develop.springbootdeveloper.dto;

import com.jjeonghee.springboot3develop.springbootdeveloper.domain.Article;
import lombok.Getter;

@Getter
public class ArticleListViewResponse {
    private final Long id;
    private final String title;
    private final String content;

    public ArticleListViewResponse(Long id, String title, String content) {
        this.id = id;
        this.title = title;
        this.content = content;
    }
    public ArticleListViewResponse(Article article){
        id = article.getId();
        title = article.getTitle();
        content = article.getContent();
    }
}
