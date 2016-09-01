package dmir.nlp.spider;

import dmir.nlp.tools.ReadConfig;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Richard on 2016-08-18.
 * 爬评论
 */
public class CommentsProcessor implements PageProcessor{
    private ReadConfig config = new ReadConfig("crawl.properties", true);
    private int count;
    private int countOfComment;//评论总数
    private int number;//前台需求的评论数
    private Object itemID;
    private Site site = Site
            .me()
            .setRetryTimes(Integer.valueOf(config.getValue("site_RetryTimes")))
            .setSleepTime(Integer.valueOf(config.getValue("site_SleepTime")));

    public  CommentsProcessor(int countOfComment,int number){
        this.countOfComment = countOfComment;
        this.number = number;
    }
    @Override
    public void process(Page page) {
        int check = number<countOfComment ? number:countOfComment;//取小值
        if(count <=(check/10)) {
            count++;
            String url = "http://club.jd.com/productpage/p-" + itemID + "-s-0-t-3-p-" + count + ".html";
            System.out.println("url="+url);
            ArrayList<String> arr = new ArrayList<String>();
            arr.add(url);
            page.addTargetRequests(arr);
            try {
                //抽取summary
                ObjectMapper mapper = new ObjectMapper();
                JsonNode node = mapper.readTree(page.getRawText());
                JsonNode comments =node.get("comments");
                page.putField("comments", comments);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            System.err.println("count=" + count);
        }
        //System.exit(0);
    }

    @Override
    public Site getSite() {
        return site;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public void setItemID(Object itemID) {
        this.itemID = itemID;
    }

    public Object getItemID() {
        return itemID;
    }

    public int getCountOfComment() {
        return countOfComment;
    }

    public void setCountOfComment(int countOfComment) {
        this.countOfComment = countOfComment;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
