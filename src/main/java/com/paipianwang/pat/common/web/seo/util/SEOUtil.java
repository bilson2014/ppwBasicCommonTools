package com.paipianwang.pat.common.web.seo.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.paipianwang.pat.common.config.PublicConfig;
import com.paipianwang.pat.common.util.ValidateUtil;

/**
 * SEO tdk数据
 */
@Component
public class SEOUtil {
	final Logger log = LoggerFactory.getLogger(SEOUtil.class);

	private static JSONArray tdkArray;
	
	//数据格式
	public static String ARRAY_NAME="items";
	public static String ITEMS_KEY_NAME="key";
	public static String ITEMS_NAME_NAME="name";
	public static String ITEMS_URL_NAME="url";
	public static String ITEMS_TITLE_NAME="title";
	public static String ITEMS_KEYWORDS_NAME="keywords";
	public static String ITEMS_DESCRIPTION_NAME="description";
	

	/**
	 * 初始化
	 */
	@PostConstruct
	public void init() {
		log.info("init SEOUtil");
		loadTdkConfig();
	}

	/**
	 * 加载tdk参数
	 */
	private void loadTdkConfig() {
		String path = PublicConfig.TDK_CONFIG_PATH;
		File file = new File(path);
		StringBuilder content = new StringBuilder();
		if (file.exists()) {
			// 读取文件信息
			FileInputStream fstream=null;
			InputStreamReader istream=null;
			BufferedReader reader = null;
			try {
				fstream=new FileInputStream(file);
				istream=new InputStreamReader(fstream,"UTF-8");
				reader=new BufferedReader(istream);
				String line = null;
				while ((line = reader.readLine()) != null) {
					if (ValidateUtil.isValid(line)) {
						content.append(line);
					}
				}
				// 格式化-JSON
				JSONObject root=JSONObject.parseObject(content.toString());//new JSONObject(content.toString());
				tdkArray=root.getJSONArray(ARRAY_NAME);
			} catch (Exception e) {
				log.error("error", e);
			} finally {
				try {
					if (fstream != null) {
						fstream.close();
					}
					if (istream != null) {
						istream.close();
					}
					if (reader != null) {
						reader.close();
					}
				} catch (IOException e) {
					log.error("close reader error", e);
				}
			}
		}
	}
	
	/**
	 * 根据key获取对应tdk项
	 * @param key
	 * @return
	 */
	public static JSONObject getByKey(String key){
		if(key!=null && tdkArray!=null){
			for(int i=0;i<tdkArray.size();i++){
				JSONObject obj=tdkArray.getJSONObject(i);
				if(key.equals(obj.getString(ITEMS_KEY_NAME))){
					return obj;
				}
			}
		}
		return null;
	}
	
	/**
	 * 获取默认tdk
	 * @return
	 */
	public static JSONObject getDefault(){
		if(tdkArray!=null){
			for(int i=0;i<tdkArray.size();i++){
				JSONObject obj=tdkArray.getJSONObject(i);
				if("jpal".equals(obj.getString(ITEMS_KEY_NAME))){
					return obj;
				}
			}
		}
		return null;
	}
}
