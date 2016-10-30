/**
 * Created by lenovo on 2016-10-04.
 */
require(["jquery", "handlebars", "messenger","jquery.address","nav_active"], function ($, Handlebars, messenger,jqueryAddress,nav_active) {

    /*
     ajax url.
     */
    var ajax_url = {
        init_data_url: '/web/system/application/init',
        update: '/web/system/application/update',
        applicationNameValid: '/web/system/application/update/valid/name',
        applicationEnNameValid: '/web/system/application/update/valid/en_name',
        applicationUrlValid: '/web/system/application/update/valid/url',
        applicationCodeValid: '/web/system/application/update/valid/code',
        back: '/web/menu/system/application'
    };

    // 刷新时选中菜单
    nav_active(ajax_url.back);

    /*
     参数id
     */
    var paramId = {
        applicationId: '#applicationId',
        applicationPid: '#select_application_pid',
        applicationName: '#applicationName',
        applicationEnName: '#applicationEnName',
        applicationUrl: '#applicationUrl',
        applicationDataUrlStartWith: '#applicationDataUrlStartWith',
        applicationCode: '#applicationCode',
        icon: '#icon',
        applicationSort: '#applicationSort'
    };

    /*
     参数
     */
    var param = {
        applicationId: $(paramId.applicationId).val().trim(),
        applicationPid: $(paramId.applicationPid).val().trim(),
        applicationName: $(paramId.applicationName).val().trim(),
        applicationEnName: $(paramId.applicationEnName).val().trim(),
        applicationUrl: $(paramId.applicationUrl).val().trim(),
        applicationDataUrlStartWith: $(paramId.applicationDataUrlStartWith).val().trim(),
        applicationCode: $(paramId.applicationCode).val().trim(),
        icon: $(paramId.icon).val().trim(),
        applicationSort: $(paramId.applicationSort).val().trim()
    };

    /*
     检验id
     */
    var validId = {
        applicationPid: '#valid_application_pid',
        applicationName: '#valid_application_name',
        applicationEnName: '#valid_application_en_name',
        applicationUrl: '#valid_application_url',
        applicationDataUrlStartWith: '#valid_application_data_url_start_with',
        applicationCode: '#valid_application_code',
        icon: '#icon',
        applicationSort: '#valid_application_sort'
    };

    /*
     错误消息id
     */
    var errorMsgId = {
        applicationPid: '#application_pid_error_msg',
        applicationName: '#application_name_error_msg',
        applicationEnName: '#application_en_name_error_msg',
        applicationUrl: '#application_url_error_msg',
        applicationDataUrlStartWith: '#application_data_url_start_with_error_msg',
        applicationCode: '#application_code_error_msg',
        icon: '#icon_error_msg',
        applicationSort: '#application_sort_error_msg'
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
        param.applicationId = $(paramId.applicationId).val().trim();
        param.applicationPid = $(paramId.applicationPid).val().trim();
        param.applicationName = $(paramId.applicationName).val().trim();
        param.applicationEnName = $(paramId.applicationEnName).val().trim();
        param.applicationUrl = $(paramId.applicationUrl).val().trim();
        param.applicationDataUrlStartWith = $(paramId.applicationDataUrlStartWith).val().trim();
        param.applicationCode = $(paramId.applicationCode).val().trim();
        param.icon = $(paramId.icon).val().trim();
        param.applicationSort = $(paramId.applicationSort).val().trim();
    }

    var selectedApplicationPid = true;

    /**
     * 数据初始化
     * @param data json数据
     */
    function initData(data) {
        initApplicationPids(data.mapResult);
    }

    /**
     * 初始化父级菜单
     * @param data
     */
    function initApplicationPids(data) {
        var source = $("#application-parent-template").html();
        var template = Handlebars.compile(source);

        Handlebars.registerHelper('application_parent_value', function () {
            var value = Handlebars.escapeExpression(this.applicationId);
            return new Handlebars.SafeString(value);
        });

        Handlebars.registerHelper('application_parent_name', function () {
            var name = Handlebars.escapeExpression(this.applicationName);
            return new Handlebars.SafeString(name);
        });

        var html = template(data);
        $(paramId.applicationPid).html(html);

        if (selectedApplicationPid) {
            selectedPid();
            selectedApplicationPid = false;
        }

    }

    /**
     * 选中Pid
     */
    function selectedPid() {
        var realPid = $('#applicationPid').val().trim();
        var pidChildrens = $('#select_application_pid').children();
        for (var i = 0; i < pidChildrens.length; i++) {
            if ($(pidChildrens[i]).val() === realPid) {
                $(pidChildrens[i]).prop('selected', true);
                break;
            }
        }
    }

    $.get(web_path + ajax_url.init_data_url, function (data) {
        initData(data);
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

    // 即时检验应用名
    $(paramId.applicationName).blur(function () {
        initParam();
        var applicationName = param.applicationName;
        if (applicationName.length <= 0 || applicationName.length > 30) {
            validErrorDom(validId.applicationName, errorMsgId.applicationName, '应用中文名为1~30个字符');
        } else {
            Messenger().run({
                errorMessage: '请求失败'
            }, {
                url: web_path + ajax_url.applicationNameValid,
                type: 'post',
                data: param,
                success: function (data) {
                    if (data.state) {
                        validSuccessDom(validId.applicationName, errorMsgId.applicationName);
                    } else {
                        validErrorDom(validId.applicationName, errorMsgId.applicationName, data.msg);
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

    // 即时检验应用英文名
    $(paramId.applicationEnName).blur(function () {
        initParam();
        var applicationEnName = param.applicationEnName;
        if (applicationEnName.length <= 0 || applicationEnName.length > 100) {
            validErrorDom(validId.applicationEnName, errorMsgId.applicationEnName, '应用英文名为1~100个字符');
        } else {
            Messenger().run({
                errorMessage: '请求失败'
            }, {
                url: web_path + ajax_url.applicationEnNameValid,
                type: 'post',
                data: param,
                success: function (data) {
                    if (data.state) {
                        validSuccessDom(validId.applicationEnName, errorMsgId.applicationEnName);
                    } else {
                        validErrorDom(validId.applicationEnName, errorMsgId.applicationEnName, data.msg);
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

    // 即时检验应用链接
    $(paramId.applicationUrl).blur(function () {
        initParam();
        var applicationUrl = param.applicationUrl;
        if (applicationUrl.length <= 0 || applicationUrl.length > 300) {
            validErrorDom(validId.applicationUrl, errorMsgId.applicationUrl, '应用链接url为1~300个字符');
        } else {
            if (applicationUrl === '#') {
                validSuccessDom(validId.applicationUrl, errorMsgId.applicationUrl);
            } else {
                Messenger().run({
                    errorMessage: '请求失败'
                }, {
                    url: web_path + ajax_url.applicationUrlValid,
                    type: 'post',
                    data: param,
                    success: function (data) {
                        if (data.state) {
                            validSuccessDom(validId.applicationUrl, errorMsgId.applicationUrl);
                        } else {
                            validErrorDom(validId.applicationUrl, errorMsgId.applicationUrl, data.msg);
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
    });

    // 即时检验应用识别码
    $(paramId.applicationCode).blur(function () {
        initParam();
        var applicationCode = param.applicationCode;
        if (applicationCode.length <= 0 || applicationCode.length > 100) {
            validErrorDom(validId.applicationCode, errorMsgId.applicationCode, '应用识别码为1~100个字符');
        } else {
            Messenger().run({
                errorMessage: '请求失败'
            }, {
                url: web_path + ajax_url.applicationUrlValid,
                type: 'post',
                data: param,
                success: function (data) {
                    if (data.state) {
                        validSuccessDom(validId.applicationCode, errorMsgId.applicationCode);
                    } else {
                        validErrorDom(validId.applicationCode, errorMsgId.applicationCode, data.msg);
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
     添加询问
     */
    function add() {
        initParam();
        var applicationName = param.applicationName;
        var msg;
        msg = Messenger().post({
            message: "确定添加应用 '" + applicationName + "'  吗?",
            actions: {
                retry: {
                    label: '确定',
                    phrase: 'Retrying TIME',
                    action: function () {
                        validApplicationName(msg);
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
     * 检验应用名
     * @param msg
     */
    function validApplicationName(msg) {
        msg.cancel();
        initParam();
        var applicationName = param.applicationName;
        if (applicationName.length <= 0 || applicationName.length > 30) {
            Messenger().post({
                message: '应用中文名为1~30个字符',
                type: 'error',
                showCloseButton: true
            });
        } else {
            Messenger().run({
                errorMessage: '请求失败'
            }, {
                url: web_path + ajax_url.applicationNameValid,
                type: 'post',
                data: param,
                success: function (data) {
                    if (data.state) {
                        validApplicationEnName();
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
     * 检验应用英文名
     */
    function validApplicationEnName() {
        initParam();
        var applicationEnName = param.applicationEnName;
        if (applicationEnName.length <= 0 || applicationEnName.length > 100) {
            Messenger().post({
                message: '应用英文名为1~100个字符',
                type: 'error',
                showCloseButton: true
            });
        } else {
            Messenger().run({
                errorMessage: '请求失败'
            }, {
                url: web_path + ajax_url.applicationEnNameValid,
                type: 'post',
                data: param,
                success: function (data) {
                    if (data.state) {
                        validApplicationUrl();
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
     * 检验应用链接
     */
    function validApplicationUrl() {
        initParam();
        var applicationUrl = param.applicationUrl;
        if (applicationUrl.length <= 0 || applicationUrl.length > 300) {
            Messenger().post({
                message: '应用链接url为1~300个字符',
                type: 'error',
                showCloseButton: true
            });
        } else {
            if (applicationUrl !== '#') {
                Messenger().run({
                    errorMessage: '请求失败'
                }, {
                    url: web_path + ajax_url.applicationUrlValid,
                    type: 'post',
                    data: param,
                    success: function (data) {
                        if (data.state) {
                            validApplicationCode();
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
            } else {
                validApplicationCode();
            }
        }
    }

    /**
     * 检验应用识别码
     */
    function validApplicationCode() {
        initParam();
        var applicationCode = param.applicationCode;
        if (applicationCode.length <= 0 || applicationCode.length > 100) {
            Messenger().post({
                message: '应用识别码为1~100个字符',
                type: 'error',
                showCloseButton: true
            });
        } else {
            Messenger().run({
                errorMessage: '请求失败'
            }, {
                url: web_path + ajax_url.applicationCodeValid,
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