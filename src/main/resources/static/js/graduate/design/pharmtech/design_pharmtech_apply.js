/**
 * Created by zbeboy on 2017/5/19.
 */
//# sourceURL=design_pharmtech_apply.js
require(["jquery", "nav_active", "handlebars", "messenger", "jquery.address", "jquery.showLoading", "tablesaw"], function ($, nav_active, Handlebars) {
    /*
     ajax url.
     */
    var ajax_url = {
        data_url: '/web/graduate/design/pharmtech/apply/data',
        selected_url: '/web/graduate/design/pharmtech/apply/selected',
        cancel_url: '/web/graduate/design/pharmtech/apply/cancel',
        back: '/web/menu/graduate/design/pharmtech'
    };

    // 刷新时选中菜单
    nav_active(ajax_url.back);

    function startLoading() {
        // 显示遮罩
        $('#page-wrapper').showLoading();
    }

    function endLoading() {
        // 去除遮罩
        $('#page-wrapper').hideLoading();
    }

    $('#refresh').click(function () {
        init();
    });

    var tableData = '#tableData';

    /*
     返回
     */
    $('#page_back').click(function () {
        $.address.value(ajax_url.back);
    });

    init();

    function init() {
        startLoading();
        $.get(web_path + ajax_url.data_url, {id: init_page_param.graduationDesignReleaseId}, function (data) {
            endLoading();
            if (data.state) {
                listData(data);
            } else {
                Messenger().post({
                    message: data.msg,
                    type: 'error',
                    showCloseButton: true
                });
            }
        });
    }

    /**
     * 列表数据
     * @param data 数据
     */
    function listData(data) {
        var template = Handlebars.compile($("#teacher-template").html());
        $(tableData).html(template(data));
        $('#teacherTable').tablesaw().data("tablesaw").refresh();
    }

    /*
     选择
     */
    $(tableData).delegate('.selectTeacher', "click", function () {
        inquiry(this, '选择', ajax_url.selected_url);
    });

    /*
     取消
     */
    $(tableData).delegate('.cancelTeacher', "click", function () {
        inquiry(this, '取消选择', ajax_url.cancel_url);
    });

    /**
     * 操作询问
     * @param obj 对象
     * @param word 文字
     * @param url 链接
     */
    function inquiry(obj, word, url) {
        var name = $(obj).attr('data-name');
        var msg;
        msg = Messenger().post({
            message: "确定" + word + " " + name + " 教师吗?",
            actions: {
                retry: {
                    label: '确定',
                    phrase: 'Retrying TIME',
                    action: function () {
                        msg.cancel();
                        sendAjax($(obj).attr('data-id'), url);
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
     * 发送ajax
     * @param graduationDesignTeacherId 指导教师id
     * @param url 链接
     */
    function sendAjax(graduationDesignTeacherId, url) {
        Messenger().run({
            successMessage: '保存数据成功',
            errorMessage: '保存数据失败',
            progressMessage: '正在保存数据....'
        }, {
            url: web_path + url,
            type: 'post',
            data: {
                graduationDesignTeacherId: graduationDesignTeacherId,
                graduationDesignReleaseId: init_page_param.graduationDesignReleaseId
            },
            success: function (data) {
                if (data.state) {
                    init();
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