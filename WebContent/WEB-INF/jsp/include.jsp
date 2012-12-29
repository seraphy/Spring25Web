<%@ page session="false"%><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"
%><%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"
%><%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"
%><%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"
%><%@ taglib prefix="spring" uri="http://www.springframework.org/tags"
%><%@ taglib prefix="security" uri="http://www.springframework.org/security/tags"
%><%@ taglib prefix="msg" uri="/WEB-INF/tld/MessageHelper.tld"
%><%@ taglib prefix="tg" tagdir="/WEB-INF/tags"
%><%!
  private String getHTTPDate() {
    java.text.SimpleDateFormat formatter =
      new java.text.SimpleDateFormat("E, dd MMM yyyy HH:mm:ss zzz", java.util.Locale.US);
    formatter.setTimeZone(java.util.TimeZone.getTimeZone("GMT"));
    return formatter.format(new java.util.Date());
  }
%><%
  response.setHeader("Expires", getHTTPDate());
  response.setHeader("Pragma", "no-cache");
  response.setHeader("Cache-Control", "no-cache");
%>