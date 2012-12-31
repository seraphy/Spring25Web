<%@ include file="/WEB-INF/jsp/include.jsp"
%><%@ page contentType="text/html; charset=utf-8" pageEncoding="UTF-8"
%><!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>accountlist.do</title>
</head>
<body>
	<h1>admin/accountlist.do -- accountlist.jsp</h1>

	<table border="1" cellpadding="5" cellspacing="0">
		<tr>
			<th>loginid</th>
			<th>password hash</th>
			<th>enabled</th>
			<th>fail-count</th>
		</tr>
		<c:forEach items="${users}" var="user">
			<tr>
				<c:url value="/admin/changepassword.do" var="lnk"><c:param name="loginid" value="${user.loginid}"/></c:url>
				<td><a href='<c:out value="${lnk}" escapeXml="true"/>'><c:out value="${user.loginid}" escapeXml="true"/></a></td>
				<td><c:out value="${fn:substring(user.password, 1, 16)}" escapeXml="true"/></td>
				<td><c:out value="${user.enabled}" escapeXml="true"/></td>
				<td><c:out value="${user.failcount}" escapeXml="true"/></td>
			</tr>
		</c:forEach>	
	</table>
	<a href='<c:url value="/admin/"/>'>return</a>
</body>
</html>