/**
 * 工具类lodash js 扩展功能.
 */
define(["lodash"], function (D) {
    return {
        /**
         * undefine 默认值
         * @param param
         * @param defaultValue
         * @returns {string}
         */
        defaultUndefinedValue: function (param, defaultValue) {
            return D.isUndefined(param) ? defaultValue : param;
        }
    };
});