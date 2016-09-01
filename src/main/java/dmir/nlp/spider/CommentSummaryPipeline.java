package dmir.nlp.spider;

import com.mongodb.BasicDBObject;
import dmir.nlp.tools.CommonUtil;
import dmir.nlp.tools.MongoManager;
import dmir.nlp.tools.ReadConfig;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Richard on 2016-08-17.
 * comment_summary处理，入库
 */
public class CommentSummaryPipeline implements Pipeline {
    public static Logger logger = Logger.getLogger(CommentSummaryPipeline.class);
    private static ReadConfig config = new ReadConfig("crawl.properties", true);
    private String collectionName = null;

    static {
        Bson bson = new BasicDBObject(config.getValue("summary_index"),1);//创建唯一索引
        MongoManager.createIndex(config.getValue("dbName"), config.getValue("summary_collectionName"),bson);
    }

    public CommentSummaryPipeline(String collectionName){
        this.collectionName = collectionName;
    }

    @Override
    public void process(ResultItems resultItems, Task task) {
        Iterator i$ = resultItems.getAll().entrySet().iterator();
        while(i$.hasNext()) {
            Map.Entry entry = (Map.Entry)i$.next();
            try {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode node = mapper.readTree(entry.getValue().toString());
                CommonUtil commonUtil = new CommonUtil();
                Document document = commonUtil.jsonNodeToBson(node);
                MongoManager manager = new MongoManager();
                manager.insertOne(document, config.getValue("dbName"), config.getValue("summary_collectionName"));
                logger.info("insert summary success!"+document.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.out.println(this.collectionName);
        //System.out.println(this.queryNum);
    }
}
