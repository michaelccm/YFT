package com.yfjc.easyfilleqm;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;
import java.io.IOException;
import java.lang.reflect.Field;

public class JacobExcelUtil {
	private ActiveXComponent xl = null;
	private Dispatch workbooks = null;
	private Dispatch workbook = null;
	private Dispatch sheets = null;
	private Dispatch currentSheet = null;

//	public static void main(String[] args) {
//		JacobExcelUtil test = new JacobExcelUtil();
//		test.openExcel("C:\\EQM评估表 2.xlsx", false, false);
//		String TC_path = System.getenv("TPR");
//		try {
//			test.addDir(TC_path + "\\plugins");
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//
//		Dispatch sheet = test.getSheetByName("INPUT - Risk");
//		Dispatch cell = Dispatch.invoke(sheet, "Range", 2,
//				new Object[] { "F10" }, new int[1]).toDispatch();
//		String value = Dispatch.get(cell, "Value").toString();
//		test.setValue(sheet, "G8", "yyyy");
//		//System.out.println(value);
//		test.closeExcel(true);
//	}

	/**
	 * 激活指定名称的sheet页
	 */
	public Dispatch openSheetByName(Dispatch sheets, String sheetName) {
		Dispatch sheet1 = null;
		try {
			sheet1 = Dispatch.invoke(sheets, "Item", 2,
					new Variant[] { new Variant(sheetName) }, new int[1])
					.toDispatch();

			Dispatch.call(sheet1, "Activate");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sheet1;
	}
	
	
	/**
	 *  打开excel
	 * @param filepath  excel路径
	 * @param visible   是否可见
	 * @param readonly  是否只读
	 */

	public void openExcel(String filepath, boolean visible, boolean readonly) {
		ComThread.InitSTA();
		if (xl == null)
			xl = new ActiveXComponent("Excel.Application");
		xl.setProperty("Visible", new Variant(visible));
		if (workbooks == null)
			workbooks = xl.getProperty("Workbooks").toDispatch();
		workbook = Dispatch.invoke(
				workbooks,
				"Open",
				1,
				new Object[] { filepath, new Variant(false),
						new Variant(readonly) }, new int[1]).toDispatch();
	}
	
	

	/**
	 * 得到当前活动的sheet页
	 * @return
	 */
	public Dispatch getCurrentSheet() {
		currentSheet = Dispatch.get(workbook, "ActiveSheet")
				.toDispatch();
		return currentSheet;
	}
	
	
	
	/**
	 * 后台得到指定名称的sheet页
	 * @param name
	 * @return
	 */
	public Dispatch getSheetByName(String name) {
		return Dispatch.invoke(getSheets(), "Item", 2, new Object[] { name },
				new int[1]).toDispatch();
	}

	/**
	 * 得到sheet指定位置的值
	 * @param sheet
	 * @param position
	 * @return
	 */
	public String getValue(Dispatch sheet, String position) {
		Dispatch cell = Dispatch.invoke(sheet, "Range", 2,
				new Object[] { position }, new int[1]).toDispatch();
		return Dispatch.get(cell, "Value").toString();
	}

	 /**
	   *  得到sheets的集合对象
	   * @return
	   */
	public Dispatch getSheets() {
		if (sheets == null)
			sheets = Dispatch.get(workbook, "sheets").toDispatch();
		return sheets;
	}

	 /**
     * 释放资源
     */
	public void releaseSource() {
		if (xl != null) {
			xl.invoke("Quit", new Variant[0]);
			xl = null;
		}
		this.workbooks = null;
		ComThread.Release();
		System.gc();
	}

	 /**
     *  单元格写入值
     * @param sheet  被操作的sheet
     * @param position 单元格位置，如：C1
     * @param value
     */
	public void setValue(Dispatch sheet, String position, Object value) {
		Dispatch cell = Dispatch.invoke(sheet, "Range", 2,
				new Object[] { position }, new int[1]).toDispatch();
		Dispatch.put(cell, "Value", value);
	}

	/**
     * 关闭excel文档
     * @param f 含义不明 （关闭是否保存？默认false）
     */
	public void closeExcel(boolean f) {
		try {
			Dispatch.call(workbook, "Save");
			Dispatch.call(workbook, "Close",
					new Object[] { new Variant(f) });
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			releaseSource();
		}
	}

	public void addDir(String s) throws IOException {
		try {
			Field field1[] = ClassLoader.class.getDeclaredFields();// .getDeclaredField("usr_paths");
			for (int i = 0; i < field1.length; i++) {
				// //System.out
				// .println("======field==11=====" + field1[i].getName());
				// //System.out.println("======field======="+field1[i].get);
			}

			Field field = ClassLoader.class.getDeclaredField("usr_paths");
			field.setAccessible(true);
			String[] paths = (String[]) field.get(null);
			for (int i = 0; i < paths.length; i++) {
				// //System.out.println("======field=======" + paths[i]);
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
}
