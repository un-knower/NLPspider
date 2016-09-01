package dmir.nlp.spider;

import dmir.nlp.tools.ReadConfig;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

import java.io.IOException;

/**
 * Created by Richard on 2016-08-17.
 * 负责爬取comment的summary并存到mongodb，将summary中的comment数目存入redis
 */
public class CommentSummaryProcessor implements PageProcessor {
    private ReadConfig config = new ReadConfig("crawl.properties", true);
    private Site site = Site
            .me()
            .setRetryTimes(Integer.valueOf(config.getValue("site_RetryTimes")))
            .setSleepTime(Integer.valueOf(config.getValue("site_SleepTime")));
    private Object query;

    @Override
    public void process(Page page) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            //抽取summary
            JsonNode node = mapper.readTree(page.getRawText());
            JsonNode summary =node.get("productCommentSummary");
            System.out.println(summary);
            page.putField("summary",summary);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {

    }

}
