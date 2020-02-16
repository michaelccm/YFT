//package com.yfjcebp.change.user.info;
//
//import com.jacob.activeX.ActiveXComponent;
//import com.jacob.com.ComThread;
//import com.jacob.com.Dispatch;
//import com.jacob.com.Variant;
//
///**
// * * @fileName MSWordManager.java * @description �������ڲ���word�ĵ�ָ��λ�ò���ͼƬ���� * @date
// * 2011-10-21 * @time * @author wst
// */
//public class MSWordManager { // word�ĵ�
//	private Dispatch doc;
//	// word���г������
//	private ActiveXComponent word; // ����word�ĵ�����
//	private Dispatch documents; // ѡ���ķ�Χ������
//	private Dispatch selection;
//	public static int instanceSize = 3;// һ���̴߳�ŵ�MSWordManager����
//
//	public MSWordManager(int index) {
//		if (word == null) {
//			word = new ActiveXComponent("Word.Application"); // Ϊtrue��ʾwordӦ�ó����
//			word.setProperty("Visible", new Variant(false));
//		}
//		if (documents == null) {
//			documents = word.getProperty("Documents").toDispatch();
//		}
//		if (index == 0) {
//			ComThread.InitSTA();
//			// ��ʼ��һ���̲߳������ڴ��еȴ�����
//		}
//	}
//
//	/**
//	 * * ��һ���Ѿ����ڵ��ĵ� * @param docPath Ҫ�򿪵��ĵ� * @param key �ı�������ݣ����ݸ�key��ȡ�ı���ǰλ��
//	 * * @date 2011-12-9 * @author wst
//	 */
//	public void openDocumentAndGetSelection(String docPath, String key) {
//		try {
//			closeDocument(); // ���ĵ�
//			doc = Dispatch.call(documents, "Open", docPath).toDispatch(); // shapes����
//			Dispatch shapes = Dispatch.get(doc, "Shapes").toDispatch(); // shape�ĸ���
//			String Count = Dispatch.get(shapes, "Count").toString();
//			for (int i = 1; i <= Integer.parseInt(Count); i++) { // ȡ��һ��shape
//				Dispatch shape = Dispatch.call(shapes, "Item", new Variant(i))
//						.toDispatch(); // ��һ��shape�����ȡ���ı���
//				Dispatch textframe = Dispatch.get(shape, "TextFrame")
//						.toDispatch();
//				boolean hasText = Dispatch.call(textframe, "HasText")
//						.toBoolean();
//				if (hasText) { // ��ȡ���ı������
//					Dispatch TextRange = Dispatch.get(textframe, "TextRange")
//							.toDispatch(); // ��ȡ�ı����е��ַ���
//					String str = Dispatch.get(TextRange, "Text").toString(); // ��ȡָ���ַ�key���ڵ��ı����λ��
//					if (str != null && !str.equals("") && str.indexOf(key) > -1) { // ��ǰ�ı����λ��
//						selection = Dispatch.get(textframe, "TextRange")
//								.toDispatch(); // ����ı�������
//						Dispatch.put(selection, "Text", "");
//						break;
//					}
//				}
//			}
//		} catch (Exception e) {
//			System.out.println(e);
//			return;
//		}
//	}
//
//	/** * �ڵ�ǰλ�ò���ͼƬ * @param imagePath ����ͼƬ��·�� * @return �ɹ���true��ʧ�ܣ�false */
//	public boolean insertImage(String imagePath) {
//		try {
//			Dispatch.call(Dispatch.get(selection, "InLineShapes").toDispatch(),
//					"AddPicture", imagePath);
//		} catch (Exception e) {
//			System.out.println(e);
//			return false;
//		}
//		return true;
//	} 
//	
//	// �ر��ĵ�
//	public void closeDocument() {
//		if (doc != null) {
//			Dispatch.call(doc, "Close");
//			doc = null;
//		}
//	} 
//	// �ر�ȫ��Ӧ��
//	public void close(int index) {
//		if (word != null) {
//			Dispatch.call(word, "Quit");
//			word = null;
//		}
//		selection = null;
//		documents = null;
//		if (index == instanceSize) { // �ͷ�ռ�õ��ڴ�ռ䣬��Ϊcom���̻߳��ղ���java����������������
//			ComThread.Release();
//
//		}
//	}
//}
