package com.yfjc.workdayhourform;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

public class Test {

	public static void main(String[] args) {
		Map<String,Integer> projectM = new TreeMap<String,Integer>();
		//排序
		projectM.put("1234", 0);
		projectM.put("test003", 0);
		projectM.put("Test001", 0);
		projectM.put("r21s", 0);
		projectM.put("r21A", 0);
		projectM.put("r01s", 0);
		projectM.put("abs01", 0);
		projectM.put("Test002", 0);
		projectM.put("abs0aq", 0);
		Object[] key = projectM.keySet().toArray(); 
    	Arrays.sort(key);
    	for(Map.Entry<String,Integer> entry : projectM.entrySet()){
    		//System.out.println(entry.getKey());
    	}
    	
    	
    	//System.out.println("================================");
    	
    	
    	
    	 Map<String, Integer> resultMap = sortMapByKey(projectM);    //按Key进行排序  
         for (Map.Entry<String, Integer> entry : resultMap.entrySet()) {  
             //System.out.println(entry.getKey() + " " + entry.getValue());  
         }  
	}
	
    /** 
     * 使用 Map按key进行排序 
     * @param map 
     * @return 
     */  
    public static Map<String, Integer> sortMapByKey(Map<String, Integer> map) {  
        if (map == null || map.isEmpty()) {  
            return null;  
        }  
        Map<String, Integer> sortMap = new TreeMap<String, Integer>(new MapKeyComparator());  
        sortMap.putAll(map);  
        return sortMap;  
    } 
}

//比较器类  
class MapKeyComparator implements Comparator<String>{  
  public int compare(String str1, String str2) {  
      return str1.compareTo(str2);  
  }  
}  
