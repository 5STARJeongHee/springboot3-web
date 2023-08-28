package com.jjeonghee.springboot3develop.springbootdeveloper.config.error;

public class ArticleNotFoundException extends NotFoundException{
    public ArticleNotFoundException() {
        super(ErrorCode.ARTICLE_NOT_FOUND);
    }
}
