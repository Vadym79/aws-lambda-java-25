package software.amazonaws.example.product.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;


import software.amazonaws.example.product.entity.Product;

public class ProductDao {
			
	/**
	 * create a product and return its id
	 * 
	 * @param product product
	 * @return product id
	 */
	public int createProduct(Product product) throws Exception {
		try (Connection con = getConnection()) {
			try (PreparedStatement pst = this.createProductPreparedStatement(con, product)) {
				pst.executeUpdate();
			} catch (SQLException ex) {
				con.rollback();
				throw ex;
			}
		}
      return product.id();
	}
	


	/**
	 * returns product by its id 
	 * 
	 * @param id -product id
	 * @return 
	 * @throws Exception
	 */
	public Optional<Product> getProductById(int id) throws Exception {
		try (Connection con = getConnection();
				PreparedStatement pst = this.getProductByIdPreparedStatement(con, id);
				ResultSet rs = pst.executeQuery()) {
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