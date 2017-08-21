package com.paipianwang.pat.common.web.poi.util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.solr.common.util.Hash;

import com.paipianwang.pat.common.config.PublicConfig;
import com.paipianwang.pat.common.web.file.FastDFSClient;

import net.sf.jxls.exception.ParsePropertyException;
import net.sf.jxls.transformer.XLSTransformer;

/**
 * 根据模板生成excel文件
 */
public class GenerateExcel {

	/**
	 * 根据模板生成excel文件
	 * @param beanParams
	 * @param modelType
	 * @return
	 */
	public static String generateByModel(Map<String, String> beanParams,String fileName, int modelType) {
		// 获取模板文件路径
		String srcFilePath = getTemplate(modelType);

		// 生成临时文件
		String destFilePath = PublicConfig.FILE_TEMPLATE_PATH+File.separator+"temp"+File.separator+fileName+".xlsx";
//		String destFilePath = "f:\\temp"+File.separator+fileName+".xlsx";
		XLSTransformer former = new XLSTransformer();

		try {
			former.transformXLS(srcFilePath, beanParams, destFilePath);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} 
		File file=new File(destFilePath);
		//临时文件上传到fast DFS
//		String fdstPath=FastDFSClient.uploadFile(file, fileName+".xlsx");
		//删除临时文件
//		file.delete();
		
		return destFilePath;
	}
	
	/**
	 * 根据模板类型获取对应模板路径
	 * @param modelType
	 * @return
	 */
	private static String getTemplate(int modelType){
		return PublicConfig.FILE_TEMPLATE_PATH+File.separator+"template"+File.separator+"projectSheetTemplate.xlsx";
//		return "f:\\C-02项目制作单 - 模板.xlsx";
	}
	
}
