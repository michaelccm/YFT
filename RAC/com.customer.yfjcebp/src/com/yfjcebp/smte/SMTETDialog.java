/*jadclipse*/// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.

package com.yfjcebp.smte;
import com.teamcenter.rac.stylesheet.*;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Panel;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.teamcenter.rac.kernel.*;

import com.teamcenter.rac.util.VerticalLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

// Referenced classes of package com.teamcenter.rac.stylesheet:
//            PropertyNameLabel, PropertyArray, InterfaceTitledPropertyComponent, InterfaceBufferedPropertyComponent, 
//            InterfaceStylePropertyComponent

public class SMTETDialog extends JPanel
    implements InterfaceTitledPropertyComponent, InterfaceBufferedPropertyComponent, InterfaceStylePropertyComponent
{
	Object obj;
	SMTETPanel sp = new SMTETPanel();
    public SMTETDialog()
    {
    	
    	
    	super(new VerticalLayout(0, 0, 0, 0, 0));
        bordered = false; 
        setOpaque(false);
        jbutton=new JButton("获取");
        array = new PropertyArray1();
        titleBorder = BorderFactory.createTitledBorder("");
        add("1.2.right.top",array);
        add("1.1.left.top", jbutton);
        jbutton.addActionListener(new ActionListener(){
        	
			@Override
			public void actionPerformed(ActionEvent e) {
				JFrame jf = new JFrame();
				jDialog1=new JDialog(jf,"Dialog",true);
				JButton bt1 = new JButton("确定");
				JButton bt2 = new JButton("取消");
				new SMTETPanel();
				sp.addParameters(null);
				sp.addParameters(array.getComp());
				Panel bp = new Panel();
				bp.add(bt1);
				bp.add(bt2);
				bt2.addActionListener(new ActionListener(){
			         	
					@Override
					public void actionPerformed(ActionEvent e) {

			 			jDialog1.dispose();	
			 		}
			    });
			    bt1.addActionListener(new ActionListener(){
			    	
			    	@Override
					public void actionPerformed(ActionEvent e) {
			    		sp.getOrcreateParameterComp(array.getComp());
				 		if(sp.comp != null){
				 			array.addaction(sp.comp);
				 			jDialog1.dispose();
				 		}
			    	}
			    	        
				});
			    
				jDialog1.getContentPane().add(bp,BorderLayout.SOUTH);
				jDialog1.getContentPane().add(sp,BorderLayout.CENTER);
				jDialog1.setSize(800,600);
				Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		         Dimension frameSize = jDialog1.getSize();
		         if (frameSize.height > screenSize.height) {
		             frameSize.height = screenSize.height;
		         }
		         if (frameSize.width > screenSize.width) {
		             frameSize.width = screenSize.width;
		         }
		         jDialog1.setLocation((screenSize.width - frameSize.width) / 2,
		                        (screenSize.height - frameSize.height) / 2);
		        
		         jDialog1.setVisible(true);
		        
		         jDialog1.setModal(true);
			}
        	
        });
       
    }
   

   

	@Override
	public void setProperty(String s)
    {
        array.setProperty(s);
    }

    @Override
	public String getProperty()
    {
        return array.getProperty();
    }

    public boolean getMandatory()
    {
        return array.getMandatory();
    }

    @Override
	public void setMandatory(boolean flag)
    {
        array.setMandatory(flag);
    }

    @Override
	public boolean isMandatory()
    {
        return array.getMandatory();
    }

    @Override
	public void setModifiable(boolean flag)
    {
        array.setModifiable(flag);
    }

    public boolean getModifiable()
    {
        return array.getModifiable();
    }

    @Override
	public void setBordered(boolean flag)
    {
        bordered = flag;
        if(bordered)
        {
            setBorder(titleBorder);
        } else
        {
            setBorder(null);
        }
    }

   	private void setBorder(TitledBorder titleBorder2) {
		// TODO Auto-generated method stub
		
	}


	public boolean getBordered()
    {
        return bordered;
    }

   

   

    public PropertyArray1 getArray()
    {
        return array;
    }

  

    @Override
	public void setVisible(boolean flag)
    {
        super.setVisible(flag);
    }

    @Override
	public Object getEditableValue()
    {
       return array.getEditableValue();
        
    }

    @Override
	public void setUIFValue(Object obj)
    {
        array.setUIFValue(obj);
    }

    @Override
	public void load(TCComponentType tccomponenttype)
        throws Exception
    {
        if(tccomponenttype != null)
        {
            TCPropertyDescriptor tcpropertydescriptor = tccomponenttype.getPropertyDescriptor(getProperty());
           System.out.println("getProperty()============="+getProperty()); 
            load(tcpropertydescriptor);
        }
    }

    @Override
	public void load(TCPropertyDescriptor tcpropertydescriptor)
        throws Exception
    {
       
        array.load(tcpropertydescriptor);
        if(getBordered())
        {
          
            if(!tcpropertydescriptor.isDisplayable())
                setVisible(false);
        }
    }

    @Override
	public void load(TCComponent tccomponent)
        throws Exception
    {
       
        array.load(tccomponent);
        if(getBordered())
        {
           
            if(!array.isDisplayable())
                setVisible(false);
        }
    }

    @Override
	public void load(TCProperty tcproperty)
        throws Exception
    {
       
        array.load(tcproperty);
        if(getBordered())
        {
           
            if(!array.isDisplayable())
                setVisible(false);
        }
    }

    @Override
	public void save(TCComponent tccomponent)
        throws Exception
    {
        array.save(tccomponent);
    }

    @Override
	public void save(TCProperty tcproperty)
        throws Exception
    {
        array.save(tcproperty);
    }

    @Override
	public TCProperty saveProperty(TCComponent tccomponent)
        throws Exception
    {
        return array.saveProperty(tccomponent);

    	
    }

    @Override
	public TCProperty saveProperty(TCProperty tcproperty)
        throws Exception
    {
        return array.saveProperty(tcproperty);
    }

    public TCProperty getPropertyToSave(TCComponent tccomponent)
        throws Exception
    {
    	
        return array.getPropertyToSave(tccomponent);
    }

    public TCProperty getPropertyToSave(TCProperty tcproperty)
        throws Exception
    {
    	

        return array.getPropertyToSave(tcproperty);
       
    }

    @Override
	public boolean isPropertyModified(TCComponent tccomponent)
        throws Exception
    {
        return array.isPropertyModified(tccomponent);
    }

    @Override
	public boolean isPropertyModified(TCProperty tcproperty)
        throws Exception
    {
        return array.isPropertyModified(tcproperty);
    }
    public JButton jbutton;
    protected boolean bordered;
    protected TitledBorder titleBorder;
    protected PropertyArray1 array;
    SMTETPanel st = new SMTETPanel();
    
    JDialog jDialog1=null;
	@Override
	public void setStyle(String s) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setDisplayableName(boolean flag) {
		// TODO Auto-generated method stub
		
	}
}


/*
	DECOMPILATION REPORT

	Decompiled from: E:\work\tc9\portal\plugins\com.teamcenter.rac.common_9000.1.1.jar
	Total time: 86 ms
	Jad reported messages/errors:
The class file version is 50.0 (only 45.3, 46.0 and 47.0 are supported)
	Exit status: 0
	Caught exceptions:
*/