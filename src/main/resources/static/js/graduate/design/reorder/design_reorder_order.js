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
        timer_url: '/web/graduate/design/reorder/timer',
        back: '/web/menu/graduate/design/reorder'
    };

    /*
     参数id
     */
    var paramId = {
        studentName: '#search_student_name',
        studentNumber: '#search_student_number'
    };

    /*
     参数
     */
    var param = {
        graduationDesignReleaseId: init_page_param.graduationDesignReleaseId,
        defenseGroupId: init_page_param.defenseGroupId,
        studentName: $(paramId.studentName).val(),
        studentNumber: $(paramId.studentNumber).val()
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
    }

    /**
     * 刷新查询参数
     */
    function refreshSearch() {
        param.studentName = $(paramId.studentName).val();
        param.studentNumber = $(paramId.studentNumber).val();
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
        var template = Handlebars.compile($("#order-template").html());
        Handlebars.registerHelper('subject', function () {
            var v = '';
            if (this.subject !== null) {
                if (this.subject.length > 5) {
                    v = this.subject.substring(0, 5) + '...';
                } else {
                    v = this.subject;
                }
            }
            return '<button type="button" class="btn btn-default" data-toggle="tooltip" data-placement="top" title="' + this.subject + '">' + v + '</button>';
        });

        Handlebars.registerHelper('operator', function () {
            return buildOperatorButton(this);
        });

        $(tableData).html(template(data));
        $('[data-toggle="tooltip"]').tooltip();
        $('#tablesawTable').tablesaw().data("tablesaw").refresh();
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
                        "name": "状态",
                        "css": "",
                        "type": "default",
                        "defenseOrderId": c.defenseOrderId,
                        "sortNum": c.sortNum,
                        "studentName": c.studentName
                    },
                    {
                        "name": "打分",
                        "css": "",
                        "type": "default",
                        "defenseOrderId": c.defenseOrderId,
                        "sortNum": c.sortNum,
                        "studentName": c.studentName
                    },
                    {
                        "name": "成绩",
                        "css": "",
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

        return template(context);
    }

    /*
     设置
     */
    $(tableData).delegate('.timer', "click", function () {
        var id = $(this).attr('data-id');
        var name = $(this).attr('data-student');
        $('#timerDefenseOrderId').val(id);
        $('#timerModalLabel').text(name);
        $('#timerModal').modal('show');
    });

    // 计时
    $('#toTimer').click(function () {
        var id = $('#timerDefenseOrderId').val();
        var timer = Math.round(Number($('#timerInput').val()));
        $('#timerModal').modal('hide');
        window.open(web_path + ajax_url.timer_url + '?defenseOrderId=' + id + '&timer=' + timer);
    });

});