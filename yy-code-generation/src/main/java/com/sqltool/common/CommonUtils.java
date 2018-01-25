package com.sqltool.common;

import java.math.BigDecimal;
import java.util.Date;

public class CommonUtils {
    /** 
     * 驼峰
     * @param underscoreName 
     * @return 
     */  
    public static String camelCaseName(String underscoreName) {  
        StringBuilder result = new StringBuilder();  
        if (underscoreName != null && underscoreName.length() > 0) {  
            boolean flag = false;  
            for (int i = 0; i < underscoreName.length(); i++) {  
                char ch = underscoreName.charAt(i);  
                if ("_".charAt(0) == ch) {  
                    flag = true;  
                } else {  
                    if (flag) {  
                        result.append(Character.toUpperCase(ch));  
                        flag = false;  
                    } else {  
                        result.append(ch);  
                    }  
                }  
            }  
        }  
        return result.toString();  
    }  
    
    //首字母大写
	public static String captureName(String name) {
		char[] cs = name.toCharArray();
		cs[0] -= 32;
		return String.valueOf(cs);
	}
	//首字母小写
	public static String lowerName(String name) {
		char[] cs = name.toCharArray();
		cs[0] += 32;
		return String.valueOf(cs);
	}
    
    public static String makeSetMechod(String pros) {
    	return "set"+captureName(camelCaseName(pros));
    }
    public static String makeGetMechod(String pros) {
    	return "get"+captureName(camelCaseName(pros));
    }
    
    public static String makeClassName(String className) {
    	return captureName(camelCaseName(className));
    }
    
    /**
     * 数据库类型转成java类型
     * @param type
     * @return
     */
	public static Object changeType(String type) {

		if ("DATE".equals(type) || "DATETIME".equals(type) || "TIMESTAMP".equals(type)) {
			return new Date();
		}
		if ("TINYINT".equals(type) || "SMALLINT".equals(type) || "INT".equals(type)) {
			return new Integer(0);
		}
		if ("BIGINT".equals(type) || "BIGINT UNSIGNED".equals(type)) {
			return new Long(0);
		}
		if ("FLOAT".equals(type)) {
			return new Float(0.00);
		}
		if ("DOUBLE".equals(type)) {
			return new Double(0.00);
		}
		if ("DECIMAL".equals(type) || "NUMERIC".equals(type)) {
			return new BigDecimal(0);
		}
		if ("BIT".equals(type) || "BOOLEAN".equals(type)) {
			return new Boolean(false);
		}
		if ("CHAR".equals(type) || "VARCHAR".equals(type) || "TEXT".equals(type)) {
			return new String();
		}
		throw new RuntimeException("未知数据类型 = " + type);
	}
    
    public static void main(String[] args) {
    	System.out.println (lowerName("Kame"));;
	}
}
