package com.sf.custom.propertybean;

import java.util.Map;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCPropertyDescriptor;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.controls.TextControl;
import com.teamcenter.rac.viewer.stylesheet.beans.TextfieldPropertyBean;

public class PropertyBean extends TextfieldPropertyBean {
	private Text textField;

	public PropertyBean(FormToolkit arg0, Composite arg1, boolean arg2, Map arg3) {
		super(arg0, arg1, arg2, arg3);
		//System.out.println("----PropertyBean 1---");
	}

	public PropertyBean(TextControl arg0) {
		super(arg0);
		//System.out.println("----PropertyBean 2---");
	}

	@Override
	public void load(TCPropertyDescriptor parmTCPropertyDescriptor) throws Exception {
		super.load(parmTCPropertyDescriptor);
		//System.out.println("load......");
		textField = this.getTextField();
		textField.setText("ÇëË«»÷Éè¶¨id Text ");
		final String type = parmTCPropertyDescriptor.getTypeComponent().getType();
		textField.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseUp(MouseEvent arg0) {
			}
			
			@Override
			public void mouseDown(MouseEvent arg0) {
			}
			
			@Override
			public void mouseDoubleClick(MouseEvent arg0) {
				AbstractAIFUIApplication app = AIFUtility.getCurrentApplication();
				//System.out.println("type--->"+type);
				if("N9_UPGCreI".equals(type)){
					new SetUPGDialog(app,app.getDesktop(),textField,1);
				}else if("N9_PARTSCreI".equals(type)){
					new SetUPGDialog(app,app.getDesktop(),textField,2);
				}else if("Item".equals(type)){
					new SetUPGDialog(app,app.getDesktop(),textField,3);
				} 
			}
		});
	}
}
