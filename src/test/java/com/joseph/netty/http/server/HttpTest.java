package com.joseph.netty.http.server;

import com.google.common.base.Strings;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Date;

/**
 * Created by dys09435 on 2017/5/4.
 */
public class HttpTest {

    @Before
    public void init(){

    }

    @Test
    public void testConcurrent() throws IOException {
        String url = "http://localhost:8888/test";
        System.out.println(new Date());
        for(int i = 0; i < 10; i++){
            post(url, "Joseph-" + i);
        }
        System.out.println(new Date());
    }

    private void post(String url, String requestData) throws IOException {
        HttpClient httpClient = HttpClients.createDefault();
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(120000).setConnectTimeout(120000).build();//设置请求和传输超时时间
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(requestConfig);
        if (!Strings.isNullOrEmpty(requestData)) {
            StringEntity myEntity = new StringEntity(requestData);
            httpPost.setEntity(myEntity);
        }
        HttpResponse response = httpClient.execute(httpPost);
        System.out.println(response.getStatusLine().getStatusCode());
    }
}
