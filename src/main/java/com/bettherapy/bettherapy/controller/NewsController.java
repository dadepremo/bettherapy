package com.bettherapy.bettherapy.controller;


import com.bettherapy.bettherapy.model.entity.News;
import com.bettherapy.bettherapy.model.repository.NewsRepository;
import com.bettherapy.bettherapy.model.request.NewsRequest;
import com.bettherapy.bettherapy.model.response.NewsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/news")
public class NewsController {

    @Autowired
    private NewsRepository newsRepository;

    @GetMapping
    public List<NewsResponse> getVisibleNews() {
        return newsRepository.findByVisibleTrueAndExpiresAtAfterOrderByCreatedAtDesc(LocalDateTime.now())
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @PostMapping("/save")
    public ResponseEntity<NewsResponse> createNews(@RequestBody NewsRequest request) {
        News news = new News();
        news.setMessage(request.getMessage());
        news.setType(request.getType());
        news.setExpiresAt(request.getExpiresAt());
        news.setCreatedAt(LocalDateTime.now());
        news.setVisible(request.isVisible());
        News saved = newsRepository.save(news);
        return ResponseEntity.ok(toResponse(saved));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNews(@PathVariable Long id) {
        newsRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private NewsResponse toResponse(News news) {
        NewsResponse res = new NewsResponse();
        res.setId(news.getId());
        res.setMessage(news.getMessage());
        res.setCreatedAt(news.getCreatedAt());
        res.setExpiresAt(news.getExpiresAt());
        res.setVisible(news.isVisible());
        return res;
    }

}

