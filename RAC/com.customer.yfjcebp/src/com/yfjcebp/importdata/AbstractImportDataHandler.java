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
#	2013-3-5	liqz  		Ini		������ʷ���ݣ�����hyperion��timelog�����˹�ʵ�ʷ����õ��ĳ����ࣩ									   
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
	 * initImportPublicPreferenceData::���˹���Hyperion�ȵ�����,���ù�������ѡ������������ƥ�䡢����ƥ��
	 * @return boolean
	 * */
	protected  boolean initImportPublicPreferenceData(){
		return true;
	}
	
	/**
	 * initImportPreferenceData::���˹���Hyperion�ȵ�������Ҫ��ʼ������ѡ��
	 * @return boolean
	 * */
	protected  boolean initImportPreferenceData(){
		return true;
	}
	
	/**
	 * doImport::ִ�е���
	 * @return boolean 
	 * */
	protected abstract void doImport();
	
	/**
	 * checkImportFile::���ѡ������ļ�
	 * @param TCComponent selectedComponent
	 * @return File
	 * */
	protected abstract File checkImportFile(TCComponent selectedComponent);
	
}
