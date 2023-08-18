package com.inshorts.reader.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.inshorts.reader.entity.ArticleEntity;
import com.inshorts.reader.service.ArticleService;

@RestController
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @PostMapping("/article")
    public ResponseEntity<String> createArticle(@RequestBody ArticleEntity articleEntity)
    {
        articleService.createArticle(articleEntity);
        return ResponseEntity.status(HttpStatus.CREATED).body("New Article Created");
    }

    @GetMapping("/article/{id}")
    public ResponseEntity<ArticleEntity> getArticle(@PathVariable Integer id)
    {
        return ResponseEntity.ok(articleService.getArticle(id));
    }

    @PutMapping("/article/{id}")
    public ResponseEntity<String> updateArticle(@PathVariable Integer id, @RequestBody ArticleEntity articleEntity)
    {
        articleService.updateArticle(id, articleEntity);
        return ResponseEntity.ok(String.format("Article %s has been updated.", id));
    }

    @PatchMapping("/article/{id}")
    public ResponseEntity<String> updateArticle(@PathVariable Integer id, @RequestBody Map<String, Object> request)
    {
        articleService.updateArticle(id, request);
        return ResponseEntity.ok(String.format("Article %s has been updated.", id));
    }

    @GetMapping("/article")
    public ResponseEntity<List<ArticleEntity>> getAllArticles(@RequestParam(name = "pageNo", required = false) Integer pageNo,@RequestParam(name = "size", required = false) Integer size)
    {
        return ResponseEntity.ok(articleService.getAllArticles(pageNo, size));
    }
}