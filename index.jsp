<!-- $Id$ -->
<%@ page import="java.util.*" %>
<%@ page import="org.mager.gwebcache.*" %>
<% String version = GWebCache.getVersion(); %>
<html>
<head>
<title>
GWebCache <%=version%>
</title>
</head>
<body bgcolor="white">
<h1>
<img src="icon.gif" width="32" height="32">
GWebCache <%=version%>
</h1>
[ <a href="index.jsp">Home</a> | <a href="data.jsp">Data</a> ]
<hr>
This is a Java servlet based Gnutella web cache. For more
information see this <a href="http://www.mager.org/GWebCache/">page</a>.
<hr>
Running on 
<%=getServletConfig().getServletContext().getServerInfo()%><br>
Current Time: <%=new Date()%><br>
<address><a href="mailto:jum@anubis.han.de">Jens-Uwe Mager</a>
</body>
</html>
