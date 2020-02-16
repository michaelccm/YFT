/*
#=============================================================================
#																			   
#			copyright (c) 2009 Origin Enterprise Solution LTD.		   
#																			   
#=============================================================================
# File name: AbstractImportDataHandler.java
# File description: 										   	
#=============================================================================
#	Date		Name		Action	Description of Change					   
#	2013-3-5	liqz  		Ini		导入历史数据（包括hyperion、timelog、非人工实际费用用到的抽象类）									   
#=============================================================================
*/
package com.yfjcebp.importdata;

import java.io.File;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCSession;

/**
 * @author liqz
 */
public abstract class AbstractImportDataHandler extends AbstractHandler{
	protected TCSession session;
	protected AbstractAIFUIApplication	app;
	protected File file;

	/* 
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		app = AIFUtility.getCurrentApplication();
		session = (TCSession) app.getSession();
		InterfaceAIFComponent component = app.getTargetComponent();
	
		file = checkImportFile((TCComponent)component);
		if(file == null) {
			return null;
		}
		if(!initImportPublicPreferenceData()){
			return null;
		}
		if(!initImportPreferenceData()){
			return null;
		}
		doImport();
		return null;
	}

	/**
	 * initImportPublicPreferenceData::非人工、Hyperion等导入中,配置公共的首选项，比如费用类型匹配、日期匹配
	 * @return boolean
	 * */
	protected  boolean initImportPublicPreferenceData(){
		return true;
	}
	
	/**
	 * initImportPreferenceData::非人工、Hyperion等导入中需要初始化的首选项
	 * @return boolean
	 * */
	protected  boolean initImportPreferenceData(){
		return true;
	}
	
	/**
	 * doImport::执行导入
	 * @return boolean 
	 * */
	protected abstract void doImport();
	
	/**
	 * checkImportFile::检查选择导入的文件
	 * @param TCComponent selectedComponent
	 * @return File
	 * */
	protected abstract File checkImportFile(TCComponent selectedComponent);
	
}
