/**
 * Created by zbeboy on 2017/5/26.
 */
//# sourceURL=design_project_list.js
require(["jquery", "nav_active", "handlebars", "messenger", "jquery.address", "jquery.showLoading", "tablesaw", "check.all"], function ($, nav_active, Handlebars) {
    /*
     ajax url.
     */
    var ajax_url = {
        data_url: '/web/graduate/design/project/list/data',
        condition:'/web/graduate/design/project/condition',
        add:'/web/graduate/design/project/list/add',
        edit:'/web/graduate/design/project/list/edit',
        back: '/web/menu/graduate/design/project'
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
        var template = Handlebars.compile($("#project-template").html());
        $(tableData).html(template(data));
        $('#tablesawTable').tablesaw().data("tablesaw").refresh();
        // 调用全选插件
        $.fn.check({checkall_name: "checkall", checkbox_name: "check"});
    }

    /*
     添加
     */
    $('#project_add').click(function () {
        $.post(ajax_url.condition,{id:init_page_param.graduationDesignReleaseId},function(data){
            if(data.state){
                $.address.value(ajax_url.add + '?id=' + init_page_param.graduationDesignReleaseId);
            } else {
                Messenger().post({
                    message: data.msg,
                    type: 'error',
                    showCloseButton: true
                });
            }
        });
    });

    /*
     编辑
     */
    $(tableData).delegate('.edit', "click", function () {
        var graduationDesignPlanId = $(this).attr('data-id');
        $.post(ajax_url.condition,{id:init_page_param.graduationDesignReleaseId},function(data){
            if(data.state){
                $.address.value(ajax_url.edit + '?id=' + init_page_param.graduationDesignReleaseId + '&graduationDesignPlanId=' + graduationDesignPlanId);
            } else {
                Messenger().post({
                    message: data.msg,
                    type: 'error',
                    showCloseButton: true
                });
            }
        });
    });

    /*
     删除
     */
    $(tableData).delegate('.del', "click", function () {

    });
});