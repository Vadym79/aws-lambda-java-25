package software.amazonaws.example.product.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
//import java.time.Duration;
import java.util.Properties;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;


public class DsqlDataSourceConfig {
	
	private static Connection jdbConnection=null; 
	 
	private static final String AURORA_DSQL_CLUSTER_ENDPOINT = System.getenv("AURORA_DSQL_CLUSTER_ENDPOINT");
			
	private static final String JDBC_URL = "jdbc:aws-dsql:postgresql://"
			+ AURORA_DSQL_CLUSTER_ENDPOINT
			+ ":5432/postgres?sslmode=verify-full&sslfactory=org.postgresql.ssl.DefaultJavaSSLFactory"
			+ "&token-duration-secs=900";

	
	private static HikariDataSource hds=initHikariDataSource();
	
	
	private static HikariDataSource initHikariDataSource() {
		var config = new HikariConfig();
		
		config.setUsername("admin");
		System.out.println("JDCB-URL: "+JDBC_URL);
		config.setJdbcUrl(JDBC_URL);
		config.setMaxLifetime(1500 * 1000); // pool connection expiration time in milli seconds, default 30
		config.setMaximumPoolSize(1); // default is 10
        try {
        	
        	
        	/*
        	hds.setUsername("admin");
    		
    		hds.setJdbcUrl(JDBC_URL);
    		hds.setMaxLifetime(1500 * 1000); // pool connection expiration time in milli seconds, default 30
    		hds.setMaximumPoolSize(1); // default is 10
    		hds.setMinimumIdle(1);´
    		*/
        	HikariDataSource hds= new HikariDataSource(config);
    		System.out.println("url: "+hds.getJdbcUrl());
    		System.out.println("ds: "+hds.getDataSource());
    		System.out.println("ds class name: "+hds.getDataSourceClassName());
        	
    		return hds;
		    
        } catch (Exception ex) {
        	ex.printStackTrace();
        	System.out.println("error message : "+ex.getMessage());
        	throw ex;
        }
	}
		
	/**
	 * creates jdbc connection backed by Hikari data source pool
	 * 
	 * @return jdbc connection backed by Hikari data source pool
	 * @throws SQLException
	 */
	public static Connection getPooledConnection() throws SQLException {
		// Use generateDbConnectAuthToken when connecting as `admin` user
		return hds.getConnection();
	}


	/** creates a new jdbc connection
	 * 
	 * @return new jdbc connection 
	 * @throws SQLException
	 */
	public static Connection getJDBCConnection() throws SQLException {
		long startTime = System.currentTimeMillis();
		if (jdbConnection == null || jdbConnection.isClosed()) {
			var props = new Properties();
			props.setProperty("user", "admin");
			jdbConnection = DriverManager.getConnection(JDBC_URL, props);
		}
		var endTime=System.currentTimeMillis();
		System.out.println("time to create jdbc connection in ms "+(endTime-startTime)); 
		return jdbConnection;
	}

}