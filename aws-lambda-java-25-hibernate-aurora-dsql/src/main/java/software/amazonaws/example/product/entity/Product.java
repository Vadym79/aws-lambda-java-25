
package software.amazonaws.example.product.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import java.io.Serializable;

@Entity
@Table(name = "products")
public class Product implements Serializable {

  @Id	
  private int id;
  private String name;
  private int price;

  private static final long serialVersionUID = -4036966741701017325L;
  
  public Product() {
  }

  public Product(int id, String name, int price) {
    this.id = id;
    this.name = name;
    this.price=price;
  }

  public int getId() {
    return this.id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getPrice() {
    return price;
  }

  public void setPrice(int price) {
    this.price = price;
  }

  @Override
  public String toString() {
    return "Product{" +
      "id='" + this.id + '\'' +
      ", name='" + this.name + '\'' +
      ", price=" + this.price +
      '}';
  }
}
