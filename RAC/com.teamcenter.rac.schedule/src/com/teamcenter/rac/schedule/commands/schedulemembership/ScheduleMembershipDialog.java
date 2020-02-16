package com.teamcenter.rac.schedule.commands.schedulemembership;

import com.teamcenter.rac.common.AbstractTCCommandDialog;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentType;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.schedule.ScheduleViewApplication;
import com.teamcenter.rac.schedule.ScheduleViewApplicationPanel;
import com.teamcenter.rac.schedule.commands.deferred.ScheduleDeferredContext;
import com.teamcenter.rac.schedule.common.tree.ScheduleTreeNode;
import com.teamcenter.rac.schedule.common.util.ScheduleUtil;
import com.teamcenter.rac.schedule.common.util.SchedulingExceptionHandler;
import com.teamcenter.rac.schedule.project.scheduling.SchedulingException;
import com.teamcenter.rac.util.Registry;
import com.teamcenter.rac.util.UIUtilities;
import com.teamcenter.rac.util.VerticalLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

public class ScheduleMembershipDialog extends AbstractTCCommandDialog
{
  GridBagLayout gridBag;
  Registry r = Registry.getRegistry(this);
  private TCSession tcSession = null;
  private ScheduleMemberTreePanel memberTreePanel = null;
  private ScheduleViewApplication schApp = null;
  private JPanel messagePanel = null;
  private JLabel costDBALabel = null;
  private JLabel deferredLabel = null;
  private JLabel costDBADeferredLabel = null;
  private boolean showCostDBANotes = false;
  private DefaultTreeModel model;
  private ScheduleMemberTree memberTree;
  Set<String> membersComps = new HashSet();
  Set<ScheduleTreeNode> allNodes = new HashSet();

  public ScheduleMembershipDialog(Frame paramFrame, ScheduleMembershipCommand paramScheduleMembershipCommand, boolean paramBoolean)
  {
    super(paramFrame, paramScheduleMembershipCommand, Boolean.valueOf(paramBoolean));
    setBounds(100, 100, 800, 639);
    if (paramScheduleMembershipCommand != null)
      this.tcSession = paramScheduleMembershipCommand.session;
    this.schApp = ScheduleViewApplication.getApplication();
    setTitle(this.r.getString("dialog.TITLE"));
    initUI();
    this.okButton.setEnabled(true);
    this.applyButton.setEnabled(false);
    this.applyButton.setVisible(false);
    init();
  }

  protected void init()
  {
	System.out.println("init"+this.okButton.isEnabled());
    this.mainPanel.setLayout(null);
    this.memberTreePanel = new ScheduleMemberTreePanel(null, this.tcSession, true);
    this.memberTree = this.memberTreePanel.getMemberTree();
    this.model = this.memberTree.getTreeModel();
    this.messagePanel = new JPanel();
    this.messagePanel.setLayout(new VerticalLayout(1, 1, 1, 1, 21));
    JPanel localJPanel = new JPanel();
    localJPanel.setBounds(10, 10, 800, 520);
    this.mainPanel.add(localJPanel);
    localJPanel.setLayout(null);
    Object localObject = UIUtilities.createGradientHeader(this.r.getString("dialog.MSG"));
    ((JPanel)localObject).setBounds(10, 5, 795, 40);
    localJPanel.add((Component)localObject);
    localObject = new MemberSearchPanel();
    ((MemberSearchPanel)localObject).setBounds(10, 73, 795, 49);
    localJPanel.add((Component)localObject);
    this.memberTreePanel.setBounds(10, 140, 796, 225);
    localJPanel.add(this.memberTreePanel);
    this.messagePanel.setBounds(10, 401, 795, 108);
    localJPanel.add(this.messagePanel);
    this.mainPanel.add(localJPanel);
    this.costDBALabel = new JLabel(this.r.getString("costDBA.NOTES"));
    this.costDBALabel.setBorder(BorderFactory.createTitledBorder(this.r.getString("notes.TITLE")));
    this.costDBADeferredLabel = new JLabel(this.r.getString("costDBADeferred.NOTES"));
    this.costDBADeferredLabel.setBorder(BorderFactory.createTitledBorder(this.r.getString("notes.TITLE")));
    this.deferredLabel = new JLabel(this.r.getString("deferred.NOTES"));
    this.deferredLabel.setBorder(BorderFactory.createTitledBorder(this.r.getString("notes.TITLE")));
    boolean bool1 = ScheduleUtil.isCostDBA();
    if ((this.memberTreePanel.isEditable()) && (!bool1))
      this.showCostDBANotes = true;
    if (!this.memberTreePanel.isEditable())
      this.okButton.setVisible(false);
    //YFCJ_ADDED
    if(tcSession.hasBypass()){
    	this.memberTreePanel.setEnabled(true);
    	this.showCostDBANotes = true;
    	this.okButton.setVisible(true);
    	this.okButton.setEnabled(true);
    	this.messagePanel.removeAll();
    	this.messagePanel.add("unbound.bind", this.costDBALabel);
    	this.messagePanel.validate();
    	System.out.println("init"+this.okButton.isEnabled());
    }
    else{
	    boolean bool2 = ScheduleDeferredContext.inDeferredSession();
	    if ((this.showCostDBANotes) && (bool2))
	      this.messagePanel.add("unbound.bind", this.costDBADeferredLabel);
	    else if (this.showCostDBANotes)
	      this.messagePanel.add("unbound.bind", this.costDBALabel);
	    else if (bool2)
	      this.messagePanel.add("unbound.bind", this.deferredLabel);
	    this.messagePanel.validate();
    }
    this.memberTree.expandToLevel(2);
    
   //YFCJ_END
    /*this.messagePanel.removeAll();
    boolean bool2 = ScheduleDeferredContext.inDeferredSession();
    if ((this.showCostDBANotes) && (bool2))
      this.messagePanel.add("unbound.bind", this.costDBADeferredLabel);
    else if (this.showCostDBANotes)
      this.messagePanel.add("unbound.bind", this.costDBALabel);
    else if (bool2)
      this.messagePanel.add("unbound.bind", this.deferredLabel);
    this.messagePanel.validate();*/
  }

  public boolean showCloseButton()
  {
    return false;
  }

  public void startCommandOperation()
  {
  }

  public boolean isPerformable()
  {
	if(tcSession.hasBypass())
		return true;
	else
		return ((ScheduleViewApplicationPanel)this.schApp.getApplicationPanel()).isScheduleModifiable();
  }

  public void okPressed()
  {
    super.okPressed();
    try
    {
      if (this.memberTreePanel.isUpdated())
      {
        this.memberTreePanel.performAdds();
        this.memberTreePanel.performDeletes();
        this.memberTreePanel.performUpdates();
        this.memberTreePanel.reset(true);
        dispose();
      }
    }
    catch (SchedulingException localSchedulingException)
    {
      SchedulingExceptionHandler.handleScheduleException(localSchedulingException);
      dispose();
    }
  }

  public void stopPressed()
  {
    this.memberTreePanel.reset(false);
    dispose();
  }

  public void cancelPressed()
  {
    super.cancelPressed();
    this.memberTreePanel.reset(false);
    dispose();
  }

  public void run()
  {
	System.out.println("run");
    setSize(new Dimension(850, 600));
    setResizable(false);
    setVisible(true);
    centerToScreen();
  }

  public class MemberSearchPanel extends JPanel
  {
    protected static final String SEGOEFONT = "Segoe UI";
    private JTextField txtSearchField;
    private JButton btnClear;
    private String FILTER_BLANK = "";
    protected boolean systemMsgIsShowing = true;
    private String filterMsg = ScheduleMembershipDialog.this.r.getString("dialog.search.panel.textmsg");
    private ScheduleTreeNode rootNode = ScheduleMembershipDialog.this.memberTree.getRootNode();
    private ScheduleTreeNode shadowRoot;

    public MemberSearchPanel()
    {
      System.out.println("MemberSearchPanel");
      setLayout(null);
      this.txtSearchField = new JTextField();
      this.txtSearchField.setBounds(10, 11, 650, 23);
      this.txtSearchField.setText(this.filterMsg);
      this.txtSearchField.setForeground(new Color(192, 180, 191));
      this.txtSearchField.setFont(new Font("Tree.font", 0, 10));
      this.txtSearchField.setColumns(10);
      add(this.txtSearchField);
      this.btnClear = new JButton(ScheduleMembershipDialog.this.r.getString("dialog.search.button.clearmsg"));
      this.btnClear.setToolTipText(ScheduleMembershipDialog.this.r.getString("dialog.search.button.tooltipTxt"));
      this.btnClear.setBounds(680, 11, 80, 23);
      this.btnClear.setFont(new Font("Tree.font", 0, 11));
      this.btnClear.setEnabled(false);
      add(this.btnClear);
      this.btnClear.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent paramAnonymousActionEvent)
        {
          ScheduleMembershipDialog.MemberSearchPanel.this.systemMsgIsShowing = true;
          ScheduleMembershipDialog.this.model.setRoot(ScheduleMembershipDialog.MemberSearchPanel.this.rootNode);
          ScheduleMembershipDialog.MemberSearchPanel.this.txtSearchField.setText(ScheduleMembershipDialog.MemberSearchPanel.this.filterMsg);
          ScheduleMembershipDialog.MemberSearchPanel.this.txtSearchField.setForeground(new Color(192, 180, 191));
          ScheduleMembershipDialog.MemberSearchPanel.this.btnClear.setEnabled(false);
          ScheduleMembershipDialog.MemberSearchPanel.this.checkAssignedMembers();
        }
      });
      this.txtSearchField.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent paramAnonymousActionEvent)
        {
          ScheduleMembershipDialog.MemberSearchPanel.this.btnClear.setEnabled(true);
          ScheduleMembershipDialog.MemberSearchPanel.this.doFilter(ScheduleMembershipDialog.MemberSearchPanel.this.txtSearchField.getText());
        }
      });
      this.txtSearchField.addKeyListener(new KeyAdapter()
      {
        public void keyReleased(KeyEvent paramAnonymousKeyEvent)
        {
          if (!ScheduleMembershipDialog.MemberSearchPanel.this.systemMsgIsShowing)
          {
            ScheduleMembershipDialog.MemberSearchPanel.this.btnClear.setEnabled(false);
            ScheduleMembershipDialog.MemberSearchPanel.this.systemMsgIsShowing = true;
          }
          else
          {
            ScheduleMembershipDialog.MemberSearchPanel.this.btnClear.setEnabled(true);
          }
        }
      });
      this.txtSearchField.addFocusListener(new FocusAdapter()
      {
        public void focusLost(FocusEvent paramAnonymousFocusEvent)
        {
          if ((ScheduleMembershipDialog.MemberSearchPanel.this.txtSearchField.getText().trim().length() == 0) || (ScheduleMembershipDialog.MemberSearchPanel.this.systemMsgIsShowing))
          {
            ScheduleMembershipDialog.MemberSearchPanel.this.txtSearchField.setText(ScheduleMembershipDialog.MemberSearchPanel.this.filterMsg);
            ScheduleMembershipDialog.MemberSearchPanel.this.txtSearchField.setForeground(new Color(192, 180, 191));
          }
        }

        public void focusGained(FocusEvent paramAnonymousFocusEvent)
        {
          ScheduleMembershipDialog.MemberSearchPanel.this.txtSearchField.setEnabled(true);
          if (ScheduleMembershipDialog.MemberSearchPanel.this.systemMsgIsShowing)
          {
            ScheduleMembershipDialog.MemberSearchPanel.this.txtSearchField.setText(ScheduleMembershipDialog.MemberSearchPanel.this.FILTER_BLANK);
            ScheduleMembershipDialog.MemberSearchPanel.this.txtSearchField.setForeground(new Color(0, 0, 0));
          }
          else if (ScheduleMembershipDialog.MemberSearchPanel.this.txtSearchField.getText().trim().length() > 0)
          {
            ScheduleMembershipDialog.MemberSearchPanel.this.btnClear.setEnabled(true);
          }
        }
      });
    }

    private void doFilter(String paramString)
    {
      System.out.println("doFilter");
      this.shadowRoot = mirror(this.rootNode);
      getAssignedMembers();
      visitAllNodes();
      paramString = paramString.replace("\b", "");
      if (paramString.trim().toString().equals(this.FILTER_BLANK))
      {
        ScheduleMembershipDialog.this.model.setRoot(this.rootNode);
        ScheduleMembershipDialog.this.memberTree.setModel(ScheduleMembershipDialog.this.model);
        this.systemMsgIsShowing = true;
        this.txtSearchField.setForeground(new Color(192, 180, 191));
        this.txtSearchField.setText(this.filterMsg);
        this.btnClear.grabFocus();
      }
      else
      {
        Filter localFilter = new Filter(paramString);
        this.shadowRoot = localFilter.match((ScheduleTreeNode)this.shadowRoot.getRoot());
        ScheduleMembershipDialog.this.model.setRoot(this.shadowRoot);
        ScheduleMembershipDialog.this.memberTree.setModel(ScheduleMembershipDialog.this.model);
      }
      for (int i = 0; i < ScheduleMembershipDialog.this.memberTree.getRowCount(); i++)
        ScheduleMembershipDialog.this.memberTree.expandRow(i);
      checkAssignedMembers();
    }

    public void checkAssignedMembers()
    {
      System.out.println("checkAssignedMembers");
      Iterator localIterator = ScheduleMembershipDialog.this.allNodes.iterator();
      while (localIterator.hasNext())
      {
        ScheduleTreeNode localScheduleTreeNode = (ScheduleTreeNode)localIterator.next();
        Object localObject = localScheduleTreeNode.getUserObject();
        if ((localObject instanceof String))
        {
          String str1 = (String)localObject;
          String str2 = localScheduleTreeNode.getType();
          String str3 = str1 + str2;
          if (ScheduleMembershipDialog.this.membersComps.contains(str3))
          {
            TreePath localTreePath = new TreePath(((DefaultTreeModel)ScheduleMembershipDialog.this.memberTree.getModel()).getPathToRoot(localScheduleTreeNode));
            localScheduleTreeNode.setChecked(true);
            int i = ScheduleMembershipDialog.this.memberTree.getRowForPath(localTreePath);
            if (i > -1)
            {
              ScheduleMembershipDialog.this.memberTree.getCheckRenderer().updateRowChecked(i, true);
              ScheduleMembershipDialog.this.memberTree.getCheckRenderer().getCheckBox().setSelected(true);
              ScheduleMembershipDialog.this.memberTree.setSelectionPath(localTreePath);
              ScheduleMembershipDialog.this.memberTree.getCheckRenderer().getTreeCellRendererComponent(ScheduleMembershipDialog.this.memberTree, localScheduleTreeNode, true, true, localScheduleTreeNode.isLeaf(), i, true);
            }
          }
        }
      }
    }

    public void visitAllNodes()
    {
      System.out.println("visitAllNodes");
      TreeNode localTreeNode = (TreeNode)ScheduleMembershipDialog.this.memberTree.getModel().getRoot();
      if ((localTreeNode instanceof ScheduleTreeNode))
      {
        ScheduleTreeNode localScheduleTreeNode = (ScheduleTreeNode)localTreeNode;
        visitAllNodes(localScheduleTreeNode);
      }
    }

    public void visitAllNodes(ScheduleTreeNode paramScheduleTreeNode)
    {
      System.out.println("visitAllNodes");
      ScheduleMembershipDialog.this.allNodes.add(paramScheduleTreeNode);
      if (paramScheduleTreeNode.getChildCount() >= 0)
      {
        Enumeration localEnumeration = paramScheduleTreeNode.children();
        while (localEnumeration.hasMoreElements())
        {
          TreeNode localTreeNode = (TreeNode)localEnumeration.nextElement();
          if ((localTreeNode instanceof ScheduleTreeNode))
          {
            ScheduleTreeNode localScheduleTreeNode = (ScheduleTreeNode)localTreeNode;
            visitAllNodes(localScheduleTreeNode);
          }
        }
      }
    }

    public void getAssignedMembers()
    {
      System.out.println("getAssignedMembers");
      TableModel localTableModel = ScheduleMembershipDialog.this.memberTreePanel.getScheduleMemberPanel().getMemberTable().getModel();
      if ((localTableModel instanceof MemberTableModel))
      {
        int i = ScheduleMembershipDialog.this.memberTreePanel.getScheduleMemberPanel().getMemberTable().getRowCount();
        MemberTableModel localMemberTableModel = (MemberTableModel)localTableModel;
        for (int j = 0; j < i; j++)
        {
          Object localObject = localMemberTableModel.getValueAt(j, 0);
          if ((localObject instanceof TCComponent))
          {
            TCComponent localTCComponent = (TCComponent)localObject;
            ScheduleMembershipDialog.this.membersComps.add(localTCComponent.toDisplayString() + localTCComponent.getTypeComponent().toDisplayString());
          }
        }
      }
    }

    private ScheduleTreeNode mirror(ScheduleTreeNode paramScheduleTreeNode)
    {
      System.out.println("mirror");
      ScheduleTreeNode localScheduleTreeNode1 = paramScheduleTreeNode.copy();
      Object[] arrayOfObject1 = paramScheduleTreeNode.getChildNodes();
      if (arrayOfObject1 != null)
        for (Object localObject : arrayOfObject1)
          if ((localObject instanceof ScheduleTreeNode))
          {
            ScheduleTreeNode localScheduleTreeNode2 = (ScheduleTreeNode)localObject;
            localScheduleTreeNode1.add(mirror(localScheduleTreeNode2));
          }
      return localScheduleTreeNode1;
    }

    class Filter
    {
      private String patternToMatch;

      public Filter(String arg2)
      {
        this.patternToMatch = arg2;
      }

      public ScheduleTreeNode match(ScheduleTreeNode paramScheduleTreeNode)
      {
        for (boolean bool = true; bool; bool = patternMatches(paramScheduleTreeNode));
        return paramScheduleTreeNode;
      }

      private boolean patternMatches(ScheduleTreeNode paramScheduleTreeNode)
      {
        boolean bool = false;
        Object localObject1 = paramScheduleTreeNode.getFirstLeaf();
        if (((DefaultMutableTreeNode)localObject1).isRoot())
          return false;
        int i = paramScheduleTreeNode.getLeafCount();
        for (int j = 0; j < i; j++)
        {
          DefaultMutableTreeNode localDefaultMutableTreeNode1 = ((DefaultMutableTreeNode)localObject1).getNextLeaf();
          Object localObject2 = ((DefaultMutableTreeNode)localObject1).getUserObject();
          if (localObject2 != null)
          {
            String str1 = localObject2.toString();
            String str2 = this.patternToMatch;
            try
            {
              this.patternToMatch = this.patternToMatch.replace("?", "");
              this.patternToMatch = this.patternToMatch.replace("*", "");
              str2 = ".*?" + this.patternToMatch + ".*";
            }
            catch (Exception localException)
            {
              str2 = this.patternToMatch;
            }
            Pattern localPattern = Pattern.compile(str2);
            Matcher localMatcher = localPattern.matcher(str1);
            if (!localMatcher.matches())
            {
              DefaultMutableTreeNode localDefaultMutableTreeNode2 = (DefaultMutableTreeNode)((DefaultMutableTreeNode)localObject1).getParent();
              if (localDefaultMutableTreeNode2 != null)
                localDefaultMutableTreeNode2.remove((MutableTreeNode)localObject1);
              bool = true;
            }
            localObject1 = localDefaultMutableTreeNode1;
          }
        }
        return bool;
      }
    }
  }
}