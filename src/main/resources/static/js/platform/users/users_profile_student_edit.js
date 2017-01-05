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
            valid_student_url: '/anyone/users/valid/student',
            valid_id_card_url: '/anyone/users/valid/id_card',
            update: '/anyone/users/student/update',
            back: '/anyone/users/profile'
        };

        /*
         正则
         */
        var valid_regex = {
            student_number_valid_regex: /^\d{13,}$/,
            id_card_valid_regex: /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/,
            parent_contact_phone_regex: /^1[0-9]{10}/
        };

        /*
         消息
         */
        var msg = {
            student_number_error_msg: '学号至少13位数字',
            id_card_error_msg: '身份证号不正确',
            parent_contact_phone_error_msg: '手机号不正确'
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
         参数id
         */
        var paramId = {
            username: '#username',
            studentNumber: '#studentNumber',
            idCard: '#idCard',
            parentContactPhone: '#parentContactPhone',
            select_nation: '#select_nation',
            select_political_landscape: '#select_political_landscape'
        };

        /*
         参数
         */
        var param = {
            username: $(paramId.username).val().trim(),
            studentNumber: $(paramId.studentNumber).val().trim(),
            idCard: $(paramId.idCard).val().trim(),
            parentContactPhone: $(paramId.parentContactPhone).val().trim()
        };

        /*
         初始化参数
         */
        function initParam() {
            param.username = $(paramId.username).val().trim();
            param.studentNumber = $(paramId.studentNumber).val().trim();
            param.idCard = $(paramId.idCard).val().trim();
            param.parentContactPhone = $(paramId.parentContactPhone).val().trim();
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
                    $('#avatar').attr('src', web_path + '/' + data.result.objectResult + file.newName);
                    $('#form_avatar').val(data.result.objectResult + file.newName);
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
         验证form id
         */
        var validId = {
            valid_student_number: '#valid_student_number',
            valid_id_card: '#valid_id_card',
            valid_parent_contact_phone: '#valid_parent_contact_phone'
        };

        /*
         错误消息 id
         */
        var errorMsgId = {
            student_number_error_msg: '#student_number_error_msg',
            id_card_error_msg: '#id_card_error_msg',
            parent_contact_phone_error_msg: '#parent_contact_phone_error_msg'
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

        // 即时检验
        $(paramId.studentNumber).blur(function () {
            initParam();
            var studentNumber = param.studentNumber;
            if (!valid_regex.student_number_valid_regex.test(studentNumber)) {
                validErrorDom(validId.valid_student_number, errorMsgId.student_number_error_msg, msg.student_number_error_msg);
            } else {
                // ajax 检验
                $.post(web_path + ajax_url.valid_student_url, param, function (data) {
                    if (data.state) {
                        validSuccessDom(validId.valid_student_number, errorMsgId.student_number_error_msg);
                    } else {
                        validErrorDom(validId.valid_student_number, errorMsgId.student_number_error_msg, '该学号已被注册');
                    }
                });
            }
        });

        $(paramId.idCard).blur(function () {
            initParam();
            var idCard = param.idCard;
            if (idCard.length > 0) {
                if (!valid_regex.id_card_valid_regex.test(idCard)) {
                    validErrorDom(validId.valid_id_card, errorMsgId.id_card_error_msg, msg.id_card_error_msg);
                } else {
                    // ajax 检验
                    $.post(web_path + ajax_url.valid_id_card_url, param, function (data) {
                        if (data.state) {
                            validSuccessDom(validId.valid_id_card, errorMsgId.id_card_error_msg);
                        } else {
                            validErrorDom(validId.valid_id_card, errorMsgId.id_card_error_msg, '该身份证已被使用');
                        }
                    });
                }
            } else {
                validCleanDom(validId.valid_id_card, errorMsgId.id_card_error_msg);
            }
        });

        $(paramId.parentContactPhone).blur(function () {
            initParam();
            var parentContactPhone = param.parentContactPhone;
            if (parentContactPhone.length > 0) {
                if (!valid_regex.parent_contact_phone_regex.test(parentContactPhone)) {
                    validErrorDom(validId.valid_parent_contact_phone, errorMsgId.parent_contact_phone_error_msg, msg.parent_contact_phone_error_msg);
                } else {
                    validSuccessDom(validId.valid_parent_contact_phone, errorMsgId.parent_contact_phone_error_msg);
                }
            } else {
                validCleanDom(validId.valid_parent_contact_phone, errorMsgId.parent_contact_phone_error_msg);
            }
        });

        /**
         * 表单提交时检验
         */
        $('#save').click(function () {
            // 显示遮罩
            startLoading();
            validStudentNumber();
        });

        /**
         * 检验数据是否正常
         */
        function validStudentNumber() {
            initParam();
            var studentNumber = param.studentNumber;
            if (!valid_regex.student_number_valid_regex.test(studentNumber)) {
                validErrorDom(validId.valid_student_number, errorMsgId.student_number_error_msg, msg.student_number_error_msg);
                // 去除遮罩
                endLoading();
            } else {
                // ajax 检验
                $.post(web_path + ajax_url.valid_student_url, param, function (data) {
                    if (data.state) {
                        validSuccessDom(validId.valid_student_number, errorMsgId.student_number_error_msg);
                        validIdCard();
                    } else {
                        validErrorDom(validId.valid_student_number, errorMsgId.student_number_error_msg, '该学号已被注册');
                        // 去除遮罩
                        endLoading();
                    }
                });
            }
        }

        /**
         * 检验身份证号
         */
        function validIdCard() {
            initParam();
            var idCard = param.idCard;
            if (idCard.length > 0) {
                if (!valid_regex.id_card_valid_regex.test(idCard)) {
                    validErrorDom(validId.valid_id_card, errorMsgId.id_card_error_msg, msg.id_card_error_msg);
                    // 去除遮罩
                    endLoading();
                } else {
                    // ajax 检验
                    $.post(web_path + ajax_url.valid_id_card_url, param, function (data) {
                        if (data.state) {
                            validSuccessDom(validId.valid_id_card, errorMsgId.id_card_error_msg);
                            validParentContactPhone();
                        } else {
                            validErrorDom(validId.valid_id_card, errorMsgId.id_card_error_msg, '该身份证已被使用');
                            // 去除遮罩
                            endLoading();
                        }
                    });
                }
            } else {
                validCleanDom(validId.valid_id_card, errorMsgId.id_card_error_msg);
                validParentContactPhone();
            }
        }

        function validParentContactPhone() {
            initParam();
            var parentContactPhone = param.parentContactPhone;
            if (parentContactPhone.length > 0) {
                if (!valid_regex.parent_contact_phone_regex.test(parentContactPhone)) {
                    validErrorDom(validId.valid_parent_contact_phone, errorMsgId.parent_contact_phone_error_msg, msg.parent_contact_phone_error_msg);
                    // 去除遮罩
                    endLoading();
                } else {
                    validSuccessDom(validId.valid_parent_contact_phone, errorMsgId.parent_contact_phone_error_msg);
                    sendAjax();
                }
            } else {
                validCleanDom(validId.valid_parent_contact_phone, errorMsgId.parent_contact_phone_error_msg);
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