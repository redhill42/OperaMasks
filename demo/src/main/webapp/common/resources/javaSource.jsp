<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<html>
<head>
<style type="text/css">
html, body {
      font-size: 10px;
      margin:0;
      padding:0;
      border:0 none;
      overflow:hidden;
      height:100%;
}
p {
    margin:10;
    padding:0;
}
a {
    font-family: Verdana, Arial, helvetica, sans-serif;
    font-size: 12px;
    color: #003399;
}

</style>
</head>
<body>
<table height="100%" width="100%" border="0">
<tr><td valign="top">
<%
    String fileStr = request.getParameter("file");
    String[] files = fileStr.split(":") ;
    
    if (files.length > 1) {
%>
<p>
<%
        for (String file : files) {
%>
<a href="#" onclick="document.getElementById('source_frm').src='source/<%=file%>.java.html';return false;"><%=file%></a><br>
<%            
        }
%>

</p><hr>
<%
    }
%>
</td></tr>

<tr><td height="100%">
<iframe id="source_frm" frameborder="no" style="width:100%;height:90%" src="source/<%=files[0]%>.java.html"></iframe>
</td></tr>
</body>
</html>