package com.bobo.cms.util;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.GetQuery;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;

import com.github.pagehelper.PageInfo;
/**
 * Es工具类
 */
public class EsUtils {
	/**
	 * 保存及更新方法
	 * @param elasticsearchTemplate
	 * @param id
	 * @param object
	 */
	public static void saveObject(ElasticsearchTemplate elasticsearchTemplate, String id, Object object) {
		// 创建所以对象
		IndexQuery query = new IndexQueryBuilder().withId(id).withObject(object).build();
		// 建立索引
		elasticsearchTemplate.index(query);
	}

	/**
	 * 批量删除
	 * @param elasticsearchTemplate
	 * @param clazz
	 * @param ids
	 */
	public static void deleteObject(ElasticsearchTemplate elasticsearchTemplate, Class<?> clazz, Integer ids[]) {
		for (Integer id : ids) {
			elasticsearchTemplate.delete(clazz, id + "");
		}
	}

	/**
	 * 
	 * @Title: selectById
	 * @Description: 根据id在es服务启中查询对象
	 * @param elasticsearchTemplate
	 * @param clazz 对象的Class
	 * @param id 主键
	 * @return: Object
	 */
	public static Object selectById(ElasticsearchTemplate elasticsearchTemplate, Class<?> clazz, Integer id) {
		GetQuery query = new GetQuery();
		query.setId(id + "");
		return elasticsearchTemplate.queryForObject(query, clazz);
	}

	/**
	 * 根据关键词查询文档，支持关键词高亮、可以指定排序字段
	 * @param elasticsearchTemplate
	 * @param keyword 搜索关键词
	 * @param clazz 返回实体类class
	 * @param sortField
	 * @param pageNum
	 * @param pageSize
	 * @param highLightNames
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static  PageInfo<?> findByKeyword(ElasticsearchTemplate elasticsearchTemplate, String keyword, 
			Class<?> clazz, String sortField,Integer pageNum,Integer pageSize, String highLightfieldNames) {
		/** 定义要返回结果PageInfo **/
		PageInfo<?> pageInfo = new PageInfo<>();
		/** ES的查询结果  **/		
		AggregatedPage<?> page = null;
		/** 创建Pageable对象,主键的实体类属性名 **/	
		final Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.ASC, sortField));
		/** 定义查询对象 **/
		SearchQuery searchQuery = null;
		/** 查询条件高亮的构建对象 **/
		QueryBuilder queryBuilder = null;
		/** 高亮拼接的前缀与后缀 **/
		String preTags = "<font color=\"red\">";
		String postTags = "</font>";
		/** 定义创建高亮的构建集合对象 **/
		String[] fieldNames = highLightfieldNames.split(",");
		HighlightBuilder.Field highlightFields[] = new HighlightBuilder.Field[fieldNames.length];
		for (int i = 0; i < fieldNames.length; i++) {
			// 这个代码有问题
			highlightFields[i] = new HighlightBuilder.Field(fieldNames[i]).preTags(preTags).postTags(postTags);
		}
		/** 查询数据 **/
		if (keyword != null && !"".equals(keyword)) {
			/** 创建queryBuilder对象 **/
			queryBuilder = QueryBuilders.multiMatchQuery(keyword, fieldNames);
			searchQuery = new NativeSearchQueryBuilder()
					.withQuery(queryBuilder)
					.withHighlightFields(highlightFields)
					.withPageable(pageable).build();
			/** 查询数据 **/
			page = elasticsearchTemplate.queryForPage(searchQuery, clazz, new SearchResultMapper() {
				public <T> AggregatedPage<T> mapResults(SearchResponse response, Class<T> clazz, Pageable pageable1) {
					List<T> contentList = new ArrayList<T>();
					long total = 0l;
					try {
						/** 查询结果 **/
						SearchHits hits = response.getHits();
						/** 查询结果为空 **/
						if(hits==null) {
							return new AggregatedPageImpl<T>(contentList, pageable, total);
						}
						/** 获取总记录数 **/
						total = hits.getTotalHits();
						/** 获取结果数组 **/
						SearchHit[] searchHits = hits.getHits();
						/** 遍历封装查询的对象 **/
						for (SearchHit searchHit:searchHits) {
							/** 对象值  **/
							T entity = clazz.newInstance();
							/** 获取对象的所有的字段 **/
							Field[] fields = clazz.getDeclaredFields();
							/** 遍历字段对象 **/
							for (Field field:fields) {
								/** 暴力反射 **/
								field.setAccessible(true);
								/** 字段名称 **/
								String fieldName = field.getName();
								if("serialVersionUID".equals(fieldName) || "user".equals(fieldName)
										 || "channel".equals(fieldName)  || "category".equals(fieldName)) {
									continue;
								}
								/** 字段值 **/
								Object fieldValue = searchHit.getSourceAsMap().get(fieldName);
								/** 字段类型 **/
								Class<?> type = field.getType();
								if (type == Date.class) {
									fieldValue = new Date(Long.valueOf(fieldValue + ""));
								}
								/** 是否高亮字段 **/
								HighlightField highlightField = searchHit.getHighlightFields().get(fieldName);
								if(highlightField!=null) {
									/** 高亮 处理 拿到 被<font color='red'> </font>结束所包围的内容部分 **/
									fieldValue = highlightField.getFragments()[0].toString();
								}
								field.set(entity, fieldValue);
							}
							contentList.add(entity);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

					return new AggregatedPageImpl<T>(contentList, pageable, total);
				}
			});

		} else {
			/** 没有查询条件，分页获取ES中的全部数据  **/
			searchQuery = new NativeSearchQueryBuilder().withPageable(pageable).build();
			page = elasticsearchTemplate.queryForPage(searchQuery, clazz);
		}
		/** 封装PageInfo对象 **/
		int totalCount = (int) page.getTotalElements();
		int pages = (totalCount+ pageSize-1) / pageSize;
		//totalCount%pageSize==0?totalCount/pageSize:totalCount/pageNum+1;
		pageInfo.setTotal(page.getTotalElements());
		pageInfo.setPageNum(pageNum);
		pageInfo.setPageSize(pageSize);
		pageInfo.setPrePage(pageNum-1);
		pageInfo.setNextPage(pageNum+1);
		pageInfo.setPages(pages);
		pageInfo.setHasNextPage(pageNum!=pages);
		pageInfo.setHasPreviousPage(pageNum>1);
		int[] navigatepageNums = new int[pages];
		for(int i=0;i<pages;i++) {
			navigatepageNums[i] = i+1;
		}
		pageInfo.setNavigatepageNums(navigatepageNums);
		List content = page.getContent();
		pageInfo.setList(content);
		return pageInfo;
	}

}
