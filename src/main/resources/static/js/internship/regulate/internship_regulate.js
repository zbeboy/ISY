/**
 * Created by lenovo on 2016/12/14.
 */
//# sourceURL=internship_regulate.js
require(["jquery", "handlebars", "messenger", "jquery.address", "jquery.simple-pagination", "jquery.showLoading"],
    function ($, Handlebars) {

        /*
         ajax url.
         */
        var ajax_url = {
            internship_regulate_data_url: '/anyone/internship/data',
            regulate_url: '/web/internship/regulate/list',
            my_regulate: '/web/internship/regulate/my/list',
            my_regulate_condition: '/web/internship/regulate/my/list/condition',
            add: '/web/internship/regulate/list/add',
            valid_is_staff: '/anyone/valid/cur/is/staff',
            valid_staff: '/web/internship/regulate/valid/staff',
            access_condition_url: '/web/internship/regulate/condition'
        };

        /*
         参数id
         */
        var paramId = {
            internshipTitle: '#search_internship_title'
        };

        /*
         参数
         */
        var param = {
            searchParams: '',
            pageNum: 0,
            pageSize: 2,
            displayedPages: 3
        };

        /*
         检验id
         */
        var validId = {
            staff: '#valid_staff'
        };

        /*
         错误消息id
         */
        var errorMsgId = {
            staff: '#staff_error_msg'
        };

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

        var tableData = '#tableData';

        function startLoading() {
            // 显示遮罩
            $('#page-wrapper').showLoading();
        }

        function endLoading() {
            // 去除遮罩
            $('#page-wrapper').hideLoading();
        }

        /*
         清空参数
         */
        function cleanParam() {
            $(paramId.internshipTitle).val('');
        }

        /**
         * 刷新查询参数
         */
        function refreshSearch() {
            var params = {
                internshipTitle: $(paramId.internshipTitle).val()
            };
            param.pageNum = 0;
            param.searchParams = JSON.stringify(params);
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

        $('#refresh').click(function () {
            init();
        });

        $(paramId.internshipTitle).keyup(function (event) {
            if (event.keyCode == 13) {
                refreshSearch();
                init();
            }
        });

        /**
         * 列表数据
         * @param data 数据
         */
        function listData(data) {
            var template = Handlebars.compile($("#internship-release-template").html());

            Handlebars.registerHelper('internship_title', function () {
                return new Handlebars.SafeString(Handlebars.escapeExpression(this.internshipTitle));
            });

            Handlebars.registerHelper('school_name', function () {
                return new Handlebars.SafeString(Handlebars.escapeExpression(this.schoolName));
            });

            Handlebars.registerHelper('college_name', function () {
                return new Handlebars.SafeString(Handlebars.escapeExpression(this.collegeName));
            });

            Handlebars.registerHelper('department_name', function () {
                return new Handlebars.SafeString(Handlebars.escapeExpression(this.departmentName));
            });

            Handlebars.registerHelper('real_name', function () {
                return new Handlebars.SafeString(Handlebars.escapeExpression(this.realName));
            });

            $(tableData).html(template(data));
        }

        /*
         监管列表
         */
        $(tableData).delegate('.regulate_list', "click", function () {
            var id = $(this).attr('data-id');
            $.address.value(ajax_url.regulate_url + "?id=" + id);
        });

        /*
         我的监管
         */
        $(tableData).delegate('.my_regulate', "click", function () {
            var id = $(this).attr('data-id');
            $.post(web_path + ajax_url.my_regulate_condition,{id:id},function (data) {
                if(data.state){
                    $.address.value(ajax_url.my_regulate + "?id=" + id);
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
         写监管记录
         */
        $(tableData).delegate('.write_regulate', "click", function () {
            var id = $(this).attr('data-id');
            // 如果用户类型不是教职工，则这里需要一个弹窗，填写教职工账号或教职工工号以获取教职工id
            $.get(web_path + ajax_url.valid_is_staff, function (data) {
                if (data.state) {
                    accessAdd(id, data.objectResult);
                } else {
                    $('#staffInfoInternshipReleaseId').val(id);
                    $('#staffModal').modal('show');
                }
            });
        });

        /*
         教职工 form 确定
         */
        $('#staffInfo').click(function () {
            validStaff($('#staffInfoInternshipReleaseId').val());
        });

        // 用于可以去添加页
        var to_add = false;
        var to_add_data = '';

        $('#staffModal').on('hidden.bs.modal', function (e) {
            // do something...
            if (to_add) {
                to_add = false;
                accessAdd($('#staffInfoInternshipReleaseId').val(), to_add_data);
            }
        });

        /**
         * 检验教职工信息
         */
        function validStaff(id) {
            var staffUsername = $('#staffUsername').val();
            var staffNumber = $('#staffNumber').val();
            if (staffUsername.length <= 0 && staffNumber.length <= 0) {
                validErrorDom(validId.staff, errorMsgId.staff, '请至少填写一项教职工信息');
            } else {
                var staff = "";
                var type = -1;
                if (staffUsername.length > 0) {
                    staff = staffUsername;
                    type = 0;
                }

                if (staffNumber.length > 0) {
                    staff = staffNumber;
                    type = 1;
                }

                // 检验教职工信息
                Messenger().run({
                    errorMessage: '请求失败'
                }, {
                    url: web_path + ajax_url.valid_staff,
                    type: 'post',
                    data: {staff: staff, internshipReleaseId: id, type: type},
                    success: function (data) {
                        if (data.state) {
                            to_add_data = data.objectResult;
                            to_add = true;
                            $('#staffModal').modal('hide');
                        } else {
                            validErrorDom(validId.staff, errorMsgId.staff, data.msg);
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
         * 进入添加
         * @param internshipReleaseId
         * @param staffId
         */
        function accessAdd(internshipReleaseId, staffId) {
            $.address.value(ajax_url.add + '?id=' + internshipReleaseId + '&staffId=' + staffId);
        }

        init();

        /**
         * 初始化数据
         */
        function init() {
            startLoading();
            $.get(web_path + ajax_url.internship_regulate_data_url, param, function (data) {
                endLoading();
                createPage(data);
                listData(data);
            });
        }

        /**
         * 创建分页
         * @param data 数据
         */
        function createPage(data) {
            $('#pagination').pagination({
                pages: data.paginationUtils.totalPages,
                displayedPages: data.paginationUtils.displayedPages,
                hrefTextPrefix: '',
                prevText: '上一页',
                nextText: '下一页',
                cssStyle: '',
                listStyle: 'pagination',
                onPageClick: function (pageNumber, event) {
                    // Callback triggered when a page is clicked
                    // Page number is given as an optional parameter
                    console.log(pageNumber);
                    nextPage(pageNumber);
                }
            });
        }

        /**
         * 下一页
         * @param pageNumber 当前页
         */
        function nextPage(pageNumber) {
            param.pageNum = pageNumber;
            startLoading();
            $.get(web_path + ajax_url.internship_regulate_data_url, param, function (data) {
                endLoading();
                listData(data);
            });
        }

    });