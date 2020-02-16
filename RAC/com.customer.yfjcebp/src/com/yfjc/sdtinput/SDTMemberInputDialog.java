package com.yfjc.sdtinput;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import java.util.Map.Entry;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;

import com.teamcenter.rac.aif.AbstractAIFDialog;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentGroupMember;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentUser;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.pse.AbstractPSEApplication;
import com.teamcenter.rac.util.Registry;
import com.teamcenter.rac.util.VerticalLayout;

public class SDTMemberInputDialog extends AbstractAIFDialog implements ActionListener{
    private Registry registry =null;
    private JRadioButton[] rbt;
    private JButton sureBtn;
    private JButton cancelBtn;
    private Vector lineVec;
    private HashMap memberMap;
    private ButtonGroup bg;
    private String key;
    
    public SDTMemberInputDialog(Vector v,HashMap map) {
        registry = Registry.getRegistry("com.yfjc.sdtinput.sdtinput");
        lineVec=v;
        memberMap=map;
        initUI();
        this.setAlwaysOnTop(true);
    }

    public void initUI() {
        this.setTitle(registry.getString("dialog.title"));
        JPanel main = new JPanel(new BorderLayout());
        JPanel pane1 = new JPanel(new VerticalLayout(5,2,2,2,2));
        rbt=new JRadioButton[memberMap.size()];
        bg = new ButtonGroup();
        //System.out.println(" mmeberMap "+memberMap.size());
        for (int i = 0; i < memberMap.size(); i++) {
            Iterator iterator = memberMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Entry entry = (Entry) iterator.next();
                
                rbt[i]=new JRadioButton(entry.getKey().toString(),false);
                rbt[i].addItemListener(new ItemListener() {

                    @Override
                    public void itemStateChanged(ItemEvent arg0) {
                        if(arg0.getStateChange()==ItemEvent.SELECTED){
                            key=((JRadioButton)arg0.getSource()).getText();
                            sureBtn.setEnabled(true);
                        }else if(arg0.getStateChange()==ItemEvent.DESELECTED){
                            sureBtn.setEnabled(false);
                        }
                    }
                });
                bg.add(rbt[i]);
                pane1.add("top.bind.left.top",rbt[i]);
            }
        }
        
        JPanel pane2 = new JPanel(new FlowLayout(FlowLayout.CENTER));
        sureBtn = new JButton("OK");
        sureBtn.setEnabled(false);
        sureBtn.addActionListener(this);
        cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(this);
        pane2.add(sureBtn);
        pane2.add(cancelBtn);

        main.add(new JScrollPane(pane1), BorderLayout.CENTER);
        main.add(pane2, BorderLayout.SOUTH);
        main.setPreferredSize(new Dimension(800, 550));
        getContentPane().add(main);
        centerToScreen();
        setVisible(true);
    }
    
    
    
    @Override
	public void actionPerformed(ActionEvent a) {
        if (a.getSource() == sureBtn) {
            TCComponentGroupMember member = (TCComponentGroupMember) memberMap
                    .get(key);
            //System.out.println("key==" + key);
            //System.out.println(" lineVec .size  "+lineVec.size());
            if (member != null) {
                for (int i = 0; i < lineVec.size(); i++) {
                    TCComponentBOMLine line = (TCComponentBOMLine) lineVec
                            .get(i);
                    try {
                        TCComponentItemRevision rev = line.getItemRevision();
                        TCComponentUser user = member.getUser();
                        rev.add("JCI6_SDT", user);
                        ((AbstractPSEApplication)AIFUtility.getCurrentApplication()).getBOMWindow().refresh();
                    } catch (TCException e) {
                        e.printStackTrace();
                    }
                }
            }
            this.dispose();

        } else {

            this.dispose();
        }
    }

    
}
