/**
 * Created by Simona on 6/1/2016.
 */
WPAngularStarter.controller('FirstPageController',
    ['$scope', 'RanggoService', '$location', '$http',
        function($scope, RanggoService, $location, $http) {

            $scope.people = [];
            $scope.ratings = [];
            $scope.activeSlide = 1;

            //this list is not generated using an algorithm, it's merely filled
            //with the first 5 people from the response that have dbpedia url
            $scope.top5People = [];


            if($scope.people.length == 0){
                getPeople();
            }


            function getPeople(){
                //call angular service to connect with back end
                RanggoService.getPeople().$promise.then(function(data){
                    $scope.people = data;
                    var count = 0;
                    for(var i=0;i<$scope.people.length;i++){
                        if($scope.people[i].dbpediaUrl){
                            $scope.top5People[count++] = $scope.people[i];
                        }
                        if(count == 5){
                            break;
                        }
                    }
                    getRatingsForTop5People();
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
        }
    ]);