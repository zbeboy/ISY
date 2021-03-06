/**
 * Created by lenovo on 2016-09-07.
 */
requirejs.config({
    // pathsオプションの設定。"module/name": "path"を指定します。拡張子（.js）は指定しません。
    paths: {
        "jquery.showLoading": web_path + "/plugin/loading/js/jquery.showLoading.min",
        "csrf": web_path + "/js/util/csrf",
        "attribute_extensions": web_path + "/js/util/attribute_extensions"
    },
    // shimオプションの設定。モジュール間の依存関係を定義します。
    shim: {
        "jquery.showLoading": {
            // jQueryに依存するのでpathsで設定した"module/name"を指定します。
            deps: ["jquery"]
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

// require(["module/name", ...], function(params){ ... });
require(["jquery", "requirejs-domready", "bootstrap", "jquery.showLoading", "csrf", "attribute_extensions"], function ($, domready) {
    domready(function () {
        //This function is called once the DOM is ready.
        //It will be safe to query the DOM and manipulate
        //DOM nodes in this function.

        /*
         ajax url
         */
        var ajax_url = {
            valid_email: '/user/login/valid/email',
            forget_email: '/user/login/password/forget/email',
            finish: '/user/login/password/forget/finish'
        };

        /*
         参数id
         */
        var paramId = {
            email: '#email'
        };

        /*
         参数
         */
        var param = {
            email: $(paramId.email).val().trim()
        };

        /*
         验证id
         */
        var validId = {
            email: '#valid_email'
        };

        /*
         错误消息id
         */
        var errorMsgId = {
            email: '#email_error_msg'
        };

        /*
         消息
         */
        var msg = {
            email: '邮箱格式不正确'
        };

        /*
         验证正则
         */
        var valid_regex = {
            email: /^\w+((-\w+)|(\.\w+))*@[A-Za-z0-9]+(([.-])[A-Za-z0-9]+)*\.[A-Za-z0-9]+$/
        };

        /*
         初始化参数
         */
        function initParam() {
            param.email = $(paramId.email).val().trim();
        }

        /*
         显示遮罩
         */
        function startLoading() {
            $('#loading_region').showLoading();
        }

        /*
         去除遮罩
         */
        function endLoading() {
            $('#loading_region').hideLoading();
        }

        /**
         * 验证错误
         * @param validId 验证id
         * @param errorMsgId 错误验证id
         * @param msg 消息
         */
        function validErrorDom(validId, errorMsgId, msg) {
            $(validId).addClass('has-error');
            $(errorMsgId).removeClass('hidden').text(msg);
        }

        /*
         提交数据
         */
        $('#forget_password').click(function () {
            startLoading();
            validEmail();
        });

        /**
         * 提交时检验邮箱
         */
        function validEmail() {
            initParam();
            var email = param.email;
            if (!valid_regex.email.test(email)) {
                endLoading();
                validErrorDom(validId.email, errorMsgId.email, msg.email);
            } else {
                $.post(web_path + ajax_url.valid_email, {email: email}, function (data) {
                    if (data.state) {
                        sendForgetPasswordEmail();
                    } else {
                        endLoading();
                        validErrorDom(validId.email, errorMsgId.email, data.msg);
                    }
                });
            }
        }

        /**
         * 提交到后台
         */
        function sendForgetPasswordEmail() {
            $.post(web_path + ajax_url.forget_email, $('#forget_password_form').serialize(), function (data) {
                if (data.state) {
                    window.location.href = web_path + ajax_url.finish;
                } else {
                    endLoading();
                    $('#email_error_msg').removeClass('hidden').text(data.msg);
                }
            });
        }

    });
});