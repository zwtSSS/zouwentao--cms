package com.bobo.cms.service.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
/**
 * 抓取163.com的文章
 *
 */
public class JsoupTests {
	/** 保存已抓取的html链接，避免重复抓取 **/
	private static Set<String> linkSet = new HashSet<>();
	/** txt文件保存位置 **/
	private static String filePath = "D:\\html\\";
	

	/**
	 * 主函数，抓取163.com的文章
	 */
	public static void main(String[] args) {
		/** 保存163首页文章链接为.txt的文件 **/
		/** linkList为页面的其它链接 **/
		List<String> linkList = getHtml("https://www.163.com/");
		for (String link : linkList) {
			/** 抓取二级页面的文章为.txt的文件 **/
			List<String> link2List = getHtml(link);
			/** 抓取三级页面的文章为.txt的文件 **/
			for (String link2 : link2List) {
				getHtml(link2);
			}
		}
	}

	/**
	 * Jsoup.connect获取文档 因为url非法或请求超时，会有异常方式，所有在这个方法统一处理异常。
	 * @param url
	 * @return
	 */
	public static Document getDocument(String url) {
		Document document = null;
		try {
			document = Jsoup.connect(url).get();
		} catch (Exception e) {
			// e.printStackTrace();
			System.out.println("error:" + url);
		}
		return document;
	}
	/**
	 * 抓取文章内容，保存成txt文件到磁盘
	 * @param url 入库url：https://www.163.com/
	 * @return
	 */
	public static List<String> getHtml(String url) {
		/** 当前url抓取的地址 **/
		List<String> linkList = new ArrayList<>();
		/** 请求html内容 **/
		Document document = getDocument(url);
		/** 如果请求失败，放回空list **/
		if (document == null) {
			return linkList;
		}
		/** 选择页面的说有链接 **/
		Elements links = document.select("a");
		/** 遍历链接 **/
		for (Element link : links) {
			/** 获取a链接的href属性 **/
			String href = link.attr("href");
			/** 判断链接是否是文章类型的链接，忽略判断 **/
			if (href != null && href.startsWith("https://") && href.contains("163.com")) {
				/** 保存链接到list **/
				linkList.add(href);
				if(!href.endsWith(".html")) {
					continue;
				}
				/** 已经抓取过，避免重复抓取 **/
				if (linkSet.contains(href)) {
					continue;
				}
				/** 抓取href对应的文档 **/
				System.out.println("href:"+href);
				Document article = getDocument(href);
				/** 如果未抓取到或该链接已经抓取过则忽略 **/
				if (article == null) {
					continue;
				}
				/** herf对应的html内容 **/
				String html = article.html();
				/** 获取文章的html **/
				Elements content = article.select(".post_text");
				/** 如果html页面包含文章样式且文章内容包含图片，则抓取文章，保存成txt文件 **/
				if (html.contains("class=\"post_body\"") && content.html().contains("<img")) {
					/** 文章标题 **/
					String title = article.title();
					/** 要保存的文件名称 **/
					String fileName = title + ".txt";
					/** 替换文件名称里的非法字符，"、？、/等 **/
					fileName = fileName.replaceAll("\\\"", "").replaceAll("\\?", "").replaceAll("/", "");
					/** 要保存的页面的html **/
					String htmlContent = article.html();
					if (htmlContent!=null && !"".equals(htmlContent)) {
						/** 保存文件 **/
						writeTextFile(article.html(), filePath + fileName, false);
						/** 保存文件的链接，避免重复抓取 **/
						linkSet.add(href);
						/** 打印文件名称 **/
						System.out.println(new Date() + ":" + fileName);
					}
				}
			}
		}
		return linkList;
	}
	
	
	
	/** 工具类方法  **/
	public static void writeTextFile(String content,File file,boolean append) {
		BufferedWriter writer = null;
		try {
			//判断写文件的文件夹是否存在
			String parent = file.getParent();
			File parentFile = new File(parent);
			if(!parentFile.exists()) {
				parentFile.mkdirs();
			}
			//写文件
			writer = new BufferedWriter(new FileWriter(file,append));
			writer.write(content);
			writer.flush();
		} catch (IOException e) {
//			e.printStackTrace();
		}finally {
			close(writer);
		}
	}
	
	public static void writeTextFile(String content,String fileFullName,boolean append) {
		writeTextFile(content,new File(fileFullName), append);
	}
	
	public static void close(AutoCloseable... autoCloseables ) {
		for(AutoCloseable autoCloseable:autoCloseables) {
			try {
				autoCloseable.close();
			} catch (Exception e) {
//				e.printStackTrace();
			}
		}
	}

	

}
