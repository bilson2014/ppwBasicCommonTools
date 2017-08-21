package com.paipianwang.pat.common.web.poi.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Map;

import com.paipianwang.pat.common.config.PublicConfig;
import com.paipianwang.pat.common.web.file.FastDFSClient;
import com.paipianwang.pat.common.web.poi.util.XWPF.WordTemplate;

public class GenerateWord {

	public static String generateByModel(Map<String, String> beanParams, String fileName, int modelType) {
		// 获取模板文件路径
		String srcFilePath = getTemplate(modelType);
		File srcFile=new File(srcFilePath);

		// 生成临时文件
		String destFilePath = PublicConfig.FILE_TEMPLATE_PATH+File.separator+"temp"+File.separator+fileName+ ".docx";
//		String destFilePath = "f:/temp"+File.separator+fileName+ ".docx";
		File file = new File(destFilePath);
		
		FileInputStream fileInputStream = null;
		FileOutputStream out=null;
		BufferedOutputStream bos=null;
	
		try {
			fileInputStream = new FileInputStream(srcFile);
			WordTemplate template = new WordTemplate(fileInputStream);
			template.replaceTag(beanParams);
			out = new FileOutputStream(file);
			bos = new BufferedOutputStream(out);
			template.write(bos);
			
			fileInputStream.close();
			out.close();
			bos.close();
		}  catch (Exception e) {
			e.printStackTrace();
			return null;
		}finally{}
		
		// 临时文件上传到fast DFS
//		String fdstPath = FastDFSClient.uploadFile(file, fileName+ ".docx");
		// 删除临时文件
//		file.delete();

		return destFilePath;
	}

	/**
	 * 根据模板类型获取对应模板路径
	 * 
	 * @param modelType
	 * @return
	 */
	private static String getTemplate(int modelType) {
		return PublicConfig.FILE_TEMPLATE_PATH+File.separator+"template"+File.separator+"customerServiceLetterTemplate.docx";
//		return "f:/W-04客户项目服务函-模板x.docx";
	}
}
