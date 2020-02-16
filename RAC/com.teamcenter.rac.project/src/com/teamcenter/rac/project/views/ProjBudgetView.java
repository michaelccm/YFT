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
import com.yfjcebp.projectmanager.budget.ProjectBudgetPanel;
import com.yfjcebp.projectmanager.budget.ProjectProgressBar;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

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

public class ProjBudgetView extends AbstractContentViewPart implements
		IContentView {
	private Frame m_awtframe;
	private TCSession session;
	private ProjectBudgetWrapper amPanel = null;
	private Composite m_parent;
	private Composite m_composite;
	private TCComponent current_program;
	private HashMap<String, ProjectBudgetWrapper> mapsAll = new HashMap();

	int isFlag = 0;
	//public Vector<TCComponent> VecCostInfo = new Vector<TCComponent>();
	
	public ProjBudgetView() {
		System.out.println("===Enter ProjResourceView====");
		setMultiSelectionSupported(false);
		setProcessNullSelectionsEnabled(true);
		setComponentEventListeningEnabled(true);

		setIgnoreSelectionFromPartsNotInActivePage(false);
		setUseInitialInputJobEnabled(false);
		setUISupportsAssociate(false);
		setUISupportsSetScope(true);
		setUISupportsViewLocking(true);
		
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
				ProjBudgetView.this.m_awtframe.removeAll();
				ProjBudgetView.this.m_awtframe.validate();
				ProjBudgetView.this.m_awtframe.repaint();
				((StackLayout) ProjBudgetView.this.m_parent.getLayout()).topControl = ProjBudgetView.this.m_composite;
				ProjBudgetView.this.m_parent.layout();
			}
		});
	}

	protected void processSetInput(IWorkbenchPart paramIWorkbenchPart,
			List<Object> paramList) {
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
			System.out.println("la22 localTCComponent-->" + localTCComponent);
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

	private void setViewContent(TCComponent paramTCComponent){
		if (paramTCComponent == null) {
			return;
		}
		System.out.println("qq===ProjBudgetView 123 setViewContent==="
				+ paramTCComponent);
		setCurrentProgramItem(paramTCComponent);

		this.amPanel = new ProjectBudgetWrapper(this.session, paramTCComponent);
		
		
		
		IRunnableWithProgress runnable = new IRunnableWithProgress(){
			@Override
			public void run(IProgressMonitor monitor)
					throws InvocationTargetException, InterruptedException {
				// TODO Auto-generated method stub
				ProjBudgetView.this.m_awtframe.removeAll();
				ProjBudgetView.this.m_awtframe.add(ProjBudgetView.this.amPanel,
						"Center");
				ProjBudgetView.this.m_awtframe.validate();
				ProjBudgetView.this.m_awtframe.repaint();
				
				amPanel.VecCostInfo.clear();
				
				try{
					if(amPanel.getProgramInfo()!=null){
						TCComponent	programInfo=amPanel.getProgramInfo();
						if (programInfo instanceof TCComponentItem) {
							TCComponentItem programInfoItem = (TCComponentItem) programInfo;
							TCComponent[] revisions = programInfoItem.getReferenceListProperty("revision_list");
							int revNum = revisions.length;
							for (int i = 0; i < revNum; i++) {
								if (revisions[i].getTCProperty("jci6_BudgetState").getStringValue().equals("State4")) {
									System.out.println("revisions[i]:"+revisions[i]+"  jci6_BudgetState-->State4");
									TCComponent[] costInfos = revisions[i].getRelatedComponents("IMAN_external_object_link");
									addDataToList(costInfos);
								}
							}
						} else {
							TCComponent[] costInfos = programInfo.getRelatedComponents("IMAN_external_object_link");
							addDataToList(costInfos);
						}
					}
				}catch(TCException e){
					e.printStackTrace();
				}
				monitor.beginTask("complete...", amPanel.VecCostInfo.size());
				
				//amPanel.VecCostInfo=VecCostInfo;
				System.out.println("amPanel.VecCostInfo.size()-->"+amPanel.VecCostInfo.size());
				
				amPanel.fillOriginalDataToTable(monitor);
				
				ProjBudgetView.this.m_awtframe.validate();
				ProjBudgetView.this.m_awtframe.repaint();
				
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
//				ProjBudgetView.this.m_awtframe.removeAll();
//				ProjBudgetView.this.m_awtframe.add(ProjBudgetView.this.amPanel,
//						"Center");
//				
//				
//			
//				ProjBudgetView.this.amPanel.refreshData();
//				ProjBudgetView.this.m_awtframe.validate();
//				ProjBudgetView.this.m_awtframe.repaint();
//				
//			}
//		};
//		SwingUtilities.invokeLater(local3);

		Runnable local4 = new Runnable() {
			public void run() {
				((StackLayout) ProjBudgetView.this.m_parent.getLayout()).topControl = ProjBudgetView.this.m_composite;
				ProjBudgetView.this.m_parent.layout();
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

	class ProjectBudgetWrapper extends ProjectBudgetPanel {
		public ProjectBudgetWrapper(TCSession paramTCSession,
				TCComponent project) {
			super();
			setProgramInfoComponent(project);
		}
		
//		public void refreshData() {
//			ProjectProgressBar bar = new ProjectProgressBar(this,
//					"please waiting...", 1000000);
//			bar.setTitle("Budget");
//			bar.initUI();
//
//			// ProgressBarThread barThread=new
//			// ProgressBarThread("","please waiting...",ProjectBudgetWrapper.this,
//			// 1000000);
//			// barThread.start();
//		}
	}
	
	public void addDataToList(TCComponent[] costInfos) {
		if (costInfos != null) {
			int costInfoLength = costInfos.length;
			System.out.println("costInfoLength=" + costInfoLength);
			//logger.debug("find " + costInfoLength + " CostInfo Object...");
			if (costInfoLength > 0) {
				for (int i = 0; i < costInfoLength; i++) {
					TCComponent costInfo = costInfos[i];
					if (costInfo.getType().equals("JCI6_CostInfo")) {
						try {
							String key1 = ProjectBudgetPanel.reg.getString("jci6_CPT.NAME");
							//System.out.println("jci6_CPT.NAME--->"+key1);
							if (costInfo.getProperty("jci6_CPT").equals(key1)) {
								amPanel.VecCostInfo.add(costInfos[i]);
							}
						} catch (TCException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}
	
	
}