<!-- $Id$ -->
<%@ page import="java.util.*" %>
<%@ page import="org.mager.gwebcache.*" %>
<%@ page import="java.text.DecimalFormat" %>
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
<style>
table td.n
{
      text-align: right;
}</style>

<h1>
<img src="gnutella.gif" width="83" height="103" align="center">
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
<h3>Global Stats</h3><small><a href="#">top</a></small>
<table border="1">
	<tr>
		<th></th>
		<th></th>
		<th>This Hour</th>
		<th>Last Hour</th>
		<th>This Day</th>
		<th>Last Day</th>
		<th>Since Start</th>
	</tr>
	<tr>
		<th rowspan="2" valign="top">Global</th>
		<th>Requests</th>
		<td class=n><%=stats.numRequests.getThisHourCount()%></td>
		<td class=n><%=stats.numRequests.getLastHourCount()%></td>
		<td class=n><%=stats.numRequests.getThisDayCount()%></td>
		<td class=n><%=stats.numRequests.getLastDayCount()%></td>
		<td class=n><%=stats.numRequests.getTotalCount()%></td>
	</tr>
	<tr>
		<th>Updates</th>
		<td class=n><%=stats.numUpdates.getThisHourCount()%></td>
		<td class=n><%=stats.numUpdates.getLastHourCount()%></td>
		<td class=n><%=stats.numUpdates.getThisDayCount()%></td>
		<td class=n><%=stats.numUpdates.getLastDayCount()%></td>
		<td class=n><%=stats.numUpdates.getTotalCount()%></td>
	</tr>
	<tr>
		<th rowspan="8" valign="top">GWC1</th>
		<th>urlfile Requests</th>
		<td class=n><%=stats.urlfileRequests.getThisHourCount()%></td>
		<td class=n><%=stats.urlfileRequests.getLastHourCount()%></td>
		<td class=n><%=stats.urlfileRequests.getThisDayCount()%></td>
		<td class=n><%=stats.urlfileRequests.getLastDayCount()%></td>
		<td class=n><%=stats.urlfileRequests.getTotalCount()%></td>
	</tr>
	<tr>
		<th>hostfile Requests</th>
		<td class=n><%=stats.hostfileRequests.getThisHourCount()%></td>
		<td class=n><%=stats.hostfileRequests.getLastHourCount()%></td>
		<td class=n><%=stats.hostfileRequests.getThisDayCount()%></td>
		<td class=n><%=stats.hostfileRequests.getLastDayCount()%></td>
		<td class=n><%=stats.hostfileRequests.getTotalCount()%></td>
	</tr>
	<tr>
		<th>statfile Requests</th>
		<td class=n><%=stats.statfileRequests.getThisHourCount()%></td>
		<td class=n><%=stats.statfileRequests.getLastHourCount()%></td>
		<td class=n><%=stats.statfileRequests.getThisDayCount()%></td>
		<td class=n><%=stats.statfileRequests.getLastDayCount()%></td>
		<td class=n><%=stats.statfileRequests.getTotalCount()%></td>
	</tr>
	<tr>
		<th>ping Requests</th>
		<td class=n><%=stats.pingRequestsGWC1.getThisHourCount()%></td>
		<td class=n><%=stats.pingRequestsGWC1.getLastHourCount()%></td>
		<td class=n><%=stats.pingRequestsGWC1.getThisDayCount()%></td>
		<td class=n><%=stats.pingRequestsGWC1.getLastDayCount()%></td>
		<td class=n><%=stats.pingRequestsGWC1.getTotalCount()%></td>
	</tr>
	<tr>
		<th>Updates</th>
		<td class=n><%=stats.updateRequestsGWC1.getThisHourCount()%></td>
		<td class=n><%=stats.updateRequestsGWC1.getLastHourCount()%></td>
		<td class=n><%=stats.updateRequestsGWC1.getThisDayCount()%></td>
		<td class=n><%=stats.updateRequestsGWC1.getLastDayCount()%></td>
		<td class=n><%=stats.updateRequestsGWC1.getTotalCount()%></td>
	</tr>
	<tr>
		<th>IP Updates</th>
		<td class=n><%=stats.IPUpdateRequestsGWC1.getThisHourCount()%></td>
		<td class=n><%=stats.IPUpdateRequestsGWC1.getLastHourCount()%></td>
		<td class=n><%=stats.IPUpdateRequestsGWC1.getThisDayCount()%></td>
		<td class=n><%=stats.IPUpdateRequestsGWC1.getLastDayCount()%></td>
		<td class=n><%=stats.IPUpdateRequestsGWC1.getTotalCount()%></td>
	</tr>
	<tr>
		<th>URL Updates</th>
		<td class=n><%=stats.URLUpdateRequestsGWC1.getThisHourCount()%></td>
		<td class=n><%=stats.URLUpdateRequestsGWC1.getLastHourCount()%></td>
		<td class=n><%=stats.URLUpdateRequestsGWC1.getThisDayCount()%></td>
		<td class=n><%=stats.URLUpdateRequestsGWC1.getLastDayCount()%></td>
		<td class=n><%=stats.URLUpdateRequestsGWC1.getTotalCount()%></td>
	</tr>
	<tr>
		<th>Rate Limited</th>
		<td class=n><%=stats.RateLimitedGWC1.getThisHourCount()%></td>
		<td class=n><%=stats.RateLimitedGWC1.getLastHourCount()%></td>
		<td class=n><%=stats.RateLimitedGWC1.getThisDayCount()%></td>
		<td class=n><%=stats.RateLimitedGWC1.getLastDayCount()%></td>
		<td class=n><%=stats.RateLimitedGWC1.getTotalCount()%></td>
	</tr>
	<tr>
		<th rowspan="6" valign="top">GWC2</th>
		<th>get Requests</th>
		<td class=n><%=stats.getRequests.getThisHourCount()%></td>
		<td class=n><%=stats.getRequests.getLastHourCount()%></td>
		<td class=n><%=stats.getRequests.getThisDayCount()%></td>
		<td class=n><%=stats.getRequests.getLastDayCount()%></td>
		<td class=n><%=stats.getRequests.getTotalCount()%></td>
	</tr>
	<tr>
		<th>ping Requests</th>
		<td class=n><%=stats.pingRequestsGWC2.getThisHourCount()%></td>
		<td class=n><%=stats.pingRequestsGWC2.getLastHourCount()%></td>
		<td class=n><%=stats.pingRequestsGWC2.getThisDayCount()%></td>
		<td class=n><%=stats.pingRequestsGWC2.getLastDayCount()%></td>
		<td class=n><%=stats.pingRequestsGWC2.getTotalCount()%></td>
	</tr>
	<tr>
		<th>Updates</th>
		<td class=n><%=stats.updateRequestsGWC2.getThisHourCount()%></td>
		<td class=n><%=stats.updateRequestsGWC2.getLastHourCount()%></td>
		<td class=n><%=stats.updateRequestsGWC2.getThisDayCount()%></td>
		<td class=n><%=stats.updateRequestsGWC2.getLastDayCount()%></td>
		<td class=n><%=stats.updateRequestsGWC2.getTotalCount()%></td>
	</tr>
	<tr>
		<th>IP Updates</th>
		<td class=n><%=stats.IPUpdateRequestsGWC2.getThisHourCount()%></td>
		<td class=n><%=stats.IPUpdateRequestsGWC2.getLastHourCount()%></td>
		<td class=n><%=stats.IPUpdateRequestsGWC2.getThisDayCount()%></td>
		<td class=n><%=stats.IPUpdateRequestsGWC2.getLastDayCount()%></td>
		<td class=n><%=stats.IPUpdateRequestsGWC2.getTotalCount()%></td>
	</tr>
	<tr>
		<th>URL Updates</th>
		<td class=n><%=stats.URLUpdateRequestsGWC2.getThisHourCount()%></td>
		<td class=n><%=stats.URLUpdateRequestsGWC2.getLastHourCount()%></td>
		<td class=n><%=stats.URLUpdateRequestsGWC2.getThisDayCount()%></td>
		<td class=n><%=stats.URLUpdateRequestsGWC2.getLastDayCount()%></td>
		<td class=n><%=stats.URLUpdateRequestsGWC2.getTotalCount()%></td>
	</tr>
	<tr>
		<th>Rate Limited</th>
		<td class=n><%=stats.RateLimitedGWC2.getThisHourCount()%></td>
		<td class=n><%=stats.RateLimitedGWC2.getLastHourCount()%></td>
		<td class=n><%=stats.RateLimitedGWC2.getThisDayCount()%></td>
		<td class=n><%=stats.RateLimitedGWC2.getLastDayCount()%></td>
		<td class=n><%=stats.RateLimitedGWC2.getTotalCount()%></td>
	</tr>
</table>

<a name="client"></a>
<h3>Requests by client</h3><small><a href="#">top</a></small>
<table border="1">
	<tr>
		<th>Client</th>
		<th>This Hour</th>
		<th>% This Hour</th>
		<th>Last Hour</th>
		<th>This Day</th>
		<th>Last Day</th>
		<th>Since Start</th>
		<th>%Since Start</th>
	</tr>
	<%
		long total = stats.numRequests.getTotalCount();
		long totalHour = stats.numRequests.getThisHourCount();
		DecimalFormat df = new DecimalFormat("#.##");
		Iterator it1 = new TreeSet(
			stats.clientRequests.keySet()).iterator();
		while (it1.hasNext()) {
			String client = (String)it1.next();
			Counter counter =
				(Counter)stats.clientRequests.get(client);
	%>
	<tr>
		<th><%=client%></th>
		<td class=n><%=counter.getThisHourCount()%></td>
		<td class=n><%=df.format(counter.getThisHourCount()*100.0/totalHour)%></td>
		<td class=n><%=counter.getLastHourCount()%></td>
		<td class=n><%=counter.getThisDayCount()%></td>
		<td class=n><%=counter.getLastDayCount()%></td>
		<td class=n><%=counter.getTotalCount()%></td>
		<td class=n><%=df.format(counter.getTotalCount()*100.0/total)%></td>
	</tr>
	<%
		}
	%>
	<tr>
		<th>Total</th>
		<td class=n><%=(int)totalHour%></td>
		<td class=n>100%</td>
		<td class=n><%=stats.numRequests.getLastHourCount()%></td>
		<td class=n><%=stats.numRequests.getThisDayCount()%></td>
		<td class=n><%=stats.numRequests.getLastDayCount()%></td>
		<td class=n><%=total%></td>
		<td class=n>100%</td>
	</tr>
</table>

<a name="clientversion"></a>
<h3>Requests by client/version</h3><small><a href="#">top</a></small>
<table border="1">
	<tr>
		<th>Client/version</th>
		<th>This Hour</th>
		<th>% This Hour</th>
		<th>Last Hour</th>
		<th>This Day</th>
		<th>Last Day</th>
		<th>Since Start</th>
		<th>%Since Start</th>
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
		<td class=n><%=counter.getThisHourCount()%></td>
		<td class=n><%=df.format(counter.getThisHourCount()*100.0/totalHour)%></td>
		<td class=n><%=counter.getLastHourCount()%></td>
		<td class=n><%=counter.getThisDayCount()%></td>
		<td class=n><%=counter.getLastDayCount()%></td>
		<td class=n><%=counter.getTotalCount()%></td>
		<td class=n><%=df.format(counter.getTotalCount()*100.0/total)%></td>
	</tr>
	<%
		}
	%>
		<tr>
		<th>Total</th>
		<td class=n><%=(int)totalHour%></td>
		<td class=n>100%</td>
		<td class=n><%=stats.numRequests.getLastHourCount()%></td>
		<td class=n><%=stats.numRequests.getThisDayCount()%></td>
		<td class=n><%=stats.numRequests.getLastDayCount()%></td>
		<td class=n><%=total%></td>
		<td class=n>100%</td>
	</tr>
</table>

<a name="page"></a>
<h3>Page requests</h3><small><a href="#">top</a></small>
<table border="1">
	<tr>
		<th></th>
		<th>This Hour</th>
		<th>Last Hour</th>
		<th>This Day</th>
		<th>Last Day</th>
		<th>Since Start</th>
	</tr>
	<tr>
		<th>data.jsp</th>
		<td class=n><%=stats.numDataRequests.getThisHourCount()%></td>
		<td class=n><%=stats.numDataRequests.getLastHourCount()%></td>
		<td class=n><%=stats.numDataRequests.getThisDayCount()%></td>
		<td class=n><%=stats.numDataRequests.getLastDayCount()%></td>
		<td class=n><%=stats.numDataRequests.getTotalCount()%></td>
	</tr>	
	<tr>
		<th>index.jsp</th>
		<td class=n><%=stats.numIndexRequests.getThisHourCount()%></td>
		<td class=n><%=stats.numIndexRequests.getLastHourCount()%></td>
		<td class=n><%=stats.numIndexRequests.getThisDayCount()%></td>
		<td class=n><%=stats.numIndexRequests.getLastDayCount()%></td>
		<td class=n><%=stats.numIndexRequests.getTotalCount()%></td>
	</tr>
	<tr>
		<th>license.jsp</th>
		<td class=n><%=stats.numLicenseRequests.getThisHourCount()%></td>
		<td class=n><%=stats.numLicenseRequests.getLastHourCount()%></td>
		<td class=n><%=stats.numLicenseRequests.getThisDayCount()%></td>
		<td class=n><%=stats.numLicenseRequests.getLastDayCount()%></td>
		<td class=n><%=stats.numLicenseRequests.getTotalCount()%></td>
	</tr>
	<tr>
		<th>stats.jsp</th>
		<td class=n><%=stats.numStatsRequests.getThisHourCount()%></td>
		<td class=n><%=stats.numStatsRequests.getLastHourCount()%></td>
		<td class=n><%=stats.numStatsRequests.getThisDayCount()%></td>
		<td class=n><%=stats.numStatsRequests.getLastDayCount()%></td>
		<td class=n><%=stats.numStatsRequests.getTotalCount()%></td>
	</tr>
</table>

<hr>
Current Time: <%=new Date()%><br>
<%@ include file="address.html" %>
</body>
</html>
