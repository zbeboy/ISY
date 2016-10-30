/**
 * Created by lenovo on 2016-09-22.
 */
require(["jquery", "handlebars", "messenger", "jquery.address","nav_active"], function ($, Handlebars,  messenger, jqueryAddress,nav_active) {

    /*
     ajax url.
     */
    var ajax_url = {
        school_data_url: '/user/schools',
        update: '/web/data/college/update',
        valid: '/web/data/college/update/valid',
        back: '/web/menu/data/college'
    };

    // 刷新时选中菜单
    nav_active(ajax_url.back);

    /*
     参数id
     */
    var paramId = {
        schoolId: '#select_school',
        collegeId: '#collegeId',
        collegeName: '#collegeName'
    };

    /*
     参数
     */
    var param = {
        schoolId: $(paramId.schoolId).val().trim(),
        collegeId: $(paramId.collegeId).val().trim(),
        collegeName: $(paramId.collegeName).val().trim()
    };

    /*
     检验id
     */
    var validId = {
        schoolId: '#valid_school',
        collegeName: '#valid_college_name'
    };

    /*
     错误消息id
     */
    var errorMsgId = {
        schoolId: '#school_error_msg',
        collegeName: '#college_name_error_msg'
    };

    /*
     初始化选中学校
     */
    var selectedSchoolCount = true;

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

    /**
     * 初始化参数
     */
    function initParam() {
        param.schoolId = $(paramId.schoolId).val().trim();
        param.collegeId = $(paramId.collegeId).val().trim();
        param.collegeName = $(paramId.collegeName).val().trim();
    }

    /**
     * 学校数据展现
     * @param data json数据
     */
    function schoolData(data) {
        var source = $("#school-template").html();
        var template = Handlebars.compile(source);

        Handlebars.registerHelper('school_value', function () {
            var value = Handlebars.escapeExpression(this.schoolId);
            return new Handlebars.SafeString(value);
        });

        Handlebars.registerHelper('school_name', function () {
            var name = Handlebars.escapeExpression(this.schoolName);
            return new Handlebars.SafeString(name);
        });

        var html = template(data);
        $(paramId.schoolId).html(html);

        // 只在页面初始化加载一次
        if (selectedSchoolCount) {
            selectedSchool();
            selectedSchoolCount = false;
        }

    }

    /**
     * 选中学校
     */
    function selectedSchool() {
        var realSchoolId = $('#schoolId').val().trim();
        var schoolChildrens = $('#select_school').children();
        for (var i = 0; i < schoolChildrens.length; i++) {
            if ($(schoolChildrens[i]).val() === realSchoolId) {
                $(schoolChildrens[i]).prop('selected', true);
                break;
            }
        }
    }

    $.get(web_path + ajax_url.school_data_url, function (data) {
        schoolData(data);
    });

    // 当改变学校时，变换学院数据.
    $(paramId.schoolId).change(function () {
        initParam();
        var school = param.schoolId;

        // 改变选项时，检验
        if (Number(school) > 0) {
            validSuccessDom(validId.schoolId, errorMsgId.schoolId);
        } else {
            validErrorDom(validId.schoolId, errorMsgId.schoolId, '请选择学校');
        }
    });

    /*
     即时检验院名
     */
    $(paramId.collegeName).blur(function () {
        initParam();
        var collegeName = param.collegeName;
        if (collegeName.length <= 0 || collegeName.length > 200) {
            validErrorDom(validId.collegeName, errorMsgId.collegeName, '院名200个字符以内');
        } else {
            // 院名是否重复
            Messenger().run({
                errorMessage: '请求失败'
            }, {
                url: web_path + ajax_url.valid,
                type: 'post',
                data: param,
                success: function (data) {
                    if (data.state) {
                        validSuccessDom(validId.collegeName, errorMsgId.collegeName);
                    } else {
                        validErrorDom(validId.collegeName, errorMsgId.collegeName, data.msg);
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

    /*
     返回
     */
    $('#page_back').click(function () {
        $.address.value(ajax_url.back);
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
        var collegeName = param.collegeName;
        var msg;
        msg = Messenger().post({
            message: "确定更改院为 '" + collegeName + "'  吗?",
            actions: {
                retry: {
                    label: '确定',
                    phrase: 'Retrying TIME',
                    action: function () {
                        validSchoolId(msg);
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
     * 检验学校id
     * @param msg
     */
    function validSchoolId(msg) {
        msg.cancel();
        initParam();
        var schoolId = param.schoolId;
        if (Number(schoolId) <= 0) {
            Messenger().post({
                message: '请选择学校',
                type: 'error',
                showCloseButton: true
            });
        } else {
            validCollegeName();
        }
    }

    /**
     * 添加时检验并提交数据
     * @param msg
     */
    function validCollegeName() {
        initParam();
        var collegeName = param.collegeName;
        if (collegeName.length <= 0 || collegeName.length > 200) {
            Messenger().post({
                message: '院名为1~200个字符',
                type: 'error',
                showCloseButton: true
            });
        } else {
            // 院名是否重复
            Messenger().run({
                errorMessage: '请求失败'
            }, {
                url: web_path + ajax_url.valid,
                type: 'post',
                data: param,
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
                    $.address.value(ajax_url.back);
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