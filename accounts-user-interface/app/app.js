const app = angular.module('app', ['ngResource', 'ui.bootstrap','ngRoute']);

app.config(['$routeProvider', '$locationProvider',
  function ($routeProvider, $locationProvider) {

    $routeProvider
    .when('/list', {
      templateUrl: 'app/accounts_list/accounts.list.view.html',
      controller: 'AccountsListController'
    })
    .when('/transactions', {
      templateUrl: 'app/account_transactions/transactions.view.html',
      controller: 'TransactionsController'
    })
    .when('/openAccount', {
      templateUrl: 'app/account_form/account.form.view.html',
      controller: 'AccountsFormController'
    })
    .otherwise({
      templateUrl: 'app/accounts_list/accounts.list.view.html',
      controller: 'AccountsListController'
    });

    $locationProvider.html5Mode({
      enabled: false,
      requireBase: false
    });
  }]); 