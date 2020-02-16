/*package com.teamcenter.rac.pse;


#=============================================================================
#																			   
#			opyright (c) 2009 Origin Enterprise Solution LTD.		  

#																			   
#=============================================================================
# File name: MyPSEApplicationPanel.java
# File description: 										   	
#=============================================================================
#	Date		Name		Action	Description of Change					   
#	2013-4-17	MengYaWei  	To register the custom TCDnDAdapter(PKRTDnDAdapter)		 								   
#=============================================================================




import com.teamcenter.rac.pse.common.BOMTreeTable;
import com.teamcenter.rac.pse.services.IPSEService;
import com.yfjcebp.pse.paste.PKRTCDnDAdapter;

public class MyPSEApplicationPanel extends PSEApplicationPanel {

	public MyPSEApplicationPanel(IPSEService ipseservice) {
		super(ipseservice);
		System.out.println("----MyPSEApplicationPanel---");
		// TODO Auto-generated constructor stub
		BOMTreeTable bomTreeTable = this.getActiveBOMTreeTable();
		PKRTCDnDAdapter.addDragListener(bomTreeTable);
		PKRTCDnDAdapter.addDropListener(bomTreeTable);
	}

}
*/