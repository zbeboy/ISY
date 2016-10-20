/**
 * Created by lenovo on 2016-08-19.
 */
 requirejs.config({
     // pathsオプションの設定。"module/name": "path"を指定します。拡張子（.js）は指定しません。
     paths: {
         "nav":web_path + "/js/util/nav"
     }
 });
require(["nav"], function (nav) {

});