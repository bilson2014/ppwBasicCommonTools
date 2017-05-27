package com.paipianwang.pat.common.web.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.beans.DocumentObjectBinder;
import org.apache.solr.client.solrj.impl.BinaryRequestWriter;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.request.FieldAnalysisRequest;
import org.apache.solr.client.solrj.response.AnalysisResponseBase.AnalysisPhase;
import org.apache.solr.client.solrj.response.AnalysisResponseBase.TokenInfo;
import org.apache.solr.client.solrj.response.FieldAnalysisResponse;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.SpellCheckResponse;
import org.apache.solr.client.solrj.response.SpellCheckResponse.Suggestion;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import com.paipianwang.pat.common.entity.BaseEntity;
import com.paipianwang.pat.common.web.service.BaseSolrService;
import com.paipianwang.pat.facade.information.entity.PmsNewsSolr;
import com.paipianwang.pat.facade.information.entity.PmsProductSolr;

public class BaseSolrServiceImpl implements BaseSolrService {

	@Override
	public long addDocs(String solrUrl, Collection<? extends BaseEntity> docs) {
		try {
			HttpSolrClient client = new HttpSolrClient(solrUrl);
			if (docs != null && docs.size() > 0) {
				client.setRequestWriter(new BinaryRequestWriter());
				UpdateResponse response = client.addBeans(docs.iterator());
				client.commit(true, true);
				client.optimize(true, true);
				return response.getResponse().size();
			}
		} catch (SolrServerException | IOException e) {
			e.printStackTrace();
		}
		return 0l;
	}

	@Override
	public long delDocs(String solrUrl, List<String> ids) {
		try {
			final HttpSolrClient client = new HttpSolrClient(solrUrl);
			final UpdateResponse response = client.deleteById(ids);
			final long size = response.getResponse().size();
			client.commit(true, true);
			client.optimize(true, true);
			return size;
		} catch (SolrServerException | IOException e) {
			e.printStackTrace();
		}
		return 0l;
	}

	@Override
	public List<PmsProductSolr> queryDocs(String solrUrl, SolrQuery query) {
		final HttpSolrClient client = new HttpSolrClient(solrUrl);
		client.setConnectionTimeout(5000);

		try {
			QueryResponse response = client.query(query);
			final long numFound = response.getResults().getNumFound();
			SolrDocumentList list = response.getResults();
			Map<String, Map<String, List<String>>> map = response.getHighlighting();

			// 高亮设置
			for (int i = 0; i < list.size(); i++) {
				final SolrDocument document = list.get(i);
				document.setField("total", numFound); // 设置总数
				if (null != map) {
					final List<String> pNameList = map.get(document.getFieldValue("productId")).get("productName");
					if (pNameList != null && !pNameList.isEmpty()) {
						document.setField("productName", pNameList.get(0));
					}

					List<String> tagList = map.get(document.getFieldValue("productId")).get("tags");
					if (tagList != null && !tagList.isEmpty()) {
						document.setField("tags", tagList.get(0) + "...");
					}
				}

				list.set(i, document);
			}

			DocumentObjectBinder binder = new DocumentObjectBinder();
			List<PmsProductSolr> lists = binder.getBeans(PmsProductSolr.class, list);
			return lists;
		} catch (SolrServerException | IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void deleteProductDoc(long productId, String solrUrl) {
		final HttpSolrClient client = new HttpSolrClient(solrUrl);
		client.setConnectionTimeout(3000);

		try {
			client.deleteById(String.valueOf(productId));
			client.optimize();
		} catch (SolrServerException | IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public List<String> suggestProductDocs(String solrUrl, SolrQuery query) {
		final HttpSolrClient client = new HttpSolrClient(solrUrl);
		client.setConnectionTimeout(2000);

		try {
			QueryResponse response = client.query(query);
			final SpellCheckResponse sr = response.getSpellCheckResponse();
			if (sr != null) {
				List<Suggestion> list = sr.getSuggestions();
				for (final Suggestion suggestion : list) {
					final List<String> ls = suggestion.getAlternatives();
					return ls;
				}
			}
		} catch (SolrServerException | IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public List<PmsNewsSolr> queryNewDocs(String solrUrl, SolrQuery query) {
		final HttpSolrClient client = new HttpSolrClient(solrUrl);
		client.setConnectionTimeout(5000);

		try {
			QueryResponse response = client.query(query);
			final long numFound = response.getResults().getNumFound();
			SolrDocumentList list = response.getResults();

			for (int i = 0; i < list.size(); i++) {
				final SolrDocument document = list.get(i);
				document.setField("total", numFound); // 设置总数
				list.set(i, document);
			}

			DocumentObjectBinder binder = new DocumentObjectBinder();
			List<PmsNewsSolr> lists = binder.getBeans(PmsNewsSolr.class, list);
			return lists;
		} catch (SolrServerException | IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public List<String> getAnalysis(final String solrUrl, final String sentence) {

		HttpSolrServer solrServer = new HttpSolrServer(solrUrl);
		solrServer.setConnectionTimeout(5000);
		FieldAnalysisRequest request = new FieldAnalysisRequest("/analysis/field");
		request.addFieldName("productName");// 字段名，随便指定一个支持中文分词的字段
		request.setFieldValue("text_ik");// 字段值，可以为空字符串，但是需要显式指定此参数
		request.setQuery(sentence);
		FieldAnalysisResponse response = null;
		try {
			response = request.process(solrServer);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Set<String> set = new HashSet<String>();
        Iterator<AnalysisPhase> it = response.getFieldNameAnalysis("productName")
                .getQueryPhases().iterator();
        while(it.hasNext()) {
          AnalysisPhase pharse = (AnalysisPhase)it.next();
          List<TokenInfo> list = pharse.getTokens();
          for (TokenInfo info : list) {
              set.add(info.getText());
          }
        }
		return new ArrayList<String>(set);
	}

}
