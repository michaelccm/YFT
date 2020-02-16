package com.teamcenter.custwork.export;

import java.io.File;

import com.teamcenter.rac.aif.AbstractAIFOperation;
import com.teamcenter.rac.kernel.TCComponentFormType;
import com.teamcenter.rac.kernel.TCSession;


public class ExportDataOperation extends AbstractAIFOperation {

	private ExportProgressDialog processDialog = null;

	private TCComponentFormType formType;

	private TCSession session;


	public ExportDataOperation(ExportProgressDialog processDialog) {
		super();
		this.processDialog = processDialog;
	}

	@Override
	public void executeOperation() throws Exception {
		ExcelOperation excelOperation = processDialog.excelOperation;
		File file = processDialog.file;
		if(file != null){
			excelOperation.exportExcel(file,this.processDialog);
		}
	}

}
