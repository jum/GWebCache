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
This is an experimental Java servlet based Gnutella web cache. For more
information see this <a href="http://www.mager.org/GWebCache/">page</a>.
<p>
There are no statistics yet. Click this <a href="data.jsp">link</a>
for a data dump.
<p>
Running on 
<%=getServletConfig().getServletContext().getServerInfo()%><br>
Current Time: <%=new Date()%>
</body>
</html>
