package com.teamcenter.custwork.saves;
import com.teamcenter.custwork.toolkit.IWorkManagerTookit;
import com.teamcenter.rac.aif.AbstractAIFOperation;
public class SaveProgressOperation extends AbstractAIFOperation {
	
	private SaveProgressDialog processDialog = null;

	public SaveProgressOperation(SaveProgressDialog processDialog){
		this.processDialog = processDialog;
	}
	
	@Override
	public void executeOperation() throws Exception {
	
		IWorkManagerTookit tookit = processDialog.processTookit;
		if(tookit != null)
		{
                   if (processDialog.action_ != null && processDialog.action_.equals (SaveProgressDialog.ACT_SAVEALL))
                   {
			tookit.saveAll(processDialog);
                   }
                   else if (processDialog.action_ != null && processDialog.action_.equals (SaveProgressDialog.ACT_SAVE))
                   {
			tookit.save(processDialog);
                   }
		   else if (processDialog.action_ != null && processDialog.action_.equals (SaveProgressDialog.ACT_REVISE))
                   {
                        tookit.revise (processDialog);
                   }			    
		}

	}

}
