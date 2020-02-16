package com.teamcenter.rac.schedule.commands.assigntotask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentDiscipline;
import com.teamcenter.rac.kernel.TCComponentResourcePool;
import com.teamcenter.rac.kernel.TCComponentSchedule;
import com.teamcenter.rac.kernel.TCComponentScheduleMember;
import com.teamcenter.rac.kernel.TCComponentUser;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;

public class AssignmentTreeContentProvider
  implements ITreeContentProvider, ISelectionChangedListener, Listener
{
  protected final TaskAssignmentDialog taskAssignmentDialog;
  protected List<TreeItem> treeItems = new ArrayList();
  private static final Logger logger = Logger.getLogger(TaskAssignmentDialog.class);
  protected TreeViewer viewer = null;
  protected static final String TABLEITEM = "_TABLEITEM";
  protected ScheduleTreeRoot root = null;
  protected TCComponentSchedule schedule = null;
  protected TCSession session = null;
  protected UIAssignmentCache assignmentCache = null;
  protected List<TCComponent> selectedTasks = null;
  protected static HashMap<String, TCComponentDiscipline> resObjectMap = null;
  
  public AssignmentTreeContentProvider(TaskAssignmentDialog paramTaskAssignmentDialog)
  {
    this.taskAssignmentDialog = paramTaskAssignmentDialog;
    this.schedule = paramTaskAssignmentDialog.getSchedule();
    this.session = paramTaskAssignmentDialog.getTcSession();
    this.assignmentCache = paramTaskAssignmentDialog.getAssignmentCache();
    this.selectedTasks = paramTaskAssignmentDialog.getSelectedTasks();
    this.viewer = paramTaskAssignmentDialog.getScheduleTaskTreeViewer();
    this.viewer.getTree().addListener(32, this);
  }

  public Object[] getChildren(Object paramObject)
  {
    ResourceTreeModel localResourceTreeModel = null;
    Object localObject = new ArrayList();
    if ((paramObject instanceof ResourceTreeModel))
    {
      localResourceTreeModel = (ResourceTreeModel)paramObject;
      localObject = localResourceTreeModel.getChilds();
    }
    return ((List)localObject).toArray();
  }

  public Object getParent(Object paramObject)
  {
    ResourceTreeModel localResourceTreeModel1 = null;
    if ((paramObject instanceof ResourceTreeModel))
    {
      ResourceTreeModel localResourceTreeModel2 = (ResourceTreeModel)paramObject;
      localResourceTreeModel1 = localResourceTreeModel2.getParent();
    }
    return localResourceTreeModel1;
  }

  public boolean hasChildren(Object paramObject)
  {
    boolean bool = false;
    if ((paramObject instanceof ResourceTreeModel))
    {
    	ResourceTreeModel localResourceTreeModel = (ResourceTreeModel)paramObject;
        if (localResourceTreeModel.isChildFetched())
        {
          bool = localResourceTreeModel.getChilds().size() > 0;
        }
        else
        {
          Object[] arrayOfObject = getChildren(paramObject);
          return arrayOfObject != null;
        }
    }
   return bool;
  }

  public Object[] getElements(Object paramObject)
  {
    Object[] arrayOfObject = new Object[1];
    if (this.root == null)
    {
      this.root = new ScheduleTreeRoot(this.schedule, this.session, this.assignmentCache);
      this.root.setSelectedTasks(this.selectedTasks);
    }
    arrayOfObject[0] = this.root;
    return arrayOfObject;
  }

  public void dispose()
  {
    this.treeItems.clear();
  }

  public void inputChanged(Viewer paramViewer, Object paramObject1, Object paramObject2)
  {
  }

  public void setInitalSelectedTreeItems()
  {
	//System.out.println("setInitalSelectedTreeItems");
    TreeViewer localTreeViewer = this.taskAssignmentDialog.getScheduleTaskTreeViewer();
    Tree localTree = localTreeViewer.getTree();
    TreeItem[] arrayOfTreeItem1 = this.taskAssignmentDialog.getScheduleTaskTreeViewer().getTree().getItems();
    if ((arrayOfTreeItem1 != null) && (arrayOfTreeItem1.length == 1))
    {
      //根节点
      TreeItem localTreeItem = arrayOfTreeItem1[0];
      //递归获取初始的树
      getInitTree(localTreeItem);
      //选中初始的树
      if (this.treeItems.size() > 0)
      {
        TreeItem[] arrayOfTreeItem2 = new TreeItem[this.treeItems.size()];
        for (int i = 0; i < this.treeItems.size(); i++)
          arrayOfTreeItem2[i] = ((TreeItem)this.treeItems.get(i));
        localTree.setSelection(arrayOfTreeItem2);
      }
    }
  }

  protected void getInitTree(TreeItem paramTreeItem)
  {
    TreeItem[] arrayOfTreeItem1 = paramTreeItem.getItems();
    //System.out.println("InitTree："+paramTreeItem.getText());
    if (arrayOfTreeItem1 != null)
      for (TreeItem localTreeItem : arrayOfTreeItem1)
      {
    	//System.out.println("InitTree"+paramTreeItem.getText()+" child "+localTreeItem.getText());
        getInitTree(localTreeItem);
        Object localObject = localTreeItem.getData();
        if ((localObject instanceof ScheduleTaskTreeModel))
        {
          ScheduleTaskTreeModel localScheduleTaskTreeModel = (ScheduleTaskTreeModel)localObject;
          if (localScheduleTaskTreeModel.isPreSelected())
            this.treeItems.add(localTreeItem);
        }
      }
  }

  public void selectionChanged(SelectionChangedEvent paramSelectionChangedEvent)
  {
	//System.out.println("selectionChanged ");
    if ((paramSelectionChangedEvent.getSelection() instanceof IStructuredSelection))
    {
      //System.out.println(" is IStructuredSelection");
      if (this.taskAssignmentDialog.assignQualificationTableViewer != null)
      {
    	  //System.out.println(" is assignQualificationTableViewer");
        this.taskAssignmentDialog.enableQualificationButtons(false);
        this.taskAssignmentDialog.assignQualificationTableViewer.getTable().removeAll();
      }
      if (this.taskAssignmentDialog.filteringTableviewViewer_dis != null){
    	  //System.out.println(" is assignQualificationTableViewer");
    	  this.taskAssignmentDialog.filteringTableviewViewer_dis.getTable().removeAll();
      }
      if (this.taskAssignmentDialog.filteringTableviewViewer_grprole != null){
    	  //System.out.println(" is filteringTableviewViewer_grprole");
    	  this.taskAssignmentDialog.filteringTableviewViewer_grprole.getTable().removeAll();
      }
      if (this.taskAssignmentDialog.filteringTableviewViewer_qual != null){
    	  //System.out.println(" is filteringTableviewViewer_qual");
    	  this.taskAssignmentDialog.filteringTableviewViewer_qual.getTable().removeAll();
      }
        
      if (this.taskAssignmentDialog.filteringResults_viewer != null){
    	  //System.out.println(" is filteringResults_viewer");
    	  this.taskAssignmentDialog.filteringResults_viewer.getTable().removeAll();
      }
      TabFolder localTabFolder = this.taskAssignmentDialog.getTabFolder();
      Event localEvent = new Event();
      localEvent.type = 13;
      localEvent.widget = localTabFolder;
      localTabFolder.notifyListeners(13, localEvent);
      IStructuredSelection localIStructuredSelection = (IStructuredSelection)paramSelectionChangedEvent.getSelection();
      List localList = localIStructuredSelection.toList();
      if (localList.size() == 0)
      {
        this.taskAssignmentDialog.getBtnRemove().setEnabled(false);
        return;
      }
      getDisplinefromNodes(localList);
      getQualificationsForTaskNode(localList);
      Iterator localIterator = localList.iterator();
      while (localIterator.hasNext())
      {
        ResourceTreeModel localResourceTreeModel = (ResourceTreeModel)localIterator.next();
        if ((localResourceTreeModel instanceof AssignmentTreeModel))
        {
          this.taskAssignmentDialog.getBtnRemove().setEnabled(true);
        }
        else
        {
          this.taskAssignmentDialog.getBtnRemove().setEnabled(false);
          break;
        }
      }
    }
  }

  public void handleEvent(Event paramEvent)
  {
    Shell localShell1 = null;
    Label localLabel = null;
    Shell localShell2 = this.taskAssignmentDialog.getShlTaskAssignment().getShell();
    Display localDisplay = this.taskAssignmentDialog.getShlTaskAssignment().getDisplay();
    switch (paramEvent.type)
    {
    case 32:
      if (paramEvent.widget == this.viewer.getTree())
      {
        TreeItem localTreeItem = this.viewer.getTree().getItem(new Point(paramEvent.x, paramEvent.y));
        if (localTreeItem != null)
        {
          Object localObject = localTreeItem.getData();
          if ((localObject instanceof ScheduleTaskTreeModel))
          {
            boolean bool = localTreeItem.getExpanded();
            if (!bool)
            {
              ScheduleTaskTreeModel localScheduleTaskTreeModel = (ScheduleTaskTreeModel)localObject;
              localShell1 = new Shell(localShell2, 540676);
              localShell1.setBackground(localDisplay.getSystemColor(29));
              FillLayout localFillLayout = new FillLayout();
              localFillLayout.marginWidth = 2;
              localShell1.setLayout(localFillLayout);
              localLabel = new Label(localShell1, 0);
              localLabel.setForeground(localDisplay.getSystemColor(28));
              localLabel.setBackground(localDisplay.getSystemColor(29));
              localLabel.setData("_TABLEITEM", localTreeItem);
              Listener local1 = new Listener()
              {
                public void handleEvent(Event paramAnonymousEvent)
                {
                  Label localLabel = (Label)paramAnonymousEvent.widget;
                  Shell localShell = localLabel.getShell();
                  switch (paramAnonymousEvent.type)
                  {
                  case 3:
                    Event localEvent = new Event();
                    localEvent.item = ((TableItem)localLabel.getData("_TABLEITEM"));
                    AssignmentTreeContentProvider.this.viewer.getTree().setSelection(new TreeItem[] { (TreeItem)localEvent.item });
                    AssignmentTreeContentProvider.this.viewer.getTree().notifyListeners(13, localEvent);
                    localShell.dispose();
                    AssignmentTreeContentProvider.this.viewer.getTree().setFocus();
                    break;
                  case 7:
                    localShell.dispose();
                  case 4:
                  case 5:
                  case 6:
                  }
                }
              };
              localLabel.addListener(7, local1);
              localLabel.addListener(3, local1);
              String str = localScheduleTaskTreeModel.getToolTipText();
              if (str != null)
              {
                localLabel.setText(localScheduleTaskTreeModel.getToolTipText());
                Point localPoint1 = localShell1.computeSize(-1, -1);
                Rectangle localRectangle = localTreeItem.getBounds(0);
                Point localPoint2 = this.viewer.getTree().toDisplay(localRectangle.x, localRectangle.y);
                localShell1.setBounds(localPoint2.x, localPoint2.y, localPoint1.x, localPoint1.y);
                localShell1.setVisible(true);
              }
            }
          }
        }
      }
      break;
    }
  }

  protected void getQualificationsForTaskNode(List<ResourceTreeModel> paramList)
  {
    if (paramList.size() > 1)
      return;
    ResourceTreeModel localResourceTreeModel = (ResourceTreeModel)paramList.get(0);
    if ((localResourceTreeModel instanceof ScheduleTaskTreeModel))
    {
      ScheduleTaskTreeModel localScheduleTaskTreeModel = (ScheduleTaskTreeModel)localResourceTreeModel;
      if (!localScheduleTaskTreeModel.getQualifications().isEmpty())
        this.taskAssignmentDialog.assignQualificationTableViewer.setInput(localScheduleTaskTreeModel.getQualifications().toArray(new TaskQualificationModel[localScheduleTaskTreeModel.getQualifications().size()]));
    }
  }

  protected void getDisplinefromNodes(List<ResourceTreeModel> paramList)
  {
	//System.out.println("getDisplinefromNodes");
    if (paramList.size() > 1)
      return;
    ResourceTreeModel localResourceTreeModel = null;
    localResourceTreeModel = (ResourceTreeModel)paramList.get(0);
    if ((localResourceTreeModel instanceof PlaceHolderAssignmentTreeModel))
    {
      PlaceHolderAssignmentTreeModel localObject1 = null;
      TCComponent localObject2 = null;
      //System.out.println("is PlaceHolderAssignmentTreeModel___");
      localObject1 = (PlaceHolderAssignmentTreeModel) localResourceTreeModel;
      localObject2 = localObject1.getResource();
      TCComponent localTCComponent = ((PlaceHolderAssignmentTreeModel)localObject1).getResourcePool();
      TCComponentDiscipline localObject3 = null;
      TCComponentResourcePool localObject4 = null;
      if ((localObject2 instanceof TCComponentDiscipline))
        localObject3 = (TCComponentDiscipline) localObject2;
      if ((localObject2 instanceof TCComponentResourcePool))
        localObject4 = (TCComponentResourcePool) localObject2;
      if ((localTCComponent != null) && ((localTCComponent instanceof TCComponentResourcePool)))
        localObject4 = (TCComponentResourcePool) localTCComponent;
      TCComponent[] arrayOfTCComponent;
      if (localObject3 != null)
      {
        arrayOfTCComponent = new TCComponent[] { (TCComponent) localObject3 };
        if(this.taskAssignmentDialog.filteringTableviewViewer_dis!=null){
        	this.taskAssignmentDialog.filteringTableviewViewer_dis.setInput(arrayOfTCComponent);
        	this.taskAssignmentDialog.filteringTableviewViewer_dis.setCheckedElements(arrayOfTCComponent);
        }
      }
      if (localObject4 != null)
      {
        arrayOfTCComponent = new TCComponent[] { (TCComponent) localObject4 };
        if(this.taskAssignmentDialog.filteringTableviewViewer_grprole!=null){
        	this.taskAssignmentDialog.filteringTableviewViewer_grprole.setInput(arrayOfTCComponent);
        	this.taskAssignmentDialog.filteringTableviewViewer_grprole.setCheckedElements(arrayOfTCComponent);
        }
      }
    }
    if ((localResourceTreeModel instanceof ScheduleTaskTreeModel))
    {
      //System.out.println("is ScheduleTaskTreeModel___");
      ScheduleTaskTreeModel localObject1 = (ScheduleTaskTreeModel)localResourceTreeModel;
      List localObject2 = ((ScheduleTaskTreeModel)localObject1).getQualifications();
      if ((localObject2 != null) && (((List)localObject2).size() > 0))
      {
    	if(this.taskAssignmentDialog.filteringTableviewViewer_qual!=null){
    		this.taskAssignmentDialog.filteringTableviewViewer_qual.setInput(((List)localObject2).toArray(new TaskQualificationModel[((List)localObject2).size()]));
    		this.taskAssignmentDialog.filteringTableviewViewer_qual.setCheckedElements(((List)localObject2).toArray(new TaskQualificationModel[((List)localObject2).size()]));
    	}
      }
      return;
    }
    if (((localResourceTreeModel instanceof PlaceHolderAssignmentTreeModel)) && ((localResourceTreeModel.getParent() instanceof ScheduleTaskTreeModel)))
    {
      //System.out.println("is PlaceHolderAssignmentTreeModel ScheduleTaskTreeModel");
      ScheduleTaskTreeModel localObject1 = (ScheduleTaskTreeModel)localResourceTreeModel.getParent();
      List localObject2 = localObject1.getQualifications();
      if ((localObject2 != null) && (((List)localObject2).size() > 0))
      {
    	  if(this.taskAssignmentDialog.filteringTableviewViewer_qual!=null){
    		  this.taskAssignmentDialog.filteringTableviewViewer_qual.setInput(((List)localObject2).toArray(new TaskQualificationModel[((List)localObject2).size()]));
    		  this.taskAssignmentDialog.filteringTableviewViewer_qual.setCheckedElements(((List)localObject2).toArray(new TaskQualificationModel[((List)localObject2).size()]));
    	  }
      }
    }
  }
}