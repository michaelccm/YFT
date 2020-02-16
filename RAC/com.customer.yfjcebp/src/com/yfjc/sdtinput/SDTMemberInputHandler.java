package com.yfjc.sdtinput;

import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.Vector;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentGroupMember;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentProject;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.kernel.TCTextService;
import com.teamcenter.rac.pse.AbstractPSEApplication;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.Registry;
import com.yfjc.sdtinput.SDTMemberInputDialog;

public class SDTMemberInputHandler extends AbstractHandler {

    private TCSession session=null;
    private Registry registry =null;
    
    @Override
    public Object execute(ExecutionEvent arg0) throws ExecutionException {
//        registry = Registry.getRegistry("com.yfjc.sdtinput.sdtinput");
        final String BURFLE_NAME = "com.yfjc.sdtinput.sdtinput_locale";
        final ResourceBundle registry = ResourceBundle.getBundle(BURFLE_NAME);
        final Vector vec=new Vector();
        session = (TCSession) AIFUtility.getCurrentApplication()
        .getSession();
        try {
            TCComponentBOMLine topline=((AbstractPSEApplication)AIFUtility.getCurrentApplication()).getBOMWindow().getTopBOMLine();
            
            
            InterfaceAIFComponent[] targets = AIFUtility.getCurrentApplication().getTargetComponents();
            if(targets==null || targets.length==0){
                MessageBox.post(registry.getString("messagebox.info1"), "WARNING",
                        MessageBox.WARNING);
                return null;
            }
            for(int i=0;i<targets.length;i++){
                if (targets[i] instanceof TCComponentBOMLine) {
                    TCComponentBOMLine line = (TCComponentBOMLine) targets[i];
                    if(line.isRoot()){
                        MessageBox.post(registry.getString("messagebox.info2"), "WARNING",
                                MessageBox.WARNING);
                        return null;
                    }
                    vec.add(line);
                }
            }
           TCComponentItem item= topline.getItem();
           TCComponentProject prj=getProject(item);
           //System.out.println("prj --->"+prj);
           if(prj == null)
        	   return null;
           //System.out.println(" prj === "+prj.getProperty("object_name"));
           TCComponent team=prj.getReferenceProperty("project_team");
           if(team == null)
        	   return null;
           TCComponent[] members=team.getReferenceListProperty("project_members");
           if(members == null)
        	   return null;
           final HashMap memberMap=new HashMap();
           for(int i=0;i<members.length;i++){
               if(members[i] instanceof TCComponentGroupMember){
                   TCComponentGroupMember member=(TCComponentGroupMember) members[i];
                   StringBuffer sbf=new StringBuffer();
//                   sbf.append(member.getGroup().toDisplayString());
//                   sbf.append("/");
//                   sbf.append(member.getRole().toDisplayString());
//                   sbf.append("/");
//                   String user=member.getUser().toDisplayString();
//                   sbf.append(user);
                   //modify by wuh  2014-6-26
                   String user=member.getUser().toDisplayString();
                   sbf.append(user);
                   sbf.append("/");
                   sbf.append(member.getRole().toDisplayString());
                   sbf.append("/");
                   sbf.append(member.getGroup().toDisplayString());                                                               
                   memberMap.put(sbf.toString(), member);
               }
           }
           SDTMemberInputDialog dialog=new SDTMemberInputDialog(vec,memberMap);
           ((AbstractPSEApplication)AIFUtility.getCurrentApplication()).getBOMWindow().refresh();
        } catch (TCException e1) {
            e1.printStackTrace();
        }
        return null;
    }

    /**
     * get the TCProject object assigned to current Dataset
     * 获得指派给当前数捛�的项��
     */
    private TCComponentProject getProject(TCComponentItem item) {
        String s = "";
        TCComponentProject project = null;
        String[] projectIds;
        try {
            projectIds = item.getProperty("project_ids").split(",");
            //System.out.println("projectIds .length  "+projectIds.length);
            if(projectIds.length>0){
            	//System.out.println("project_id --->"+projectIds[0].trim());
                if(!projectIds[0].trim().equals("")){
                    project = searchProject(projectIds[0].trim());
                }
            }
        } catch (TCException e) {
            e.printStackTrace();
        }
        return project;
    }
    
    /**
     * get TCProject object by searching ID
     * 根据项目ID查找到项�
     */
    private TCComponentProject searchProject(String projectId) {
        TCComponentProject project = null;
        session = (TCSession) AIFUtility.getCurrentApplication().getSession();
        try {
            TCTextService tcTextService = session.getTextService();
            String askKey[] = { tcTextService.getTextValue("ProjectID") };
            String askValue[] = { projectId };
            InterfaceAIFComponent objects[] = session.search("Projects...", askKey,
                    askValue);
            if (objects != null && objects.length > 0) {
                project = (TCComponentProject) objects[0];
            }
            
        } catch (TCException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return project;
    }
}
