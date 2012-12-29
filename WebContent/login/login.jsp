<%@ include file="/WEB-INF/jsp/include.jsp"
%><%@ page contentType="text/html; charset=utf-8" pageEncoding="UTF-8"
%><!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>index.do</title>
</head>
<body>
<h1>login.jsp</h1>
<form name="f" action="<c:url value='/j_spring_security_check'/>" method="post">
<table>
<tr>
<td>user</td><td><input type="text" name="j_username" value="" >
</tr>
<tr>
<td>password</td><td><input type="password" name="j_password" value="" >
</tr>
<tr>
<td colspan="2"><input type="submit"></td>
</tr>
</table>
</form>
</body>
</html>