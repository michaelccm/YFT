/*
#=============================================================================
#																			   
#			opyright (c) 2009 Origin Enterprise Solution LTD.		   
#																			   
#=============================================================================
# File name: ImportTimeLogHandler.java
# File description: 										   	
#=============================================================================
#	Date		Name		Action	Description of Change					   
#	2013-2-25	liuc  	Ini		³õÊ¼»¯		
#	2013-3-4	liuc  	modify		ÐÞ¸Ä		
#	2013-4-18	zhanggl  	modify								   
#=============================================================================
 */

package com.yfjcebp.importdata.importtimelog;

import java.io.File;
import com.teamcenter.rac.kernel.TCComponent;
import com.yfjcebp.importdata.AbstractImportDataHandler;
import com.yfjcebp.importdata.utils.OriginUtil;


public class ImportTimeLogHandler extends AbstractImportDataHandler {



	
	/*
	 * @see com.yfjcebp.importdata.AbstractImportDataHandler#doImport()
	 */
	@Override
	protected void doImport() {
		session.setStatus("import timelog data£¬waiting...");
		session.queueOperation(new ImportTimeLogOperation(file,session));
		session.setReadyStatus();
		
	}

	
	
	/* 
	 * @see com.yfjcebp.importdata.AbstractImportDataHandler#checkImportFile(com.teamcenter.rac.kernel.TCComponent)
	 */
	@Override
	protected File checkImportFile(TCComponent component) {
		file = OriginUtil.checkImportFile(component);
		return file;
	}


	
}
