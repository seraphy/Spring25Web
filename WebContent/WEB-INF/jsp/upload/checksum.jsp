<%@ include file="/WEB-INF/jsp/include.jsp"
%><%@ page contentType="text/html; charset=utf-8" pageEncoding="UTF-8"
%><!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>index.do</title>
</head>
<body>
	<h1>upload/checksum.do -- checksum.jsp</h1>

	<form:form modelAttribute="checksumForm" method="post" enctype="multipart/form-data">
		<fieldset>
			<legend>Upload Fields</legend>

			<p>
				<form:label for="name" path="name">Name: </form:label><br />
				<form:input path="name" />
				<form:errors path="name"/>
			</p>

			<p>
				<form:label for="fileData" path="fileData">File: </form:label><br />
				<input type="file" size="40" name="fileData" />
				<form:errors path="fileData"/>
			</p>

			<p>
				<input type="submit"  name="doCheck" value="Check"/><br />
				<input type="submit"  name="doReport" value="Report"/>
			</p>

			<p>
				<form:label for="checksum" path="checksum">Checksum: </form:label><br />
				<form:input path="checksum" readonly="true"/>
			</p>
		</fieldset>
	</form:form>
</body>
</html>