package com.yfjc.lccreport;

import java.io.File;
import java.util.ResourceBundle;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentContextList;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCComponentQuery;
import com.teamcenter.rac.kernel.TCComponentQueryType;
import com.teamcenter.rac.kernel.TCComponentTcFile;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.kernel.TCTextService;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.Registry;

public class LCCReportHandler extends AbstractHandler {

    private TCSession session = null;
    private Registry registry =null;
    private String parentFolderName="EBP报表配置模板";
    private String dsName="LCC报表模板";
    private String searchName="YFJC_Search For Active Projects";
    private String option1 = "YFJC_LaborType";
    private String option2 = "YFJC_LCC_Report_Devision_mapping";
    private String option3 = "YFJC_LCC_Report_UserName_mapping";
    
    @Override
    public Object execute(ExecutionEvent arg0) throws ExecutionException {
        session = (TCSession) AIFUtility.getCurrentApplication().getSession();
//        registry = Registry.getRegistry("com.yfjc.lccreport.lccreport");
        final String BURFLR_NAME = "com.yfjcebp.importdata.utils.utils_locale";
    	final ResourceBundle registry = ResourceBundle.getBundle(BURFLR_NAME);
      
        File file=getTemplate();
        if(file==null){
            MessageBox.post(registry.getString("messagebox.template"), "WARNING",
                    MessageBox.WARNING);
            return null;
        }
        
        TCComponent[] prjs = query(this.session,
                searchName,new String[] { "is_active" }, new String[] { "TRUE" });
        if (prjs == null || prjs.length == 0) {
            MessageBox.post(registry.getString("messagebox.search"), "WARNING",
                    MessageBox.WARNING);
            return null;
        }
        
        String[]  array=session.getPreferenceService().getStringArray(
                4, option1);
        if(array==null || array.length==0){
            MessageBox.post(registry.getString("messagebox.option")+option1, "WARNING",
                    MessageBox.WARNING);
            return null;
        }
        
        array=session.getPreferenceService().getStringArray(
                4, option2);
        if(array==null || array.length==0){
            MessageBox.post(registry.getString("messagebox.option")+option2, "WARNING",
                    MessageBox.WARNING);
            return null;
        }
        
        array=session.getPreferenceService().getStringArray(
                4, option3);
        if(array==null || array.length==0){
            MessageBox.post(registry.getString("messagebox.option")+option3, "WARNING",
                    MessageBox.WARNING);
            return null;
        }
        
        LCCReportCommand cmd = new LCCReportCommand(prjs,file);
        try {
            cmd.executeModal();
        } catch (Exception e) {
            e.printStackTrace();
        }
       
        return null;
    }

    public static TCComponent[] query(TCSession session, String query_name,
            String[] arg1, String[] arg2) {
        TCComponentContextList imancomponentcontextlist = null;
        TCComponent[] component = null;
        try {
            TCComponentQueryType imancomponentquerytype = (TCComponentQueryType) session
                    .getTypeComponent("ImanQuery");
            TCComponentQuery imancomponentquery = (TCComponentQuery) imancomponentquerytype
                    .find(query_name);
            TCTextService imantextservice = session.getTextService();
            String[] queryAttribute = new String[arg1.length];
            for (int i = 0; i < arg1.length; ++i)
                queryAttribute[i] = imantextservice.getTextValue(arg1[i]);

            String[] queryValues = new String[arg2.length];
            for (int i = 0; i < arg2.length; ++i)
                queryValues[i] = arg2[i];

            imancomponentcontextlist = imancomponentquery
                    .getExecuteResultsList(queryAttribute, queryValues);
            component = imancomponentcontextlist.toTCComponentArray();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return component;
    }

    public File getTemplate() {
        File theFile = null;
        try {
            TCComponent[] comps = query(this.session, "General...",
                    new String[] { "object_name" },
                    new String[] { parentFolderName });
            if (comps != null && comps.length > 0) {
                if (comps[0] instanceof TCComponentFolder) {
                    TCComponentFolder parentFolder = (TCComponentFolder) comps[0];
                    AIFComponentContext[] context1 = parentFolder.getChildren();
                    for (int i = 0; i < context1.length; i++) {
                        TCComponent comp1 = (TCComponent) context1[i]
                                .getComponent();
                        if (comp1 instanceof TCComponentDataset) {
                            TCComponentDataset ds = (TCComponentDataset) comp1;
                            if (ds.getType().contains("MSExcel")
                                    && ds.getProperty("object_name").equals(
                                            dsName)) {
                                TCComponentTcFile[] tcfiles = ds.getTcFiles();
                                if (tcfiles != null && tcfiles.length > 0) {
                                    theFile = tcfiles[0].getFmsFile();
                                }
                            }
                        }
                    }
                }
            }
        } catch (TCException e) {
            e.printStackTrace();
        }
        return theFile;
    }
}
