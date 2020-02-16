package com.yfjc.lccreport;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;
/**
 * 
try {
	String wsf_path = getFileFromClass(getClass(), "deleteExcelCol.wsf",
					System.getenv("Temp"));
	String txt_value = "1|2|3";// 需要删除的列
	StringBuffer sb = new StringBuffer(txt_value);
	String excel_path = "C:\\Users\\Administrator\\Desktop\\延峰现场\\VBA删除指定列\\LCC.xlsx";
	String[] paras = new String[] { sb.reverse().toString(), excel_path };
	runWsf(wsf_path, paras);

} catch (Exception e) {
			// TODO Auto-generated catch block
	e.printStackTrace();
}
 * @author wuh
 *
 */
public class WSFUtil {

	/**
	 * 将wsf下载到temp目录
	 * @param class1              当前class所在路径
	 * @param fileName  wsf文件名
	 * @param path      需要生成的wsf所在路径
	 * @return
	 * @throws Exception
	 */
	public static String getFileFromClass(Class<?> class1, String fileName,
			String path) throws Exception {
		String filePath = path + "\\" + fileName;
		File file = new File(filePath);
		if (file.exists()) {
			file.delete();
		}
		file.createNewFile();

		InputStream inputStream = class1.getResourceAsStream(fileName);
		OutputStream os = new BufferedOutputStream(new FileOutputStream(
				filePath));

		int readLen = 0;
		byte[] buf = new byte[1];

		while ((readLen = inputStream.read(buf, 0, 1)) != -1) {
			os.write(buf, 0, readLen);
		}

		inputStream.close();
		os.flush();
		os.close();

		return filePath;
	}

	/**
	 *  运行wsf
	 * @param wsfPath   wsf路径
	 * @param paras     运行wsf给的参数
	 * @return
	 */
	public static Process runWsf(String wsfPath, String[] paras) {
		Process proc = null;
		try {
			Vector<String> cmdVector = new Vector<String>();
			cmdVector.add("wscript");
			cmdVector.add(wsfPath);
			for (String para : paras) {
				cmdVector.add(para);
			}
			String[] cmd = cmdVector.toArray(new String[cmdVector
					.size()]);
			Runtime runtime = Runtime.getRuntime();
			proc = runtime.exec(cmd);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return proc;
	}
}
