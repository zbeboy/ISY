/**
 * Created by lenovo on 2016-11-01.
 */
//# sourceURL=users_setting.js
require(["jquery", "handlebars", "messenger", "bootstrap", "jquery.address", "jquery.entropizer"],
    function ($) {
        /*
         ajax url
         */
        var ajax_url = {
            valid_mobile_url: '/user/register/valid/mobile',
            mobile_code_url: '/user/register/mobile/code',
            valid_users_url: '/anyone/valid/users',
            update_mobile_url: '/anyone/user/mobile/update',
            update_password_url: '/anyone/user/password/update'
        };

        /*
         正则
         */
        var valid_regex = {
            mobile_valid_regex: /^1[0-9]{10}$/,
            phone_verify_code_valid_regex: /^\w+$/,
            password_valid_regex: /^[a-zA-Z0-9]\w{5,17}$/
        };

        /*
         消息
         */
        var msg = {
            mobile_error_msg: '手机号格式不正确',
            phone_verify_code_error_msg: '验证码不正确',
            password_error_msg: '密码6-16位任意字母或数字，以及下划线',
            confirm_password_error_msg: '密码不一致'
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
         清除验证
         */
        function validCleanDom(inputId, errorId) {
            $(inputId).removeClass('has-error').removeClass('has-success');
            $(errorId).addClass('hidden').text('');
        }

        /*
         参数id
         */
        var paramId = {
            username: '#username',
            mobile: '#newMobile',
            phoneVerifyCode: '#phoneVerifyCode',
            password: '#newPassword',
            confirmPassword: '#okPassword'
        };

        /*
         参数
         */
        var param = {
            username: $(paramId.username).val().trim(),
            mobile: $(paramId.mobile).val().trim(),
            phoneVerifyCode: $(paramId.phoneVerifyCode).val().trim(),
            password: $(paramId.password).val().trim(),
            confirmPassword: $(paramId.confirmPassword).val().trim()
        };

        /*
         初始化参数
         */
        function initParam() {
            param.username = $(paramId.username).val().trim();
            param.mobile = $(paramId.mobile).val().trim();
            param.phoneVerifyCode = $(paramId.phoneVerifyCode).val().trim();
            param.password = $(paramId.password).val().trim();
            param.confirmPassword = $(paramId.confirmPassword).val().trim();
        }

        /*
         验证form id
         */
        var validId = {
            valid_mobile: '#valid_mobile',
            valid_phone_verify_code: '#valid_phone_verify_code',
            valid_password: '#valid_new_password',
            valid_confirm_password: '#valid_ok_password'
        };

        /*
         错误消息 id
         */
        var errorMsgId = {
            mobile_error_msg: '#mobile_error_msg',
            phone_verify_code_error_msg: '#phone_verify_code_error_msg',
            password_error_msg: '#new_password_error_msg',
            confirm_password_error_msg: '#ok_password_error_msg'
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

        $(paramId.mobile).blur(function () {
            initParam();
            var mobile = param.mobile;
            if (!valid_regex.mobile_valid_regex.test(mobile)) {
                validErrorDom(validId.valid_mobile, errorMsgId.mobile_error_msg, msg.mobile_error_msg);
            } else {
                // ajax 检验
                $.post(web_path + ajax_url.valid_users_url, {
                    username: param.username,
                    mobile: mobile,
                    validType: 2
                }, function (data) {
                    if (data.state) {
                        validSuccessDom(validId.valid_mobile, errorMsgId.mobile_error_msg);
                    } else {
                        validErrorDom(validId.valid_mobile, errorMsgId.mobile_error_msg, data.msg);
                    }
                });
            }
        });

        $(paramId.phoneVerifyCode).blur(function () {
            initParam();
            var phoneVerifyCode = param.phoneVerifyCode;
            var mobile = param.mobile;
            if (!valid_regex.phone_verify_code_valid_regex.test(phoneVerifyCode)) {
                validErrorDom(validId.valid_phone_verify_code, errorMsgId.phone_verify_code_error_msg, msg.phone_verify_code_error_msg);
            } else {
                if (mobile !== $('#mobile').val()) {
                    // ajax 检验 参数：手机号，验证码 后台保存这两个参数，在提交时再次检验
                    $.post(web_path + ajax_url.valid_mobile_url, {
                        mobile: mobile,
                        phoneVerifyCode: phoneVerifyCode
                    }, function (data) {
                        if (data.state) {
                            validSuccessDom(validId.valid_phone_verify_code, errorMsgId.phone_verify_code_error_msg);
                        } else {
                            validErrorDom(validId.valid_phone_verify_code, errorMsgId.phone_verify_code_error_msg, data.msg);
                        }
                    });
                } else {
                    validErrorDom(validId.valid_phone_verify_code, errorMsgId.phone_verify_code_error_msg, '检测到您未更改手机号');
                }
            }
        });

        $(paramId.password).blur(function () {
            initParam();
            var password = param.password;
            if (!valid_regex.password_valid_regex.test(password)) {
                validErrorDom(validId.valid_password, errorMsgId.password_error_msg, msg.password_error_msg);
            } else {
                validSuccessDom(validId.valid_password, errorMsgId.password_error_msg);
            }
        });

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
         * 获取手机验证码
         */
        var InterValObj; //timer变量，控制时间
        var count = 120; //间隔函数，1秒执行
        var curCount;//当前剩余秒数
        var btnId = '#get_verification_code';
        $(btnId).click(function () {
            initParam();
            var mobile = param.mobile;
            if (valid_regex.mobile_valid_regex.test(mobile)) {
                if (mobile !== $('#mobile').val()) {

                    $.post(web_path + ajax_url.valid_users_url, {
                        username: param.username,
                        mobile: mobile,
                        validType: 2
                    }, function (data) {
                        if (data.state) {
                            validSuccessDom(validId.valid_mobile, errorMsgId.mobile_error_msg);
                            curCount = count;
                            //设置button效果，开始计时
                            $(btnId).attr("disabled", "true");
                            $(btnId).val(curCount + "秒后重新获取验证码");
                            InterValObj = window.setInterval(SetRemainTime, 1000); //启动计时器，1秒执行一次

                            $.get(web_path + ajax_url.mobile_code_url, {mobile: mobile}, function (data) {
                                if (data.state) {
                                    $(errorMsgId.phone_verify_code_error_msg).removeClass('hidden').text(data.msg);
                                } else {
                                    validErrorDom(validId.valid_phone_verify_code, errorMsgId.phone_verify_code_error_msg, data.msg);
                                }
                            });
                        } else {
                            validErrorDom(validId.valid_mobile, errorMsgId.mobile_error_msg, data.msg);
                        }
                    });

                } else {
                    validErrorDom(validId.valid_mobile, errorMsgId.mobile_error_msg, '检测到您未更改手机号');
                }

            } else {
                validErrorDom(validId.valid_mobile, errorMsgId.mobile_error_msg, msg.mobile_error_msg);
            }
        });

        //timer处理函数
        function SetRemainTime() {
            if (curCount === 0) {
                window.clearInterval(InterValObj);//停止计时器
                $(btnId).removeAttr("disabled");//启用按钮
                $(btnId).val("重新发送验证码");
            }
            else {
                curCount--;
                $(btnId).val(curCount + "秒后重新获取验证码");
            }
        }

        // 打开修改手机号模态框
        $('#mobileUpdate').click(function () {
            $(paramId.mobile).val($('#mobile').val());
            $('#mobileModal').modal('show');
        });

        // 提交手机号
        $('#mobile_submit').click(function () {
            validMobile();
        });

        function validMobile() {
            initParam();
            var mobile = param.mobile;
            if (!valid_regex.mobile_valid_regex.test(mobile)) {
                validErrorDom(validId.valid_mobile, errorMsgId.mobile_error_msg, msg.mobile_error_msg);
            } else {
                // ajax 检验
                $.post(web_path + ajax_url.valid_users_url, {
                    username: param.username,
                    mobile: mobile,
                    validType: 2
                }, function (data) {
                    if (data.state) {
                        validSuccessDom(validId.valid_mobile, errorMsgId.mobile_error_msg);
                        validPhoneVerifyCode();
                    } else {
                        validErrorDom(validId.valid_mobile, errorMsgId.mobile_error_msg, data.msg);
                    }
                });
            }
        }

        function validPhoneVerifyCode() {
            initParam();
            var phoneVerifyCode = param.phoneVerifyCode;
            var mobile = param.mobile;
            if (!valid_regex.phone_verify_code_valid_regex.test(phoneVerifyCode)) {
                validErrorDom(validId.valid_phone_verify_code, errorMsgId.phone_verify_code_error_msg, msg.phone_verify_code_error_msg);
            } else {
                if (mobile !== $('#mobile').val()) {
                    // ajax 检验 参数：手机号，验证码 后台保存这两个参数，在提交时再次检验
                    $.post(web_path + ajax_url.valid_mobile_url, {
                        mobile: mobile,
                        phoneVerifyCode: phoneVerifyCode
                    }, function (data) {
                        if (data.state) {
                            validSuccessDom(validId.valid_phone_verify_code, errorMsgId.phone_verify_code_error_msg);
                            sendMobile();
                        } else {
                            validErrorDom(validId.valid_phone_verify_code, errorMsgId.phone_verify_code_error_msg, data.msg);
                        }
                    });
                } else {
                    validErrorDom(validId.valid_phone_verify_code, errorMsgId.phone_verify_code_error_msg, '检测到您未更改手机号');
                }
            }
        }

        /**
         * 发送手机号
         */
        function sendMobile() {
            $.post(web_path + ajax_url.update_mobile_url, $('#mobile_form').serialize(), function (data) {
                if (data.state) {
                    var msg = Messenger().post({
                        message: "修改手机号成功，系统需要您重新登录",
                        actions: {
                            retry: {
                                label: '立刻退出',
                                phrase: '即将退出',
                                auto: true,
                                delay: 5,
                                action: function () {
                                    $('#logout').submit();
                                }
                            },
                            cancel: {
                                label: '取消',
                                action: function () {
                                    validCleanDom(validId.valid_mobile, errorMsgId.mobile_error_msg);
                                    validCleanDom(validId.valid_phone_verify_code, errorMsgId.phone_verify_code_error_msg);
                                    $('#mobileModal').modal('hide');
                                    return msg.cancel();
                                }
                            }
                        }
                    });
                } else {
                    Messenger().post({
                        message: data.msg,
                        type: 'error',
                        showCloseButton: true
                    });
                }
            });
        }

        // 打开修改密码模态框
        $('#passwordUpdate').click(function () {
            $('#passwordModal').modal('show');
        });

        // 提交密码
        $('#password_submit').click(function () {
            validPassword();
        });

        function validPassword() {
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
                    validSuccessDom(validId.valid_confirm_password, errorMsgId.confirm_password_error_msg);
                    sendPassword();
                }
            }
        }

        /**
         * 发送密码
         */
        function sendPassword() {
            $.post(web_path + ajax_url.update_password_url, $('#password_form').serialize(), function (data) {
                if (data.state) {
                    var msg = Messenger().post({
                        message: "修改密码成功，系统需要您重新登录",
                        actions: {
                            retry: {
                                label: '立刻退出',
                                phrase: '即将退出',
                                auto: true,
                                delay: 5,
                                action: function () {
                                    $('#logout').submit();
                                }
                            },
                            cancel: {
                                label: '取消',
                                action: function () {
                                    validCleanDom(validId.valid_password, errorMsgId.password_error_msg);
                                    validCleanDom(validId.valid_confirm_password, errorMsgId.confirm_password_error_msg);
                                    $(paramId.password).val('');
                                    $(paramId.confirmPassword).val('');
                                    $('#passwordModal').modal('hide');
                                    return msg.cancel();
                                }
                            }
                        }
                    });

                } else {
                    Messenger().post({
                        message: data.msg,
                        type: 'error',
                        showCloseButton: true
                    });
                }
            });
        }
    });