package software.amazonaws.example.product.dao;

/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */

import org.hibernate.annotations.*;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.annotations.DialectOverride;
import org.hibernate.boot.models.annotations.spi.DialectOverrider;
import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.dialect.Dialect;


import static org.hibernate.internal.util.collections.CollectionHelper.isNotEmpty;

/**
 * @author Sanne Grinovero
 * @author Steve Ebersole
 */
public class DialectOverridesAnnotationHelper {
	private static final Map<Class<? extends Annotation>, Class<? extends Annotation>> OVERRIDE_MAP = buildOverrideMap();

	private static Map<Class<? extends Annotation>, Class<? extends Annotation>> buildOverrideMap() {
		// not accessed concurrently
		System.out.println("build override map");
		final Map<Class<? extends Annotation>, Class<? extends Annotation>> results = new HashMap<>();
		for ( Class<?> dialectOverrideMember : DialectOverride.class.getNestMembers() ) {
			System.out.println("class "+dialectOverrideMember);
			if ( dialectOverrideMember.isAnnotation() ) {
				System.out.println("class is annot");
				final var overrideAnnotation =
						dialectOverrideMember.getAnnotation( DialectOverride.OverridesAnnotation.class );
				if ( overrideAnnotation != null ) {
					System.out.println("class override annot");
					// The "real" annotation.  e.g. `org.hibernate.annotations.Formula`
					final var baseAnnotation = overrideAnnotation.value();
					// the "override" annotation.  e.g. `org.hibernate.annotations.DialectOverride.Formula`
					//noinspection unchecked
					final var dialectOverrideAnnotation = (Class<? extends Annotation>) dialectOverrideMember;
					System.out.println("put base  annot: "+ baseAnnotation+ "dialect override ann: "+dialectOverrideAnnotation);
					results.put( baseAnnotation, dialectOverrideAnnotation );
				}
			}
		}
		System.out.println("res map: "+results);
		return results;
	}

	public static <A extends Annotation, O extends Annotation> Class<O> getOverrideAnnotation(Class<A> annotationType) {
		final Class<O> overrideAnnotation = findOverrideAnnotation( annotationType );
		if ( overrideAnnotation == null ) {
			throw new HibernateException(
					String.format(
							Locale.ROOT,
							"Specified Annotation type (%s) does not have an override form",
							annotationType.getName()
					)
			);
		}
		else {
			return overrideAnnotation;
		}
	}

	public static <A extends Annotation, O extends Annotation> Class<O> findOverrideAnnotation(Class<A> annotationType) {
		//noinspection unchecked
		System.out.println("res map: "+OVERRIDE_MAP);
		System.out.println("ann type: "+annotationType);
		return (Class<O>) OVERRIDE_MAP.get( annotationType );
	}

	
}
