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
<%
	Data i = Data.getInstance();
	synchronized (i) {
		Iterator it = i.allNets();
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
			<th>Time</th>
<%
			map = net.getURLs();
			it1 = map.keySet().iterator();
			while (it1.hasNext()) {
				String key = (String)it1.next();
				RemoteURL url = (RemoteURL)map.get(key);
%>
				<tr>
				<td><a href="<%=url.getRemoteURL()%>"><%=url.getRemoteURL()%></a></td>
				<td><%=url.getCacheVersion()%></td>
				<td><%=url.getProtoVersion()%></td>
				<td><%=url.getClientVersion()%></td>
				<td><%=url.getLastUpdated()%></td>
				</tr>
<%
			}
%>
			</table>
<%
		}
	}
%>
<p>
Current Time: <%=new Date()%>
</body>
</html>
