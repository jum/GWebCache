<!-- $Id$ -->
<%@ page import="java.util.*" %>
<%@ page import="org.mager.gwebcache.*" %>
<% String version = GWebCache.getVersion(); %>
<html>
<head>
<title>
GWebCache <%=version%> Stats
</title>
</head>
<body bgcolor="white">
<h1>
<img src="icon.gif" width="32" height="32">
GWebCache <%=version%> Stats
</h1>
[ <a href="index.jsp">Home</a> |
<a href="data.jsp">Data</a> |
<a href="stats.jsp">Stats</a> ]
<hr>
Cache Start Time: <%=Stats.startTime%><br>
<table border="1">
	<tr>
		<th></th>
		<th>This Hour</th>
		<th>Last Hour</th>
		<th>Since Start</th>
	</tr>
	<tr>
		<th>Requests</th>
		<td><%=Stats.numRequestsThisHour%></td>
		<td><%=Stats.numRequestsLastHour%></td>
		<td><%=Stats.numRequests%></td>
	</tr>
	<tr>
		<th>Updates</th>
		<td><%=Stats.numUpdatesThisHour%></td>
		<td><%=Stats.numUpdatesLastHour%></td>
		<td><%=Stats.numUpdates%></td>
	</tr>
</table>
<hr>
Current Time: <%=new Date()%><br>
<address><a href="mailto:jum@anubis.han.de">Jens-Uwe Mager</a>
</body>
</html>
