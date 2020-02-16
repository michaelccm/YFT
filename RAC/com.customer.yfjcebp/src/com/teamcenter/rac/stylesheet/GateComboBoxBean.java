package com.teamcenter.rac.stylesheet;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Date;
import java.util.Vector;

import javax.swing.JComboBox;

import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentProject;
import com.teamcenter.rac.kernel.TCComponentSchedule;
import com.teamcenter.rac.kernel.TCComponentScheduleTask;
import com.teamcenter.rac.kernel.TCComponentType;
import com.teamcenter.rac.kernel.TCDateFormat;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCPropertyDescriptor;
import com.teamcenter.rac.util.Registry;
import com.teamcenter.rac.util.log.Debug;

/**
 * @author liujm
 * @modify zhanggl 4-9 Judge jci6_TaskType. Get project to project_list property
 */
public class GateComboBoxBean extends JComboBox
implements InterfacePropertyComponent, InterfaceBufferedPropertyComponent{

	
	   /**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	public GateComboBoxBean()
	    {
	        modifiable = true;
	        property = "";
	        Registry registry = Registry.getRegistry(this);
	        bkgrdColor = AbstractRendering.getReadOnlyBackgroundColor(registry);
	        foregrdColor = AbstractRendering.getReadOnlyForegroundColor(registry);
	    }

	    @Override
		public void setProperty(String s)
	    {
	        property = s;
	    }

	    @Override
		public String getProperty()
	    {
	        return property;
	    }

	    @Override
		public void setModifiable(boolean flag)
	    {
	        modifiable = flag;
	        setEnabled(modifiable);
	    }

	    public boolean getModifiable()
	    {
	        return modifiable;
	    }

	    @Override
		public void setVisible(boolean flag)
	    {
	        super.setVisible(flag);
	    }

	    @Override
		public Object getEditableValue()
	    {
	        Object obj = getSelectedItem();
	        if(obj == null)
	            obj = getSelectedItem();
	        return obj;
	    }

	    @Override
		public void setUIFValue(Object obj)
	    {
	        if(obj != null)
	            this.setSelectedItem(obj);
	        else
	        	this.setSelectedItem(null);
	    }

	    @Override
		public void load(TCComponentType tccomponenttype)
	        throws Exception
	    {
	        if(tccomponenttype != null)
	        {
	            TCPropertyDescriptor tcpropertydescriptor = tccomponenttype.getPropertyDescriptor(property);
	            load(tcpropertydescriptor);
	        }
	    }

	    @Override
		public void load(TCPropertyDescriptor tcpropertydescriptor)
	        throws Exception
	    {
	        descriptor = tcpropertydescriptor;
	        setEnabled(tcpropertydescriptor.isEnabled() && modifiable);
	        if(tcpropertydescriptor.isRequired())
	            setMandatory(true);
//	        TCComponentListOfValues tccomponentlistofvalues = getLOV();
//	        if(tccomponentlistofvalues != null)
//	            setLOVComponent(tccomponentlistofvalues);
	        if(!tcpropertydescriptor.isDisplayable())
	            setVisible(false);
	    }

	    @Override
		public void load(TCComponent tccomponent)
	        throws Exception
	    {
	        if(property != null)
	        {
	            TCProperty tcproperty = tccomponent.getTCProperty(property);
	            load(tcproperty);
	        }
	    }

	    @Override
		public void load(TCProperty tcproperty)
	        throws Exception
	    {
	        TCComponent tccomponent = tcproperty.getTCComponent();
	        tcProperty = tcproperty;
	        property = tcproperty.getPropertyName();
	        if(property != null)
	        {
	            if(descriptor == null)
	            {
	                TCPropertyDescriptor tcpropertydescriptor = tccomponent.getTypeComponent().getPropertyDescriptor(property);
	                load(tcpropertydescriptor);
	            }
	            if(isEnabled() && !tcproperty.isEnabled())
	                setEnabled(false);
	            //找到当前对象所属项目，并通过TC_Program_Preferred_Items关系找到时间表，找到里程碑，添加到此组件中
	            //TCComponentProject currentproject = (TCComponentProject) tccomponent.getReferenceProperty("owning_project");
	            TCComponentProject currentproject = null;
	            TCComponent[] tempProjects =  tccomponent.getReferenceListProperty("project_list");
	            if(tempProjects.length > 0 ){
	            	currentproject = (TCComponentProject)tempProjects[0];
	            }
	            if(currentproject!=null){
	            	 TCComponent[] preferred_Items = null;
	 				try {
	 					preferred_Items = currentproject.getReferenceListProperty("TC_Program_Preferred_Items");
	 				} catch (TCException e) {
	 					e.printStackTrace();
	 				}
	 				int preferred_ItemLength = preferred_Items.length;
	 				//System.out.println("preferred_ItemLength = "+preferred_ItemLength);
	 				if(preferred_ItemLength>0){
	 					int itemCount = this.getItemCount();
	 					Vector<Object> veComponent = new Vector<Object>();
	 					for(int i=0;i<itemCount;i++){
	 						veComponent.add(this.getItemAt(i));
	 					}
	 					if(!veComponent.contains(null)){
	 						addItem(null);
	 					}
	 					for(int i=0;i<preferred_ItemLength;i++){
		 					TCComponent preferred_Item = preferred_Items[i];
		 					if(preferred_Item instanceof TCComponentSchedule){
		 						TCComponentSchedule schedule = (TCComponentSchedule) preferred_Item;
		 						try {
		 							TCComponentScheduleTask scheduleTask = (TCComponentScheduleTask) schedule.getReferenceProperty("sch_summary_task");
		 							TCComponent[] childrenTasks = scheduleTask.getReferenceListProperty("child_task_taglist");
		 							int childrenTaskNum = childrenTasks.length;
		 							//System.out.println("childrenTaskNum = "+childrenTaskNum);
		 							for(int j=0;j<childrenTaskNum;j++){
		 								TCComponent childScheduleTask = childrenTasks[j];
		 								//if(childScheduleTask.getIntProperty("task_type")==1&&childScheduleTask.getProperty("jci6_TaskType").contains("gatetype")){
		 								if(childScheduleTask.getIntProperty("task_type")==1&&childScheduleTask.getProperty("jci6_TaskType")!= null){
			 								if(!childScheduleTask.getProperty("jci6_TaskType").equals("")){
			 									TCComponentItemRevision childScheduleTaskRevision = ((TCComponentItem)childScheduleTask).getLatestItemRevision();
			 									if(!veComponent.contains(childScheduleTaskRevision)){
			 										addItem(childScheduleTaskRevision); 
			 									}
			 								}
		 								}
		 								TCComponent[] childrenTasks2 = childScheduleTask.getReferenceListProperty("child_task_taglist");
			 							int childrenTaskNum2 = childrenTasks2.length;
		 								for(int k=0;k<childrenTaskNum2;k++){
		 									TCComponent childScheduleTask2 = childrenTasks2[k];
			 								if(childScheduleTask2.getIntProperty("task_type")==1&&childScheduleTask2.getProperty("jci6_TaskType")!= null){
				 								if(!childScheduleTask2.getProperty("jci6_TaskType").equals("")){
				 									TCComponentItemRevision childScheduleTaskRevision = ((TCComponentItem)childScheduleTask2).getLatestItemRevision();
				 									if(!veComponent.contains(childScheduleTaskRevision)){
				 										addItem(childScheduleTaskRevision); 
				 									}
				 								}
			 								}
		 								}
		 							}
		 						} catch (TCException e) {
		 							e.printStackTrace();
		 						}
		 						break;
		 					}
		 				}
	 					//System.out.println("166 this.getItemCount()=="+this.getItemCount());
	 				}
	            }
	            if(tcproperty.getNullVerdict())
	            {
	                originalValue = null;
	                setSelectedItem(null);
	                return;
	            }
	            Object obj = null;
	            switch(tcproperty.getPropertyType())
	            {
	            default:
	                break;

	            case 1: // '\001'
	                char c = tcproperty.getCharValue();
	                obj = Character.valueOf(c);
	                break;

	            case 2: // '\002'
	                Date date = tcproperty.getDateValue();
	                TCDateFormat tcdateformat = new TCDateFormat(tccomponent.getSession());
	                java.text.SimpleDateFormat simpledateformat = tcdateformat.askDefaultDateFormat();
	                if(date != null)
	                    obj = simpledateformat.format(date);
	                break;

	            case 3: // '\003'
	                double d = tcproperty.getDoubleValue();
	                obj = Double.valueOf(d);
	                break;

	            case 4: // '\004'
	                float f = tcproperty.getFloatValue();
	                obj = Double.valueOf(f);
	                break;

	            case 5: // '\005'
	                int i = tcproperty.getIntValue();
	                obj = Integer.valueOf(i);
	                break;

	            case 6: // '\006'
	                boolean flag = tcproperty.getLogicalValue();
	                obj = Boolean.valueOf(flag);
	                break;

	            case 7: // '\007'
	                short word0 = tcproperty.getShortValue();
	                obj = Integer.valueOf(word0);
	                break;

	            case 8: // '\b'
	                obj = tcproperty.getUIFValue();
	                if(obj == null)
	                    obj = tcproperty.getStringValue();
	                break;

	            case 12: // '\f'
	                obj = tcproperty.getNoteValue();
	                break;

	            case 0: // '\0'
	            case 9: // '\t'
	            case 10: // '\n'
	            case 11: // '\013'
	            case 13: // '\r'
	                obj = tcproperty.getReferenceValue();
	                break;
	            }
	            originalValue = tcproperty.getPropertyData();
	            setSelectedItem(obj);
	        }
	    }

	    @Override
		public void save(TCComponent tccomponent)
	        throws Exception
	    {
	        TCProperty tcproperty = getPropertyToSave(tccomponent);
	        if(savable)
	            tccomponent.setTCProperty(tcproperty);
	    }

	    @Override
		public void save(TCProperty tcproperty)
	        throws Exception
	    {
	        TCProperty tcproperty1 = getPropertyToSave(tcproperty);
	        if(savable && tcproperty1 != null)
	            tcproperty1.getTCComponent().setTCProperty(tcproperty);
	    }

	    @Override
		public TCProperty saveProperty(TCComponent tccomponent)
	        throws Exception
	    {
	        TCProperty tcproperty = getPropertyToSave(tccomponent);
	        if(savable)
	            return tcproperty;
	        else
	            return null;
	    }

	    @Override
		public TCProperty saveProperty(TCProperty tcproperty)
	        throws Exception
	    {
	        TCProperty tcproperty1 = getPropertyToSave(tcproperty);
	        if(savable)
	            return tcproperty1;
	        else
	            return null;
	    }

	    public TCProperty getPropertyToSave(TCComponent tccomponent)
	        throws Exception
	    {
	        if(property != null)
	        {
	            TCProperty tcproperty = tccomponent.getTCProperty(property);
	            return getPropertyToSave(tcproperty);
	        } else
	        {
	            savable = false;
	            return null;
	        }
	    }

	    public TCProperty getPropertyToSave(TCProperty tcproperty)
	        throws Exception
	    {
	        savable = false;
	        if(tcproperty != null)
	        {
	            if(!tcproperty.isEnabled() || !modifiable)
	            {
	                if(Debug.isOn("stylesheet,form,property,properties"))
	                    Debug.println((new StringBuilder()).append("PropertyLOVCombobox: save propName=").append(property).append(" not modifiable, skip.").toString());
	                return null;
	            }
	            Object obj = this.getSelectedItem();
	            String s = obj != null ? obj.toString() : "";
	            String s1 = tcproperty.getUIFValue();
	            boolean flag = originalValue == null;
	            Registry registry = Registry.getRegistry(this);
	            String s2 = (new StringBuilder()).append(getProperty()).append(" ").append(registry.getString("cannotSaveUserInput")).toString();
	            TCException tcexception = new TCException(s2);
	            tcexception.errorSeverities = (new int[] {
	                2
	            });
	            try
	            {
	                switch(tcproperty.getPropertyType())
	                {
	                default:
	                    break;

	                case 1: // '\001'
	                    if(s.length() > 0)
	                    {
	                        char c = s.charAt(0);
	                        if(flag || c != originalValue.toString().charAt(0))
	                        {
	                            tcproperty.setCharValueData(c);
	                            savable = true;
	                            if(Debug.isOn("stylesheet,form,property,properties"))
	                                Debug.println((new StringBuilder()).append("PropertyLOVCombobox:save propName=").append(property).append(",set char = ").append(c).toString());
	                        } else
	                        if(Debug.isOn("stylesheet,form,property,properties"))
	                            Debug.println((new StringBuilder()).append("PropertyLOVCombobox:save propName=").append(property).append(", value not changed, skip").toString());
	                        break;
	                    }
	                    if(flag)
	                        break;
	                    if(Debug.isOn("stylesheet,form,property,properties"))
	                        Debug.println((new StringBuilder()).append("PropertyLOVCombobox: save propName=").append(property).append(", set to null.").toString());
	                    tcproperty.setNullVerdict(true);
	                    savable = true;
	                    break;

	                case 2: // '\002'
	                    Date date = null;
	                    if(obj instanceof Date)
	                        date = (Date)obj;
	                    else
	                    if(s != null && s.length() > 0)
	                    {
	                        TCDateFormat tcdateformat = new TCDateFormat(tcproperty.getTCComponent().getSession());
	                        java.text.SimpleDateFormat simpledateformat = tcdateformat.askDefaultDateFormat();
	                        date = simpledateformat.parse(s);
	                    } else
	                    if(!flag)
	                    {
	                        if(Debug.isOn("stylesheet,form,property,properties"))
	                            Debug.println((new StringBuilder()).append("PropertyLOVCombobox: save propName=").append(property).append(", set to null.").toString());
	                        tcproperty.setNullVerdict(true);
	                        savable = true;
	                    }
	                    if(date != null && !date.equals(originalValue))
	                    {
	                        tcproperty.setDateValueData(date);
	                        savable = true;
	                        if(Debug.isOn("stylesheet,form,property,properties"))
	                            Debug.println((new StringBuilder()).append("PropertyLOVCombobox:save propName=").append(property).append(",set date = ").append(date).toString());
	                        break;
	                    }
	                    if(Debug.isOn("stylesheet,form,property,properties"))
	                        Debug.println((new StringBuilder()).append("PropertyLOVCombobox:save propName=").append(property).append(", value not changed, skip").toString());
	                    break;

	                case 3: // '\003'
	                    Double double1 = null;
	                    if(obj instanceof Double)
	                        double1 = (Double)obj;
	                    else
	                    if(s != null && s.length() > 0)
	                        double1 = Double.valueOf(s);
	                    else
	                    if(!flag)
	                    {
	                        if(Debug.isOn("stylesheet,form,property,properties"))
	                            Debug.println((new StringBuilder()).append("PropertyLOVCombobox: save propName=").append(property).append(", set to null.").toString());
	                        tcproperty.setNullVerdict(true);
	                        savable = true;
	                    }
	                    if(double1 != null && !double1.equals(originalValue))
	                    {
	                        tcproperty.setDoubleValueData(double1.doubleValue());
	                        savable = true;
	                        if(Debug.isOn("stylesheet,form,property,properties"))
	                            Debug.println((new StringBuilder()).append("PropertyLOVCombobox:save propName=").append(property).append(",set double = ").append(double1).toString());
	                        break;
	                    }
	                    if(Debug.isOn("stylesheet,form,property,properties"))
	                        Debug.println((new StringBuilder()).append("PropertyLOVCombobox:save propName=").append(property).append(", value not changed, skip").toString());
	                    break;

	                case 4: // '\004'
	                    Float float1 = null;
	                    if(obj instanceof Float)
	                        float1 = (Float)obj;
	                    else
	                    if(s != null && s.length() > 0)
	                        float1 = Float.valueOf(s);
	                    else
	                    if(!flag)
	                    {
	                        if(Debug.isOn("stylesheet,form,property,properties"))
	                            Debug.println((new StringBuilder()).append("PropertyLOVCombobox: save propName=").append(property).append(", set to null.").toString());
	                        tcproperty.setNullVerdict(true);
	                        savable = true;
	                    }
	                    if(float1 != null && !float1.equals(originalValue))
	                    {
	                        tcproperty.setFloatValue(float1.floatValue());
	                        savable = true;
	                        if(Debug.isOn("stylesheet,form,property,properties"))
	                            Debug.println((new StringBuilder()).append("PropertyLOVCombobox:save propName=").append(property).append(",set float = ").append(float1).toString());
	                        break;
	                    }
	                    if(Debug.isOn("stylesheet,form,property,properties"))
	                        Debug.println((new StringBuilder()).append("PropertyLOVCombobox:save propName=").append(property).append(", value not changed, skip").toString());
	                    break;

	                case 5: // '\005'
	                    Integer integer = null;
	                    if(obj instanceof Integer)
	                        integer = (Integer)obj;
	                    else
	                    if(s != null && s.length() > 0)
	                        integer = Integer.valueOf(s);
	                    else
	                    if(!flag)
	                    {
	                        if(Debug.isOn("stylesheet,form,property,properties"))
	                            Debug.println((new StringBuilder()).append("PropertyLOVCombobox: save propName=").append(property).append(", set to null.").toString());
	                        tcproperty.setNullVerdict(true);
	                        savable = true;
	                    }
	                    if(integer != null && !integer.equals(originalValue))
	                    {
	                        tcproperty.setIntValueData(integer.intValue());
	                        savable = true;
	                        if(Debug.isOn("stylesheet,form,property,properties"))
	                            Debug.println((new StringBuilder()).append("PropertyLOVCombobox:save propName=").append(property).append(",set int = ").append(integer).toString());
	                        break;
	                    }
	                    if(Debug.isOn("stylesheet,form,property,properties"))
	                        Debug.println((new StringBuilder()).append("PropertyLOVCombobox:save propName=").append(property).append(", value not changed, skip").toString());
	                    break;

	                case 6: // '\006'
	                    Boolean boolean1;
	                    if(obj instanceof Boolean)
	                        boolean1 = (Boolean)obj;
	                    else
	                        boolean1 = Boolean.valueOf(s);
	                    if(boolean1.booleanValue() != tcproperty.getLogicalValue())
	                    {
	                        tcproperty.setLogicalValue(boolean1.booleanValue());
	                        savable = true;
	                        if(Debug.isOn("stylesheet,form,property,properties"))
	                            Debug.println((new StringBuilder()).append("PropertyLOVCombobox:save propName=").append(property).append(",set logical = ").append(boolean1).toString());
	                        break;
	                    }
	                    if(Debug.isOn("stylesheet,form,property,properties"))
	                        Debug.println((new StringBuilder()).append("PropertyLOVCombobox:save propName=").append(property).append(", value not changed, skip").toString());
	                    break;

	                case 7: // '\007'
	                    Short short1 = null;
	                    if(obj instanceof Short)
	                        short1 = (Short)obj;
	                    else
	                    if(s != null && s.length() > 0)
	                        short1 = Short.valueOf(s);
	                    else
	                    if(!flag)
	                    {
	                        if(Debug.isOn("stylesheet,form,property,properties"))
	                            Debug.println((new StringBuilder()).append("PropertyLOVCombobox: save propName=").append(property).append(", set to null.").toString());
	                        tcproperty.setNullVerdict(true);
	                        savable = true;
	                    }
	                    if(short1 != null && !short1.equals(originalValue))
	                    {
	                        tcproperty.setShortValue(short1.shortValue());
	                        savable = true;
	                        if(Debug.isOn("stylesheet,form,property,properties"))
	                            Debug.println((new StringBuilder()).append("PropertyLOVCombobox:save propName=").append(property).append(",set short = ").append(short1).toString());
	                        break;
	                    }
	                    if(Debug.isOn("stylesheet,form,property,properties"))
	                        Debug.println((new StringBuilder()).append("PropertyLOVCombobox:save propName=").append(property).append(", value not changed, skip").toString());
	                    break;

	                case 8: // '\b'
	                    savable = false;
	                    if(originalValue == null && s.isEmpty())
	                    {
	                        if(Debug.isOn("stylesheet,form,property,properties"))
	                            Debug.println((new StringBuilder()).append("PropertyLOVCombobox:save propName=").append(property).append(", value not changed, skip").toString());
	                        break;
	                    }
	                    if(!s.isEmpty() && s1 != null && s1.equals(s))
	                    {
	                        if(Debug.isOn("stylesheet,form,property,properties"))
	                            Debug.println((new StringBuilder()).append("PropertyLOVCombobox:save propName=").append(property).append(", value not changed, skip").toString());
	                        break;
	                    }
	                    if(!s.equals(originalValue))
	                    {
	                        tcproperty.setStringValueData(s);
	                        savable = true;
	                        if(Debug.isOn("stylesheet,form,property,properties"))
	                            Debug.println((new StringBuilder()).append("PropertyLOVCombobox:save propName=").append(property).append(",set str = ").append(s).toString());
	                        break;
	                    }
	                    if(Debug.isOn("stylesheet,form,property,properties"))
	                        Debug.println((new StringBuilder()).append("PropertyLOVCombobox:save propName=").append(property).append(", value not changed, skip").toString());
	                    break;

	                case 12: // '\f'
	                    savable = false;
	                    if(originalValue == null && s.equals(""))
	                    {
	                        if(Debug.isOn("stylesheet,form,property,properties"))
	                            Debug.println((new StringBuilder()).append("PropertyLOVCombobox:save propName=").append(property).append(", value not changed, skip").toString());
	                        break;
	                    }
	                    if(!s.equals(originalValue))
	                    {
	                        tcproperty.setNoteValueData(s);
	                        savable = true;
	                        if(Debug.isOn("stylesheet,form,property,properties"))
	                            Debug.println((new StringBuilder()).append("PropertyLOVCombobox:save propName=").append(property).append(",set note = ").append(s).toString());
	                        break;
	                    }
	                    if(Debug.isOn("stylesheet,form,property,properties"))
	                        Debug.println((new StringBuilder()).append("PropertyLOVCombobox:save propName=").append(property).append(", value not changed, skip").toString());
	                    break;

	                case 0: // '\0'
	                case 9: // '\t'
	                case 10: // '\n'
	                case 11: // '\013'
	                case 13: // '\r'
	                    Object obj1 = originalValue;
	                    String s3 = obj1 != null ? obj1.toString() : "";
	                    if(obj != null && (obj instanceof TCComponent))
	                    {
	                        if(!obj.equals(obj1))
	                        {
	                            tcproperty.setReferenceValueData((TCComponent)obj);
	                            savable = true;
	                            if(Debug.isOn("stylesheet,form,property,properties"))
	                                Debug.println((new StringBuilder()).append("PropertyLOVCombobox:save propName=").append(property).append(",set ref = ").append(obj).toString());
	                            break;
	                        }
	                        if(Debug.isOn("stylesheet,form,property,properties"))
	                            Debug.println((new StringBuilder()).append("PropertyLOVCombobox:save propName=").append(property).append(", value not changed, skip").toString());
	                        break;
	                    }
	                    if(s.equals(s3))
	                    {
	                        if(Debug.isOn("stylesheet,form,property,properties"))
	                            Debug.println((new StringBuilder()).append("PropertyLOVCombobox:save propName=").append(property).append(", value not changed, skip").toString());
	                        break;
	                    }
	                    if(flag)
	                        break;
	                    if(Debug.isOn("stylesheet,form,property,properties"))
	                        Debug.println((new StringBuilder()).append("PropertyLOVCombobox: save propName=").append(property).append(", set to null.").toString());
	                    tcproperty.setNullVerdict(true);
	                    savable = true;
	                    break;
	                }
	            }
	            catch(NumberFormatException numberformatexception)
	            {
	                if(Debug.isOn("stylesheet,form,property,properties"))
	                    Debug.printStackTrace(numberformatexception);
	                throw tcexception;
	            }
	        }
	        return tcproperty;
	    }

	    @Override
		public boolean isPropertyModified(TCComponent tccomponent)
	        throws Exception
	    {
	        if(property != null)
	        {
	            TCProperty tcproperty = tccomponent.getTCProperty(property);
	            return isPropertyModified(tcproperty);
	        } else
	        {
	            return false;
	        }
	    }

	    @Override
		public boolean isPropertyModified(TCProperty tcproperty)
	        throws Exception
	    {
	        savable = false;
	        if(!tcproperty.isEnabled() || !modifiable)
	            return false;
	        Object obj = getSelectedItem();
	        String s = obj != null ? obj.toString() : "";
	        String s1 = tcproperty.getUIFValue();
	        boolean flag = tcproperty.getNullVerdict();
	        switch(tcproperty.getPropertyType())
	        {
	        default:
	            break;

	        case 1: // '\001'
	            if(s.length() > 0)
	            {
	                char c = s.charAt(0);
	                if(c != tcproperty.getCharValue())
	                    savable = true;
	                break;
	            }
	            if(!flag)
	                savable = true;
	            break;

	        case 2: // '\002'
	            Date date = null;
	            if(obj instanceof Date)
	                date = (Date)obj;
	            else
	            if(s != null && s.length() > 0)
	            {
	                TCDateFormat tcdateformat = new TCDateFormat(tcproperty.getTCComponent().getSession());
	                java.text.SimpleDateFormat simpledateformat = tcdateformat.askDefaultDateFormat();
	                date = simpledateformat.parse(s);
	            } else
	            if(!flag)
	                savable = true;
	            if(date != null && !date.equals(tcproperty.getDateValue()))
	                savable = true;
	            break;

	        case 3: // '\003'
	            Double double1 = null;
	            if(obj instanceof Double)
	                double1 = (Double)obj;
	            else
	            if(s != null && s.length() > 0)
	                double1 = Double.valueOf(s);
	            else
	            if(!flag)
	                savable = true;
	            if(double1 != null && double1.doubleValue() != tcproperty.getDoubleValue())
	                savable = true;
	            break;

	        case 4: // '\004'
	            Float float1 = null;
	            if(obj instanceof Float)
	                float1 = (Float)obj;
	            else
	            if(s != null && s.length() > 0)
	                float1 = Float.valueOf(s);
	            else
	            if(!flag)
	                savable = true;
	            if(float1 != null && float1.floatValue() != tcproperty.getFloatValue())
	                savable = true;
	            break;

	        case 5: // '\005'
	            Integer integer = null;
	            if(obj instanceof Integer)
	                integer = (Integer)obj;
	            else
	            if(s != null && s.length() > 0)
	                integer = Integer.valueOf(s);
	            else
	            if(!flag)
	                savable = true;
	            if(integer != null && integer.intValue() != tcproperty.getIntValue())
	                savable = true;
	            break;

	        case 6: // '\006'
	            Boolean boolean1;
	            if(obj instanceof Boolean)
	                boolean1 = (Boolean)obj;
	            else
	                boolean1 = Boolean.valueOf(s);
	            if(boolean1.booleanValue() != tcproperty.getLogicalValue())
	                savable = true;
	            break;

	        case 7: // '\007'
	            Short short1 = null;
	            if(obj instanceof Short)
	                short1 = (Short)obj;
	            else
	            if(s != null && s.length() > 0)
	                short1 = Short.valueOf(s);
	            else
	            if(!flag)
	                savable = true;
	            if(short1 != null && short1.shortValue() != tcproperty.getShortValue())
	                savable = true;
	            break;

	        case 8: // '\b'
	            if(originalValue == null && s.isEmpty())
	            {
	                savable = false;
	                break;
	            }
	            if(!s.isEmpty() && s1 != null && s1.equals(s))
	            {
	                savable = false;
	                break;
	            }
	            if(!s.equals(originalValue))
	                savable = true;
	            else
	                savable = false;
	            break;

	        case 12: // '\f'
	            if(!s.equals(tcproperty.getNoteValue()))
	                savable = true;
	            break;

	        case 0: // '\0'
	        case 9: // '\t'
	        case 10: // '\n'
	        case 11: // '\013'
	        case 13: // '\r'
	            TCComponent tccomponent = tcproperty.getReferenceValue();
	            String s2 = tccomponent != null ? tccomponent.toString() : "";
	            if(obj != null && (obj instanceof TCComponent))
	            {
	                if(!obj.equals(tccomponent))
	                    savable = true;
	                break;
	            }
	            if(!s.equals(s2) && !flag)
	                savable = true;
	            break;
	        }
	        return savable;
	    }

	    @Override
		public void paint(Graphics g)
	    {
//	        if(!isEnabled())
//	        {
//	            iTextField itextfield = getTextField();
//	            if(bkgrdColor != null)
//	            {
//	                setOpaque(true);
//	                itextfield.setBackground(bkgrdColor);
//	            }
//	            if(foregrdColor != null)
//	                itextfield.setDisabledTextColor(foregrdColor);
//	        }
	        super.paint(g);
	    }

	    protected String property;
	    protected boolean modifiable;
	    protected TCProperty tcProperty;
	    protected TCPropertyDescriptor descriptor;
	    private boolean savable;
	    private Object originalValue;
	    private static Color bkgrdColor;
	    private static Color foregrdColor;
		@Override
		public boolean isMandatory() {
			// TODO Auto-generated constructor stub
			return false;
		}

		@Override
		public void setMandatory(boolean flag) {
			// TODO Auto-generated constructor stub
			
		}
}
