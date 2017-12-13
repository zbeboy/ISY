/**
 * Created by lenovo on 2016/9/21.
 */
require(["jquery", "nav_active", "messenger", "jquery.address", "bootstrap-maxlength"], function ($, nav_active) {

    /*
     ajax url.
     */
    var ajax_url = {
        update: '/web/data/school/update',
        valid: '/web/data/school/update/valid',
        back: '/web/menu/data/school'
    };

    // 刷新时选中菜单
    nav_active(ajax_url.back);

    /*
     参数id
     */
    var paramId = {
        schoolId: '#schoolId',
        schoolName: '#schoolName'
    };

    /*
     参数
     */
    var param = {
        schoolId: $(paramId.schoolId).val(),
        schoolName: $(paramId.schoolName).val()
    };

    /*
     检验id
     */
    var validId = {
        schoolName: '#valid_school_name'
    };

    /*
     错误消息id
     */
    var errorMsgId = {
        schoolName: '#school_name_error_msg'
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

    /**
     * 初始化参数
     */
    function initParam() {
        param.schoolId = $(paramId.schoolId).val();
        param.schoolName = $(paramId.schoolName).val();
    }

    init();

    function init(){
        initMaxLength();
    }

    /**
     * 初始化Input max length
     */
    function initMaxLength(){
        $(paramId.schoolName).maxlength({
            alwaysShow: true,
            threshold: 10,
            warningClass: "label label-success",
            limitReachedClass: "label label-danger"
        });
    }

    /*
     即时检验学校名
     */
    $(paramId.schoolName).blur(function () {
        initParam();
        var schoolName = param.schoolName;
        if (schoolName.length <= 0 || schoolName.length > 200) {
            validErrorDom(validId.schoolName, errorMsgId.schoolName, '学校名200个字符以内');
        } else {
            // 学校名是否重复
            Messenger().run({
                errorMessage: '请求失败'
            }, {
                url: web_path + ajax_url.valid,
                type: 'post',
                data: param,
                success: function (data) {
                    if (data.state) {
                        validSuccessDom(validId.schoolName, errorMsgId.schoolName);
                    } else {
                        validErrorDom(validId.schoolName, errorMsgId.schoolName, data.msg);
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
        var schoolName = param.schoolName;
        var msg;
        msg = Messenger().post({
            message: "确定更改学校为 '" + schoolName + "'  吗?",
            actions: {
                retry: {
                    label: '确定',
                    phrase: 'Retrying TIME',
                    action: function () {
                        msg.cancel();
                        validSchoolName();
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
     * 添加时检验并提交数据
     */
    function validSchoolName() {
        initParam();
        var schoolName = param.schoolName;
        if (schoolName.length <= 0 || schoolName.length > 200) {
            Messenger().post({
                message: '学校名1~200个字符',
                type: 'error',
                showCloseButton: true
            });
        } else {
            // 学校名是否重复
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