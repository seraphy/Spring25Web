<%@ include file="/WEB-INF/jsp/include.jsp"
%><%@ page contentType="text/html; charset=utf-8" pageEncoding="UTF-8"
%><!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>index.do</title>
</head>
<body>
<h1>index.do -- jsp/index.jsp</h1>

<%-- メッセージソースから国際化した文字列を取得する例 --%>
<p>${msg:message("greeting")}</p>

<%-- 独自タグリブと、ログインユーザの表示例 --%>
<p>login: <tg:loginName defaultName="**anonymous**"/></p>

<ul>
<li><a href='<c:url value="/personEdit.do"/>'>personEdit</a> (フォームのバインド例)</li>
<li><a href='<c:url value="/upload/checksum.do"/>'>upload</a> (権限と、ファイルのアップロード例)</li>
<li><a href='<c:url value="/backgroundJob.do"/>'>backgroundJob</a> (バックグラウンド処理例)</li>
<li><a href='<c:url value="/admin/index.jsp"/>'>admin ...</a> (権限と、データベースの更新例)</li>
<li><a href='<c:url value="/logout"/>'>logout</a></li>
</ul>

</body>
</html>