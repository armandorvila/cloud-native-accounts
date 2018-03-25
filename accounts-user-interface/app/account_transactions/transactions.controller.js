'use strict';

angular.module('app').controller('TransactionsController', function ($scope,  $rootScope, $log, Transaction) {

    $scope.errors = {};

    $scope.account = $rootScope.selectedAccount;

    $scope.currentPage = 1;

    $scope.isCollapsed = true;
    
    $scope.pageSizes = [50, 100, 500, 1000, 10000];
    $scope.selectedPageSize = 50;

    $scope.init = function () {
      $scope.loadPage();
    }

    $scope.offset = function () {
      return ($scope.currentPage - 1) * $scope.selectedPageSize;
    }

    $scope.loadPage = function () {
      var request = {
        accountId: $scope.account.id,
        offset: $scope.offset(),
        limit: $scope.selectedPageSize
      }

      Transaction.list(request, function (result, headers) {
        if(!result.length == 0){
          $scope.transactions = result;
          $scope.paginationEnded = false;
          request = {};
        }
        else {
          $scope.currentPage = $scope.currentPage - 1;
          $scope.paginationEnded = true;
        }

      });
    };

  }).service('Transaction', function ($resource) {
    return $resource('http://localhost/api/accounts/:accountId/transactions', {}, {
      'list': { method: 'GET', isArray: true }
    });
  });
