<!DOCTYPE html>
<html lang="en">
<head>
    <script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.5.8/angular.js"></script>
    <script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.5.8/angular-route.js"></script>
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
    <meta content='width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0' name='viewport' />
    <script>


        var app = angular.module("myApp", ["ngRoute"]);
        //$username = '';
        app.config(function($routeProvider) {
            $routeProvider
                .when("/", {
                    templateUrl : "main1.html",
                    controller: function ($scope, $rootScope, $location, $routeParams, $interval, $http)
                    {
                        $scope.counter = 0;
                        $scope.result = [];
                        $scope.nodes = [];
                        angular.element(document).ready(function () {
                            $scope.getnodes();
                        });
                        $scope.addmessage = function(view){
                            $scope.messagesend = $rootScope.username + ": " + $scope.message;

                            $http.post("http://localhost:3000/api/addmessage",
                                { 'name': $rootScope.username, 'message': $scope.message }
                            ).success(function (res) {
                            });
                        }
                         $scope.remove = function(id) {
                            $http.post("http://michielserver.com/IOT/remove.php",
                                { 'id': id }
                            ).success(function (res) {
                                $scope.counter = id;
                            });
                        };
                        $scope.addnode = function(DeviceName, Message) {
                            $http.post("http://michielserver.com/IOT/addnode.php",
                                { 'DeviceName': DeviceName, 'Message' : Message }
                            ).success(function (res) {
                            });
                        };

                        $interval(function(){ $scope.interval(); }, 1000);                      
                        $scope.interval = function(view){
                            $scope.getnodes();

                        };
                        $scope.getnodes = function(){
                            $http.get("http://michielserver.com/IOT/api/getnodes.php").success(function (res) {
                                for(var i = 0; i < res.length; i++){
                                    $scope.nodes = res;
                                } 
                            });
                        }
                    }
                })
        });
    </script>
    <meta charset="UTF-8">
    <title>Title</title>
</head>
<body ng-app="myApp">
<div ng-view></div>
</body>
</html>