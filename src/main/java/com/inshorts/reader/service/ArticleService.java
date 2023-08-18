package com.inshorts.reader.service;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.server.ResponseStatusException;

import com.inshorts.reader.entity.ArticleEntity;
import com.inshorts.reader.repository.ArticleRepository;

@Service
public class ArticleService {

    @Autowired
    private ArticleRepository articleRepository;

    public List<ArticleEntity> getAllArticles()
    {
        return articleRepository.findAll();
    }

    public void createArticle(ArticleEntity articleEntity)
    {
        articleEntity.setDate(LocalDate.now());
        articleRepository.saveAndFlush(articleEntity);
    }

    public ArticleEntity getArticle(Integer id)
    {
        return articleRepository.findById(id).orElseThrow(() -> {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Id not found.");
        });
    }

    public void updateArticle(Integer id, ArticleEntity articleEntity)
    {
    	ArticleEntity existingArticleEntity = articleRepository.findById(id).orElseThrow(() -> {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Articl Id %s not found.", id));
        });
    	updateExistingArticle(articleEntity, existingArticleEntity);
        articleRepository.saveAndFlush(existingArticleEntity);
    }

	public void updateArticle(Integer id, Map<String, Object> request)
    {
        ArticleEntity articleEntity = articleRepository.findById(id).orElseThrow(() -> {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Id not found.");
        });

        request.forEach((k,v) -> {

            Field field = ReflectionUtils.findField(ArticleEntity.class, k);
            field.setAccessible(true);
            if(field.getType().equals(LocalDate.class)) {
            	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate date = LocalDate.parse((String)v, formatter);
            	ReflectionUtils.setField(field, articleEntity, date);
            }else {
            	ReflectionUtils.setField(field, articleEntity, v);
            }

        });
        articleRepository.saveAndFlush(articleEntity);
    }

    public List<ArticleEntity> getAllArticles(Integer pageNo, Integer size) {

        if (Objects.isNull(pageNo) && Objects.isNull(size))
        {
            return articleRepository.findAll();
        }

        Pageable pageable = PageRequest.of(pageNo, size, Sort.by("date").descending());
        Page<ArticleEntity> page = articleRepository.findAll(pageable);
        return page.stream().collect(Collectors.toList());
    }

	private void updateExistingArticle(ArticleEntity articleEntity, ArticleEntity existingArticleEntity) {
		if(articleEntity != null) {
    		if(!isEmpty(articleEntity.getTitle())) {
    			existingArticleEntity.setTitle(articleEntity.getTitle());
    		}
			if(!isEmpty(articleEntity.getDescription())) {
				existingArticleEntity.setDescription(articleEntity.getDescription());		
    		}
			if(!isEmpty(articleEntity.getAddedBy())) {
				existingArticleEntity.setAddedBy(articleEntity.getAddedBy());
			}
			if(articleEntity.getDate() != null) {
				existingArticleEntity.setDate(articleEntity.getDate());
			}
    	}
	}

    private boolean isEmpty(String data) {
		return data == null || data.isEmpty();
	}
}
