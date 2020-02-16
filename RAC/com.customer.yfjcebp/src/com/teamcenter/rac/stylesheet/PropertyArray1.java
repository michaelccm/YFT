package com.teamcenter.rac.stylesheet;

import com.teamcenter.rac.aif.AIFClipboard;
import com.teamcenter.rac.aif.AIFDesktop;
import com.teamcenter.rac.aif.AIFPortal;
import com.teamcenter.rac.aif.AbstractAIFCommand;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.aif.kernel.AIFComponentEvent;
import com.teamcenter.rac.aif.kernel.AIFInputEvent;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponentEventListener;
import com.teamcenter.rac.common.TCTypeRenderer;
import com.teamcenter.rac.common.lov.LOVUIComponent;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentListOfValues;
import com.teamcenter.rac.kernel.TCComponentTaskInBox;
import com.teamcenter.rac.kernel.TCComponentTcFile;
import com.teamcenter.rac.kernel.TCComponentType;
import com.teamcenter.rac.kernel.TCDateFormat;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCPropertyDescriptor;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.popupmenu.AbstractTCComponentPopupMenu;
import com.teamcenter.rac.stylesheet.InterfaceBufferedPropertyComponent;
import com.teamcenter.rac.stylesheet.InterfacePropertyComponent;
import com.teamcenter.rac.util.ArraySorter;
import com.teamcenter.rac.util.ConfirmationDialog;
import com.teamcenter.rac.util.DateButton;
import com.teamcenter.rac.util.FilterDocument;
import com.teamcenter.rac.util.HorizontalLayout;
import com.teamcenter.rac.util.HyperLink;
import com.teamcenter.rac.util.InterfaceSignalOnClose;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.Painter;
import com.teamcenter.rac.util.Registry;
import com.teamcenter.rac.util.UIUtilities;
import com.teamcenter.rac.util.Utilities;
import com.teamcenter.rac.util.VerticalLayout;
import com.teamcenter.rac.util.iTextField;
import com.teamcenter.rac.util.log.Debug;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Rectangle2D;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.eclipse.swt.widgets.Shell;

// Referenced classes of package com.teamcenter.rac.stylesheet:
//            InterfacePropertyComponent, InterfaceBufferedPropertyComponent

public class PropertyArray1 extends JPanel
    implements InterfacePropertyComponent, InterfaceBufferedPropertyComponent, InterfaceAIFComponentEventListener, InterfaceSignalOnClose
{
    private class RelationLink extends HyperLink
    {

        @Override
		public boolean equals(Object obj)
        {
            if((obj instanceof String) && propValue != null)
                return propValue.toString().equals(obj);
            else
                return super.equals(obj);
        }

        @Override
		public String toString()
        {
            return propValue != null ? propValue.toString() : "";
        }

        public Object getPropValue()
        {
            return propValue;
        }

        private Object propValue;
      

        public RelationLink(Object obj)
        {
           
            propValue = obj;
            if((obj instanceof TCComponent) && obj != null)
            {
                TCComponent tccomponent = (TCComponent)obj;
                setText(obj.toString());
                setIcon(TCTypeRenderer.getIcon(tccomponent, false));
            }
        }
    }

    private class FormattedDate
    {

        @Override
		public String toString()
        {
            if(dateButton != null)
            {
                if(date != null)
                {
                    String s = dateButton.getDateFormat().format(date);
                    if(s != null && !dateButton.getSecondEnabled())
                        s = s.substring(0, s.lastIndexOf(':'));
                    return s;
                } else
                {
                    return "";
                }
            } else
            {
                return date == null ? "" : date.toString();
            }
        }

        public Date getDate()
        {
            return date;
        }

        private Date date;
       

        public FormattedDate(Date date1)
        {
           
           
            date = date1;
        }
    }

    private class RefPopupMenu extends AbstractTCComponentPopupMenu
    {

       
    	
        public RefPopupMenu()
        {

            addPopupHeader(Registry.getRegistry(PropertyArray1.this).getString("popupmenu.TITLE"));
            final Registry r = Registry.getRegistry("com.teamcenter.rac.common.actions.actions");
            JMenuItem jmenuitem = addMenuItem(this, "copyAction");
            jmenuitem.addActionListener(new ActionListener() {

                @Override
				public void actionPerformed(ActionEvent actionevent)
                {
                    AIFComponentContext aaifcomponentcontext[] = getSelection();
                    if(aaifcomponentcontext != null)
                    {
                        AbstractAIFCommand abstractaifcommand = (AbstractAIFCommand)r.newInstanceFor("copyCommand", new Object[] {
                            aaifcomponentcontext
                        });
                        abstractaifcommand.executeModeless();
                    }
                }

                
// JavaClassFileOutputException: Invalid index accessing method local variables table of <init>
            });
            JMenuItem jmenuitem1 = addMenuItem(this, "openAction");
            jmenuitem1.addActionListener(new ActionListener() {

                @Override
				public void actionPerformed(ActionEvent actionevent)
                {
                    AIFComponentContext aaifcomponentcontext[] = getSelection();
                    if(aaifcomponentcontext != null)
                    {
                        InterfaceAIFComponent ainterfaceaifcomponent[] = new InterfaceAIFComponent[aaifcomponentcontext.length];
                        for(int i = 0; i < aaifcomponentcontext.length; i++)
                            ainterfaceaifcomponent[i] = aaifcomponentcontext[i].getComponent();

                        AbstractAIFCommand abstractaifcommand = (AbstractAIFCommand)r.newInstanceFor("openCommand", new Object[] {
                            AIFDesktop.getActiveDesktop(), ainterfaceaifcomponent
                        });
                        abstractaifcommand.executeModeless();
                    }
                }

       
// JavaClassFileOutputException: Invalid index accessing method local variables table of <init>
            });
            JMenuItem jmenuitem2 = addMenuItem(this, "propertiesAction");
            jmenuitem2.addActionListener(new ActionListener() {

                @Override
				public void actionPerformed(ActionEvent actionevent)
                {
                    AIFComponentContext aaifcomponentcontext[] = getSelection();
                    if(aaifcomponentcontext != null)
                    {
                        TCComponent atccomponent[] = new TCComponent[aaifcomponentcontext.length];
                        for(int i = 0; i < aaifcomponentcontext.length; i++)
                            atccomponent[i] = (TCComponent)aaifcomponentcontext[i].getComponent();

                        AbstractAIFCommand abstractaifcommand = (AbstractAIFCommand)r.newInstanceFor("propertiesCommand", new Object[] {
                            atccomponent, AIFDesktop.getActiveDesktop()
                        });
                        abstractaifcommand.executeModeless();
                    }
                }


// JavaClassFileOutputException: Invalid index accessing method local variables table of <init>
            });
           
        }
    }

    public class PropArrayListModel extends DefaultListModel
    {
    	
        public Object getDisplayObject(Object obj)
        {

            if(obj instanceof String)
            {
                String s = (String)obj;
                for(Iterator iterator = modelDisplayObjects.iterator(); iterator.hasNext();)
                {
                    L10NModel l10nmodel = (L10NModel)iterator.next();
                    if(l10nmodel.getModelValue() == s)
                        return l10nmodel.getDisplayValue();
                }

                return null;
            } else
            {
                return displayObjects.get(obj);
            }
            
        }

        public Object getModelObject(Object obj)
        {
        	
            if(obj instanceof L10NModel)
                return ((L10NModel)obj).getModelValue();
            if(obj instanceof String)
            {
                String s = (String)obj;
                for(Iterator iterator = modelDisplayObjects.iterator(); iterator.hasNext();)
                {
                    L10NModel l10nmodel = (L10NModel)iterator.next();
                    if(l10nmodel.getDisplayValue() == s)
                        return l10nmodel.getModelValue();
                }

                return null;
            } else
            {
                return modelObjects.get(obj);
            }
        }

        public void addElement(Object obj, Object obj1)
        {
        	
            if((obj instanceof String) && (obj1 instanceof String))
            {
                L10NModel l10nmodel = new L10NModel((String)obj1, (String)obj);
                super.addElement(l10nmodel);
                modelDisplayObjects.add(l10nmodel);
            } else
            {
                super.addElement(obj1);
                displayObjects.put(obj1, obj);
                modelObjects.put(obj, obj1);
            }

        }

        @Override
		public void addElement(Object obj)
        {

            if(obj instanceof String)
            {
                L10NModel l10nmodel = new L10NModel((String)obj, (String)obj);
                super.addElement(l10nmodel);
                modelDisplayObjects.add(l10nmodel);
            } else
            {
                super.addElement(obj);
                displayObjects.put(obj, obj);
                modelObjects.put(obj, obj);
            }

        }

        private ArrayList modelDisplayObjects;
        private HashMap modelObjects;
        private HashMap displayObjects;
       

        public PropArrayListModel()
        {

            modelDisplayObjects = new ArrayList(0);
            modelObjects = new HashMap(0);
            displayObjects = new HashMap(0);

        }
    }

    private class ListRenderer extends DefaultListCellRenderer
    {
    	
        @Override
		public Component getListCellRendererComponent(JList jlist, Object obj, int i, boolean flag, boolean flag1)
        {
            Component component1 = super.getListCellRendererComponent(jlist, obj, i, flag, flag1);
            theValue = obj;
            theIndex = i;
            if(obj instanceof TCComponent)
                setIcon(TCTypeRenderer.getIcon(obj, false));
            else
            if(obj instanceof RelationLink)
            {
                RelationLink relationlink = (RelationLink)obj;
                setIcon(relationlink.getIcon());
                Object obj1 = null;
                if(i == mouseOver)
                {
                    java.awt.Color color = HyperLink.LINKING_FOREGROUND;
                    component1.setForeground(color);
                }
            }
            return component1;
        }

        @Override
		protected void paintComponent(Graphics g)
        {
            super.paintComponent(g);
            if((theValue instanceof RelationLink) && theIndex == mouseOver)
            {
                String s = ((RelationLink)theValue).getText();
                Icon icon = ((RelationLink)theValue).getIcon();
                FontMetrics fontmetrics = getFontMetrics(getFont());
                Rectangle2D rectangle2d = fontmetrics.getStringBounds(s, g);
                int i = icon == null ? 0 : icon.getIconWidth();
                int j = icon == null ? 0 : icon.getIconHeight();
                int k = ((RelationLink)theValue).getIconTextGap();
                int l = fontmetrics.getHeight() <= j ? j : fontmetrics.getHeight();
                int i1 = (int)rectangle2d.getX();
                g.drawLine(i1, l, (int)(i1 + rectangle2d.getWidth() + i + k), l);
            }
        }

        private Object theValue;
        private int theIndex;
       

    }

    public class L10NModel
    {

        public void setModelValue(String s)
        {
            m_modelValue = s;
        }

        public String getModelValue()
        {
            return m_modelValue;
        }

        public void setDisplayValue(String s)
        {
            m_displayValue = s;
        }

        public String getDisplayValue()
        {
            return m_displayValue;
        }

        @Override
		public String toString()
        {
            return m_displayValue;
        }

        private String m_modelValue;
        private String m_displayValue;
        

        public L10NModel(String s, String s1)
        {
           
            
            m_modelValue = s;
            m_displayValue = s1;
        }
    }


    public PropertyArray1()
    {
        modifiable = true;
        isExhaustive = false;
        originalDispValues = null;
        allowModify = true;
        mouseOver = -1;
        setOpaque(false);
        initUI();
    }

    protected void initUI()
    {
    	
        setLayout(new VerticalLayout(0, 0, 0, 0, 0));
        final Registry r = Registry.getRegistry(this);
        numOfObjsDisplayedInList = r.getInt("ArrayListDisplayNumber", 10);
        listModel = new PropArrayListModel();
        list = new JList(listModel) {

            @Override
			public String getToolTipText(MouseEvent mouseevent)
            {
                return getListToolTip(mouseevent);
            }

           
        };
        ToolTipManager.sharedInstance().registerComponent(list);
        listScrollPane = new JScrollPane(list);
        listScrollPane.setPreferredSize(new Dimension(250, 120));
        list.setVisibleRowCount(numOfObjsDisplayedInList);
        list.setCellRenderer(new ListRenderer());
        expandButton = new JToggleButton(r.getImageIcon("edit.ICON"), false);
        expandButton.setFocusPainted(false);
        expandButton.setMargin(new Insets(0, 0, 0, 0));
        expandButton.setToolTipText(r.getString("expandButton.TIP"));
        listScrollPane.setVerticalScrollBarPolicy(22);
        listScrollPane.setHorizontalScrollBarPolicy(32);
        listScrollPane.setCorner("LOWER_RIGHT_CORNER", expandButton);
        addButton = new JButton(r.getImageIcon("addButton.ICON"));
        addButton.setEnabled(false);
        addButton.setFocusPainted(false);
        addButton.setMargin(new Insets(0, 0, 0, 0));
        addButton.setToolTipText(r.getString("addButton.TIP"));
        modifyButton = new JButton(r.getImageIcon("modifyButton.ICON"));
        modifyButton.setFocusPainted(false);
        modifyButton.setMargin(new Insets(0, 0, 0, 0));
        modifyButton.setEnabled(false);
        modifyButton.setToolTipText(r.getString("modifyButton.TIP"));
        removeButton = new JButton(r.getImageIcon("removeButton.ICON"));
        removeButton.setFocusPainted(false);
        removeButton.setMargin(new Insets(0, 0, 0, 0));
        removeButton.setEnabled(false);
        removeButton.setToolTipText(r.getString("removeButton.TIP"));
        upButton = new JButton(r.getImageIcon("upButton.ICON"));
        upButton.setFocusPainted(false);
        upButton.setMargin(new Insets(0, 0, 0, 0));
        upButton.setEnabled(false);
        upButton.setToolTipText(r.getString("upButton.TIP"));
        downButton = new JButton(r.getImageIcon("downButton.ICON"));
        downButton.setFocusPainted(false);
        downButton.setMargin(new Insets(0, 0, 0, 0));
        downButton.setEnabled(false);
        downButton.setToolTipText(r.getString("downButton.TIP"));
        ascSortButton = new JButton(r.getImageIcon("sort.ICON"));
        ascSortButton.setFocusPainted(false);
        ascSortButton.setMargin(new Insets(0, 0, 0, 0));
        ascSortButton.setEnabled(false);
        ascSortButton.setToolTipText(r.getString("ascSortButton.TIP"));
        listButtonPanel = new JPanel(new VerticalLayout(0, 0, 0, 0, 0));
        listButtonPanel.add("top.nobind.left.top", removeButton);
        listButtonPanel.add("top.nobind.left.top", upButton);
        listButtonPanel.add("top.nobind.left.top", downButton);
        listButtonPanel.add("bottom.nobind.left.top", ascSortButton);
        listButtonPanel.setVisible(false);
        
       // JPanelcom.teamcenter.rac.stylesheet.PropertyArray.listButtonPanel
        
        ((JPanel)popupMenu).add("unbound.bind.left.top", listScrollPane);
        ((JPanel)popupMenu).add("right.bind.left.top", listButtonPanel);
        addValuePanel = new JPanel(new HorizontalLayout(0, 0, 0, 0, 0));
        addValuePanel.add("right.nobind.left.top", modifyButton);
        addValuePanel.add("right.nobind.left.top", addButton);
        addValuePanel.setVisible(false);
        add("unbound.bind.left.top",(Component)popupMenu);
        add("bottom.bind.left.top", addValuePanel);
        popupMenu = new RefPopupMenu();
        list.addListSelectionListener(new ListSelectionListener() {

            @Override
			public void valueChanged(ListSelectionEvent listselectionevent)
            {
                int ai[] = list.getSelectedIndices();
                boolean flag = ai != null && ai.length > 0;
                removeButton.setEnabled(allowModify && flag);
                upButton.setEnabled(allowModify && flag);
                downButton.setEnabled(allowModify && flag);
                AIFComponentContext aaifcomponentcontext[] = getSelection();
                if(aaifcomponentcontext == null)
                    modifyButton.setEnabled(flag);
                updateEditField();
            }
           
        });
        list.addMouseMotionListener(new MouseMotionAdapter() {

            @Override
			public void mouseMoved(MouseEvent mouseevent)
            {
                mouseOver = list.locationToIndex(new Point(mouseevent.getX(), mouseevent.getY()));
                repaint();
            }

           
        });
        list.addMouseListener(new MouseAdapter() {

            @Override
			public void mousePressed(MouseEvent mouseevent)
            {
                if(mouseevent.getModifiers() == 4 && getSelection() != null)
                {
                    ((AbstractTCComponentPopupMenu) popupMenu).show((Component)mouseevent.getSource(), mouseevent.getX(), mouseevent.getY());
                    return;
                } else
                {
                    return;
                }
            }

            @Override
			public void mouseClicked(MouseEvent mouseevent)
            {
                if(mouseevent.getClickCount() == 2)
                {
                    int i = list.locationToIndex(mouseevent.getPoint());
                    if(i >= 0)
                    {
                        AIFComponentContext aaifcomponentcontext[] = getSelection();
                        if(aaifcomponentcontext != null)
                        {
                            InterfaceAIFComponent ainterfaceaifcomponent[] = new InterfaceAIFComponent[aaifcomponentcontext.length];
                            for(int j = 0; j < aaifcomponentcontext.length; j++)
                                ainterfaceaifcomponent[j] = aaifcomponentcontext[j].getComponent();

                            Registry registry = Registry.getRegistry("com.teamcenter.rac.common.actions.actions");
                            AbstractAIFCommand abstractaifcommand = (AbstractAIFCommand)registry.newInstanceFor("openCommand", new Object[] {
                                AIFDesktop.getActiveDesktop(), ainterfaceaifcomponent
                            });
                            abstractaifcommand.executeModeless();
                        }
                    }
                }
            }

            @Override
			public void mouseExited(MouseEvent mouseevent)
            {
                mouseOver = -1;
                repaint();
            }

            
        });
        expandButton.addActionListener(new ActionListener() {

            @Override
			public void actionPerformed(ActionEvent actionevent)
            {
                boolean flag = expandButton.isSelected();
                listButtonPanel.setVisible(flag);
                addValuePanel.setVisible(flag);
                if(flag)
                {
                    setBorder(new EtchedBorder(1));
                    if(addValueTextField != null)
                        addValueTextField.requestFocus();
                } else
                {
                    setBorder(null);
                }
            }

           
        });
        addButton.addActionListener(new ActionListener() {

            @Override
			public void actionPerformed(ActionEvent actionevent)
            {
                String as[] = getValues();
                addValue();
                String as1[] = getValues();
                firePropertyChange(getProperty(), as, as1);
                if(addValueLovUIComponent != null)
                    addValueLovUIComponent.requestInputFocus();
            }

           
        });
        modifyButton.addActionListener(new ActionListener() {

            @Override
			public void actionPerformed(ActionEvent actionevent)
            {
                String as[] = getValues();
                modifyValue();
                String as1[] = getValues();
                firePropertyChange(getProperty(), as, as1);
            }

           
        });
        removeButton.addActionListener(new ActionListener() {

            @Override
			public void actionPerformed(ActionEvent actionevent)
            {
                int i = descriptor.getMaxArraySize();
                int j = listModel.getSize();
                if(i == j)
                {
                    MessageBox.post(r.getString("notEqualToFixedSize"), r.getString("warning"), 4);
                    return;
                }
                String as[] = getValues();
                removeValue();
                String as1[] = getValues();
                firePropertyChange(getProperty(), as, as1);
                int ai[] = list.getSelectedIndices();
                if(ai != null && ai.length > 0)
                    removeButton.setEnabled(allowModify);
                else
                    removeButton.setEnabled(false);
            }

          
        });
        ascSortButton.addActionListener(new ActionListener() {

            @Override
			public void actionPerformed(ActionEvent actionevent)
            {
                String as[] = getValues();
                sortValue();
                String as1[] = getValues();
                firePropertyChange(getProperty(), as, as1);
            }

          
        });
        upButton.addActionListener(new ActionListener() {

            @Override
			public void actionPerformed(ActionEvent actionevent)
            {
                String as[] = getValues();
                moveValue(-2);
                String as1[] = getValues();
                firePropertyChange(getProperty(), as, as1);
            }

           
        });
        downButton.addActionListener(new ActionListener() {

            @Override
			public void actionPerformed(ActionEvent actionevent)
            {
                String as[] = getValues();
                moveValue(-1);
                String as1[] = getValues();
                firePropertyChange(getProperty(), as, as1);
            }

           
        });
   
    }

    protected void buildAddValuePanel()
    {
    	
    
        if(descriptor == null)
            return;
        addButton.setEnabled(allowModify);
        TCComponentListOfValues tccomponentlistofvalues = null;
        try
        {
            if(tcProperty != null)
                tccomponentlistofvalues = tcProperty.getLOV();
            else
                tccomponentlistofvalues = descriptor.getLOV();
        }
        catch(Exception exception) { }
        if(tccomponentlistofvalues != null)
        {
            Object aobj[] = null;
            try
            {
                short word0 = tccomponentlistofvalues.getListOfValuesUsage();
                if(word0 == 1)
                    isExhaustive = true;
                else
                    isExhaustive = false;
                if(tcProperty != null)
                    aobj = tccomponentlistofvalues.getInterdependentProperties(tcProperty);
                else
                    aobj = tccomponentlistofvalues.getInterdependentProperties(descriptor);
            }
            catch(Exception exception1) { }
            addValueLovUIComponent = new LOVUIComponent(tccomponentlistofvalues, "", -1, mandatory, property, aobj);
            if(isExhaustive)
                addValueLovUIComponent.setEditable(false);
            addValuePanel.add("unbound.bind.left.top", addValueLovUIComponent);
            addButton.setToolTipText(Registry.getRegistry(this).getString("addButtonLOV.TIP"));
        } else
        {
            int i = descriptor.getType();
            switch(i)
            {
            case 2: // '\002'
                dateButton = new DateButton();
                TCDateFormat tcdateformat = descriptor.getTypeComponent().getSession().askTCDateFormat();
                SimpleDateFormat simpledateformat = tcdateformat.askDefaultDateFormat();
                dateButton.setDisplayFormatter(simpledateformat);
                dateButton.setDate(dateButton.getDate());
                addValuePanel.add("unbound.bind.left.top", dateButton);
                break;

            case 6: // '\006'
                JPanel jpanel = new JPanel(new HorizontalLayout());
                trueButton = new JRadioButton(Boolean.TRUE.toString(), true);
                falseButton = new JRadioButton(Boolean.FALSE.toString(), false);
                ButtonGroup buttongroup = new ButtonGroup();
                buttongroup.add(trueButton);
                buttongroup.add(falseButton);
                jpanel.add("left.nobind.left.top", trueButton);
                jpanel.add("left.nobind.left.top", falseButton);
                addValuePanel.add("unbound.bind.left.top", jpanel);
                break;

            case 9: // '\t'
            case 10: // '\n'
            case 11: // '\013'
            case 13: // '\r'
            case 14: // '\016'
                Object obj;
                try
                {
                    obj = getValidClipboardObjects();
                }
                catch(TCException tcexception)
                {
                    obj = getClipboardObjects();
                }
                int j = 0;
                if(obj != null)
                {
                    j = ((java.util.List) (obj)).size();
                    if(j > 0)
                        addButton.setEnabled(allowModify);
                } else
                {
                    addButton.setEnabled(false);
                }
                Registry registry = Registry.getRegistry(this);
                JLabel jlabel = new JLabel((new StringBuilder()).append(j).append(" ").append(registry.getString("objectsOnClipboard")).toString());
                addValuePanel.add("unbound.bind.left.top", jlabel);
                break;

            case 3: // '\003'
            case 4: // '\004'
            case 5: // '\005'
            case 7: // '\007'
            case 8: // '\b'
            case 12: // '\f'
            default:
                TCSession tcsession = descriptor.getTypeComponent().getSession();
                String s = TCSession.getServerEncodingName(tcsession);
                document = new FilterDocument(32, s);
                setConstraintsOnFilterDocument();
                addValueTextField = new iTextField(document, "", 15);
                addButton.setEnabled(false);
                addValueTextField.addActionListener(new ActionListener() {

                    @Override
					public void actionPerformed(ActionEvent actionevent)
                    {
                        boolean flag = list.isSelectionEmpty();
                        if(flag)
                        {
                            addButton.doClick();
                        } else
                        {
                            modifyButton.doClick();
                            list.requestFocus();
                        }
                    }

                  
                });
                addValueTextField.addKeyListener(new KeyAdapter() {

                    @Override
					public void keyReleased(KeyEvent keyevent)
                    {
                        int ai[] = list.getSelectedIndices();
                        boolean flag = ai != null && ai.length > 0;
                        if(addValueTextField.getText().length() > 0)
                        {
                            addButton.setEnabled(allowModify);
                            modifyButton.setEnabled(allowModify && flag);
                        } else
                        {
                            addButton.setEnabled(false);
                            modifyButton.setEnabled(false);
                        }
                    }

                   
                });
                addValuePanel.add("unbound.bind.left.top", addValueTextField);
                break;
            }
        }
    
    }

    public String[] getValues()
    {
        Object aobj[] = listModel.toArray();
        String as[] = new String[aobj.length];
        for(int i = 0; i < as.length; i++)
            if(aobj[i] == null)
                as[i] = "";
            else
                as[i] = aobj[i].toString();

        return as;
    }

    public String[] getOrigValues()
    {
        if(originalDispValues != null && originalDispValues.length > 0)
        {
            String as[] = new String[originalDispValues.length];
            for(int i = 0; i < as.length; i++)
                if(originalDispValues[i] == null)
                    as[i] = "";
                else
                    as[i] = originalDispValues[i].toString().trim();

            return as;
        } else
        {
            return new String[0];
        }
    }

	public void addaction(TCComponent[] comp)
    {
		String as[] = getValues();
		listModel.removeAllElements();
	    if ((comp != null) && (comp.length > 0))
	    {
	    	for (int n = 0; n < comp.length; n++)
	    	listModel.addElement(new RelationLink(comp[n]));
	    	
	    }
        String as1[] = getValues();
        firePropertyChange(getProperty(), as, as1);
        if(addValueLovUIComponent != null)
        	addValueLovUIComponent.requestInputFocus();
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

    public boolean getMandatory()
    {
        return mandatory;
    }

    @Override
	public void setMandatory(boolean flag)
    {
        mandatory = flag;
    }

    @Override
	public boolean isMandatory()
    {
        return mandatory;
    }

    @Override
	public void setModifiable(boolean flag)
    {
        modifiable = flag;
        if(modifiable)
        {
            listScrollPane.setVerticalScrollBarPolicy(22);
            listScrollPane.setHorizontalScrollBarPolicy(32);
            listScrollPane.setCorner("LOWER_RIGHT_CORNER", expandButton);
        } else
        {
            listScrollPane.setVerticalScrollBarPolicy(20);
            listScrollPane.setHorizontalScrollBarPolicy(30);
            if(expandButton.isSelected())
                expandButton.doClick();
            listScrollPane.setCorner("LOWER_RIGHT_CORNER", null);
        }
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
    	
    	
        Object aobj[] = listModel.toArray();
        if(descriptor != null && aobj != null)
        {
            int i = descriptor.getType();
            if(i == 2)
            {
                int j = aobj.length;
                Date adate[] = new Date[j];
                for(int i1 = 0; i1 < j; i1++)
                    if(aobj[i1] instanceof FormattedDate)
                        adate[i1] = ((FormattedDate)aobj[i1]).getDate();
                    else
                        adate[i1] = (Date)aobj[i1];

                return adate;
            }
            if(i == 9 || i == 10 || i == 11 || i == 13 || i == 14)
            {
                int k = aobj.length;
                Object aobj1[] = new Object[k];
                for(int j1 = 0; j1 < k; j1++)
                    if(aobj[j1] instanceof RelationLink)
                        aobj1[j1] = ((RelationLink)aobj[j1]).getPropValue();
                    else
                        aobj1[j1] = aobj[j1];

                return ((aobj1));
            }
            if(i == 8)
            {
                int l = aobj.length;
                String as[] = new String[l];
                for(int k1 = 0; k1 < l; k1++)
                {
                    if(aobj[k1] == null)
                    {
                        as[k1] = "";
                        continue;
                    }
                    if(addValueLovUIComponent != null)
                    {
                        Object obj = listModel.getModelObject(aobj[k1]);
                        if(obj != null)
                            as[k1] = obj.toString();
                        else
                            as[k1] = aobj[k1].toString();
                    } else
                    {
                        as[k1] = aobj[k1].toString();
                    }
                }

                return as;
            }
        }
        return ((aobj));
    }

    @Override
	public void setUIFValue(final Object valueToBeSet)
    {
    	
        int i = 0;
        try
        {
            i = ((Object[])valueToBeSet).length;
        }
        catch(Exception exception)
        {
            return;
        }
        if(i == 0)
        {
            return;
        } else
        {
            final int ss = i;
            SwingUtilities.invokeLater(new Runnable() {

                @Override
				public void run()
                {
                    listModel.removeAllElements();
                    for(int j = 0; j < ss; j++)
                    {
                        Object obj = ((Object[])valueToBeSet)[j];
                        listModel.addElement(obj);
                    }

                    list.revalidate();
                }

               
            });
            return;
        }
        
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
        if(tcpropertydescriptor.isEnabled() && modifiable)
        {
            listScrollPane.setVerticalScrollBarPolicy(22);
            listScrollPane.setHorizontalScrollBarPolicy(32);
            listScrollPane.setCorner("LOWER_RIGHT_CORNER", expandButton);
            buildAddValuePanel();
        } else
        {
            listScrollPane.setVerticalScrollBarPolicy(20);
            listScrollPane.setHorizontalScrollBarPolicy(30);
        }
        if(tcpropertydescriptor.isRequired())
            setMandatory(true);
        if(!tcpropertydescriptor.isDisplayable())
            setVisible(false);
        tcpropertydescriptor.getTypeComponent().getSession().addAIFComponentEventListener(this);
        TCComponentListOfValues tccomponentlistofvalues = tcpropertydescriptor.getLOV();
        if(tccomponentlistofvalues != null)
            component = tccomponentlistofvalues;
     
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
        if(property == null)
            return;
        if(descriptor == null)
        {
            TCPropertyDescriptor tcpropertydescriptor = tcproperty.getPropertyDescriptor();
            load(tcpropertydescriptor);
        }
        component = tccomponent;
        TCSession tcsession = tccomponent.getSession();
        tcsession.addAIFComponentEventListener(this);
        if((component instanceof TCComponentTaskInBox) || component.getType().equals("User_Inbox"))
            allowModify = false;
        int i = descriptor.getType();
        boolean flag = descriptor.isEnabled() && modifiable;
        if(Debug.isOn("stylesheet,form,property,properties"))
            Debug.println((new StringBuilder()).append("PropertyArray:load propName=").append(property).append(" propValue=").append(tcproperty.toString()).append(" type=").append(i).toString());
        Object aobj[] = null;
        java.util.List list1 = tcproperty.getDisplayableValues();
        String as[] = (String[])list1.toArray(new String[list1.size()]);
        switch(i)
        {
        default:
            break;

        case 1: // '\001'
            if(flag && document != null)
                document.setLength(1);
            char ac[] = tcproperty.getCharValueArray();
            if(ac != null && ac.length > 0)
            {
                aobj = new Object[ac.length];
                for(int l = 0; l < ac.length; l++)
                    aobj[l] = Character.valueOf(ac[l]);

            }
            break;

        case 2: // '\002'
            Date adate[] = tcproperty.getDateValueArray();
            if(adate == null || adate.length <= 0)
                break;
            aobj = new Object[adate.length];
            for(int i1 = 0; i1 < adate.length; i1++)
                aobj[i1] = new FormattedDate(adate[i1]);

            break;

        case 3: // '\003'
            if(flag && document != null)
                document.setAcceptedChars("0123456789.eE");
            double ad[] = tcproperty.getDoubleValueArray();
            if(ad == null || ad.length <= 0)
                break;
            aobj = new Object[ad.length];
            for(int j1 = 0; j1 < ad.length; j1++)
                aobj[j1] = Double.valueOf(ad[j1]);

            break;

        case 4: // '\004'
            if(flag && document != null)
                document.setAcceptedChars("0123456789.eE");
            float af[] = tcproperty.getFloatValueArray();
            if(af == null || af.length <= 0)
                break;
            aobj = new Object[af.length];
            for(int k1 = 0; k1 < af.length; k1++)
                aobj[k1] = Float.valueOf(af[k1]);

            break;

        case 5: // '\005'
            if(flag && document != null)
                document.setAcceptedChars("9876543210");
            int ai[] = tcproperty.getIntValueArray();
            if(ai == null || ai.length <= 0)
                break;
            aobj = new Object[ai.length];
            for(int l1 = 0; l1 < ai.length; l1++)
                aobj[l1] = Integer.valueOf(ai[l1]);

            break;

        case 6: // '\006'
            boolean aflag[] = tcproperty.getLogicalValueArray();
            if(aflag == null || aflag.length <= 0)
                break;
            aobj = new Object[aflag.length];
            for(int i2 = 0; i2 < aflag.length; i2++)
                aobj[i2] = Boolean.valueOf(aflag[i2]);

            break;

        case 12: // '\f'
            int j = descriptor.getMaxStringLength();
            if(flag && document != null)
                document.setLength(j);
            aobj = tcproperty.getNoteValueArray();
            break;

        case 7: // '\007'
            if(flag && document != null)
                document.setAcceptedChars("1023456789");
            short aword0[] = tcproperty.getShortValueArray();
            if(aword0 == null || aword0.length <= 0)
                break;
            aobj = new Object[aword0.length];
            for(int j2 = 0; j2 < aword0.length; j2++)
                aobj[j2] = Short.valueOf(aword0[j2]);

            break;

        case 8: // '\b'
            int k = descriptor.getMaxStringLength();
            if(k <= 0)
                k = -1;
            if(flag && document != null)
                document.setLength(k);
            else
            if(flag && addValueLovUIComponent != null)
                addValueLovUIComponent.setLengthLimit(k);
            aobj = tcproperty.getStringValueArray();
            break;

        case 9: // '\t'
        case 10: // '\n'
        case 11: // '\013'
        case 13: // '\r'
        case 14: // '\016'
            TCComponent atccomponent[] = tcproperty.getReferenceValueArray();
            if(atccomponent == null || atccomponent.length <= 0)
                break;
            TCComponentType.cachePropertiesSet(atccomponent);
            aobj = new RelationLink[atccomponent.length];
            for(int k2 = 0; k2 < atccomponent.length; k2++)
                aobj[k2] = new RelationLink(atccomponent[k2]);

            break;
        }
        final Object listValues[] = aobj;
        final String displayValues[];
        if(as != null && listValues != null && as.length == listValues.length)
            displayValues = as;
        else{
        	 //System.out.println("displayValues11111111111111111111");
            displayValues = ((String []) (listValues));
       
        }
        SwingUtilities.invokeLater(new Runnable() {

            @Override
			public void run()
            {
                listModel.removeAllElements();
                for(int l2 = 0; listValues != null && l2 < listValues.length; l2++)
                    if(listValues[l2] != null)
                    {
                        Object obj = displayValues[l2] == null ? listValues[l2] : ((Object) (displayValues[l2].toString().trim()));
                        listModel.addElement(obj, listValues[l2]);
                    }

                if(Debug.isOn("stylesheet,form,property,properties"))
                {
                    for(int i3 = 0; listValues != null && i3 < listValues.length; i3++)
                        Debug.println((new StringBuilder()).append("       value[").append(i3).append("]=").append(listValues[i3]).toString());

                }
                setAscSortButton();
            }

           
        });
        originalValues = aobj;
        originalDispValues = displayValues;
        if(aobj == null || aobj.length < 5)
        {
            listScrollPane.setPreferredSize(new Dimension(250, 100));
            list.setVisibleRowCount(5);
        } else
        if(aobj.length < numOfObjsDisplayedInList)
        {
            listScrollPane.setPreferredSize(new Dimension(250, 20 * aobj.length));
            list.setVisibleRowCount(aobj.length);
        } else
        {
            listScrollPane.setPreferredSize(new Dimension(250, 20 * numOfObjsDisplayedInList));
            list.setVisibleRowCount(numOfObjsDisplayedInList);
        }
        addButton.setEnabled(allowModify);
       
    }

    protected String getListToolTip(MouseEvent mouseevent)
    {
    	//System.out.println("String getListToolTip(MouseEvent mouseevent)");
        String s = null;
        int i = list.locationToIndex(mouseevent.getPoint());
        if(i == -1)
            return null;
        String s1 = list.getModel().getElementAt(i).toString();
        if(list.getModel().getElementAt(i) instanceof RelationLink)
        {
            RelationLink relationlink = (RelationLink)list.getModel().getElementAt(i);
            if(relationlink.getPropValue() instanceof TCComponentTcFile)
            {
                TCComponentTcFile tccomponenttcfile = (TCComponentTcFile)relationlink.getPropValue();
                TCSession tcsession = descriptor.getTypeComponent().getSession();
                TCPreferenceService tcpreferenceservice = tcsession.getPreferenceService();
                if(tcpreferenceservice.isTrue(0, "IMF_display_full_path"))
                    try
                    {
                        s = tccomponenttcfile.getProperty("file_name");
                    }
                    catch(TCException tcexception)
                    {
                        if(Debug.isOn("stylesheet,form,property,properties"))
                            tcexception.printStackTrace();
                    }
                if(s == null || s.length() == 0)
                    try
                    {
                        s = tccomponenttcfile.getPath();
                    }
                    catch(TCException tcexception1)
                    {
                        if(Debug.isOn("stylesheet,form,property,properties"))
                            tcexception1.printStackTrace();
                    }
            }
        }
        if(s == null || s.length() == 0)
            s = s1;
        String s2 = "";
        if(s.length() > 80)
        {
            for(int j = 0; j < s.length(); j += 80)
                if(j + 80 < s.length())
                    s2 = (new StringBuilder()).append(s2).append(s.substring(j, j + 80)).append("\n").toString();
                else
                    s2 = (new StringBuilder()).append(s2).append(s.substring(j)).toString();

        } else
        {
            s2 = s;
        }
        return Utilities.getToolTipHTML(s2, 0);
    }

    protected AIFComponentContext[] getSelection()
    {
    	
        if(descriptor == null)
            return null;
        int i = descriptor.getType();
        if(i == 9 || i == 10 || i == 11 || i == 13 || i == 14)
        {
            Object aobj[] = list.getSelectedValues();
            if(aobj == null || aobj.length == 0)
                return null;
            AIFComponentContext aaifcomponentcontext[] = new AIFComponentContext[aobj.length];
            for(int j = 0; j < aobj.length; j++)
                if(aobj[j] instanceof RelationLink)
                {
                    Object obj = ((RelationLink)aobj[j]).getPropValue();
                    aaifcomponentcontext[j] = new AIFComponentContext((InterfaceAIFComponent)obj, (InterfaceAIFComponent)obj, null);
                } else
                {
                    aaifcomponentcontext[j] = new AIFComponentContext((InterfaceAIFComponent)aobj[j], (InterfaceAIFComponent)aobj[j], null);
                }

            return aaifcomponentcontext;
        } else
        {
            return null;
        }
    }

    protected void addValue()
    {
    	
    	String str1 = "";
        Object localObject1 = null;
        Object localObject2 = null;
        String str2 = null;
        if (descriptor == null)
          return;
        
        int i = descriptor.getType();
        int j = descriptor.getMaxArraySize();
        int k = listModel.getSize();
        Object localObject3;
        Object localObject4;
        
        if ((j > 0) && (k >= j))
        {
        	
          localObject3 = Registry.getRegistry(this);
          localObject4 = ((Registry)localObject3).getString("exceedMaxArraySize");
         
          if (i == 8)
            localObject4 = ((Registry)localObject3).getString("stringMaxArraySize");
          Shell localShell = UIUtilities.getCurrentModalShell();
          if (localShell != null)
            MessageBox.post(UIUtilities.getCurrentModalShell(), (String)localObject4, ((Registry)localObject3).getString("warning"), 4);
          else
            MessageBox.post(getParentFrame(this), (String)localObject4, ((Registry)localObject3).getString("warning"), 4);
         
          return;
        
        }
        try
        {
        
        	if (addValueLovUIComponent != null)
            {
        		 
              localObject2 = addValueLovUIComponent.getValue();
              str2 = addValueLovUIComponent.getText();
              if ((localObject2 != null) && (!localObject2.toString().isEmpty()))
                str1 = localObject2.toString();
            }
            else if ((addValueTextField != null) && (addValueTextField.getText().length() != 0))
            {
            	
              str1 = addValueTextField.getText();
            }
        	
	        boolean bool;
	        Object localObject5;
	        switch (i)
	        {   
	       
	        case 1:
	        
	            if ((localObject2 instanceof Character))
	            {
	            	
	              listModel.addElement(localObject2);
	            }
	            else if (str1.length() > 0)
	            {
	            
	              localObject3 = Character.valueOf(str1.charAt(0));
	              listModel.addElement(localObject3);
	            }
	            break;
	          case 2:
	        	
	            localObject3 = null;
	            if (localObject2 != null)
	            {
	              if ((localObject2 instanceof Date))
	              {
	                localObject3 = localObject2;
	              }
	              else
	              {
	                localObject4 = new SimpleDateFormat();
	                localObject3 = ((DateFormat)localObject4).parse(localObject2.toString());
	              }
	            }
	            else if (dateButton != null)
	              localObject3 = dateButton.getDate();
	            if (localObject3 != null)
	              listModel.addElement(new FormattedDate((Date)localObject3));
	            break;
	          case 3:
	        	
	            if ((localObject2 instanceof Double))
	              localObject1 = localObject2;
	            else
	              localObject1 = Double.valueOf(str1);
	            listModel.addElement(localObject1);
	            break;
	          case 4:
	        	 
	            if ((localObject2 instanceof Float))
	              localObject1 = localObject2;
	            else
	              localObject1 = Float.valueOf(str1);
	            listModel.addElement(localObject1);
	            break;
	          case 5:
	        	
	            if ((localObject2 instanceof Integer))
	              localObject1 = localObject2;
	            else
	              localObject1 = Integer.valueOf(str1);
	            listModel.addElement(localObject1);
	            break;
	          case 6:
	        	 
	            if (localObject2 != null)
	            {
	              if ((localObject2 instanceof Boolean))
	              {
	                listModel.addElement(localObject2);
	              }
	              else
	              {
	                localObject4 = Boolean.valueOf(localObject2.toString());
	                listModel.addElement(localObject4);
	              }
	            }
	            else if (trueButton != null)
	            {
	              bool = trueButton.isSelected();
	              listModel.addElement(Boolean.valueOf(bool));
	            }
	            break;
	          case 12:
	        	
	            if ((str1 != null) && (str1.length() > 0))
	              listModel.addElement(str1);
	            break;
	          case 7:
	        	
	            if ((localObject2 != null) && ((localObject2 instanceof Short)))
	              localObject1 = localObject2;
	            else
	              localObject1 = Short.valueOf(str1);
	            listModel.addElement(localObject1);
	            break;
	          case 8:
	        	
	            if ((str1 != null) && (str1.length() > 0))
	              if ((str2 != null) && (localObject2 != null))
	                listModel.addElement(str2, localObject2);
	              else
	                listModel.addElement(str1);
	            break;
		        case 9:
		        	
		            bool = true;
		            int m = 0;
		            if (addValueLovUIComponent != null)
		            {
		              bool = false;
		              if ((localObject2 != null) && ((localObject2 instanceof InterfaceAIFComponent)))
		              {
		                if ((str2 != null) && (str2.toString().length() != 0))
		                  listModel.addElement(str2, new RelationLink(localObject2));
		                else
		                  listModel.addElement(new RelationLink(localObject2));
		              }
		              else if (!isExhaustive)
		              {
		                bool = true;
		                m = 1;
		              }
		            }
		            if (bool)
		            {
		              localObject5 = Registry.getRegistry(this);
		              List localList = getValidClipboardObjects();
		              if ((localList != null) && (localList.size() > 0))
		              {
		                if (m != 0)
		                {
		                  int i1 = ConfirmationDialog.post(getParentFrame(this), ((Registry)localObject5).getString("confirm"), ((Registry)localObject5).getString("addFromClipboardConfirmation.MSG"));
		                  if (i1 == 1)
		                    return;
		                }
		                Iterator localIterator = localList.iterator();
		                while (localIterator.hasNext())
		                {
		                  Object localObject6 = localIterator.next();
		                  listModel.addElement(new RelationLink(localObject6));
		                }
		              }
		              if ((m != 0) && (localList.isEmpty()))
		                MessageBox.post(getParentFrame(this), ((Registry)localObject5).getString("cannotConvertToRefType.MSG"), ((Registry)localObject5).getString("warning"), 4);
		            }
		            break;
		          case 10:
		          case 11:
		          case 13:
		          case 14:
		        	
		            if ((localObject2 != null) && ((localObject2 instanceof InterfaceAIFComponent)))
		            {
		            	
		              if ((str2 != null) && (str2.toString().length() != 0))
		                listModel.addElement(str2, localObject2);
		              else
		                listModel.addElement(new RelationLink(localObject2));
		            }
		            else
		            {
		            	
		              localObject5 = getClipboardObjects();
		              if ((localObject5 != null) && (((Vector)localObject5).size() > 0))
		                for (int n = 0; n < ((Vector)localObject5).size(); n++)
		                  listModel.addElement(new RelationLink(((Vector)localObject5).elementAt(n)));
		            }
		            break;
		          }
		        }
        catch(NumberFormatException localNumberFormatException)
        {
        	
	        Registry registry1 = Registry.getRegistry(this);
	        MessageBox.post(getParentFrame(this), registry1.getString("cannotConvert"), (localNumberFormatException), registry1.getString("error"), 4);
	        if(Debug.isOn("stylesheet,form,property,properties"))
	            Debug.printStackTrace(localNumberFormatException);
        }
        catch (Exception localException)
        {
        	
          MessageBox.post(getParentFrame(this), localException);
          if (Debug.isOn("stylesheet,form,property,properties"))
            Debug.printStackTrace(localException);
        }
       
        if(addValueTextField != null && addValueTextField.isVisible())
            addValueTextField.selectAll();
      
        setAscSortButton();
       
    }
    private List<Object> getValidClipboardObjects()
    	    throws TCException
    	  {
    	    Vector localVector = getClipboardObjects();
    	    return localVector;
    	  }


    protected void modifyValue()
    {
    	
        int i = list.getSelectedIndex();
        if(i < 0)
            return;
        String s = "";
        Object obj = null;
        Object obj1 = null;
        if(descriptor == null)
            return;
        int j = descriptor.getType();
        if(addValueLovUIComponent != null)
        {
            obj = addValueLovUIComponent.getValue();
            if(obj != null)
                s = obj.toString();
        } else
        if(addValueTextField != null)
            s = addValueTextField.getText();
        try
        {
            switch(j)
            {
            case 1: // '\001'
                Character character = null;
                if(obj != null && (obj instanceof Character))
                    character = (Character)obj;
                else
                if(s.length() > 0)
                    character = Character.valueOf(s.charAt(0));
                if(character != null)
                {
                    listModel.remove(i);
                    listModel.insertElementAt(character, i);
                    list.setSelectedIndex(i);
                }
                break;

            case 2: // '\002'
                Date date = null;
                if(obj != null)
                {
                    if(obj instanceof Date)
                    {
                        date = (Date)obj;
                    } else
                    {
                        SimpleDateFormat simpledateformat = new SimpleDateFormat();
                        date = simpledateformat.parse(s);
                    }
                } else
                if(dateButton != null)
                    date = dateButton.getDate();
                if(date != null)
                {
                    listModel.remove(i);
                    listModel.insertElementAt(new FormattedDate(date), i);
                    list.setSelectedIndex(i);
                }
                break;

            case 3: // '\003'
                if(obj instanceof Double)
                    obj1 = obj;
                else
                    obj1 = Double.valueOf(s);
                listModel.remove(i);
                listModel.insertElementAt(obj1, i);
                list.setSelectedIndex(i);
                break;

            case 4: // '\004'
                if(obj instanceof Float)
                    obj1 = obj;
                else
                    obj1 = Float.valueOf(s);
                listModel.remove(i);
                listModel.insertElementAt(obj1, i);
                list.setSelectedIndex(i);
                break;

            case 5: // '\005'
                if(obj instanceof Integer)
                    obj1 = obj;
                else
                    obj1 = Integer.valueOf(s);
                listModel.remove(i);
                listModel.insertElementAt(obj1, i);
                list.setSelectedIndex(i);
                break;

            case 6: // '\006'
                if(obj != null)
                {
                    if(obj instanceof Boolean)
                        obj1 = obj;
                    else
                        obj1 = Boolean.valueOf(obj.toString());
                } else
                if(trueButton != null)
                {
                    boolean flag = trueButton.isSelected();
                    obj1 = Boolean.valueOf(flag);
                }
                if(obj1 != null)
                {
                    listModel.remove(i);
                    listModel.insertElementAt(obj1, i);
                    list.setSelectedIndex(i);
                }
                break;

            case 12: // '\f'
                listModel.remove(i);
                listModel.insertElementAt(s, i);
                list.setSelectedIndex(i);
                break;

            case 7: // '\007'
                Object obj2;
                if(obj instanceof Short)
                    obj2 = obj;
                else
                    obj2 = Short.valueOf(s);
                listModel.remove(i);
                listModel.insertElementAt(obj2, i);
                list.setSelectedIndex(i);
                break;

            case 8: // '\b'
                listModel.remove(i);
                listModel.insertElementAt(s, i);
                list.setSelectedIndex(i);
                break;
            }
        }
        catch(NumberFormatException numberformatexception)
        {
            Registry registry = Registry.getRegistry(this);
            MessageBox.post(getParentFrame(this), registry.getString("cannotConvert"), numberformatexception, registry.getString("error"), 4);
            if(Debug.isOn("stylesheet,form,property,properties"))
                Debug.printStackTrace(numberformatexception);
        }
        catch(Exception exception)
        {
            MessageBox.post(getParentFrame(this), exception);
            if(Debug.isOn("stylesheet,form,property,properties"))
                Debug.printStackTrace(exception);
        }
        if(addValueTextField != null && addValueTextField.isVisible())
            addValueTextField.selectAll();
        setAscSortButton();
      
    }

    protected void removeValue()
    {
    	
        int ai[] = list.getSelectedIndices();
        if(ai == null || ai.length == 0)
            return;
        Arrays.sort(ai);
        for(int i = ai.length - 1; i >= 0; i--)
            listModel.removeElementAt(ai[i]);
        	
        setAscSortButton();
        int ai1[] = list.getSelectedIndices();
        boolean flag = ai1 != null && ai1.length > 0;
        removeButton.setEnabled(allowModify && flag);
        upButton.setEnabled(allowModify && flag);
        downButton.setEnabled(allowModify && flag);
        modifyButton.setEnabled(allowModify && flag);
        list.invalidate();
        list.doLayout();
        list.repaint();
       
    }

    protected void updateEditField()
    {
    	
        Object obj = list.getSelectedValue();
        if(obj == null)
            return;
        if(descriptor == null || !descriptor.isEnabled() || !modifiable)
            return;
        int i = descriptor.getType();
        if(addValueLovUIComponent != null)
        {
            if(obj instanceof RelationLink)
                obj = ((RelationLink)obj).getPropValue();
            addValueLovUIComponent.setSelectedValue(obj);
            return;
        }
        switch(i)
        {
        case 9: // '\t'
        case 10: // '\n'
        case 11: // '\013'
        case 13: // '\r'
        case 14: // '\016'
        default:
            break;

        case 1: // '\001'
            addValueTextField.setText(obj.toString());
            break;

        case 2: // '\002'
            if(obj instanceof FormattedDate)
            {
                Date date = ((FormattedDate)obj).getDate();
                dateButton.setDate(date);
            } else
            {
                dateButton.setDate((Date)obj);
            }
            break;

        case 3: // '\003'
        case 4: // '\004'
        case 5: // '\005'
        case 7: // '\007'
        case 8: // '\b'
        case 12: // '\f'
            addValueTextField.setText(obj.toString());
            break;

        case 6: // '\006'
            Boolean boolean1 = (Boolean)obj;
            if(boolean1.booleanValue())
                trueButton.setSelected(true);
            else
                falseButton.setSelected(true);
            break;
        }
      
    }

    protected Vector getClipboardObjects()
    {
    	
        AIFClipboard aifclipboard = AIFPortal.getClipboard();
        Transferable transferable = aifclipboard.getContents(this);
        Vector vector = null;
        if(transferable != null)
            try
            {
                vector = (Vector)transferable.getTransferData(new DataFlavor(Vector.class, "AIF Vector"));
            }
            catch(Exception exception) { }
        return vector;
    }

    protected void setAscSortButton()
    {
    	
        if(descriptor == null)
            return;
        boolean flag = descriptor.isEnabled() && modifiable;
        int i = descriptor.getType();
        if(flag && ascSortButton != null)
            if(i == 6)
                ascSortButton.setEnabled(false);
            else
            if(listModel.size() > 1)
                ascSortButton.setEnabled(allowModify);
            else
                ascSortButton.setEnabled(false);
    }

    protected void sortValue()
    {
    	
        Object aobj[] = listModel.toArray();
        String as[] = new String[aobj.length];
        for(int i = 0; i < aobj.length; i++)
            as[i] = (String)listModel.getDisplayObject(aobj[i]);

        ArraySorter.sortByIndex(aobj, as, true);
        listModel.removeAllElements();
        for(int j = 0; j < aobj.length; j++)
            listModel.addElement(listModel.getDisplayObject(aobj[j]), aobj[j]);

        list.revalidate();
        list.repaint();
    }

    private void moveValue(int i)
    {
    	
        if(list.isSelectionEmpty())
        {
            return;
        } else
        {
            int ai[] = list.getSelectedIndices();
            Utilities.move(list, ai, i, true);
            return;
        }
    }

    public Frame getParentFrame(JComponent jcomponent)
    {
    	
    	Object obj = jcomponent;
        Frame frame = null;
        do
        {
            if(obj == null || (obj instanceof Frame))
                break;
            obj = ((Component) (obj)).getParent();
            if(obj instanceof Frame)
                frame = (Frame)obj;
        } while(true);
        return frame;
    }

    public void setVisibleRowCount(int i)
    {
        list.setVisibleRowCount(i);
    }

    public boolean isValueModified()
    {
        Object aobj[] = listModel.toArray();
        if(originalValues == null || aobj == null)
        {
            int i = aobj == null ? 0 : aobj.length;
            int k = originalValues == null ? 0 : originalValues.length;
            if(Debug.isOn("stylesheet,form,property,properties"))
                //System.out.println((new StringBuilder()).append("PropertyArray:isValueModified(): returning: ").append(i != k).append(" originalValues ").append(((originalValues))).append(" values ").append(((aobj))).toString());
            return i != k;
        }
        int j = aobj.length;
        int l = originalValues.length;
        if(j != l)
        {
            if(Debug.isOn("stylesheet,form,property,properties"))
                //System.out.println((new StringBuilder()).append("PropertyArray:isValueModified(): newSize=").append(j).append("   oldSize=").append(l).toString());
            return true;
        }
        for(int i1 = 0; i1 < j; i1++)
        {
            if(originalValues[i1] == null && aobj[i1] == null)
                continue;
            Object obj = aobj[i1];
            if(aobj[i1] instanceof L10NModel)
                obj = ((L10NModel)aobj[i1]).m_modelValue;
            if(originalValues[i1] != null ? originalValues[i1].equals(obj) : aobj[i1] == null)
                continue;
            if(Debug.isOn("stylesheet,form,property,properties"))
                //System.out.println((new StringBuilder()).append("PropertyArray:isValueModified(): originalValues[i]=").append(originalValues[i1]).append("   values[i]=").append(aobj[i1]).append("   i=").append(i1).toString());
            return true;
        }

        return false;
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
            tcproperty1.getTCComponent().setTCProperty(tcproperty1);
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
    	//System.out.println("TCProperty getPropertyToSave(TCProperty tcproperty)----------in");
        if(tcproperty == null)
            return null;
        savable = false;
        
        if(list == null)
            return null;
        if(!tcproperty.isEnabled() || !modifiable)
        {
            if(Debug.isOn("stylesheet,form,property,properties"))
                Debug.println((new StringBuilder()).append("PropertyArray: save propName=").append(property).append(" not modifiable, skip.").toString());
            return null;
        }
        if(!isValueModified())
        {
            if(Debug.isOn("stylesheet,form,property,properties"))
                Debug.println((new StringBuilder()).append("PropertyArray: save propName=").append(property).append(" value not changed, skip.").toString());
            return null;
        }
        
        Object aobj[] = listModel.toArray();
        if(aobj == null || aobj.length == 0)
        {
        	//System.out.println("aobj == null || aobj.length == 0");
            if(Debug.isOn("stylesheet,form,property,properties"))
                Debug.println((new StringBuilder()).append("PropertyArray: save propName=").append(property).append(", value is null or length is 0, setNullVerdict(true)").toString());
            savable = true;
            tcproperty.setNullVerdict(true);
            return tcproperty;
        }
        int i = aobj.length;
        if(Debug.isOn("stylesheet,form,property,properties"))
        {
            Debug.println((new StringBuilder()).append("PropertyArray: save propName=").append(property).append(", length=").append(i).toString());
            for(int j = 0; j < i; j++)
                Debug.println((new StringBuilder()).append("        value[").append(j).append("]=").append(aobj[j]).toString());

        }
        int k = tcproperty.getPropertyType();
        switch(k)
        {
        default:
            break;

        case 2: // '\002'
            Date adate[] = new Date[i];
            for(int l = 0; l < i; l++)
                if(aobj[l] instanceof FormattedDate)
                    adate[l] = ((FormattedDate)aobj[l]).getDate();
                else
                    adate[l] = (Date)aobj[l];

            savable = true;
            tcproperty.setDateValueArrayData(adate);
            break;

        case 1: // '\001'
            char ac[] = new char[i];
            for(int i1 = 0; i1 < i; i1++)
                ac[i1] = ((Character)aobj[i1]).charValue();

            savable = true;
            tcproperty.setCharValueArrayData(ac);
            break;

        case 3: // '\003'
            double ad[] = new double[i];
            for(int j1 = 0; j1 < i; j1++)
                ad[j1] = ((Double)aobj[j1]).doubleValue();

            savable = true;
            tcproperty.setDoubleValueArrayData(ad);
            break;

        case 4: // '\004'
            float af[] = new float[i];
            for(int k1 = 0; k1 < i; k1++)
                af[k1] = ((Float)aobj[k1]).floatValue();

            savable = true;
            tcproperty.setFloatValueArrayData(af);
            break;

        case 5: // '\005'
            int ai[] = new int[i];
            for(int l1 = 0; l1 < i; l1++)
                ai[l1] = ((Integer)aobj[l1]).intValue();

            savable = true;
            tcproperty.setIntValueArrayData(ai);
            break;

        case 7: // '\007'
            short aword0[] = new short[i];
            for(int i2 = 0; i2 < i; i2++)
                aword0[i2] = ((Short)aobj[i2]).shortValue();

            savable = true;
            tcproperty.setShortValueArrayData(aword0);
            break;

        case 6: // '\006'
            boolean aflag[] = new boolean[i];
            for(int j2 = 0; j2 < i; j2++)
                aflag[j2] = ((Boolean)aobj[j2]).booleanValue();

            savable = true;
            tcproperty.setLogicalValueArrayData(aflag);
            break;

        case 8: // '\b'
            String as[] = new String[i];
            for(int k2 = 0; k2 < i; k2++)
            {
                if(aobj[k2] == null)
                {
                    as[k2] = "";
                    continue;
                }
                if(addValueLovUIComponent != null)
                {
                    Object obj = listModel.getModelObject(aobj[k2]);
                    if(obj != null)
                        as[k2] = obj.toString();
                    else
                        as[k2] = aobj[k2].toString();
                } else
                {
                    as[k2] = aobj[k2].toString();
                }
            }

            savable = true;
            tcproperty.setStringValueArrayData(as);
            break;

        case 12: // '\f'
            String as1[] = new String[i];
            for(int l2 = 0; l2 < i; l2++)
                as1[l2] = aobj[l2].toString();

            savable = true;
            tcproperty.setNoteValueArrayData(as1);
            break;

        case 9: // '\t'
        case 10: // '\n'
        case 11: // '\013'
        case 13: // '\r'
        case 14: // '\016'
        	atccomponent1 = new TCComponent[i];
        	//System.out.println("atccomponent[i3] ===========" + i);
            for(int i3 = 0; i3 < i; i3++)
                if(aobj[i3] instanceof RelationLink)
                    atccomponent1[i3] = (TCComponent)((RelationLink)aobj[i3]).getPropValue();
                else
                    atccomponent1[i3] = (TCComponent)aobj[i3];

            savable = true;
            
            tcproperty.setReferenceValueArrayData(atccomponent1);

            


            break;
        }
        //System.out.println("TCProperty getPropertyToSave(TCProperty tcproperty)----------out");
        return tcproperty;
    }
    public TCComponent[] getComp(){
    	
    	Object aobj[] = listModel.toArray();
    	int i = aobj.length;
    	TCComponent[] atccomponent1 = new TCComponent[i];
    	 for(int i3 = 0; i3 < i; i3++)
             if(aobj[i3] instanceof RelationLink)
                 atccomponent1[i3] = (TCComponent)((RelationLink)aobj[i3]).getPropValue();
             else
                 atccomponent1[i3] = (TCComponent)aobj[i3];
    	 
		return atccomponent1;
    	
    }
    @Override
	public boolean isPropertyModified(TCComponent tccomponent)
        throws Exception
    {
    	//System.out.println("isPropertyModified(TCComponent tccomponent)");
        if(property == null)
        {
            return false;
        } else
        {
            TCProperty tcproperty = tccomponent.getTCProperty(property);
            return isPropertyModified(tcproperty);
        }
    }

    @Override
	public boolean isPropertyModified(TCProperty tcproperty)
        throws Exception
    {
    	
        savable = false;
        if(tcproperty == null || list == null)
            return false;
        if(!tcproperty.isEnabled() || !modifiable)
            return false;
        else
            return isValueModified();
    }

    @Override
	public void paint(Graphics g)
    {
    	
        super.paint(g);
        if(mandatory)
            Painter.paintIsRequired(this, g);
    }

    private void setConstraintsOnFilterDocument()
    {
    	
    	
        int i = descriptor.getType();
        boolean flag = descriptor.isEnabled() && modifiable;
        switch(i)
        {
        case 2: // '\002'
        case 6: // '\006'
        case 9: // '\t'
        case 10: // '\n'
        case 11: // '\013'
        default:
            break;

        case 1: // '\001'
            if(flag && document != null)
                document.setLength(1);
            break;

        case 3: // '\003'
        case 4: // '\004'
            if(flag && document != null)
                document.setAcceptedChars("0123456789.eE");
            break;

        case 5: // '\005'
            if(flag && document != null)
                document.setAcceptedChars("9876543210");
            break;

        case 7: // '\007'
            if(flag && document != null)
                document.setAcceptedChars("1023456789");
            break;

        case 12: // '\f'
            if(flag && document != null)
                document.setLength(descriptor.getMaxStringLength());
            break;

        case 8: // '\b'
            int j = descriptor.getMaxStringLength();
            if(j <= 0)
                j = -1;
            if(flag && document != null)
            {
                document.setLength(j);
                break;
            }
            if(flag && addValueLovUIComponent != null)
                addValueLovUIComponent.setLengthLimit(j);
            break;
        }
        //System.out.println("setConstraintsOnFilterDocument()-----------out");
      
    }

    @Override
	public void processComponentEvents(AIFComponentEvent aaifcomponentevent[])
    {
    	//System.out.println("processComponentEvents(AIFComponentEvent aaifcomponentevent[])");

        if(addValueLovUIComponent == null)
            return;
        Object obj = null;
        Arrays.sort(aaifcomponentevent);
        for(int i = 0; i < aaifcomponentevent.length; i++)
        {
            TCComponent tccomponent = (TCComponent)aaifcomponentevent[i].getComponent();
            if(tccomponent != component || !(aaifcomponentevent[i] instanceof AIFInputEvent))
                continue;
            AIFInputEvent aifinputevent = (AIFInputEvent)aaifcomponentevent[i];
            if(!aifinputevent.getProperty().equals(getProperty()))
                continue;
            Object obj1 = aifinputevent.getInputValue();
            if(obj1 == null || !(obj1 instanceof java.util.List))
                continue;
            
            if(isExhaustive){
                listModel.clear();
                //System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
            }
            java.util.List list1 = (java.util.List)obj1;
            Object obj2 = addValueLovUIComponent.getValue();
            String as[];
            String as1[];
            for(Iterator iterator = list1.iterator(); iterator.hasNext(); firePropertyChange(getProperty(), as, as1))
            {
                addValueLovUIComponent.setSelectedValue(iterator.next());
                as = getValues();
                addValue();
                as1 = getValues();
            }
           
            addValueLovUIComponent.setSelectedValue(obj2);
        
        }
       
    }

    @Override
	public void closeSignaled()
    {
    	//System.out.println("closeSignaled()");
        TCSession tcsession = null;
        if(descriptor != null)
            tcsession = descriptor.getTypeComponent().getSession();
        else
        if(component != null)
            tcsession = component.getSession();
        if(tcsession != null)
            tcsession.removeAIFComponentEventListener(this);
    }

    protected String property;
    protected boolean mandatory;
    protected boolean modifiable;
    protected boolean isExhaustive;
    protected TCProperty tcProperty;
    protected TCComponent component;
    protected TCPropertyDescriptor descriptor;
    protected Object originalValues[];
    protected Object originalDispValues[];
    protected boolean savable;
    protected JList list;
    protected JScrollPane listScrollPane;
    public PropArrayListModel listModel;
    protected JButton addButton;
    protected JButton removeButton;
    protected JButton upButton;
    protected JButton downButton;
    protected JButton ascSortButton;
    protected JButton modifyButton;
    protected JPanel listButtonPanel;
    protected JPanel addValuePanel;
    protected iTextField addValueTextField;
    protected FilterDocument document;
    protected LOVUIComponent addValueLovUIComponent;
    protected DateButton dateButton;
    protected JRadioButton trueButton;
    protected JRadioButton falseButton;
    protected JToggleButton expandButton;
    protected boolean allowModify;
    protected int numOfObjsDisplayedInList;
    protected int mouseOver;
    protected java.util.List typedReferenceClassList;
    protected static final String IMF_display_full_path = "IMF_display_full_path";
    public static final int DEFAULT_VIEW_COUNT = 10;
    Object popupMenu = new JPanel(new HorizontalLayout());
    public TCComponent[] atccomponent1;


}


