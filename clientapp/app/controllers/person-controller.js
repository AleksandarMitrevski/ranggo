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
        }
    ]);