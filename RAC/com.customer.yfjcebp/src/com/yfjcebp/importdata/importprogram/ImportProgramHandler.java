/*
#=============================================================================
#																			   
#			opyright (c) 2009 Origin Enterprise Solution LTD.		   
#																			   
#=============================================================================
# File name: ImportProgramHandler.java
# File description: 										   	
#=============================================================================
#	Date		Name		Action	Description of Change					   
#	2013-2-25	liuc  	Ini		³õÊ¼»¯		
#	2013-3-4	liuc  	modify		ÐÞ¸Ä		
#	2013-3-13	zhanggl  	modify	get JCI6_ProgramInfoType LOV and JCI6_Product
                                    LOV for check Type column and Product column.
                                    ItemRevision update.
                                    Remove item_id rule.		
                                    EBP double.					   
#=============================================================================
 */
package com.yfjcebp.importdata.importprogram;

import java.io.File;

import com.teamcenter.rac.kernel.TCComponent;
import com.yfjcebp.importdata.AbstractImportDataHandler;
import com.yfjcebp.importdata.utils.OriginUtil;

public class ImportProgramHandler extends AbstractImportDataHandler {



	

	/*
	 * @see
	 * com.yfjcebp.importdata.AbstractImportDataHandler#checkImportFile(com.
	 * teamcenter.rac.kernel.TCComponent)
	 */
	@Override
	protected File checkImportFile(TCComponent component) {
		file = OriginUtil.checkImportFile(component);
		return file;
	}

	/*
	 * @see com.yfjcebp.importdata.AbstractImportDataHandler#doImport()
	 */
	@Override
	protected void doImport() {
		
		session.setStatus("import project data£¬waiting...");
		session.queueOperation(new ImportProgramOperation(file,session));
		session.setReadyStatus();
		
		
	}

}
