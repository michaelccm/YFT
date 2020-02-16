package com.yfjc.workdayhourform;

import java.util.Date;

import javax.swing.SwingUtilities;

import com.teamcenter.rac.aif.AbstractAIFCommand;
import com.teamcenter.rac.kernel.TCComponentTaskTemplate;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.Registry;
import com.yfjcebp.extsupport.JCI6YFJCUtil;

public class WorkdayHourFormCommand extends AbstractAIFCommand {

	private TCComponentTaskTemplate template;
	private TCSession session;
	private Registry reg = Registry.getRegistry(this);

	public WorkdayHourFormCommand(TCSession session) {
		this.session = session;
	}

	public void execute() throws Exception {
		// tyl 2015/01/12 ������ʱʱ�� ( ���������ղ�����������¹�ʱ)
		Date now = new Date();
		int currentDate = now.getDate();
		String preferenceName = "YFJC_LCC_OverDue_Day";
		TCPreferenceService tcpreferenceservice = session.getPreferenceService();
		String value = tcpreferenceservice.getString(TCPreferenceService.TC_preference_all, preferenceName);
		System.out.println("value============="+value);
		if ((value == null) || (value.trim().length() == 0)) {
			MessageBox.post("��������ѡ�" + preferenceName, "WARNING", MessageBox.WARNING);
			return;
		}
		int overDueDate = 32;
		try {
			overDueDate = Integer.valueOf(value);
		} catch (Exception e1) {
			MessageBox.post("��ѡ��[" + preferenceName + "]ֵ����������������ȷ��ֵ��", "WARNING", MessageBox.WARNING);
			e1.printStackTrace();
			return;
		}
		if (currentDate > overDueDate) {
			MessageBox.post("�������ѹ�", "WARNING", MessageBox.WARNING);
			return;
		}
		// tyl 2015/01/12 ������ʱʱ�� ( ���������ղ�����������¹�ʱ)

		String templateName = reg.getString("ExtHourReviewProcess");
		System.out.println("----------->" + templateName);
		try {
			template = JCI6YFJCUtil.findTaskTemplate(session, templateName);
			if (template == null) {
				MessageBox.post("δ�ҵ�����ģ��" + templateName, "WARNING", MessageBox.WARNING);
				return;
			}
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					SelectDivisionDialog divisionDialog = new SelectDivisionDialog(session, template);
					try {
						divisionDialog.init();
					} catch (TCException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			});

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
