package com.yfjcebp.reorientation.user.privileges;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

/**
 * ��ȡ���ݿ�����
 * 
 */
public class SqlUtil {

	/** Oracle���ݿ�����URL */
	private String DB_URL = "jdbc:oracle:thin:@192.168.2.66:1521:orcl66";

	/** Oracle���ݿ��������� */
	private String DB_DRIVER = "oracle.jdbc.driver.OracleDriver";

	/** ���ݿ��û��� */
	private String DB_USERNAME = "cpp";

	/** ���ݿ����� */
	private String DB_PASSWORD = "cpp";


	// ����DBConnection
	public SqlUtil(String DB_URL, String DB_USERNAME, String DB_PASSWORD) {
		this.setDB_URL(DB_URL);
		this.setDB_USERNAME(DB_USERNAME);
		this.setDB_PASSWORD(DB_PASSWORD);
	}

	public SqlUtil() {
	}

	/**
	 * ��ȡ���ݿ�����
	 * 
	 * @return
	 */
	public Connection getConnection() {
		/** ����Connection���Ӷ��� */
		Connection conn = null;
		try {
			/** ʹ��Class.forName()�����Զ�����������������ʵ�����Զ�����DriverManager��ע���� */
			Class.forName(DB_DRIVER);
			/** ͨ��DriverManager��getConnection()������ȡ���ݿ����� */
			conn = DriverManager
					.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return conn;
	}

	/**
	 * �ر����ݿ�����
	 * 
	 * @param connect
	 */
	public void closeConnection(Connection conn) {
		try {
			if (conn != null) {
				/** �жϵ�ǰ�������Ӷ������û�б��رվ͵��ùرշ��� */
				if (!conn.isClosed()) {
					conn.close();
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	
	/**
	 * �õ�Statement
	 */
	public PreparedStatement getPs(Connection conn,Object[] argments,String sql)
			throws SQLException {
		//�������ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY,����ᱨ"��ֻת�����������Ч����: last"
		PreparedStatement ps = conn.prepareStatement(sql,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
		if (argments != null) {
			for (int i = 0; i < argments.length; i++) {
				ps.setObject(i + 1, argments[i]);
			}
		}
		return ps;
	}
	
	/**
	 * ִ��SQL���
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
	 * �ͷ���Դ(��������ҲҪȫ���ͷ�)
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