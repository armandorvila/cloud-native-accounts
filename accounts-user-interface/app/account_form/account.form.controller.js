'use strict';

angular.module('app').controller('AccountsFormController', function ($scope, $log, $location, Account) {

    $scope.openAccount = function(){
      let payload = {
        description: $scope.account.description,
        initialCredit: $scope.account.initialCredit,
        customerId: $scope.account.customer.id,
      }

      Account.save(payload,  function(resp) {
        $scope.account = {};
        $location.path( "/#/accounts" );
      });
    };

    $scope.init = function () {
      Account.list({ offset: 0, limit: 10000 }, function (result, headers) {

        let removeDuplicates = function removeDuplicates(myArr, prop) {
          return myArr.filter((obj, pos, arr) => {
            return arr.map(mapObj => mapObj[prop]).indexOf(obj[prop]) === pos;
          });
        }
        $scope.customers = removeDuplicates(result.map(acc => acc.customer), 'id')
      });
    }
  });
