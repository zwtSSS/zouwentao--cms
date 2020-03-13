package com.bobo.cms.dao;

import java.util.List;

import org.apache.ibatis.annotations.Select;

import com.bobo.cms.domain.Category;
import com.bobo.cms.domain.Channel;

public interface ChannelMapper {
	/**
	 * 
	 * @Title: select 
	 * @Description: 根据id返回栏目对象
	 * @param id
	 * @return
	 * @return: Channel
	 */
	Channel select(Integer id);
	
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
	
	@Select("SELECT id FROM cms_channel")
	List<Integer> selectChannelIdList();
	

	@Select("SELECT id FROM cms_category where channel_id=#{channelId}")
	List<Integer> selectCateIdList(Integer channelId);

}
