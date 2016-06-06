/**
 * Created by Simona on 6/4/2016.
 */
/**
 * Created by Simona on 6/1/2016.
 */
WPAngularStarter.controller('CategoriesController',
    ['$scope', 'RanggoService', '$location', '$http',
        function($scope, RanggoService, $location, $http) {
            $scope.categories = [];
            $scope.people = [];
            $scope.chosenCategories = [];

            $scope.hideButton = true;

            $scope.tempCategory = $location.search().category;
            $scope.hiddenSearchResult = true;

            $scope.searchResults = [];
            $scope.hiddenSearchError = true;

            $scope.hiddenError = true;

            if($scope.tempCategory != null && $scope.tempCategory!= ""){
                $scope.chosenCategories.push({value: $scope.tempCategory, display: $scope.tempCategory});
                $scope.tempCategory = "";
                $scope.hideButton = false;
            }

            if($scope.categories.length == 0){
                getCategories();
            }

            if($scope.people.length == 0){
                getPeople();
            }

            $scope.search = function(){
                if($scope.selectedItem != null && $scope.selectedItem != ""){
                    //napravi prebaruvanje po id
                    $location.path('/people/' + $scope.selectedItem.value);
                    $scope.selectedItem = "";
                    $scope.searchText = "";
                    $scope.hiddenError = true;
                } else {
                    $scope.selectedItem = "";
                    $scope.searchText = "";
                    $scope.hiddenError = false;
                }
            }

            $scope.addCategory = function(){
                if($scope.selectedItemCategory!=null && $scope.selectedItemCategory!=""){
                    var flag = true;
                    for(var i=0;i<$scope.chosenCategories.length;i++){
                        if($scope.chosenCategories[i].value == $scope.selectedItemCategory.value){
                            flag = false;
                            break;
                        }
                    }
                    if(flag){
                        $scope.chosenCategories.push($scope.selectedItemCategory);
                    }
                }
                $scope.selectedItemCategory = "";
                $scope.searchTextCategory = "";

                if($scope.chosenCategories.length > 0){
                    $scope.hideButton = false;
                } else {
                    $scope.hideButton = true;
                }
            }

            $scope.removeCategory = function(category){
                for(var i=$scope.chosenCategories.length-1;i>=0;i--){
                    if($scope.chosenCategories[i].display == category){
                        $scope.chosenCategories.splice(i,1);
                        break;
                    }
                }
                if($scope.chosenCategories.length > 0){
                    $scope.hideButton = false;
                } else {
                    $scope.hideButton = true;
                }
            }

            $scope.searchForCategories = function(){
                 $scope.searchResults = [];
                 for(var i=0;i<$scope.people.length;i++){
                     if($scope.people[i].categories){
                         var temp = 0;
                         var categories = $scope.people[i].categories;
                         for(var j=0;j<categories.length;j++){
                             for(var z=0;z<$scope.chosenCategories.length;z++){
                                 if($scope.chosenCategories[z].value == categories[j]){
                                     temp++;
                                 }
                             }
                         }
                         if(temp >= $scope.chosenCategories.length){
                             $scope.searchResults.push($scope.people[i]);
                         }
                     }
                 }
                 if($scope.searchResults.length > 0){
                     $scope.hiddenSearchError = true;
                     $scope.hiddenSearchResult = false;
                 } else {
                     $scope.hiddenSearchError = false;
                     $scope.hiddenSearchResult = true;
                 }
            }

            function getCategories(){
                RanggoService.getCategories().$promise.then(function(data){
                    $scope.categories = data;
                    $scope.statesCategory = loadStatesCategory();
                });
            }

            function getPeople(){
                //call angular service to connect with back end
                RanggoService.getPeople().$promise.then(function(data){
                    for(var i=0;i<data.length;i++){
                        var flag = false;
                        for(var j=0;j<$scope.people.length;j++){
                            if($scope.people[j].name == data[i].name){
                                if(!$scope.people[j].dbpediaUrl){
                                    if(data[i].dbpediaUrl){
                                        $scope.people[j] = data[i];
                                    }
                                }
                                flag = true;
                                break;
                            }
                        }

                        if(!flag){
                            $scope.people.push(data[i]);
                        }
                    }
                    $scope.states = loadStates();
                });
            }

            $scope.statesCategory = [];
            $scope.querySearchCategory = querySearchCategory;
            $scope.searchTextCategory = "";
            $scope.selectedItemCategory;

            function querySearchCategory(query) {
                var results = query ? $scope.statesCategory.filter( createFilterFor(query) ) : $scope.statesCategory, deferred;
                return results;
            }

            function loadStatesCategory() {
                var resultArr = [];
                for(var i=0;i<$scope.categories.length;i++){
                    resultArr.push({
                        value:  $scope.categories[i],
                        display: $scope.categories[i]
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

        }
    ]);