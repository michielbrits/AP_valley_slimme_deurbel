
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
				for(var j =0;j<$scope.timestamps.length; j++)
				{

					if($scope.timestamps[j].timestamp.getTime() > $scope.now.getTime() - $scope.oneday)
					{
						$scope.amount--;                              	
					}
				}
			};
			$scope.datechanged = function(){
				console.log($scope.datepicker1);
				$scope.url = "http://michielserver.com/AP_valley/api/Gettimestamps.php";
				$scope.myChartObject = {};
				$scope.test = [];
				$scope.timestamps = [];
				$interval.cancel(stopTime);
				$scope.typeofchart = "LineChart";
				if ($scope.datepicker1 != null && $scope.datepicker2 != null) 
				{
					if ($scope.datepicker2 - $scope.datepicker1 <= 86400000) 
					{
						$scope.typeofchart = "ColumnChart";
						console.log("Shorter than a day");
						$http.post($scope.url, { 'userid': $rootScope.userid, 'starttimestamp': $scope.datepicker1, 'endtimestamp': $scope.datepicker2 }).then(function (res) 
						{
							$scope.timestamps = res.data;
							for(var j = res.data.length - 1; j > -1; j--)
							{
								$scope.timestamps[j].timestamp = new Date(res.data[j].timestamp);
								var timestamp = new Date($scope.timestamps[j].timestamp);
								$scope.test.push({c: [{v: $scope.timestamps[j].timestamp.getHours() + ":" + $scope.timestamps[j].timestamp.getMinutes() + ":" + $scope.timestamps[j].timestamp.getSeconds() },{v: 1},{f:"hey"}]})
							}
						});
					}
					else if($scope.datepicker2 - $scope.datepicker1 <= 86400000*31)
					{
						$scope.amountperday = [];
						$scope.daysofselection = [];
						$scope.test1 = [];
						$scope.test2 = [];

						$http.post($scope.url, { 'userid': $rootScope.userid, 'starttimestamp': $scope.datepicker1, 'endtimestamp': $scope.datepicker2 }).then(function (res) 
						{
							$scope.timestamps = res.data;
							for (var j = res.data.length - 1; j > -1; j-- ) 
							{
								$scope.timestamps[j].timestamp = new Date(res.data[j].timestamp);
								for(var i = 0; i < ($scope.datepicker2 - $scope.datepicker1)/86400000; i++)
								{
									if ($scope.amountperday[i] == null) 
									{
										$scope.amountperday[i] = 0;

									}
									if ($scope.daysofselection[i] == null)
									{
										$scope.date1 = new Date($scope.datepicker1);
										$scope.date1.setDate($scope.datepicker1.getDate()+ i);
										$scope.test1.push($scope.date1);

										$scope.date2 = new Date($scope.datepicker2);
										$scope.date2.setDate($scope.datepicker2.getDate()- i);
										$scope.test2.push($scope.date2);
                  
                  if($scope.datepicker1.getMonth() + 1 != $scope.datepicker2.getMonth() + 1){
                      if(i+1 <= Math.round(((new Date($scope.date1.getFullYear(), $scope.datepicker1.getMonth() +1))-(new Date($scope.date1.getFullYear(), 
                        $scope.datepicker1.getMonth() )))/86400000) - $scope.datepicker1.getDate()+ 1){
                            $scope.test123 = $scope.datepicker1.getMonth() + 1;
                      }else{
                              $scope.test123 = $scope.datepicker2.getMonth() + 1;
                      }
                  }else{
                      $scope.test123 = $scope.datepicker1.getMonth() + 1;
                  }
                  
										$scope.daysofselection[i] = ({v: $scope.date1.getDate() + "/" + $scope.test123});
									}
									if ($scope.test1[i].getDate() == $scope.timestamps[j].timestamp.getDate()) 
									{
										$scope.amountperday[i]++;
									}				
								}
							}
							for(var i = 0; i < ($scope.datepicker2 - $scope.datepicker1)/86400000; i++)
							{	
								$scope.date = new Date($scope.datepicker1);
								$scope.test[i] = ({c: [$scope.daysofselection[i],{v: $scope.amountperday[i]},{f:"hey"}]} )
							}
						});
						console.log($scope.daysofselection);
						console.log($scope.amountperday);
					}
					else if($scope.datepicker2 - $scope.datepicker1 < 86400000*365)
					{
						$scope.amountpermonth = [];
						$scope.daysofselection = [];
						$scope.date1 = new Date($scope.datepicker1);
						$scope.date2 = new Date($scope.datepicker2);
						$scope.months = ["januari", "februari", "maart", "april", "mei", "juni", "juli", "augustus", "september", "oktober", "november","december","januari", "februari", "maart", "april", "mei", "juni", "juli", "augustus", "september", "oktober", "november","december","januari", "februari", "maart", "april", "mei", "juni", "juli", "augustus", "september", "oktober", "november","december","januari", "februari", "maart", "april", "mei", "juni", "juli", "augustus", "september", "oktober", "november","december","januari", "februari", "maart", "april", "mei", "juni", "juli", "augustus", "september", "oktober", "november","december","januari", "februari", "maart", "april", "mei", "juni", "juli", "augustus", "september", "oktober", "november","december","januari", "februari", "maart", "april", "mei", "juni", "juli", "augustus", "september", "oktober", "november","december","januari", "februari", "maart", "april", "mei", "juni", "juli", "augustus", "september", "oktober", "november","december","januari", "februari", "maart", "april", "mei", "juni", "juli", "augustus", "september", "oktober", "november","december","januari", "februari", "maart", "april", "mei", "juni", "juli", "augustus", "september", "oktober", "november","december"];
						$http.post($scope.url, { 'userid': $rootScope.userid, 'starttimestamp': $scope.datepicker1, 'endtimestamp': $scope.datepicker2 }).then(function (res) 
						{
							$scope.timestamps = res.data;
							for (var j = res.data.length - 1; j > -1; j-- ) 
							{
								for (var i = $scope.date1.getMonth(); i < $scope.date2.getMonth() + 1 + (($scope.date2.getFullYear()-$scope.date1.getFullYear())*12); i++) 
								{
									if ($scope.date1.getFullYear() == $scope.date2.getFullYear()) 
									{
										$scope.timestamps[j].timestamp = new Date(res.data[j].timestamp);
										if ($scope.amountpermonth[i] == null) 
										{
											$scope.amountpermonth[i] = 0;
										}
											if ($scope.timestamps[j].timestamp.getMonth() == i) 
											{
												$scope.amountpermonth[i]++;
											}
										}
										else
										{
											$scope.timestamps[j].timestamp = new Date(res.data[j].timestamp);
											if ($scope.amountpermonth[i] == null) 
											{
												$scope.amountpermonth[i] = 0;
											}
											if ($scope.timestamps[j].timestamp.getMonth() == i && $scope.timestamps[j].timestamp.getFullYear() == $scope.date1.getFullYear()) 
											{
												$scope.amountpermonth[i]++;
											}
											else if ($scope.timestamps[j].timestamp.getMonth() == i-12 && $scope.timestamps[j].timestamp.getFullYear() == $scope.date2.getFullYear()) 
											{
												$scope.amountpermonth[i]++;
											}
										}
									}
								}
								for (var i = $scope.date1.getMonth(); i < $scope.date2.getMonth()+1 + (($scope.date2.getFullYear()-$scope.date1.getFullYear())*12); i++) 
								{                     
									$scope.test[i - $scope.date1.getMonth()] = ({c: [({v: $scope.months[i]}),{v: $scope.amountpermonth[i]},{f:"hey"}]} );
								}
							});
					}
					else if($scope.datepicker2 - $scope.datepicker1 >= 86400000*365)
					{
						$scope.amountperyear = [];
						$scope.daysofselection = [];
						$scope.date1 = new Date($scope.datepicker1);
						$scope.date2 = new Date($scope.datepicker2);
						//console.log($scope.date1.getMonth()+1);
						//$scope.months = ["januari", "februari", "maart", "april", "mei", "juni", "juli", "augustus", "september", "oktober", "november","december"];
						$http.post($scope.url, { 'userid': $rootScope.userid, 'starttimestamp': $scope.datepicker1, 'endtimestamp': $scope.datepicker2 }).then(function (res) 
						{
							$scope.timestamps = res.data;
							for (var j = 0; j < res.data.length; j++ ) 
							{
								for (var i = $scope.date1.getFullYear(); i < $scope.date2.getFullYear() +1; i++) 
								{
									$scope.timestamps[j].timestamp = new Date(res.data[j].timestamp);
									if ($scope.amountperyear[i - $scope.date1.getFullYear()] == null) 
									{
										$scope.amountperyear[i - $scope.date1.getFullYear()] = 0;
									}
											//console.log($scope.timestamps);
											if ($scope.timestamps[j].timestamp.getFullYear() == i) 
											{
												$scope.amountperyear[i - $scope.date1.getFullYear()]++;
												console.log($scope.amountperyear);
											}
										}
									}
									for (var i = $scope.date1.getFullYear(); i < $scope.date2.getFullYear() +1; i++) 
									{
										console.log($scope.date1.getFullYear());
										console.log($scope.date2.getFullYear());
										console.log(i);
										$scope.daysofselection[i - $scope.date1.getFullYear()] = ({v: i});
										$scope.test[i - $scope.date1.getFullYear()] = ({c: [$scope.daysofselection[i - $scope.date1.getFullYear()],{v: $scope.amountperyear[i - $scope.date1.getFullYear()]},{f:"hey"}]} );
										console.log($scope.test);
									}
								});
					}
				}
				$scope.myChartObject.type = $scope.typeofchart;
				$scope.myChartObject.data = {"cols": [
				{id: "d", label: "Days", type: "string"},
				{id: "a", label: "Amount", type: "number"}
				], "rows": $scope.test};
				$scope.myChartObject.options = {
					title: 'Doorbell activity',
					vAxis: { 
						minValue: 4,                    
						viewWindow:{min:0}, /*this also makes 0 = min value*/         
						format: '0',                     
					},
					titleTextStyle: {
								color: '#ff7900',    // any HTML string color ('red', '#cc00cc')
								fontName: 'Verdana', // i.e. 'Times New Roman'
								fontSize: 25, // 12, 18 whatever you want (don't specify px)
								bold: true,    // true or falseitalic: <boolean>   // true of false
							},
							width: 1200,
							height: 450,
							backgroundColor: '#DDD',
							colors: ['#ff7900', 'black']
						};

					}

					$scope.gettimestamps = function(){
						$http.post($scope.url,
							{ 'userid': $rootScope.userid }).then(function (res) {
								console.log($rootScope.userid);
								$scope.timestamps = [];
								$scope.timestamps = res.data;
								for(var j =0;j<res.data.length;j++)
								{
									$scope.timestamps[j].timestamp = new Date(res.data[j].timestamp);

								}
									//console.log(res.data);


								});
						}
						angular.element(document).ready(function () {
							$scope.gettimestamps();
							$scope.showtable = true;
						});

						stopTime = $interval($scope.gettimestamps, 1000);
						$scope.getgraphs = function(view)
						{
            console.log("before change here");
            if($scope.datepicker1 != null && $scope.datepicker2 != null){
              $scope.datechanged();//changehere
              console.log("change here");
            }else{
            console.log("change here not");
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
															width: 1200,
															height: 450,
															backgroundColor: '#DDD',
															colors: ['#ff7900', 'black']
														};
													};
												}
											})}
});
