<!-- $Id$ -->
<%@ page import="java.util.*" %>
<%@ page import="org.mager.gwebcache.*" %>
<% String version = GWebCache.getVersion(); %>
<% Stats stats = Stats.getInstance();%>
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
<%@ include file="menu.html" %>
<hr>
Cache Start Time: <%=stats.startTime%><br>
<table border="1">
	<tr>
		<th></th>
		<th>This Hour</th>
		<th>Last Hour</th>
		<th>This Day</th>
		<th>Last Day</th>
		<th>Since Start</th>
	</tr>
	<%stats.bumpHour(new Date());%>
	<tr>
		<th>Requests</th>
		<td><%=stats.numRequests.getThisHourCount()%></td>
		<td><%=stats.numRequests.getLastHourCount()%></td>
		<td><%=stats.numRequests.getThisDayCount()%></td>
		<td><%=stats.numRequests.getLastDayCount()%></td>
		<td><%=stats.numRequests.getTotalCount()%></td>
	</tr>
	<tr>
		<th>Updates</th>
		<td><%=stats.numUpdates.getThisHourCount()%></td>
		<td><%=stats.numUpdates.getLastHourCount()%></td>
		<td><%=stats.numUpdates.getThisDayCount()%></td>
		<td><%=stats.numUpdates.getLastDayCount()%></td>
		<td><%=stats.numUpdates.getTotalCount()%></td>
	</tr>
		<tr>
		<th>url Requests (GWC1)</th>
		<td><%=stats.urlRequests.getThisHourCount()%></td>
		<td><%=stats.urlRequests.getLastHourCount()%></td>
		<td><%=stats.urlRequests.getThisDayCount()%></td>
		<td><%=stats.urlRequests.getLastDayCount()%></td>
		<td><%=stats.urlRequests.getTotalCount()%></td>
	</tr>
	<tr>
		<th>host Requests (GWC1)</th>
		<td><%=stats.hostRequests.getThisHourCount()%></td>
		<td><%=stats.hostRequests.getLastHourCount()%></td>
		<td><%=stats.hostRequests.getThisDayCount()%></td>
		<td><%=stats.hostRequests.getLastDayCount()%></td>
		<td><%=stats.hostRequests.getTotalCount()%></td>
	</tr>
	<tr>
		<th>get Requests (GWC2)</th>
		<td><%=stats.getRequests.getThisHourCount()%></td>
		<td><%=stats.getRequests.getLastHourCount()%></td>
		<td><%=stats.getRequests.getThisDayCount()%></td>
		<td><%=stats.getRequests.getLastDayCount()%></td>
		<td><%=stats.getRequests.getTotalCount()%></td>
	</tr>

	<%
		Iterator it1 = stats.clientRequests.entrySet().iterator();
		while (it1.hasNext()) {
			Map.Entry entry = (Map.Entry)it1.next();
			Counter counter = (Counter)entry.getValue();
	%>
	<tr>
		<th><%=entry.getKey()%></th>
		<td><%=counter.getThisHourCount()%></td>
		<td><%=counter.getLastHourCount()%></td>
		<td><%=counter.getThisDayCount()%></td>
		<td><%=counter.getLastDayCount()%></td>
		<td><%=counter.getTotalCount()%></td>
	</tr>
	<%
		}
	%>

	<%
		it1 = stats.clientVersionRequests.entrySet().iterator();
		while (it1.hasNext()) {
			Map.Entry entry = (Map.Entry)it1.next();
			Counter counter = (Counter)entry.getValue();
	%>
	<tr>
		<th><%=entry.getKey()%></th>
		<td><%=counter.getThisHourCount()%></td>
		<td><%=counter.getLastHourCount()%></td>
		<td><%=counter.getThisDayCount()%></td>
		<td><%=counter.getLastDayCount()%></td>
		<td><%=counter.getTotalCount()%></td>
	</tr>
	<%
		}
	%>
	
</table>
<hr>
Current Time: <%=new Date()%><br>
<address><a href="mailto:jum@anubis.han.de">Jens-Uwe Mager</a>
</body>
</html>
