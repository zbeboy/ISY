/**
 * Created by lenovo on 2016-09-08.
 */
requirejs.config({
    // pathsオプションの設定。"module/name": "path"を指定します。拡張子（.js）は指定しません。
    paths: {
        "jquery.showLoading": web_path + "/plugin/loading/js/jquery.showLoading.min",
        "csrf": web_path + "/js/util/csrf",
        "attribute_extensions": web_path + "/js/util/attribute_extensions",
        "jquery.entropizer": web_path + "/plugin/jquery_entropizer/js/jquery-entropizer.min",
        "entropizer": web_path + "/plugin/jquery_entropizer/js/entropizer.min"
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
require(["jquery", "requirejs-domready", "bootstrap", "jquery.showLoading", "csrf", "attribute_extensions", "jquery.entropizer"], function ($, domready) {
    domready(function () {
        //This function is called once the DOM is ready.
        //It will be safe to query the DOM and manipulate
        //DOM nodes in this function.

        /*
         正则
         */
        var valid_regex = {
            student_number_valid_regex: /^\d{13,}$/,
            email_valid_regex: /^\w+((-\w+)|(\.\w+))*@[A-Za-z0-9]+(([.-])[A-Za-z0-9]+)*\.[A-Za-z0-9]+$/,
            mobile_valid_regex: /^1[0-9]{10}/,
            phone_verify_code_valid_regex: /^\w+$/,
            password_valid_regex: /^[a-zA-Z0-9]\w{5,17}$/
        };

        /*
         消息
         */
        var msg = {
            password_error_msg: '密码6-16位任意字母或数字，以及下划线',
            confirm_password_error_msg: '密码不一致'
        };

        /**
         * 显示遮罩
         */
        function startLoading() {
            $('#loading_region').showLoading();
        }

        /**
         * 去除遮罩
         */
        function endLoading() {
            $('#loading_region').hideLoading();
        }

        /*
         ajax url
         */
        var ajax_url = {
            password_reset: '/user/login/password/reset',
            finish: '/user/login/password/reset/finish'
        };

        /*
         错误验证
         */
        function validErrorDom(inputId, errorId, msg) {
            $(inputId).removeClass('has-success').addClass('has-error');
            $(errorId).removeClass('hidden').text(msg);
        }

        /*
         正确验证
         */
        function validSuccessDom(inputId, errorId) {
            $(inputId).removeClass('has-error').addClass('has-success');
            $(errorId).addClass('hidden').text('');
        }

        /*
         参数id
         */
        var paramId = {
            password: '#password',
            confirmPassword: '#confirmPassword'
        };

        /*
         参数
         */
        var param = {
            password: $(paramId.password).val().trim(),
            confirmPassword: $(paramId.confirmPassword).val().trim()
        };

        /*
         初始化参数
         */
        function initParam() {
            param.password = $(paramId.password).val().trim();
            param.confirmPassword = $(paramId.confirmPassword).val().trim();
        }

        /*
         验证form id
         */
        var validId = {
            valid_password: '#valid_password',
            valid_confirm_password: '#valid_confirm_password'
        };

        /*
         错误消息 id
         */
        var errorMsgId = {
            password_error_msg: '#password_error_msg',
            confirm_password_error_msg: '#confirm_password_error_msg'
        };

        // 密码强度检测
        $('#meter2').entropizer({
            target: paramId.password,
            update: function (data, ui) {
                ui.bar.css({
                    'background-color': data.color,
                    'width': data.percent + '%'
                });
            }
        });

        /*
         密码验证
         */
        $(paramId.password).blur(function () {
            initParam();
            var password = param.password;
            if (!valid_regex.password_valid_regex.test(password)) {
                validErrorDom(validId.valid_password, errorMsgId.password_error_msg, msg.password_error_msg);
            } else {
                validSuccessDom(validId.valid_password, errorMsgId.password_error_msg);
            }
        });

        /*
         确认密码验证
         */
        $(paramId.confirmPassword).blur(function () {
            initParam();
            var password = param.password;
            var confirmPassword = param.confirmPassword;
            if (!valid_regex.password_valid_regex.test(password)) {
                validErrorDom(validId.valid_password, errorMsgId.password_error_msg, msg.password_error_msg);
            } else {
                validSuccessDom(validId.valid_password, errorMsgId.password_error_msg);
                if (confirmPassword !== password) {
                    validErrorDom(validId.valid_confirm_password, errorMsgId.confirm_password_error_msg, msg.confirm_password_error_msg);
                } else {
                    validSuccessDom(validId.valid_confirm_password, errorMsgId.confirm_password_error_msg)
                }
            }
        });

        /**
         * 表单提交时检验
         */
        $('#reset_password').click(function () {
            startLoading();
            validPassword();
        });

        /**
         * 验证密码
         */
        function validPassword() {
            initParam();
            var password = param.password;
            var confirmPassword = param.confirmPassword;
            if (!valid_regex.password_valid_regex.test(password)) {
                validErrorDom(validId.valid_password, errorMsgId.password_error_msg, msg.password_error_msg);
                // 去除遮罩
                endLoading();
            } else {
                validSuccessDom(validId.valid_password, errorMsgId.password_error_msg);
                if (confirmPassword !== password) {
                    validErrorDom(validId.valid_confirm_password, errorMsgId.confirm_password_error_msg, msg.confirm_password_error_msg);
                    // 去除遮罩
                    endLoading();
                } else {
                    validSuccessDom(validId.valid_confirm_password, errorMsgId.confirm_password_error_msg);
                    $.post(web_path + ajax_url.password_reset, $('#reset_password_form').serialize(), function (data) {
                        if (data.state) {
                            window.location.href = web_path + ajax_url.finish;
                        } else {
                            $('#error_msg').removeClass('hidden').text(data.msg);
                            // 去除遮罩
                            endLoading();
                        }
                    });
                }
            }
        }

    });
});