package test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


public class Test {

	public static void main(String[] args) {
		
		
		System.out.println("执行main函数");
		/*OracleBeen oracleBeen = new OracleBeen();
		//                 接口名称                                                   接口唯一识别ID              接口传输方                                               接口接收方                                                  接口传送时间                                                    接口完成结果                                                  报错时间                                                   报错类型                                                    报错信息                                                       处理结果标识
		//create table 表名   (接口名称     VARCHAR2(30),接口唯一识别ID VARCHAR2(30),接口传输方   VARCHAR2(30),接口接收方    VARCHAR2(30),接口传送时间     VARCHAR2(30),接口完成结果     VARCHAR2(30),报错时间     VARCHAR2(30),报错类型     VARCHAR2(30),报错信息VARCHAR2  (130),处理结果标识   VARCHAR2(30))
		LogBeen log = oracleBeen.operation_database("select * from aaa where a = ?", new String[]{"a"});
		if(log!=null){
			System.out.println("接口传输方"+log.getInterface_transporter());
		}*/
		
		//File file = new File("d:\\我的文档\\桌面\\项目\\博世华域项目\\【博世华域TCM项目】开发计划V1.0_20181030(1).xlsx");
		
		try {
//			savePic(new FileInputStream(file), file.getName());
			
//			File file2 = new File("d:\\我的文档\\桌面\\哎哎哎.xlsx");
//			FileInputStream fileInputStream = new FileInputStream(file2);
//			XSSFWorkbook xssfWorkbook = new XSSFWorkbook(fileInputStream);
//			fileInputStream.close();
//			Sheet sheet = xssfWorkbook.getSheet("aaa");
//			
//			System.out.println(sheet);
//			Row row = sheet.getRow(6);
//			Cell cell = row.getCell(4);
//			cell.setCellValue("aaa");
//			FileOutputStream fileOutputStream = new FileOutputStream(file2);
//			xssfWorkbook.write(fileOutputStream);
//			fileOutputStream.flush();
//			fileOutputStream.close();
			/*String path="d:\\我的文档\\桌面\\test.xlsx";
	        
			XSSFWorkbook wb = new XSSFWorkbook();
	        Sheet sheet = wb.createSheet("第一个sheet页");
	        
	        
	        FileOutputStream output = new FileOutputStream(path);
	        wb.write(output);
	        output.flush();
	        output.close();
	        File file2 = new File(path);*/
	        
	        
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void aaa(File file2) {
		// TODO Auto-generated method stub
		file2.delete();
		
	}

	public static  void savePic(InputStream inputStream, String fileName) {

        OutputStream os = null;
        try {
            String path = "c:\\";
            // 2、保存到临时文件
            // 1K的数据缓冲
            byte[] bs = new byte[1024];
            // 读取到的数据长度
            int len;
            // 输出的文件流保存到本地文件

            File tempFile = new File(path);
            if (!tempFile.exists()) {
                tempFile.mkdirs();
            }
            os = new FileOutputStream(tempFile.getPath() +fileName);
            // 开始读取
            while ((len = inputStream.read(bs)) != -1) {
                os.write(bs, 0, len);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 完毕，关闭所有链接
            try {
                os.close();
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
