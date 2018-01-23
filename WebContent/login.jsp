<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page session="true" %>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<base href="<%=basePath%>">
<title>Ingreso</title>
	<meta charset="UTF-8">
<%@include file="header.jsp"%>
<style type="text/css">
	body {
		font-family: "Verdana";
		background-image: url("img/formas/Fondo_Form.png");
		background-size: cover;
	}
	* {
		border-radius: 1px;
	}
</style>
<script type="text/javascript" src="js/apps/SET/Login/login.js"></script>
   <link rel="shortcut icon" href="img/favicon.ico" type="image/x-icon" />
</head>

<body>
<!--<div id="divLoginWindow" class="x-hidden">
    <div class="x-window-header"></div>
</div>
<div id="welcome" align="center" STYLE="height:100%; width:100%; background-color:#E9EFD1;">
	 <img src="img/formas/Fondo_Form.png" alt="Bienvenida" align="middle"> 
</div>-->

</body>
</html>