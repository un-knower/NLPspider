package dmir.nlp.tools;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.MongoCommandException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;

/**
 * Created by Richard on 2016-08-16.
 */
public class MongoManager{
    public static Logger logger = Logger.getLogger(MongoManager.class);
    private static MongoPool mongoPool =MongoPool.getMongoPool();
    public MongoManager(){
    }

    public MongoDatabase getDb(String dbName) {
        return mongoPool.mongoClient.getDatabase(dbName);
    }

    public MongoCollection<Document> getCollection(String dbName, String collectionName) {
        MongoDatabase db = mongoPool.mongoClient.getDatabase(dbName);
        return db.getCollection(collectionName);
    }

    /**
     *
     * 查找所有符合条件的结果
     * @param dbName
     * @param collectionName
     * @param key
     * @param query
     */
    public ArrayList<Document> findData(String dbName, String collectionName, Object key, Object query,int number){
        MongoDatabase db = mongoPool.mongoClient.getDatabase(dbName);
        MongoCollection<Document> collection = db.getCollection(collectionName);

        DBObject bson = new BasicDBObject();
        bson.put((String)key,query);
        FindIterable<Document> cursor = collection.find((Bson) bson);
        MongoCursor<Document> itr = cursor.iterator();

        ArrayList<Document> list = new ArrayList<Document>();

        if(number == -1){
            while(itr.hasNext()){
                list.add(itr.next());
            }
        }else{
            while(itr.hasNext() && number>0){
                list.add(itr.next());
                number--;
            }
        }

        return list;
    }

    public static void createIndex(String dbName, String collectionName, Bson index){

    }
    /**
     * 插入一条数据
     * @param document
     * @param dbName
     * @param collectionName
     */
    public boolean insertOne(Document document, String dbName, String collectionName) {
        try{
            MongoCollection<Document> collection = getCollection(dbName,collectionName);
            collection.insertOne(document);
            return true;
        }catch (MongoCommandException e){
            return false;
        }
    }

    /**
     * 插入一组数据
     * @param list
     * @param dbName
     * @param collectionName
     * @return
     */
    public boolean insertMany(ArrayList<Document> list, String dbName, String collectionName) {
        try {
            MongoCollection<Document> collection = getCollection(dbName, collectionName);
            collection.insertMany(list);
            return true;
        }catch (MongoCommandException e){
            return false;
        }
    }

    /**
     * 新建collection
     * @param itemID
     * @param dbName
     * @return
     */
    public boolean createCollection(String itemID, String dbName) {
        MongoDatabase db = getDb(dbName);
        try{
            db.createCollection(itemID);
            return true;
        }catch (MongoCommandException e){
            return false;
        }

    }

    public static void main(String[] args) {
        MongoManager manager = new MongoManager();
        manager.findData("JD_spider_data", "JD_spider_comment_summary","crawl_flag",0,3);
    }
}
