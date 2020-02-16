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

//根据 用户/路径/数据集名 的模式 获取数据集并下载到本地temp目录的工具类
public class GetExlDataset {
	private static TCComponentFolder efolder;//模板所在的文件夹
	private static int k=2;//用于记录文件夹层级
	
	
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
	 * 从服务器上下载指定名字的数据集所引用的文件
	 * 
	 * @param session
	 *            TCSession对象
	 * @param dataset
	 *            数据集名字
	 * @param directory
	 *            数据集要下载到的本地目录
	 * 
	 * @return 数据集所引用的文件
	 * 
	 * @throws TCException
	 *             操作失败时抛出异常
	 */
	public static File downloadFileFromServer(TCSession session, TCComponentDataset dataset, String directory) throws TCException
	{
		File file = null;
		try
		{
			if (dataset != null)
			{
				// 得到数据集的最新版本
				dataset = dataset.latest();
				TCComponentTcFile[] tcfiles = dataset.getTcFiles();

				// 判断数据集是否存在命名的引用
				if (tcfiles != null && tcfiles.length > 0)
				{
					// 如果存在,取其第一个命名的引用
					TCComponentTcFile tcFile = tcfiles[0];
					// 判断下载数据集的本地目录是否存在
					File dir = new File(directory);
					if (dir.exists() || dir.mkdirs())
					{
						// 将指定的命名引用下载到本地指定目录
						file = tcFile.getFile(directory);
					}
					else
					{
						throw new TCException("创建下载目录  " + directory + " 失败");
					}
				}
				else
				{
					throw new TCException("数据集 " + dataset + "中没有命名引用");
				}
			}
			else
			{
				throw new TCException("没有找到名称为 " + dataset + "的数据集");
			}
		}
		catch (TCException e)
		{
			throw new TCException("从服务器上下载文件发生错误: \n" + e.getMessage());
		}
		return file;
	}
}
