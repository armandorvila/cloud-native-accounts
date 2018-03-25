'use strict';

angular.module('app').controller('AccountsListController', function ($scope, $rootScope, $location, $log, Account) {

    $scope.errors = {};

    $scope.account = {}

    $scope.clearFilters = function () {
      $scope.account = {}
    };

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
        offset: $scope.offset(),
        limit: $scope.selectedPageSize
      }

      Account.list(request, function (result, headers) {
        if(!result.length == 0){
          $scope.accounts = result;
          $scope.paginationEnded = false;
          request = {};
        }
        else {
          $scope.currentPage = $scope.currentPage - 1;
          $scope.paginationEnded = true;
        }
      });
    };

    $scope.goToTransactions = function(account) {
      $rootScope.selectedAccount = account;
      $location.path("/transactions");
    };

  }).service('Account', function ($resource) {
    return $resource('http://localhost/api/accounts/:id', {}, {
      'get': { method: 'GET', isArray: false },
      'list': { method: 'GET', isArray: true },
      'save': { method: 'POST'}
    });
  });
