<%@ include file="/WEB-INF/jsp/include.jsp"
%><%@ page contentType="text/html; charset=utf-8" pageEncoding="UTF-8"
%><!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>admin.do</title>
</head>
<body>
	<h1>admin/account.do -- account.jsp</h1>
	
	<form method="post">
		login: <input type="text" name="loginid" value='<c:out value="${loginid}"/>'>
		<br />
		password: <input type="text" name="password" value='<c:out value="${password}"/>'>
		<br />
		hash: <input type="text" readonly="readonly" value="<c:out value="${hash}" />" >
		<br />
		<input type="submit" value="send">
	</form>
</body>
</html>