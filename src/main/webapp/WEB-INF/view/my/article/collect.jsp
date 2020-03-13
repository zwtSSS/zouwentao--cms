<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport"
	content="width=device-width, initial-scale=1, shrink-to-fit=no">
	<title></title>
<!-- 引入 css -->
<link rel="stylesheet" type="text/css"
	href="/resource/css/bootstrap.css">
<!-- 引入js -->
<script type="text/javascript" src="/resource/js/bootstrap.min.js"></script>
<script type="text/javascript" src="/resource/js/jquery-3.2.1.js"></script>
<script type="text/javascript">

</script>
</head>

<body>
	
	<c:forEach items="${info.list}" var="article">
		<div class="media">
			
			<div class="media-body">
				<h5 class="mt-0">${article.text }
					
				</h5>
				<a href="${article.url }"><button type="button" class="btn btn-link"  style="float: right"
						 data-toggle="modal"
						data-target="#exampleModalLong">详情</button></a>
				
				<p>${article.text }</p>
				<p>
					<fmt:formatDate value="${article.created }"
						pattern="yyyy-MM-dd HH:mm:ss" />
					0 评论
				</p>
			</div>
		</div>
		<hr>
	</c:forEach>

	

</body>

</html>