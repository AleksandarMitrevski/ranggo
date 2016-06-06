/**
 * Created by Simona on 6/5/2016.
 */
/**
 * Created by Simona on 6/4/2016.
 */
/**
 * Created by Simona on 6/1/2016.
 */
WPAngularStarter.controller('ComparePeopleController',
    ['$scope', 'RanggoService', '$location', '$http',
        function($scope, RanggoService, $location, $http) {

            $scope.people = [];

            $scope.person1 = {};
            $scope.person2 = {};

            $scope.person1Rating = [];
            $scope.person2Rating = [];

            $scope.totalRating1 = 0;
            $scope.totalArticles1 = 0;
            $scope.totalRating2 = 0;
            $scope.totalArticles2 = 0;

            $scope.errorMessage1 = "";
            $scope.errorMessage2 = "";


            if($scope.people.length == 0){
                getPeople();
            }

            $scope.chooseNew = function(){
                $scope.person1 = {};
                $scope.person2 = {};
            }

            $scope.compare = function(){
                if($scope.selectedItem == null || $scope.selectedItem == ""){
                    $scope.errorMessage1 = "You have selected a resource that doesn't exist. Please adjust your parameters and try again.";
                    $scope.person1 = {};
                    $scope.selectedItem = "";
                    $scope.searchText = "";
                } else {
                    for(var i=0;i<$scope.people.length;i++){
                        if($scope.people[i].id == $scope.selectedItem.value){
                            $scope.person1 = $scope.people[i];
                        }
                    }
                }


                if($scope.selectedItem2 == null || $scope.selectedItem2 == ""){
                    $scope.errorMessage2 = "You have selected a resource that doesn't exist. Please adjust your parameters and try again.";
                    $scope.person2 = {};
                    $scope.selectedItem2 = "";
                    $scope.searchText2 = "";
                } else {
                    for(var i=0;i<$scope.people.length;i++){
                        if($scope.people[i].id == $scope.selectedItem2.value){
                            $scope.person2 = $scope.people[i];
                        }
                    }
                }

                if($scope.person1.id == $scope.person2.id){
                    $scope.errorMessage2 = "You cannot compare with the same person. Please choose another person";
                    $scope.person2 = {};
                    $scope.selectedItem2 = "";
                    $scope.searchText2 = "";
                }

                if($scope.errorMessage2 == "" && $scope.errorMessage1 == ""){
                    $scope.selectedItem = "";
                    $scope.searchText = "";
                    $scope.selectedItem2 = "";
                    $scope.searchText2 = "";
                    getPerson1Ratings();
                }

            }

            function getPerson1Ratings(){
                RanggoService.getPersonRating({id: $scope.person1.id}).$promise.then(function(data) {
                    $scope.person1Rating = data;
                    for (var i = 0; i < $scope.person1Rating.length; i++) {
                        $scope.person1Rating[i].timestamp = parseTimestamp($scope.person1Rating[i].timestamp);
                    }

                    $scope.person1Rating.sort(function (a, b) {
                        return compareTime(a.timestamp, b.timestamp);
                    });

                    getPerson2Ratings();
                });
            }

            function getPerson2Ratings(){
                RanggoService.getPersonRating({id: $scope.person2.id}).$promise.then(function(data) {
                    $scope.person2Rating = data;
                    for (var i = 0; i < $scope.person2Rating.length; i++) {
                        $scope.person2Rating[i].timestamp = parseTimestamp($scope.person2Rating[i].timestamp);
                    }

                    $scope.person2Rating.sort(function (a, b) {
                        return compareTime(a.timestamp, b.timestamp);
                    });
                    drawChart();
                });
            }

            function drawChart(){
                var data = getData();
                var title = getTitle();

                $scope.lineChart = {};
                $scope.lineChart.type = "LineChart";
                $scope.lineChart.data = data;
                $scope.lineChart.options = {
                    title: title,
                    defaultSeriesType: 'line',
                    interpolateNulls: true,
                    animation:{
                        startup: true,
                        duration: 1000
                    },
                    pointSize: 10,
                    vAxis: {minValue:0},
                    legend:{position: 'right', width: '200px', height: '100px'},
                    colors:  ['#1b9e77', '#d95f02', '#7570b3'],
                    tooltip: {
                        isHtml: true
                    }
                };
            }

            function getData(){
                var data1 = getPersonData(1);
                var data2 = getPersonData(2);

                var tempArray = [];
                for(var i=0;i<data1.length;i++){
                    tempArray.push({date: data1[i].date, person1Rating: data1[i].total, person2Rating: NaN});
                }

                for(var j=0;j<data2.length;j++){
                    var flag = true;
                    for(var i=0;i<tempArray.length;i++){
                        if(tempArray[i].date == data2[j].date){
                            tempArray[i].person2Rating = data2[j].total;
                            flag = false;
                            break;
                        }
                    }
                    if(flag){
                        tempArray.push({date: data2[j].date, person1Rating: NaN, person2Rating: data2[j].total});
                    }
                }

                tempArray.sort(function(a, b){
                   return compareTime(a.date, b.date);
                });

                var result = [['Date', $scope.person1.name + "'s rating", $scope.person2.name + "'s rating"]];
                for(var i=0;i<tempArray.length;i++){
                    var rating1 = tempArray[i].person1Rating;
                    var rating2 = tempArray[i].person2Rating;
                    if(isNaN(rating1)){
                        rating1 = null;
                    }
                    if(isNaN(rating2)){
                        rating2 = null;
                    }
                    result.push([tempArray[i].date, rating1, rating2]);
                }
                console.log(result);
                return result;
            }

            function getTitle(){
                return "Comparison of the ratings of " + $scope.person1.name + " and " + $scope.person2.name;
            }

            function getPersonData(person){
                var array = [];
                var personID = 0;
                var personName = "";
                if(person == 1){
                    personID = $scope.person1.id;
                    personName = $scope.person1.name;
                    array = $scope.person1Rating;
                } else{
                    array = $scope.person2Rating;
                    personID = $scope.person2.id;
                    personName = $scope.person2.name;
                }

                var arr = [];
                for(var i=0;i<array.length;i++){
                    var rating = array[i];
                    var totalRating = 0;
                    var totalNumber = 0;

                    for(var j=0;j<rating.personEntities.length;j++){
                        if(rating.personEntities[j].person){
                            if(rating.personEntities[j].person.id == personID){
                                if(rating.personEntities[j].score){
                                    totalRating += rating.personEntities[j].score;
                                    totalNumber++;
                                }
                            }
                        }
                    }

                    for(var j=0;j<rating.keywords.length;j++){
                        if(rating.keywords[j].text == personName){
                            if(rating.keywords[j].score){
                                totalRating += rating.keywords[j].score;
                                totalNumber++;
                            }
                        }
                    }

                    var flag = true;
                    for(var j=0;j<arr.length;j++){
                        if(arr[j].date == rating.timestamp){
                            flag = false;
                            arr[j].totalRating = arr[j].totalRating + totalRating;
                            arr[j].totalNumber = arr[j].totalNumber + totalNumber;
                            break;
                        }
                    }

                    if(flag){
                        arr.push({date: rating.timestamp, totalRating: totalRating, totalNumber: totalNumber});
                    }
                }

                var temp = [];
                var tempTotal = 0;
                var tempRating = 0;
                for(var i=0;i<arr.length;i++){
                    tempTotal += arr[i].totalNumber;
                    tempRating += arr[i].totalRating;
                    var total = arr[i].totalRating/arr[i].totalNumber;
                    if(isNaN(total)){
                        total = 0.0;
                    }
                    temp.push({date: arr[i].date, total: total});
                }

                if(person == 1){
                    $scope.totalRating1 = (tempRating/tempTotal).toFixed(3);
                    $scope.totalArticles1 = array.length;
                } else {
                    $scope.totalRating2 = (tempRating/tempTotal).toFixed(3);
                    $scope.totalArticles2 = array.length;
                }

                return temp;
            }

            function getPeople(){
                RanggoService.getPeople().$promise.then(function(data){
                    for(var i=0;i<data.length;i++){
                        var flag = false;
                        //avoid duplicate names
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
                    $scope.states2 = loadStates();

                    $scope.tempPerson = $location.search().person;
                    console.log($scope.tempPerson);
                    if($scope.tempPerson!=null && $scope.tempPerson!=""){
                        for(var i=0;i<$scope.people.length;i++){
                            if($scope.people[i].id == $scope.tempPerson){
                                $scope.person1 = $scope.people[i];
                                $scope.searchText = $scope.person1.name;
                                $scope.selectedItem = {value: $scope.person1.id, display: $scope.person1.name};
                                break;
                            }
                        }
                    }
                });
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

            $scope.states2 = [];
            $scope.querySearch2   = querySearch2;
            $scope.searchText2 = "";
            $scope.selectedItem2;

            function querySearch2 (query) {
                var results = query ? $scope.states2.filter( createFilterFor(query) ) : $scope.states2, deferred;
                return results;
            }

            function compareTime(time1, time2){
                var first = time1.split(".");
                var second = time2.split(".");

                var firstDay = parseInt(first[0]);
                var firstMonth = parseInt(first[1]);
                var firstYear = parseInt(first[2]);

                var secondDay = parseInt(second[0]);
                var secondMonth = parseInt(second[1]);
                var secondYear = parseInt(second[2]);

                if(firstYear < secondYear){
                    return -1;
                } else if(firstYear > secondYear){
                    return 1;
                } else {
                    if(firstMonth < secondMonth){
                        return -1;
                    } else if(firstMonth > secondMonth){
                        return 1;
                    } else {
                        if(firstDay < secondDay){
                            return -1;
                        } else if(firstDay > secondDay){
                            return 1;
                        } else {
                            return 0;
                        }
                    }
                }

            }

            function parseTimestamp(timestamp){
                var months = [
                    {name:"Jan", value: '01'},
                    {name:"Feb", value: '02'},
                    {name:"Mar", value: '03'},
                    {name:"Apr", value: '04'},
                    {name:"May", value: '05'},
                    {name:"Jun", value: '06'},
                    {name:"Jul", value: '07'},
                    {name:"Aug", value: '08'},
                    {name:"Sep", value: '09'},
                    {name:"Oct", value: '10'},
                    {name:"Nov", value: '11'},
                    {name:"Dec", value: '12'}
                ];

                var substring = timestamp.substr(timestamp.indexOf(",")+2);
                var parts = substring.split(" ");
                var month = '00';
                for(var i=0;i<months.length;i++){
                    if(months[i].name == parts[1]){
                        month = months[i].value;
                    }
                }
                var result = parts[0] + "." + month + "." + parts[2];
                return result;
            }

        }
    ]);