package com.paipianwang.pat.common.web.file.utils;

import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.csource.fastdfs.TrackerServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 连接池定时器设置
 * @author Jack
 *
 */
public class HeartBeat {

	private static final Logger logger = LoggerFactory.getLogger(HeartBeat.class);
	
	// FastDFS 连接池
	private ConnectionPool pool = null;
	// 小时毫秒数
	public static int SCHEDULE_TIME = 1000 * 60 * 60 * 1;
	// 等待时间
	public static int waitTimes = 200;
	
	public HeartBeat(ConnectionPool pool) {
		this.pool = pool;
	}
	
	/**
	 * 心跳检测
	 * 执行定时任务，检测当前的空闲连接是否可用，如果不可用将从连接池中移除
	 */
	public void beat() {
		logger.info("[心跳任务方法(beat)]");
		TimerTask task = new TimerTask() {
			
			@Override
			public void run() {
				final String logId = UUID.randomUUID().toString();
				logger.info("[心跳任务方法（beat）]["  
                        + logId  
                        + "][Description:对idleConnectionPool中的TrackerServer进行监测]");
				
				final LinkedBlockingQueue<TrackerServer> idleConnectionPool = pool.getIdleConnectionPool();
				TrackerServer trackerServer = null;
				for (int i = 0;i < idleConnectionPool.size(); i ++) {
					try {
						trackerServer = idleConnectionPool.poll(waitTimes, TimeUnit.SECONDS);
						if(trackerServer != null) {
							org.csource.fastdfs.ProtoCommon.activeTest(trackerServer  
                                    .getSocket());
							idleConnectionPool.add(trackerServer);
						} else {
							// 已经没有空闲的长连接了
							break;
						}
					} catch (Exception e) {
						// 发生异常，要删除该连接，在重建
						logger.error("[心跳任务方法（beat）][" + logId  
                                + "][异常：当前连接已不可用将进行重新获取连接]",e);
						pool.drop(trackerServer, logId);
					}
					
				}
			}
		};
		
		Timer timer = new Timer();
		timer.schedule(task, SCHEDULE_TIME, SCHEDULE_TIME);
	}
	
}
