package com.jjeonghee.springboot3develop.springbootdeveloper.controller;

import com.jjeonghee.springboot3develop.springbootdeveloper.domain.Article;
import com.jjeonghee.springboot3develop.springbootdeveloper.domain.User;
import com.jjeonghee.springboot3develop.springbootdeveloper.dto.AddArticleRequest;
import com.jjeonghee.springboot3develop.springbootdeveloper.dto.UpdateArticleRequest;
import com.jjeonghee.springboot3develop.springbootdeveloper.repository.BlogRepository;
import com.jjeonghee.springboot3develop.springbootdeveloper.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class BlogControllerTest extends ControllerTestHelper {
    @Autowired
    BlogRepository blogRepository;

    @Autowired
    UserRepository userRepository;

    User user;

    @BeforeEach
    public void mockMvcSetUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        blogRepository.deleteAll();
    }

    @BeforeEach
    void setSecurityContext() {
        userRepository.deleteAll();
        user = userRepository.save(User.builder()
                .email("user@gmail.com")
                .password("test")
                .build()
        );
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities()));
    }

    @DisplayName("addArticle: 블로그 글 추가 성공 시나리오")
    @Test
    public void addArticle() throws Exception {
        final String url = "/api/articles";
        final String title = "title";
        final String content = "content";

        final AddArticleRequest userRequest = new AddArticleRequest(title, content);

        final String requestBody = objectMapper.writeValueAsString(userRequest);

        Principal principal = Mockito.mock(Principal.class);
        Mockito.when(principal.getName()).thenReturn("username");

        ResultActions result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .principal(principal)
                .content(requestBody)
        );

        result.andExpect(status().isCreated());

        List<Article> articles = blogRepository.findAll();

        assertThat(articles.size()).isEqualTo(1);
        assertThat(articles.get(0).getTitle()).isEqualTo(title);
        assertThat(articles.get(0).getContent()).isEqualTo(content);

    }

    @DisplayName("findAllArticles: 블로그 글 목록 조회에 성공한다")
    @Test
    public void findAllArticles() throws Exception {

        final String url = "/api/articles";
        Article savedArticle = createDefaultArticle();

        Principal principal = Mockito.mock(Principal.class);
        Mockito.when(principal.getName()).thenReturn("username");

        final ResultActions resultActions = mockMvc.perform(get(url).accept(MediaType.APPLICATION_JSON));

        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value(savedArticle.getContent()))
                .andExpect(jsonPath("$[0].title").value(savedArticle.getTitle()));

    }

    @DisplayName("findArticle: 블로그 글 단일 조회에 성공한다")
    @Test
    public void findArticle() throws Exception {

        final String url = "/api/articles/{id}";

        Article savedArticle = createDefaultArticle();

        final ResultActions resultActions = mockMvc.perform(get(url, savedArticle.getId()).accept(MediaType.APPLICATION_JSON));

        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value(savedArticle.getContent()))
                .andExpect(jsonPath("$.title").value(savedArticle.getTitle()));

    }
    private Article createDefaultArticle() {
        final String title = "title";
        final String content = "content";

        return blogRepository.save(Article.builder()
                .title(title)
                .author(user.getUsername())
                .content(content)
                .build());
    }

    @DisplayName("deleteArticle: 블로그 글 삭제에 성공한다")
    @Test
    public void deleteArticle() throws Exception {

        final String url = "/api/articles/{id}";

        Article savedArticle = createDefaultArticle();

        mockMvc.perform(delete(url, savedArticle.getId()).accept(MediaType.APPLICATION_JSON));

        List<Article> articles = blogRepository.findAll();
        assertThat(articles).isEmpty();
    }

    @DisplayName("updateArticle: 블로그 글 수정에 성공한다")
    @Test
    public void updateArticle() throws Exception {

        final String url = "/api/articles/{id}";
        final String title = "title";
        final String content = "content";

        Article savedArticle = createDefaultArticle();

        final String updatedTitle = "new Title";
        final String updatedContent = "new Content";

        UpdateArticleRequest request =  new UpdateArticleRequest(updatedTitle, updatedContent);

        final ResultActions resultActions = mockMvc.perform(
                put(url, savedArticle.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        );

        resultActions.andExpect(status().isOk());

        Article article = blogRepository.findById(savedArticle.getId()).get();

        assertThat(article.getTitle()).isEqualTo(updatedTitle);
        assertThat(article.getContent()).isEqualTo(updatedContent);

        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value(updatedContent))
                .andExpect(jsonPath("$.title").value(updatedTitle));

    }
}