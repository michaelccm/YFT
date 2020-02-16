/*
 #=============================================================================
 #																			   
 #			opyright (c) 2009 Origin Enterprise Solution LTD.		   
 #																			   
 #=============================================================================
 # File name: ImportHyperionDataHandler.java
 # File description: 										   	
 #=============================================================================
 #	Date		Name		Action	Description of Change					   
 #	2013-3-11	liuc  		Ini		解析excel的工具类				   
 #=============================================================================
 */
package com.yfjcebp.projectmanager.budget.dialogs;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Vector;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

public class JacobEReportTool {
	

	public ActiveXComponent activexcomponent;
	public Dispatch workbooks = null;
	public Dispatch workbook = null;
	// public Dispatch sheet = null;

	private HashMap<String, String> fileType = new HashMap<String, String>();
	{
		fileType.put("txt", "icons/txticon.exe");
		fileType.put("doc", "icons/wordicon.exe");
		fileType.put("xls", "icons/excelicon.exe");
		fileType.put("pdf", "icons/pdficon.ico");
	}
	private Vector<String> imageType = new Vector<String>();
	{
		imageType.add("bmp");
		imageType.add("jpg");
		imageType.add("jpeg");
		imageType.add("gif");
	}

	public void addDir(String s) throws IOException {
		try {
			// System.out.println("======222=======");
			Field field1[] = ClassLoader.class.getDeclaredFields();// .getDeclaredField("usr_paths");
			for (int i = 0; i < field1.length; i++) {
				// System.out
				// .println("======field==11=====" + field1[i].getName());
				// System.out.println("======field======="+field1[i].get);
			}

			Field field = ClassLoader.class.getDeclaredField("usr_paths");
			field.setAccessible(true);
			String[] paths = (String[]) field.get(null);
			for (int i = 0; i < paths.length; i++) {
				// System.out.println("======field=======" + paths[i]);
				if (s.equals(paths[i])) {
					return;
				}
			}
			String[] tmp = new String[paths.length + 1];
			System.arraycopy(paths, 0, tmp, 0, paths.length);
			tmp[paths.length] = s;
			field.set(null, tmp);
		} catch (IllegalAccessException e) {
			throw new IOException(
					"Failed to get permissions to set library path");
		} catch (NoSuchFieldException e) {
			throw new IOException(
					"Failed to get field handle to set library path");
		}
	}

	// 得到cell业��,根据Dispatch sheet
	public String getDataFromExcel(String col, int row, Dispatch sheet) {
		if(sheet!=null)
			return getValue(col + "" + row, sheet);
		else
			return "";
	}

	public void closeExcelFile(boolean close) {
		try {
			Dispatch.call(workbook, "Close", new Variant(close));
		} catch (Exception e) {
			// System.out.println("保存关闭时出错．．．");
			e.printStackTrace();
		}finally {
			activexcomponent.invoke("Quit", new Variant[] {});
		}
	}

	public Dispatch openExcelFile(Dispatch sheets, String sheetName) {
		Dispatch sheet1 = null;
		try {

			sheet1 = Dispatch.invoke(sheets, "Item", Dispatch.Get,
					new Variant[] { new Variant(sheetName) }, new int[1])
					.toDispatch();
			// �活sheet工作�
			Dispatch.call(sheet1, "Activate");

			// sheet = Dispatch.get((Dispatch)
			// workbook,"ActiveSheet").toDispatch();
		} catch (Exception e) {
			// System.out.println("打开EXCEL时出错．．．");
			e.printStackTrace();
		}
		return sheet1;
	}

	public Dispatch getSheets(String filename) {
		System.out.println("getSheets before");
		activexcomponent = new ActiveXComponent("Excel.Application");
		activexcomponent.setProperty("Visible", new Variant(false));
		workbooks = activexcomponent.getProperty("Workbooks").toDispatch();
		System.out.println("打开excel�");
		workbook = Dispatch
				.invoke((Dispatch)workbooks,
						"Open",
						Dispatch.Method,
						new Object[] { filename, new Variant(false),
								new Variant(false) },// 昐�以只读方式打�
						new int[1]).toDispatch();
		System.out.println("打开excel完成");
		Dispatch sheets = Dispatch.get(workbook, "Sheets")
				.toDispatch();
		System.out.println("getSheets finish");
		return sheets;
	}

	// 写入�
	public void insertValue(String position, String type, String value,
			Dispatch sheet1) {
		Dispatch cell = Dispatch.invoke(sheet1, "Range", Dispatch.Get,
				new Object[] { position }, new int[1]).toDispatch();
		String str = getValue(position, sheet1);
		Dispatch.put(cell, type, str + value);
	}

	// 读取�
	public String getValue(String position, Dispatch sheet1) {
		Dispatch cell = Dispatch.invoke(sheet1, "Range", Dispatch.Get,
				new Object[] { position }, new int[1]).toDispatch();
		String value = Dispatch.get(cell, "Value").toString();

		return value;
	}

	// 读取高度
	public String getHeight(String position, Dispatch sheet1) {
		Dispatch cell = Dispatch.invoke(sheet1, "Range", Dispatch.Get,
				new Object[] { position }, new int[1]).toDispatch();
		String value = Dispatch.get(cell, "Height").toString();

		return value;
	}

}
