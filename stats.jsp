<!-- $Id$ -->
<%@ page import="java.util.*" %>
<%@ page import="org.mager.gwebcache.*" %>
<% String version = GWebCache.getVersion(); %>
<% Stats stats = Stats.getInstance();%>
<% stats.numStatsRequests.bumpCount();%>
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
<center><h2>Statistics</h2></center>
<h3>Global Stats</h3>
<table border="1">
	<tr>
		<th></th>
		<th>This Hour</th>
		<th>Last Hour</th>
		<th>This Day</th>
		<th>Last Day</th>
		<th>Since Start</th>
	</tr>
	<%stats.bumpHour(System.currentTimeMillis());%>
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
	<tr>
		<th>GWC1 Updates</th>
		<td><%=stats.updateRequestsGWC1.getThisHourCount()%></td>
		<td><%=stats.updateRequestsGWC1.getLastHourCount()%></td>
		<td><%=stats.updateRequestsGWC1.getThisDayCount()%></td>
		<td><%=stats.updateRequestsGWC1.getLastDayCount()%></td>
		<td><%=stats.updateRequestsGWC1.getTotalCount()%></td>
	</tr>	<tr>
		<th>GWC2 Updates</th>
		<td><%=stats.updateRequestsGWC2.getThisHourCount()%></td>
		<td><%=stats.updateRequestsGWC2.getLastHourCount()%></td>
		<td><%=stats.updateRequestsGWC2.getThisDayCount()%></td>
		<td><%=stats.updateRequestsGWC2.getLastDayCount()%></td>
		<td><%=stats.updateRequestsGWC2.getTotalCount()%></td>
	</tr>
</table>
<h3>Requests by client</h3>
<table border="1">
	<tr>
		<th>Client</th>
		<th>This Hour</th>
		<th>Last Hour</th>
		<th>This Day</th>
		<th>Last Day</th>
		<th>Since Start</th>
	</tr>
	<%
		Iterator it1 = new TreeSet(
			stats.clientRequests.keySet()).iterator();
		while (it1.hasNext()) {
			String client = (String)it1.next();
			Counter counter =
				(Counter)stats.clientRequests.get(client);
	%>
	<tr>
		<th><%=client%></th>
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
<h3>Requests by client/version</h3>
<table border="1">
	<tr>
		<th>Client/version</th>
		<th>This Hour</th>
		<th>Last Hour</th>
		<th>This Day</th>
		<th>Last Day</th>
		<th>Since Start</th>
	</tr>
	<%
		it1 = new TreeSet(
			stats.clientVersionRequests.keySet()).iterator();
		while (it1.hasNext()) {
			ClientVersion client = (ClientVersion)it1.next();
			Counter counter =
				(Counter)stats.clientVersionRequests.get(client);
	%>
	<tr>
		<th><%=client%></th>
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
<h3>page requests</h3>
<table border="1">
	<tr>
		<th>Client/version</th>
		<th>This Hour</th>
		<th>Last Hour</th>
		<th>This Day</th>
		<th>Last Day</th>
		<th>Since Start</th>
	</tr>
	<tr>
		<th>data.jsp</th>
		<td><%=stats.numDataRequests.getThisHourCount()%></td>
		<td><%=stats.numDataRequests.getLastHourCount()%></td>
		<td><%=stats.numDataRequests.getThisDayCount()%></td>
		<td><%=stats.numDataRequests.getLastDayCount()%></td>
		<td><%=stats.numDataRequests.getTotalCount()%></td>
	</tr>	
	<tr>
		<th>index.jsp</th>
		<td><%=stats.numIndexRequests.getThisHourCount()%></td>
		<td><%=stats.numIndexRequests.getLastHourCount()%></td>
		<td><%=stats.numIndexRequests.getThisDayCount()%></td>
		<td><%=stats.numIndexRequests.getLastDayCount()%></td>
		<td><%=stats.numIndexRequests.getTotalCount()%></td>
	</tr>
	<tr>
		<th>license.jsp</th>
		<td><%=stats.numLicenseRequests.getThisHourCount()%></td>
		<td><%=stats.numLicenseRequests.getLastHourCount()%></td>
		<td><%=stats.numLicenseRequests.getThisDayCount()%></td>
		<td><%=stats.numLicenseRequests.getLastDayCount()%></td>
		<td><%=stats.numLicenseRequests.getTotalCount()%></td>
	</tr>
	<tr>
		<th>stats.jsp</th>
		<td><%=stats.numStatsRequests.getThisHourCount()%></td>
		<td><%=stats.numStatsRequests.getLastHourCount()%></td>
		<td><%=stats.numStatsRequests.getThisDayCount()%></td>
		<td><%=stats.numStatsRequests.getLastDayCount()%></td>
		<td><%=stats.numStatsRequests.getTotalCount()%></td>
	</tr>
</table>

<hr>
Current Time: <%=new Date()%><br>
<%@ include file="address.html" %>
</body>
</html>
