<!-- $Id$ -->
<%@ page import="java.util.*" %>
<%@ page import="org.mager.gwebcache.*" %>
<% String version = GWebCache.getVersion(); %>
<% Stats.getInstance().numDataRequests.bumpCount();%>
<html>
<head>
<title>
GWebCache <%=version%> Data
</title>
</head>
<body bgcolor="white">
<a name="top"></a>
<h1>
<img src="icon.gif" width="32" height="32">
GWebCache <%=version%> Data
</h1>
<%@ include file="menu.html" %>
<ul>
<%
	Data i = Data.getInstance();
	synchronized (i) {
		Iterator it = new TreeSet(i.getNets().keySet()).iterator();
		while (it.hasNext()) {
		String net = (String)it.next();
%>
			<li><a href="#<%=net%>"><%=net%></a></li>
<%
		}
	}
%>
			<li><a href ="#ratelimited">Rate limited</a></li>
</ul>
<hr>


<%
	synchronized (i) {
		Iterator it = new TreeSet(i.getNets().keySet()).iterator();
		while (it.hasNext()) {
			String netName = (String)it.next();
			GnutellaNet net = i.lookupNet(netName);
			HashMap map = net.getHosts();
%>
			<a name="<%=netName%>"></a><small><a href="#top">top</a></small>
			<h1><%=netName%></h1>
			<h3>Hosts (<%=map.size()%> Entries)</h3>
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
			<h3>Working URLs</h3>
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
				if (!cacheVersion.startsWith(RemoteURL.STATE_FAILED)){
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
			}
%>
			</table>
			
			<h3>Bad URLs</h3>
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
				if (cacheVersion.startsWith(RemoteURL.STATE_FAILED)){
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
			}
%>
			</table>
<%
		}
		HashMap map = i.getRateLimited();
%>
		</table>
		<a name="ratelimited"></a>
		<h2>Rate Limited (<%=map.size()%> Entries)</h2>
		<small><a href="#top">top</a></small>
		<table border="1">
		<th>IP</th>
		<th>Time</th>
		<th>Client/Version</th>
<%
		it = map.keySet().iterator();
		while (it.hasNext()) {
			String key = (String)it.next();
			RemoteClient r = (RemoteClient)map.get(key);
%>
			<tr>
			<td><%=key%></td>
			<td><%=r.getLastUpdated()%></td>
			<td><%=r.getClientVersion()%></td>
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
