<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Rango</title>

<!-- Stylesheets -->
<link rel="stylesheet" href="app/styles/index.css"></link>

<!-- jQuery proper, Angular bootstraps it automatically -->
<script src="app/components/jquery.min.js"></script>

<!-- d3js -->
<script src="app/components/d3.min.js"></script>

<!-- AngularJS scripts -->
<script src="app/components/angular.min.js"></script>
<script src="app/components/angular-route.min.js"></script>
<script src="app/components/angular-resource.min.js"></script>
<script src="app/components/angular-ui-router.min.js"></script>
<script src="app/components/ui-router-styles.js"></script>

<!-- The definition and the configuration of the application module -->
<script src="app/scripts/app.js"></script>

<!-- Services -->
<!-- nothing here so far -->

<!-- Directives -->
<!-- nothing here so far -->

<!-- Routes -->
<script src="app/scripts/router.js"></script>

<!-- Controllers -->
<script src="app/scripts/controllers/first.controller.js"></script>
<script src="app/scripts/controllers/second.controller.js"></script>
<script src="app/scripts/controllers/third.controller.js"></script>

</head>
<body ng-app="ranggoApp">
	<div class="outer-container">
		<div class="navigation">
			<h1>Header</h1>
			<a ui-sref="first">First page</a> <a ui-sref="second">Second page</a> <a ui-sref="third">Third page</a>
		</div>
	    <div class="partial-page-container">
	        <div ui-view></div>
	    </div>
    </div>
</body>
</html>