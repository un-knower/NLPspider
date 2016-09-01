package dmir.test.webmagic;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

import java.io.IOException;
import java.util.Iterator;

/**
 * Created by Richard on 2016-08-15.
 */
public class JDspider implements PageProcessor{

    private Site  site = Site.me().setRetryTimes(3).setSleepTime(100);
    @Override
    public void process(Page page) {
        System.out.println(page);
        ObjectMapper mapper = new ObjectMapper();
        System.out.println(page.getRawText());
        try {
            JsonNode node = mapper.readTree(page.getRawText());
            System.out.println(node.size());
            Iterator<JsonNode> itr = node.getElements();
            while(itr.hasNext()) {
                //String ass =itr.next().toString();//.getTextValue();
                System.out.println(itr.next());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

/*        System.out.println("start");
        Json s = page.getJson();
        String ss = page.getRawText();
        System.out.println(page.toString());
        System.out.println("end");*/
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
        Spider.create(new JDspider()).addUrl("http://club.jd.com/productpage/p-1856588-s-0-t-3-p-1.html").thread(5).run();
    }
}
