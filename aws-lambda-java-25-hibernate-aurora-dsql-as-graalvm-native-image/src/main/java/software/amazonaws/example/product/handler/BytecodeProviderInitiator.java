package software.amazonaws.example.product.handler;

/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */

import java.util.Map;
import java.util.ServiceLoader;

import org.hibernate.Internal;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.bytecode.spi.BytecodeProvider;
import org.hibernate.service.spi.ServiceRegistryImplementor;

public final class BytecodeProviderInitiator implements StandardServiceInitiator<BytecodeProvider> {

	/**
	 * @deprecated Register a {@link BytecodeProvider} through Java {@linkplain java.util.ServiceLoader services}.
	 */
	@Deprecated( forRemoval = true, since = "6.2" )
	public static final String BYTECODE_PROVIDER_NAME_BYTEBUDDY = "bytebuddy";

	/**
	 * Singleton access
	 */
	public static final StandardServiceInitiator<BytecodeProvider> INSTANCE = new BytecodeProviderInitiator();

	@Override
	public BytecodeProvider initiateService(Map<String, Object> configurationValues, ServiceRegistryImplementor registry) {
		
		System.out.println("INIT bytcode Service");
		final var bytecodeProviders =
				registry.requireService( ClassLoaderService.class )
						.loadJavaServices( BytecodeProvider.class );
		
		System.out.println("bytecode providers "+bytecodeProviders);
		return getBytecodeProvider( bytecodeProviders );
	}

	@Override
	public Class<BytecodeProvider> getServiceInitiated() {
		return BytecodeProvider.class;
	}

	@Internal
	public static BytecodeProvider buildDefaultBytecodeProvider() {
		// Use BytecodeProvider's ClassLoader to ensure we can find the service
		return getBytecodeProvider( ServiceLoader.load(
				BytecodeProvider.class,
				BytecodeProvider.class.getClassLoader()
		) );
	}

	@Internal
	public static BytecodeProvider getBytecodeProvider(Iterable<BytecodeProvider> bytecodeProviders) {
		final var iterator = bytecodeProviders.iterator();
		System.out.println("bytecode providers "+bytecodeProviders);
		if ( !iterator.hasNext() ) {
			System.out.println("bytecode provider no op ");
			// If no BytecodeProvider service is available, default to the "no-op" enhancer
			return new org.hibernate.bytecode.internal.none.BytecodeProviderImpl();
		}
		else {
			final var provider = iterator.next();
			System.out.println("bytecode provider found "+provider);
			if ( iterator.hasNext() ) {
				throw new IllegalStateException(
						"Found multiple BytecodeProvider service registrations, cannot determine which one to use" );
			}
			return provider;
		}
	}
}
