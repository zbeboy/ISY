/**
 * Created by lenovo on 2016-12-12.
 */
//# sourceURL=internship_change_history.js
require(["jquery", "handlebars", "jquery.address", "css!" + web_path + "/css/custom/timeline.css"],
    function ($, Handlebars) {

        /*
         ajax url.
         */
        var ajax_url = {
            data_url: '/web/internship/statistical/data',
            back: '/web/internship/statistical/submitted'
        };

        /*
         返回
         */
        $('#page_back').click(function () {
            $.address.value(ajax_url.submitted_url + "?id=" + init_page_param.internshipReleaseId);
        });

        function startLoading() {
            // 显示遮罩
            $('#page-wrapper').showLoading();
        }

        function endLoading() {
            // 去除遮罩
            $('#page-wrapper').hideLoading();
        }

        /**
         * 列表数据
         * @param data 数据
         */
        function listData(data) {
            var source = $("#timeline-template").html();
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
            $('#timeData').html(html);
        }


        //init();

        /**
         * 初始化数据
         */
        function init() {
            startLoading();
            $.get(web_path + ajax_url.data_url, init_page_param, function (data) {
                endLoading();
                listData(data);
            });
        }

    });