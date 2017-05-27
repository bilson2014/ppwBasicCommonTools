package com.paipianwang.pat.common.web.service;

import java.util.Collection;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;

import com.paipianwang.pat.common.entity.BaseEntity;
import com.paipianwang.pat.facade.information.entity.PmsNewsSolr;
import com.paipianwang.pat.facade.information.entity.PmsProductSolr;

public interface BaseSolrService {

	/**
	 * 添加document
	 * @param solrUrl solr服务地址
	 * @param docs 文档集合
	 */
	public long addDocs(final String solrUrl,final Collection<? extends BaseEntity> docs);
	
	/**
	 * 删除 document
	 * @param solrUrl solr服务地址
	 * @param ids 主键集合
	 */
	public long delDocs(final String solrUrl,final List<String> ids);
	
	/**
	 * 查询文档
	 * @param solrUrl solr服务地址
	 * @param query 条件
	 * @return 符合条件的 集合
	 */
	public List<PmsProductSolr> queryDocs(final String solrUrl,final SolrQuery query);
	
	public void deleteProductDoc(final long productId,final String solrUrl);
	
	public List<String> suggestProductDocs(final String solrUrl,final SolrQuery query);
	
	public List<PmsNewsSolr> queryNewDocs(final String solrUrl, final SolrQuery query);
	
	public List<String> getAnalysis(final String solrUrl, final String sentence);
}
