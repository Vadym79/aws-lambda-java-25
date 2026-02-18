package software.amazonaws.example.product.handler;

import org.hibernate.boot.registry.BootstrapServiceRegistryBuilder;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;

/**
 * Provides the most basic services such as class loading. Other
 * configuration-time objects such as {@link org.hibernate.boot.MetadataSources},
 * {@link StandardServiceRegistryBuilder}, and {@link org.hibernate.cfg.Configuration}
 * all depend on an instance of {@code BootstrapServiceRegistry}.
 * <p>
 * An instance may be obtained using {@link BootstrapServiceRegistryBuilder#build()}.
 * <p>
 * Specialized from {@link ServiceRegistry} mainly for type safety.
 *
 * @see BootstrapServiceRegistryBuilder
 *
 * @author Steve Ebersole
 */
public interface BootstrapServiceRegistry extends ServiceRegistry {
}
