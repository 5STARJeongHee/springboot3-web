package com.jjeonghee.springboot3develop.springbootdeveloper.dto;

import com.jjeonghee.springboot3develop.springbootdeveloper.domain.Article;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AddArticleRequest {
    @NotNull
    @Size(min=1,max=10)
    private String title;
    @NotNull
    private String content;

    public Article toEntity(String author) {
        return Article.builder()
                .author(author)
                .title(title)
                .content(content)
                .build();
    }
}
