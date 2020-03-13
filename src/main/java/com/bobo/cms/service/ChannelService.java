package com.bobo.cms.service;

import java.util.List;

import com.bobo.cms.domain.Category;
import com.bobo.cms.domain.Channel;

public interface ChannelService {
	/**
	 * 
	 * @Title: selects 
	 * @Description: 栏目列表
	 * @return
	 * @return: List<Channel>
	 */
	List<Channel> selects();
	
	/**
	 * 
	 * @Title: selectsByCid 
	 * @Description: 根据栏目查询分类
	 * @param channelId
	 * @return
	 * @return: List<Category>
	 */
	List<Category> selectsByCid(Integer channelId);
	
	/**
	 * 获取随机频道Id
	 * @return
	 */
	Integer getRandomChannelId();
	/**
	 * 随机指定频道下的分类Id
	 * @param channelId
	 * @return
	 */
	Integer getRandomCateId(Integer channelId);
}
