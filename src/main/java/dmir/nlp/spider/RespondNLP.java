package dmir.nlp.spider;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import dmir.nlp.tools.MongoManager;
import dmir.nlp.tools.MyJedisPool;
import dmir.nlp.tools.ParseUtil;
import dmir.nlp.tools.ReadConfig;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.bson.conversions.Bson;
import redis.clients.jedis.JedisPool;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.scheduler.RedisScheduler;

/**
 * Created by Richard on 2016-08-17.
 */
public final class  RespondNLP {
    private ReadConfig config = new ReadConfig("crawl.properties", true);
    private static JedisPool redisPool = MyJedisPool.getPool();
    public static Logger logger = Logger.getLogger(RespondNLP.class);
    private RespondNLP(){}

    /**
     * 响应前台用户的请求，负责解析链接，并调用爬虫。
     * @param query
     *              用户给定的链接
     * @param number
     *              前台给定的最大评论条数
     * @return
     */
    public final String Response(Object query , int number) {

        ParseUtil parse = new ParseUtil();
        String itemID = parse.matchStringGroup1((String) query, "item.jd.com/(\\d+).");

        if (itemID != null){
            String startUrl = "http://club.jd.com/productpage/p-"+itemID+"-s-0-t-3-p-1.html";
            startCrawl(number,startUrl,itemID);
            return itemID;
        }
        return null;
    }

    /**
     *爬虫，1、爬取summary，存入mongodb. 2、爬取comments,存入mongodb（新建collection）
     * @param number
     * @param startUrl
     * @param itemID
     */
    private void startCrawl(int number,String startUrl,String itemID) {

        //爬取summary
        CommentSummaryProcessor summaryProcessor =new CommentSummaryProcessor();
        Spider summarySpider = Spider.create(summaryProcessor);
        summarySpider
               .addUrl(startUrl).addPipeline(new CommentSummaryPipeline(itemID))
               .thread(Integer.valueOf(config.getValue("max_thread")))
               .run();

        //爬取评论（基于summary提供的信息）
        MongoManager manager = new MongoManager();//建collection
        boolean flag = manager.createCollection(itemID, config.getValue("dbName"));
        if(flag) {
            Bson bson = new BasicDBObject("id", 1);//创建唯一索引
            MongoManager.createIndex(config.getValue("dbName"), itemID, bson);
            //找到summary中评论条数
            Bson findCommentCount = new BasicDBObject("productId", itemID);
            FindIterable<Document> doc = manager.getCollection(config.getValue("dbName"), config.getValue("summary_collectionName"))
                    .find(findCommentCount);
            Object value = doc.first().get("commentCount");
            int countOfComment =Integer.parseInt(String.valueOf(value));
            CommentsProcessor commentsProcessor = new CommentsProcessor(countOfComment, number);
            commentsProcessor.setCount(1);
            commentsProcessor.setItemID(itemID);
            Spider commentsSpider = Spider.create(commentsProcessor);
            commentsSpider
                    .setScheduler(new RedisScheduler(redisPool))
                    .addUrl(startUrl).addPipeline(new CommentsPipeline(itemID))
                    .thread(Integer.valueOf(config.getValue("max_thread")))
                    .run();
        }
    }

    public static void main(String[] args) {
        String query ="http://item.jd.com/3243688.html";
        int num = 50;
        RespondNLP r =new RespondNLP();
        String s =r.Response(query,num);
    }
}
