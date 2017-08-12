/**
 * Created by lenovo on 2016-09-22.
 */
require(["jquery", "handlebars", "nav_active", "messenger", "jquery.address", "bootstrap-maxlength", "jquery.showLoading"], function ($, Handlebars, nav_active) {

    /*
     ajax url.
     */
    var ajax_url = {
        school_data_url: '/user/schools',
        update: '/web/data/college/update',
        valid: '/web/data/college/update/valid/name',
        valid_code: '/web/data/college/update/valid/code',
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
        collegeName: '#collegeName',
        collegeCode: '#collegeCode',
        collegeAddress: '#collegeAddress'
    };

    /*
     参数
     */
    var param = {
        schoolId: $(paramId.schoolId).val().trim(),
        collegeId: $(paramId.collegeId).val().trim(),
        collegeName: $(paramId.collegeName).val().trim(),
        collegeCode: $(paramId.collegeCode).val().trim(),
        collegeAddress: $(paramId.collegeAddress).val().trim()
    };

    /*
     检验id
     */
    var validId = {
        schoolId: '#valid_school',
        collegeName: '#valid_college_name',
        collegeCode: '#valid_college_code',
        collegeAddress: '#valid_college_address'
    };

    /*
     错误消息id
     */
    var errorMsgId = {
        schoolId: '#school_error_msg',
        collegeName: '#college_name_error_msg',
        collegeCode: '#college_code_error_msg',
        collegeAddress: '#college_address_error_msg'
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
        param.schoolId = $(paramId.schoolId).val().trim();
        param.collegeId = $(paramId.collegeId).val().trim();
        param.collegeName = $(paramId.collegeName).val().trim();
        param.collegeCode = $(paramId.collegeCode).val().trim();
        param.collegeAddress = $(paramId.collegeAddress).val().trim();
    }

    /**
     * 学校数据展现
     * @param data json数据
     */
    function schoolData(data) {
        var template = Handlebars.compile($("#school-template").html());

        Handlebars.registerHelper('school_value', function () {
            return new Handlebars.SafeString(Handlebars.escapeExpression(this.schoolId));
        });

        Handlebars.registerHelper('school_name', function () {
            return new Handlebars.SafeString(Handlebars.escapeExpression(this.schoolName));
        });

        $(paramId.schoolId).html(template(data));

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

    init();

    /**
     * 初始化
     */
    function init() {
        startLoading();
        $.get(web_path + ajax_url.school_data_url, function (data) {
            endLoading();
            schoolData(data);
        });

        initMaxLength();
    }

    /**
     * 初始化Input max length
     */
    function initMaxLength() {
        $(paramId.collegeName).maxlength({
            alwaysShow: true,
            threshold: 10,
            warningClass: "label label-success",
            limitReachedClass: "label label-danger"
        });

        $(paramId.collegeCode).maxlength({
            alwaysShow: true,
            threshold: 10,
            warningClass: "label label-success",
            limitReachedClass: "label label-danger"
        });

        $(paramId.collegeAddress).maxlength({
            alwaysShow: true,
            threshold: 10,
            warningClass: "label label-success",
            limitReachedClass: "label label-danger"
        });
    }

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
     即时检验院代码
     */
    $(paramId.collegeCode).blur(function () {
        initParam();
        var collegeCode = param.collegeCode;
        if (collegeCode.length <= 0 || collegeCode.length > 20) {
            validErrorDom(validId.collegeCode, errorMsgId.collegeCode, '院代码20个字符以内');
        } else {
            // 院代码是否重复
            Messenger().run({
                errorMessage: '请求失败'
            }, {
                url: web_path + ajax_url.valid_code,
                type: 'post',
                data: param,
                success: function (data) {
                    if (data.state) {
                        validSuccessDom(validId.collegeCode, errorMsgId.collegeCode);
                    } else {
                        validErrorDom(validId.collegeCode, errorMsgId.collegeCode, data.msg);
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
                        msg.cancel();
                        validSchoolId();
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
     */
    function validSchoolId() {
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
                        validCollegeCode();
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
     * 添加时检验并提交数据
     */
    function validCollegeCode() {
        initParam();
        var collegeCode = param.collegeCode;
        if (collegeCode.length <= 0 || collegeCode.length > 20) {
            Messenger().post({
                message: '院代码为1~20个字符',
                type: 'error',
                showCloseButton: true
            });
        } else {
            // 院代码是否重复
            Messenger().run({
                errorMessage: '请求失败'
            }, {
                url: web_path + ajax_url.valid_code,
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