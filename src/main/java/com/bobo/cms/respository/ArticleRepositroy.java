package com.bobo.cms.respository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.bobo.cms.domain.Article;

public interface ArticleRepositroy extends ElasticsearchRepository<Article, Integer>{

}
