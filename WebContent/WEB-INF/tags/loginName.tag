<%@ tag import="spring25web.util.LoginInfoHelper"
%><%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"
%><%@ attribute name="defaultName" rtexprvalue="true" required="false" type="java.lang.String"
%><%
	String loginName = LoginInfoHelper.getCurrentLoginId();
	if (loginName == null || loginName.length() == 0) {
		loginName = defaultName;
	}
	out.print(loginName);
%>