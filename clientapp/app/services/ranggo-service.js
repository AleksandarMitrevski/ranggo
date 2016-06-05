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
            },
            getCategories:{
                url: root + '/categories',
                method: 'GET',
                isArray: true
            },
            getLatestNews:{
                url: root + '/latest-news',
                method: 'GET',
                isArray: true
            }, getPersonInfo:{
                url: root + '/persons/:id',
                method: 'GET',
                isArray: false
            }, getPersonRating: {
                url: root + '/ratings/:id',
                method: 'GET',
                isArray: true
            }
        });
    }
]);