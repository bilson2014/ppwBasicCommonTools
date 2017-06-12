package com.paipianwang.pat.common.web.poi.util;

import java.awt.Color;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.paipianwang.pat.common.util.ValidateUtil;

public class PoiReportUtils {

	public static void generateHeader(List<String> columns, XSSFWorkbook workbook, XSSFSheet sheet) {
		if(ValidateUtil.isValid(columns)) {
			// 设置默认的高度
			sheet.setDefaultRowHeight((short)(20 * 20));

			XSSFCellStyle cs = getCenterCellStyle(workbook);
			XSSFRow xssfRow = sheet.createRow(0);
			// 设置头部的高度
			xssfRow.setHeight((short)(26 * 20));
			for(int i = 0;i < columns.size();i ++) {
				// 宽度自适应
				sheet.autoSizeColumn(i, true);
				XSSFCell xssfCell = xssfRow.createCell(i);
				xssfCell.setCellStyle(cs);
				xssfCell.setCellValue(columns.get(i));
			}
		}
	}
	
	public static XSSFCellStyle getCenterCellStyle(XSSFWorkbook workbook) {
		XSSFCellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setAlignment(HorizontalAlignment.CENTER);
		cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		cellStyle.setBottomBorderColor(new XSSFColor(Color.black));
		setCellBorder(cellStyle);
		return cellStyle;
	}
	
	public static XSSFCellStyle getLeftCellStyle(XSSFWorkbook workbook) {
		XSSFCellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setAlignment(HorizontalAlignment.LEFT);
		cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		cellStyle.setBottomBorderColor(new XSSFColor(Color.black));
		setCellBorder(cellStyle);
		return cellStyle;
	}
	
	public static void setCellBorder(final XSSFCellStyle cellStyle) {
		cellStyle.setBorderBottom(XSSFCellStyle.BORDER_THIN); //下边框   
		cellStyle.setBorderLeft(XSSFCellStyle.BORDER_THIN);//左边框   
		cellStyle.setBorderTop(XSSFCellStyle.BORDER_THIN);//上边框   
		cellStyle.setBorderRight(XSSFCellStyle.BORDER_THIN);//右边框
	}
}
