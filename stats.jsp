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
<a name="top"></a>
<h1>
<img src="icon.gif" width="32" height="32">
GWebCache <%=version%> Stats
</h1>
<%@ include file="menu.html" %>
<ul>
   <li><a href="#global">Global GWebCache statistics</a></li>
   <li><a href="#client">GWebCache statistics by client</a></li>
   <li><a href="#clientversion">GWebCache statistics by client/version</a></li>
   <li><a href="#page">HTML page served statistics</a></li>
</ul>
<hr>
Stat Start Time: <%=stats.statsStartTime%><br>
Last Cache Start Time: <%=stats.startTime%><br>

<center><h2>Statistics</h2></center>
<a name="global"></a>
<h3>Global Stats</h3><small><a href="#top">top</a></small>
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
		<th>urlfile Requests (GWC1)</th>
		<td><%=stats.urlfileRequests.getThisHourCount()%></td>
		<td><%=stats.urlfileRequests.getLastHourCount()%></td>
		<td><%=stats.urlfileRequests.getThisDayCount()%></td>
		<td><%=stats.urlfileRequests.getLastDayCount()%></td>
		<td><%=stats.urlfileRequests.getTotalCount()%></td>
	</tr>
	<tr>
		<th>hostfile Requests (GWC1)</th>
		<td><%=stats.hostfileRequests.getThisHourCount()%></td>
		<td><%=stats.hostfileRequests.getLastHourCount()%></td>
		<td><%=stats.hostfileRequests.getThisDayCount()%></td>
		<td><%=stats.hostfileRequests.getLastDayCount()%></td>
		<td><%=stats.hostfileRequests.getTotalCount()%></td>
	</tr>
	<tr>
		<th>statfile Requests (GWC1)</th>
		<td><%=stats.statfileRequests.getThisHourCount()%></td>
		<td><%=stats.statfileRequests.getLastHourCount()%></td>
		<td><%=stats.statfileRequests.getThisDayCount()%></td>
		<td><%=stats.statfileRequests.getLastDayCount()%></td>
		<td><%=stats.statfileRequests.getTotalCount()%></td>
	</tr>
	<tr>
		<th>ping Requests (GWC1)</th>
		<td><%=stats.pingRequestsGWC1.getThisHourCount()%></td>
		<td><%=stats.pingRequestsGWC1.getLastHourCount()%></td>
		<td><%=stats.pingRequestsGWC1.getThisDayCount()%></td>
		<td><%=stats.pingRequestsGWC1.getLastDayCount()%></td>
		<td><%=stats.pingRequestsGWC1.getTotalCount()%></td>
	</tr>
	<tr>
		<th>Updates (GWC1)</th>
		<td><%=stats.updateRequestsGWC1.getThisHourCount()%></td>
		<td><%=stats.updateRequestsGWC1.getLastHourCount()%></td>
		<td><%=stats.updateRequestsGWC1.getThisDayCount()%></td>
		<td><%=stats.updateRequestsGWC1.getLastDayCount()%></td>
		<td><%=stats.updateRequestsGWC1.getTotalCount()%></td>
	</tr>
	<tr>
		<th>IP Updates (GWC1)</th>
		<td><%=stats.IPUpdateRequestsGWC1.getThisHourCount()%></td>
		<td><%=stats.IPUpdateRequestsGWC1.getLastHourCount()%></td>
		<td><%=stats.IPUpdateRequestsGWC1.getThisDayCount()%></td>
		<td><%=stats.IPUpdateRequestsGWC1.getLastDayCount()%></td>
		<td><%=stats.IPUpdateRequestsGWC1.getTotalCount()%></td>
	</tr>
	<tr>
		<th>URL Updates (GWC1)</th>
		<td><%=stats.URLUpdateRequestsGWC1.getThisHourCount()%></td>
		<td><%=stats.URLUpdateRequestsGWC1.getLastHourCount()%></td>
		<td><%=stats.URLUpdateRequestsGWC1.getThisDayCount()%></td>
		<td><%=stats.URLUpdateRequestsGWC1.getLastDayCount()%></td>
		<td><%=stats.URLUpdateRequestsGWC1.getTotalCount()%></td>
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
		<th>ping Requests (GWC2)</th>
		<td><%=stats.pingRequestsGWC2.getThisHourCount()%></td>
		<td><%=stats.pingRequestsGWC2.getLastHourCount()%></td>
		<td><%=stats.pingRequestsGWC2.getThisDayCount()%></td>
		<td><%=stats.pingRequestsGWC2.getLastDayCount()%></td>
		<td><%=stats.pingRequestsGWC2.getTotalCount()%></td>
	</tr>
	<tr>
		<th>Updates (GWC2)</th>
		<td><%=stats.updateRequestsGWC2.getThisHourCount()%></td>
		<td><%=stats.updateRequestsGWC2.getLastHourCount()%></td>
		<td><%=stats.updateRequestsGWC2.getThisDayCount()%></td>
		<td><%=stats.updateRequestsGWC2.getLastDayCount()%></td>
		<td><%=stats.updateRequestsGWC2.getTotalCount()%></td>
	</tr>
	<tr>
		<th>IP Updates (GWC2)</th>
		<td><%=stats.IPUpdateRequestsGWC2.getThisHourCount()%></td>
		<td><%=stats.IPUpdateRequestsGWC2.getLastHourCount()%></td>
		<td><%=stats.IPUpdateRequestsGWC2.getThisDayCount()%></td>
		<td><%=stats.IPUpdateRequestsGWC2.getLastDayCount()%></td>
		<td><%=stats.IPUpdateRequestsGWC2.getTotalCount()%></td>
	</tr>
	<tr>
		<th>URL Updates (GWC2)</th>
		<td><%=stats.URLUpdateRequestsGWC2.getThisHourCount()%></td>
		<td><%=stats.URLUpdateRequestsGWC2.getLastHourCount()%></td>
		<td><%=stats.URLUpdateRequestsGWC2.getThisDayCount()%></td>
		<td><%=stats.URLUpdateRequestsGWC2.getLastDayCount()%></td>
		<td><%=stats.URLUpdateRequestsGWC2.getTotalCount()%></td>
	</tr>
</table>

<a name="client"></a>
<h3>Requests by client</h3><small><a href="#top">top</a></small>
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

<a name="clientversion"></a>
<h3>Requests by client/version</h3><small><a href="#top">top</a></small>
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

<a name="page"></a>
<h3>Page requests</h3><small><a href="#top">top</a></small>
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
