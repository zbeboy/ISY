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
            var template = Handlebars.compile($("#timeline-template").html());
            var count = 0;

            Handlebars.registerHelper('timeline_state_css', function () {
                return new Handlebars.SafeString(Handlebars.escapeExpression('info'));
            });

            Handlebars.registerHelper('icon', function () {
                return new Handlebars.SafeString(Handlebars.escapeExpression('fa fa-pencil'));
            });

            Handlebars.registerHelper('internship_title', function () {
                return new Handlebars.SafeString(Handlebars.escapeExpression(this.internshipTitle));
            });

            Handlebars.registerHelper('time', function () {
                return new Handlebars.SafeString(Handlebars.escapeExpression(this.changeTimeStr));
            });

            Handlebars.registerHelper('organize_name', function () {
                return new Handlebars.SafeString(Handlebars.escapeExpression(this.organizeName));
            });

            Handlebars.registerHelper('real_name', function () {
                return new Handlebars.SafeString(Handlebars.escapeExpression(this.realName));
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
                return new Handlebars.SafeString(Handlebars.escapeExpression(this.companyName));
            });

            Handlebars.registerHelper('company_address', function () {
                return new Handlebars.SafeString(Handlebars.escapeExpression(this.companyAddress));
            });

            Handlebars.registerHelper('company_contacts', function () {
                return new Handlebars.SafeString(Handlebars.escapeExpression(this.companyContacts));
            });

            Handlebars.registerHelper('company_tel', function () {
                return new Handlebars.SafeString(Handlebars.escapeExpression(this.companyTel));
            });
            $('#timeData').html(template(data));
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