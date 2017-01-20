/**
 * Created by lenovo on 2016-11-02.
 */
//# sourceURL=users_profile_system_edit.js
require(["jquery", "jquery.cropper.upload", "jquery.showLoading", "messenger", "bootstrap", "jquery.address",
        "bootstrap-datetimepicker-zh-CN"],
    function ($, cropper) {

        /*
         ajax url
         */
        var ajax_url = {
            avatar_review_url: '/anyone/users/review/avatar',
            update: '/anyone/users/update',
            back: '/anyone/users/profile'
        };

        function startLoading() {
            // 显示遮罩
            $('#page-wrapper').showLoading();
        }

        function endLoading() {
            // 去除遮罩
            $('#page-wrapper').hideLoading();
        }

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
         消息
         */
        var msg = {
            real_name_error_msg: '请填写姓名'
        };

        /*
         参数id
         */
        var paramId = {
            realName: '#realName',
            username: '#username'
        };

        /*
         参数
         */
        var param = {
            realName: $(paramId.realName).val().trim(),
            username: $(paramId.username).val().trim()
        };

        /*
         验证form id
         */
        var validId = {
            valid_real_name: '#valid_real_name'
        };

        /*
         错误消息 id
         */
        var errorMsgId = {
            real_name_error_msg: '#real_name_error_msg'
        };

        /*
         初始化参数
         */
        function initParam() {
            param.realName = $(paramId.realName).val().trim();
            param.username = $(paramId.username).val().trim();
        }

        // 初始化生日
        $('.form_date').datetimepicker({
            language: 'zh-CN',
            weekStart: 1,
            todayBtn: 1,
            autoclose: 1,
            todayHighlight: 1,
            startView: 2,
            minView: 2,
            forceParse: 0
        });

        cropper($('#page-wrapper'), web_path + ajax_url.avatar_review_url);

        // 清除头像
        $('#cleanAvatar').click(function () {
            $('#avatar').attr('src', web_path + '/images/avatar.jpg');
            $('#form_avatar').val('images/avatar.jpg');
        });

        /*
         返回
         */
        $('#page_back').click(function () {
            $.address.value(ajax_url.back);
        });

        // 即时检验
        $(paramId.realName).blur(function () {
            initParam();
            var realName = param.realName;
            if (realName === '') {
                validErrorDom(validId.valid_real_name, errorMsgId.real_name_error_msg, msg.real_name_error_msg);
            } else {
                validSuccessDom(validId.valid_real_name, errorMsgId.real_name_error_msg);
            }
        });

        /**
         * 表单提交时检验
         */
        $('#save').click(function () {
            // 显示遮罩
            startLoading();
            validRealName();
        });

        function validRealName() {
            initParam();
            var realName = param.realName;
            if (realName === '') {
                // 去除遮罩
                endLoading();
                validErrorDom(validId.valid_real_name, errorMsgId.real_name_error_msg, msg.real_name_error_msg);
            } else {
                validSuccessDom(validId.valid_real_name, errorMsgId.real_name_error_msg);
                sendAjax();
            }
        }

        /*
         发送数据到后台保存
         */
        function sendAjax() {
            $.post(web_path + ajax_url.update, $('#edit_form').serialize(), function (data) {
                if (data.state) {
                    // 去除遮罩
                    endLoading();
                    $.address.value(ajax_url.back);
                } else {
                    Messenger().post({
                        message: '更新数据失败',
                        type: 'error',
                        showCloseButton: true
                    });
                }
            });
        }

    });