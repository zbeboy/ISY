/**
 * Created by lenovo on 2016-08-17.
 */
requirejs.config({
    // pathsオプションの設定。"module/name": "path"を指定します。拡張子（.js）は指定しません。
    paths: {
        "jquery.fireworks": web_path + "/plugin/jquery_fireworks/jquery.fireworks"
    },
    // shimオプションの設定。モジュール間の依存関係を定義します。
    shim: {
        "jquery.fireworks": {
            // jQueryに依存するのでpathsで設定した"module/name"を指定します。
            deps: ["jquery"]
        }
    }
});
// require(["module/name", ...], function(params){ ... });
require(["jquery", "jquery.fireworks"], function ($) {
    // Preloader
    $('#status').delay(300).fadeOut();
    $('#preloader').delay(300).fadeOut('slow');
    $('body').delay(550).css({'overflow': 'visible'});

    // 烟火 庆祝准备
    $('.intro-header').fireworks({
        sound: false, // 声音效果 不开
        opacity: 0.5,
        width: '100%',
        height: '100%'
    });
});