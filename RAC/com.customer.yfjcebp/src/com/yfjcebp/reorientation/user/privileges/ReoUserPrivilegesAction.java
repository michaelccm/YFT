package com.yfjcebp.reorientation.user.privileges;
import javax.swing.SwingUtilities;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aif.common.actions.AbstractAIFAction;
import com.teamcenter.rac.kernel.TCComponentUser;
import com.teamcenter.rac.kernel.TCComponentUserType;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.Registry;

public class ReoUserPrivilegesAction extends AbstractAIFAction{
	private AbstractAIFUIApplication app;
	private TCSession session;
	private Registry reg;
	public ReoUserPrivilegesAction(AbstractAIFUIApplication arg0, String arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
		this.app = arg0; 	
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		session = (TCSession) app.getSession();
		reg = Registry.getRegistry(this);
		String preVal = getPreVal("YFJC_transfer_org_valid_userID").trim();
		if(preVal.equals("")){
			MessageBox.post(reg.getString("tarnsfer_valid_userid_null"), reg.getString("info.title"), MessageBox.INFORMATION);
		}else{
			try {
				TCComponentUser user = findUser(preVal);
				if(user == null){
					MessageBox.post(reg.getString("tarnsfer_valid_userid_invalid"), reg.getString("info.title"), MessageBox.INFORMATION);
				}else{
					//得到当前登录用户ID
					String user_id = session.getUser().getUserId();
					System.out.println(user_id+"--->"+preVal);
					if(!user_id.equals(preVal)){
						MessageBox.post(reg.getString("user_can_not_transfer"), reg.getString("info.title"), MessageBox.INFORMATION);
					}else{
						//弹出“用户转岗对话框”
					   SwingUtilities.invokeLater(new Runnable() {
						
						   @Override
						   public void run() {
							   // TODO Auto-generated method stub
							   ReoUserPrivilegesDialog dialog = new ReoUserPrivilegesDialog(session);
							   dialog.initDialog();
						   }
					   });
						
					}
				}
			} catch (TCException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	//得首选项的值
	private String getPreVal(String preName){
		TCPreferenceService preService = session.getPreferenceService();
		return  preService.getString(TCPreferenceService.TC_preference_all, preName);
	}
	
	//查询用户是否存在
	private TCComponentUser findUser(String user_id){
		TCComponentUserType userType;
		try {
			userType = (TCComponentUserType) session.getTypeComponent("User");
			TCComponentUser user = userType.find(user_id);
			return user;
		} catch (TCException e) {
			// TODO Auto-generated catch block
			return null;
		}
		
	}
	
}
