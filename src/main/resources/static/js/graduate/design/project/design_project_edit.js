/**
 * Created by zbeboy on 2017/6/2.
 */
require(["jquery", "nav_active", "handlebars", "messenger", "jquery.address", "bootstrap-maxlength", "jquery.showLoading"], function ($, nav_active, Handlebars) {

    /*
     ajax url.
     */
    var ajax_url = {
        building_data_url: '/web/graduate/design/project/buildings',
        schoolroom_data_url: '/user/schoolrooms',
        update: '/web/graduate/design/project/update',
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
        clean_all: '#clean_all',
        copy_scheduling: '#copy_scheduling',
        copy_supervision_time: '#copy_supervision_time',
        copy_guide_location: '#copy_guide_location',
        copy_guide_content: '#copy_guide_content',
        copy_note: '#copy_note',
        recently_scheduling: '#recently_scheduling',
        recently_supervisionTime: '#recently_supervisionTime',
        recently_select_building: '#recently_select_building',
        recently_building_id: '#recently_building_id',
        recently_select_schoolroom: '#recently_select_schoolroom',
        recently_schoolroom_id: '#recently_schoolroom_id',
        recently_guideContent: '#recently_guideContent',
        recently_note: '#recently_note',
        scheduling: '#scheduling',
        supervisionTime: '#supervisionTime',
        select_building: '#select_building',
        select_schoolroom: '#select_schoolroom',
        buildingId:'#buildingId',
        schoolroomId:'#schoolroomId',
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
        recently_building_id: $(paramId.recently_building_id).val(),
        recently_schoolroom_id: $(paramId.recently_schoolroom_id).val(),
        recently_select_schoolroom: $(paramId.recently_select_schoolroom).val(),
        recently_guideContent: $(paramId.recently_guideContent).val(),
        recently_note: $(paramId.recently_note).val(),
        scheduling: $(paramId.scheduling).val(),
        supervisionTime: $(paramId.supervisionTime).val(),
        select_building: $(paramId.select_building).val(),
        select_schoolroom: $(paramId.select_schoolroom).val(),
        buildingId:$(paramId.buildingId).val(),
        schoolroomId:$(paramId.schoolroomId).val(),
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

    /*
     清除验证
     */
    function validCleanDom(inputId, errorId) {
        $(inputId).removeClass('has-error').removeClass('has-success');
        $(errorId).addClass('hidden').text('');
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
        param.recently_scheduling = $(paramId.recently_scheduling).val();
        param.recently_supervisionTime = $(paramId.recently_supervisionTime).val();
        param.recently_building_id = $(paramId.recently_building_id).val();
        param.recently_schoolroom_id = $(paramId.recently_schoolroom_id).val();
        param.recently_select_building = $(paramId.recently_select_building).val();
        param.recently_select_schoolroom = $(paramId.recently_select_schoolroom).val();
        param.recently_guideContent = $(paramId.recently_guideContent).val();
        param.recently_note = $(paramId.recently_note).val();
        param.scheduling = $(paramId.scheduling).val();
        param.supervisionTime = $(paramId.supervisionTime).val();
        param.select_building = $(paramId.select_building).val();
        param.select_schoolroom = $(paramId.select_schoolroom).val();
        param.buildingId = $(paramId.buildingId).val();
        param.schoolroomId = $(paramId.schoolroomId).val();
        param.guideContent = $(paramId.guideContent).val();
        param.note = $(paramId.note).val();
    }

    /*
     页面加载时初始化选中
     */
    var selectedBuildingCount = true;
    var selectedCollegeCount = true;
    var selectedDepartmentCount = true;
    var selectedScienceCount = true;
    var selectedGradeCount = true;

    init();

    function init() {
        startLoading();
        $.post(web_path + ajax_url.building_data_url, {id: init_page_param.graduationDesignReleaseId}, function (data) {
            endLoading();
            buildingData(data);
            if (data.listResult.length > 0) {
                $('#operator_button').removeClass('hidden');
            }
        });
        initMaxLength();
    }

    /**
     * 初始化Input max length
     */
    function initMaxLength() {
        $(paramId.scheduling).maxlength({
            alwaysShow: true,
            threshold: 10,
            warningClass: "label label-success",
            limitReachedClass: "label label-danger"
        });

        $(paramId.supervisionTime).maxlength({
            alwaysShow: true,
            threshold: 10,
            warningClass: "label label-success",
            limitReachedClass: "label label-danger"
        });

        $(paramId.guideContent).maxlength({
            alwaysShow: true,
            threshold: 10,
            warningClass: "label label-success",
            limitReachedClass: "label label-danger"
        });

        $(paramId.note).maxlength({
            alwaysShow: true,
            threshold: 10,
            warningClass: "label label-success",
            limitReachedClass: "label label-danger"
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

        if (selectedBuildingCount) {
            selectedBuilding();
            selectedBuildingCount = false;
        }
    }

    // 当改变楼时，变换教室数据.
    $(paramId.select_building).change(function () {
        initParam();
        var building = param.select_building;
        global_schoolroom_id = 0;
        changeSchoolroom(building);// 根据楼重新加载教室数据

        if (Number(building) > 0) {
            validSuccessDom(validId.guideLocation, errorMsgId.guideLocation);
        } else {
            validErrorDom(validId.guideLocation, errorMsgId.guideLocation, '请选择楼');
        }
    });

    $(paramId.select_schoolroom).change(function () {
        initParam();
        var schoolroom = param.select_schoolroom;

        if (Number(schoolroom) > 0) {
            validSuccessDom(validId.guideLocation, errorMsgId.guideLocation);
        } else {
            validErrorDom(validId.guideLocation, errorMsgId.guideLocation, '请选择教室');
        }
    });

    /**
     * 选中楼
     */
    function selectedBuilding() {
        var realBuildingId = $(paramId.buildingId).val();
        var buildingChildrens = $(paramId.select_building).children();
        for (var i = 0; i < buildingChildrens.length; i++) {
            if ($(buildingChildrens[i]).val() === realBuildingId) {
                $(buildingChildrens[i]).prop('selected', true);
                changeSchoolroom($(buildingChildrens[i]).val());
                global_schoolroom_id = Number($(paramId.schoolroomId).val());
                break;
            }
        }
    }

    var global_schoolroom_id = 0;

    /**
     * 改变教室选项
     * @param building_id 楼id
     */
    function changeSchoolroom(building_id) {
        if (Number(building_id) == 0) {
            var source = $("#schoolroom-template").html();
            var template = Handlebars.compile(source);

            var context = {
                listResult: [
                    {name: "请选择教室", value: ""}
                ]
            };

            Handlebars.registerHelper('schoolroom_value', function () {
                var value = Handlebars.escapeExpression(this.value);
                return new Handlebars.SafeString(value);
            });

            Handlebars.registerHelper('schoolroom_name', function () {
                var name = Handlebars.escapeExpression(this.name);
                return new Handlebars.SafeString(name);
            });

            $(paramId.select_schoolroom).html(template(context));
        } else {
            // 根据楼id查询教室数据
            startLoading();
            $.post(web_path + ajax_url.schoolroom_data_url, {buildingId: building_id}, function (data) {
                endLoading();
                var template = Handlebars.compile($("#schoolroom-template").html());

                Handlebars.registerHelper('schoolroom_value', function () {
                    var value = Handlebars.escapeExpression(this.schoolroomId);
                    return new Handlebars.SafeString(value);
                });

                Handlebars.registerHelper('schoolroom_name', function () {
                    var name = Handlebars.escapeExpression(this.buildingCode);
                    return new Handlebars.SafeString(name);
                });

                $(paramId.select_schoolroom).html(template(data));
                selectedSchoolroom();
            });
        }
    }

    /**
     * 选中教室
     */
    function selectedSchoolroom() {
        var schoolroomChildrens = $(paramId.select_schoolroom).children();
        for (var i = 0; i < schoolroomChildrens.length; i++) {
            if (Number($(schoolroomChildrens[i]).val()) === global_schoolroom_id) {
                $(schoolroomChildrens[i]).prop('selected', true);
                break;
            }
        }
    }

    // 即时验证
    $(paramId.scheduling).blur(function () {
        initParam();
        var scheduling = param.scheduling;
        if (scheduling.length <= 0 || scheduling.length > 100) {
            validErrorDom(validId.scheduling, errorMsgId.scheduling, '进度安排100个字符以内');
        } else {
            validSuccessDom(validId.scheduling, errorMsgId.scheduling);
        }
    });

    $(paramId.supervisionTime).blur(function () {
        initParam();
        var supervisionTime = param.supervisionTime;
        if (supervisionTime.length <= 0 || supervisionTime.length > 100) {
            validErrorDom(validId.supervisionTime, errorMsgId.supervisionTime, '指导时间100个字符以内');
        } else {
            validSuccessDom(validId.supervisionTime, errorMsgId.supervisionTime);
        }
    });

    $(paramId.guideContent).blur(function () {
        initParam();
        var guideContent = param.guideContent;
        if (guideContent.length <= 0 || guideContent.length > 100) {
            validErrorDom(validId.guideContent, errorMsgId.guideContent, '指导内容150个字符以内');
        } else {
            validSuccessDom(validId.guideContent, errorMsgId.guideContent);
        }
    });

    $(paramId.note).blur(function () {
        initParam();
        var note = param.note;
        if (note.length <= 0 || note.length > 100) {
            validErrorDom(validId.note, errorMsgId.note, '备注100个字符以内');
        } else {
            validSuccessDom(validId.note, errorMsgId.note);
        }
    });

    /*
     返回
     */
    $('#page_back').click(function () {
        $.address.value(ajax_url.back + '?id=' + init_page_param.graduationDesignReleaseId);
    });

    // 复制内容
    function copyScheduling() {
        $(paramId.scheduling).val($(paramId.recently_scheduling).val());
    }

    // 清空内容
    function cleanScheduling() {
        $(paramId.scheduling).val('');
    }

    function copySupervisionTime() {
        $(paramId.supervisionTime).val($(paramId.recently_supervisionTime).val());
    }

    function cleanSupervisionTime() {
        $(paramId.supervisionTime).val('');
    }

    function copyGuideLocation() {
        var realBuildingId = Number($(paramId.recently_building_id).val());
        var buildingChildrens = $(paramId.select_building).children();
        for (var i = 0; i < buildingChildrens.length; i++) {
            if (Number($(buildingChildrens[i]).val()) === realBuildingId) {
                $(buildingChildrens[i]).prop('selected', true);
                break;
            }
        }
        global_schoolroom_id = Number($(paramId.recently_schoolroom_id).val());
        changeSchoolroom(realBuildingId);
    }

    function cleanGuideLocation() {
        var realBuildingId = 0;
        var buildingChildrens = $(paramId.select_building).children();
        for (var i = 0; i < buildingChildrens.length; i++) {
            if (Number($(buildingChildrens[i]).val()) === realBuildingId) {
                $(buildingChildrens[i]).prop('selected', true);
                break;
            }
        }
        global_schoolroom_id = 0;
        changeSchoolroom(realBuildingId);
    }

    function copyGuideContent() {
        $(paramId.guideContent).val($(paramId.recently_guideContent).val());
    }

    function cleanGuideContent() {
        $(paramId.guideContent).val('');
    }

    function copyNote() {
        $(paramId.note).val($(paramId.recently_note).val());
    }

    function cleanNote() {
        $(paramId.note).val('');
    }

    $(paramId.copy_all).click(function () {
        copyScheduling();
        copySupervisionTime();
        copyGuideLocation();
        copyGuideContent();
        copyNote();
    });

    $(paramId.clean_all).click(function () {
        cleanScheduling();
        cleanSupervisionTime();
        cleanGuideLocation();
        cleanGuideContent();
        cleanNote();
    });

    $(paramId.copy_scheduling).click(function () {
        copyScheduling();
    });

    $(paramId.copy_supervision_time).click(function () {
        copySupervisionTime();
    });

    $(paramId.copy_guide_location).click(function () {
        copyGuideLocation();
    });

    $(paramId.copy_guide_content).click(function () {
        copyGuideContent();
    });

    $(paramId.copy_note).click(function () {
        copyNote();
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
            message: "确定保存吗?",
            actions: {
                retry: {
                    label: '确定',
                    phrase: 'Retrying TIME',
                    action: function () {
                        msg.cancel();
                        validScheduling();
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

    function validScheduling() {
        var scheduling = param.scheduling;
        if (scheduling.length <= 0 || scheduling.length > 100) {
            Messenger().post({
                message: '进度安排100个字符以内',
                type: 'error',
                showCloseButton: true
            });
        } else {
            validSupervisionTime();
        }
    }

    function validSupervisionTime(){
        var supervisionTime = param.supervisionTime;
        if (supervisionTime.length <= 0 || supervisionTime.length > 100) {
            Messenger().post({
                message: '指导时间100个字符以内',
                type: 'error',
                showCloseButton: true
            });
        } else {
            validGuideLocation();
        }
    }

    function validGuideLocation(){
        var building = param.select_building;
        var schoolroom = param.select_schoolroom;
        if (Number(building) <= 0) {
            Messenger().post({
                message: '请选择楼',
                type: 'error',
                showCloseButton: true
            });
        } else {
            if(Number(schoolroom) <= 0){
                Messenger().post({
                    message: '请选择教室',
                    type: 'error',
                    showCloseButton: true
                });
            } else {
                validGuideContent();
            }
        }
    }

    function validGuideContent(){
        var guideContent = param.guideContent;
        if (guideContent.length <= 0 || guideContent.length > 100) {
            Messenger().post({
                message: '指导内容150个字符以内',
                type: 'error',
                showCloseButton: true
            });
        } else {
            validNote();
        }
    }

    function validNote(){
        var note = param.note;
        if (note.length <= 0 || note.length > 100) {
            Messenger().post({
                message: '备注100个字符以内',
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
            url: web_path + ajax_url.update,
            type: 'post',
            data: $('#edit_form').serialize(),
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