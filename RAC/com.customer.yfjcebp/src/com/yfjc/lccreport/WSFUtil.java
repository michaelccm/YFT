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
	String txt_value = "1|2|3";// ��Ҫɾ������
	StringBuffer sb = new StringBuffer(txt_value);
	String excel_path = "C:\\Users\\Administrator\\Desktop\\�ӷ��ֳ�\\VBAɾ��ָ����\\LCC.xlsx";
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
	 * ��wsf���ص�tempĿ¼
	 * @param class1              ��ǰclass����·��
	 * @param fileName  wsf�ļ���
	 * @param path      ��Ҫ���ɵ�wsf����·��
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
	 *  ����wsf
	 * @param wsfPath   wsf·��
	 * @param paras     ����wsf���Ĳ���
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
