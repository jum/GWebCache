<!-- $Id$ -->
<%@ page import="java.util.*" %>
<%@ page import="org.mager.gwebcache.*" %>
<% String version = GWebCache.getVersion(); %>
<html>
<head>
<title>
GWebCache <%=version%> Data
</title>
</head>
<body bgcolor="white">
<h1>
<img src="icon.gif" width="32" height="32">
GWebCache <%=version%> Data
</h1>
<%@ include file="menu.html" %>
<hr>
<%
	Data i = Data.getInstance();
	synchronized (i) {
		Iterator it = new TreeSet(i.getNets().keySet()).iterator();
		while (it.hasNext()) {
			String netName = (String)it.next();
			GnutellaNet net = i.lookupNet(netName);
			HashMap map = net.getHosts();
%>
			<h2><%=netName%></h2>
			<h3>Hosts</h3>
			<table border="1">
			<th>IP:Port</th>
			<th>Client</th>
			<th>Time</th>
<%
			Iterator it1 = map.keySet().iterator();
			while (it1.hasNext()) {
				String key = (String)it1.next();
				RemoteClient host = (RemoteClient)map.get(key);
%>
				<tr>
				<td><%=host.getRemoteIP()%>:<%=host.getPort()%></td>
				<td><%=host.getClientVersion()%></td>
				<td><%=host.getLastUpdated()%></td>
				</tr>
<%
			}
%>
			</table>
			<h3>URLs</h3>
			<table border="1">
			<th>URL</th>
			<th>Version</th>
			<th>Proto</th>
			<th>Client</th>
			<th>Last Updated</th>
			<th>Last Checked</th>
<%
			map = net.getURLs();
			it1 = map.keySet().iterator();
			while (it1.hasNext()) {
				String key = (String)it1.next();
				RemoteURL url = (RemoteURL)map.get(key);
				String urlTitle = url.getRemoteURL();
				if (urlTitle.length() > 60)
					urlTitle = urlTitle.substring(0, 60) + "...";
				String cacheVersion = url.getCacheVersion();
				if (cacheVersion.length() > 80)
					cacheVersion = cacheVersion.substring(0, 80) + "...";
%>
				<tr>
				<td><a href="<%=url.getRemoteURL()%>"><%=urlTitle%></a></td>
				<td><%=cacheVersion%></td>
				<td><%=url.getProtoVersion()%></td>
				<td><%=url.getClientVersion()%></td>
				<td><%=url.getLastUpdated()%></td>
				<td><%=url.getLastChecked()%></td>
				</tr>
<%
			}
%>
			</table>
<%
		}
%>
		</table>
		<h2>Rate Limited</h2>
		<table border="1">
		<th>IP</th>
		<th>Time</th>
<%
		HashMap map = i.getRateLimited();
		it = map.keySet().iterator();
		while (it.hasNext()) {
			String key = (String)it.next();
			Date d = (Date)map.get(key);
%>
			<tr>
			<td><%=key%></td>
			<td><%=d%></td>
			</tr>
<%
		}
%>
		</table>
<%
	}
%>
<hr>
Current Time: <%=new Date()%><br>
<%@ include file="address.html" %>
</body>
</html>
