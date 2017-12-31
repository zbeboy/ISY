package top.zbeboy.isy.service.common

/**
 * Created by zbeboy 2017-12-31 .
 **/
interface DesService {

    /**
     * 使用 默认key 加密
     *
     * @return String
     */
    fun encrypt(data: String): String

    /**
     * 使用 默认key 解密
     *
     * @return String
     */
    fun decrypt(data: String?): String?

    /**
     * Description 根据键值进行加密
     *
     * @param data
     * @param key
     */
    fun encrypt(data: String, key: String): String

    /**
     * Description 根据键值进行解密
     *
     * @param data
     * @param key
     */
    fun decrypt(data: String?, key: String): String?
}