/**
 * Created by zbeboy on 2017/7/20.
 */
require(["jquery", "nav_active", "handlebars", "messenger", "jquery.address",
    "jquery.showLoading", "tablesaw", "bootstrap"], function ($, nav_active, Handlebars) {
    /*
     ajax url.
     */
    var ajax_url = {
        data_url: '/anyone/graduate/design/defense/order/data',
        scores: '/user/scores',
        order_url: '/web/graduate/design/reorder/info',
        grade_info_url: '/web/graduate/design/reorder/grade/info',
        mark_info_url: '/web/graduate/design/reorder/mark/info',
        timer_url: '/web/graduate/design/reorder/timer',
        status_url: '/web/graduate/design/reorder/status',
        grade_url: '/web/graduate/design/reorder/grade',
        mark_url: '/web/graduate/design/reorder/mark',
        back: '/web/menu/graduate/design/reorder'
    };

    /*
     参数id
     */
    var paramId = {
        studentName: '#search_student_name',
        studentNumber: '#search_student_number',
        scoreTypeId: '#select_score',
        defenseStatus: '#select_status'
    };

    /*
     参数
     */
    var param = {
        graduationDesignReleaseId: init_page_param.graduationDesignReleaseId,
        defenseGroupId: init_page_param.defenseGroupId,
        studentName: $(paramId.studentName).val(),
        studentNumber: $(paramId.studentNumber).val(),
        scoreTypeId: $(paramId.scoreTypeId).val(),
        defenseStatus: $(paramId.defenseStatus).val()
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

    /*
     清空参数
     */
    function cleanParam() {
        $(paramId.studentName).val('');
        $(paramId.studentNumber).val('');
        $(paramId.scoreTypeId).val(0);
        $(paramId.defenseStatus).val(-1);
    }

    /**
     * 刷新查询参数
     */
    function refreshSearch() {
        param.studentName = $(paramId.studentName).val();
        param.studentNumber = $(paramId.studentNumber).val();
        param.scoreTypeId = $(paramId.scoreTypeId).val();
        param.defenseStatus = $(paramId.defenseStatus).val();
    }

    /*
     搜索
     */
    $('#search').click(function () {
        refreshSearch();
        init();
    });

    /*
     重置
     */
    $('#reset_search').click(function () {
        cleanParam();
        refreshSearch();
        init();
    });

    $(paramId.studentName).keyup(function (event) {
        if (event.keyCode === 13) {
            refreshSearch();
            init();
        }
    });

    $(paramId.studentNumber).keyup(function (event) {
        if (event.keyCode === 13) {
            refreshSearch();
            init();
        }
    });

    $(paramId.scoreTypeId).change(function () {
        refreshSearch();
        init();
    });

    $(paramId.defenseStatus).change(function () {
        refreshSearch();
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
    initScore();

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

    function initScore() {
        $.get(web_path + ajax_url.scores, function (data) {
            if (data.state) {
                scoreData(data);
                markScoreData(data);
            }
        });
    }

    /**
     * 列表数据
     * @param data 数据
     */
    function listData(data) {
        var template = Handlebars.compile($("#order-template").html());
        Handlebars.registerHelper('subject', function () {
            var v = '';
            var html = '';
            if (this.subject !== null) {
                if (this.subject.length > 5) {
                    v = this.subject.substring(0, 5) + '...';
                } else {
                    v = this.subject;
                }
                html = '<button type="button" class="btn btn-default" data-toggle="tooltip" data-placement="top" title="' + this.subject + '">' + v + '</button>';
            }
            return html;
        });

        Handlebars.registerHelper('operator', function () {
            return buildOperatorButton(this);
        });

        Handlebars.registerHelper('defense_status', function () {
            var v = '';
            if (this.defenseStatus === 0) {
                v = '未开始';
            } else if (this.defenseStatus === 1) {
                v = '进行中';
            } else if (this.defenseStatus === 2) {
                v = '已结束';
            } else if (this.defenseStatus === 3) {
                v = '缺席';
            }
            return new Handlebars.SafeString(Handlebars.escapeExpression(v));
        });

        $(tableData).html(template(data));
        $('[data-toggle="tooltip"]').tooltip();
        $('#tablesawTable').tablesaw().data("tablesaw").refresh();
    }

    /**
     * 成绩数据
     * @param data 数据
     */
    function scoreData(data) {
        var template = Handlebars.compile($("#score-template").html());
        $(paramId.scoreTypeId).html(template(data));
    }

    /**
     * 修改成绩数据
     * @param data
     */
    function markScoreData(data) {
        var template = Handlebars.compile($("#mark-score-template").html());
        $('#scoreData').html(template(data));
        $($('#scoreData').children()[0]).remove();
    }

    /**
     * 教师打分情况
     * @param data
     */
    function markTeacherData(data) {
        var template = Handlebars.compile($("#rate-template").html());
        $('#rate').html(template(data));
    }

    /**
     * 构建操作按钮
     * @param c
     */
    function buildOperatorButton(c) {
        // 预编译模板
        var template = Handlebars.compile($("#operator_button").html());
        var context =
            {
                func: [
                    {
                        "name": "计时",
                        "css": "timer",
                        "type": "primary",
                        "defenseOrderId": c.defenseOrderId,
                        "sortNum": c.sortNum,
                        "studentName": c.studentName
                    },
                    {
                        "name": "问题",
                        "css": "",
                        "type": "default",
                        "defenseOrderId": c.defenseOrderId,
                        "sortNum": c.sortNum,
                        "studentName": c.studentName
                    }
                ]
            };

        // 是管理员或系统
        if (init_page_param.reorderIsSuper) {
            context =
                {
                    func: [
                        {
                            "name": "计时",
                            "css": "timer",
                            "type": "primary",
                            "defenseOrderId": c.defenseOrderId,
                            "sortNum": c.sortNum,
                            "studentName": c.studentName
                        },
                        {
                            "name": "状态",
                            "css": "defenseStatus",
                            "type": "default",
                            "defenseOrderId": c.defenseOrderId,
                            "sortNum": c.sortNum,
                            "studentName": c.studentName
                        },
                        {
                            "name": "打分",
                            "css": "grade",
                            "type": "default",
                            "defenseOrderId": c.defenseOrderId,
                            "sortNum": c.sortNum,
                            "studentName": c.studentName
                        },
                        {
                            "name": "成绩",
                            "css": "mark",
                            "type": "default",
                            "defenseOrderId": c.defenseOrderId,
                            "sortNum": c.sortNum,
                            "studentName": c.studentName
                        },
                        {
                            "name": "问题",
                            "css": "",
                            "type": "default",
                            "defenseOrderId": c.defenseOrderId,
                            "sortNum": c.sortNum,
                            "studentName": c.studentName
                        }
                    ]
                };
        } else if (init_page_param.reorderIsLeader) {
            context =
                {
                    func: [
                        {
                            "name": "计时",
                            "css": "timer",
                            "type": "primary",
                            "defenseOrderId": c.defenseOrderId,
                            "sortNum": c.sortNum,
                            "studentName": c.studentName
                        },
                        {
                            "name": "状态",
                            "css": "defenseStatus",
                            "type": "default",
                            "defenseOrderId": c.defenseOrderId,
                            "sortNum": c.sortNum,
                            "studentName": c.studentName
                        },
                        {
                            "name": "打分",
                            "css": "grade",
                            "type": "default",
                            "defenseOrderId": c.defenseOrderId,
                            "sortNum": c.sortNum,
                            "studentName": c.studentName
                        },
                        {
                            "name": "成绩",
                            "css": "mark",
                            "type": "default",
                            "defenseOrderId": c.defenseOrderId,
                            "sortNum": c.sortNum,
                            "studentName": c.studentName
                        },
                        {
                            "name": "问题",
                            "css": "",
                            "type": "default",
                            "defenseOrderId": c.defenseOrderId,
                            "sortNum": c.sortNum,
                            "studentName": c.studentName
                        }
                    ]
                };
        } else if (init_page_param.reorderIsSecretary) {
            context =
                {
                    func: [
                        {
                            "name": "计时",
                            "css": "timer",
                            "type": "primary",
                            "defenseOrderId": c.defenseOrderId,
                            "sortNum": c.sortNum,
                            "studentName": c.studentName
                        },
                        {
                            "name": "状态",
                            "css": "defenseStatus",
                            "type": "default",
                            "defenseOrderId": c.defenseOrderId,
                            "sortNum": c.sortNum,
                            "studentName": c.studentName
                        },
                        {
                            "name": "成绩",
                            "css": "mark",
                            "type": "default",
                            "defenseOrderId": c.defenseOrderId,
                            "sortNum": c.sortNum,
                            "studentName": c.studentName
                        },
                        {
                            "name": "问题",
                            "css": "",
                            "type": "default",
                            "defenseOrderId": c.defenseOrderId,
                            "sortNum": c.sortNum,
                            "studentName": c.studentName
                        }
                    ]
                };
        } else if (init_page_param.reorderIsMember) {
            context =
                {
                    func: [
                        {
                            "name": "计时",
                            "css": "timer",
                            "type": "primary",
                            "defenseOrderId": c.defenseOrderId,
                            "sortNum": c.sortNum,
                            "studentName": c.studentName
                        },
                        {
                            "name": "打分",
                            "css": "grade",
                            "type": "default",
                            "defenseOrderId": c.defenseOrderId,
                            "sortNum": c.sortNum,
                            "studentName": c.studentName
                        },
                        {
                            "name": "问题",
                            "css": "",
                            "type": "default",
                            "defenseOrderId": c.defenseOrderId,
                            "sortNum": c.sortNum,
                            "studentName": c.studentName
                        }
                    ]
                };
        }

        return template(context);
    }

    /*
     计时
     */
    $(tableData).delegate('.timer', "click", function () {
        var id = $(this).attr('data-id');
        var name = $(this).attr('data-student');
        $('#timerDefenseOrderId').val(id);
        $('#timerModalLabel').text(name);
        $('#timerModal').modal('show');
    });

    // 计时确定
    $('#toTimer').click(function () {
        var id = $('#timerDefenseOrderId').val();
        var timer = Math.round(Number($('#timerInput').val()));
        $('#timerModal').modal('hide');
        window.open(web_path + ajax_url.timer_url + '?defenseOrderId=' + id + '&timer=' + timer);
    });

    /*
   状态
   */
    $(tableData).delegate('.defenseStatus', "click", function () {
        var id = $(this).attr('data-id');
        var name = $(this).attr('data-student');
        $.post(web_path + ajax_url.order_url, {
            id: init_page_param.graduationDesignReleaseId,
            defenseOrderId: id
        }, function (data) {
            if (data.state) {
                $('#statusDefenseOrderId').val(id);
                $('#statusModalLabel').text(name);
                selectedStatus(data);
                $('#statusModal').modal('show');
            } else {
                Messenger().post({
                    message: data.msg,
                    type: 'error',
                    showCloseButton: true
                });
            }
        });
    });

    /**
     * 选中状态
     * @param data
     */
    function selectedStatus(data) {
        var inputs = $("input[name='defenseStatus']");
        for (var i = 0; i < inputs.length; i++) {
            if (Number($(inputs[i]).val()) === data.objectResult.defenseStatus) {
                $(inputs[i]).prop('checked', true);
                break;
            }
        }
    }

    // 状态确定
    $('#toStatus').click(function () {
        $.post(web_path + ajax_url.status_url, $('#statusForm').serialize(), function (data) {
            if (data.state) {
                $('#statusModal').modal('hide');
                init();
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
     打分
    */
    $(tableData).delegate('.grade', "click", function () {
        var id = $(this).attr('data-id');
        var name = $(this).attr('data-student');
        $.post(web_path + ajax_url.grade_info_url, {
            graduationDesignReleaseId: init_page_param.graduationDesignReleaseId,
            defenseOrderId: id,
            defenseGroupId: init_page_param.defenseGroupId
        }, function (data) {
            if (data.state) {
                $('#grade').val(data.objectResult);
                $('#gradeDefenseOrderId').val(id);
                $('#gradeModalLabel').text(name);
                $('#gradeModal').modal('show');
            } else {
                Messenger().post({
                    message: data.msg,
                    type: 'error',
                    showCloseButton: true
                });
            }
        });
    });

    // 打分确定
    $('#toGrade').click(function () {
        $.post(web_path + ajax_url.grade_url, $('#gradeForm').serialize(), function (data) {
            if (data.state) {
                $('#gradeModal').modal('hide');
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
     成绩
    */
    $(tableData).delegate('.mark', "click", function () {
        var id = $(this).attr('data-id');
        var name = $(this).attr('data-student');
        $.post(web_path + ajax_url.mark_info_url, {
            graduationDesignReleaseId: init_page_param.graduationDesignReleaseId,
            defenseOrderId: id,
            defenseGroupId: init_page_param.defenseGroupId
        }, function (data) {
            if (data.state) {
                markTeacherData(data);
                selectedScore(data.objectResult.scoreTypeId);
                $('#markDefenseOrderId').val(id);
                $('#markModalLabel').text(name);
                $('#markModal').modal('show');
            } else {
                Messenger().post({
                    message: data.msg,
                    type: 'error',
                    showCloseButton: true
                });
            }
        });
    });

    function selectedScore(scoreTypeId) {
        var scoreTypes = $('.markScore');
        for (var i = 0; i < scoreTypes.length; i++) {
            if (Number($(scoreTypes[i]).val()) === scoreTypeId) {
                $(scoreTypes[i]).prop('checked', true);
                break;
            }
        }
    }

    // 成绩确定
    $('#toMark').click(function () {
        $.post(web_path + ajax_url.mark_url, $('#markForm').serialize(), function (data) {
            if (data.state) {
                $('#markModal').modal('hide');
                init();
            } else {
                Messenger().post({
                    message: data.msg,
                    type: 'error',
                    showCloseButton: true
                });
            }
        });
    });

});