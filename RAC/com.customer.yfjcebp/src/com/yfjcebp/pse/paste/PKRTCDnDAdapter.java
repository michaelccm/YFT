/*
 #=============================================================================
 #																			   
 #			opyright (c) 2009 Origin Enterprise Solution LTD.		  
 
 #																			   
 #=============================================================================
 # File name: PKRTCDnDAdapter.java
 # File description: 										   	
 #=============================================================================
 #	Date		Name		Action	Description of Change					   
 #	2013-4-17	MengYaWei  	Custom pse application the drag action	 								   
 #=============================================================================
 */
package com.yfjcebp.pse.paste;

import java.awt.Component;
import java.awt.Frame;
import java.awt.dnd.DragSource;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.common.InterfaceTCDnD;
import com.teamcenter.rac.common.TCDnDAdapter;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.Registry;
import com.yfjcebp.pse.paste.PKRCreater;

public class PKRTCDnDAdapter extends TCDnDAdapter {
	private Registry reg = Registry.getRegistry("com.yfjcebp.pse.paste.paste");
	//class
	//this
	
	public PKRTCDnDAdapter(InterfaceTCDnD interfacetcdnd) {
		super(interfacetcdnd);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void executeDrop(DropTargetDropEvent droptargetdropevent,
			TCComponent[] atccomponent,
			InterfaceAIFComponent interfaceaifcomponent, Frame frame) {
		// TODO Auto-generated method stub
    	
    	try {
    		PKRCreater pkrcreater = new PKRCreater(interfaceaifcomponent , atccomponent);
			
			if(pkrcreater.PEFFunAreaRevType.equals(pkrcreater.getPEFFunAreaRevision().getType())){
				String errMsg = null;
				String rtnStr = pkrcreater.isALLPKRTRevisionType();
				if("ALL".equals(rtnStr)){
					errMsg = pkrcreater.VerifyFuncArea();
					if(errMsg!=null){
						MessageBox.post(errMsg,"WARNING",MessageBox.WARNING);
					}else{
						pkrcreater.baseOnPKRTRevisionsCreatePKRItems();
					}
					return;
				}else if("SOME".equals(rtnStr)){
					//提示：PKR模板与其他类型的零组件应该分开粘贴到功能领域版本下。
					MessageBox.post(reg.getString("SomePKRT"),"WARNING",MessageBox.WARNING);
					return;
				}
			}
		} catch (TCException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		super.executeDrop(droptargetdropevent, atccomponent, interfaceaifcomponent,
				frame);
	}
    public static void addDropListener(InterfaceTCDnD interfacetcdnd)
    {
        if((interfacetcdnd instanceof Component) && isDragDropEnabled())
        {
        	PKRTCDnDAdapter pkrtcdndadapter = new PKRTCDnDAdapter(interfacetcdnd);
            new DropTarget((Component)interfacetcdnd, 3, pkrtcdndadapter);
        }
    }
    public static void addDragListener(InterfaceTCDnD interfacetcdnd)
    {
        if((interfacetcdnd instanceof Component) && isDragDropEnabled())
        {
            PKRTCDnDAdapter pkrtcdndadapter = new PKRTCDnDAdapter(interfacetcdnd);
            DragSource dragsource = new DragSource();
            dragsource.createDefaultDragGestureRecognizer((Component)interfacetcdnd, 3, pkrtcdndadapter);
        }
    }
}
