<%@ include file="/WEB-INF/jsp/include.jsp"
%><%@ page contentType="text/html; charset=utf-8" pageEncoding="UTF-8"
%><!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>index.do</title>
</head>
<body>
<h1>backgroundJob.do -- jsp/backgroundJob.jsp</h1>

<p><c:out value="${message}" escapeXml="true"/></p>

<form action="<c:url value='/backgroundJob.do'/>" method="POST">
<input type="submit" name="doBackground" value="Do BackgroundJob">
</form>

</body>
</html>