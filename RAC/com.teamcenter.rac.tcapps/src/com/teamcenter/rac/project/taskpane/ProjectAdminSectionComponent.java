/*** Eclipse Class Decompiler plugin, copyright (c) 2012 Chao Chen (cnfree2000@hotmail.com) ***/
package com.teamcenter.rac.project.taskpane;

import com.teamcenter.rac.aif.IPerspectiveDefService;
import com.teamcenter.rac.aif.navigationpane.impl.SectionComponent;
import java.awt.event.ActionEvent;
import org.apache.log4j.Logger;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;

public class ProjectAdminSectionComponent extends SectionComponent {
	public void executeAction(ActionEvent paramActionEvent) {
		System.out.println("====ProjectAdminSectionComponent::executeAction======");
		postPerspective("com.teamcenter.rac.project.ProjectPerspective");
	}

	public void postPerspective(String paramString) {
		try {
			System.out.println("ProjectAdminSectionComponent::postPerspective--->"+paramString);
			
			IWorkbench localIWorkbench = PlatformUI.getWorkbench();
			System.out.println("localIWorkbench--->"+localIWorkbench);
			
			if (localIWorkbench == null)
				return;
			
			
			if (!(getPerspectiveDefService()
					.isTypeOfPerspectiveActive(paramString)))
				localIWorkbench.showPerspective(paramString,
						localIWorkbench.getActiveWorkbenchWindow());
			localIWorkbench.getActiveWorkbenchWindow().getActivePage()
					.showView("com.teamcenter.rac.project.ProjectView");
		} catch (WorkbenchException localWorkbenchException) {
			Logger.getLogger(ProjectAdminSectionComponent.class).error(
					localWorkbenchException.getLocalizedMessage(),
					localWorkbenchException);
		}
	}
}