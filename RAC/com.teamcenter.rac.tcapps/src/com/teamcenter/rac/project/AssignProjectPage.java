package com.teamcenter.rac.project;

import com.teamcenter.rac.aifrcp.AppThemeHelper;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentProject;
import com.teamcenter.rac.kernel.TCComponentProjectType;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.providers.delegates.DefaultLabelProviderDelegate;
import com.teamcenter.rac.services.ISessionService;
import com.teamcenter.rac.tcapps.TcappsPlugin;
import com.teamcenter.rac.util.AdapterUtil;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.Registry;
import com.teamcenter.rac.util.event.ClientEventDispatcher;
import com.teamcenter.rac.util.wizard.extension.BaseExternalWizardPage;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.apache.log4j.Logger;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

public class AssignProjectPage extends BaseExternalWizardPage
  implements EventHandler
{
  private static final Logger m_logger = Logger.getLogger(AssignProjectPage.class);
  private static Registry m_reg = Registry.getRegistry(AssignProjectPage.class);
  private ListViewer m_availableProjListViewer;
  //private SelectionViewerComposite m_assignableComposite;
  private MySelectionViewerComposite m_assignableComposite;
  private ServiceRegistration m_eventServiceRegistration;
  private TCComponentProject[] m_availableProjects;
  private TCComponentProject[] m_selectedProjects;
  private static final String TC_PROJECTS = "TC_Project";

  public AssignProjectPage(String paramString1, String paramString2, String paramString3, ImageDescriptor paramImageDescriptor, TCComponentProject[] paramArrayOfTCComponentProject1, TCComponentProject[] paramArrayOfTCComponentProject2, int paramInt)
  {
    super(paramString1, paramString2, paramString3, paramImageDescriptor, paramInt);
    System.out.println("initialize");
    if(m_availableProjects!=null && m_availableProjects.length > 0)
    	System.out.println("get paramArrayOfTCComponentProject1 "+ m_availableProjects.length);
    else
    	System.out.println("get no  paramArrayOfTCComponentProject1");
    
    
    if(m_selectedProjects!=null && m_selectedProjects.length > 0)
    	System.out.println("get paramArrayOfTCComponentProject2 "+ m_selectedProjects.length);
    else
    	System.out.println("get no  paramArrayOfTCComponentProject2");
    System.out.println("get paramArrayOfTCComponentProject2 ");
    this.m_availableProjects = paramArrayOfTCComponentProject1;
    this.m_selectedProjects = paramArrayOfTCComponentProject2;
    this.m_eventServiceRegistration = ClientEventDispatcher.addEventHandler(this, new String[] { "com/teamcenter/rac/newObjectCreated" });
  }

  public void createControl(Composite paramComposite)
  {
	System.out.println("createControl");
    FormToolkit localFormToolkit = new FormToolkit(paramComposite.getDisplay());
    Composite localComposite1 = localFormToolkit.createComposite(paramComposite);
    localComposite1.setLayout(new GridLayout(1, false));
    try
    {
      TCSession localTCSession = (TCSession)TcappsPlugin.getSessionService().getSession(TCSession.class.getName());
      Composite localComposite2 = new Composite(localComposite1, 0);
      localComposite2.setBackground(AppThemeHelper.getTreeBackground());
      localComposite2.setLayout(new GridLayout(2, false));
      localComposite2.setLayoutData(new GridData(16384, 16777216, true, false, 1, 1));
      Label localLabel1 = new Label(localComposite2, 0);
      localLabel1.setBackground(AppThemeHelper.getTreeBackground());
      localLabel1.setText(MessageFormat.format(m_reg.getString("ProjectPageOwningProject.LABEL"), new Object[] { TCComponentProject.getDisplayName(localTCSession) }));
      Label localLabel2 = new Label(localComposite2, 0);
      TCComponentProject localTCComponentProject = localTCSession.getCurrentProject();
      if (localTCComponentProject != null)
      {
        localLabel2.setText(localTCComponentProject.toString());
        localLabel2.setBackground(AppThemeHelper.getTreeBackground());
      }
      //this.m_assignableComposite = new SelectionViewerComposite(localComposite1, new IC_ViewerConfigurator());
      this.m_assignableComposite = new MySelectionViewerComposite(localComposite1,new IC_ViewerConfigurator());
      this.m_availableProjListViewer = new ListViewer(this.m_assignableComposite.getComposite(), 2818);
      this.m_availableProjListViewer.setContentProvider(new IC_AssignProjectContentProvider());
      DefaultLabelProviderDelegate localDefaultLabelProviderDelegate = new DefaultLabelProviderDelegate();
      localDefaultLabelProviderDelegate.setProperty("object_string");
      this.m_availableProjListViewer.setLabelProvider(localDefaultLabelProviderDelegate);
      this.m_availableProjListViewer.setSorter(new ViewerSorter()
      {
        public int compare(Viewer paramAnonymousViewer, Object paramAnonymousObject1, Object paramAnonymousObject2)
        {
          String str1 = ((TCComponentProject)paramAnonymousObject1).toString();
          String str2 = ((TCComponentProject)paramAnonymousObject2).toString();
          String str3 = str1.toUpperCase();
          String str4 = str2.toUpperCase();
          return str3.compareTo(str4);
        }
      });
      setProjectLists(this.m_availableProjects, this.m_selectedProjects);
      if ((this.m_availableProjects.length > 0) && (this.m_availableProjects[0] != null))
      {
        this.m_availableProjListViewer.setSelection(new StructuredSelection(this.m_availableProjects[0]));
        this.m_availableProjListViewer.getControl().setFocus();
      }
    }
    catch (Exception localException)
    {
      m_logger.error(localException.getLocalizedMessage(), localException);
    }
    setControl(localComposite1);
  }

  public void dispose()
  {
    if (this.m_eventServiceRegistration != null)
    {
      ClientEventDispatcher.removeEventHandler(this.m_eventServiceRegistration);
      this.m_eventServiceRegistration = null;
    }
    super.dispose();
  }

  //YFJC
  public TCComponentProject getMySelectProject(){
	  return m_assignableComposite.getMySelection();
  }
  public void handleEvent(Event paramEvent)
  {
    String str = paramEvent.getTopic();
    if ("com/teamcenter/rac/newObjectCreated".equals(str))
    {
      Object localObject1 = new ArrayList();
      if (this.m_assignableComposite != null)
      {
        ISelection localObject2 = this.m_assignableComposite.getSelection();
        if ((localObject2 instanceof StructuredSelection))
          ((Collection)localObject1).addAll(((StructuredSelection)localObject2).toList());
      }
      else if ((this.m_selectedProjects != null) && (this.m_selectedProjects.length > 0))
      {
        localObject1 = Arrays.asList(this.m_selectedProjects);
      }
      Object localObject2 = localObject1;
      Object localObject3 = paramEvent.getProperty("event.source");
      if (getWizardId().equals(localObject3))
      {
        Object localObject4 = paramEvent.getProperty("com/teamcenter/rac/wizard/resultOfObjectCreation");
        TCComponent localTCComponent = (TCComponent)AdapterUtil.getAdapter(localObject4, TCComponent.class);
        if ((localTCComponent != null) && (localObject2 != null) && (((Collection)localObject2).size() > 0))
        {
          TCComponentProject[] arrayOfTCComponentProject1 = (TCComponentProject[])((Collection)localObject2).toArray(new TCComponentProject[((Collection)localObject2).size()]);
          try
          {
            TCComponentProjectType localTCComponentProjectType = (TCComponentProjectType)localTCComponent.getSession().getTypeComponent("TC_Project");
            localTCComponentProjectType.assignToProjects(arrayOfTCComponentProject1, localTCComponent);
          }
          catch (TCException localTCException)
          {
            Object[] arrayOfObject = new Object[2];
            StringBuilder localStringBuilder = new StringBuilder();
            for (TCComponentProject localTCComponentProject : arrayOfTCComponentProject1)
              localStringBuilder.append(localTCComponentProject.toDisplayString());
            arrayOfObject[0] = localStringBuilder.toString();
            arrayOfObject[1] = localTCComponent.toDisplayString();
            MessageBox.post(getWizard().getContainer().getShell(), MessageFormat.format(m_reg.getString("ProjectAssignFailure.MESSAGE"), arrayOfObject), localTCException, true, null, 2);
          }
        }
      }
    }
  }

  private void setProjectLists(TCComponentProject[] paramArrayOfTCComponentProject1, TCComponentProject[] paramArrayOfTCComponentProject2)
  {
    ArrayList localArrayList1 = new ArrayList(Arrays.asList(paramArrayOfTCComponentProject1));
    ArrayList localArrayList2 = new ArrayList();
    if (paramArrayOfTCComponentProject2 != null)
      for (TCComponentProject localTCComponentProject : paramArrayOfTCComponentProject2)
        if (localArrayList1.contains(localTCComponentProject))
          localArrayList2.add(localTCComponentProject);
    refresh(localArrayList1, localArrayList2);
  }

  private void refresh(List<TCComponentProject> paramList1, List<TCComponentProject> paramList2)
  {
    if ((this.m_assignableComposite != null) && (paramList1 != null))
    {
      this.m_availableProjListViewer.setInput(paramList1);
      this.m_assignableComposite.setViewer(this.m_availableProjListViewer);
      StructuredSelection localStructuredSelection = new StructuredSelection(paramList2);
      this.m_assignableComposite.setSelection(localStructuredSelection);
    }
  }
  public TCComponentProject[] getselectProject(){
	  return this.m_selectedProjects;
  }

  public void reRender()
  {
    super.reRender();
    ArrayList localArrayList = new ArrayList(Arrays.asList(this.m_availableProjects));
    List localList = Collections.emptyList();
    refresh(localArrayList, localList);
  }

  private static class IC_AssignProjectContentProvider
    implements IStructuredContentProvider
  {
    List<TCComponentProject> m_assignableProjectList;

    public void dispose()
    {
    }

    public Object[] getElements(Object paramObject)
    {
      return this.m_assignableProjectList.toArray();
    }

    public void inputChanged(Viewer paramViewer, Object paramObject1, Object paramObject2)
    {
      if ((this.m_assignableProjectList == null) && ((paramObject2 instanceof List)))
        this.m_assignableProjectList = ((List)paramObject2);
    }
  }

  private static class IC_ViewerConfigurator extends MySelectionViewerComposite.ViewerConfigurator
  {
    Object[] m_args;

    IC_ViewerConfigurator()
    {
      try
      {
        TCSession localTCSession = (TCSession)TcappsPlugin.getSessionService().getSession(TCSession.class.getName());
        this.m_args = new String[] { TCComponentProject.getDisplayName(localTCSession) };
      }
      catch (Exception localException)
      {
        this.m_args = new String[] { "No Name" };
      }
    }

    public String getLeftTitle()
    {
      return MessageFormat.format(AssignProjectPage.m_reg.getString("projectPageAvailableList.TITLE"), this.m_args);
    }

    public String getRightTitle()
    {
      return MessageFormat.format(AssignProjectPage.m_reg.getString("projectPageSelectedList.TITLE"), this.m_args);
    }

    public String getToolTipForLeftShiftButton()
    {
      return MessageFormat.format(AssignProjectPage.m_reg.getString("leftShiftButton.TOOLTIP"), this.m_args);
    }

    public String getToolTipForRightShiftButton()
    {
      return MessageFormat.format(AssignProjectPage.m_reg.getString("rightShiftButton.TOOLTIP"), this.m_args);
    }

    public String getToolTipForShiftAllToLeftButton()
    {
      return MessageFormat.format(AssignProjectPage.m_reg.getString("shiftAllToLeft.TOOLTIP"), this.m_args);
    }

    public String getToolTipForShiftAllToRightButton()
    {
      return MessageFormat.format(AssignProjectPage.m_reg.getString("shiftAllToRight.TOOLTIP"), this.m_args);
    }
  }
}