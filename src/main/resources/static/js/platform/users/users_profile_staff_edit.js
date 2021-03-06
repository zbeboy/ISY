/**
 * Created by lenovo on 2016-11-02.
 */
//# sourceURL=users_profile_staff_edit.js
require(["jquery", "handlebars", "jquery.cropper.upload", "jquery.showLoading", "messenger", "bootstrap", "jquery.address",
        "bootstrap-datetimepicker-zh-CN"],
    function ($, Handlebars, cropper) {

        /*
         ajax url
         */
        var ajax_url = {
            nation_data_url: '/user/nations',
            political_landscape_data_url: '/user/political_landscapes',
            academic_data_url:'/user/academic',
            avatar_review_url: '/anyone/users/review/avatar',
            valid_staff_url: '/anyone/users/valid/staff',
            valid_id_card_url: '/anyone/users/valid/id_card',
            update: '/anyone/users/staff/update',
            back: '/anyone/users/profile'
        };

        /*
         正则
         */
        var valid_regex = {
            staff_number_valid_regex: /^\d{8,}$/,
            id_card_valid_regex: /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/
        };

        /*
         消息
         */
        var msg = {
            real_name_error_msg: '请填写姓名',
            staff_number_error_msg: '工号至少8位数字',
            id_card_error_msg: '身份证号不正确'
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
            realName: '#realName',
            username: '#username',
            staffNumber: '#staffNumber',
            idCard: '#idCard',
            select_nation: '#select_nation',
            select_political_landscape: '#select_political_landscape',
            select_academic:'#select_academic'
        };

        /*
         参数
         */
        var param = {
            realName: $(paramId.realName).val().trim(),
            username: $(paramId.username).val().trim(),
            staffNumber: $(paramId.staffNumber).val().trim(),
            idCard: $(paramId.idCard).val().trim()
        };

        /*
         初始化参数
         */
        function initParam() {
            param.realName = $(paramId.realName).val().trim();
            param.username = $(paramId.username).val().trim();
            param.staffNumber = $(paramId.staffNumber).val().trim();
            param.idCard = $(paramId.idCard).val().trim();
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
         验证form id
         */
        var validId = {
            valid_real_name: '#valid_real_name',
            valid_staff_number: '#valid_staff_number',
            valid_id_card: '#valid_id_card'
        };

        /*
         错误消息 id
         */
        var errorMsgId = {
            real_name_error_msg: '#real_name_error_msg',
            staff_number_error_msg: '#staff_number_error_msg',
            id_card_error_msg: '#id_card_error_msg'
        };

        /*
         页面加载时初始化选中
         */
        var selectedNationCount = true;
        var selectedPoliticalLandscapeCount = true;
        var selectedAcademicCount = true;

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
         * 选中职称
         */
        function selectedAcademic() {
            var realAcademicTitleId = $('#academicTitleId').val();
            var academicChildrens = $(paramId.select_academic).children();
            for (var i = 0; i < academicChildrens.length; i++) {
                if ($(academicChildrens[i]).val() === realAcademicTitleId) {
                    $(academicChildrens[i]).prop('selected', true);
                    break;
                }
            }
        }

        /**
         * 初始化
         */
        function init() {
            startLoading();
            $.get(web_path + ajax_url.nation_data_url, function (data) {
                endLoading();
                nationData(data);
            });

            $.get(web_path + ajax_url.political_landscape_data_url, function (data) {
                politicalLandscapeData(data);
            });

            $.get(web_path + ajax_url.academic_data_url, function (data) {
                academicData(data);
            });
        }

        init();

        /**
         * 民族数据展现
         * @param data json数据
         */
        function nationData(data) {
            var template = Handlebars.compile($("#nation-template").html());

            Handlebars.registerHelper('nation_value', function () {
                return new Handlebars.SafeString(Handlebars.escapeExpression(this.nationId));
            });

            Handlebars.registerHelper('nation_name', function () {
                return new Handlebars.SafeString(Handlebars.escapeExpression(this.nationName));
            });

            $(paramId.select_nation).html(template(data));
            if (selectedNationCount) {
                selectedNation();
                selectedNationCount = false;
            }
        }

        /**
         * 政治面貌数据展现
         * @param data json数据
         */
        function politicalLandscapeData(data) {
            var template = Handlebars.compile($("#political-landscape-template").html());

            Handlebars.registerHelper('political_landscape_value', function () {
                return new Handlebars.SafeString(Handlebars.escapeExpression(this.politicalLandscapeId));
            });

            Handlebars.registerHelper('political_landscape_name', function () {
                return new Handlebars.SafeString(Handlebars.escapeExpression(this.politicalLandscapeName));
            });

            $(paramId.select_political_landscape).html(template(data));
            if (selectedPoliticalLandscapeCount) {
                selectedPoliticalLandscape();
                selectedPoliticalLandscapeCount = false;
            }
        }

        /**
         * 职称数据展现
         * @param data json数据
         */
        function academicData(data) {
            var template = Handlebars.compile($("#academic-template").html());

            Handlebars.registerHelper('academic_value', function () {
                return new Handlebars.SafeString(Handlebars.escapeExpression(this.academicTitleId));
            });

            Handlebars.registerHelper('academic_name', function () {
                return new Handlebars.SafeString(Handlebars.escapeExpression(this.academicTitleName));
            });

            $(paramId.select_academic).html(template(data));
            if (selectedAcademicCount) {
                selectedAcademic();
                selectedAcademicCount = false;
            }
        }

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

        // 即时检验
        $(paramId.staffNumber).blur(function () {
            initParam();
            var staffNumber = param.staffNumber;
            if (!valid_regex.staff_number_valid_regex.test(staffNumber)) {
                validErrorDom(validId.valid_staff_number, errorMsgId.staff_number_error_msg, msg.staff_number_error_msg);
            } else {
                // ajax 检验
                $.post(web_path + ajax_url.valid_staff_url, param, function (data) {
                    if (data.state) {
                        validSuccessDom(validId.valid_staff_number, errorMsgId.staff_number_error_msg);
                    } else {
                        validErrorDom(validId.valid_staff_number, errorMsgId.staff_number_error_msg, '该工号已被注册');
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
                validStaffNumber();
            }
        }

        /**
         * 检验数据是否正常
         */
        function validStaffNumber() {
            initParam();
            var staffNumber = param.staffNumber;
            if (!valid_regex.staff_number_valid_regex.test(staffNumber)) {
                validErrorDom(validId.valid_staff_number, errorMsgId.staff_number_error_msg, msg.staff_number_error_msg);
                // 去除遮罩
                endLoading();
            } else {
                // ajax 检验
                $.post(web_path + ajax_url.valid_staff_url, param, function (data) {
                    if (data.state) {
                        validSuccessDom(validId.valid_staff_number, errorMsgId.staff_number_error_msg);
                        validIdCard();
                    } else {
                        validErrorDom(validId.valid_staff_number, errorMsgId.staff_number_error_msg, '该工号已被注册');
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
                            sendAjax();
                        } else {
                            validErrorDom(validId.valid_id_card, errorMsgId.id_card_error_msg, '该身份证已被使用');
                            // 去除遮罩
                            endLoading();
                        }
                    });
                }
            } else {
                validCleanDom(validId.valid_id_card, errorMsgId.id_card_error_msg);
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