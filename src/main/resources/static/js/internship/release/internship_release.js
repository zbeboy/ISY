/**
 * Created by lenovo on 2016-11-10.
 */
//# sourceURL=internship_release.js
require(["jquery", "handlebars", "messenger", "jquery.address", "jquery.simple-pagination"], function ($, Handlebars) {

    /*
     ajax url.
     */
    var ajax_url = {
        internship_release_data_url: '/web/internship/release/data',
        add: '/web/internship/release/add'
    };

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
        $.get(web_path + ajax_url.internship_release_data_url, function (data) {
            listData(data);
        });
    }

    $('#pagination').pagination({
        items: 100,
        itemsOnPage: 10,
        hrefTextPrefix: '',
        prevText: '上一页',
        nextText: '下一页',
        cssStyle: '',
        listStyle: 'pagination',
        onPageClick: function (pageNumber, event) {
            // Callback triggered when a page is clicked
            // Page number is given as an optional parameter
            console.log(pageNumber);
        }
    });

});