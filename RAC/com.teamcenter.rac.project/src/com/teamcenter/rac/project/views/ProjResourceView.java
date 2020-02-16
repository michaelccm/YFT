package com.teamcenter.rac.project.views;

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
import com.yfjcebp.projectmanager.budget.ProjectProgressBar;
import com.yfjcebp.projectmanager.forecast.ProjectResourcePanel;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
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

public class ProjResourceView extends AbstractContentViewPart implements
		IContentView {
	private Frame m_awtframe;
	private TCSession session;
	private ProjectResourceWrapper amPanel = null;
	private Composite m_parent;
	private Composite m_composite;
	private TCComponent current_program;
	
	TCComponent[] costInfos = null; 

	public ProjResourceView() {
		setMultiSelectionSupported(false);
		setProcessNullSelectionsEnabled(true);
		setComponentEventListeningEnabled(true);

		setUseInitialInputJobEnabled(false);
		setUISupportsAssociate(true);
		setUISupportsSetScope(true);
		setUISupportsViewLocking(true);
		System.out.println("===Enter ProjResourceView====");
	}

	
	protected void createContent(Composite paramComposite) {
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
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				ProjResourceView.this.m_awtframe.removeAll();
				ProjResourceView.this.m_awtframe.validate();
				ProjResourceView.this.m_awtframe.repaint();
				((StackLayout) ProjResourceView.this.m_parent.getLayout()).topControl = ProjResourceView.this.m_composite;
				ProjResourceView.this.m_parent.layout();
			}
		});
	}

	protected void processSetInput(IWorkbenchPart paramIWorkbenchPart,
			List<Object> paramList) {
		setInputObjects(paramIWorkbenchPart, paramList);
		try {
			Object localObject = paramList.isEmpty() ? null : paramList.get(0);
			System.out.println("processSetInput localObject-->" + localObject);

			if ((localObject instanceof ProjectRootNode)) {
				makeViewBlank(paramIWorkbenchPart, paramList, false);
				return;
			}

			TCComponent localTCComponent = RACUIUtil.getComponent(localObject);
			System.out.println("processSetInput localTCComponent-->"
					+ localTCComponent);
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
		if (paramTCComponent == null) {
			return;
		}
		System.out.println("qq===ProjResourceView 123 setViewContent==="
				+ paramTCComponent);

	
		this.amPanel = new ProjectResourceWrapper(this.session,
				paramTCComponent);
		
		System.out.println("this.amPanel.getProgramInfoItem()-->"+this.amPanel.getProgramInfoItem());

		if(this.amPanel.getProgramInfoItem()!=null){
			try {
				costInfos = this.amPanel.getProgramInfoItem().getRelatedComponents("IMAN_external_object_link");
			} catch (TCException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		}
		
		IRunnableWithProgress runnable = new IRunnableWithProgress(){
			@Override
			public void run(IProgressMonitor monitor){
				// TODO Auto-generated method stub
				ProjResourceView.this.m_awtframe.removeAll();
				ProjResourceView.this.m_awtframe.add(
						ProjResourceView.this.amPanel, "Center");
				
				ProjResourceView.this.m_awtframe.validate();
				ProjResourceView.this.m_awtframe.repaint();
				if (costInfos != null) {
					int costInfoLength = costInfos.length;
    				amPanel.getVecCostInfo().clear();
    				
    				if (costInfoLength > 0) {
    					monitor.beginTask("complete...", costInfoLength);
    					for (int i = 0; i < costInfoLength; i++) {
    						 if (monitor.isCanceled()) // 随时监控是否选择了对话框的“取消”按钮
     	                        return;// 中断处理
    						 
    						 TCComponent costInfo = costInfos[i];
    						 try {
    							 if (costInfo.getType().equals("JCI6_CostInfo")) {
    									if (costInfo.getProperty("jci6_CPT").equals(ProjectResourcePanel.reg.getString("jci6_CPT.NAME"))) {
    										amPanel.getVecCostInfo().add(costInfo);
    									}
    								} 
    						 }catch (Throwable t) {}
    					}
    				}
				}
				
				amPanel.sotosfillOriginalDataToTable(monitor);
				m_awtframe.validate();
				m_awtframe.repaint();
                monitor.done();// 进度条前进到完成
			}
		};
		
		 try {
	            // 创建一个进度条对话传框，并将runnable入
	            // 第一个参数推荐设为true，如果设为false则处理程序会运行在UI线程里，界面将有一点停滞感。
	            // 第二个参数：true＝对话框的“取消”按钮有效
	            new ProgressMonitorDialog(m_parent.getShell()).run(true, false, runnable);
	        } catch (Exception e2) {
	            e2.printStackTrace();
	        }
		
//		Runnable local3 = new Runnable() {
//			public void run() {
//				ProjResourceView.this.m_awtframe.removeAll();
//				ProjResourceView.this.m_awtframe.add(
//						ProjResourceView.this.amPanel, "Center");
//				
//				
//				ProjResourceView.this.m_awtframe.validate();
//				ProjResourceView.this.m_awtframe.repaint();
//				
//				ProjResourceView.this.amPanel.refreshData();
//				ProjResourceView.this.m_awtframe.validate();
//				ProjResourceView.this.m_awtframe.repaint();
//			}
//		};
//		SwingUtilities.invokeLater(local3);

		Runnable local4 = new Runnable() {
			public void run() {
				((StackLayout) ProjResourceView.this.m_parent.getLayout()).topControl = ProjResourceView.this.m_composite;
				ProjResourceView.this.m_parent.layout();
			}
		};
		SWTUIUtilities.asyncExec(local4);
	}

	private void setCurrentProgramItem(TCComponent paramTCComponent) {
		this.current_program = paramTCComponent;
	}

	private TCComponent getCurrentProgramItem() {
		return this.current_program;
	}

	class ProjectResourceWrapper extends ProjectResourcePanel {
		private static final long serialVersionUID = 1L;

		public ProjectResourceWrapper(TCSession paramTCSession,
				TCComponent programInfoItem) {
			super();
			setProgramInfoItem2(programInfoItem);
		}

//		public void refreshData() {
//			ProjectProgressBar bar = new ProjectProgressBar(this,
//					"please waiting...", 1000000);
//			bar.setTitle("Forecast");
//			bar.initUI();
//		}
	}
}