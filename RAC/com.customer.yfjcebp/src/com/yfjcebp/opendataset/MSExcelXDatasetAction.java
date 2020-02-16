
/*
 #=============================================================================
 #																			   
 #			opyright (c) 2009 Origin Enterprise Solution LTD.		  

 #																			   
 #=============================================================================
 # File name: MSExcelXDatasetAction.java
 # File description: 						   	
 #=============================================================================
 #	Date		Name		Action	Description of Change					   
 #	2013-3-11	MengYaWei  	OpenExcelXDataset		 								   
 #=============================================================================
 */

package com.yfjcebp.opendataset;

import com.teamcenter.rac.kernel.AEShell;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.InterfaceDatasetAction;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.Registry;
import com.yfjc.easyfilleqm.MSExcelXAction;

import java.io.*;
import java.util.Vector;
public class MSExcelXDatasetAction implements InterfaceDatasetAction {
	private Registry reg = Registry.getRegistry("com.yfjcebp.opendataset.opendataset");
	//modify by wuh 2014-6-11
	private MSExcelXAction fy_action;
	@Override
	public boolean postProcess(TCComponentDataset tccomponentdataset, String s,
			int i) {
		// TODO Auto-generated method stub
		//关闭数据集时调用该方�
		//System.out.println("11fy postProcess start");
		fy_action = new MSExcelXAction();
		fy_action.postProcess_EQM(tccomponentdataset, s, i);
		//System.out.println("fy postProcess end");
		return false;
	}

	@Override
	public int preProcess(TCComponentDataset tccomponentdataset,
			AEShell aeshell, int i) {
		// TODO Auto-generated method stub
		//打开数据集时调用该方�
		try {
			TCComponent tempRev = tccomponentdataset.getReferenceProperty("item_revision");//获得当前数据集所在版��
			//System.out.println("tempRev--->"+tempRev);
			if(tempRev!=null){
				String tempRevType = tempRev.getType();
				if("JCI6_XSORevision".equals(tempRevType)){//如果数据集类型为MSExcelX�2007版的Excel，并且�数据集所在版朸�"XSO"版本时，才获取�选项
				String[] siteValues = tccomponentdataset.getSession().getPreferenceService().getStringArray(TCPreferenceService.TC_preference_site, "YFJC_Office_Attribute_Mapping");
				if(siteValues.length>0){
				    	XSODatasetParser parser = new XSODatasetParser(tccomponentdataset);
				    	String err = null;
				    	String txtFilePath = System.getenv("TEMP")+"\\excel_signoff.txt";//为了VB程序操作的方便将生成的txt文档的路径和名字写�了�
				    	if((err=parser.init())==null){
				    		TxtFileRWHelper helper = new TxtFileRWHelper(txtFilePath);
				    		Vector<String> realm_attr = parser.parsePreference(siteValues);
				    		if(realm_attr!=null){
				    			try {
									helper.writeTxtFile(realm_attr);
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
									MessageBox.post(reg.getString("WriteTXTFailed"),"WARNING",MessageBox.WARNING);
								}
				    		}else{
				    			MessageBox.post(reg.getString("IllegalPreferenceFormat"),"WARNING", MessageBox.WARNING);
				    		}
				    	}else{
				    		MessageBox.post(err,"WARNING", MessageBox.WARNING);
				    	}
					}else{
						MessageBox.post(reg.getString("PreferenceUnset"),"WARNING", MessageBox.WARNING);
					}
				 }
			 }
			
			//add by wuh 2014-6-11    write by fy
			//System.out.println("fy preProcess start");
			fy_action = new MSExcelXAction();
			fy_action.preProcess_EQM(tccomponentdataset, aeshell, i);
			//System.out.println("fy preProcess end");
		} catch (TCException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

	 /*
     * 解析XSO数据�
     */
    class XSODatasetParser{
    	private TCComponentDataset tccomponentdataset = null;
    	private TCComponentItemRevision XSORev = null;
    	private TCComponent Project = null;
    	private TCComponent ProjectInfo = null;
    	private TCComponentItemRevision ProjectInfoRev = null;
    	private TCComponent	Gate = null;
    	private String errMsg = null;
    	public XSODatasetParser(TCComponentDataset tccomponentdataset){
    		this.tccomponentdataset = tccomponentdataset;
    	}
    	//初�化当前系统中�有的对象
    	private String init() throws TCException{
    		errMsg = "";
    		XSORev = (TCComponentItemRevision) tccomponentdataset.getReferenceProperty("item_revision");
    		Gate =  XSORev.getReferenceProperty("jci6_Gate");
    		if(Gate == null){
    			errMsg+=reg.getString("GateUnset");
    		}
    		TCComponent[] tempProjects = XSORev.getReferenceListProperty("project_list");
    		if(tempProjects!=null&&tempProjects.length >0){
    			for(int i=0;i<tempProjects.length;i++){
    				String ProjectType = tempProjects[i].getType();
    				if("TC_Project".equals(ProjectType)){
    					Project = tempProjects[i];
    					break;
    				}
    			}
    		TCComponent[] tempProjectInfos = Project.getReferenceListProperty("TC_Program_Preferred_Items");//TC_Program_Preferred_Items
    		if(tempProjectInfos!=null&&tempProjectInfos.length>0){	
        		for(int i=0;i<tempProjectInfos.length;i++){
        				String projectInfoType = tempProjectInfos[i].getType();
        				if("JCI6_ProgramInfo".equals(projectInfoType)){
        					ProjectInfo= tempProjectInfos[i];
        					ProjectInfoRev = ((TCComponentItem) ProjectInfo).getLatestItemRevision();
        					break;
        				}
        		}
    		}
        		if(ProjectInfo==null){
        			errMsg+=reg.getString("ProjectInfoUnset");
        		}
    		}else{
    			errMsg+=reg.getString("ProjectUnset");
    		}
    		
    		if("".equals(errMsg)){
    			return null;
    		}else{
    			return errMsg;
    		}
    	}
    	//根据首�项值，获得对应对象的内容，并返回需要写�xt文档业�内�
    	public Vector<String> parsePreference(String[] preferenceValues) throws TCException{
    		Vector<String> realm_attr = new Vector<String>();//“域�=属���的集合
    		for(int i=0;i<preferenceValues.length;i++){
			String[] obj_attrs = preferenceValues[i].split("=");
			if(obj_attrs.length==2){
				int index = obj_attrs[0].indexOf('.');
				if(index != -1){
				String obj = obj_attrs[0].substring(0, index);
				String att = obj_attrs[0].substring(index+1);
				StringBuffer sb = new StringBuffer(obj_attrs[1]);//域名
				sb.append("=");									//=
				if("Rev".equals(obj)){			//属��
					sb.append(XSORev.getProperty(att)==null?"":XSORev.getProperty(att));
				}else if("ProgramInfo".equals(obj)){
					sb.append(ProjectInfo.getProperty(att)==null?"":ProjectInfo.getProperty(att));
				}else if("ProgramInfoRev".equals(obj)){
					sb.append(ProjectInfoRev.getProperty(att)==null?"":ProjectInfoRev.getProperty(att));
				}else if("Gate".equals(obj)){
					sb.append(Gate.getProperty(att)==null?"":Gate.getProperty(att));
				}
				realm_attr.add(sb.toString());
				}else{
					return null;
				}
			}else{
				return null;
			}
    		}
    		return realm_attr;
    	}
    }
    /*
     * 读，写Text文档
     */
    class TxtFileRWHelper{
    	private File txtFile = null;
    	private BufferedReader br = null;
    	private BufferedWriter bw = null;
    	public TxtFileRWHelper(String txtFilePath){
    		this.txtFile = new File(txtFilePath);
    		if(!this.txtFile.exists()){
    			try {
					this.txtFile.createNewFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    		}
    	}
    	//�	Text文件
    	//返回值：Text文件内�，Vector每个成员相当于一行数�
    	public Vector<String> readTxtFile() throws IOException{
    		Vector<String> txtFileContent = new Vector<String>();
				br = new BufferedReader(new FileReader(txtFile));
				if(!txtFile.canRead()){
					txtFile.setReadable(true);
				}
				String line = br.readLine();
				while(line != null){
					txtFileContent.add(line);
					line = br.readLine();
				}
				br.close();
				br = null;
    		return txtFileContent;
    	}
    	//写text文件
    	//入参：Vector<String>Vector业�每个成员作为�行数��
    	public void writeTxtFile(Vector<String> txtFileContent) throws IOException{
				bw = new BufferedWriter(new FileWriter(txtFile));
				if(!txtFile.canWrite()){
					txtFile.setWritable(true);
				}
				for(int i=0;i<txtFileContent.size();i++){
					bw.write(txtFileContent.get(i));
					if(i!=txtFileContent.size()-1){
						bw.write("|");
					}
				}	
				bw.close();
				bw = null;
    	}
    	//删除text 文件
    	public void deleteTxtFile(){
    		if(txtFile.exists()){
    			txtFile.delete();
    		}
    		
    	}
    }

}
