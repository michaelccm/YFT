package com.yfjc.workdayhourform;

import java.awt.BorderLayout;
import java.awt.Dimension;
import com.teamcenter.rac.aif.AbstractAIFDialog;
import com.teamcenter.rac.kernel.TCComponentGroup;
import com.teamcenter.rac.kernel.TCComponentTaskTemplate;
import com.teamcenter.rac.kernel.TCSession;

public class WorkdayHourDialog extends AbstractAIFDialog{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private TCComponentTaskTemplate taskTemplate;
	private TCSession session ;
	private TCComponentGroup divisionGroup;
	private String divisionName;

		
	public WorkdayHourDialog(TCSession session,TCComponentTaskTemplate taskTemplate,String divisionName,TCComponentGroup divisionGroup){
		super(true);
		this.taskTemplate = taskTemplate;
		this.session =session;
		this.divisionName = divisionName;
		this.divisionGroup = divisionGroup;
	} 
	
	public void initDialog() throws Exception{
		setLayout(new BorderLayout());
		setTitle("\u5916\u5305\u5DE5\u65F6\u586B\u62A5");
		WorkdayHourPanel pane = new WorkdayHourPanel(this,session,taskTemplate,divisionName,divisionGroup);
		add(pane,BorderLayout.CENTER);
		setPreferredSize(new Dimension(1100,400));
		centerToScreen();
		setVisible(true);
	} 
}
