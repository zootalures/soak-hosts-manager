<%@ include file="/base.include.jsp"%>

<table>
<tr><td> Vlans</td><td>${fn:length bean.vlans }</td></tr>
<tr><td> Network Classes</td><td>${fn:length bean.networkClasses }</td></tr>
<tr><td> Subnets </td><td>${fn:length bean.subnets }</td></tr>
<tr><td> Host Classes</td><td>${fn:length bean.hostClasses }</td></tr>
<tr><td> Name Domains</td><td>${fn:length bean.nameDomains }</td></tr>
<tr><td> Hosts </td><td>${fn:length bean.hosts }</td></tr>
</table>