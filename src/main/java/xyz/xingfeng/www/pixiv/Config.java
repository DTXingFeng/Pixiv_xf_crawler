package xyz.xingfeng.www.pixiv;

import java.io.File;

public class Config {
    //浏览器信息
    static final private String urse_agent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.75 Safari/537.36 Edg/100.0.1185.36";
    //cookie
    static private String cookie;

    //pixiv起点站"referer: https://www.pixiv.net";
    private String pixiv_First = "https://www.pixiv.net";

    public String getPixiv_First() {
        return pixiv_First;
    }

    //pixiv登录页
    private String pixiv_Login = "https://www.pixiv.net/ranking.php";

    //图片下载后的地址
    private String Fileload = "Pixiv/";

    public String getFileload() {
        return Fileload;
    }

    public String getPixiv_Login() {
        return pixiv_Login;
    }


    public void setCookie()throws Exception {

    }

    public String getUrse_agent() {
        return urse_agent;
    }

    /**
     * 从config.txt中加载cookie配置
     * @throws Exception
     */
    public String getCookie()throws Exception {
        File file = new File("config.txt");
        FileDo fileDo = new FileDo(file);
        String s = fileDo.tiqu(2);
        this.cookie = s;
        return cookie;
    }
}
