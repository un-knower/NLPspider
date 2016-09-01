package dmir.nlp.spider;

import dmir.nlp.tools.CommonUtil;
import dmir.nlp.tools.MongoManager;
import dmir.nlp.tools.ReadConfig;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Richard on 2016-08-18.
 */
public class CommentsPipeline implements Pipeline {
    private String itemID = null;
    public static Logger logger = Logger.getLogger(CommentsPipeline.class);
    private static ReadConfig config = new ReadConfig("crawl.properties", true);


    public CommentsPipeline(String itemID) {
        this.itemID = itemID;
    }

    @Override
    public void process(ResultItems resultItems, Task task) {
        Iterator i$ = resultItems.getAll().entrySet().iterator();

        while(i$.hasNext()) {
            Map.Entry entry = (Map.Entry) i$.next();
            try {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode node = mapper.readTree(entry.getValue().toString());
                Iterator itr = node.iterator();
                CommonUtil commonUtil = new CommonUtil();
                ArrayList<Document> list = new ArrayList<Document>();
                while(itr.hasNext()){
                    Document document = commonUtil.jsonNodeToBson((JsonNode) itr.next());
                    list.add(document);
                }
                MongoManager manager = new MongoManager();
                manager.insertMany(list,config.getValue("dbName"),itemID);
                logger.info("insert comments success!");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
