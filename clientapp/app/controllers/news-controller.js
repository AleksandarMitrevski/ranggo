/**
 * Created by Simona on 6/1/2016.
 */
WPAngularStarter.controller('NewsController',
    ['$scope', 'RanggoService', '$location', '$http', '$stateParams', '$anchorScroll',
        function($scope, RanggoService, $location, $http, $stateParams, $anchorScroll) {
            $scope.newsID = $stateParams.id;
            $scope.news = {};

            $scope.error = false;

            $scope.taxonomies = [];

            $scope.hidePeople = false;

            $scope.similarNews = [];

            $scope.selectHandler = function(item){
                var selectedRow = item.row;
                var row = $scope.peopleChart.data[selectedRow+1];
                var name = row[0];
                for(var i=0;i<$scope.news.personEntities.length;i++){
                    var entity = $scope.news.personEntities[i];
                    if(entity.person && entity.person.id){
                        if(entity.person.name == name){
                            var id = entity.person.id;
                            $location.path("/people/" + id);
                            break;
                        }
                    }
                }
            }

            if($scope.newsID != null && $scope.newsID != ""){
                getContent();
            }

            function getContent(){
                RanggoService.getContentByID({id: $scope.newsID}).$promise.then(function(data){
                    if(data.id){
                        $scope.news = data;
                        $scope.error = false;
                        getSimilarNews();
                        for(var i=0;i<$scope.news.taxonomies.length;i++){
                            var taxonomy = $scope.news.taxonomies[i];
                            for(var j=0;j<taxonomy.labels.length;j++){
                                if($.inArray(taxonomy.labels[j], $scope.taxonomies) == -1){
                                    $scope.taxonomies.push(taxonomy.labels[j]);
                                }
                            }
                        }
                        drawConceptsChart();
                        drawPeopleChart();
                    } else {
                        $scope.news = {};
                        $scope.error = true;
                    }
                });
            }

            function getSimilarNews(){
                RanggoService.getSimilarContents({id: $scope.news.id}).$promise.then(function(data){
                    $scope.similarNews = data;
                })
            }

            function drawPeopleChart(){
                var data = getPeopleData();
                if(data.length == 1){
                    $scope.hidePeople = true;
                    return;
                }

                $scope.hidePeople = false;

                var title = "People mentioned in the text and their scores";

                $scope.peopleChart = {};
                $scope.peopleChart.type = "BubbleChart";
                $scope.peopleChart.data = data;

                $scope.peopleChart.options = {
                    title: title,
                    animation:{
                        startup: true,
                        duration: 1000
                    },
                    pointSize: 10,
                    legend:{position: 'left', width: '200px', height: '100px'},
                    colors:  ['#04CB78', '#FF4081'],
                    tooltip: {
                        isHtml: true
                    },
                    hAxis: {title: 'Score', minValue: -1, maxValue: 1},
                    vAxis: {title: 'Relevance', minValue: 0, maxValue: 1},
                    bubble: {textStyle: {fontSize: 11, color:'black', auraColor:'none', bold: true}, opacity:1}
                };

            }

            function getPeopleData(){
                var arr = [];

                arr.push(['Person name', 'Score', 'Relevance']);

                for(var i=0;i<$scope.news.personEntities.length;i++){
                    var tmp = $scope.news.personEntities[i];
                    if(tmp.person && tmp.person.id){
                        var score = tmp.score ? tmp.score : 0;
                        var relevance = tmp.relevance ? tmp.relevance : 0;
                        var personName = tmp.person.name;
                        var personID = tmp.person.id;

                        arr.push([personName, score, relevance]);
                    }
                }

                return arr;
            }

            function drawConceptsChart(){
                var data = getConceptsData();

                $scope.conceptChart = {};
                $scope.conceptChart.type = "TreeMap";
                $scope.conceptChart.data = data;

                $scope.conceptChart.options = {
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
                    },
                    minColor: '#04CB78',
                    midColor: '#ddd',
                    maxColor: '#FF4081',
                    fontColor: 'black'
                };
            }

            function getConceptsData(){
                var arr = [["Concept", "Parent", "Relevance", "Relevance color"]];

                arr.push(["Concepts", null, 0, 0]);

                for(var i=0;i<$scope.news.concepts.length;i++){
                    var concept = $scope.news.concepts[i];
                    arr.push([concept.text, 'Concepts', concept.relevance, 100*concept.relevance]);
                }

                return arr;
            }


            $scope.gotoAnchor = function(x) {
                var newHash = x;
                if ($location.hash() !== newHash) {
                    // set the $location.hash to `newHash` and
                    // $anchorScroll will automatically scroll to it
                    $location.hash(x);
                } else {
                    // call $anchorScroll() explicitly,
                    // since $location.hash hasn't changed
                    $anchorScroll();
                }
            };


        }
    ]);