package software.amazonaws.example.product.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;


import software.amazonaws.example.product.entity.Product;

public class ProductDao {
		
	//@SuppressWarnings("unused")
	//private static final DsqlDataSourceConfig dsqlDataSourceConfig=new DsqlDataSourceConfig();
	
	/**
	 * create a product and return its id
	 * 
	 * @param product product
	 * @return product id
	 */
	public int createProduct(Product product) throws Exception {
		 Product productToCreate=product;
		 try (Connection con = getConnection()) {
			if(product.id()==0) {
				try (var pst1 = this.generateProductId(con);
				     var rs = pst1.executeQuery()) {
				if (rs.next()) {
					var id = rs.getInt("nextval");
					productToCreate=new Product(id, product.name(), product.price());
			     }
		       }
			}
			
			try (var pst2 = this.createProductPreparedStatement(con, productToCreate)) {
				pst2.executeUpdate();
			} catch (SQLException ex) {
				con.rollback();
				throw ex;
			}
		}
      return productToCreate.id();
	}
	


	/**
	 * returns product by its id 
	 * 
	 * @param id -product id
	 * @return 
	 * @throws Exception
	 */
	public Optional<Product> getProductById(int id) throws Exception {
		try (var con = getConnection();
				var pst = this.getProductByIdPreparedStatement(con, id);
				var rs = pst.executeQuery()) {
			if (rs.next()) {
				var name = rs.getString("name");
				int price = rs.getInt("price");
				var product = new Product(id, name, price);
				return Optional.of(product);
			} else {
				return Optional.empty();
			}
		}

	}
	
	
	private PreparedStatement createProductPreparedStatement(Connection con, Product product) throws SQLException {
		var pst = con.prepareStatement("INSERT INTO products VALUES (?, ?, ?) ");		
		pst.setInt(1, product.id());
		pst.setString(2, product.name());
		pst.setInt(3, product.price());
		return pst;
	}


	private PreparedStatement generateProductId(Connection con) throws SQLException {
		PreparedStatement pst = con.prepareStatement("SELECT nextval('product_id')");
		return pst;
	}
	
	private PreparedStatement getProductByIdPreparedStatement(Connection con, int id) throws SQLException {
		PreparedStatement pst = con.prepareStatement("SELECT * FROM products WHERE id = ?");
		pst.setInt(1, id);
		return pst;
	}
	
	private static final Connection getConnection() throws SQLException {
		 return DsqlDataSourceConfig.getPooledConnection();
		//return DsqlDataSourceConfig.getJDBCConnection();
	}
}