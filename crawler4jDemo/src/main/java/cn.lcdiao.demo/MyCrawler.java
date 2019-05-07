package cn.lcdiao.demo;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.parser.ParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyCrawler extends WebCrawler {
    //过滤
    private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|gif|jpg|png|mp3|mp4|zip|gz))$");

    /**爬取的页面*/
    @Override
    public void visit(Page page) {
        //h获取页面的地址
        String url = page.getWebURL().getURL();
        System.out.println("获取的url："+url);
        //解析的页面数据
        ParseData parseData = page.getParseData();
        //
        if (parseData instanceof HtmlParseData) {
            HtmlParseData htmlParseData = (HtmlParseData) parseData;
            String html = htmlParseData.getHtml();
//            String text = htmlParseData.getText();
//            System.out.println("获取的html：" + html);
//            System.out.println("获取的text：" + text);
            String cssQuery = "div.bt-body h1.works-name";
            //<h1 class="works-name">正式隆重颁奖典礼商业晚会企业震撼大气开场</h1>  匹配中间的文字
            String reg = ">(.*?)<";
            Document document = Jsoup.parse(html);
            Elements elements = document.select(cssQuery);

            if (elements.size()>0) {
                Element element = elements.get(0);
                System.out.println("==================element:" + element);

                Pattern pattern = Pattern.compile(reg);
                Matcher matcher = pattern.matcher(element.toString());


                String srcCssQuery = "div.bt-body div.audio-src audio>source";
                String srcReg = "src=\"(.*?)\"";
                Elements srcElements = document.select(srcCssQuery);

                if (matcher.find()) {
                    System.out.println("name:" + matcher.group(1));
                    System.out.println("source:" + srcElements.get(0).attr("src"));
                    
                    //下载资源
                    try {
                        downloadSource(srcElements.get(0).attr("src"),matcher.group(1)+".mp3","data/mp3");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }


        }
    }


    /**
     * 判断url是否匹配你的爬取策略
     */
    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        String href = url.getURL().toLowerCase();
        return !FILTERS.matcher(href).matches() && href.startsWith("https://ibaotu.com/sucai/");

    }

    public static void downloadSource(String urlStr,String fileName,String savePath) throws IOException {
        URL url = new URL("http:"+urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        //设置超时时间为3秒
        conn.setConnectTimeout(3*1000);
        //防止屏蔽程序抓取而返回403错误
        conn.setRequestProperty("User-Agent","Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
        //得到输入流
        InputStream inputStream = conn.getInputStream();
        //获取字节数组
        byte[] getData = readInputStream(inputStream);
        //文件保存位置
        File saveDir = new File(savePath);
        if (!saveDir.exists()) {
            saveDir.mkdir();
        }
        File file = new File(saveDir + File.separator + fileName);
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(getData);
        if (fos!=null) {
            fos.close();
        }
        if (inputStream != null) {
            inputStream.close();
        }

        System.out.println("info:" + url + "   download success");
    }



    /**从输入流中获取字节数组*/
    public static byte[] readInputStream(InputStream inputStream) throws IOException{
        byte[] buffer = new byte[1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while ((len = inputStream.read(buffer))!=-1){
            bos.write(buffer,0,len);
        }
        bos.close();
        return bos.toByteArray();
    }
}
