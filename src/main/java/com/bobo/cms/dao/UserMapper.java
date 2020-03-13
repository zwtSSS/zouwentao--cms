package com.bobo.cms.dao;

import java.util.List;

import org.apache.ibatis.annotations.Select;

import com.bobo.cms.domain.User;

public interface UserMapper {
	
	/**
	 * 
	 * @Title: selectByName 
	 * @Description: TODO
	 * @param name
	 * @return
	 * @return: User
	 */
	User selectByName(String name);

	/**
	 * 
	 * @Title: selects 
	 * @Description: 用户列表
	 * @param user
	 * @return
	 * @return: List<User>
	 */
	List<User> selects(User user);
	/**
	 * 
	 * @Title: update 
	 * @Description:修改
	 * @param user
	 * @return
	 * @return: int
	 */
	int update(User user);
	
	/**
	 * 
	 * @Title: insert 
	 * @Description: TODO
	 * @param user
	 * @return
	 * @return: int
	 */
	int insert(User user);
	
	/**
	 * @Title: selectIdList   
	 * @Description: 查询头像和昵称不为空的用户Id   
	 * @param: @return      
	 * @return: List<Integer>      
	 * @throws
	 */
	@Select("SELECT id FROM cms_user WHERE nickname IS NOT NULL")
	List<Integer> selectIdList();
	
	
}
