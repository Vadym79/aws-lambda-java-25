package software.amazonaws.example.product.dao;

import java.util.Properties;

import software.amazonaws.example.product.entity.Product;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;


public final class HibernateUtils {
		 
	private static final String AURORA_DSQL_CLUSTER_ENDPOINT = System.getenv("AURORA_DSQL_CLUSTER_ENDPOINT");
			
	private static final String JDBC_URL = "jdbc:aws-dsql:postgresql://"
			+ AURORA_DSQL_CLUSTER_ENDPOINT
			+ ":5432/postgres?sslmode=verify-full&sslfactory=org.postgresql.ssl.DefaultJavaSSLFactory"
			+ "&token-duration-secs=900";

	
	private static SessionFactory sessionFactory= getHibernateSessionFactory();
	
	private HibernateUtils () {
		
	}
	
	private static SessionFactory getHibernateSessionFactory () {			
		var settings = new Properties();
		settings.put("jakarta.persistence.jdbc.user", "admin");
		settings.put("jakarta.persistence.jdbc.url", JDBC_URL);
		settings.put("hibernate.connection.pool_size", 1);
		settings.put(Environment.CONNECTION_PROVIDER, "org.hibernate.hikaricp.internal.HikariCPConnectionProvider");
		settings.put("hibernate.hikari.maxLifetime", 1500 * 1000);

		return new Configuration()
				 .setProperties(settings)
				 .addAnnotatedClass(Product.class)
				 //.addPackage("software.amazonaws.example.product.entity")
				 .buildSessionFactory();
		
	}
	
	/** returns hibernate session factory
	 * 
	 * @return hibernate session factory
	 */
	public static SessionFactory getSessionFactory() {
		return sessionFactory;
	}
	
		
}