package com.teamcenter.custwork.query;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.teamcenter.custwork.toolkit.IWorkManagerTookit;
import com.teamcenter.rac.aif.InterfaceAIFOperationExecutionListener;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.common.AbstractProgressDialog;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.Registry;

public class QueryProgressDialog extends AbstractProgressDialog {
	
	private int n = 0;	
	public TCSession session;	
	public AIFComponentContext[] targets;	
	public boolean confirmationFlag = true;	
	public IWorkManagerTookit processTookit = null;
	private String title = null;
	private QueryProgressOperation operation;
	
	public QueryProgressDialog(IWorkManagerTookit processTookit,String title,
			TCSession session,TCComponent targ) throws TCException{
		this.session = session;	
		this.processTookit = processTookit;
		targets = new AIFComponentContext[1];
		targets[0] = new AIFComponentContext(null, 
				session.getUser(), session.getUser());
		this.title = title;
		confirmationFlag = false;
		initializeDialog();
	}
	

	private void initializeDialog(){
		setTitle(title);
		setCommandIcon(getReg().getImageIcon("transfermode_16.png"));
		setSuccessIcon(getReg().getImageIcon("transfermode_16.png"));		
		setConfirmationText("完成");
		setDisplaySuccessComponents(confirmationFlag);
		setTCComponents(targets);
		setConfirmationFlag(confirmationFlag);
		this.stopButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				//MessageBox.post("数据导出结束", "提示", MessageBox.INFORMATION);
			//	operation.setAbortRequested(true);
			}
			
		});
	}
	
	public void setProgressBarState(String s,int i){	
		progressBar.setValue(i);
		progressBar.setString(s);
		System.out.println(s);
		session.setStatus(s);
	}
	
	public void setProgressBarMaxiMum(int n){
		progressBar.setMaximum(n);
	}
	
	@Override
	protected void getOperations(AIFComponentContext aifcomponentcontext) {
		operation = new QueryProgressOperation(this);
		operation.addOperationListener(new InterfaceAIFOperationExecutionListener(){
			@Override
			public void startOperation(String s1){		
			
			}
			@Override
			public void endOperation(){
			
			}	
				
			@Override
			public void exceptionThrown(Exception exception){
				MessageBox.post(exception);
				exception.printStackTrace();
			}
		});
		addOperation(operation);
	}	

	@Override
	protected Registry getReg(){
		return Registry.getRegistry(this);
	}

	
}
