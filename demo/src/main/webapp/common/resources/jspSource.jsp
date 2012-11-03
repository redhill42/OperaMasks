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
p, label {
    margin:10;
    padding:0;
    font-family: Verdana, Arial, helvetica, sans-serif;
    font-size: 12px;
}
label {
    font-weight:bold;
    text-align:center;
    width: 100%;
    text-align: center;
}
a {
    font-family: Verdana, Arial, helvetica, sans-serif;
    font-size: 12px;
    color: #003399;
}

</style>
</head>
<body>
<%
    String fileStr = request.getParameter("file");
    String[] files = fileStr.split(":") ;
    String curFile;
    if (files.length > 1) {
%>
<p>
<%
        for (String file : files) {
            curFile = file.replace('.', '/').concat(".xhtml");
%>
<a href="#" onclick="document.getElementById('jspTitle').innerHTML='<%=curFile%>';document.getElementById('source_frm').src='jsp-source/<%=file%>.xhtml.html';return false;"><%=curFile%></a>&nbsp;
<%            
        }
%>
</p><hr>
<%
    }
    curFile = files[0].replace('.', '/').concat(".xhtml");
%>
<label id="jspTitle"><%=curFile%></label>
<iframe id="source_frm" frameborder="no" style="width:100%;height:90%" src="jsp-source/<%=files[0]%>.xhtml.html"></iframe>
</body>
</html>