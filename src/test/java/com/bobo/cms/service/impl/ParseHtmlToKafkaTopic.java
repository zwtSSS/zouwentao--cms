package com.bobo.cms.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alibaba.fastjson.JSON;
import com.bobo.cms.domain.Article;
import com.bobo.cms.service.ChannelService;
import com.bobo.cms.service.UserService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath:/spring-beans.xml")
public class ParseHtmlToKafkaTopic {

	@Autowired
	private UserService userService;
	@Autowired
	private ChannelService channelService;
	
	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;
	
	/** 解析html，封装成Article对象，发送到kafka的topic 
	 * @throws IOException **/
	@Test
	public void parseHtmlToKafkaTopic() throws IOException {
		/** 读取文件 **/
		File htmlFileDir = new File("D:\\html\\");
		File[] listFiles = htmlFileDir.listFiles();
		for (File htmlFile : listFiles) {
			Article article = new Article();
			/** Jsoup解析html，获取标题、文章内容、图片 **/
			Document htmlPage = Jsoup.parse(htmlFile, "utf-8");
			/** 解析title **/
			Elements titleE = htmlPage.select(".post_content_main h1");
			String title = titleE.text();
			article.setTitle(title);
			/** 解析content **/
			Elements contentE = htmlPage.select(".post_text");
			String content = contentE.html();
			article.setContent(content);
			/** 解析图片 **/
			Elements imgE = contentE.select("img");
			if(imgE.size()>0) {
				String pic = imgE.get(0).attr("src");
				article.setPicture(pic);
			}else {
				continue;
			}
			/** 随机生成频道id、分类id、用户Id **/
			Integer channelId = channelService.getRandomChannelId();
			Integer cateId = channelService.getRandomCateId(channelId);
			Integer userId = userService.getRandomUserId();
			article.setChannelId(channelId);
			article.setCategoryId(cateId);
			article.setUserId(userId);
			/** 设置文章对象的默认值：状态、删除、创建时间、更新时间 **/
			article.setStatus(1);
			/** 设置默认值 **/
			article.setHits(0);
			article.setHot(0);
			article.setDeleted(0);
			article.setCreated(new Date());
			article.setUpdated(new Date());
			/** 把文章Article转Json字符串发送到kakfa的topic **/
			String articleJsonStr = JSON.toJSONString(article);
			kafkaTemplate.send("boboArticleTopic", articleJsonStr);
		}
		
	}
}
