package dmir.nlp.tools;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;

/**
 * Created by Richard on 2016-08-17.
 */
public class MongoPool {
    protected static MongoClient mongoClient = null;
    private static ReadConfig config = new ReadConfig("crawl.properties", true);
    private static final MongoPool pool = new MongoPool();// 饿汉式单例模式
    static{
        if (mongoClient == null){
            MongoClientOptions.Builder buider = new MongoClientOptions.Builder();
            buider.connectionsPerHost(Integer.parseInt(config.getValue("max_connections")));// 与目标数据库可以建立的最大链接数
            buider.connectTimeout(Integer.parseInt(config.getValue("connection_timeout")));// 与数据库建立链接的超时时间
            buider.maxWaitTime(Integer.parseInt(config.getValue("wait_connection_timeout")));// 一个线程成功获取到一个可用数据库之前的最大等待时间
            buider.threadsAllowedToBlockForConnectionMultiplier(Integer.parseInt(config.getValue("block_thread")));
            buider.maxConnectionIdleTime(0);
            buider.maxConnectionLifeTime(0);
            buider.socketTimeout(0);
            buider.socketKeepAlive(true);
            MongoClientOptions myOptions = buider.build();
            mongoClient = new MongoClient(new ServerAddress(config.getValue("mongo_ip"), Integer.parseInt(config.getValue("mongo_port"))), myOptions);
        }
    }

    public static MongoPool getMongoPool()
    {
        return pool;
    }
}
