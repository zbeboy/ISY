var gulp = require('gulp');
var minifycss = require('gulp-minify-css');
var concat = require('gulp-concat');
var uglify = require('gulp-uglify');
var rename = require('gulp-rename');
var del = require('del');
var filter = require('gulp-filter');
var vinylPaths = require('vinyl-paths');
var gutil = require('gulp-util');

var pathPrefix = './src/main/resources/static/plugin/';

gulp.task('minifycss', function() {
	var cssFileter = filter(['**', '!**/*.min.css'],{restore: true});
	
    return gulp.src(pathPrefix + '**/*.css')
		.pipe(cssFileter)
        .pipe(rename({suffix: '.min'}))
        .pipe(minifycss())
		.on('error', gutil.log)
		.pipe(cssFileter.restore)
		.pipe(gulp.dest(pathPrefix))
});

gulp.task('minifyjs', function() {
	var jsFileter = filter(['**', '!**/*.min.js'],{restore: true});
	
    return gulp.src(pathPrefix +  '**/*.js')
        .pipe(jsFileter)
        .pipe(rename({suffix: '.min'}))
        .pipe(uglify())
		.on('error', gutil.log)
		.pipe(jsFileter.restore)
        .pipe(gulp.dest(pathPrefix));
});

gulp.task('backup',['minifycss','minifyjs'], function(cb) {
	var fileFileter = filter(['**', '!**/*.min.css','!**/*.min.js'],{restore: true});
	
    return gulp.src([pathPrefix +  '**/*.css',
	pathPrefix +  '**/*.js'])      
		.pipe(fileFileter)
		.pipe(gulp.dest('./rup'))
		.pipe(fileFileter.restore);  
});

gulp.task('clean',['backup'], function(cb) {
	var fileFileter = filter(['**', '!**/*.min.css','!**/*.min.js'],{restore: true});
	
    return gulp.src([pathPrefix +  '**/*.css',
	pathPrefix +  '**/*.js'])      
		.pipe(fileFileter)
        .pipe(vinylPaths(del))  
		.pipe(fileFileter.restore);
});

gulp.task('default', ['clean']);