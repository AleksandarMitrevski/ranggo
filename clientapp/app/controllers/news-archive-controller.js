/**
 * Created by Simona on 6/7/2016.
 */

/**
 * Created by Simona on 6/1/2016.
 */
WPAngularStarter.controller('NewsArchiveController',
    ['$scope', 'RanggoService', '$location', '$http', '$cookies', '$stateParams',
        function($scope, RanggoService, $location, $http, $cookies, $stateParams) {
            $scope.thisDate = $stateParams.date;

            $scope.macedonianSources = [];
            $scope.englishSources = [];

            $scope.selectedMacedonian = true;
            $scope.selectedEnglish = true;

            $scope.cookies = [];

            $scope.myDate = new Date();

            $scope.news = [];
            $scope.newsGroupedBySources = [];

            if($scope.news.length == 0){
                getNews();
            }

            $scope.switchDate = function(){
                if(getDateFormat($scope.myDate) != $scope.thisDate){
                    $location.path("/newsarchive/" + getDateFormat($scope.myDate));
                }
            }


            function getDateFormat(date){
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

            function getNews(){
                var result = $scope.thisDate;
                var cookies = [];
                if($cookies.getObject("preferences")){
                    var tmp = $cookies.getObject("preferences");
                    for(var i=0;i<tmp.length;i++){
                        cookies.push(tmp[i].name);
                    }
                }

                if(cookies.length == 0){
                    for(var i=0;i<$scope.macedonianSources.length;i++){
                        cookies.push($scope.macedonianSources[i].name);
                    }
                    for(var i=0;i<$scope.englishSources.length;i++){
                        cookies.push($scope.englishSources[i].name);
                    }
                }

                RanggoService.getNewsByDateAndPreferences({date: result, preferences: cookies}).$promise.then(function(data){
                    $scope.news = data;
                    $scope.newsGroupedBySources = [];
                    for(var i=0;i<$scope.news.length;i++){

                        var flag = true;
                        for(var j=0;j<$scope.newsGroupedBySources.length;j++){
                            if($scope.newsGroupedBySources[j].source == $scope.news[i].type){
                                $scope.newsGroupedBySources[j].contents.push($scope.news[i]);
                                flag = false;
                                break;
                            }
                        }

                        if(flag){
                            var result = [];
                            result.push($scope.news[i]);
                            $scope.newsGroupedBySources.push({source: $scope.news[i].type, contents: result});
                        }

                    }
                });
            }

            if($scope.macedonianSources.length == 0 && $scope.englishSources.length == 0){
                getSources();
            }

            function getSources(){
                RanggoService.getSources().$promise.then(function(data){
                    $scope.cookies = [];
                    if($cookies.getObject("preferences")){
                        $scope.cookies = $cookies.getObject("preferences");
                        if($scope.cookies.length == 0 || $scope.cookies.length == 13){
                            $scope.selectedMacedonian = true;
                            $scope.selectedEnglish = true;
                        } else {
                            $scope.selectedMacedonian = false;
                            $scope.selectedEnglish = false;
                        }
                    } else {
                        $scope.selectedMacedonian = true;
                        $scope.selectedEnglish = true;
                    }

                    var countMacedonian = 0;
                    var countEnglish = 0;
                    var maxMacedonian = 11;
                    var maxEnglish = 2;
                    for(var i=0;i<data.length;i++){
                        if(data[i].country == "mk"){
                            var checked = $scope.selectedMacedonian;
                            for(var j=0;j<$scope.cookies.length;j++){
                                if($scope.cookies[j].name == data[i].name){
                                    countMacedonian++;
                                    checked = true;
                                    break;
                                }
                            }
                            $scope.macedonianSources.push({name: data[i].name, url: data[i].url, checked: checked});
                        } else {
                            var checked = $scope.selectedEnglish;
                            for(var j=0;j<$scope.cookies.length;j++){
                                if($scope.cookies[j].name == data[i].name){
                                    countEnglish++;
                                    checked = true;
                                    break;
                                }
                            }
                            $scope.englishSources.push({name: data[i].name, url: data[i].url, checked: checked});
                        }
                    }

                    if(countMacedonian == maxMacedonian){
                        $scope.selectedMacedonian = true;
                    }
                    if(countEnglish == maxEnglish){
                        $scope.selectedEnglish = true;
                    }
                });
            }



            $scope.clickCheckbox = function(event, source){
                if(source == 'mk'){
                    $scope.selectedMacedonian = !$scope.selectedMacedonian;
                } else if(source == 'en'){
                    $scope.selectedEnglish = !$scope.selectedEnglish;
                }
                fillInitialCheckboxes();
            }

            $scope.check = function(name, source){
                if(source == 'mk'){
                    for(var i=0;i<$scope.macedonianSources.length;i++){
                        if($scope.macedonianSources[i].name == name){
                            $scope.macedonianSources[i].checked = !$scope.macedonianSources[i].checked;
                            break;
                        }
                    }
                } else {
                    for(var i=0;i<$scope.englishSources.length;i++){
                        if($scope.englishSources[i].name == name){
                            $scope.englishSources[i].checked = !$scope.englishSources[i].checked;
                            break;
                        }
                    }
                }
            }

            $scope.savePreferences = function(){
                var preferences = [];

                for(var i=0;i<$scope.macedonianSources.length;i++){
                    if($scope.macedonianSources[i].checked){
                        preferences.push({"name": $scope.macedonianSources[i].name});
                    }
                }

                for(var i=0;i<$scope.englishSources.length;i++){
                    if($scope.englishSources[i].checked){
                        preferences.push({"name": $scope.englishSources[i].name});
                    }
                }

                $cookies.remove("preferences");
                $cookies.putObject("preferences", preferences);

                var result = document.getElementById("settings");
                var wrappedResult = angular.element(result);
                wrappedResult.removeClass('in');

                getNews();
            }

            function fillInitialCheckboxes(){
                for(var i=0;i<$scope.macedonianSources.length;i++){
                    $scope.macedonianSources[i].checked = $scope.selectedMacedonian || $scope.macedonianSources[i].checked;
                }

                for(var i=0;i<$scope.englishSources.length;i++){
                    $scope.englishSources[i].checked = $scope.selectedEnglish || $scope.englishSources[i].checked;
                }
            }

        }
    ]);