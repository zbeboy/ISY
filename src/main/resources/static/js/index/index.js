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
        "vegas": ["https://lib.baomitu.com/vegas/2.4.0/vegas.min", web_path + "/plugin/vegas/vegas.min"]
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
require(["jquery", "lodash", "handlebars", "bootstrap", "wow", "vegas"], function ($, D, Handlebars) {
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

    /*
    ajax url.
    */
    var ajax_url = {
        save: '/user/graduate/wishes/save',
        data: '/user/graduate/wishes/data'
    };

    /*
    参数id
    */
    var paramId = {
        schoolName: '#schoolName',
        username: '#username',
        content: '#content'
    };

    /*
     参数
     */
    var param = {
        schoolName: '',
        username: '',
        content: ''
    };

    /*
     错误消息id
     */
    var errorMsgId = {
        error_meg: '#error_meg'
    };

    /**
     * 检验成功
     * @param errorMsgId
     */
    function validSuccessDom(errorMsgId) {
        $(errorMsgId).text('');
    }

    /**
     * 检验失败
     * @param errorMsgId
     * @param msg
     */
    function validErrorDom(errorMsgId, msg) {
        $(errorMsgId).text(msg);
    }

    /**
     * 初始化参数
     */
    function initParam() {
        param.schoolName = $(paramId.schoolName).val();
        param.username = $(paramId.username).val();
        param.content = $(paramId.content).val();
    }

    $('#toGraduate').click(function () {
        initGraduateData();
        $('#graduate').modal('show');
    });

    function initGraduateData() {
        $.get(web_path + ajax_url.data, function (data) {
            listData(data);
        });
    }

    /**
     * 列表数据
     * @param data 数据
     */
    function listData(data) {
        var template = Handlebars.compile($("#graduate-template").html());
        Handlebars.registerHelper('schoolName', function () {
            return new Handlebars.SafeString(Handlebars.escapeExpression(this.schoolName));
        });
        Handlebars.registerHelper('username', function () {
            return new Handlebars.SafeString(Handlebars.escapeExpression(this.username));
        });
        Handlebars.registerHelper('content', function () {
            return new Handlebars.SafeString(Handlebars.escapeExpression(this.content));
        });
        $('#graduateData').html(template(data));
    }

    $('#submitGraduate').click(function () {
        add();
    });

    function add() {
        initParam();
        var schoolName = D.trim(param.schoolName);
        if (schoolName === '') {
            validErrorDom(errorMsgId.error_meg, '请告诉我们，您的学校。');
            return;
        } else {
            validSuccessDom(errorMsgId.error_meg);
        }

        var username = D.trim(param.username);
        if (username === '') {
            validErrorDom(errorMsgId.error_meg, '请告诉我们，您的姓名。');
            return;
        } else {
            validSuccessDom(errorMsgId.error_meg);
        }

        var content = D.trim(param.content);
        if (content === '') {
            validErrorDom(errorMsgId.error_meg, '不想说点什么吗？');
            return;
        } else {
            validSuccessDom(errorMsgId.error_meg);
        }

        sendAjax();
    }

    function sendAjax() {
        $.get(web_path + ajax_url.save, param, function (data) {
            if (data.state) {
                initGraduateData();
            } else {
                validErrorDom(errorMsgId.error_meg, data.msg);
            }
        });
    }

});