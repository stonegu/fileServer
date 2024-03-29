<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>Employees</title>
</head>
<body>
<h1>employee</h1>
<table border=1>
	<thead><tr>
		<th>ID</th>
		<th>Name</th>
		<th>Email</th>
	</tr></thead>
	<tr>
		<td>${object.id}</td>
		<td>${object.name}</td>
		<td>${object.email}</td>
	</tr>
</table>


<h1>employees</h1>
<table border=1>
	<thead><tr>
		<th>ID</th>
		<th>Name</th>
		<th>Email</th>
	</tr></thead>
	<c:forEach var="employee" items="${employees.employees}">
	<tr>
		<td>${employee.id}</td>
		<td>${employee.name}</td>
		<td>${employee.email}</td>
	</tr>
	</c:forEach>
</table>
</body>
</html>