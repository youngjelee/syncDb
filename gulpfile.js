const { src, dest } = require('gulp');
const sass = require('sass');
const gulpSass = require('gulp-sass');

const sassTask = gulpSass(sass);

exports.buildSass = function() {

console.log('hi')

  return src('src/main/resources/static/scss/main.scss')
    .pipe(sassTask().on('error', sassTask.logError))
    .pipe(dest('src/main/resources/static/css/'));
}