// require gulp dependencies
var gulp = require('gulp');

var connect = require('gulp-connect');

gulp.task('serve', function() {
  connect.server({
    root: '.',
    port: 5000,
    livereload: true
  });
});