
/**
 * @ngdoc here we are configuring the module exposed through the FirstApp
 *        variable. The method receives an array that has a function as a last
 *        argument. Here, the angular inject the dependencies defined as strings
 *        in the array to the corresponding elements in the function. <br/> The
 *        $routeProvider is used to configure the routes. It maps templateUrl
 *        and optionally a controller to a given path. This is used by the
 *        ng-view directive. It replaces the content of the defining element
 *        with the content of the templateUrl, and connects it to the controller
 *        through the $scope.
 * @see https://docs.angularjs.org/guide/di
 */
RanggoApp.config([ '$stateProvider', '$urlRouterProvider',
  function($stateProvider, $urlRouterProvider) {
	
	$stateProvider

    .state('first', {
	  url: "/",
      templateUrl: "app/views/first.html",
      controller: "FirstCtrl",
      data: {
		  css: ["app/styles/first.css"]
	  }
    })
    
    .state('second', {
      url: "/second",
      templateUrl: "app/views/second.html",
	  controller: "SecondCtrl",
	  data: {
		  css: ["app/styles/second.css"]
	  }
    })
    
    .state('third', {
      url: "/third",
      templateUrl: "app/views/third.html",
      controller: "ThirdCtrl",
      data: {
    	  css: ["app/styles/third.css"]
      }
    });
	
	//if another url is in place, go to index
	$urlRouterProvider.otherwise('/');

}]);