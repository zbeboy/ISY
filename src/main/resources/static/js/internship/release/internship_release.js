/**
 * Created by lenovo on 2016-11-10.
 */
//# sourceURL=internship_release.js
require(["jquery", "handlebars", "messenger", "jquery.address", "jquery.simple-pagination", "jquery.showLoading"],
    function ($, Handlebars) {

        /*
         ajax url.
         */
        var ajax_url = {
            internship_release_data_url: '/web/internship/release/data',
            add: '/web/internship/release/add'
        };

        /*
         参数
         */
        var param = {
            searchParams: '',
            pageNum: 0,
            pageSize: 1,
            displayedPages: 3
        };

        /*
         参数id
         */
        var paramId = {
            internshipTitle: '#internshipTitle'
        };

        function startLoading(targetId) {
            // 显示遮罩
            $(targetId).showLoading();
        }

        function endLoading(targetId) {
            // 去除遮罩
            $(targetId).hideLoading();
        }

        /**
         * 刷新查询参数
         */
        function refreshSearch() {
            var params = {
                internshipTitle: $(paramId.internshipTitle).val()
            };
            param.searchParams = JSON.stringify(params);
        }

        /*
         发布
         */
        $('#release').click(function () {
            $.address.value(ajax_url.add);
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

            Handlebars.registerHelper('department_name', function () {
                var value = Handlebars.escapeExpression(this.departmentName);
                return new Handlebars.SafeString(value);
            });

            Handlebars.registerHelper('real_name', function () {
                var value = Handlebars.escapeExpression(this.realName);
                return new Handlebars.SafeString(value);
            });

            var html = template(data);
            $('#tableData').html(html);
        }

        init();

        /**
         * 初始化数据
         */
        function init() {
            startLoading('#tableData');
            $.get(web_path + ajax_url.internship_release_data_url, param, function (data) {
                endLoading('#tableData');
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
            refreshSearch();
            param.pageNum = pageNumber;
            startLoading('#tableData');
            $.get(web_path + ajax_url.internship_release_data_url, param, function (data) {
                endLoading('#tableData');
                listData(data);
            });
        }

    });