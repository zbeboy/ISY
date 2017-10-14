/**
 * 工具类lodash js 扩展功能.
 */
define(["lodash"], function (D) {
    return {
        /**
         * undefine 默认值
         * @param param
         * @returns {string}
         */
        defaultUndefinedValue: function (param) {
            return D.isUndefined(param) ? '' : param;
        }
    };
});