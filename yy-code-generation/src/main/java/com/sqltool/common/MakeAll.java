package com.sqltool.common;

import org.springframework.util.StopWatch;

import java.sql.*;

/**
 * @author yangyu
 */

public class MakeAll {

	private static Connection connection;
	private static Statement statement;
	private static ResultSetMetaData metaData;

	private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
	private static final String URL = "jdbc:mysql://localhost:3306/yydb?serverTimezone=UTC&useSSL=false";
	private static final String USERNAME = "root";
	private static final String PASSWORD = "123456";

	private static final String filePath = System.getProperty("user.dir") + "\\src\\main\\java\\";
	private static final String packageName = "com.website";
	private static final String[] TABLES = { "company"};

	static {
		try {
			initConnection(DRIVER, URL, USERNAME, PASSWORD);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {

		StopWatch sw = new StopWatch();
		sw.start("生成文件");
		for (String table : TABLES) {
			metaData = getMetaDataFromTable(table);
			String className = CommonUtils.makeClassName(table);
			new MakeModelUtils(packageName, className, filePath, metaData).makeModelClass();
			new MakeMapperUtils(packageName, className, filePath, metaData).makeMapperClass();
			new MakeServiceUtils(packageName, className, filePath).makeServiceClass();
			new MakeControllerUtils(packageName, className, filePath).makeControllerClass();
		}
		sw.stop();
		System.out.println(sw.prettyPrint());

	}

	/**
	 * @param driverClass 驱动类
	 * @param dbUrl 数据库地址
	 * @param username 用户名
	 * @param password 密码
	 * @throws SQLException 异常
	 * @throws ClassNotFoundException 异常
	 */
	private static void initConnection(String driverClass, String dbUrl, String username, String password) throws SQLException, ClassNotFoundException {
		Class.forName(driverClass);
		connection = DriverManager.getConnection(dbUrl, username, password);
		statement = connection.createStatement();
	}

	private static ResultSetMetaData getMetaDataFromTable(String tableName) throws Exception {
		String sql = ("SELECT * FROM ").concat(tableName).concat(" WHERE 1 != 1");
		ResultSet rs = statement.executeQuery(sql);
		return rs.getMetaData();
	}

}