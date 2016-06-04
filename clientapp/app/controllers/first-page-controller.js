/**
 * Created by Simona on 6/1/2016.
 */
WPAngularStarter.controller('FirstPageController',
    ['$scope', 'RanggoService', '$location', '$http',
        function($scope, RanggoService, $location, $http) {

            $scope.people = [];
            $scope.ratings = [];
            $scope.top5People = [];
            $scope.categories = [];
            $scope.latestNews = [];

            $scope.hiddenError = true;
            $scope.activeSlide = 1;

            if($scope.people.length == 0){
                getPeople();
            }

            if($scope.categories.length == 0){
                getCategories();
            }

            if($scope.latestNews.length == 0){
                getLatestNews();
            }

            $scope.search = function(){
                if($scope.selectedItem != null && $scope.selectedItem != ""){
                    //napravi prebaruvanje po id
                    $scope.selectedItem = "";
                    $scope.searchText = "";
                    $scope.hiddenError = true;
                } else {
                    $scope.selectedItem = "";
                    $scope.searchText = "";
                    $scope.hiddenError = false;
                }
            }

            function getLatestNews(){
                RanggoService.getLatestNews().$promise.then(function(data){
                    for(var i=0;i<4;i++){
                        $scope.latestNews.push(data[i]);
                    }
                });
            }

            function getCategories(){
                RanggoService.getCategories().$promise.then(function(data){
                    var end = data.length > 20 ? 20 : data.length;
                    for(var i=0;i<end;i++){
                        $scope.categories.push(data[i]);
                    }

                });
            }

            function getPeople(){
                //call angular service to connect with back end
                RanggoService.getPeople().$promise.then(function(data){
                    $scope.people = [];

                    for(var i=0;i<data.length;i++){
                        var flag = false;
                        for(var j=0;j<$scope.people.length;j++){
                            if($scope.people[j].name == data[i].name){
                                flag = true;
                                break;
                            }
                        }

                        if(!flag){
                            $scope.people.push(data[i]);
                        }
                    }

                    var count = 0;
                    for(var i=0;i<$scope.people.length;i++){
                        if($scope.people[i].pictureUrl){
                            $scope.top5People[count++] = $scope.people[i];
                        }
                        if(count == 5){
                            break;
                        }
                    }
                    getRatingsForTop5People();
                    $scope.states = loadStates();
                });
            }

            function getRatingsForTop5People(){
                for(var i=0;i<$scope.top5People.length;i++){
                    var person = $scope.top5People[i];
                    RanggoService.getPersonAverageRating({id: person.id}).$promise.then(function(data){
                        $scope.ratings.push({id: person.id, rating: data[0]});
                    });
                }
            }

            $scope.states = [];
            $scope.querySearch   = querySearch;
            $scope.searchText = "";
            $scope.selectedItem;

            function querySearch (query) {
                var results = query ? $scope.states.filter( createFilterFor(query) ) : $scope.states, deferred;
                return results;
            }

            function loadStates() {
                var resultArr = [];
                for(var i=0;i<$scope.people.length;i++){
                    resultArr.push({
                        value:  $scope.people[i].id,
                        display: $scope.people[i].name
                    });
                }
                return resultArr;
            }

            function createFilterFor(query) {
                var lowercaseQuery = angular.lowercase(query);
                return function filterFn(state) {
                    return (angular.lowercase(state.display).indexOf(lowercaseQuery) === 0);
                };
            }
        }
    ]);