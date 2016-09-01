package dmir.test.webmagic;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

/**
 * Created by Richard on 2016-08-15.
 */
public class GithubRepoPageProcessor implements PageProcessor {

    private Site site = Site.me().setRetryTimes(3).setSleepTime(100);

    @Override
    public void process(Page page) {
       // System.out.println(page);
        page.addTargetRequests(page.getHtml().links().regex("(https://github\\.com/\\w+/\\w+)").all());
        page.putField("author", page.getUrl().regex("https://github\\.com/(\\w+)/.*").toString());
        page.putField("name", page.getHtml().xpath("//h1[@class='entry-title public']/strong/a/text()").toString());
        if (page.getResultItems().get("name")==null){
            //skip this page
            page.setSkip(true);
        }
        page.putField("readme", page.getHtml().xpath("//div[@id='readme']/tidyText()"));
        System.out.println("wocaaaaaaaaaaaaaaaaaaaaaaaaaa");
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
        //PropertyConfigurator.configure("log4j.properties");//加载.properties文件
       Spider s = Spider.create(new GithubRepoPageProcessor());
        s.addUrl("https://github.com/code4craft").thread(5).run();

        System.out.println(s.getStatus()+"222222222222222222222222222222");
    }
}