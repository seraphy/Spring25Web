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

<p>${msg:message("greeting")}</p>
<p>login: <tg:loginName defaultName="**anonymous**"/></p>

<ul>
<li><a href='<c:url value="/upload/checksum.do"/>'>upload</a></li>
<li><a href='<c:url value="/backgroundJob.do"/>'>backgroundJob</a></li>
<li><a href='<c:url value="/admin/index.jsp"/>'>admin ...</a></li>
<li><a href='<c:url value="/logout"/>'>logout</a></li>
</ul>

</body>
</html>