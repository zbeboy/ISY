/**
 * Created by lenovo on 2016-08-17.
 */
requirejs.config({
    // pathsオプションの設定。"module/name": "path"を指定します。拡張子（.js）は指定しません。
    paths: {
        "wow": ["https://cdn.bootcss.com/wow/1.1.2/wow.min", web_path + "/plugin/wow/wow.min"],
        "vegas": ["https://cdn.bootcss.com/vegas/2.4.0/vegas.min", web_path + "/plugin/vegas/vegas.min"],
        "snow":web_path + "/plugin/snow/christmassnow.min"// 雪花效果
    },
    // shimオプションの設定。モジュール間の依存関係を定義します。
    shim: {
        "wow": {
            deps: ["jquery"]
        },
        "vegas": {
            deps: ["jquery"]
        },
        "snow": {
            deps: ["jquery"]
        }
    }
});
// require(["module/name", ...], function(params){ ... });
require(["jquery", "bootstrap", "wow", "vegas","snow"], function ($) {
    // Preloader
    $('.preloader').fadeOut(1000);

    var body = $('body');

    // Snow style
    body.christmassnow({
        snowflaketype: 23, // 1 to 25 types of flakes are available change the number from 1 to 25. each one contain different images.
        snowflakesize: 2, //snowflakesize is 1 then it get the size of the image as random , if the snowflakesize is 2 means size of the image as custom
        snowflakedirection: 1, // 1 means default no wind (top to bottom), 2 means random, 3 means left to right and 4 means  right to left
        snownumberofflakes: 4, // number of flakes is user option
        snowflakespeed: 10, // falling speed of flake 10 sec is default
        flakeheightandwidth: 15 // if you are mention that option flakesize is 2 then this flakeheightandwidth should work values are in pixels 16*16.
    });

    // qq social
    $('.fa-qq').popover({
        trigger: 'focus',
        html: true,
        animation: true,
        content: '<img src="' + web_path + '/images/social-qq.png" style="height: 150px;width: 150px;" alt="QQ群" />',
        placement: 'top'
    });

    // weixin social
    $('.fa-weixin').popover({
        trigger: 'focus',
        html: true,
        animation: true,
        content: '<img src="' + web_path + '/images/social-weixin.jpg" style="height: 150px;width: 150px;" alt="微信公众号" />',
        placement: 'top'
    });

    // pic
    body.vegas({
        slides: [
            {src: web_path + '/images/day-5.jpg'},
            {src: web_path + '/images/day-6.jpg'}
        ],
        timer: false,
        transition: ['zoomOut']
    });

    new WOW({mobile: false}).init();
});