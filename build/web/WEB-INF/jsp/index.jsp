<%-- 
    Document   : index
    Created on : Mar 30, 2015, 1:51:45 PM
    Author     : SYUS
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->
    <meta name="description" content="">
    <meta name="author" content="">
    <link rel="icon" href="../../favicon.ico">

    <title><spring:message code="messages.index.title"/></title>

    <!-- Latest compiled and minified CSS -->
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap.min.css">

    <!-- Optional theme -->
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap-theme.min.css">

    <!-- Latest compiled and minified JavaScript -->
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/js/bootstrap.min.js"></script>
   
    <!-- Bootstrap core CSS -->
    <link href="../css/bootstrap-theme.min.css" rel="stylesheet">

    <!-- Custom styles for this template -->
    <link href="../css/signin.css" rel="stylesheet">

    <!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
    <!--[if lt IE 9]>
      <script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
      <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
    <![endif]-->
  </head>

  <body>
    <%@include file="header.jspf" %>
    
    <div class="container">
        <div class="row">
            <form class="form-signin col-md-5 col-md-offset-3" action="${pageContext.request.contextPath}/overview.htm" method="POST" commandName="overview">
            
                <!-- Login info -->
                <h2 class="form-signin-heading"><spring:message code="messages.index.info"/></h2>

                <!-- error field -->
                <c:if test="${error ne null}">
                <div class="alert alert-danger" role="alert">
                    <span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true"></span>
                    <span class="sr-only">Error:</span>
                    <c:out value="${error}"></c:out>
                </div>
                </c:if>

                <!-- email -->
                <label for="email" class="sr-only"><spring:message code="messages.index.email"/></label>
                <input type="email" id="email" name="email" class="form-control" placeholder="Email address" autofocus>

                <!-- password -->
                <label for="password" class="sr-only"><spring:message code="messages.index.password"/></label>
                <input type="password" id="password" name="password" class="form-control" placeholder="Password">

            <input id="login" class="btn btn-lg btn-primary btn-block" type="submit" name="action" value="<spring:message code="messages.index.loginbutton"/>"/>
          </form>
          
          <form class="form-signin col-md-5 col-md-offset-3" action="${pageContext.request.contextPath}/showCurrencies.htm" method="POST" commandName="showCountries">
            <input class="btn btn-lg btn-info btn-block" type="submit" name="action" value="<spring:message code="messages.index.currenciesbutton"/>"/>
          </form>
        </div>
    </div> <!-- /container -->
    
    <c:if test="${currencies ne null}">
        <div class="container">
            <h1><spring:message code="messages.index.currencytitle"/></h1>
            <p><spring:message code="messages.index.currencyinfo"/></p>
            <table class="table table-hover">
                <thead>
                    <tr>
                        <th><spring:message code="messages.index.currencycode"/></th>
                        <th><spring:message code="messages.index.currencyname"/></th>
                        <th><spring:message code="messages.index.currencyrate"/></th>
                    </tr>
                </thead>
                <tbody>
                     <c:forEach var="currency" items="${currencies}">
                        <tr>
                            <td><c:out value="${currency.code}"/></td>
                            <td><c:out value="${currency.name}"/></td>
                            <td><c:out value="${currency.rate}"/></td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </c:if>
    
    
    <%@include file="footer.jspf" %>
    <!-- IE10 viewport hack for Surface/desktop Windows 8 bug -->
    <script src="../../assets/js/ie10-viewport-bug-workaround.js"></script>
  </body>
</html>
