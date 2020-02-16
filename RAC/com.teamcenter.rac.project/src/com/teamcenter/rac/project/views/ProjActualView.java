package com.teamcenter.rac.project.views;

import com.teamcenter.rac.kernel.SessionChangedEvent;
import com.teamcenter.rac.kernel.SessionChangedListener;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentProject;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.project.nodes.ProjectRootNode;
import com.teamcenter.rac.ui.common.RACUIUtil;
import com.teamcenter.rac.ui.views.AbstractContentViewPart;
import com.teamcenter.rac.util.AdapterUtil;
import com.teamcenter.rac.util.EmbeddedComposite;
import com.teamcenter.rac.util.SWTUIUtilities;
import com.teamcenter.rac.vns.model.IContentView;
import com.yfjcebp.projectmanager.actual.ProjectActualPanel;
import com.yfjcebp.projectmanager.budget.ProgressBarThread;
import com.yfjcebp.projectmanager.budget.ProjectProgressBar;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import javax.swing.SwingUtilities;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPart;

public class ProjActualView extends AbstractContentViewPart implements
		SessionChangedListener, IContentView {
	private Frame m_awtframe;
	private TCSession session;
	private ProjectActualWrapper amPanel = null;
	private Composite m_parent;
	private Composite m_composite;
	private TCComponent current_program;
	private HashMap<String, ProjectActualWrapper> mapsAll = new HashMap();
	TCComponent[] costInfos = null; 
	int isFlag = 0;

	public ProjActualView() {
		System.out.println("===Enter ProjActualView===");

		setMultiSelectionSupported(false);
		setProcessNullSelectionsEnabled(true);
		setComponentEventListeningEnabled(true);

		setUseInitialInputJobEnabled(false);
		setUISupportsAssociate(true);
		setUISupportsSetScope(true);
		setUISupportsViewLocking(true);
	}

	protected void createContent(Composite paramComposite) {
		System.out.println("===Enter createContent===");
		this.session = RACUIUtil.getTCSession();

		this.m_parent = paramComposite;
		StackLayout localStackLayout = new StackLayout();
		this.m_parent.setLayout(localStackLayout);
		this.m_composite = new EmbeddedComposite(this.m_parent, 0);
		this.m_composite.setLayout(new FillLayout());
		this.m_awtframe = SWT_AWT.new_Frame(this.m_composite);
		this.m_awtframe.setLayout(new BorderLayout());
		setViewContent(null);
	}

	public boolean isValidSelectedObjectForViewPart(
			IWorkbenchPart paramIWorkbenchPart, Object paramObject) {
		System.out.println("===Enter isValidSelectedObjectForViewPart===");
		if (AdapterUtil.getAdapter(paramObject, ProjectRootNode.class) != null) {
			makeViewBlank(paramIWorkbenchPart, null, false);
		}
		this.current_program = null;
		System.out.println("paramObject-->" + paramObject);
		if (paramObject != null) {
			if ((paramObject instanceof TCComponentProject)) {
				System.out.println("tree select TCComponentProject");
				TCComponentProject currentproject = (TCComponentProject) paramObject;
				try {
					TCComponent[] preferred_Items = currentproject
							.getReferenceListProperty("TC_Program_Preferred_Items");
					int project_dataLength = preferred_Items.length;
					System.out
							.println("TC_Program_Preferred_Items   length--->"
									+ project_dataLength);
					for (int i = 0; i < project_dataLength; i++) {
						TCComponent tccomponent = preferred_Items[i];
						if (tccomponent.getType().equals("JCI6_ProgramInfo")) {
							this.current_program = ((TCComponentItem) tccomponent);
							break;
						}
					}
				} catch (TCException e) {
					e.printStackTrace();
				}
			} else if ((paramObject instanceof TCComponentItem)) {
				System.out.println("tree select TCComponentItem");

				if (((TCComponentItem) paramObject).getType().equals(
						"JCI6_ProgramInfo")) {
					this.current_program = ((TCComponentItem) paramObject);
				}
			} else if ((paramObject instanceof TCComponentItemRevision)) {
				System.out.println("tree select TCComponentItemRevision");

				if (((TCComponentItemRevision) paramObject).getType().equals(
						"JCI6_ProgramInfoRevision")) {
					TCComponentItemRevision programRev = (TCComponentItemRevision) paramObject;
					try {
						this.current_program = programRev.getItem();
					} catch (TCException e) {
						e.printStackTrace();
					}
				}
			}
		}

		return true;
	}

	protected void makeViewBlank(IWorkbenchPart paramIWorkbenchPart,
			List<Object> paramList, boolean paramBoolean) {
		System.out.println("===Enter makeViewBlank===");

		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				ProjActualView.this.m_awtframe.removeAll();
				ProjActualView.this.m_awtframe.validate();
				ProjActualView.this.m_awtframe.repaint();
				((StackLayout) ProjActualView.this.m_parent.getLayout()).topControl = ProjActualView.this.m_composite;
				ProjActualView.this.m_parent.layout();
			}
		});
	}

	protected void processSetInput(IWorkbenchPart paramIWorkbenchPart,
			List<Object> paramList) {
		System.out.println("===Enter processSetInput===");
		setInputObjects(paramIWorkbenchPart, paramList);
		try {
			Object localObject = paramList.isEmpty() ? null : paramList.get(0);
			System.out.println("la1 localObject-->" + localObject);

			if ((localObject instanceof ProjectRootNode)) {
				makeViewBlank(paramIWorkbenchPart, paramList, false);
				System.out.println("localObject-->return ");
				return;
			}

			TCComponent localTCComponent = RACUIUtil.getComponent(localObject);
			System.out.println("la1 localTCComponent-->" + localTCComponent);
			if ((localTCComponent != null)
					&& (((localTCComponent instanceof TCComponentProject))
							|| (localTCComponent.getType()
									.equals("JCI6_ProgramInfo")) || (localTCComponent
								.getType().equals("JCI6_ProgramInfoRevision")))) {
				makeViewBlank(paramIWorkbenchPart, paramList, false);
				setInputObject(paramIWorkbenchPart, localTCComponent);
				setViewContent(localTCComponent);
				return;
			}

			makeViewBlank(paramIWorkbenchPart, paramList, false);
		} catch (Exception localException) {
			localException.printStackTrace();
		}
	}
	private void setViewContent(TCComponent paramTCComponent) {
		if (paramTCComponent == null)
			return;
		System.out.println("qq===ProjActualView 123 setViewContent==="
				+ paramTCComponent);

		setCurrentProgramItem(paramTCComponent);
		this.amPanel = new ProjectActualWrapper(this.session, paramTCComponent);
		
		System.out.println("this.amPanel.getProgramInfoItem()-->"+this.amPanel.getProgramInfoItem());
		if(this.amPanel.getProgramInfoItem()!=null){
			try {
				costInfos = this.amPanel.getProgramInfoItem().getRelatedComponents("IMAN_external_object_link");
			} catch (TCException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();  
			}
		}
		
		
		//获取数据进度条
		IRunnableWithProgress runnable = new IRunnableWithProgress(){
            public void run(IProgressMonitor monitor) {
            	m_awtframe.removeAll();
                m_awtframe.add(ProjActualView.this.amPanel,"Center");
                m_awtframe.validate();
				m_awtframe.repaint();
            	if (costInfos != null) {
    				int costInfoLength = costInfos.length;
    				amPanel.getVecCostInfo().clear();
    				if (costInfoLength > 0) {
    					monitor.beginTask("complete...", costInfoLength);
    	                for (int i = 0; i < costInfoLength; i++) {// 循环10次，每次间隔一秒
    	                    if (monitor.isCanceled()) // 随时监控是否选择了对话框的“取消”按钮
    	                        return;// 中断处理
    	                    try {
    	                    	TCComponent costInfo = costInfos[i];
        						if (costInfo.getType().equals("JCI6_CostInfo")) {
        							String jci6_CPT = costInfo.getProperty("jci6_CPT");
        							if (jci6_CPT.equals(ProjectActualPanel.reg.getString("jci6_CPT1.NAME")) || jci6_CPT.equals(ProjectActualPanel.reg.getString("jci6_CPT2.NAME"))){
        								amPanel.getVecCostInfo().add(costInfo);
        							}
        						}
    	                    } catch (Throwable t) {}
    	                  
    	                }
    	                
    	                //monitor.setTaskName("正在执行界面刷新");// 提示信息
    	                
    	                
    					//填充数据
    					amPanel.sotosfillOriginalDataToTable(monitor);
    					// monitor.setTaskName("正在执行第"+i+"条");// 提示信息
  	                   // monitor.worked(1);// 进度条前进一步
    				
    					
    					m_awtframe.validate();
    					m_awtframe.repaint();
    	                monitor.done();// 进度条前进到完成
    				}
    			}
            	

            }
        };
        try {
            // 创建一个进度条对话传框，并将runnable入
            // 第一个参数推荐设为true，如果设为false则处理程序会运行在UI线程里，界面将有一点停滞感。
            // 第二个参数：true＝对话框的“取消”按钮有效
        	ProgressMonitorDialog dialog=  new ProgressMonitorDialog(m_parent.getShell());
        	dialog.run(true, false, runnable);
        } catch (Exception e2) {
            e2.printStackTrace();
        }
		/*Runnable local3 = new Runnable() {
			public void run() {
				ProjActualView.this.m_awtframe.removeAll();

				//ProjActualView.this.amPanel.refreshData();
				//填充数据
				amPanel.sotosfillOriginalDataToTable();

				ProjActualView.this.m_awtframe.add(ProjActualView.this.amPanel,"Center");

				ProjActualView.this.m_awtframe.validate();
				ProjActualView.this.m_awtframe.repaint();
			}
		};
		SwingUtilities.invokeLater(local3);*/

		Runnable local4 = new Runnable() {
			public void run() {
				((StackLayout) ProjActualView.this.m_parent.getLayout()).topControl = ProjActualView.this.m_composite;
				ProjActualView.this.m_parent.layout();
			}
		};
		SWTUIUtilities.asyncExec(local4);
	}

	private void setCurrentProgramItem(TCComponent paramTCComponent) {
		System.out.println("===Enter setCurrentProject===");
		this.current_program = paramTCComponent;
	}

	private TCComponent getCurrentProgramItem() {
		System.out.println("===Enter setViewContent===");
		return this.current_program;
	}

	public void sessionChanged(SessionChangedEvent arg0) {
	}

	class ProjectActualWrapper extends ProjectActualPanel {
		private static final long serialVersionUID = 1L;

		public ProjectActualWrapper(TCSession paramTCSession,
				TCComponent programInfoItem) {
			super();
			System.out.println("===Enter ProjectActualWrapper===");
			setProgramInfoItem2(programInfoItem);
		}

//		public void refreshData() {
//			ProjectProgressBar bar = new ProjectProgressBar(this,
//					"please waiting...", 1000000);
//			bar.setTitle("Actual");
//			bar.initUI();
//
//		}
	}
}