package com.teamcenter.custwork.query;
import com.teamcenter.custwork.toolkit.IWorkManagerTookit;
import com.teamcenter.rac.aif.AbstractAIFOperation;
public class QueryProgressOperation extends AbstractAIFOperation {
	
	private QueryProgressDialog processDialog = null;

	public QueryProgressOperation(QueryProgressDialog processDialog){
		this.processDialog = processDialog;
	}
	
	@Override
	public void executeOperation() throws Exception {
	
		IWorkManagerTookit tookit = processDialog.processTookit;
		if(tookit != null){
			tookit.seach(processDialog);
		}

	}

}
