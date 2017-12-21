/**
 * Created by lenovo on 2016/11/24.
 */
//# sourceURL=internship_edit_distribution.js
require(["jquery", "handlebars", "nav_active", "messenger", "jquery.address",
        "bootstrap-select-zh-CN", "bootstrap-duallistbox", "jquery.showLoading"],
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
            studentId: $(paramId.studentId).val(),
            staffId: $(paramId.staffId).val(),
            id: init_page_param.internshipReleaseId
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
            param.studentId = $(paramId.studentId).val();
            param.staffId = $(paramId.staffId).val();
            param.id = init_page_param.internshipReleaseId;
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

        // 选中
        var selectedStaffCount = true;

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

        $(paramId.staffId).on('loaded.bs.select', function (e) {
            // do something...
            // 只在页面初始化加载一次
            if (selectedStaffCount) {
                selectedStaff();
                $(paramId.staffId).selectpicker('refresh');
                selectedStaffCount = false;
            }
        });

        /**
         * 选中教职工
         */
        function selectedStaff() {
            var realStaffId = init_page_param.staffId;
            var staffChildrens = $(paramId.staffId).children();
            for (var i = 0; i < staffChildrens.length; i++) {
                if (Number($(staffChildrens[i]).val()) === realStaffId) {
                    $(staffChildrens[i]).prop('selected', true);
                    break;
                }
            }
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