package com.yfjcebp.reorientation.user.privileges;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

/**
 * 获取数据库连接
 * 
 */
public class SqlUtil {

	/** Oracle数据库连接URL */
	private String DB_URL = "jdbc:oracle:thin:@192.168.2.66:1521:orcl66";

	/** Oracle数据库连接驱动 */
	private String DB_DRIVER = "oracle.jdbc.driver.OracleDriver";

	/** 数据库用户名 */
	private String DB_USERNAME = "cpp";

	/** 数据库密码 */
	private String DB_PASSWORD = "cpp";


	// 构造DBConnection
	public SqlUtil(String DB_URL, String DB_USERNAME, String DB_PASSWORD) {
		this.setDB_URL(DB_URL);
		this.setDB_USERNAME(DB_USERNAME);
		this.setDB_PASSWORD(DB_PASSWORD);
	}

	public SqlUtil() {
	}

	/**
	 * 获取数据库连接
	 * 
	 * @return
	 */
	public Connection getConnection() {
		/** 声明Connection连接对象 */
		Connection conn = null;
		try {
			/** 使用Class.forName()方法自动创建这个驱动程序的实例且自动调用DriverManager来注册它 */
			Class.forName(DB_DRIVER);
			/** 通过DriverManager的getConnection()方法获取数据库连接 */
			conn = DriverManager
					.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return conn;
	}

	/**
	 * 关闭数据库连接
	 * 
	 * @param connect
	 */
	public void closeConnection(Connection conn) {
		try {
			if (conn != null) {
				/** 判断当前连接连接对象如果没有被关闭就调用关闭方法 */
				if (!conn.isClosed()) {
					conn.close();
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	
	/**
	 * 得到Statement
	 */
	public PreparedStatement getPs(Connection conn,Object[] argments,String sql)
			throws SQLException {
		//必须加上ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY,否则会报"对只转发结果集的无效操作: last"
		PreparedStatement ps = conn.prepareStatement(sql,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
		if (argments != null) {
			for (int i = 0; i < argments.length; i++) {
				ps.setObject(i + 1, argments[i]);
			}
		}
		return ps;
	}
	
	/**
	 * 执行SQL语句
	 * @param conn
	 * @param disMap
	 * @throws Exception
	 */
	public void execute(Connection conn,Map<String,String> disMap) throws Exception{
		conn.setAutoCommit(false);
		String sql = "update presourceassignment set rdisciplineu = ? where puid = ? ";
		PreparedStatement prest =conn.prepareStatement(sql,ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
		for(Map.Entry<String, String> entry : disMap.entrySet()){
			prest.setString(1,entry.getValue());
			prest.setString(2,entry.getKey());
			prest.addBatch();
		}
		prest.executeBatch();
		conn.commit();
	}

	/**
	 * 释放资源(传满参数也要全部释放)
	 */
	public void free(ResultSet resultSet, Statement statement,
			Connection connection) {
		try {
			if (resultSet != null) {
				try {
					resultSet.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		} finally {
			try {
				if (statement != null) {
					try {
						statement.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			} finally {
				if (connection != null) {
					try {
						connection.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}



	/**
	 * @return the dB_URL
	 */
	public String getDB_URL() {
		return DB_URL;
	}

	/**
	 * @return the dB_USERNAME
	 */
	public String getDB_USERNAME() {
		return DB_USERNAME;
	}

	/**
	 * @return the dB_PASSWORD
	 */
	public String getDB_PASSWORD() {
		return DB_PASSWORD;
	}

	/**
	 * @param dB_URL
	 *            the dB_URL to set
	 */
	public void setDB_URL(String dB_URL) {
		DB_URL = dB_URL;
	}

	/**
	 * @param dB_USERNAME
	 *            the dB_USERNAME to set
	 */
	public void setDB_USERNAME(String dB_USERNAME) {
		DB_USERNAME = dB_USERNAME;
	}

	/**
	 * @param dB_PASSWORD
	 *            the dB_PASSWORD to set
	 */
	public void setDB_PASSWORD(String dB_PASSWORD) {
		DB_PASSWORD = dB_PASSWORD;
	}

}