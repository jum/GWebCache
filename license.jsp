<!-- $Id$ -->
<%@ page import="java.util.*" %>
<%@ page import="org.mager.gwebcache.*" %>
<% String version = GWebCache.getVersion(); %>
<html>
<head>
<title>
GWebCache <%=version%> License
</title>
</head>
<body bgcolor="white">
<h1>
<img src="icon.gif" width="32" height="32">
GWebCache <%=version%> License
</h1>
<%@ include file="menu.html" %>
<hr>
<pre>
<%@ include file="COPYING" %>
</pre>
<hr>
Current Time: <%=new Date()%><br>
<%@ include file="address.html" %>
</body>
</html>
