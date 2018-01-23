<%-- 
    Document   : monitor.jsp
    Date : 07/04/2016
    Author     : Yoseline E.C. - WebSet
--%>      

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import = "java.net.InetAddress" %>
<%@ page session="true" %>
<%
//ültimo agregado PAEE
response.setHeader("Cache-control","no-store");
response.setHeader("Pragma","no-cache");
response.setDateHeader("Expires",0);
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";

/*** OBTENER DE SESSION  ***/    
int iUserId = session.getAttribute("id_usuario") == null ? 0 : Integer.parseInt(session.getAttribute("id_usuario").toString());

// LOGIN
String sUserLogin = session.getAttribute("userLogin")==null ? "":session.getAttribute("userLogin").toString();

//Esta variable es para obtener el codigo de session
String sCodSession = session.getAttribute("codSession") == null ? "" : session.getAttribute("codSession").toString();

session.setAttribute("sUserLogin", sUserLogin);
session.setAttribute("iUserId", iUserId);
session.getAttribute("sUserLogin");

String ip = request.getRemoteAddr();
String hostName = InetAddress.getByName(ip).getHostName(); 

session.setAttribute("hostNameLocal", hostName) ;
String hostNameLocal = session.getAttribute("hostNameLocal").toString();

session.setAttribute("debug", "true");
%>
<html>
<head>
   <title>Principal</title>
   <meta http-equiv="Content-Type" content="text/html; charset=windows-1252">
   <META HTTP-EQUIV="" NAME="Expires" CONTENT="0">
   <META HTTP-EQUIV="" NAME="Pragma" CONTENT="no-cache">
   <META HTTP-EQUIV="" NAME="Cache-Control" CONTENT="no-cache">
   <%@include file="header.jsp"%>
   <link rel="stylesheet" type="text/css" href="css/style.css">
   <script type="text/javascript">
		var sUserLogin = '<%=sUserLogin%>';
		var iUserId = <%=iUserId%>;
		var HOST_NAME_LOCAL = '<%=hostNameLocal%>';
		var sCodSession = '<%=sCodSession%>';
   </script>	
	</head>
	<body onclick="stopAndStartTime()" onkeypress="stopAndStartTime()">
		
		<!-- Fields required for history management -->
		<form id="history-form" class="x-hidden">
			<input type="hidden" id="x-history-field" />
			<iframe id="x-history-frame"></iframe>
		</form>
		
		<script type="text/javascript" src="js/apps/SET/Login/monitor.js"></script>
		
		<div id="welcome" align="center" STYLE="height:100%; width:100%; background-color:#A3BD31"></div>
		<div id="props-panel" style="width:100%;height:100%;border:0px;overflow:hidden;"></div>
		<div id="south"><p>Mensajes de estatus</p></div>
		<div id="north" align="center" STYLE="height:100%; width:100%; background-color:#E9EFD1; color:black">
			<img src="img/empresas/logoempresa.png" border="0" align="left" title="Grupo FORMULA">
			<p align="right"><a onclick="cerrarSession(false)" target="_blank" style="text-align:right" onmouseover="this.style.color='#A3BD31'" onmouseout="this.style.color='#000000'">Cerrar sesión</a>&nbsp;&nbsp;&nbsp;&nbsp;</p>
		</div>
		
		<script type="text/javascript" src="js/bfrmwrk/util/jQuery.js"></script>
		<script src="js/bfrmwrk/util/scroll-startstop.events.jquery.js"></script>
	 	<script type="text/javascript" src="js/bfrmwrk/util/file-input.js"></script>
	 	<script type="text/javascript" src="js/bfrmwrk/util/funcionesJquery.js"></script>
	</body>
</html>