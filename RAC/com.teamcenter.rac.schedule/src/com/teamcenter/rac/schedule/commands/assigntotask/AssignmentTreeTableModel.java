package com.teamcenter.rac.schedule.commands.assigntotask;

import com.teamcenter.rac.aif.common.AIFTreeTableModel;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentDiscipline;
import com.teamcenter.rac.kernel.TCComponentGroup;
import com.teamcenter.rac.kernel.TCComponentRole;
import com.teamcenter.rac.kernel.TCComponentScheduleMember;
import com.teamcenter.rac.kernel.TCComponentScheduleTask;
import com.teamcenter.rac.kernel.TCComponentUser;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.schedule.ScheduleViewApplicationPanel;
import com.teamcenter.rac.schedule.common.tree.ScheduleTreeLine;
import com.teamcenter.rac.schedule.common.util.TCComponentComparator;
import com.teamcenter.rac.schedule.scheduler.componentutils.ScheduleHelper;
import com.teamcenter.rac.schedule.scheduler.componentutils.TaskHelper;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.Registry;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;
import javax.swing.tree.MutableTreeNode;
import org.apache.log4j.Logger;

public class AssignmentTreeTableModel extends AIFTreeTableModel
{
  private ScheduleViewApplicationPanel schedulePanel = null;
  private ScheduleTreeLine rootNode = null;
  private Vector<String> previousAssignmentList = null;
  private HashMap<String, Number> resourcePercent = null;
  private Registry r = Registry.getRegistry(this);
  private final String ATTR_MEMBER_TAG = "fnd0Schedulemember_taglist";
  private final String ATTR_RESOURCE_TAG = "resource_tag";
  private final String PERCENTAGE = "%";
  private final int DEFAULTRESLEVEL = 100;
  private NumberFormat pf = null;
  private NumberFormat nf = null;
  private TreeTablePanel panel = null;
  private static final Logger logger = Logger.getLogger(AssignmentTreeTableModel.class);
  private Set nonEditableColumns = null;
  public static int resourceColumnID = 0;
  public static int resourceAssignColumnID = 1;
  public static int resourceGraphIconColumnID = 3;

  public AssignmentTreeTableModel(ScheduleViewApplicationPanel paramScheduleViewApplicationPanel, AssignmentTreeLine paramAssignmentTreeLine, String[] paramArrayOfString, TreeTablePanel paramTreeTablePanel, boolean paramBoolean)
  {
	  
    super(paramAssignmentTreeLine, paramArrayOfString);
    //System.out.println("AssignmentTreeTableModel paramScheduleViewApplicationPanel paramAssignmentTreeLine paramArrayOfString paramTreeTablePanel");
    this.pf.setMinimumFractionDigits(0);
    this.pf.setMaximumFractionDigits(3);
    this.nf = NumberFormat.getNumberInstance();
    this.schedulePanel = paramScheduleViewApplicationPanel;
    this.rootNode = paramAssignmentTreeLine;
    this.panel = paramTreeTablePanel;
    this.resourcePercent = new HashMap();
    this.nonEditableColumns = new HashSet();
    this.nonEditableColumns.add(Integer.valueOf(resourceGraphIconColumnID));
    this.nonEditableColumns.add(Integer.valueOf(resourceAssignColumnID));
    buildTree(paramBoolean);
  }

  public boolean isCellEditable(Object paramObject, int paramInt)
  {
	//System.out.println("isCellEditable paramObject paramInt");
    return (((AssignmentTreeLine)paramObject).getUserObject().getClass() != String.class) && (!this.nonEditableColumns.contains(Integer.valueOf(paramInt)));
  }

  public void setValueAt(Object paramObject1, Object paramObject2, int paramInt)
  {
	//System.out.println("setValueAt paramObject1 paramObject2 paramInt");
    if (paramObject2 != null)
    {
      AssignmentTreeLine localAssignmentTreeLine = (AssignmentTreeLine)paramObject2;
      Class localClass = localAssignmentTreeLine.getUserObject().getClass();
      if ((localClass != String.class) && (localClass != TCComponentGroup.class) && (localClass != TCComponentRole.class) && (paramInt > 1))
      {
        localAssignmentTreeLine.setSelectStatus(1);
        double d = 0.0D;
        d = getResourceLevel((String)paramObject1, paramObject2);
        super.setValueAt(paramObject1, paramObject2, paramInt);
        this.resourcePercent.put(localAssignmentTreeLine.getKey(), Double.valueOf(d));
        this.panel.getTreeTable().repaint();
      }
    }
  }

  public Object getValueAt(Object paramObject, int paramInt)
  {
	//System.out.println("getValueAt paramObject paramInt");
    AssignmentTreeLine localAssignmentTreeLine = (AssignmentTreeLine)paramObject;
    String str1 = localAssignmentTreeLine.getKey();
    String str2 = "";
    if ((((AssignmentTreeLine)paramObject).isSelected()) && (paramInt > 1))
      if (this.resourcePercent.containsKey(str1))
      {
        str2 = this.pf.format(((Number)this.resourcePercent.get(str1)).doubleValue() / 100.0D);
      }
      else if ((localAssignmentTreeLine.isSelected()) && (!this.resourcePercent.containsKey(str1)))
      {
        this.resourcePercent.put(str1, Integer.valueOf(100));
        str2 = this.pf.format(1L);
        this.panel.getTreeTable().repaint();
      }
    return str2;
  }

  private double getResourceLevel(String paramString, Object paramObject)
  {
	//System.out.println("getResourceLevel paramString paramObject");
    double d = 0.0D;
    if (paramString.trim().length() == 0)
    {
      paramString = "";
      Number localNumber = (Number)this.resourcePercent.get(((AssignmentTreeLine)paramObject).getKey());
      if ((localNumber == null) && (((AssignmentTreeLine)paramObject).isSelected()))
        d = 100.0D;
      else if (localNumber == null)
        ((AssignmentTreeLine)paramObject).setSelectStatus(0);
    }
    else
    {
      try
      {
        paramString = removePercentage(paramString).trim();
        d = Math.abs(this.nf.parse(paramString).doubleValue());
      }
      catch (ParseException localParseException)
      {
        MessageBox.post(this.r.getString("resLevel.INVALIDINPUT"), this.r.getString(" info "), 1);
      }
    }
    return d;
  }

  private void buildTree(boolean paramBoolean)
  {
	//System.out.println("buildTree paramBoolean");
    try
    {
      TCComponentScheduleTask localTCComponentScheduleTask = null;
      this.panel.getPreviousAssignments();
      this.resourcePercent.clear();
      HashMap localHashMap = this.panel.getResourcePercent();
      Iterator localIterator = localHashMap.entrySet().iterator();
      while (localIterator.hasNext())
      {
        Entry localObject1 = (Entry) localIterator.next();
        this.resourcePercent.put((String)(localObject1).getKey(), (Number)(localObject1).getValue());
      }
      this.previousAssignmentList = this.panel.getPreviousAssignmentList();
      Object localObject1 = this.schedulePanel.getSelectedComponent();
      Object localObject2 = null;
      if ((localObject1 instanceof TCComponentScheduleTask))
      {
        localObject2 = TaskHelper.getScheduleForTask((TCComponent)localObject1);
        localTCComponentScheduleTask = (TCComponentScheduleTask)localObject1;
      }
      else
      {
        localObject2 = localObject1;
      }
      if (localObject2 == null)
        localObject2 = this.schedulePanel.getCurrentProject();
      TCProperty localTCProperty = ((TCComponent)localObject2).getTCProperty("fnd0Schedulemember_taglist");
      TCComponent[] arrayOfTCComponent1 = localTCProperty.getReferenceValueArray();
      ArrayList localArrayList1 = new ArrayList(Arrays.asList(arrayOfTCComponent1));
      ArrayList localArrayList2 = null;
      if (localTCComponentScheduleTask != null)
        this.panel.isNewTaskAssignment();
      Collections.sort(localArrayList1, new TCComponentComparator());
      for (int i = 0; i < localArrayList1.size(); i++)
      {
        TCComponent localTCComponent = (TCComponent)localArrayList1.get(i);
        if ((localTCComponent instanceof TCComponentScheduleMember))
        {
          TCComponentScheduleMember localTCComponentScheduleMember = (TCComponentScheduleMember)localTCComponent;
          localTCComponent = localTCComponentScheduleMember.getReferenceProperty("resource_tag");
        }
        else
        {
          localTCComponent = null;
        }
        if (localTCComponent != null)
        {
          int j = 0;
          if (localTCComponent.getClass() == TCComponentUser.class)
          {
            try
            {
              j = localTCComponent.getIntProperty("status");
            }
            catch (TCException localTCException2)
            {
              localTCException2.printStackTrace();
            }
            if ((j == 1) && (this.previousAssignmentList != null) && (!this.previousAssignmentList.contains(localTCComponent.getUid())));
          }
          else
          {
            boolean bool = true;
            if (j == 1)
              bool = false;
            String str = null;
            if ((localTCComponent instanceof TCComponentDiscipline))
              str = localTCComponent.getUid();
            AssignmentTreeLine localAssignmentTreeLine = new AssignmentTreeLine();
            localAssignmentTreeLine.init(localTCComponent, localTCComponent.getUid(), str, false, bool);
            this.rootNode.insert(localAssignmentTreeLine, this.rootNode.getChildCount());
            if ((localArrayList2 != null) && (localArrayList2.contains(localTCComponent)))
              localArrayList2.remove(localTCComponent);
            Object localObject3;
            TCComponent[] localObject4;
            Object localObject5 = null;
            if (localTCComponent.getClass() == TCComponentDiscipline.class)
            {
              if ((this.previousAssignmentList != null) && (this.previousAssignmentList.contains(localTCComponent.getUid())))
                if (!paramBoolean)
                  localAssignmentTreeLine.setSelectStatus(0);
                else
                  localAssignmentTreeLine.setSelectStatus(1);
              localObject3 = localTCComponent.getTCProperty("TC_discipline_member");
              localObject4 = ((TCProperty)localObject3).getReferenceValueArray();
              for (int k = 0; k < localObject4.length; k++)
                if (ScheduleHelper.isUserAMemberFromCachedData(localObject4[k], arrayOfTCComponent1, null))
                  ((ArrayList)localObject5).add(localObject4[k]);
              Collections.sort((List)localObject5, new Comparator()
              {
                public int compare(Object paramAnonymousObject1, Object paramAnonymousObject2)
                {
                  String str1 = paramAnonymousObject1.toString();
                  String str2 = paramAnonymousObject2.toString();
                  return str1.compareTo(str2);
                }
              });
              setPreviousAssignments(localAssignmentTreeLine, (TCComponent[])((ArrayList)localObject5).toArray(new TCComponent[0]), localArrayList1, localArrayList2);
            }
            else if (localTCComponent.getClass() == TCComponentGroup.class)
            {
              localObject3 = (TCComponentGroup)localTCComponent;
              localObject4 = ScheduleHelper.getSubGroups((TCComponentGroup)localObject3);
              if ((localObject4 != null) && (localObject4.length > 0))
              {
                Collections.sort(Arrays.asList((Object[])localObject4), new TCComponentComparator());
                addSubGroups(localAssignmentTreeLine, (TCComponentGroup[])localObject4, localTCComponent, localArrayList1, localArrayList2);
              }
              TCComponentRole[] localObject8 = ScheduleHelper.getRolesInGroup((TCComponentGroup)localObject3);
              if ((localObject8 != null) && (localObject8.length > 0))
              {
                Collections.sort(Arrays.asList((Object[])localObject8), new Comparator()
                {
                  public int compare(Object paramAnonymousObject1, Object paramAnonymousObject2)
                  {
                    String str1 = paramAnonymousObject1.toString();
                    String str2 = paramAnonymousObject2.toString();
                    return str1.compareTo(str2);
                  }
                });
                addRoles(localAssignmentTreeLine, (TCComponentRole[])localObject8, localTCComponent, localArrayList1, localArrayList2);
              }
              TCComponent[] arrayOfTCComponent2 = localTCComponent.getTCProperty("list_of_discipline").getReferenceValueArray();
              if (arrayOfTCComponent2.length > 0)
              {
                Collections.sort(Arrays.asList(arrayOfTCComponent2), new Comparator()
                {
                  public int compare(Object paramAnonymousObject1, Object paramAnonymousObject2)
                  {
                    String str1 = paramAnonymousObject1.toString();
                    String str2 = paramAnonymousObject2.toString();
                    return str1.compareTo(str2);
                  }
                });
                addDiscToGroup(localAssignmentTreeLine, arrayOfTCComponent2, localTCComponent, arrayOfTCComponent1, localArrayList1, localArrayList2);
              }
            }
            else if (this.previousAssignmentList != null)
            {
              this.previousAssignmentList.contains(localTCComponent.getUid());
            }
          }
        }
      }
    }
    catch (TCException localTCException1)
    {
      logger.error("Exception", localTCException1);
    }
  }
  private void addRoles(AssignmentTreeLine paramAssignmentTreeLine, TCComponentRole[] paramArrayOfTCComponentRole, TCComponent paramTCComponent, ArrayList paramArrayList1, ArrayList paramArrayList2)throws TCException
  {
	//System.out.println("addRoles paramAssignmentTreeLine paramArrayOfTCComponentRole");
    TcComparator localTcComparator = new TcComparator();
    ArrayList localArrayList = new ArrayList();
    for (TCComponentRole localObject1 : paramArrayOfTCComponentRole)
      localArrayList.add(localObject1);
    Collections.sort(localArrayList, localTcComparator);
    Iterator localIterator = localArrayList.iterator();
    while (localIterator.hasNext())
    {
      TCComponent localObject1 = (TCComponent)localIterator.next();
      String str = null;
      if (paramAssignmentTreeLine.isDisciplineLine())
        str = paramAssignmentTreeLine.getResUID();
      AssignmentTreeLine assigntreeline = new AssignmentTreeLine();
      assigntreeline.init(localObject1, ((TCComponent)localObject1).getUid(), str);
      paramAssignmentTreeLine.add(assigntreeline);
    }
  }
  private void addDiscToGroup(AssignmentTreeLine paramAssignmentTreeLine, TCComponent[] paramArrayOfTCComponent1, TCComponent paramTCComponent, TCComponent[] paramArrayOfTCComponent2, ArrayList<TCComponent> paramArrayList, ArrayList paramArrayList1)
    throws TCException
  {
	//System.out.println("addDiscToGroup paramAssignmentTreeLine paramArrayOfTCComponent1 paramTCComponent paramArrayOfTCComponent2 paramArrayList paramArrayList1");
    for (TCComponent localTCComponent : paramArrayOfTCComponent1)
    {
      AssignmentTreeLine localAssignmentTreeLine = new AssignmentTreeLine();
      localAssignmentTreeLine.init(localTCComponent, localTCComponent.getUid(), localTCComponent.getUid());
      paramAssignmentTreeLine.add(localAssignmentTreeLine);
      if ((this.previousAssignmentList != null) && (this.previousAssignmentList.contains(AssignmentTreeLine.getKey(localTCComponent.getUid(), localTCComponent.getUid()))))
        localAssignmentTreeLine.setSelectStatus(1);
      if ((paramArrayList1 != null) && (paramArrayList1.contains(localAssignmentTreeLine)))
        paramArrayList1.remove(localAssignmentTreeLine);
      TCProperty localTCProperty = localTCComponent.getTCProperty("TC_discipline_member");
      TCComponent[] arrayOfTCComponent2 = localTCProperty.getReferenceValueArray();
      ArrayList localArrayList = new ArrayList();
      for (int k = 0; k < arrayOfTCComponent2.length; k++)
        if (ScheduleHelper.isUserAMemberFromCachedData(arrayOfTCComponent2[k], paramArrayOfTCComponent2, null))
          localArrayList.add(arrayOfTCComponent2[k]);
      setPreviousAssignments(localAssignmentTreeLine, (TCComponent[])localArrayList.toArray(new TCComponent[0]), paramArrayList, paramArrayList1);
    }
  }

  private void addSubGroups(AssignmentTreeLine paramAssignmentTreeLine, TCComponentGroup[] paramArrayOfTCComponentGroup, TCComponent paramTCComponent, ArrayList paramArrayList1, ArrayList paramArrayList2)
    throws TCException
  {
	//System.out.println("addSubGroups paramAssignmentTreeLine paramArrayOfTCComponentGroup paramTCComponent paramArrayOfTCComponent2 paramArrayList1 paramArrayList2");
    for (TCComponentGroup localTCComponentGroup : paramArrayOfTCComponentGroup)
    {
      String str = null;
      if (paramAssignmentTreeLine.isDisciplineLine())
        str = paramAssignmentTreeLine.getResUID();
      AssignmentTreeLine localAssignmentTreeLine = new AssignmentTreeLine();
      localAssignmentTreeLine.init(localTCComponentGroup, localTCComponentGroup.getUid(), str);
      paramAssignmentTreeLine.add(localAssignmentTreeLine);
      TCComponentRole[] arrayOfTCComponentRole = ScheduleHelper.getRolesInGroup((TCComponentGroup)localTCComponentGroup);
      if ((arrayOfTCComponentRole != null) && (arrayOfTCComponentRole.length > 0))
        addRoles(localAssignmentTreeLine, arrayOfTCComponentRole, localTCComponentGroup, paramArrayList1, paramArrayList2);
      TCComponentDiscipline[] arrayOfTCComponentDiscipline = ScheduleHelper.getDisciplinesInGroup((TCComponentGroup)localTCComponentGroup);
      if ((arrayOfTCComponentDiscipline != null) && (arrayOfTCComponentDiscipline.length > 0))
      {
    	TCComponent[] localObject = new TCComponent[paramArrayList1.size()];
        paramArrayList1.toArray((Object[])localObject);
        addDiscToGroup(localAssignmentTreeLine, arrayOfTCComponentDiscipline, paramTCComponent, (TCComponent[])localObject, paramArrayList1, paramArrayList2);
      }
      TCComponentGroup[] localObject = ScheduleHelper.getSubGroups((TCComponentGroup)localTCComponentGroup);
      if ((localObject != null) && (localObject.length > 0))
        addSubGroups(localAssignmentTreeLine, (TCComponentGroup[])localObject, paramTCComponent, paramArrayList1, paramArrayList2);
    }
  }

  private void setPreviousAssignments(AssignmentTreeLine paramAssignmentTreeLine, TCComponent[] paramArrayOfTCComponent, ArrayList paramArrayList1, ArrayList paramArrayList2)
  {
	//System.out.println("setPreviousAssignments paramAssignmentTreeLine paramArrayOfTCComponent paramArrayList1 paramArrayList2");
    for (TCComponent localTCComponent : paramArrayOfTCComponent)
    {
      int k = 0;
      try
      {
        k = localTCComponent.getIntProperty("status");
      }
      catch (TCException localTCException)
      {
        localTCException.printStackTrace();
      }
      if ((k != 1) || (this.previousAssignmentList == null) || (this.previousAssignmentList.contains(localTCComponent.getUid())))
      {
        boolean bool = true;
        if (k == 1)
          bool = false;
        String str = null;
        if (paramAssignmentTreeLine.isDisciplineLine())
          str = paramAssignmentTreeLine.getResUID();
        AssignmentTreeLine localAssignmentTreeLine = new AssignmentTreeLine();
        localAssignmentTreeLine.init(localTCComponent, localTCComponent.getUid(), str, false, bool);
        paramAssignmentTreeLine.add(localAssignmentTreeLine);
        if ((this.previousAssignmentList != null) && (this.previousAssignmentList.contains(AssignmentTreeLine.getKey(localTCComponent.getUid(), str))))
          localAssignmentTreeLine.setSelectStatus(1);
        if ((paramArrayList2 != null) && (paramArrayList2.contains(localTCComponent)))
          paramArrayList2.remove(localTCComponent);
      }
    }
  }

  public Vector<String> getPreviousAssignmentList()
  {
	//System.out.println("getPreviousAssignmentList ");
    return this.previousAssignmentList;
  }

  public String getAssignmentId(String paramString)
  {
    return null;
  }

  public Number getResourceLevel(String paramString)
  {
	//System.out.println("getResourceLevel paramString ");
    return (Number)this.resourcePercent.get(paramString);
  }

  public double getTotalResourceLevel(String paramString, Number paramNumber)
  {
	//System.out.println("getTotalResourceLevel paramString paramNumber");
    double d = ((Number)this.resourcePercent.get(paramString)).doubleValue();
    return d + paramNumber.doubleValue();
  }

  private String removePercentage(String paramString)
  {
	//System.out.println("removePercentage paramString");
    String str = paramString.trim();
    if (str.endsWith("%"))
      str = str.substring(0, str.length() - 1);
    else if (str.startsWith("%"))
      str = str.substring(1);
    return str;
  }

  private static class TcComparator
    implements Comparator<TCComponent>
  {
    public int compare(TCComponent paramTCComponent1, TCComponent paramTCComponent2)
    {
      try
      {
        String str1 = paramTCComponent1.toString();
        String str2 = paramTCComponent2.toString();
        return str1.compareTo(str2);
      }
      catch (Exception localException)
      {
      }
      return 0;
    }
  }
}