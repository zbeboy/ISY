/**
 * Created by lenovo on 2016-11-02.
 */
require(["jquery", "jquery.showLoading", "messenger", "bootstrap", "jquery.address",
        "bootstrap-datetimepicker-zh-CN", "jquery.fileupload-validate"],
    function ($) {

        /*
         ajax url
         */
        var ajax_url = {
            file_upload_url: '/anyone/users/upload/avatar',
            download_avatar_url: '/anyone/users/download/avatar',
            avatar_preview_url: '/anyone/users/avatar/preview',
            update: '/anyone/users/update',
            back: '/anyone/users/profile'
        };

        function startLoading() {
            // 显示遮罩
            $('#loading_region').showLoading();
        }

        function endLoading() {
            // 去除遮罩
            $('#loading_region').hideLoading();
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
         参数id
         */
        var paramId = {
            username: '#username'
        };

        /*
         参数
         */
        var param = {
            username: $(paramId.username).val().trim()
        };

        /*
         初始化参数
         */
        function initParam() {
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

        // 上传组件
        $('#fileupload').fileupload({
            url: web_path + ajax_url.file_upload_url,
            dataType: 'json',
            maxFileSize: 10000000,// 10MB
            acceptFileTypes: /(\.|\/)(gif|jpe?g|png)$/i,
            done: function (e, data) {
                initParam();
                $.each(data.result.listResult, function (index, file) {
                    $('#avatar').attr('src', web_path + ajax_url.avatar_preview_url + "?username=" + param.username + '&fileName=' + file.newName);
                    $('#form_avatar').val(file.relativePath +  file.newName);
                });
            },
            progressall: function (e, data) {
                var progress = parseInt(data.loaded / data.total * 100, 10);
                $('#progress').find('.progress-bar').css(
                    'width',
                    progress + '%'
                );
            }
        });

        // 清除头像
        $('#cleanAvatar').click(function () {
            $('#avatar').attr('src', web_path + '/images/avatar.jpg');
            $('#form_avatar').val('');
        });

        /*
         返回
         */
        $('#page_back').click(function () {
            $.address.value(ajax_url.back);
        });

        /**
         * 表单提交时检验
         */
        $('#save').click(function () {
            // 显示遮罩
            startLoading();
            sendAjax();
        });

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