angular.module('hello', [])
  .controller('home', function($http) {
  var self = this;
  $http.get('user/Janez').then(function(response) {
    self.user = response.data;
  })
});