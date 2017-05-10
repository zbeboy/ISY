/**
 * Created by zbeboy on 2017/5/9.
 */
require(["jquery", "nav_active", "handlebars", "messenger", "jquery.address", "icheck"], function ($, nav_active, Handlebars) {

    /*
     ajax url.
     */
    var ajax_url = {
        teachers: '/web/graduate/design/tutor/teachers',
        save: '/web/graduate/design/tutor/save',
        back: '/web/graduate/design/tutor/look',
        nav: '/web/menu/graduate/design/tutor'
    };

    // 刷新时选中菜单
    nav_active(ajax_url.nav);

    /*
     参数id
     */
    var paramId = {
        teachers: '.icheckbox_sm-blue'
    };

    /*
     参数
     */
    var param = {
        staffId: []
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
        var staffIds = [];
        $(paramId.teachers).each(function () {
            if ($(this).hasClass('checked')) {
                staffIds.push($($(this).find('input')[0]).val());
            }
        });
        param.staffId = staffIds;
    }

    init();

    function init() {
        startLoading();
        $.get(web_path + ajax_url.teachers, {id: init_page_param.graduationDesignReleaseId}, function (data) {
            endLoading();
            staffData(data);
            if (data.listResult.length > 0) {
                $('#operator_button').removeClass('hidden');
            }
        });
    }

    /**
     * 初始化check插件
     */
    function initIcheck() {
        $('#datas').find('input').each(function () {
            var self = $(this),
                label = self.next(),
                label_text = label.text();
            label.remove();
            self.iCheck({
                checkboxClass: 'icheckbox_sm-blue',
                radioClass: 'radio_sm-blue',
                insert: label_text
            });
        });
    }

    /**
     * 列表数据
     * @param data 数据
     */
    function staffData(data) {
        var template = Handlebars.compile($("#teacher-template").html());

        Handlebars.registerHelper('column_class', function () {
            return new Handlebars.SafeString(Handlebars.escapeExpression('col-md-3'));
        });

        Handlebars.registerHelper('input_name', function () {
            return new Handlebars.SafeString(Handlebars.escapeExpression('teacher'));
        });

        Handlebars.registerHelper('teacher_value', function () {
            return new Handlebars.SafeString(Handlebars.escapeExpression(this.staffId));
        });

        Handlebars.registerHelper('teacher_name', function () {
            return new Handlebars.SafeString(Handlebars.escapeExpression(this.realName + ' ' + this.staffNumber));
        });

        $('#datas').html(template(data));
        initIcheck();
    }

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
            message: "确定添加选中的教师吗?",
            actions: {
                retry: {
                    label: '确定',
                    phrase: 'Retrying TIME',
                    action: function () {
                        msg.cancel();
                        validStaff();
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

    function validStaff() {
        if (param.staffId.length <= 0) {
            Messenger().post({
                message: '请至少选择一名教职工',
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
                staffId: param.staffId.join(','),
                graduationDesignReleaseId: init_page_param.graduationDesignReleaseId
            },
            success: function (data) {
                if (data.state) {
                    $.address.value(ajax_url.back + '?id=' + init_page_param.graduationDesignReleaseId);
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