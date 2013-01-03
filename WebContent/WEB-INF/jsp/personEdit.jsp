<%@ include file="/WEB-INF/jsp/include.jsp"
%><%@ page contentType="text/html; charset=utf-8" pageEncoding="UTF-8"
%><!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>personEdit.do</title>
</head>
<body>
	<h1>personEdit.do -- personEdit.jsp</h1>

	<form:form modelAttribute="personEditForm" method="post">
		<fieldset>
			<legend>Upload Fields</legend>

			<p>
				<form:label for="id" path="id">ID: </form:label><br />
				<form:input path="id"/>
				<form:errors path="id"/>
			</p>

			<p>
				<form:label for="name" path="name">名前: </form:label><br />
				<form:input path="name" />
				<form:errors path="name"/>
			</p>

			<p>
				<form:label for="birthday" path="birthday">誕生日(YYYY/MM/DD): </form:label><br />
				<form:input path="birthday" />
				<form:errors path="birthday"/>
			</p>

			<p>
				<form:label for="height" path="height">身長: </form:label><br />
				<form:input path="height" />
				<form:errors path="height"/>
			</p>

			<p>
				<input type="submit"  name="doRegister" value="Register"/><br />
				<input type="submit"  name="doCancel" value="Cancel"/><br />
			</p>
		</fieldset>
	</form:form>
</body>
</html>