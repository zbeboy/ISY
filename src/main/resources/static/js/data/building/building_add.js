/**
 * Created by zbeboy on 2017/5/31.
 */
require(["jquery", "handlebars", "constants", "nav_active", "lodash_plugin", "messenger", "jquery.address", "bootstrap-maxlength", "jquery.showLoading"],
    function ($, Handlebars, constants, nav_active, DP) {

        /*
         ajax url.
         */
        var ajax_url = {
            school_data_url: '/user/schools',
            college_data_url: '/user/colleges',
            save: '/web/data/building/save',
            valid: '/web/data/building/save/valid',
            back: '/web/menu/data/building'
        };

        // 刷新时选中菜单
        nav_active(ajax_url.back);

        /*
         参数id
         */
        var paramId = {
            schoolId: '#select_school',
            collegeId: '#select_college',
            buildingName: '#buildingName'
        };

        /*
         参数
         */
        var param = {
            schoolId: $(paramId.schoolId).val(),
            collegeId: $(paramId.collegeId).val(),
            buildingName: $(paramId.buildingName).val(),
            buildingIsDel: DP.defaultUndefinedValue($('input[name="buildingIsDel"]:checked').val(), 0)
        };

        /*
         检验id
         */
        var validId = {
            schoolId: '#valid_school',
            collegeId: '#valid_college',
            buildingName: '#valid_building_name'
        };

        /*
         错误消息id
         */
        var errorMsgId = {
            schoolId: '#school_error_msg',
            collegeId: '#college_error_msg',
            buildingName: '#building_name_error_msg'
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
            param.schoolId = $(paramId.schoolId).val();
            if (init_page_param.currentUserRoleName === constants.global_role_name.system_role) {
                param.collegeId = $(paramId.collegeId).val();
            } else {
                param.collegeId = init_page_param.collegeId;
            }
            param.buildingName = $(paramId.buildingName).val();
            param.buildingIsDel = DP.defaultUndefinedValue($('input[name="buildingIsDel"]:checked').val(), 0);
        }

        /*
         初始化数据
         */
        init();

        /**
         * 学校数据展现
         * @param data json数据
         */
        function schoolData(data) {
            var template = Handlebars.compile($("#school-template").html());

            Handlebars.registerHelper('school_value', function () {
                var value = Handlebars.escapeExpression(this.schoolId);
                return new Handlebars.SafeString(value);
            });

            Handlebars.registerHelper('school_name', function () {
                var name = Handlebars.escapeExpression(this.schoolName);
                return new Handlebars.SafeString(name);
            });

            $(paramId.schoolId).html(template(data));
        }

        /**
         * 初始化界面
         */
        function init() {
            if (init_page_param.currentUserRoleName === constants.global_role_name.system_role) {
                startLoading();
                $.get(web_path + ajax_url.school_data_url, function (data) {
                    endLoading();
                    schoolData(data);
                });
            }

            initMaxLength();
        }

        /**
         * 初始化Input max length
         */
        function initMaxLength() {
            $(paramId.buildingName).maxlength({
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
            changeCollege(school);// 根据学校重新加载院数据

            // 改变选项时，检验
            if (Number(school) > 0) {
                validSuccessDom(validId.schoolId, errorMsgId.schoolId);
            } else {
                validErrorDom(validId.schoolId, errorMsgId.schoolId, '请选择学校');
            }

            validCleanDom(validId.collegeId, errorMsgId.collegeId);
        });

        // 当改变学院时，变换系数据.
        $(paramId.collegeId).change(function () {
            initParam();
            var college = param.collegeId;

            if (Number(college) > 0) {
                validSuccessDom(validId.collegeId, errorMsgId.collegeId);
            } else {
                validErrorDom(validId.collegeId, errorMsgId.collegeId, '请选择院');
            }
        });

        /**
         * 改变学院选项
         * @param school_id 学校id
         */
        function changeCollege(school_id) {
            if (Number(school_id) == 0) {
                var source = $("#college-template").html();
                var template = Handlebars.compile(source);

                var context = {
                    listResult: [
                        {name: "请选择院", value: ""}
                    ]
                };

                Handlebars.registerHelper('college_value', function () {
                    var value = Handlebars.escapeExpression(this.value);
                    return new Handlebars.SafeString(value);
                });

                Handlebars.registerHelper('college_name', function () {
                    var name = Handlebars.escapeExpression(this.name);
                    return new Handlebars.SafeString(name);
                });

                var html = template(context);
                $(paramId.collegeId).html(html);
            } else {
                // 根据学校id查询院数据
                startLoading();
                $.post(web_path + ajax_url.college_data_url, {schoolId: school_id}, function (data) {
                    endLoading();
                    var template = Handlebars.compile($("#college-template").html());

                    Handlebars.registerHelper('college_value', function () {
                        var value = Handlebars.escapeExpression(this.collegeId);
                        return new Handlebars.SafeString(value);
                    });

                    Handlebars.registerHelper('college_name', function () {
                        var name = Handlebars.escapeExpression(this.collegeName);
                        return new Handlebars.SafeString(name);
                    });

                    $(paramId.collegeId).html(template(data));
                });
            }
        }

        /*
         即时检验系名
         */
        $(paramId.buildingName).blur(function () {
            initParam();
            var buildingName = param.buildingName;
            if (buildingName.length <= 0 || buildingName.length > 30) {
                validErrorDom(validId.buildingName, errorMsgId.buildingName, '楼名30个字符以内');
            } else {
                // 楼名是否重复
                Messenger().run({
                    errorMessage: '请求失败'
                }, {
                    url: web_path + ajax_url.valid,
                    type: 'post',
                    data: param,
                    success: function (data) {
                        if (data.state) {
                            validSuccessDom(validId.buildingName, errorMsgId.buildingName);
                        } else {
                            validErrorDom(validId.buildingName, errorMsgId.buildingName, data.msg);
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
            var buildingName = param.buildingName;
            var msg;
            msg = Messenger().post({
                message: "确定添加楼 '" + buildingName + "'  吗?",
                actions: {
                    retry: {
                        label: '确定',
                        phrase: 'Retrying TIME',
                        action: function () {
                            msg.cancel();
                            if (init_page_param.currentUserRoleName === constants.global_role_name.system_role) {
                                validSchoolId();
                            } else {
                                validBuildingName();
                            }
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
                validCollegeId();
            }
        }

        /**
         * 检验院id
         */
        function validCollegeId() {
            var collegeId = param.collegeId;
            if (Number(collegeId) <= 0) {
                Messenger().post({
                    message: '请选择院',
                    type: 'error',
                    showCloseButton: true
                });
            } else {
                validBuildingName();
            }
        }

        /**
         * 添加时检验并提交数据
         */
        function validBuildingName() {
            initParam();
            var buildingName = param.buildingName;
            if (buildingName.length <= 0 || buildingName.length > 30) {
                Messenger().post({
                    message: '楼名为1~30个字符',
                    type: 'error',
                    showCloseButton: true
                });
            } else {
                // 楼名是否重复
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
                url: web_path + ajax_url.save,
                type: 'post',
                data: param,
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