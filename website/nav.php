<?php?>
    
<img class="logosmall" src="small%20logo.png" alt="Buzz" width="75" height="75">
<input type="text" name="Code" id="Code" placeholder="ENTER NEW PASSWORD" ng-model="Newpsw" ng-hide="true"/>
<button class="changebtn" type="submit" name="Change" id="Change" ng-click="Change()" ng-hide="true">Change Password</button>
<button  class="logout" type="submit" name="Logout" id="Logout" ng-click="Logout()" ng-hide="true">Logout</button>
<img class="" src="history.png" alt="History" width="90" height="100" ng-click="gethistory()">
<img class="" src="today.png" alt="Today" width="75" height="100" ng-click="gettimestamps24()">
<img class="" src="graphs.png" alt="Graphs" width="90" height="100" ng-click="getgraphs()">
