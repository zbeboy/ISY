/**
 * Created by zbeboy on 2017/5/27.
 */
require(["jquery", "nav_active", "handlebars", "messenger", "jquery.address", "jquery.showLoading"], function ($, nav_active, Handlebars) {

    /*
     ajax url.
     */
    var ajax_url = {
        building_data_url: '/web/graduate/design/project/buildings',
        schoolroom_data_url: '/user/schoolrooms',
        save: '/web/graduate/design/project/save',
        back: '/web/graduate/design/project/list',
        nav: '/web/menu/graduate/design/project'
    };

    // 刷新时选中菜单
    nav_active(ajax_url.nav);

    /*
     参数id
     */
    var paramId = {
        copy_all: '#copy_all',
        copy_scheduling: '#copy_scheduling',
        copy_supervision_time: '#copy_supervision_time',
        copy_guide_location: '#copy_guide_location',
        copy_guide_content: '#copy_guide_content',
        copy_note: '#copy_note',
        recently_scheduling: '#recently_scheduling',
        recently_supervisionTime: '#recently_supervisionTime',
        recently_select_building: '#recently_select_building',
        recently_select_schoolroom: '#recently_select_schoolroom',
        recently_guideContent: '#recently_guideContent',
        recently_note: '#recently_note',
        scheduling: '#scheduling',
        supervisionTime: '#supervisionTime',
        select_building: '#select_building',
        select_schoolroom: '#select_schoolroom',
        guideContent: '#guideContent',
        note: '#note'
    };

    /*
     参数
     */
    var param = {
        recently_scheduling: $(paramId.recently_scheduling).val(),
        recently_supervisionTime: $(paramId.recently_supervisionTime).val(),
        recently_select_building: $(paramId.recently_select_building).val(),
        recently_select_schoolroom: $(paramId.recently_select_schoolroom).val(),
        recently_guideContent: $(paramId.recently_guideContent).val(),
        recently_note: $(paramId.recently_note).val(),
        scheduling: $(paramId.scheduling).val(),
        supervisionTime: $(paramId.supervisionTime).val(),
        select_building: $(paramId.select_building).val(),
        select_schoolroom: $(paramId.select_schoolroom).val(),
        guideContent: $(paramId.guideContent).val(),
        note: $(paramId.note).val()
    };

    /*
     检验id
     */
    var validId = {
        scheduling: '#valid_scheduling',
        supervisionTime: '#valid_supervision_time',
        guideLocation: '#valid_guide_location',
        guideContent: '#valid_guide_content',
        note: '#valid_note'
    };

    /*
     错误消息id
     */
    var errorMsgId = {
        scheduling: '#scheduling_error_msg',
        supervisionTime: '#supervision_time_error_msg',
        guideLocation: '#guide_location_error_msg',
        guideContent: '#guide_content_error_msg',
        note: '#note_error_msg'
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
        param.recently_scheduling = $(paramId.recently_scheduling).val();
        param.recently_supervisionTime = $(paramId.recently_supervisionTime).val();
        param.recently_select_building = $(paramId.recently_select_building).val();
        param.recently_select_schoolroom = $(paramId.recently_select_schoolroom).val();
        param.recently_guideContent = $(paramId.recently_guideContent).val();
        param.recently_note = $(paramId.recently_note).val();
        param.scheduling = $(paramId.scheduling).val();
        param.supervisionTime = $(paramId.supervisionTime).val();
        param.select_building = $(paramId.select_building).val();
        param.select_schoolroom = $(paramId.select_schoolroom).val();
        param.guideContent = $(paramId.guideContent).val();
        param.note = $(paramId.note).val();
    }

    init();

    function init() {
        startLoading();
        $.get(web_path + ajax_url.building_data_url, {id: init_page_param.graduationDesignReleaseId}, function (data) {
            endLoading();
            buildingData(data);
            if (data.listResult.length > 0) {
                $('#operator_button').removeClass('hidden');
            }
        });
    }

    /**
     * 列表数据
     * @param data 数据
     */
    function buildingData(data) {
        var template = Handlebars.compile($("#building-template").html());

        Handlebars.registerHelper('building_value', function () {
            return new Handlebars.SafeString(Handlebars.escapeExpression(this.buildingId));
        });

        Handlebars.registerHelper('building_name', function () {
            return new Handlebars.SafeString(Handlebars.escapeExpression(this.buildingName));
        });

        $(paramId.select_building).html(template(data));
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

    }

});