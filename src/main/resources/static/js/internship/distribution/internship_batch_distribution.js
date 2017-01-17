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
            exclude_internship_release_data_url: '/web/internship/teacher_distribution/batch/distribution/releases',
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
            staffId: '#select_teacher',
            excludeInternshipReleaseId: '#exclude_internship_release'
        };

        /*
         参数
         */
        var param = {
            organizeId: $(paramId.organizeId).val(),
            staffId: $(paramId.staffId).val(),
            excludeInternshipReleaseId: $(paramId.excludeInternshipReleaseId).val(),
            internshipReleaseId: init_page_param.internshipReleaseId
        };

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
            param.organizeId = $(paramId.organizeId).val();
            param.staffId = $(paramId.staffId).val();
            param.excludeInternshipReleaseId = $(paramId.excludeInternshipReleaseId).val();
            param.internshipReleaseId = init_page_param.internshipReleaseId;
        }

        init();

        /**
         * 初始化数据
         */
        function init() {
            startLoading();
            $.get(web_path + ajax_url.organize_data_url, {id: init_page_param.internshipReleaseId}, function (data) {
                endLoading();
                organizeData(data);
            });

            $.get(web_path + ajax_url.teacher_data_url, {id: init_page_param.internshipReleaseId}, function (data) {
                staffData(data);
            });

            $.get(web_path + ajax_url.exclude_internship_release_data_url, {id: init_page_param.internshipReleaseId}, function (data) {
                excludeInternshipReleaseData(data);
            });
        }

        /**
         * 班级数据
         * @param data
         */
        function organizeData(data) {
            var template = Handlebars.compile($("#organize-template").html());

            Handlebars.registerHelper('organize_value', function () {
                return new Handlebars.SafeString(Handlebars.escapeExpression(this.organizeId));
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

            $(paramId.organizeId).html(template(data.mapResult));
            initOrganizeDualListbox();
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
                var realName = this.realName;
                if (realName == null) {
                    realName = '';
                }
                var name = Handlebars.escapeExpression(realName + ' ' + this.staffNumber);
                return new Handlebars.SafeString(name);
            });

            $(paramId.staffId).html(template(data));
            initStaffDualListbox();
        }

        /**
         * 要排除的实习数据
         * @param data
         */
        function excludeInternshipReleaseData(data) {
            var template = Handlebars.compile($("#exclude-internship-release-template").html());

            Handlebars.registerHelper('internship_title', function () {
                return new Handlebars.SafeString(Handlebars.escapeExpression(this.internshipTitle));
            });

            $(paramId.excludeInternshipReleaseId).html(template(data));
            initExcludeInternshipReleaseDualListbox();
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

        /**
         * 初始化需要排除的实习数据列表
         */
        function initExcludeInternshipReleaseDualListbox() {
            $(paramId.excludeInternshipReleaseId).bootstrapDualListbox();
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
            if (organizeId == null || organizeId === '' || organizeId.length <= 0) {
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
            if (staffId == null || staffId === '' || staffId.length <= 0) {
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
            var excludeInternshipReleaseId = param.excludeInternshipReleaseId;
            if(excludeInternshipReleaseId != null && excludeInternshipReleaseId !== ''){
                excludeInternshipReleaseId = excludeInternshipReleaseId.join(',');
            }
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
                    staffId: param.staffId.join(','),
                    excludeInternshipReleaseId: excludeInternshipReleaseId
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