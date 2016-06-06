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

    $stateProvider.state('root.categories', {
      url: 'categories',
      views: {
        'main@': {
          templateUrl: "views/main/people/categories.html",
          controller: "CategoriesController"
        }
      }
    });

    $stateProvider.state('root.people', {
      url: 'people/:id',
      views: {
        'main@': {
          templateUrl: "views/main/people/personProfile.html",
          controller: "PersonController"
        }
      }
    });

    $stateProvider.state('root.comparepeople', {
      url: 'compare',
      views: {
        'main@': {
          templateUrl: "views/main/people/comparePeople.html",
          controller: "ComparePeopleController"
        }
      }
    });

  }])
  .run([
    '$rootScope',
    function($rootScope) {
      $rootScope.menuItems = [{
        state: 'root',
        icon: 'glyphicon glyphicon-home',
        name: 'HOME'
      },{
        state: 'root.categories',
        icon: 'glyphicon glyphicon-search',
        name: 'FIND PEOPLE'
      }];

    }]);