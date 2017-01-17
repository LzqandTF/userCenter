package com.yijiawang.web.platform.userCenter.util;

import net.sf.json.JSONObject;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;

/**
 * 发送HTTP请求工具类
 * Created by yxd on 2016/9/7.
 */
public class HttpRequestUtils {

    private final static Logger log = Logger.getLogger(HttpRequestUtils.class);

    /**
     * 发送POST请求
     *
     * @param url   路径
     * @param param 参数
     * @return
     */
    public static JSONObject sendPost(String url, String param) {
        log.info("请求地址:" + url + "  -- 参数：" + param);
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            // 设置URL
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！" + e);
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return JSONObject.fromObject(result);
    }


}
