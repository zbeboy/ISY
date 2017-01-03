/**
 * Created by lenovo on 2016/11/23.
 */
//# sourceURL=internship_batch_distribution.js
require(["jquery", "handlebars", "nav_active", "lodash", "messenger", "jquery.address", "jquery.showLoading", "bootstrap-duallistbox"],
    function ($, Handlebars, nav_active, D) {

        /*
         ajax url.
         */
        var ajax_url = {
            organize_data_url: '/web/internship/teacher_distribution/batch/distribution/organizes',
            teacher_data_url: '/web/internship/teacher_distribution/batch/distribution/teachers',
            nav: '/web/menu/internship/teacher_distribution',
            save: '/web/internship/teacher_distribution/batch/distribution/save',
            back: '/web/internship/teacher_distribution/distribution/condition'
        };

        // 刷新时选中菜单
        nav_active(ajax_url.nav);

        /*
         参数id
         */
        var paramId = {
            organizeId: '#select_organize',
            staffId: '#select_teacher'
        };

        /*
         参数
         */
        var param = {
            organizeId: $(paramId.organizeId).val(),
            staffId: $(paramId.staffId).val(),
            internshipReleaseId: init_page_param.internshipReleaseId
        };

        /**
         * 初始化参数
         */
        function initParam() {
            param.organizeId = $(paramId.organizeId).val();
            param.staffId = $(paramId.staffId).val();
            param.internshipReleaseId = init_page_param.internshipReleaseId;
        }

        init();

        /**
         * 初始化数据
         */
        function init() {
            $.get(web_path + ajax_url.organize_data_url, {id: init_page_param.internshipReleaseId}, function (data) {
                organizeData(data);
            });

            $.get(web_path + ajax_url.teacher_data_url, {id: init_page_param.internshipReleaseId}, function (data) {
                staffData(data);
            });
        }

        /**
         * 班级数据
         * @param data
         */
        function organizeData(data) {
            var source = $("#organize-template").html();
            var template = Handlebars.compile(source);

            Handlebars.registerHelper('organize_value', function () {
                var value = Handlebars.escapeExpression(this.organizeId);
                return new Handlebars.SafeString(value);
            });

            Handlebars.registerHelper('organize_name', function () {
                var name = '';
                if (D.includes(data.mapResult.hasOrganizes, this.organizeId)) {
                    name = Handlebars.escapeExpression(this.organizeName + '  已分配');
                } else {
                    name = Handlebars.escapeExpression(this.organizeName);
                }
                return new Handlebars.SafeString(name);
            });

            var html = template(data.mapResult);
            $(paramId.organizeId).html(html);
            initOrganizeDualListbox();
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
                var name = Handlebars.escapeExpression(this.realName + ' ' + this.staffNumber);
                return new Handlebars.SafeString(name);
            });

            var html = template(data);
            $(paramId.staffId).html(html);
            initStaffDualListbox();
        }

        /**
         * 初始化班级双列表
         */
        function initOrganizeDualListbox() {
            $(paramId.organizeId).bootstrapDualListbox();
        }

        /**
         * 初始化教职工双列表
         */
        function initStaffDualListbox() {
            $(paramId.staffId).bootstrapDualListbox();
        }

        /*
         返回
         */
        $('#page_back').click(function () {
            $.address.value(ajax_url.back + '?id=' + param.internshipReleaseId);
        });

        /*
         保存
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
                message: "确定分配吗?",
                actions: {
                    retry: {
                        label: '确定',
                        phrase: 'Retrying TIME',
                        action: function () {
                            msg.cancel();
                            validOrganizeId();
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
         * 检验班级id
         */
        function validOrganizeId() {
            initParam();
            var organizeId = param.organizeId;
            if (organizeId.length <= 0) {
                Messenger().post({
                    message: '请选择班级',
                    type: 'error',
                    showCloseButton: true
                });
            } else {
                validStaffId();
            }
        }

        /**
         * 检验
         */
        function validStaffId() {
            initParam();
            var staffId = param.staffId;
            if (staffId.length <= 0) {
                Messenger().post({
                    message: '请选择教师',
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
                url: web_path + ajax_url.save,
                type: 'post',
                data: {
                    id: param.internshipReleaseId,
                    organizeId: param.organizeId.join(','),
                    staffId: param.staffId.join(',')
                },
                success: function (data) {
                    if (data.state) {
                        $.address.value(ajax_url.back + '?id=' + param.internshipReleaseId);
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