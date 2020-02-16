package com.teamcenter.custwork.toolkit;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentDatasetType;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;

public class UtilTool {
	private static String logPath = null;	

	public static File findDataset(TCSession session,String datasetName) throws TCException{	    
	     TCComponentDatasetType datasetType = (TCComponentDatasetType)session.getTypeComponent("Dataset");
	     TCComponentDataset  dataset = datasetType.find(datasetName);
		if(dataset == null){
			return null;
		}
		File[] files = dataset.getFiles(dataset.getProperty("ref_names"));
		return files != null && files.length>0?files[0]:null;
	}

	
	public static  File getFile(File[] listFile,String xxs){
		File file = null;
		if(listFile != null && listFile.length>0){
			for(int i=0;i<listFile.length;i++){
				System.out.println("文件名是：" + listFile[i].getName());
				if(listFile[i].isFile() && 
						listFile[i].getName().endsWith(xxs)){
					System.out.println("找到：" + listFile[i].getName() + "文件");
					file = listFile[i];
				}
			}
		}
		return file;
	}

	
	public static void writeLog(String s){
		if(logPath == null){			
			logPath = System.getProperty("java.io.tmpdir")+
			"\\" + Long.toString(System.currentTimeMillis())+".log";
		}
		//LogsReaderWriter.writeIntoFile(s, logPath, true);
	}

	public static void writeBat(String path,String value){
		//LogsReaderWriter.writeIntoFile(value, path, true);
	}

	public static void execProcessBat(String batPath) throws IOException{
		String command="cmd /c start " + batPath;
		Process process = Runtime.getRuntime().exec(command);
		InputStream errorStream = process.getErrorStream();
		InputStreamReader strem = new InputStreamReader(errorStream);
		BufferedReader reader = new BufferedReader(strem);
		String str = reader.readLine();
		while(str != null){
			writeLog(str);
			str = reader.readLine();
		}	
		File file = new File(batPath);
		file.delete();
	}
	
	
	public static String getLogPath() {
		return logPath;
	}

	public static void setLogPath(String logPath) {
		UtilTool.logPath = logPath;
	}

	
}
