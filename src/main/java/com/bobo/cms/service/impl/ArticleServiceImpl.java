package com.bobo.cms.service.impl;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.bobo.cms.dao.ArticleMapper;
import com.bobo.cms.domain.Article;
import com.bobo.cms.respository.ArticleRepositroy;
import com.bobo.cms.service.ArticleService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

@Service
public class ArticleServiceImpl implements ArticleService {
	@Resource
	private ArticleMapper articleMapper;
	
	@SuppressWarnings("rawtypes")
	@Autowired
	private RedisTemplate redisTemplate;
	@Autowired
	private ArticleRepositroy articleRepositroy;

	@SuppressWarnings("unchecked")
	@Override
	public PageInfo<Article> selects(Article article, Integer page, Integer pageSize) {
		List<Article> list = null;
		/** 只缓存第一页 **/
		if(page!=1) {
			PageHelper.startPage(page, pageSize);
			list = articleMapper.selects(article);
			return new PageInfo<Article>(list);
		}
		/** 设置缓存的Key **/
		String cacheKey = "hotlist:"+page;
		/** redis是否已缓存了数据 **/
		list = (List<Article>)redisTemplate.opsForValue().get(cacheKey);
		/** 如果已换成数据，则读redis数据直接返回 **/
		if(list!=null && list.size()!=0) {
			System.out.println("从缓存获取热点数据成功");
			return new PageInfo<Article>(list);
		}
		/** 如果未换成数据，则查询数据库，并换成到redis，设置缓存时间 **/
		PageHelper.startPage(page, pageSize);
		list = articleMapper.selects(article);
		/** 设置缓存 **/
		redisTemplate.opsForValue().set(cacheKey, list);
		redisTemplate.expire(cacheKey, 10, TimeUnit.SECONDS);
		System.out.println("设置缓存数据成功");
		
		return new PageInfo<Article>(list);
	}

	@Override
	public boolean update(Article article) {
		// TODO Auto-generated method stub
		boolean result = articleMapper.update(article) >0;
		/** 查询文章，同步到索引库 **/
		article = select(article.getId());
		/** 审核通过，未删除的文章同步到索引库 **/
		if(article.getStatus()==1 && article.getDeleted()==0) {
			articleRepositroy.save(article);
		}else {
			/** 否则从索引库删除 **/
			articleRepositroy.delete(article);
		}
		return result;
	}

	@Override
	public Article select(Integer id) {
		// TODO Auto-generated method stub
		return articleMapper.select(id);
	}

	@Override
	public boolean insert(Article article) {
		// TODO Auto-generated method stub
		return articleMapper.insert(article)>0;
	}

}
