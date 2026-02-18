package software.amazonaws.example.product.handler;

/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */

import java.util.Map;

import org.hibernate.service.Service;
import org.hibernate.service.spi.ServiceInitiator;
import org.hibernate.service.spi.ServiceRegistryImplementor;


/**
 * Contract for an initiator of services that target the standard {@link org.hibernate.service.ServiceRegistry}.
 *
 * @param <R> The type of the service initiated.
 *
 * @author Steve Ebersole
 */
public interface StandardServiceInitiator<R extends Service> extends ServiceInitiator<R> {
	/**
	 * Initiates the managed service.
	 *
	 * @param configurationValues The configuration values in effect
	 * @param registry The service registry.  Can be used to locate services needed to fulfill initiation.
	 *
	 * @return The initiated service.
	 */
	 R initiateService(Map<String, Object> configurationValues, ServiceRegistryImplementor registry);
}
