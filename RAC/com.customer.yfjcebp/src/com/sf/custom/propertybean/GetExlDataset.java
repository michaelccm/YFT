package com.sf.custom.propertybean;

import java.io.File;

import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCComponentTcFile;
import com.teamcenter.rac.kernel.TCComponentUser;
import com.teamcenter.rac.kernel.TCComponentUserType;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;

//���� �û�/·��/���ݼ��� ��ģʽ ��ȡ���ݼ������ص�����tempĿ¼�Ĺ�����
public class GetExlDataset {
	private static TCComponentFolder efolder;//ģ�����ڵ��ļ���
	private static int k=2;//���ڼ�¼�ļ��в㼶
	
	
	public static TCComponentDataset getDataset(TCSession session,String preferenceName) throws TCException, Exception {
		TCComponentDataset excel=null;
		String datasetPath = GetPreference.GetVaiue(session, "N9_UPG_NUM");
		String[] split = datasetPath.split("/");
		int length = split.length;
		TCComponentUserType typeComponent = (TCComponentUserType) session.getTypeComponent("User");
		TCComponentUser User = typeComponent.find(split[0]);
		TCComponentFolder home = User.getHomeFolder();
		//System.out.println(home.getType());
		findFolder(home,split,length);
		AIFComponentContext[] children = efolder.getChildren();
		for(int i=0;i<children.length;i++){
			String filename = children[i].getComponent().getProperty("object_name");
			if(filename.equals(split[length-1])){
				excel = (TCComponentDataset) children[i].getComponent();
			}
		}
		return excel;
	}
	
	private static TCComponentFolder findFolder(TCComponentFolder folder, String[] split, int length) throws TCException {
		AIFComponentContext[] children = folder.getChildren();
		for(int i=0;i<children.length;i++){
			String folderName = children[i].toString();
			if(k<length){
				if(split[k].equals(folderName)){
					k++;
					if(k==length){
						break;
					}
					efolder = (TCComponentFolder)children[i].getComponent();
					findFolder(efolder,split,length);
				}
			}
		}
		return efolder;
	}
	
	/**
	 * �ӷ�����������ָ�����ֵ����ݼ������õ��ļ�
	 * 
	 * @param session
	 *            TCSession����
	 * @param dataset
	 *            ���ݼ�����
	 * @param directory
	 *            ���ݼ�Ҫ���ص��ı���Ŀ¼
	 * 
	 * @return ���ݼ������õ��ļ�
	 * 
	 * @throws TCException
	 *             ����ʧ��ʱ�׳��쳣
	 */
	public static File downloadFileFromServer(TCSession session, TCComponentDataset dataset, String directory) throws TCException
	{
		File file = null;
		try
		{
			if (dataset != null)
			{
				// �õ����ݼ������°汾
				dataset = dataset.latest();
				TCComponentTcFile[] tcfiles = dataset.getTcFiles();

				// �ж����ݼ��Ƿ��������������
				if (tcfiles != null && tcfiles.length > 0)
				{
					// �������,ȡ���һ������������
					TCComponentTcFile tcFile = tcfiles[0];
					// �ж��������ݼ��ı���Ŀ¼�Ƿ����
					File dir = new File(directory);
					if (dir.exists() || dir.mkdirs())
					{
						// ��ָ���������������ص�����ָ��Ŀ¼
						file = tcFile.getFile(directory);
					}
					else
					{
						throw new TCException("��������Ŀ¼  " + directory + " ʧ��");
					}
				}
				else
				{
					throw new TCException("���ݼ� " + dataset + "��û����������");
				}
			}
			else
			{
				throw new TCException("û���ҵ�����Ϊ " + dataset + "�����ݼ�");
			}
		}
		catch (TCException e)
		{
			throw new TCException("�ӷ������������ļ���������: \n" + e.getMessage());
		}
		return file;
	}
}
