/**
 * Created by lenovo on 2016/12/6.
 */
//# sourceURL=internship_review.js
require(["jquery", "handlebars", "messenger", "jquery.address", "jquery.simple-pagination", "jquery.showLoading"],
    function ($, Handlebars) {

        /*
         ajax url.
         */
        var ajax_url = {
            internship_review_data_url: '/web/internship/review/data',
            audit_url: '/web/internship/review/audit',
            pass_url: '/web/internship/review/pass',
            fail_url: '/web/internship/review/fail',
            base_info_apply_url: '/web/internship/review/base_info_apply',
            base_info_fill_url: '/web/internship/review/base_info_fill',
            company_apply_url: '/web/internship/review/company_apply',
            company_fill_url: '/web/internship/review/company_fill',
            access_condition_url: '/web/internship/review/condition'
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
            var source = $("#internship-release-template").html();
            var template = Handlebars.compile(source);

            Handlebars.registerHelper('internship_title', function () {
                var value = Handlebars.escapeExpression(this.internshipTitle);
                return new Handlebars.SafeString(value);
            });

            Handlebars.registerHelper('school_name', function () {
                var value = Handlebars.escapeExpression(this.schoolName);
                return new Handlebars.SafeString(value);
            });

            Handlebars.registerHelper('college_name', function () {
                var value = Handlebars.escapeExpression(this.collegeName);
                return new Handlebars.SafeString(value);
            });

            Handlebars.registerHelper('department_name', function () {
                var value = Handlebars.escapeExpression(this.departmentName);
                return new Handlebars.SafeString(value);
            });

            Handlebars.registerHelper('real_name', function () {
                var value = Handlebars.escapeExpression(this.realName);
                return new Handlebars.SafeString(value);
            });

            var html = template(data);
            $(tableData).html(html);
        }

        /*
         进行审核
         */
        $(tableData).delegate('.review', "click", function () {
            var id = $(this).attr('data-id');
            // 进入条件判断
            $.post(web_path + ajax_url.access_condition_url, {id: id}, function (data) {
                if (data.state) {
                    $.address.value(ajax_url.audit_url + "?id=" + id);
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
         已通过列表
         */
        $(tableData).delegate('.pass_apply', "click", function () {
            var id = $(this).attr('data-id');
            // 进入条件判断
            $.post(web_path + ajax_url.access_condition_url, {id: id}, function (data) {
                if (data.state) {
                    $.address.value(ajax_url.pass_url + "?id=" + id);
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
         未通过列表
         */
        $(tableData).delegate('.fail_apply', "click", function () {
            var id = $(this).attr('data-id');
            // 进入条件判断
            $.post(web_path + ajax_url.access_condition_url, {id: id}, function (data) {
                if (data.state) {
                    $.address.value(ajax_url.fail_url + "?id=" + id);
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
         基本信息修改申请
         */
        $(tableData).delegate('.basic_apply', "click", function () {
            var id = $(this).attr('data-id');
            // 进入条件判断
            $.post(web_path + ajax_url.access_condition_url, {id: id}, function (data) {
                if (data.state) {
                    $.address.value(ajax_url.base_info_apply_url + "?id=" + id);
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
         单位信息修改申请
         */
        $(tableData).delegate('.company_apply', "click", function () {
            var id = $(this).attr('data-id');
            // 进入条件判断
            $.post(web_path + ajax_url.access_condition_url, {id: id}, function (data) {
                if (data.state) {
                    $.address.value(ajax_url.company_apply_url + "?id=" + id);
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
         基本信息填写中
         */
        $(tableData).delegate('.basic_fill', "click", function () {
            var id = $(this).attr('data-id');
            // 进入条件判断
            $.post(web_path + ajax_url.access_condition_url, {id: id}, function (data) {
                if (data.state) {
                    $.address.value(ajax_url.base_info_fill_url + "?id=" + id);
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
         单位信息填写中
         */
        $(tableData).delegate('.company_fill', "click", function () {
            var id = $(this).attr('data-id');
            // 进入条件判断
            $.post(web_path + ajax_url.access_condition_url, {id: id}, function (data) {
                if (data.state) {
                    $.address.value(ajax_url.company_fill_url + "?id=" + id);
                } else {
                    Messenger().post({
                        message: data.msg,
                        type: 'error',
                        showCloseButton: true
                    });
                }
            });
        });

        init();

        /**
         * 初始化数据
         */
        function init() {
            startLoading();
            $.get(web_path + ajax_url.internship_review_data_url, param, function (data) {
                endLoading();
                if (data.listResult.length > 0) {
                    createPage(data);
                }
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
            $.get(web_path + ajax_url.internship_review_data_url, param, function (data) {
                endLoading();
                listData(data);
            });
        }

    });