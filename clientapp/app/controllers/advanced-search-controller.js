
/**
 * Created by Simona on 6/1/2016.
 */
WPAngularStarter.controller('AdvancedSearchController',
    ['$scope', 'RanggoService', '$location', '$http',
        function($scope, RanggoService, $location, $http) {
            $scope.dateFrom = "";
            $scope.dateTo = "";
            $scope.title = "";
            $scope.keywords = [];
            $scope.keyword = "";
            $scope.selectedPeople = [];

            $scope.people = [];

            $scope.news = [];

            $scope.hideError = true;

            if($scope.people.length == 0){
                getPeople();
            }

            $scope.addKeyword = function(){
                if($scope.keyword!="" && $scope.keyword!=null){
                    if($.inArray($scope.keyword, $scope.keywords)==-1){
                        $scope.keywords.push($scope.keyword);
                    }
                }
                $scope.keyword = "";
            }

            $scope.deleteKeyword = function(index){
                $scope.keywords.splice(index,1);
            }

            $scope.addPerson = function(){
                if($scope.selectedItem != null && $scope.selectedItem != ""){
                    if($.inArray($scope.selectedItem, $scope.selectedPeople)){
                        $scope.selectedPeople.push($scope.selectedItem);
                    }
                }
                $scope.selectedItem = "";
                $scope.searchText = "";
            }

            $scope.deletePerson = function(index){
                $scope.selectedPeople.splice(index,1);
            }

            $scope.reset = function(){
                reset();
            }

            function reset(){
                $scope.dateFrom = "";
                $scope.dateTo = "";
                $scope.title = "";
                $scope.keywords = [];
                $scope.keyword = "";
                $scope.selectedPeople = [];
            }

            $scope.search = function(){
                var arr = [];
                for(var i=0;i<$scope.selectedPeople.length;i++){
                    arr.push($scope.selectedPeople[i].value);
                }

                $scope.searchObject = {
                    dateFrom: getDateFormat($scope.dateFrom),
                    dateTo: getDateFormat($scope.dateTo),
                    title: $scope.title,
                    keywords: $scope.keywords,
                    people: arr
                };


                $http.post('http://localhost:8080/ranggo/filtered-contents', $scope.searchObject).then(function(data){
                    //success
                    $scope.news = data.data;
                    console.log(data);
                    if($scope.news.length == 0){
                        reset();
                        $scope.hideError = false;
                    } else{
                        $scope.hideError = true;
                    }

                }, function(data){
                    //failure
                });

            }

            function getDateFormat(date){
                if(date == "" || date == null){
                    return "";
                }

                var day = date.getDate();
                var month = date.getMonth()+1;
                var year = date.getFullYear();

                if(day < 10){
                    day = "0" + day;
                }

                if(month < 10){
                    month = "0" + month;
                }

                var result = day + "." + month + "." + year;
                return result;
            }

            function getPeople(){
                    RanggoService.getPeople().$promise.then(function(data){
                        $scope.people = [];
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