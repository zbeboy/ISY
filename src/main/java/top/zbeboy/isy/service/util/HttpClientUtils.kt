package top.zbeboy.isy.service.util

import org.apache.http.Consts
import org.apache.http.NameValuePair
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import org.slf4j.LoggerFactory
import java.io.IOException

/**
 * Created by zbeboy 2017-11-30 .
 **/
class HttpClientUtils {
    companion object {
        private val log = LoggerFactory.getLogger(HttpClientUtils::class.java)
        /**
         * 发送get请求
         *
         * @param params 参数
         * @param url    请求路径
         * @return 响应
         * @throws IOException 异常
         */
        @JvmStatic
        @Throws(IOException::class)
        fun sendGetRequest(params: List<NameValuePair>, url: String): CloseableHttpResponse {
            val httpclient = HttpClients.createDefault()
            val str = EntityUtils.toString(UrlEncodedFormEntity(params, Consts.UTF_8))
            val httpGet = HttpGet(url + "?" + str)
            return httpclient.execute(httpGet)
        }

        /**
         * 发送get请求
         *
         * @param params 参数
         * @param url    请求路径
         * @return 响应
         * @throws IOException 异常
         */
        @JvmStatic
        @Throws(IOException::class)
        fun sendGetRequest(params: String, url: String): CloseableHttpResponse {
            val httpclient = HttpClients.createDefault()
            val httpGet = HttpGet(url + "?" + params)
            return httpclient.execute(httpGet)
        }

        /**
         * 发送post请求
         *
         * @param params 参数
         * @param url    请求路径
         * @return 响应
         * @throws IOException 异常
         */
        @JvmStatic
        @Throws(IOException::class)
        fun sendPostRequest(params: List<NameValuePair>, url: String): CloseableHttpResponse {
            val httpclient = HttpClients.createDefault()
            val httpPost = HttpPost(url)
            httpPost.entity = UrlEncodedFormEntity(params, Consts.UTF_8)
            return httpclient.execute(httpPost)
        }

        /**
         * 输出请求结果
         *
         * @param response 响应
         * @throws IOException 异常
         */
        @JvmStatic
        @Throws(IOException::class)
        fun outputResult(response: CloseableHttpResponse) {
            try {
                println(response.statusLine)
                val entity2 = response.entity
                // do something useful with the response body
                // and ensure it is fully consumed
                println(EntityUtils.toString(entity2))
                EntityUtils.consume(entity2)

            } catch (e: IOException) {
                log.error("HttpClient output error : {}", e)
            } finally {
                response.close()
            }
        }
    }
}