package xyz.xingfeng.www.pixiv;

import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 设置浏览器消息和cookie
 */
public class Main {
    public static void main(String[] args) {
        RankingUrl(2,1,0);
        /*
        mob榜单选项
        日榜：1 周榜:2 月榜:3 新人榜:4 男性榜:5 女性榜:6 原创榜:7
         */
        /*
        isR18是否开启R18
        否:0 是:1
         */
        /*
        date日期选择
        0为当日榜单，20220407则为2022年4月7日的榜单
         */
    }

    /**
     * 定向榜单，是否r18，榜单日期
     */
    static void RankingUrl(int mob, int isR18, int date){
        String url = new Config().getPixiv_Login();
        switch (mob){
            case 1:
                url += "?mode=daily";
                break;
            case 2:
                url += "?mode=weekly";
                break;
            case 3:
                url += "?mode=monthly";
                break;
            case 4:
                url += "?mode=rookie";
                break;
            case 5:
                url += "?mode=male";
                break;
            case 6:
                url += "?mode=female";
                break;
            case 7:
                url += "?mode=original";
                break;
            default:
                System.out.println("你输入的数据非法");
                return;
        }
        if(isR18 == 0){

        }else if (isR18 ==1){
            url += "_r18";
        }else {
            System.out.println("你输入的数据非法");
            return;
        }
        if (date == 0){

        }else {
            url +="&date="+String.valueOf(date);
        }
        System.out.println("构建的url为："+url);
        try {
            PixivDownloadUrl(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取榜单内图片链接链接,分析下载链接
     * @param url
     * @return
     * @throws Exception
     */
    static String PixivDownloadUrl(String url)throws Exception{
        //存放下载地址
        String DoowloadUrl = "";
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        //在httpGet中添加头文件
        httpGet.setHeader("User-Agent",new Config().getUrse_agent());
        httpGet.setHeader("cookie",new Config().getCookie());
        httpGet.setHeader("accept-language","zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6");

        //发起请求
        CloseableHttpResponse response = httpClient.execute(httpGet);
        //解析响应
        if(response.getStatusLine().getStatusCode() == 200){
            String s = EntityUtils.toString(response.getEntity(),"utf8");
            Document doc = Jsoup.parse(s);
            //获取筛选Class：sc-1qpw8k9-3 eFhoug gtm-expand-full-size-illust
            Elements elements = doc.select("._thumbnail").select(".ui-scroll-view").select("img");
            String str = null;
            //遍历循环
            for (Element element : elements){
                //合成下载链接
                str = element.attr("data-src");
                String url1 = "https://i.pximg.net/img-original/img/" + str.replace("https://i.pximg.net/c/240x480/img-master/img/","").replace("_master1200","");
                if (DownloadToFile(url1)){
                    String fileName = url1.substring(url1.lastIndexOf("/")+1);
                    fileName = fileName.replace("_p0","");
                    System.out.println("========"+fileName+"========");
                    //第一个下载完成后将p0改成p1继续下载，返回值为false时结束下载
                    int num = 0;
                    while (true){
                        num++;
                        url1 = url1.replace("p"+String.valueOf(num - 1),"p"+String.valueOf(num));
                        if (DownloadToFile(url1)){
                        }else {
                            System.out.println("该链接中有"+num+"张图片");
                            break;
                        }
                    }
                }else {
                    String url2 = url1.replace(".jpg",".png");
                    DownloadToFile(url2);
                    //第一个下载完成后将p0改成p1继续下载，返回值为false时结束下载
                    int num = 0;
                    while (true){
                        num++;
                        url2 = url2.replace("p"+String.valueOf(num -1),"p"+String.valueOf(num));
                        if (DownloadToFile(url2)){
                        }else {
                            System.out.println("该链接中有"+num+"张图片");
                            break;
                        }
                    }
                }
            }
        }else {
            System.out.println("异常"+response.getStatusLine().getStatusCode());
        }
        return DoowloadUrl;
    }

    /**
     * 用最笨的方法查看图片是jpg还是png
     * @param url
     * @return
     * @throws Exception
     */
    static boolean DownloadToFile(String url) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("User-Agent", new Config().getUrse_agent());
        try {
            httpGet.setHeader("cookie", new Config().getCookie());
            httpGet.setHeader("accept-language", "zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6");
            httpGet.setHeader("referer", "https://www.pixiv.net/");
            //设置超时时间
            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectTimeout(20000).setConnectionRequestTimeout(20000)
                    .setSocketTimeout(20000).build();
            httpGet.setConfig(requestConfig);
            CloseableHttpResponse response = null;
            int num = 0;
            while (true){
                num++;
                if (num >= 5){
                    System.out.println("重试次数过多，放弃下载");
                    return false;
                }
                try {
                    response = httpClient.execute(httpGet);
                    if (response.getStatusLine().getStatusCode() == 200) {
                        String fileName = url.substring(url.lastIndexOf("/")+1);
                        System.out.println("开始下载"+fileName);
                        HttpEntity entity;
                        entity = response.getEntity();
                        byte[] data = EntityUtils.toByteArray(entity);
                        FileOutputStream fos = new FileOutputStream(new Config().getFileload() + "/" + fileName);
                        fos.write(data);
                        fos.close();
                        System.out.println(fileName+"下载完成");
                        break;
                    } else {
                        return false;
                    }
                } catch (IOException e) {
                    System.out.println("异常，即将重试");

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }
}
