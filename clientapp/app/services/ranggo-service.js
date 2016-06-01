/**
 * Created by Simona on 6/1/2016.
 */
WPAngularStarter.factory('RanggoService', [
    '$resource',
    function($resource) {
        var root = "http://localhost:8080/ranggo";
        return $resource('/ranggo', {}, {
            getContents: {
                url: root + '/contents',
                method: 'GET',
                isArray: true
            },
            getPeople:{
                url: root + '/persons',
                method: 'GET',
                isArray: true
            },
            getPersonAverageRating:{
                url: root + '/averageRatings/:id',
                method: 'GET',
                isArray: true
            }
        });
    }
]);