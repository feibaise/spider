package com.feibai.spider;

import lombok.Getter;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.pipeline.JsonFilePipeline;
import us.codecraft.webmagic.processor.PageProcessor;

public class GithubRepoPageProcessor implements PageProcessor {

    // 部分一：抓取网站的相关配置，包括编码、抓取间隔、重试次数等
    @Getter
    private Site site = Site.me().setRetryTimes(3).setSleepTime(1000);

    public static void main(String[] args) {

        Spider.create(new GithubRepoPageProcessor())
                //从"https://github.com/code4craft"开始抓
                .addUrl("http://pe.sph900.com/CakeNoQuery/index")
                .addPipeline(new JsonFilePipeline("/Users/ws/Downloads/uploads"))
                .addPipeline(new ConsolePipeline())
                //开启5个线程抓取
                .thread(5)
                //启动爬虫
                .run();
    }

//    @Override
//    public Site getSite() {
//        return site;
//    }

    @Override
    // process是定制爬虫逻辑的核心接口，在这里编写抽取逻辑
    public void process(Page page) {
        // 部分二：定义如何抽取页面信息，并保存下来
        page.putField("author", page.getUrl().regex("http://pe.sph900\\.com/(\\w+)/.*").toString());
        page.putField("name", page.getHtml().xpath("//table[@class='tab1']/tbody/tr/td/span/text()").toString());

        if (page.getResultItems().get("name") == null) {
            //skip this page
            page.setSkip(true);
        }
//        page.putField("readme", page.getHtml().xpath("//div[@id='readme']/tidyText()"));

        // 部分三：从页面发现后续的url地址来抓取
        page.addTargetRequests(page.getHtml().links().regex("(http://pe.sph900\\.com/[\\w\\-]+/[\\w\\-]+)").all());
    }
}