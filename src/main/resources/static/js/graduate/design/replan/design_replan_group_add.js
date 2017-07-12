/**
 * Created by zbeboy on 2017/7/12.
 */
require(["jquery", "handlebars", "nav_active", "messenger", "bootstrap-maxlength",
        "jquery.address", "jquery.showLoading"],
    function ($, Handlebars, nav_active) {
        /*
         ajax url.
         */
        var ajax_url = {
            building_data_url: '/web/graduate/design/replan/buildings',
            schoolroom_data_url: '/user/schoolrooms',
            save: '/web/graduate/design/replan/group/save',
            nav: '/web/menu/graduate/design/replan',
            back: '/web/graduate/design/replan/group'
        };

        // 刷新时选中菜单
        nav_active(ajax_url.nav);

        /*
         参数id
         */
        var paramId = {
            defenseGroupName: '#defenseGroupName',
            buildingId: '#select_building',
            schoolroomId: '#select_schoolroom',
            note: '#note'
        };

        /*
         参数
         */
        var param = {
            defenseGroupName: $(paramId.defenseGroupName).val(),
            buildingId: $(paramId.buildingId).val(),
            schoolroomId: $(paramId.schoolroomId).val(),
            note: $(paramId.note).val()
        };

        /*
         检验id
         */
        var validId = {
            defenseGroupName: '#valid_defense_group_name',
            location: '#valid_location',
            note: '#valid_note'
        };

        /*
         错误消息id
         */
        var errorMsgId = {
            defenseGroupName: '#defense_group_name_error_msg',
            location: '#location_error_msg',
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

        function startLoading() {
            // 显示遮罩
            $('#page-wrapper').showLoading();
        }

        function endLoading() {
            // 去除遮罩
            $('#page-wrapper').hideLoading();
        }

        /*
         返回
         */
        $('#page_back').click(function () {
            $.address.value(ajax_url.back + '?id=' + init_page_param.graduationDesignReleaseId);
        });

        /**
         * 初始化参数
         */
        function initParam() {
            param.defenseGroupName = $(paramId.defenseGroupName).val();
            param.buildingId = $(paramId.buildingId).val();
            param.schoolroomId = $(paramId.schoolroomId).val();
            param.note = $(paramId.note).val();
        }

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
            $(paramId.defenseGroupName).maxlength({
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

            $(paramId.buildingId).html(template(data));
        }

        // 当改变楼时，变换教室数据.
        $(paramId.buildingId).change(function () {
            initParam();
            var building = param.buildingId;

            changeSchoolroom(building);// 根据楼重新加载教室数据

            if (Number(building) > 0) {
                validSuccessDom(validId.location, errorMsgId.location);
            } else {
                validErrorDom(validId.location, errorMsgId.location, '请选择楼');
            }
        });

        $(paramId.schoolroomId).change(function () {
            initParam();
            var schoolroom = param.schoolroomId;

            if (Number(schoolroom) > 0) {
                validSuccessDom(validId.location, errorMsgId.location);
            } else {
                validErrorDom(validId.location, errorMsgId.location, '请选择教室');
            }
        });

        /**
         * 改变教室选项
         * @param building_id 楼id
         */
        function changeSchoolroom(building_id) {
            if (Number(building_id) === 0) {
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

                $(paramId.schoolroomId).html(template(context));
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

                    $(paramId.schoolroomId).html(template(data));
                });
            }
        }

        // 即时验证
        $(paramId.defenseGroupName).blur(function () {
            initParam();
            var defenseGroupName = param.defenseGroupName;
            if (defenseGroupName.length <= 0 || defenseGroupName.length > 20) {
                validErrorDom(validId.defenseGroupName, errorMsgId.defenseGroupName, '组别20个字符以内');
            } else {
                validSuccessDom(validId.defenseGroupName, errorMsgId.defenseGroupName);
            }
        });

        $(paramId.note).blur(function () {
            initParam();
            var note = param.note;
            if (note.length > 0) {
                if (note.length > 100) {
                    validErrorDom(validId.note, errorMsgId.note, '备注100个字符以内');
                } else {
                    validSuccessDom(validId.note, errorMsgId.note);
                }
            }
        });

        $('#save').click(function () {
            add();
        });

        /*
         添加询问
         */
        function add() {
            initParam();
            var defenseGroupName = param.defenseGroupName;
            var msg;
            msg = Messenger().post({
                message: "确定添加 " + defenseGroupName + " 吗?",
                actions: {
                    retry: {
                        label: '确定',
                        phrase: 'Retrying TIME',
                        action: function () {
                            msg.cancel();
                            validDefenseGroupName();
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

        function validDefenseGroupName() {
            var defenseGroupName = param.defenseGroupName;
            if (defenseGroupName.length <= 0 || defenseGroupName.length > 20) {
                Messenger().post({
                    message: '组别20个字符以内',
                    type: 'error',
                    showCloseButton: true
                });
            } else {
                validNote();
            }
        }

        function validNote() {
            var note = param.note;
            if (note.length > 0) {
                if (note.length > 100) {
                    Messenger().post({
                        message: '备注100个字符以内',
                        type: 'error',
                        showCloseButton: true
                    });
                } else {
                    sendAjax();
                }
            }
        }

        function sendAjax() {
            Messenger().run({
                successMessage: '保存数据成功',
                errorMessage: '保存数据失败',
                progressMessage: '正在保存数据....'
            }, {
                url: web_path + ajax_url.save,
                type: 'post',
                data: $('#add_form').serialize(),
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

    }
)
;