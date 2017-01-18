/**
 * Created by lenovo on 2016-12-18.
 */
require(["jquery", "handlebars", "nav_active", "quill.bubble", "jquery.address", "jquery.print"],
    function ($, Handlebars, nav_active, Quill) {

        /*
         ajax url.
         */
        var ajax_url = {
            nav: '/web/menu/internship/journal'
        };

        // 刷新时选中菜单
        nav_active(ajax_url.nav);

        /*
         参数id
         */
        var paramId = {
            internshipJournalContent: '#internshipJournalContent',
            internshipJournalHtml: '#internshipJournalHtml'
        };

        /*
         返回
         */
        $('#page_back').click(function () {
            window.history.go(-1);
        });

        /*
         打印
         */
        $('#print_area').click(function () {
            $('#print_content').print({
                globalStyles: true,
                mediaPrint: false,
                stylesheet: null,
                noPrintSelector: ".no-print",
                iframe: true,
                append: null,
                prepend: "<h2 class='text-center'>实习日(周)志<h2/>",
                manuallyCopyFormValues: true,
                deferred: $.Deferred(),
                timeout: 750,
                title: null,
                doctype: '<!doctype html>'
            });
        });

        // 初始化内容与感想富文本框
        var quill = new Quill(paramId.internshipJournalContent, {
            placeholder: '内容与感想',
            theme: 'bubble'
        });

        init();

        function init() {
            quill.enable(false);
            quill.setContents(JSON.parse($(paramId.internshipJournalHtml).val()));
        }
    });