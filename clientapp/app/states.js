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

    $stateProvider.state('root.newsfeed', {
      url: 'newsfeed',
      views: {
        'main@': {
          templateUrl: "views/main/news/newsFeed.html",
          controller: "NewsFeedController"
        }
      }
    });

    $stateProvider.state('root.newsarchive', {
      url: 'newsarchive/:date',
      views: {
        'main@': {
          templateUrl: "views/main/news/newsArchive.html",
          controller: "NewsArchiveController"
        }
      }
    });

    $stateProvider.state('root.news', {
      url: 'news/:id',
      views: {
        'main@': {
          templateUrl: "views/main/news/news.html",
          controller: "NewsController"
        }
      }
    });

    $stateProvider.state('root.advancedSearch', {
      url: 'advancedsearch',
      views: {
        'main@': {
          templateUrl: "views/main/news/advancedSearch.html",
          controller: "AdvancedSearchController"
        }
      }
    });

  }])
  .run([
    '$rootScope',
    function($rootScope) {
      $rootScope.menuItems = [{
        state: 'root.categories',
        icon: 'glyphicon glyphicon-search',
        name: 'FIND PEOPLE'
      },{
        state: 'root.comparepeople',
        icon: 'glyphicon glyphicon-sort',
        name: 'COMPARE PEOPLE'
      }, {
        state: 'root.newsfeed',
        icon: 'glyphicon glyphicon-th-list',
        name: 'NEWS FEED'
      },{
        state: 'root.advancedSearch',
        icon: 'glyphicon glyphicon-search',
        name: 'ADVANCED SEARCH'
      }];

    }]);