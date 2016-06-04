/**
 * Secured states
 * All inherit from root (which has data : secured)
 */
WPAngularStarter.config([
  '$stateProvider',
  '$urlRouterProvider',
  function($stateProvider, $urlRouterProvider) {

    $urlRouterProvider.otherwise('/');

    $stateProvider.state('root', {
      url: '/',
      views: {
        navbar: {
          templateUrl: 'views/main/navbar.html'
        },
        main: {
          templateUrl: 'views/main/main.html',
          controller: "FirstPageController"
        },
        footer:{
          templateUrl: 'views/main/footer.html'
        }
      }
    });

  }])
  .run([
    '$rootScope',
    function($rootScope) {

      $rootScope.menuItems = [{
        state: 'root',
        icon: 'fa-home',
        name: 'HOME'
      }];

    }]);