package com.bobo.cms.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.bobo.cms.domain.Article;
import com.bobo.cms.service.ArticleService;

/**
 * 保存文章Article到mysql数据库
 */
@Component
@EnableKafka
public class ArticleConsumer implements MessageListener<String, String>{
	
	@Autowired
	ArticleService articleService;

	@Override
	@KafkaListener(topics= {"boboArticleTopic"})
	public void onMessage(ConsumerRecord<String, String> consumerRecord) {
		/** 接收内容 **/
		String articleJsonStr = consumerRecord.value();
		/** json字符串转Article对象 **/
		Article article = JSON.parseObject(articleJsonStr, Article.class);
		/** 保存到mysql的article表 **/
		articleService.insert(article);
	}
	
}
