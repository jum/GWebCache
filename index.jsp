<!-- $Id$ -->
<%@ page import="java.util.*" %>
<%@ page import="org.mager.gwebcache.*" %>
<% String version = GWebCache.getVersion(); %>
<% Stats.getInstance().numIndexRequests.bumpCount();%>
<html>
<head>
<title>
GWebCache <%=version%>
</title>
</head>
<body bgcolor="white">
<h1>
<img src="gnutella.gif" width="83" height="103" align="center">
GWebCache <%=version%>
</h1>
<%@ include file="menu.html" %>
<hr>
This is a Java servlet based Gnutella web cache. For more
information see this <a href="http://www.mager.org/GWebCache/">page</a>.
<hr>
Running on 
<%=getServletConfig().getServletContext().getServerInfo()%><br>
Current Time: <%=new Date()%><br>
<%@ include file="address.html" %>
</body>
</html>
