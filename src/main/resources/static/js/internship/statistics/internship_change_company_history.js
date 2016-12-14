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
            data_url: '/web/internship/statistical/record/company/data',
            back: '/web/internship/statistical/submitted'
        };

        /*
         返回
         */
        $('#page_back').click(function () {
            $.address.value(ajax_url.back + "?id=" + init_page_param.internshipReleaseId);
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
            var count = 0;

            Handlebars.registerHelper('timeline_state_css', function () {
                var value = Handlebars.escapeExpression('info');
                return new Handlebars.SafeString(value);
            });

            Handlebars.registerHelper('icon', function () {
                var value = Handlebars.escapeExpression('fa fa-pencil');
                return new Handlebars.SafeString(value);
            });

            Handlebars.registerHelper('internship_title', function () {
                var value = Handlebars.escapeExpression(this.internshipTitle);
                return new Handlebars.SafeString(value);
            });

            Handlebars.registerHelper('time', function () {
                var value = Handlebars.escapeExpression(this.changeTimeStr);
                return new Handlebars.SafeString(value);
            });

            Handlebars.registerHelper('organize_name', function () {
                var value = Handlebars.escapeExpression(this.organizeName);
                return new Handlebars.SafeString(value);
            });

            Handlebars.registerHelper('real_name', function () {
                var value = Handlebars.escapeExpression(this.realName);
                return new Handlebars.SafeString(value);
            });

            Handlebars.registerHelper('inverted', function () {
                var value = '';
                if (count % 2 == 0) {
                    value = Handlebars.escapeExpression('timeline-inverted');
                } else {
                    value = Handlebars.escapeExpression('');
                }
                count++;
                return new Handlebars.SafeString(value);
            });

            Handlebars.registerHelper('company_name', function () {
                var value = Handlebars.escapeExpression(this.companyName);
                return new Handlebars.SafeString(value);
            });

            Handlebars.registerHelper('company_address', function () {
                var value = Handlebars.escapeExpression(this.companyAddress);
                return new Handlebars.SafeString(value);
            });

            Handlebars.registerHelper('company_contacts', function () {
                var value = Handlebars.escapeExpression(this.companyContacts);
                return new Handlebars.SafeString(value);
            });

            Handlebars.registerHelper('company_tel', function () {
                var value = Handlebars.escapeExpression(this.companyTel);
                return new Handlebars.SafeString(value);
            });

            var html = template(data);
            $('#timeData').html(html);
        }

        init();

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