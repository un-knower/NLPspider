package dmir.nlp.tools;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class MyJedisPool {
	private static JedisPool pool;
	private static String ip;// redisIP
	private static int port; // reids端口
	private static int timeOut; // reids端口
	private static ReadConfig rc = new ReadConfig("crawl.properties", true);

	static {
		ip = rc.getValue("redisIP");
		port = Integer.valueOf(rc.getValue("redisPort"));
		timeOut = Integer.valueOf(rc.getValue("redisTimeOut"));

		JedisPoolConfig config = new JedisPoolConfig();

		// 控制一个pool可分配多少个jedis实例，通过pool.getResource()来获取；
		// 如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。
		config.setMaxActive(Integer.valueOf(rc.getValue("JedisPoolMaxActive")));

		// 控制一个pool最多有多少个状态为idle(空闲的)的jedis实例。
		config.setMaxIdle(Integer.valueOf(rc.getValue("JedisPoolMaxIdle")));
		// 表示当borrow(引入)一个jedis实例时，最大的等待时间，如果超过等待时间，则直接抛出JedisConnectionException；
		config.setMaxWait(Long.valueOf(rc.getValue("JedisPoolMaxWait")));
		// 在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
		config.setTestOnBorrow(Boolean.valueOf(rc.getValue("JedisPoolTestOnBorrow")));
		config.setTestOnReturn(Boolean.valueOf(rc.getValue("JedisPoolTestOnReturn")));
		pool = new JedisPool(config, ip, port, timeOut);// config,ip,port,(timeOut)
	}

	public MyJedisPool() {

	}

	public static JedisPool getPool() {
		return pool;
	}

	public static Jedis getJedis() {
		return pool.getResource();
	}

	public static void returnResource(JedisPool pool, Jedis jedis) {
		if (jedis != null) {
			pool.returnResource(jedis);
		}
	}

	public static void main(String[] args) {

		MyJedisPool pool = new MyJedisPool();
		Jedis jedis = pool.getJedis();
		jedis.flushAll();
		// jedis.lpush("commentCandidateList", "1722022490&3817312216405633");
		// jedis.lpush("commentCandidateList", "1722022490&3817066865373930");
		// jedis.lpush("userWeiboAndInfoCandidateList", "1772558133");
		//
		// Date date=new Date();
		// date.setMonth(0);
		// System.out.println(date);
		// long score=date.getTime();
		// jedis.zadd("crawledUserWeiboAndInfo",score,"1772558133");

		// jedis.sadd("set","1");
		// jedis.sadd("set","1");
		// jedis.sadd("set","2");
		// jedis.sadd("set","3");
		// jedis.sadd("set","4");
		// jedis.sadd("set","5");

		//
		// System.out.println("set:"+jedis.smembers("set"));
		// System.out.println(jedis.spop("set"));
		// System.out.println("set:"+jedis.smembers("set"));
		// jedis.zadd("zset", 10, "hello");
		// jedis.zadd("zset", 15, "hello");
		// System.out.println(jedis.zrem("zset", "hello"));
		// System.out.println(jedis.zrank("zset","hello"));
		// System.out.println("查看zset集合中hello score:"+jedis.zscore("zset",
		// "hello"));
		// System.out.println("查看zset集合中的所有元素:"+jedis.zrange("zset", 0, -1));
		System.out.println("查看crawledWeibo  集合中的所有元素:" + jedis.zrange("crawledWeibos", 0, -1));
		// System.out.println("查看crawledRepost
		// 集合中的所有元素:"+jedis.zrange("crawledRepost",0,-1));
		// System.out.println("查看crawledComment
		// 集合中的所有元素:"+jedis.zrange("crawledComment",0,-1));
		// System.out.println("查看crawledUserWeiboAndInfo集合中的所有元素:"+jedis.zrange("crawledUserWeiboAndInfo",0,-1));
		System.out.println("查看crawledFollower集合中的所有元素:" + jedis.zrange("crawledFollower", 0, -1));
		System.out.println("查看crawledFriend  集合中的所有元素:" + jedis.zrange("crawledFriend", 0, -1));
		System.out
				.println("查看userWeiboAndInfoCandidateList所有元素：" + jedis.lrange("userWeiboAndInfoCandidateList", 0, -1));
		// System.out.println("查看userWeiboCandidateList所有元素："+jedis.lrange("userInfoCandidateList",
		// 0, -1));
		System.out.println("查看userRelationCandidateList所有元素：" + jedis.lrange("userRelationCandidateList", 0, -1));
		System.out.println("查看commentCandidateList所有元素：" + jedis.lrange("commentCandidateList", 0, -1));
		// System.out.println("查看repostCandidateList
		// 所有元素："+jedis.lrange("repostCandidateList", 0, -1));

	}

}
