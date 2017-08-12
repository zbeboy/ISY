package top.zbeboy.isy.service.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zbeboy on 2017/3/7.
 */
@Slf4j
public class HttpClientUtils {

    /**
     * 发送get请求
     *
     * @param params 参数
     * @param url    请求路径
     * @return 响应
     * @throws IOException 异常
     */
    public static CloseableHttpResponse sendGetRequest(List<NameValuePair> params, String url) throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        String str = EntityUtils.toString(new UrlEncodedFormEntity(params, Consts.UTF_8));
        HttpGet httpGet = new HttpGet(url + "?" + str);
        return httpclient.execute(httpGet);
    }

    /**
     * 发送get请求
     *
     * @param params 参数
     * @param url    请求路径
     * @return 响应
     * @throws IOException 异常
     */
    public static CloseableHttpResponse sendGetRequest(String params, String url) throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url + "?" + params);
        return httpclient.execute(httpGet);
    }

    /**
     * 发送post请求
     *
     * @param params 参数
     * @param url    请求路径
     * @return 响应
     * @throws IOException 异常
     */
    public static CloseableHttpResponse sendPostRequest(List<NameValuePair> params, String url) throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(new UrlEncodedFormEntity(params, Consts.UTF_8));
        return httpclient.execute(httpPost);
    }

    /**
     * 输出请求结果
     *
     * @param response 响应
     * @throws IOException 异常
     */
    public static void outputResult(CloseableHttpResponse response) throws IOException {
        try {
            System.out.println(response.getStatusLine());
            HttpEntity entity2 = response.getEntity();
            // do something useful with the response body
            // and ensure it is fully consumed
            System.out.println(EntityUtils.toString(entity2));
            EntityUtils.consume(entity2);

        } catch (IOException e) {
            log.error("HttpClient output error : {}", e);
        } finally {
            response.close();
        }
    }
}
