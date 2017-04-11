package com.paipianwang.pat.common.web.file.utils;

import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.omg.CORBA.portable.ApplicationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectionPool {
	
	private final Logger logger = LoggerFactory.getLogger(ConnectionPool.class);

	private static final String CONF_FILENAME = Thread.currentThread().getContextClassLoader().getResource("").getPath() + "fdfs/fdfs_client.conf";
	
	/** 空闲的连接池 */  
    private LinkedBlockingQueue<TrackerServer> idleConnectionPool = null;  
    /** 连接池默认最小连接数 */  
    private long minPoolSize = 10;  
    /** 连接池默认最大连接数 */  
    private long maxPoolSize = 30;  
    /** 当前创建的连接数 */  
    private volatile long nowPoolSize = 0;  
    /** 默认等待时间（单位：秒） */  
    private long waitTimes = 200;  
    /** FastDFS 客户端创建连接默认1次 */  
    private static final int COUNT = 1; 
    
    public ConnectionPool(long minPoolSize, long maxPoolSize, long waitTimes) {  
        String logId = UUID.randomUUID().toString();  
        logger.info("[线程池构造方法(ConnectionPool)][" + logId  
                + "][默认参数：minPoolSize=" + minPoolSize + ",maxPoolSize="  
                + maxPoolSize + ",waitTimes=" + waitTimes + "]");  
        this.minPoolSize = minPoolSize;  
        this.maxPoolSize = maxPoolSize;  
        this.waitTimes = waitTimes;  
        /** 初始化连接池 */  
        poolInit(logId);  
        /** 注册心跳 */  
        HeartBeat beat = new HeartBeat(this);  
        beat.beat();  
    }

	private void poolInit(final String logId) {
		try {
			// 加载配置文件
			initClientGlobal();
			// 初始化空闲连接池
			idleConnectionPool = new LinkedBlockingQueue<TrackerServer>();
			// 往线程池中添加默认大小的线程
			for (int i = 0;i < minPoolSize; i ++) {
				createTrackerServer(logId,COUNT);
			}
		} catch (Exception e) {
			logger.error("[FASTDFS初始化(init)--异常][" + logId + "][异常：{}]",e);
		}
	}

	/**
	 * 创建Tracker，并放入空闲连接池
	 */
	public void createTrackerServer(final String logId, int flag) {
		
		logger.info("创建TrackerServer(createTrackerServer)["+ logId +"]");
		TrackerServer trackerServer = null;
		
		try {
			TrackerClient trackerClient = new TrackerClient();
			trackerServer = trackerClient.getConnection();
			while(trackerServer == null  && flag < 5) {
				logger.info("[创建TrackerServer(createTrackerServer)][" + logId  
                        + "][第" + flag + "次重建]");
				
				flag ++;
				initClientGlobal();
				trackerServer = trackerClient.getConnection();
			}
			
			org.csource.fastdfs.ProtoCommon.activeTest(trackerServer  
                    .getSocket());
			idleConnectionPool.add(trackerServer);
			// 同一时间只能允许一个线程对nowPoolSize操作
			synchronized (this) {
				nowPoolSize ++;
			}
		} catch (Exception e) {
			logger.error("[创建TrackerServer(createTrackerServer)][" + logId  
                    + "][异常：{}]",e);
		} finally {
			if(trackerServer != null) {
				try {
					trackerServer.close();
				} catch (Exception e2) {
					logger.error("[创建TrackerServer(createTrackerServer)--关闭trackerServer异常]["  
                            + logId + "][异常：{}]", e2);
				}
			}
		}
	}
	
	/**
	 * 获取空闲连接
	 * @param logId
	 * @return
	 * @throws ApplicationException
	 */
	public TrackerServer checkout(final String logId) throws ApplicationException {
		
		logger.info("[获取空闲连接(checkout)][" + logId + "]");
		TrackerServer trackerServer = idleConnectionPool.poll();
		
		if(trackerServer == null) {
			if(nowPoolSize < maxPoolSize) {
				createTrackerServer(logId,COUNT);
				try {
					trackerServer = idleConnectionPool.poll(waitTimes, TimeUnit.SECONDS);
					
				} catch (Exception e) {
					logger.error("[获取空闲连接(checkout)-error][" + logId  
                            + "][error:获取连接超时:{}]",e);
					throw new RuntimeException(e);
				}
			}
		}
		
		logger.info("[获取空闲连接(checkout)][" + logId + "][获取空闲连接成功]");  
        return trackerServer;
	}
	
	/**
	 * 释放繁忙连接
	 * 如果空闲池的连接小于最小连接值，就把当前连接放入idleConnetctionPool
	 * 如果空闲池的连接大于等于最小连接值，就把当前释放连接丢弃
	 */
	public void checkin(final TrackerServer trackerServer) {
		logger.info("[释放当前连接(checkin)][prams:" + trackerServer  
                + "] ");
		if(trackerServer != null) {
			if(idleConnectionPool.size() < minPoolSize) {
				idleConnectionPool.add(trackerServer);
			} else {
				synchronized (this) {
					if(nowPoolSize != 0) {
						nowPoolSize --;
					}
				}
			}
		}
	}
	
	public void drop(final TrackerServer trackerServer,final String logId) {
		logger.info("[删除不可用连接方法(drop)][" + logId + "][parms:" + trackerServer  
                + "] ");
		if(trackerServer != null) {
			try {
				synchronized (this) {
					if(nowPoolSize != 0) {
						nowPoolSize --;
					}
				}
				trackerServer.close();
			} catch (Exception e) {
				logger.error("[删除不可用连接方法(drop)--关闭trackerServer异常][" + logId  
                        + "][异常：{}]",e);
			}
		}
	}
	
	private void initClientGlobal() throws Exception{
		ClientGlobal.init(CONF_FILENAME);
	}

	public LinkedBlockingQueue<TrackerServer> getIdleConnectionPool() {
		return idleConnectionPool;
	}

	public long getMinPoolSize() {
		return minPoolSize;
	}

	public void setMinPoolSize(long minPoolSize) {
		if(minPoolSize != 0) {
			this.minPoolSize = minPoolSize;
		}
	}

	public long getMaxPoolSize() {
		return maxPoolSize;
	}

	public void setMaxPoolSize(long maxPoolSize) {
		if(maxPoolSize != 0) {
			this.maxPoolSize = maxPoolSize;
		}
	}

	public long getWaitTimes() {
		return waitTimes;
	}

	public void setWaitTimes(long waitTimes) {
		if(waitTimes != 0) {
			this.waitTimes = waitTimes;
		}
	}
	
}
