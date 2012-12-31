<%@ include file="/WEB-INF/jsp/include.jsp"
%><%@ page contentType="text/html; charset=utf-8" pageEncoding="UTF-8"
%><!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>changepassword.do</title>
</head>
<body>
	<h1>admin/changepassword.do -- changepassword.jsp</h1>
	
	<form method="post">
		<table>
			<tr>
				<th>loginid</th>
				<td><c:out value="${loginid}" escapeXml="true"/></td>
			</tr>
			<tr>
				<th>password:</th>
				<td><input type="text" name="password" value='<c:out value="${password}" escapeXml="true"/>'></td>
			</tr>
			<tr>
				<th>hash:</th>
				<td><c:out value="${hash}"  escapeXml="true"/></td>
			</tr>
			<tr>
				<td colspan="2">
					<input type="submit" value="Change Password">
				</td>
			</tr>
		</table>
	</form>

	<a href='<c:url value="accountlist.do"/>'>return</a>
</body>
</html>