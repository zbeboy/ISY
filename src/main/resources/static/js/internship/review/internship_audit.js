/**
 * Created by lenovo on 2016/12/6.
 */
//# sourceURL=internship_audit.js
require(["jquery", "handlebars", "messenger", "jquery.address", "jquery.simple-pagination", "jquery.showLoading"],
    function ($, Handlebars) {

        /*
         ajax url.
         */
        var ajax_url = {
            audit_data_url: '/web/internship/review/audit/data',
            back: '/web/menu/internship/apply'
        };

        /*
         参数id
         */
        var paramId = {
            studentName: '#search_real_name',
            studentNumber:'#search_student_number',
            scienceName:'#search_science_name',
            organizeName:'#search_organize_name'
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
            $(paramId.studentName).val('');
            $(paramId.studentNumber).val('');
            $(paramId.scienceName).val('');
            $(paramId.organizeName).val('');
        }

        /**
         * 刷新查询参数
         */
        function refreshSearch() {
            var params = {
                studentName: $(paramId.studentName).val(),
                studentNumber: $(paramId.studentNumber).val(),
                scienceName: $(paramId.scienceName).val(),
                organizeName: $(paramId.organizeName).val(),
                internshipReleaseId:init_page_param.internshipReleaseId
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

        $(paramId.studentName).keyup(function (event) {
            if (event.keyCode == 13) {
                refreshSearch();
                init();
            }
        });

        $(paramId.studentNumber).keyup(function (event) {
            if (event.keyCode == 13) {
                refreshSearch();
                init();
            }
        });

        $(paramId.scienceName).change(function (event) {
            if (event.keyCode == 13) {
                refreshSearch();
                init();
            }
        });

        $(paramId.organizeName).change(function (event) {
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
            var source = $("#internship-audit-template").html();
            var template = Handlebars.compile(source);
            Handlebars.registerHelper('real_name', function () {
                var value = Handlebars.escapeExpression(this.realName);
                return new Handlebars.SafeString(value);
            });
            Handlebars.registerHelper('science_name', function () {
                var value = Handlebars.escapeExpression(this.scienceName);
                return new Handlebars.SafeString(value);
            });
            Handlebars.registerHelper('organize_name', function () {
                var value = Handlebars.escapeExpression(this.organizeName);
                return new Handlebars.SafeString(value);
            });
            Handlebars.registerHelper('internship_apply_state', function () {
                var value = Handlebars.escapeExpression(internshipApplyStateCode(this.internshipApplyState));
                return new Handlebars.SafeString(value);
            });
            Handlebars.registerHelper('reason', function () {
                var value = Handlebars.escapeExpression(this.reason);
                return new Handlebars.SafeString(value);
            });
            var html = template(data);
            $(tableData).html(html);
        }

        /**
         * 状态码表
         * @param state 状态码
         * @returns {string}
         */
        function internshipApplyStateCode(state) {
            var msg = '';
            switch (state) {
                case 0:
                    msg = '未提交';
                    break;
                case 1:
                    msg = '审核中...';
                    break;
                case 2:
                    msg = '已通过';
                    break;
                case 3:
                    msg = '未通过';
                    break;
                case 4:
                    msg = '基本信息变更审核中...';
                    break;
                case 5:
                    msg = '基本信息变更填写中...';
                    break;
                case 6:
                    msg = '单位信息变更申请中...';
                    break;
                case 7:
                    msg = '单位信息变更填写中...';
                    break;
            }
            return msg;
        }

        init();

        /**
         * 初始化数据
         */
        function init() {
            startLoading();
            $.post(web_path + ajax_url.audit_data_url, param, function (data) {
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
            $.get(web_path + ajax_url.audit_data_url, param, function (data) {
                endLoading();
                listData(data);
            });
        }

    });