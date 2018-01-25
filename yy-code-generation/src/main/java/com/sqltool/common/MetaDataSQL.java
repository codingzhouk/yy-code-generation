package com.sqltool.common;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;


/**
 * 
 * @author yangyu
 *
 */

public class MetaDataSQL {


	/**
	 * INSERT
	 * @param metaData
	 * @throws Exception
	 */
	public static String insertSQL(ResultSetMetaData metaData) throws Exception{
		StringBuffer sb = new StringBuffer();
		StringBuffer sbValues = new StringBuffer();
		sb.append("INSERT INTO ");
		sb.append(metaData.getTableName(1)+" (");
		for (int i = 0; i < metaData.getColumnCount(); i++) {
			if(i != 0) {
				sb.append(metaData.getColumnName(i + 1)+",");
				sbValues.append("#{"+CommonUtils.camelCaseName(metaData.getColumnName(i + 1))+"},");
			}
		}
		String sql = sb.deleteCharAt(sb.length()-1).toString()+") VALUES (" + sbValues.deleteCharAt(sbValues.length()-1).toString() + ")";
		return sql;
	}
	
	/**
	 * @param metaData
	 * @throws Exception
	 */
	
	public static String colBuilder(ResultSetMetaData metaData) throws Exception {

		String newSObj = "\t\nStringBuffer colBuilder = new StringBuffer();\n";
		String sbCol = "colBuilder.append(\" " + colStr(metaData) + "\");\n";
		String sql = newSObj.concat(sbCol)
				.concat("colBuilder.append(whereBuilder(example));\n")
				.concat("return colBuilder.toString()");
		return sql;
	}

	/**
	 * 查询分页SQL
	 * @param metaData
	 * @return
	 * @throws Exception
	 */
	public static String colBuilderPage(ResultSetMetaData metaData) throws Exception {
		String newSObj = "\t\nStringBuffer colBuilder = new StringBuffer();\n";
		String sbCol = "colBuilder.append(\" " + colStr(metaData) + "\");\n";
		String sql = newSObj.concat(sbCol)
				.concat("colBuilder.append(whereBuilder(example));\n")
				.concat("colBuilder.append(\" limit \");\n")
				.concat("colBuilder.append((example.getSkip()-1) * example.getLimit());\n")
				.concat("colBuilder.append(\",\");\n")
				.concat("colBuilder.append(example.getLimit());\n")
				.concat("return colBuilder.toString()");
		return sql;
	}

	/**
	 * 查询总数SQL
	 * @param metaData
	 * @return
	 * @throws Exception
	 */
	public static String colBuilderPageCount(ResultSetMetaData metaData) throws Exception {
		String newSObj = "\t\nStringBuffer colBuilder = new StringBuffer();\n";
		String sbCol = "colBuilder.append(\" " + colStrCount(metaData) + "\");\n";
		String sql = newSObj.concat(sbCol)
				.concat("colBuilder.append(whereBuilder(example));\n")
				.concat("return colBuilder.toString()");
		return sql;
	}

	/**
	 * 拼接字段信息
	 * @param metaData
	 * @return
	 * @throws Exception
	 */
	public static String colStr(ResultSetMetaData metaData) throws Exception {
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT ");
		sb.append(fields(metaData));
		sb.append(" FROM ");
		sb.append(metaData.getTableName(1));
		sb.append(" WHERE 1=1 ");
		return sb.toString();
	}

	/**
	 * 拼接总数字段
	 * @param metaData
	 * @return
	 * @throws Exception
	 */
	public static String colStrCount(ResultSetMetaData metaData) throws Exception {
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT COUNT(*) FROM ");
		sb.append(metaData.getTableName(1));
		sb.append(" WHERE 1=1 ");
		return sb.toString();
	}
	
	public static String selectById(ResultSetMetaData metaData) throws Exception {
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT ");
		sb.append(fields(metaData));
		sb.append(" FROM ");
		sb.append(metaData.getTableName(1));
		sb.append(" WHERE id = #{id}");
		return sb.toString();
	}
	
	/**
	 * 字段信息
	 */
	public static String fields(ResultSetMetaData metaData) throws SQLException {
		StringBuffer sb = new StringBuffer();
		for (int i = 1; i < metaData.getColumnCount() + 1; i++) {
			String columnName = metaData.getColumnName(i);
			sb.append(columnName + ",");
		}
		sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}
	
	/**
	 * 动态拼接where条件
	 * SQL where
	 * @param metaData
	 * @throws Exception
	 */
	
	public static String whereBuilder(ResultSetMetaData metaData) throws Exception {
		StringBuffer sbWhere = new StringBuffer();
		
		String newSbObj = "\t\nStringBuffer whereBuilder = new StringBuffer();\n";
		for (int i = 1; i < metaData.getColumnCount()+1; i++) {
			String columnName = metaData.getColumnName(i);

			sbWhere.append("\t\nif (example.get" + CommonUtils.captureName(CommonUtils.camelCaseName(columnName)) + "() != null){\n" 
			+ "\twhereBuilder.append(\" and " + columnName + "=#{"+ CommonUtils.camelCaseName(columnName) + "}\"); \n}\n");
		}
		return newSbObj.concat(sbWhere.toString()).concat("return whereBuilder.toString()");
	}
	
	/**
	 * 动态拼接更新字段
	 * SQL update
	 * @param metaData
	 * @throws Exception
	 */
	
	public static String updateBuilder(ResultSetMetaData metaData) throws Exception {
		StringBuffer sbWhere = new StringBuffer();
		
		String newSbObj = "\t\nStringBuffer updateBuilder = new StringBuffer();\n";
		sbWhere.append("updateBuilder.append(\"UPDATE " + metaData.getTableName(1) + " SET \");");
		for (int i = 1; i < metaData.getColumnCount()+1; i++) {
			String columnName = metaData.getColumnName(i);
			if("id".equals(columnName)) continue;
			sbWhere.append("\t\nif (example.get" + CommonUtils.captureName(CommonUtils.camelCaseName(columnName)) + "() != null){\n" 
					+ "\tupdateBuilder.append(\"" + columnName + "=#{"+ CommonUtils.camelCaseName(columnName) + "}");
			sbWhere.append(", ");
			sbWhere.append("\"); \n}\n");
		}
		sbWhere.append("updateBuilder.deleteCharAt(updateBuilder.lastIndexOf(\",\"));\n");
		sbWhere.append("updateBuilder.append(\" WHERE id = #{id}\");\n");
		return newSbObj.concat(sbWhere.toString()).concat("return updateBuilder.toString()");
	}
	
	/**
	 * @param metaData
	 * @return
	 * @throws Exception
	 */
	public static String deleteById(ResultSetMetaData metaData) throws Exception {
		StringBuffer sb = new StringBuffer();
		sb.append("DELETE FROM ");
		sb.append(metaData.getTableName(1));
		sb.append(" WHERE id = #{id} ");
		return sb.toString();
	}
	
}