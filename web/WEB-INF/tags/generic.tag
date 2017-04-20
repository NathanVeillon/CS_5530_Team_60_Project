<%@tag description="Overall Page template" pageEncoding="UTF-8"%>
<%@attribute name="header" fragment="true" %>
<%@attribute name="header-collapse" fragment="true" %>
<%@attribute name="footer" fragment="true" %>
<%@attribute name="title" fragment="true" %>

<html>
    <head>
        <meta charset="utf-8" />
        <title>U-otel</title>
        <link rel="shortcut icon" type="image/x-icon" href="${pageContext.request.contextPath}/resources/favicon.ico" />

        <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/vendor/bootstrap-3.3.7-dist/css/bootstrap.min.css" />
        <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/vendor/bootstrap-3.3.7-dist/css/bootstrap-theme.min.css" />
        <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/vendor/datatables/datatables.min.css" />
        <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/jumbotron-narrow.css" />
        <script src="${pageContext.request.contextPath}/resources/vendor/jquery/jquery-3.2.1.min.js"></script>
        <script src="${pageContext.request.contextPath}/resources/vendor/bootstrap-3.3.7-dist/js/bootstrap.min.js"></script>
        <script src="${pageContext.request.contextPath}/resources/vendor/datatables/datatables.min.js"></script>
        <script src="${pageContext.request.contextPath}/resources/js/db-uotel.js"></script>

        <style>

            .page-title{
                margin-bottom: 15px;
            }
        </style>
    </head>
    <body>
        <div class="container">
            <nav class="nav clearfix">
                <div class="container-fluid">
                    <!-- Brand and toggle get grouped for better mobile display -->
                    <div class="navbar-header">
                        <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#nav-collapse-1" aria-expanded="false">
                            <span class="sr-only">Toggle navigation</span>
                            <span class="icon-bar"></span>
                            <span class="icon-bar"></span>
                            <span class="icon-bar"></span>
                        </button>
                        <a class="navbar-brand" href="${pageContext.request.contextPath}\">U-otel</a>
                        <jsp:invoke fragment="header"/>
                    </div>


                    <!-- Collect the nav links, forms, and other content for toggling -->
                    <div class="collapse navbar-collapse" id="nav-collapse-1">
                        <jsp:invoke fragment="header-collapse"/>
                    </div>

                </div><!-- /.container-fluid -->
            </nav>
            <hr style="margin-top: 0"/>
            <div class="page-title">
                <h3><jsp:invoke fragment="title"/></h3>
            </div>
            <div id="body">
                <jsp:doBody/>
            </div>
            <footer class="footer" id="pagefooter">
                <jsp:invoke fragment="footer"/>
            </footer>
        </div>
    </body>
</html>