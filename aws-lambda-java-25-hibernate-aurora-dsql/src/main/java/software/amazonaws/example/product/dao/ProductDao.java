package software.amazonaws.example.product.dao;


import java.util.Optional;

import org.hibernate.SessionFactory;
import software.amazonaws.example.product.entity.Product;

public class ProductDao {
		
	private static final SessionFactory sessionFactory= HibernateUtils.getSessionFactory();
	
	/**
	 * create a product and return its id
	 * 
	 * @param product product
	 * @return product id
	 */
	public int createProduct(Product product) throws Exception {
	  var session= sessionFactory.openSession();	
	  var transaction =  session.beginTransaction();
	  session.persist(product);
	  transaction.commit();
      return product.getId(); 
	}
	


	/**
	 * returns product by its id 
	 * 
	 * @param id -product id
	 * @return 
	 * @throws Exception
	 */
	public Optional<Product> getProductById(int id) throws Exception {
		var session= sessionFactory.openSession();	
		return Optional.ofNullable(session.find(Product.class, id));
	}
}