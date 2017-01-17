/**
 * Created by lenovo on 2016/11/23.
 */
//# sourceURL=internship_add_distribution.js
require(["jquery", "handlebars", "nav_active", "messenger", "jquery.address",
        "bootstrap-select-zh-CN", "bootstrap-duallistbox", "jquery.showLoading"],
    function ($, Handlebars, nav_active) {

        /*
         ajax url.
         */
        var ajax_url = {
            save: '/web/internship/teacher_distribution/save',
            valid_student: '/web/internship/teacher_distribution/save/valid/student',
            teacher_data_url: '/web/internship/teacher_distribution/batch/distribution/teachers',
            nav: '/web/menu/internship/teacher_distribution',
            back: '/web/internship/teacher_distribution/distribution/condition'
        };

        // 刷新时选中菜单
        nav_active(ajax_url.nav);

        /*
         参数id
         */
        var paramId = {
            studentUsername: '#studentUsername',
            studentNumber: '#studentNumber',
            staffId: '#select_teacher'
        };

        /*
         参数
         */
        var param = {
            studentUsername: $(paramId.studentUsername).val(),
            studentNumber: $(paramId.studentNumber).val(),
            staffId: $(paramId.staffId).val()
        };

        /*
         检验id
         */
        var validId = {
            student: '#valid_student',
        };

        /*
         错误消息id
         */
        var errorMsgId = {
            student: '#student_error_msg'
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

        function startLoading() {
            // 显示遮罩
            $('#page-wrapper').showLoading();
        }

        function endLoading() {
            // 去除遮罩
            $('#page-wrapper').hideLoading();
        }

        /**
         * 初始化参数
         */
        function initParam() {
            param.studentUsername = $(paramId.studentUsername).val();
            param.studentNumber = $(paramId.studentNumber).val();
            param.staffId = $(paramId.staffId).val();
        }

        init();

        /**
         * 初始化数据
         */
        function init() {
            startLoading();
            $.get(web_path + ajax_url.teacher_data_url, {id: init_page_param.internshipReleaseId}, function (data) {
                endLoading();
                staffData(data);
            });
        }

        /**
         * 教职工数据
         * @param data
         */
        function staffData(data) {
            var template = Handlebars.compile($("#teacher-template").html());

            Handlebars.registerHelper('teacher_value', function () {
                return new Handlebars.SafeString(Handlebars.escapeExpression(this.staffId));
            });

            Handlebars.registerHelper('teacher_name', function () {
                return new Handlebars.SafeString(Handlebars.escapeExpression(this.realName + ' ' + this.staffNumber));
            });

            $(paramId.staffId).html(template(data));
            initStaffSelect();
        }

        function initStaffSelect() {
            $(paramId.staffId).selectpicker({
                liveSearch: true,
                maxOptions: 1
            });
        }

        /*
         返回
         */
        $('#page_back').click(function () {
            $.address.value(ajax_url.back + '?id=' + init_page_param.internshipReleaseId);
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
                message: "确定添加该学生账号到实习中吗?",
                actions: {
                    retry: {
                        label: '确定',
                        phrase: 'Retrying TIME',
                        action: function () {
                            msg.cancel();
                            validStudent();
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
         * 检验学生信息
         */
        function validStudent() {
            initParam();
            var studentUsername = param.studentUsername;
            var studentNumber = param.studentNumber;
            if (studentUsername.length <= 0 && studentNumber.length <= 0) {
                Messenger().post({
                    message: '请至少填写一项学生信息',
                    type: 'error',
                    showCloseButton: true
                });
            } else {
                var student = "";
                var type = -1;
                if (studentUsername.length > 0) {
                    student = studentUsername;
                    type = 0;
                }

                if (studentNumber.length > 0) {
                    student = studentNumber;
                    type = 1;
                }

                // 检验学生信息
                Messenger().run({
                    errorMessage: '请求失败'
                }, {
                    url: web_path + ajax_url.valid_student,
                    type: 'post',
                    data: {id: init_page_param.internshipReleaseId, student: student, type: type},
                    success: function (data) {
                        if (data.state) {
                            sendAjax();
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
        }

        /**
         * 发送数据到后台
         */
        function sendAjax() {
            var student = "";
            var type = -1;
            var studentUsername = param.studentUsername;
            var studentNumber = param.studentNumber;
            if (studentUsername.length > 0) {
                student = studentUsername;
                type = 0;
            }

            if (studentNumber.length > 0) {
                student = studentNumber;
                type = 1;
            }
            var params = {
                student: student,
                staffId: param.staffId,
                type: type,
                id: init_page_param.internshipReleaseId
            };
            Messenger().run({
                successMessage: '保存数据成功',
                errorMessage: '保存数据失败',
                progressMessage: '正在保存数据....'
            }, {
                url: web_path + ajax_url.save,
                type: 'post',
                data: params,
                success: function (data) {
                    if (data.state) {
                        $.address.value(ajax_url.back + '?id=' + init_page_param.internshipReleaseId);
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