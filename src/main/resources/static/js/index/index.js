/**
 * Created by lenovo on 2016-08-17.
 */
requirejs.config({
    map: {
        '*': {
            'css': web_path + '/webjars/require-css/css.min.js' // or whatever the path to require-css is
        }
    },
    // pathsオプションの設定。"module/name": "path"を指定します。拡張子（.js）は指定しません。
    paths: {
        "wow": ["https://lib.baomitu.com/wow/1.1.2/wow.min", web_path + "/plugin/wow/wow.min"],
        "vegas": ["https://lib.baomitu.com/vegas/2.4.0/vegas.min", web_path + "/plugin/vegas/vegas.min"],
        "moment": ["https://lib.baomitu.com/moment.js/2.13.0/moment.min",
            web_path + "/plugin/moment/moment.min"]
    },
    // shimオプションの設定。モジュール間の依存関係を定義します。
    shim: {
        "wow": {
            deps: ["jquery", "css!" + web_path + "/plugin/animate/animate.min"]
        },
        "vegas": {
            deps: ["jquery", "css!" + web_path + "/plugin/vegas/vegas.min"]
        }
    }
});

/*
 捕获全局错误
 */
requirejs.onError = function (err) {
    console.log(err.requireType);
    if (err.requireType === 'timeout') {
        console.log('modules: ' + err.requireModules);
    }
    throw err;
};

/**
 * Creates the node for the load command. Only used in browser envs.
 */
requirejs.createNode = function (config, moduleName, url) {
    var node = config.xhtml ?
        document.createElementNS('http://www.w3.org/1999/xhtml', 'html:script') :
        document.createElement('script');
    node.type = config.scriptType || 'text/javascript';
    node.charset = 'utf-8';
    node.async = true;

    if (moduleName === 'wow') {
        node.crossOrigin = 'anonymous';
        node.integrity = 'sha384-V27yAyb3yYhZbiwaK9Sgxh9Cywkf/H2al4wcrcp/hKF9ZYT7d5saGJFoO/0v1Cgs';
    }

    if (moduleName === 'vegas') {
        node.crossOrigin = 'anonymous';
        node.integrity = 'sha384-uJJ21HstS6Pv/3Rdbb/DoeSiA/eZEciUBGOgDU5EPX4ZKQu80Y71qTrgSOlZAmXR';
    }

    return node;
};

// require(["module/name", ...], function(params){ ... });
require(["jquery", "bootstrap", "wow", "vegas"], function ($) {
    // Preloader
    $('.preloader').fadeOut(1000);

    var body = $('body');

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
            {src: web_path + '/images/slide-3.jpg'},
            {src: web_path + '/images/slide-4.jpg'}
        ],
        timer: false,
        transition: ['zoomOut']
    });

    new WOW({mobile: false}).init();

    console.log('%c █    ██████    █     █', 'color:blue');
    console.log('%c █    █          █   █', 'color:blue');
    console.log('%c █    ██████       █', 'color:red');
    console.log('%c █         █       █', 'color:red');
    console.log('%c █    ██████       █', 'color:green');
    console.log('%c Welcome to ISY.', 'color:red');
});