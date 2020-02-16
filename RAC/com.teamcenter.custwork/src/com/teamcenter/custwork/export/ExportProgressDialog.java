package com.teamcenter.custwork.export;

import java.io.File;
import com.teamcenter.rac.aif.InterfaceAIFOperationExecutionListener;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.common.AbstractProgressDialog;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.Registry;

public class ExportProgressDialog extends AbstractProgressDialog {
	
	private int n = 0;	
	
	public TCSession session;	
	
	public File file = null;	
	
	public AIFComponentContext[] targets;	
	
	public boolean confirmationFlag = true;		
	
	private String title = null;
	
	public ExcelOperation excelOperation = null;
	
	public ExportProgressDialog(ExcelOperation excelOperation, String title,
			TCSession session,TCComponent targ,File file) throws TCException{
		this.session = session;	
		this.file = file;	
		this.title = title;
		this.excelOperation = excelOperation;
		targets = new AIFComponentContext[1];
		targets[0] = new AIFComponentContext(null, 
				session.getUser(), session.getUser());
		initializeDialog();
	}
	

	private void initializeDialog(){
		setTitle(title);
		setCommandIcon(getReg().getImageIcon("transfermode_16.png"));
		setSuccessIcon(getReg().getImageIcon("transfermode_16.png"));
		if(confirmationFlag)
			setConfirmationText("是否进行导出操作?");
		else
			setConfirmationText("完成");
		setDisplaySuccessComponents(confirmationFlag);
		setTCComponents(targets);
		setConfirmationFlag(confirmationFlag);
	}
	
	public void setProgressBarState(String s,int i){	
		progressBar.setValue(i);
		progressBar.setString(s);
		session.setStatus(s);
	}
	
	public void setProgressBarMaxiMum(int n){
		progressBar.setMaximum(n);
	}
	
	
	@Override
	protected void getOperations(AIFComponentContext aifcomponentcontext) {
		final ExportDataOperation importparameter = new ExportDataOperation(this);
		importparameter.addOperationListener(new InterfaceAIFOperationExecutionListener(){
			@Override
			public void startOperation(String s1){		
			
			}
			@Override
			public void endOperation(){
				//MessageBox.post("数据导出结束", "提示", MessageBox.INFORMATION);
				
			}	
				
			@Override
			public void exceptionThrown(Exception exception){
				MessageBox.post(exception);
				exception.printStackTrace();
			}
		});
		addOperation(importparameter);
	}	

	@Override
	protected Registry getReg(){
		return Registry.getRegistry(this);
	}
}
