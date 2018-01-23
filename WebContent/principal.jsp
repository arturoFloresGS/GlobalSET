<%-- 
    Document   : principal.jsp
    Created on : 19/05/2008, 11:31:33 AM
    Author     : Armando Rodriguez Meneses - WebSet
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
   <meta charset="UTF-8">
   <META HTTP-EQUIV="" NAME="Cache-Control" CONTENT="no-cache">
   <%@include file="header.jsp"%>
   <link rel="stylesheet" type="text/css" href="css/style.css">
   
   <link rel="shortcut icon" href="img/favicon.ico" type="image/x-icon" />
   <script type="text/javascript">
		var sUserLogin = '<%=sUserLogin%>';
		var iUserId = <%=iUserId%>;
		var HOST_NAME_LOCAL = '<%=hostNameLocal%>';
		var sCodSession = '<%=sCodSession%>';
   </script>
   <script type="text/javascript" src="js/apps/SET/Login/principal.js"></script>
   <!--<script type="text/javascript" src="/SET/cssrefresh/live.js"></script>-->	
	</head>
	<body onclick="stopAndStartTime()" onkeypress="stopAndStartTime()">
		
		<!-- Fields required for history management -->
		<form id="history-form" class="x-hidden">
			<input type="hidden" id="x-history-field" />
			<iframe id="x-history-frame"></iframe>
		</form>
		
		<div id="welcome" align="center" STYLE="height:100%; width:100%; background-color:#A3BD31">
			<!--<img src="img/menu/Bienvenida.jpg" style="height:100%;width:100%;" alt="Bienvenida" align="middle">-->
		</div>
		
		<div id="props-panel" style="width:100%;height:100%;border:0px;overflow:hidden;"></div>
		<div id="south"><p>Mensajes de estatus</p></div>
		<div id="north" align="center" STYLE="height:100%; width:100%; margin-top: -7px; background-color:#E9EFD1; color:black">
			<img src="img/empresas/logo_phone.png" width="150" height="60" align="left" title="Grupo Dalton">
			
			<!-- <a onclick = "cambioEmpresa()" target="_blank">Cambio de Empresa | </a> -->
			<p align="right">
				<input type="button" onclick="cerrarSession(false)" class="boton" value="Cerrar sesión" >
			</p>
			<!-- div id="ticker" style="width:600px; height:28px; line-height:26px;" align="center">
				<script type="text/javascript" src="http://www.exchangeratewidget.com/converter.php?l=es&f=&t=USDMXN,EURMXN,GBPMXN,JPYMXN,CHFMXN,CADMXN,AUDMXN,&a=1&d=FFFFFF&n=FFFFFF&o=000000&v=11"></script>
			</div>
			<div id="tickerLeyenda" style="width:600px; height:28px; line-height:26px;" align="center">
				<script type="text/javascript">document.write(form_content.replace('Cambio de moneda Ticker Widge',''));</script>
			</div-->
		</div>
		
		<script type="text/javascript" src="js/bfrmwrk/util/jQuery.js"></script>
		<script src="js/bfrmwrk/util/scroll-startstop.events.jquery.js"></script>
	 	<script type="text/javascript" src="js/bfrmwrk/util/file-input.js"></script>
	 	<script type="text/javascript" src="js/bfrmwrk/util/funcionesJquery.js"></script>
	</body>
</html>
