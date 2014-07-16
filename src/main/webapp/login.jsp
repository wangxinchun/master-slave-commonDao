<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
<%
  System.out.println("nnnn");
%>
   <form method="post" action="/login.do">
      <table>
          <tr>
             <td>username</td><td>
              <input type="text" name="username" id="password"/>
             </td>
          </tr>
          <tr>
             <td>password</td><td>
              <input type="text" name="password" id="password"/>
             </td>
          </tr>
          <tr>
             <td colspan="2">
               <input type="submit" value="submit"/>
             </td>
          </tr>
      </table>
   </form>
</body>
</html>