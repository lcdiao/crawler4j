package cn.lcdiao.demo;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

public class MyController {
    public static void main(String[] args) throws Exception {
        //定义爬虫数据存储位置
        String crawlStorageFolder = "data/crawl";
        //定义5个爬虫，也就是5个线程
        int numberOfCrawlers = 5;

        //定义爬虫配置
        CrawlConfig crawlConfig = new CrawlConfig();
        //设置爬虫文件存储位置
        crawlConfig.setCrawlStorageFolder(crawlStorageFolder);

        /*
        实例化爬虫控制器
         */
        //实例化页面获取器
        PageFetcher pageFetcher = new PageFetcher(crawlConfig);
        //实例化爬虫机器人配置，比如可以设置UserAgentName(伪装成从浏览器发起的请求)
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        //robotstxtConfig.setUserAgentName();
        //实例化爬虫机器人对目标服务器的配置，每个网站都有一个robots.txt文件，规定了该网站哪些页面可以爬，哪些页面禁止爬
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig,pageFetcher);
        //实例化爬虫控制器
        CrawlController controller = new CrawlController(crawlConfig,pageFetcher,robotstxtServer);

        /*
        配置爬虫种子页面，就是规定的从哪里开始爬，可以配置多个种子页面
         */
        controller.addSeed("https://ibaotu.com/peiyue/");

        /*
        启动爬虫
         */
        controller.start(MyCrawler.class,numberOfCrawlers);
    }
}
