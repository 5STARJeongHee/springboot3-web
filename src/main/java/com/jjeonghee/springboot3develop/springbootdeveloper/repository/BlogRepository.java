package com.jjeonghee.springboot3develop.springbootdeveloper.repository;

import com.jjeonghee.springboot3develop.springbootdeveloper.domain.Article;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlogRepository extends JpaRepository<Article, Long> {

}
