package com.teamcenter.rac.schedule.commands.schedulemembership;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.apache.log4j.Logger;

import com.teamcenter.rac.aif.common.AIFTreeNode;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.common.TCTree;
import com.teamcenter.rac.common.organization.OrgIconHelper;
import com.teamcenter.rac.kernel.NameIconIdStruct;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentDiscipline;
import com.teamcenter.rac.kernel.TCComponentGroup;
import com.teamcenter.rac.kernel.TCComponentGroupMember;
import com.teamcenter.rac.kernel.TCComponentGroupMemberType;
import com.teamcenter.rac.kernel.TCComponentGroupType;
import com.teamcenter.rac.kernel.TCComponentProject;
import com.teamcenter.rac.kernel.TCComponentResourcePool;
import com.teamcenter.rac.kernel.TCComponentResourcePoolType;
import com.teamcenter.rac.kernel.TCComponentRole;
import com.teamcenter.rac.kernel.TCComponentRoleType;
import com.teamcenter.rac.kernel.TCComponentType;
import com.teamcenter.rac.kernel.TCComponentUser;
import com.teamcenter.rac.kernel.TCComponentUserType;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.schedule.common.tree.ScheduleTreeNode;
import com.teamcenter.rac.util.ArraySorter;
import com.teamcenter.rac.util.Registry;

public class ScheduleMemberTree extends TCTree
{
  private TCSession tcSession = null;
  private String[] typeNames = null;
  private Registry registry = null;
  private ScheduleTreeNode rootNode = null;
  private TCComponent schedule = null;
  private static final Logger logger = Logger.getLogger(ScheduleMemberTree.class);
  private DefaultTreeModel treeModel;
  private Map<String, TCComponent> disciplineMemberMap = new HashMap();
  private Map<String, TCComponent> resourcePoolMemberMap = new HashMap();
  private List<ScheduleTreeNode> nodeList = new ArrayList();
  //YFJC_ADDED
  public static Vector<String> rateLevelDisciplines = new Vector(Arrays.asList(
		    new String[] { "Assistant Engineer", "Engineer", "Engineer Assistant", 
		    "ExtSupporter", "Manager", "Senior Engineer", "Lead Engineer" }));
  private CheckCellRenderer checkRenderer;

  public ScheduleMemberTree(TCSession paramTCSession, String[] paramArrayOfString)
  {
    this(paramTCSession, paramArrayOfString, null);
    System.out.println("ScheduleMemberTree paramTCSession paramArrayOfString");
  }

  public ScheduleMemberTree(TCSession paramTCSession, String[] paramArrayOfString, TCComponent paramTCComponent)
  {
	System.out.println("ScheduleMemberTree paramTCSession paramArrayOfString paramTCComponent");
    this.tcSession = paramTCSession;
    this.typeNames = paramArrayOfString;
    this.schedule = paramTCComponent;
    this.registry = Registry.getRegistry(this);
    init();
  }
  public HashMap<TCComponentUser, String> getSDTMember(TCComponent schedule)
  {
    HashMap projectMemmberList = new HashMap();
    try
    {
      TCComponent[] projects = schedule.getReferenceListProperty("project_list");
      if ((projects != null) && (projects.length > 0))
      {
        TCComponentProject project = (TCComponentProject)projects[0];
        TCComponent projectTeam = project.getReferenceProperty("project_team");
        if (projectTeam != null)
        {
          TCComponent[] projectMemmbers = projectTeam.getReferenceListProperty("project_members");
          int i = 0;
          do {
            if ((projectMemmbers[i] instanceof TCComponentUser))
            {
              TCComponentUser user = (TCComponentUser)projectMemmbers[i];
              if (!projectMemmberList.containsKey(user))
                projectMemmberList.put(user, user.toString());
            }
            else if ((projectMemmbers[i] instanceof TCComponentGroupMember))
            {
              TCComponentUser user = ((TCComponentGroupMember)projectMemmbers[i]).getUser();
              if (!projectMemmberList.containsKey(user))
                projectMemmberList.put(user, user.toString());
            }
            else if ((projectMemmbers[i] instanceof TCComponentGroup))
            {
              TCComponentGroupMemberType gmType = (TCComponentGroupMemberType)schedule.getSession().getTypeComponent("GroupMember");
              TCComponent[] gms = gmType.extentPersistent();
              String groupFullName = ((TCComponentGroup)projectMemmbers[i]).getFullName();
              System.out.println("--------------------all the groupmember: " + groupFullName + gms.length);
              for (int j = 0; j < gms.length; j++)
              {
                String oneGroupFullName = ((TCComponentGroupMember)gms[j]).getGroup().getFullName();
                TCComponentUser user = ((TCComponentGroupMember)gms[j]).getUser();
                if (((groupFullName.equals(oneGroupFullName)) || (oneGroupFullName.indexOf(groupFullName) > 0)) && 
                  (!projectMemmberList.containsKey(user)))
                {
                  projectMemmberList.put(user, user.toString());
                }
              }
            }
            i++; if (projectMemmbers == null) break;  } while (i < projectMemmbers.length);
        }

      }

    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
    return projectMemmberList;
  }
  public TCSession getSessionInfo()
  {
    return this.tcSession;
  }

  public String[] getTypeNames()
  {
    return this.typeNames;
  }

  public void init()
  {
	System.out.println("init");
    this.rootNode = createNode(this.registry.getString("organization.TITLE"));
    this.checkRenderer = new CheckCellRenderer(true, true);
    setCellRenderer(this.checkRenderer);
    setRoot(this.rootNode);
    loadTree();
  }

  protected void loadTree()
  {
	System.out.println("loadTree+++");
    if ((this.typeNames != null) && (this.typeNames.length > 0))
    {
      this.treeModel = ((DefaultTreeModel)getModel());
      for (String str1 : this.typeNames)
      {
    	
    	System.out.println("typeNames  "+str1);
        String str2 = this.registry.getString(str1 + ".TYPE_COMPONENT");
        System.out.println("str2 "+str2);
        if(str2.equals("Group")||str2.equals("Role")||str2.equals("ResourcePool"))
        	continue;
        try
        {
          TCComponentType localTCComponentType = this.tcSession.getTypeComponent(str2);
          ScheduleTreeNode localScheduleTreeNode1 = createNode(this.registry.getString(str1 + "." + "TITLE"));
          localScheduleTreeNode1.setUserObject(localTCComponentType);
          this.treeModel.insertNodeInto(localScheduleTreeNode1, this.rootNode, this.rootNode.getChildCount());
          Object[] arrayOfObject = getChildren(localScheduleTreeNode1);
          localScheduleTreeNode1.setType(str2);
          if (arrayOfObject != null)
          {
            int k = arrayOfObject.length;
            ArrayList localArrayList = new ArrayList();
            for (int m = 0; m < k; m++)
              localArrayList.add(arrayOfObject[m]);
            Collections.sort(localArrayList);
            Iterator localIterator = localArrayList.iterator();
            while (localIterator.hasNext())
            {
              Object localObject = localIterator.next();
              if ((localObject instanceof String))
              {
                String str3 = (String)localObject;
                ScheduleTreeNode localScheduleTreeNode2 = new ScheduleTreeNode(str3);
                localScheduleTreeNode2.setType(str2);
                localScheduleTreeNode2.setTree(this);
                this.treeModel.insertNodeInto(localScheduleTreeNode2, localScheduleTreeNode1, localScheduleTreeNode1.getChildCount());
                this.nodeList.add(localScheduleTreeNode2);
              }
            }
          }
          localScheduleTreeNode1.setTitle(this.registry.getString(str1 + "." + "TITLE"));
        }
        catch (TCException localTCException)
        {
          logger.error("TCException :" + localTCException);
        }
      }
    }
  }

  public TCComponent findTCComponent(String paramString1, String paramString2)
  {
	System.out.println("findTCComponent paramString1 paramString2");
	TCComponent localObject1 = null;
    try
    {
      Object localObject2;
      if (paramString1.equalsIgnoreCase(this.r.getString("user.TYPE_COMPONENT")))
      {
        localObject2 = (TCComponentUserType)this.tcSession.getTypeComponent("User");
        String str = TCComponentUser.getUserIdFromFormattedString(paramString2);
        localObject1 = ((TCComponentUserType)localObject2).find(str);
      }
      else if (paramString1.equalsIgnoreCase(this.r.getString("group.TYPE_COMPONENT")))
      {
        localObject2 = (TCComponentGroupType)this.tcSession.getTypeComponent("Group");
        localObject1 = ((TCComponentGroupType)localObject2).find(paramString2 + ".");
      }
      /*else if (paramString1.equalsIgnoreCase(this.r.getString("role.TYPE_COMPONENT")))
      {
        localObject2 = (TCComponentRoleType)this.tcSession.getTypeComponent("Role");
        localObject1 = ((TCComponentRoleType)localObject2).find(paramString2);
      }*/
      else if (paramString1.equalsIgnoreCase(this.r.getString("discipline.TYPE_COMPONENT")))
      {
        localObject1 = (TCComponent)this.disciplineMemberMap.get(paramString2);
      }
      /*else if (paramString1.equalsIgnoreCase(this.r.getString("resourcepool.TYPE_COMPONENT")))
      {
        localObject1 = (TCComponent)this.resourcePoolMemberMap.get(paramString2);
      }*/
    }
    catch (Exception localException)
    {
      logger.error("TCException :" + localException);
    }
    return localObject1;
  }

  public ScheduleTreeNode getSelectedNode()
  {
	System.out.println("getSelectedNode");
    ScheduleTreeNode localScheduleTreeNode = null;
    TreePath localTreePath = getSelectionPath();
    if (localTreePath != null)
      localScheduleTreeNode = (ScheduleTreeNode)localTreePath.getLastPathComponent();
    return localScheduleTreeNode;
  }

  protected Object[] getChildren(AIFTreeNode paramAIFTreeNode)
  {
	System.out.println("getChildren paramAIFTreeNode");
    ScheduleTreeNode localScheduleTreeNode = (ScheduleTreeNode)paramAIFTreeNode;
    String[] arrayOfString = null;
    NameIconIdStruct[] arrayOfNameIconIdStruct = null;
    Registry localRegistry = Registry.getRegistry(this);
    if (localScheduleTreeNode == null)
      localScheduleTreeNode = getSelectedNode();
    if ((localScheduleTreeNode != null) && (!getRootNode().equals(localScheduleTreeNode)))
    {
      Object localObject1 = localScheduleTreeNode.getUserObject();
      TCComponentType localTCComponentType = null;
      if ((localObject1 instanceof TCComponentType))
        localTCComponentType = (TCComponentType)localObject1;
      if (localTCComponentType != null)
        try
        {
          Object localObject2;
          int i;
          if (paramAIFTreeNode.toString().equalsIgnoreCase(localRegistry.getString("user.TYPE_COMPONENT")))
          {
            localObject2 = (TCComponentUserType)this.tcSession.getTypeComponent("User");
            NameIconIdStruct[] objNameIconIdArray1 = ((TCComponentUserType)localObject2).getUserAndIconIdListByUser(null);
            //YFJC_ADDED
            Vector array = new Vector();
            HashMap projectMemmberList = getSDTMember(this.schedule);
            for (i = 0; (objNameIconIdArray1 != null) && (i < objNameIconIdArray1.length); i++)
            {
              if ((projectMemmberList.containsValue(objNameIconIdArray1[i].getObjectName())) && 
                (!array.contains(objNameIconIdArray1[i])))
              {
                array.add(objNameIconIdArray1[i]);
              }
            }
            arrayOfNameIconIdStruct = (NameIconIdStruct[])array.toArray(new NameIconIdStruct[array.size()]);
            //YFJC_END
            if ((arrayOfNameIconIdStruct != null) && (arrayOfNameIconIdStruct.length > 0))
            {
              arrayOfString = new String[arrayOfNameIconIdStruct.length];
              for (i = 0; i < arrayOfNameIconIdStruct.length; i++)
                arrayOfString[i] = arrayOfNameIconIdStruct[i].getObjectName();
              OrgIconHelper.addNameIconIdStruct(3, arrayOfNameIconIdStruct);
            }
            ArraySorter.sort(arrayOfString);
          }
          /*if (paramAIFTreeNode.toString().equalsIgnoreCase(localRegistry.getString("group.TYPE_COMPONENT")))
          {
            localObject2 = (TCComponentGroupType)this.tcSession.getTypeComponent("Group");
            arrayOfNameIconIdStruct = ((TCComponentGroupType)localObject2).getGroupAndIconIdList();
            if ((arrayOfNameIconIdStruct != null) && (arrayOfNameIconIdStruct.length > 0))
            {
              arrayOfString = new String[arrayOfNameIconIdStruct.length];
              for (i = 0; i < arrayOfNameIconIdStruct.length; i++)
                arrayOfString[i] = arrayOfNameIconIdStruct[i].getObjectName();
              OrgIconHelper.addNameIconIdStruct(0, arrayOfNameIconIdStruct);
            }
          }
          else*/
          {
            int k;
            if (paramAIFTreeNode.toString().equalsIgnoreCase(localRegistry.getString("discipline.TYPE_COMPONENT")))
            {
              localObject2 = this.tcSession.getTypeComponent("Discipline");
              TCComponent[] disciplineComps1 = ((TCComponentType)localObject2).extent();
              //YFCJ_ADDED
              Vector array = new Vector();
              for (k = 0; (disciplineComps1 != null) && (k < disciplineComps1.length); k++)
              {
                String disName = disciplineComps1[k].getProperty("object_name");
                if (!rateLevelDisciplines.contains(disName))
                {
                  array.add(disciplineComps1[k]);
                }
              }
              TCComponent[] arrayOfTCComponent1 = (TCComponent[])array.toArray(new TCComponent[array.size()]);
              //YFCJ_END
              if ((arrayOfTCComponent1 != null) && (arrayOfTCComponent1.length > 0))
              {
                arrayOfString = new String[arrayOfTCComponent1.length];
                for (k = 0; k < arrayOfTCComponent1.length; k++)
                {
                  arrayOfString[k] = arrayOfTCComponent1[k].getTCProperty("discipline_name").getDisplayableValue();
                  this.disciplineMemberMap.put(arrayOfString[k].toString(), arrayOfTCComponent1[k]);
                }
              }
            }
            /*else if (paramAIFTreeNode.toString().equalsIgnoreCase(localRegistry.getString("role.TYPE_COMPONENT")))
            {
              localObject2 = (TCComponentRoleType)this.tcSession.getTypeComponent("Role");
              arrayOfNameIconIdStruct = ((TCComponentRoleType)localObject2).getRoleAndIconIdList();
              if ((arrayOfNameIconIdStruct != null) && (arrayOfNameIconIdStruct.length > 0))
              {
                arrayOfString = new String[arrayOfNameIconIdStruct.length];
                for (int j = 0; j < arrayOfNameIconIdStruct.length; j++)
                  arrayOfString[j] = arrayOfNameIconIdStruct[j].getObjectName();
                OrgIconHelper.addNameIconIdStruct(402, arrayOfNameIconIdStruct);
              }
            }
            else if (paramAIFTreeNode.toString().equalsIgnoreCase(localRegistry.getString("resourcepool.TYPE_COMPONENT")))
            {
              localObject2 = (TCComponentResourcePoolType)this.tcSession.getTypeComponent("ResourcePool");
              TCComponent[] arrayOfTCComponent2 = ((TCComponentResourcePoolType)localObject2).extent();
              if ((arrayOfTCComponent2 != null) && (arrayOfTCComponent2.length > 0))
              {
                arrayOfString = new String[arrayOfTCComponent2.length];
                for (k = 0; k < arrayOfTCComponent2.length; k++)
                {
                  arrayOfString[k] = arrayOfTCComponent2[k].toString();
                  this.resourcePoolMemberMap.put(arrayOfString[k].toString(), arrayOfTCComponent2[k]);
                }
              }
            }*/
          }
        }
        catch (TCException localTCException)
        {
          logger.error("TCException :" + localTCException);
        }
    }
    return arrayOfString;
  }

  public ScheduleTreeNode findNodeByNameAndType(InterfaceAIFComponent paramInterfaceAIFComponent)
  {
	System.out.println("findNodeByNameAndType paramInterfaceAIFComponent");
    String str = null;
    ScheduleTreeNode localObject = null;
    if ((paramInterfaceAIFComponent instanceof TCComponentUser))
      str = this.r.getString("user.TYPE_COMPONENT");
    /*else if ((paramInterfaceAIFComponent instanceof TCComponentGroup))
      str = this.r.getString("group.TYPE_COMPONENT");*/
    else if ((paramInterfaceAIFComponent instanceof TCComponentDiscipline))
      str = this.r.getString("discipline.TYPE_COMPONENT");
   /* else if ((paramInterfaceAIFComponent instanceof TCComponentRole))
      str = this.r.getString("role.TYPE_COMPONENT");
    else if ((paramInterfaceAIFComponent instanceof TCComponentResourcePool))
      str = this.r.getString("resourcepool.TYPE_COMPONENT");*/
    Iterator localIterator = this.nodeList.iterator();
    while (localIterator.hasNext())
    {
      ScheduleTreeNode localScheduleTreeNode = (ScheduleTreeNode)localIterator.next();
      if ((localScheduleTreeNode.getType().equals(str)) && (localScheduleTreeNode.getUserObject().toString().equals(paramInterfaceAIFComponent.toString())))
      {
        localObject = localScheduleTreeNode;
        break;
      }
    }
    return localObject;
  }

  public void setSchedule(TCComponent paramTCComponent)
  {
    this.schedule = paramTCComponent;
  }

  public TCComponent getSchedule()
  {
    return this.schedule;
  }

  public ScheduleTreeNode createNode(Object paramObject)
  {
    return createNode(paramObject, null);
  }

  public ScheduleTreeNode createNode(Object paramObject1, Object paramObject2)
  {
    return new ScheduleTreeNode(paramObject1, paramObject2)
    {
      public boolean isLeaf()
      {
        return getLevel() > 1;
      }
    };
  }

  public DefaultTreeModel getTreeModel()
  {
    return this.treeModel;
  }

  public ScheduleTreeNode getRootNode()
  {
    return this.rootNode;
  }

  public CheckCellRenderer getCheckRenderer()
  {
    return this.checkRenderer;
  }
}