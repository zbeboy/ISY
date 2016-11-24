/**
 * Created by lenovo on 2016/11/24.
 */
//# sourceURL=internship_edit_distribution.js
require(["jquery", "handlebars", "nav_active", "messenger", "jquery.address", "bootstrap-select-zh-CN", "bootstrap-duallistbox"],
    function ($, Handlebars, nav_active) {
        /*
         ajax url.
         */
        var ajax_url = {
            update: '/web/internship/teacher_distribution/update',
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
            studentId: '#studentId',
            staffId: '#select_teacher'
        };

        /*
         参数
         */
        var param = {
            studentId: $(paramId.studentId).val().trim(),
            staffId: $(paramId.staffId).val().trim(),
            id: init_page_param.internshipReleaseId
        };

        /**
         * 初始化参数
         */
        function initParam() {
            param.studentId = $(paramId.studentId).val().trim();
            param.staffId = $(paramId.staffId).val().trim();
            param.id = init_page_param.internshipReleaseId;
        }

        init();

        /**
         * 初始化数据
         */
        function init() {
            $.get(web_path + ajax_url.teacher_data_url, {id: init_page_param.internshipReleaseId}, function (data) {
                staffData(data);
            });
        }

        /**
         * 教职工数据
         * @param data
         */
        function staffData(data) {
            var source = $("#teacher-template").html();
            var template = Handlebars.compile(source);

            Handlebars.registerHelper('teacher_value', function () {
                var value = Handlebars.escapeExpression(this.staffId);
                return new Handlebars.SafeString(value);
            });

            Handlebars.registerHelper('teacher_name', function () {
                var name = Handlebars.escapeExpression(this.staffName);
                return new Handlebars.SafeString(name);
            });

            var html = template(data);
            $(paramId.staffId).html(html);
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
         保存询问
         */
        function add() {
            initParam();
            var msg;
            msg = Messenger().post({
                message: "确定更改该学生账号吗?",
                actions: {
                    retry: {
                        label: '确定',
                        phrase: 'Retrying TIME',
                        action: function () {
                            msg.cancel();
                            sendAjax();
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
                data: param,
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