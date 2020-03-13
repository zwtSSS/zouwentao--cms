<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>${article.title }</title>
<meta name="viewport"
	content="width=device-width, initial-scale=1, shrink-to-fit=no">
<!-- 引入 css -->
<link rel="stylesheet" type="text/css"
	href="/resource/css/bootstrap.css">
<!-- 引入js -->
<script type="text/javascript" src="/resource/js/bootstrap.min.js"></script>
<script type="text/javascript" src="/resource/js/jquery-3.2.1.js"></script>

</head>

<body>
	
		<!-- 文章标题，占12列 -->
		<div class="row">
			<div class="col-md-12">
				<h2>${article.title }</h2>
				<p>${article.user.username}
					<fmt:formatDate value="${article.created }"
						pattern="yyyy-MM-dd HH:mm:ss" />
				</p>
				<c:if test="${null!=collect}">
				   <a href="javascript:collect(1)"><span style="color: red">取消收藏</span></a>
				
				</c:if>
				<c:if test="${null==collect}">
				 <a href="javascript:collect(0)">收藏</a>
				
				</c:if>
			</div>
		</div>

		<hr>

		<div class="row">
			<!-- 左侧占9列 -->
			<div class="col-md-9">
	
				<div class="pic" style="max-width: 50px">${article.content }</div>
				
				
				<hr style="height: 3px; border: none; border-top: 3px double red;" />
				
				
				
				
				
				
				<!-- 如果用户没有登录则不显示评论框 -->
				<c:if test="${null!=sessionScope.user }">
					<div>
						评论：
						<textarea rows="5" cols="153" name="content"></textarea>

						<button class="btn btn-info" type="button" onclick="addContent()">提交评论</button>
						<button class="btn btn-warning" type="reset">清空内容</button>

					</div>
				</c:if>
				<c:if test="${null == sessionScope.user }">
					<h3 style="color: red">请登录后进行评论</h3>
				</c:if>


				<!-- 显示评论内容
	 -->
				<div>
					<c:forEach items="${info.list}" var="compent">
						<dl>
							<dt>${compent.user.username }&nbsp;${compent.created }</dt>
							<dd>${compent.content }</dd>
						</dl>
					</c:forEach>

					<!-- 引入分页的信息 -->
					<jsp:include page="/WEB-INF/view/common/pages.jsp"></jsp:include>
				</div>
			</div>
			<div class="col-md-3">
			
			
			</div>
		</div>
		
		<!-- 友情链接 -->
		<div class="row">
			<div class="container">
				<div >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;友情链接：
					<c:forEach items="${selectLianjie }" var="lianjie">
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="${lianjie.url }" >${lianjie.name }</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					</c:forEach>
				</div>
			</div>
		</div>
	
</body>

<script type="text/javascript">
//收藏或取消收藏
function collect(flag){
	//获取当前文章的url
	var url =window.location.href;
	var text ='${article.title}';//文件的标题

	if(flag==0){//收藏
		$.post("/collect",{url:url,text:text},function(result){
		 	if(result){
		 		alert("收藏成功");
		 		location.reload();//刷新当前页面
		 		
		 	}else{
		 		alert("收藏失败")
		 	}
		})
	}else{//取消收藏
		var id='${collect.id}';
        $.post("/unCollect",{id:id},function(result){
        	if(result){
		 		alert("取消收藏成功")
		 			location.reload();	//刷新当前页面
		 	}else{
		 		alert("取消收藏失败")
		 	}
		})	
	}
	
}


	//翻页
	var id = '${article.id}';//文章ID
	function goPage(page) {

		location.href = "/articleDetail?id=" + id + "&page=" + page
	}

	//增加评论
	function addContent() {

		//参数 : （ content :评论内容），article.id ： 文章的ID
		$.post("/addContent", {
			content : $("[name='content']").val(),
			'article.id' : id
		}, function(flag) {
			if (flag) {
				alert("评论成功！");
				//刷新本页面
				location.href = "/articleDetail?id=" + id;
			} else {
				alert("评论失败！请登录后再试");
			}
		})
	}
</script>

</html>