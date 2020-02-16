//package com.yfjcebp.projectmanager.budget.dialogs;
//
//
//import java.io.IOException;
//
//import com.jacob.activeX.ActiveXComponent;
//import com.jacob.com.Dispatch;
//import com.jacob.com.Variant;
//import com.teamcenter.rac.kernel.TCComponentItemRevision;
//import com.teamcenter.rac.kernel.TCSession;
//
//public class Test2 {
//	private TCSession session;
//	private TCComponentItemRevision itemRev;
//	private String gebt_version;
//	private String coc_all;
//	private String coc_feature;
//	private double coc_feature_val = 3;
//	private Dispatch sheetsAll = null;
//	private JacobEReportTool tool;
//	private double val;
//
//	/*public static void main(String[] args) {
//		Test2 soc = new Test2();
//		soc.coc_feature = "jci6_coc_feature_V3.4.9=Content{.}[(AK-9)-(AP-8)+(AO-9)]";
//		soc.getCocFeature();
//		
//		System.out.println("濞撴皜鍡氭珪閸ㄦ岸宕�--->"+soc.val);
//		
//		soc.val = 0;
//		soc.coc_feature = "jci6_coc_feature_V3.4.9=Content{.}[(AK-9),(jci6_coc_feature),(AN-8)]";
//		soc.getCocFeature();
//		System.out.println("濞撴皜鍡氭珪閸ㄦ岸宕�--->"+soc.val);
//		
//		DecimalFormat df2  = new DecimalFormat("###.000");  
//		System.out.println(df2.format(1.45445));
//		System.out.println(df2.format(2.45450));
//		System.out.println((double)Math.round(2.45456*1000)/1000);
//
//	}*/
//	public static void main(String[] args) {
//		String path = "C:\\Users\\Administrator\\AppData\\Local\\Temp\\GEBT.xlsm";
//		System.out.println(System.getProperty("java.library.path"));
//		//demo1();
//		JacobEReportTool tool = new JacobEReportTool();
//		try {
//			tool.addDir("D:\\java\\jdk1.7\\bin");
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
////		String TC_pathss = System.getenv("TPR");
////        String TC_path = "E:\\Siemens\\Teamcenter11\\tccs\\bin";
////        try {
////            tool.addDir(TC_path );
////        } catch (IOException e) {
////            // TODO Auto-generated catch block
////            e.printStackTrace();
////        }
//        Dispatch sheetsAll = tool.getSheets(path);
//        
//        Dispatch sheet = Dispatch.invoke(sheetsAll, 
//                "Item", 
//                Dispatch.Get, 
//                new Object[] { new Integer(1) }, 
//                new int[1]).toDispatch(); 
//        String sheetName=Dispatch.get(sheet,"Name").toString();//闂佸吋鍎抽崲鑼閻eet闂佹眹鍔岀�顒勫箖閺囩姵鍐婚幏锟�
//        sheet = tool.openExcelFile(sheetsAll, sheetName);
//        //Instructions
//        String data_key_1 = tool.getDataFromExcel("A", 1, sheet);
//        System.out.println("data_key_1-->"+data_key_1);
//        tool.closeExcelFile(false);
//	}
//	
//	private static void demo1 (){
//		String path = "C:\\Users\\Administrator\\AppData\\Local\\Temp\\GEBT.xlsm";
//		ActiveXComponent activexcomponent = new ActiveXComponent("Excel.Application");
//		activexcomponent.setProperty("Visible", new Variant(false));
//		Dispatch workbooks = activexcomponent.getProperty("Workbooks").toDispatch();
//		Dispatch workbook = Dispatch
//				.invoke(workbooks,
//						"Open",
//						Dispatch.Method,
//						new Object[] { path, new Variant(false),
//								new Variant(false) },
//						new int[1]).toDispatch();
//		Dispatch sheets = Dispatch.get(workbook, "Sheets")
//				.toDispatch();
//		
//	
//		Dispatch.call(workbook, "Close", new Variant(false));
//	}
//
//	// 闁告帒妫楁竟濠勶拷娑欘殘濞戯拷
//	private void splitString(String coc) {
//		String[] s = coc.split(":");
//		if (s.length == 2) {
//			String[] p_sheet_cols = s[1].split("\\|");
//			for (int i = 0; i < p_sheet_cols.length; i++) {
//				String[] p_sheet = p_sheet_cols[i].split("=");
//				if (p_sheet.length == 2) {
//					System.out.println("---->" + p_sheet[0]);
//					System.out.println("---->" + p_sheet[1]);
//					// 鐎电増顨呴崺宀�沪閻愬洷鍐╂償閺冨倹鐣眘heet濡烇拷
//					// p_sheet[1].split("")
//				}
//			}
//		}
//	}
//
//	// 鐎电増顨呴崺瀹ヽi6_coc_feature闁汇劌瀚�
//	private void getCocFeature() {
//		if (coc_feature != null) {
//			String[] p_sheet_col = coc_feature.split("=");
//			if (p_sheet_col.length == 2) {
//				String coc_feature_sheet = p_sheet_col[1].substring(0,
//						p_sheet_col[1].indexOf("{"));
//				String cells = p_sheet_col[1].substring(
//						p_sheet_col[1].lastIndexOf("}") + 2,
//						p_sheet_col[1].length() - 1);
//				// Dispatch sheet = tool.openExcelFile(sheetsAll,
//				// coc_feature_sheet);
//				System.out.println("jci6_coc_feature" + cells);
//				// Dispatch cclbSheet =
//				getMData(tool, null, cells, true);
//				System.out.println("val--->"+val);
//				System.out.println("coc_feature--->" + coc_feature_val);
//			}
//		}
//	}
//
//	// jci6_coc_feature_V3.4.9=Content{.}[(C-330)~(C-347),(C-518)~(C-535),(C-127)~(C-158)]
//
//	// 鐎电増顨呴崺宀勫箰閸パ呮毎濠㈣埖鐭柌婊堝础閺囩偛甯楅柡宥堝亹濞堟垿宕�
//	private void getMData(JacobEReportTool tool, Dispatch sheet, String cells, boolean isadd) {
//		if (cells.contains(",")) {
//			String[] qjstr = cells.split(",");
//			for (int i = 0; i < qjstr.length; i++) {
//				getMData(tool, sheet, qjstr[i], true);
//			}
//		} else if (cells.contains("+")) {
//			String[] qjstr = cells.split("\\+");
//			for (int i = 0; i < qjstr.length; i++) {
//				getMData(tool, sheet, qjstr[i], true);
//			}
//		}else if (cells.contains(")-")) {
//			int index = cells.indexOf(")-");
//			System.out.println(cells.indexOf(")-"));
//			String[] cell = cells.substring(1,index).split("-");
//			System.out.println(cell[0]+Integer.valueOf(cell[1]));
//			if(isadd){	
//				val = val + Double.valueOf(getData(tool, sheet, cell[0], Integer.valueOf(cell[1])));
//			}else{
//				val = val - Double.valueOf(getData(tool, sheet, cell[0], Integer.valueOf(cell[1])));
//			}
//			getMData(tool, sheet,cells.substring(index+2),false);
//		} else if (cells.contains("~")) {
//			String[] qjstr = cells.split("~");
//			String end_str = qjstr[1];
//			String tmp_str = qjstr[0];
//			while (!tmp_str.equals(end_str)) {
//				getMData(tool, sheet, tmp_str, true);
//				String[] s = tmp_str.split("-");
//				tmp_str = s[0]
//						+ "-"
//						+ (Integer
//								.valueOf(s[1].substring(0, s[1].length() - 1)) + 1)
//						+ ")";
//			}
//		} else if (cells.equals("(jci6_coc_feature)")) {
//			if (isadd) {
//				val = val + coc_feature_val;
//			} else {
//				val = val - coc_feature_val;
//			}
//		} else {
//			if (cells.contains("(jci6_coc_feature)")
//					&& cells.endsWith("(jci6_coc_feature)")) {
//				int index = cells.indexOf("(jci6_coc_feature)");
//				System.out.println("闁告繂鐗嗛幖閬嶅传" + index);
//				System.out.println("-->"+cells.substring(index - 1, index)+"<--");
//				System.out.println(" ---->coc_feature_val---now---->"+coc_feature_val);
//				if (cells.substring(index - 1, index).contains("+")) {
//					System.out.println("hahah+");
//					val = val + coc_feature_val;
//				} else if (cells.substring(index - 1, index).contains("闁炽儲鏌�)) {
//					System.out.println("hahah闁炽儲鏌�);
//					val = val - coc_feature_val;
//				}else{
//					System.out.println("najsn");
//				}
//				getMData(tool, sheet, cells.substring(0, index - 1), isadd);
//			} else {
//				System.out.println("cells---->" + cells);
//				String s = cells.substring(1, cells.lastIndexOf(")"));
//				String[] row_col = s.split("\\-");
//				System.out.println("闁革拷----闁碉拷"
//						+ getData(tool, sheet, row_col[0],
//								Integer.valueOf(row_col[1])));
//				if (isadd) {
//					val = val
//							+ Double.valueOf(getData(tool, sheet, row_col[0],
//									Integer.valueOf(row_col[1])));
//				} else {
//					val = val
//							- Double.valueOf(getData(tool, sheet, row_col[0],
//									Integer.valueOf(row_col[1])));
//				}
//			}
//		}
//	}
//
//	// 闁哄秷顫夊畵浣癸純閺嶎厼顦甸妴宥夋儍閸曨偆鎽熺紒妤嬬細鐟曞棝骞嬭ぐ锟�闁革负鍔忚闁哄浜皒cel濞戞挻鐣遍柡浣哄瀹撲線鏁嶅畝鍐矗閺夊墡cel濞戞挸鐦归悗瑙勬皑濞堟垿寮悧鍫濈ウ
//	private String getData(JacobEReportTool tool, Dispatch sheet, String col,
//			int row) {
//		String value = "";
//		try {
//			//value = tool.getDataFromExcel(col, row, sheet);
//			// tool.closeExcelFile(false);
//			value = String.valueOf(row);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return value;
//	}
//}
