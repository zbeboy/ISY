/**
 * Created by zbeboy on 2017/7/13.
 */
require(["jquery", "nav_active", "handlebars", "messenger", "jquery.address", "jquery.showLoading", "tablesaw"], function ($, nav_active, Handlebars) {
    /*
     ajax url.
     */
    var ajax_url = {
        data_url: '/web/graduate/design/replan/divide/data',
        back: '/web/menu/graduate/design/replan'
    };

    /*
     参数id
     */
    var paramId = {
        realName: '#search_name',
        defenseGroupId: '#select_group'
    };

    /*
     参数
     */
    var param = {
        graduationDesignReleaseId: init_page_param.graduationDesignReleaseId,
        realName: $(paramId.realName).val(),
        defenseGroupId: $(paramId.defenseGroupId).val()
    };

    /**
     * 初始化参数
     */
    function initParam() {
        param.realName = $(paramId.realName).val();
        param.defenseGroupId = $(paramId.defenseGroupId).val()
    }

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
        $.get(web_path + ajax_url.data_url, param, function (data) {
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
        var template = Handlebars.compile($("#group-member-template").html());
        Handlebars.registerHelper('is_leader', function () {
            var isLeader = '';
            if (this.graduationDesignTeacherId === this.leaderId) {
                isLeader = '是';
            }
            return new Handlebars.SafeString(Handlebars.escapeExpression(isLeader));
        });
        $(tableData).html(template(data));
        $('#tablesawTable').tablesaw().data("tablesaw").refresh();
    }
});