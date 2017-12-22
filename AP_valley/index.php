<!DOCTYPE html>
<html lang="en">
<head>
<script type="text/javascript" src="//code.jquery.com/jquery-2.1.1.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.19.4/moment.min.js"></script>
<script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.6.4/angular.js"></script>
<script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.6.4/angular-route.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datetimepicker/4.15.35/js/bootstrap-datetimepicker.min.js"></script>
    
<script type="text/javascript" src="node_modules/angular-bootstrap-datetimepicker/src/js/datetimepicker.js"></script>
<script type="text/javascript" src="node_modules/angular-bootstrap-datetimepicker/src/js/datetimepicker.templates.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/angular-google-chart/1.0.0-beta.1/ng-google-chart.js" type="text/javascript"></script>
	<meta content='width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0' name='viewport' />
	<script>
		var app = angular.module("myApp", ["ngRoute",'ui.bootstrap.datetimepicker', 'googlechart']);
		app.config(function($routeProvider)
		{
			$routeProvider
			.when("/", {
				templateUrl : "login.html",
				controller: function ($scope, $rootScope, $location, $routeParams, $interval, $http, $window)
				{
					$scope.login = function(){
						$http.post("http://michielserver.com/AP_valley/checklogin.php",
							{ 'Code': $scope.Code, 'Password': $scope.Password }).then(function (res) {
								$scope.test = res.data;
								if (res.data == 'valid ') 
								{
									console.log("Hey");
									$rootScope.userid = $scope.Code;
									$location.url('/main');
								}
								else
								{
									console.log($scope.test);
								}
							});
						}
					}
				})
			.when("/register", {
				templateUrl : "register.html",
				controller: function ($scope, $rootScope, $location, $routeParams, $interval, $http, $window)
				{
					$scope.register = function(){

						console.log("Hey");
						$http.post("http://michielserver.com/AP_valley/Adduser.php",
							{ 'firstname': $scope.firstname, 'lastname': $scope.lastname, 'userid' : $scope.userid}).then(function (res) {
								$scope.test = res.data;
							});
						}
					}
				})
			.when("/main", {
				templateUrl : "main.html",
				controller: function ($scope, $rootScope, $location, $routeParams, $interval, $http, $window)
				{
					$scope.title = "Timestamps";
					$scope.counter = 0;
					$scope.result = [];
					$scope.timestamps = [];
					$scope.oneday = 86400000;
					$scope.countperday = [0,0,0,0,0,0,0];
					$scope.getlast24hours;
					$scope.kindoftime = "d/M/yy h:mm a";
					$scope.amount = 1000;
					$scope.weekdays = ["Sunday", "Monday","Tuesday","Wednesday","Thursday","Friday", "Saturday"];  
					$scope.now = new Date();   
					$scope.url = "http://michielserver.com/AP_valley/Gettimestamps.php";
					$scope.gethistory = function(view)
					{
						$scope.kindoftime = "d/M/yy h:mm a";
						$scope.amount = 1000;
						$scope.showtable = true;
					};
					$scope.gettimestamps24 = function(view)
					{
						$scope.now = new Date();
						$scope.kindoftime = "H:mm:ss a";
						$scope.showtable = true;
						$scope.amount = 0;
						for(var j =0;j<$scope.timestamps.length;j++)
						{

							if($scope.timestamps[j].timestamp.getTime() > $scope.now.getTime() - $scope.oneday)
							{
								$scope.amount--;                              	
							}
						}
					};
					$scope.datechanged = function(){
						console.log($scope.datepicker);
						$scope.url = "http://michielserver.com/AP_valley/Gettimestamps.php";
						$scope.myChartObject = {};
						$scope.test = [];

							for(var j =0;j<$scope.timestamps.length;j++)
							{
								var datepicker = new Date($scope.datepicker);
								var timestamp = new Date($scope.timestamps[j].timestamp);
								if (datepicker.getDate() == timestamp.getDate() && datepicker.getMonth() == timestamp.getMonth() && datepicker.getYear() == timestamp.getYear()) {
									$scope.test.push({c: [{v: $scope.timestamps[j].timestamp.getHours() + ":" + $scope.timestamps[j].timestamp.getMinutes() + ":" + $scope.timestamps[j].timestamp.getSeconds() },{v: 1},{f:"hey"}]})
								}
							}
							//$scope.test = [{c: [{v: 'heey'},{v: 8}]},{c: [{v: 'heey'},{v: 6}]}];

							$scope.myChartObject.type = "ColumnChart";
							$scope.myChartObject.data = {"cols": [
							{id: "d", label: "Days", type: "string"},
							{id: "a", label: "Amount", type: "number"}
							], "rows": $scope.test};
							$scope.myChartObject.options = {
								title: 'Doorbell activity',
								titleTextStyle: {
								color: '#ff7900',    // any HTML string color ('red', '#cc00cc')
								fontName: 'Verdana', // i.e. 'Times New Roman'
								fontSize: 25, // 12, 18 whatever you want (don't specify px)
								bold: true,    // true or falseitalic: <boolean>   // true of false
								},
								width: 800,
								height: 300,
								backgroundColor: '#DDD',
								colors: ['#ff7900', 'black']
							};

						}

						$scope.gettimestamps = function(){
							$http.post($scope.url,
								{ 'userid': $rootScope.userid }).then(function (res) {
									$scope.timestamps = [];
									$scope.timestamps = res.data;
									for(var j =0;j<res.data.length;j++)
									{
										$scope.timestamps[j].timestamp = new Date(res.data[j].timestamp);
										
									}
									console.log(res.data);


								});
							}
							angular.element(document).ready(function () {
								$scope.gettimestamps();
								$scope.showtable = true;
							});
							$interval(function()
							{ 
								$scope.interval(); 
							}, 1000);                      
							$scope.interval = function(view)
							{
								$scope.gettimestamps();
							};

							$scope.getgraphs = function(view)
							{
								$scope.url = "http://michielserver.com/AP_valley/Getgraphs.php";						
								$scope.showtable = false;
								$scope.now = new Date()
								$scope.lastmidnight = new Date()
								$scope.lastmidnight.setHours(0,0,0,0);
								$scope.countperday = [0,0,0,0,0,0,0];
								console.log($scope.now.getDay());
								console.log($scope.weekdays);
								$scope.amount = 5;

								for(var i =0;i<7;i++){
									for(var j =0;j<$scope.timestamps.length;j++){

										$scope.timestamp = new Date($scope.timestamps[j].timestamp);
										if($scope.timestamp.getTime() > $scope.lastmidnight.getTime() - $scope.oneday*i && $scope.timestamp.getTime() < ($scope.lastmidnight.getTime() - $scope.oneday*i) + $scope.oneday)
										{
											$scope.countperday[i]++;                              	
										}
									}
								}
								console.log($scope.countperday); 

								if($scope.now.getDay() == 0)
								{
									$scope.weekdays = ["Today", "Saturday", "Friday","Thursday", "Wednesday", "Tuesday", "Monday"];

								}
								if($scope.now.getDay() == 1)
								{
									$scope.weekdays = ["Today","Sunday", "Saturday", "Friday","Thursday", "Wednesday", "Tuesday"];
								}
								if($scope.now.getDay() == 2)
								{
									$scope.weekdays = ["Today","Monday","Sunday", "Saturday", "Friday","Thursday", "Wednesday"];
								}
								if($scope.now.getDay() == 3)
								{
									$scope.weekdays = ["Today", "Tuesday","Monday","Sunday", "Saturday", "Friday","Thursday"];
								}
								if($scope.now.getDay() == 4)
								{
									$scope.weekdays = ["Today", "Wednesday", "Tuesday","Monday","Sunday", "Saturday", "Friday"];
								}
								if($scope.now.getDay() == 5)
								{
									$scope.weekdays = ["Today", "Thursday", "Wednesday", "Tuesday","Monday","Sunday", "Saturday"];
								}
								if($scope.now.getDay() == 6)
								{
									$scope.weekdays = ["Today", "Friday", "Thursday", "Wednesday", "Tuesday","Monday","Sunday"];
								}

								$scope.myChartObject = {};
								$scope.myChartObject.type = "ColumnChart";
								$scope.myChartObject.data = {"cols": [
								{id: "d", label: "Days", type: "string"},
								{id: "a", label: "Amount", type: "number"}
								], "rows": [
								{c: [
									{v: $scope.weekdays[6]},
									{v: $scope.countperday[6]},
									]},
									{c: [
										{v: $scope.weekdays[5]},
										{v: $scope.countperday[5]}
										]},
										{c: [
											{v: $scope.weekdays[4]},
											{v: $scope.countperday[4]}
											]},
											{c: [
												{v: $scope.weekdays[3]},
												{v: $scope.countperday[3]},
												]},
												{c: [
													{v: $scope.weekdays[2]},
													{v: $scope.countperday[2]},
													]},
													{c: [
														{v: $scope.weekdays[1]},
														{v: $scope.countperday[1]}
														]},
														{c: [                         
															{v: $scope.weekdays[0]},
															{v: $scope.countperday[0]}
															]}
															]};

															$scope.myChartObject.options = {
																title: 'Doorbell activity last 7 days',
																titleTextStyle: {
																color: '#ff7900',    // any HTML string color ('red', '#cc00cc')
																fontName: 'Verdana', // i.e. 'Times New Roman'
																fontSize: 25, // 12, 18 whatever you want (don't specify px)
																bold: true,    // true or falseitalic: <boolean>   // true of false
															},
															width: 800,
															height: 300,
															backgroundColor: '#DDD',
															colors: ['#ff7900', 'black']
														};
													};
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