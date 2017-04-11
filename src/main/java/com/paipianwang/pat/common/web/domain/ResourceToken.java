package com.paipianwang.pat.common.web.domain;

import com.paipianwang.pat.common.config.PublicConfig;
import com.paipianwang.pat.common.entity.BaseEntity;

/**
 * 判断solr产品源 实体
 * @author Jack
 *
 */
public class ResourceToken extends BaseEntity {

	private static final long serialVersionUID = 454898756731549497L;

	public boolean flag = false; // 状态位
	
	public String solrUrl = PublicConfig.SOLR_URL; // 搜索core地址

	public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	public String getSolrUrl() {
		return solrUrl;
	}

	public void setSolrUrl(String solrUrl) {
		this.solrUrl = solrUrl;
	}
	
}
