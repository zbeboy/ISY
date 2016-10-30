/**
 * Created by lenovo on 2016-10-29.
 */
//# sourceURL=users_profile_student_edit.js
require(["jquery", "handlebars", "jquery.showLoading", "messenger", "bootstrap", "jquery.address",
        "bootstrap-datetimepicker-zh-CN", "jquery.fileupload-validate"],
    function ($, Handlebars) {

        /*
         ajax url
         */
        var ajax_url = {
            nation_data_url: '/user/nations',
            political_landscape_data_url: '/user/political_landscapes',
            file_upload_url: '/anyone/users/upload/avatar',
            download_avatar_url: '/anyone/users/download/avatar',
            avatar_preview_url:'/anyone/users/avatar/preview',
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
            username: '#username',
            studentNumber: '#studentNumber',
            select_nation: '#select_nation',
            select_political_landscape: '#select_political_landscape'
        };

        /*
         参数
         */
        var param = {
            username: $(paramId.username).val().trim(),
            studentNumber: $(paramId.studentNumber).val().trim()
        };

        /*
         初始化参数
         */
        function initParam() {
            param.username = $(paramId.username).val().trim();
            param.studentNumber = $(paramId.studentNumber).val().trim();
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
                    console.log(file.relativePath + '/' + file.newName);
                    $('#form_avatar').val(file.relativePath + '/' + file.newName);
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

        /*
         验证form id
         */
        var validId = {
            valid_student_number: '#valid_student_number'
        };

        /*
         错误消息 id
         */
        var errorMsgId = {
            student_number_error_msg: '#student_number_error_msg'
        };

        /*
         页面加载时初始化选中
         */
        var selectedNationCount = true;
        var selectedPoliticalLandscapeCount = true;

        /**
         * 选中民族
         */
        function selectedNation() {
            var realNationId = $('#nationId').val();
            var nationChildrens = $(paramId.select_nation).children();
            for (var i = 0; i < nationChildrens.length; i++) {
                if ($(nationChildrens[i]).val() === realNationId) {
                    $(nationChildrens[i]).prop('selected', true);
                    break;
                }
            }
        }

        /**
         * 选中政治面貌
         */
        function selectedPoliticalLandscape() {
            var realPoliticalLandscapeId = $('#politicalLandscapeId').val();
            var politicalLandscapeChildrens = $(paramId.select_political_landscape).children();
            for (var i = 0; i < politicalLandscapeChildrens.length; i++) {
                if ($(politicalLandscapeChildrens[i]).val() === realPoliticalLandscapeId) {
                    $(politicalLandscapeChildrens[i]).prop('selected', true);
                    break;
                }
            }
        }

        /**
         * 初始化
         */
        function init() {
            $.get(web_path + ajax_url.nation_data_url, function (data) {
                nationData(data);
            });

            $.get(web_path + ajax_url.political_landscape_data_url, function (data) {
                politicalLandscapeData(data);
            });
        }

        init();

        /**
         * 民族数据展现
         * @param data json数据
         */
        function nationData(data) {
            var source = $("#nation-template").html();
            var template = Handlebars.compile(source);

            Handlebars.registerHelper('nation_value', function () {
                var value = Handlebars.escapeExpression(this.nationId);
                return new Handlebars.SafeString(value);
            });

            Handlebars.registerHelper('nation_name', function () {
                var name = Handlebars.escapeExpression(this.nationName);
                return new Handlebars.SafeString(name);
            });

            var html = template(data);
            $(paramId.select_nation).html(html);
            if (selectedNationCount) {
                selectedNation();
                selectedNationCount = false;
            }
            // 去除遮罩
            endLoading();
        }

        /**
         * 政治面貌数据展现
         * @param data json数据
         */
        function politicalLandscapeData(data) {
            var source = $("#political-landscape-template").html();
            var template = Handlebars.compile(source);

            Handlebars.registerHelper('political_landscape_value', function () {
                var value = Handlebars.escapeExpression(this.politicalLandscapeId);
                return new Handlebars.SafeString(value);
            });

            Handlebars.registerHelper('political_landscape_name', function () {
                var name = Handlebars.escapeExpression(this.politicalLandscapeName);
                return new Handlebars.SafeString(name);
            });

            var html = template(data);
            $(paramId.select_political_landscape).html(html);
            if (selectedPoliticalLandscapeCount) {
                selectedPoliticalLandscape();
                selectedPoliticalLandscapeCount = false;
            }
            // 去除遮罩
            endLoading();
        }

        /*
         返回
         */
        $('#page_back').click(function () {
            $.address.value(ajax_url.back);
        });

    });