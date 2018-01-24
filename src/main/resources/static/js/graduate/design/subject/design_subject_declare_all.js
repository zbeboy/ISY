/**
 * Created by zbeboy on 2017/6/15.
 */
require(["jquery", "handlebars", "nav_active", "messenger", "jquery.address"],
    function ($, Handlebars, nav_active) {

        /*
         ajax url.
         */
        var ajax_url = {
            update: '/web/graduate/design/subject/declare/all/update',
            subject_type: '/anyone/graduate/design/subject/types',
            subject_origin_type: '/anyone/graduate/design/subject/origin_types',
            nav: '/web/menu/graduate/design/subject',
            back: '/web/graduate/design/subject/declare'
        };

        // 刷新时选中菜单
        nav_active(ajax_url.nav);

        /*
         参数id
         */
        var paramId = {
            staffId: '#staffId',
            oldSubjectUsesTimes: '#oldSubjectUsesTimes',
            guideTimes: '#guideTimes',
            guidePeoples: '#guidePeoples',
            subjectType: '#select_subject_type',
            originType: '#select_origin_type'
        };

        /*
         参数
         */
        var param = {
            oldSubjectUsesTimes: $(paramId.oldSubjectUsesTimes).val(),
            guideTimes: $(paramId.guideTimes).val(),
            guidePeoples: $(paramId.guidePeoples).val()
        };

        /*
         检验id
         */
        var validId = {
            oldSubjectUsesTimes: '#valid_old_subject_uses_times',
            guideTimes: '#valid_guide_times',
            guidePeoples: '#valid_guide_peoples'
        };

        /*
         错误消息id
         */
        var errorMsgId = {
            oldSubjectUsesTimes: '#old_subject_uses_times_error_msg',
            guideTimes: '#guide_times_error_msg',
            guidePeoples: '#guide_peoples_error_msg'
        };

        /*
         正则
         */
        var regex = {
            number_regex: /\d*/
        };

        /**
         * 检验成功
         * @param validId
         * @param errorMsgId
         */
        function validSuccessDom(validId, errorMsgId) {
            $(validId).addClass('has-success').removeClass('has-error');
            $(errorMsgId).addClass('hidden').text('');
        }

        /**
         * 检验失败
         * @param validId
         * @param errorMsgId
         * @param msg
         */
        function validErrorDom(validId, errorMsgId, msg) {
            $(validId).addClass('has-error').removeClass('has-success');
            $(errorMsgId).removeClass('hidden').text(msg);
        }

        /*
         清除验证
         */
        function validCleanDom(inputId, errorId) {
            $(inputId).removeClass('has-error').removeClass('has-success');
            $(errorId).addClass('hidden').text('');
        }

        /**
         * 初始化参数
         */
        function initParam() {
            param.oldSubjectUsesTimes = $(paramId.oldSubjectUsesTimes).val();
            param.guideTimes = $(paramId.guideTimes).val();
            param.guidePeoples = $(paramId.guidePeoples).val();
        }


        init();

        function init() {
            initSubjectType();
            initSubjectOriginType();
        }

        /**
         * 初始题目类型数据
         */
        function initSubjectType() {
            $.get(web_path + ajax_url.subject_type, function (data) {
                if (data.state) {
                    subjectTypeData(data);
                } else {
                    Messenger().post({
                        message: data.msg,
                        type: 'error',
                        showCloseButton: true
                    });
                }
            });
        }

        /**
         * 初始化课题来源数据
         */
        function initSubjectOriginType() {
            $.get(web_path + ajax_url.subject_origin_type, function (data) {
                if (data.state) {
                    subjectOriginTypeData(data);
                } else {
                    Messenger().post({
                        message: data.msg,
                        type: 'error',
                        showCloseButton: true
                    });
                }
            });
        }

        /**
         * 题目类型数据
         * @param data 数据
         */
        function subjectTypeData(data) {
            var template = Handlebars.compile($("#subject-type-template").html());
            $(paramId.subjectType).html(template(data));
        }

        /**
         * 题目来源数据
         * @param data 数据
         */
        function subjectOriginTypeData(data) {
            var template = Handlebars.compile($("#origin-type-template").html());
            $(paramId.originType).html(template(data));
        }

        /*
         即时检验
         */
        $(paramId.oldSubjectUsesTimes).blur(function () {
            initParam();
            var oldSubjectUsesTimes = param.oldSubjectUsesTimes;
            if (oldSubjectUsesTimes !== '' && !regex.number_regex.test(oldSubjectUsesTimes)) {
                validErrorDom(validId.oldSubjectUsesTimes, errorMsgId.oldSubjectUsesTimes, '请填写数字');
            }
        });

        $(paramId.guideTimes).blur(function () {
            initParam();
            var guideTimes = param.guideTimes;
            if (guideTimes !== '' && !regex.number_regex.test(guideTimes)) {
                validErrorDom(validId.guideTimes, errorMsgId.guideTimes, '请填写数字');
            }
        });

        $(paramId.guidePeoples).blur(function () {
            initParam();
            var guidePeoples = param.guidePeoples;
            if (guidePeoples !== '' && !regex.number_regex.test(guidePeoples)) {
                validErrorDom(validId.guidePeoples, errorMsgId.guidePeoples, '请填写数字');
            }
        });

        /*
         返回
         */
        $('#page_back').click(function () {
            $.address.value(ajax_url.back + '?id=' + init_page_param.graduationDesignReleaseId);
        });

        /*
         保存数据
         */
        $('#save').click(function () {
            add();
        });

        /*
         添加询问
         */
        function add() {
            initParam();
            var msg;
            msg = Messenger().post({
                message: "确定保存吗?",
                actions: {
                    retry: {
                        label: '确定',
                        phrase: 'Retrying TIME',
                        action: function () {
                            msg.cancel();
                            validOldSubjectUsesTimes();
                        }
                    },
                    cancel: {
                        label: '取消',
                        action: function () {
                            return msg.cancel();
                        }
                    }
                }
            });
        }

        /**
         * 检验旧题使用次数
         */
        function validOldSubjectUsesTimes() {
            var oldSubjectUsesTimes = param.oldSubjectUsesTimes;
            if (oldSubjectUsesTimes !== '' && !regex.number_regex.test(oldSubjectUsesTimes)) {
                Messenger().post({
                    message: '旧题使用次数应为数字',
                    type: 'error',
                    showCloseButton: true
                });
            } else {
                validGuideTimes();
            }
        }

        /**
         * 检验指导次数
         */
        function validGuideTimes() {
            var guideTimes = param.guideTimes;
            if (guideTimes !== '' && !regex.number_regex.test(guideTimes)) {
                Messenger().post({
                    message: '指导次数应为数字',
                    type: 'error',
                    showCloseButton: true
                });
            } else {
                validGuidePeoples();
            }
        }

        /**
         * 检验指导学生人数
         */
        function validGuidePeoples() {
            var guidePeoples = param.guidePeoples;
            if (guidePeoples !== '' && !regex.number_regex.test(guidePeoples)) {
                Messenger().post({
                    message: '指导学生人数应为数字',
                    type: 'error',
                    showCloseButton: true
                });
            } else {
                sendAjax();
            }
        }

        /**
         * 发送数据到后台
         */
        function sendAjax() {
            Messenger().run({
                successMessage: '保存数据成功',
                errorMessage: '保存数据失败',
                progressMessage: '正在保存数据....'
            }, {
                url: web_path + ajax_url.update,
                type: 'post',
                data: $('#edit_form').serialize(),
                success: function (data) {
                    if (data.state) {
                        window.history.go(-1);
                    } else {
                        Messenger().post({
                            message: data.msg,
                            type: 'error',
                            showCloseButton: true
                        });
                    }
                },
                error: function (xhr) {
                    if ((xhr != null ? xhr.status : void 0) === 404) {
                        return "请求失败";
                    }
                    return true;
                }
            });
        }

    });