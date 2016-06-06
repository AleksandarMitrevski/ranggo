/**
 * Created by Simona on 6/4/2016.
 */
/**
 * Created by Simona on 6/1/2016.
 */
WPAngularStarter.controller('PersonController',
    ['$scope', 'RanggoService', '$location', '$http', '$stateParams',
        function($scope, RanggoService, $location, $http, $stateParams) {
            $scope.personID = $stateParams.id;

            $scope.person = {};

            $scope.personRating = [];

            $scope.hiddenError = true;
            $scope.hiddenBody = false

            $scope.selectedDate = "";
            $scope.contentsForSelectedDate = [];

            $scope.totalRating = -2;

            if(!$scope.person.id){
                getPerson();
            }

            if($scope.personRating.length == 0){
                getPersonRating();
            }

            $scope.getRatingInArticle = function(articleID){
                for(var i=0;i<$scope.personRating.length;i++){
                    if($scope.personRating[i].id == articleID){
                        var rating = $scope.personRating[i];
                        var totalRating = 0;
                        var totalNumber = 0;

                        for(var j=0;j<rating.personEntities.length;j++){
                            if(rating.personEntities[j].person){
                                if(rating.personEntities[j].person.id == $scope.personID){
                                    if(rating.personEntities[j].score){
                                        totalRating += rating.personEntities[j].score;
                                        totalNumber++;
                                    }
                                }
                            }
                        }

                        for(var j=0;j<rating.keywords.length;j++){
                            if(rating.keywords[j].text == $scope.person.name){
                                if(rating.keywords[j].score){
                                    totalRating += rating.keywords[j].score;
                                    totalNumber++;
                                }
                            }
                        }
                        var result = (totalRating/totalNumber).toFixed(3);
                        if(isNaN(result)){
                            return 0.0;
                        }
                        return result;
                    }
                }
                return 0.0;
            }

            $scope.selectHandler = function(selectedItem){
                var selectedDate = $scope.lineChart.data.rows[selectedItem.row].c[0].v;
                $scope.selectedDate = selectedDate;
                getContentsForSelectedDate();
            }

            function getContentsForSelectedDate(){
                $scope.contentsForSelectedDate = [];
                for(var i=0;i<$scope.personRating.length;i++){
                    if($scope.personRating[i].timestamp == $scope.selectedDate){
                        $scope.contentsForSelectedDate.push($scope.personRating[i]);
                    }
                }

            }

            function getPerson(){
                RanggoService.getPersonInfo({id: $scope.personID}).$promise.then(function(data){
                    if(!data.id){
                        $scope.hiddenBody = true;
                        $scope.hiddenError = false;
                    } else {
                        $scope.person = data;
                        $scope.hiddenError = true;
                        $scope.hiddenBody = false;

                        if($scope.person.shortBio){
                            var end = $scope.person.shortBio.length > 370 ? 370 : $scope.person.shortBio.length;
                            $scope.person.shortBiography = $scope.person.shortBio.substr(0, end);

                            if(end < $scope.person.shortBio.length){
                                $scope.person.shortBiography += "...";
                            }
                        }

                    }
                });
            }

            function getPersonRating(){
                RanggoService.getPersonRating({id: $scope.personID}).$promise.then(function(data) {
                    $scope.personRating = data;
                    for (var i = 0; i < $scope.personRating.length; i++) {
                        $scope.personRating[i].timestamp = parseTimestamp($scope.personRating[i].timestamp);
                    }

                    $scope.personRating.sort(function (a, b) {
                        return compareTime(a.timestamp, b.timestamp);
                    });

                    drawChart();
                });
            }

            function drawChart(){
                var data = getDataForChart();
                var title = getTitleForChart();

                $scope.lineChart = {};
                $scope.lineChart.type = "LineChart";
                $scope.lineChart.data = {
                    "cols": [
                        {id: "t", label: "Drug Name", type: "string"},
                        {id: "Drug Price", label: "Rating", type: "number"},
                        {id: "Stack", label: "Not found", type: "number"},
                        {role: "style", type: "string"}
                    ],
                    "rows": data
                };
                $scope.lineChart.options = {
                    title: title,
                    animation:{
                        startup: true,
                        duration: 1000
                    },
                    pointSize: 10,
                    vAxis: {minValue:0},
                    legend:{position: 'left', width: '200px', height: '100px'},
                    colors:  ['#04CB78', '#d95f02', '#7570b3'],
                    tooltip: {
                        isHtml: true
                    }
                };
            }

            function getDataForChart(){
                var arr = [];
                for(var i=0;i<$scope.personRating.length;i++){
                    var rating = $scope.personRating[i];
                    var totalRating = 0;
                    var totalNumber = 0;

                    for(var j=0;j<rating.personEntities.length;j++){
                        if(rating.personEntities[j].person){
                            if(rating.personEntities[j].person.id == $scope.personID){
                                if(rating.personEntities[j].score){
                                    totalRating += rating.personEntities[j].score;
                                    totalNumber++;
                                }
                            }
                        }
                    }

                    for(var j=0;j<rating.keywords.length;j++){
                        if(rating.keywords[j].text == $scope.person.name){
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
                $scope.totalRating = 0;
                for(var i=0;i<arr.length;i++){
                    tempTotal += arr[i].totalNumber;
                    tempRating += arr[i].totalRating;
                    var total = arr[i].totalRating/arr[i].totalNumber;
                    if(isNaN(total)){
                        total = 0.0;
                    }
                    temp.push({date: arr[i].date, total: total});
                }

                $scope.totalRating = (tempRating/tempTotal).toFixed(3);

                var data1 = [];
                for(var k=0; k <temp.length;k++){
                    var par = {c: [ {v: temp[k].date}, {v: temp[k].total} ,{v: temp[k].color}]};
                    data1.push(par);
                }
                return data1;

            }

            function getTitleForChart(){
                return $scope.person.name + "'s rating";
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