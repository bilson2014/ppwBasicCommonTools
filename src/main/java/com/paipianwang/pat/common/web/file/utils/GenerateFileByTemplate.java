package com.paipianwang.pat.common.web.file.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map;

import org.xhtmlrenderer.pdf.ITextFontResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.lowagie.text.pdf.BaseFont;
import com.paipianwang.pat.common.config.PublicConfig;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class GenerateFileByTemplate {

	private static final Configuration configuration;
	public static final String templatePath=PublicConfig.FILE_TEMPLATE_PATH+File.separator+"template"+File.separator;
	public static final String sourcesPath=PublicConfig.FILE_TEMPLATE_PATH+File.separator+"template"+File.separator+"ppm"+File.separator;

	static {
		configuration = new Configuration();
		configuration.setDefaultEncoding("utf-8");
		try {
			configuration.setDirectoryForTemplateLoading(new File(templatePath));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void generatePdf(Map<String, Object> data,String templateFile,OutputStream os) {

		File outFile = new File(sourcesPath+"temp"+(int)(Math.random()*10000)+".html");
		Writer out;
		try {
			out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile), "utf-8"));

			Template freemarkerTemplate = configuration.getTemplate(templateFile);
			freemarkerTemplate.process(data, out); 
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		ITextRenderer renderer = new ITextRenderer();

		try {
			String url = outFile.toURI().toURL().toString();
			renderer.setDocument(url);

			// 解决中文支持问题
			ITextFontResolver fontResolver = renderer.getFontResolver();
			// 宋体
			fontResolver.addFont(sourcesPath+"font"+File.separator+"simsun.ttc", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
			// 微软雅黑
			fontResolver.addFont(sourcesPath+"font"+File.separator+"msyh.ttc", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
			fontResolver.addFont(sourcesPath+"font"+File.separator+"msyhbd.ttc", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
			fontResolver.addFont(sourcesPath+"font"+File.separator+"msyhl.ttc", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);

			renderer.layout();
			renderer.createPDF(os, true);
			os.flush();
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(outFile.exists()) {
				outFile.delete();
			}
		}

	}
	//线上图片地址不支持https://resource.apaipian.com/resource/ 需无https
	public static String getFullImgPath(String path) {
		return PublicConfig.FDFS_URL+path;
//		return "http://10.10.112.236/"+path;
	}	
	
}
