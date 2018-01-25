package com.ovoenergy.offer.test.utils;

import static com.google.common.collect.Lists.newArrayList;
import static java.lang.reflect.Modifier.isFinal;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertTrue;
import static org.springframework.beans.BeanUtils.copyProperties;
import static org.springframework.util.ReflectionUtils.makeAccessible;
import static org.springframework.util.ReflectionUtils.setField;

import java.lang.reflect.Field;
import java.util.List;

import com.flextrade.jfixture.JFixture;
import com.google.common.collect.Lists;

public abstract class BaseBeansTest {

	public static <T> void verify(Class<T> clazz) {
		GetterSetterVerifierTester<T> verifier = GetterSetterVerifierTester.forClass(clazz);
		verifier.verify();
	}

	public static <T> void hashCodeEquality(Class<T> clazz) {
		JFixture fixture = new JFixture();
		T firtsInstance = fixture.create(clazz);
		T secondInstance = fixture.create(clazz);
		assertTrue(firtsInstance.hashCode() != secondInstance.hashCode());
		assertTrue(secondInstance.hashCode() != firtsInstance.hashCode());
		assertTrue(firtsInstance.hashCode() == firtsInstance.hashCode());
	}

	public static <T> void equality(Class<T> clazz) {
		JFixture fixture = new JFixture();
		T firtsInstance = fixture.create(clazz);
		T secondInstance = fixture.create(clazz);

		// Most basic test
		new EqualsTester().addEqualityGroup(firtsInstance).addEqualityGroup(secondInstance).testEquals();
		
		List<Field> allClassFields = getAllFields(clazz);
		List<Field> filteredClassFields = filterClassFields(allClassFields);
		boolean classHasPrimitiveTypes = allClassFields.stream().anyMatch(fl -> fl.getType().isPrimitive());
		testFirstInstanceNullableFields(clazz, filteredClassFields);
		testFirtAndSecondInstanceNullableFields(clazz, filteredClassFields, classHasPrimitiveTypes);
		testWithPropertiesCopy(clazz, filteredClassFields, allClassFields, classHasPrimitiveTypes);
	}

	private static <T> void testWithPropertiesCopy(Class<T> clazz, final List<Field> classFields, final List<Field> allClassFields, boolean classHasPrimitiveTypes) {
		JFixture fixture = new JFixture();
		fixture.customise().lazyInstance(boolean.class, () -> true);
		T firtsInstance = fixture.create(clazz);
		fixture.customise().lazyInstance(boolean.class, () -> false);
		T secondInstance = fixture.create(clazz);
		List<String> ignoreProperties = allClassFields.stream().map(fl -> fl.getName()).collect(toList());		
		for (int i = 0; i < classFields.size(); i++) {
			Field field = classFields.get(i);
			makeAccessible(field);
			ignoreProperties.remove(field.getName());
			copyProperties(secondInstance, firtsInstance,
					ignoreProperties.toArray(new String[ignoreProperties.size()]));
			if(i == classFields.size() - 1 && !classHasPrimitiveTypes) {
				// Test that instances are equal because all fields has been copied
				new EqualsTester().addEqualityGroup(firtsInstance, secondInstance).testEquals();
			} else {
				new EqualsTester().addEqualityGroup(firtsInstance).addEqualityGroup(secondInstance).testEquals();
			}
		}
	}

	private static <T> void testFirtAndSecondInstanceNullableFields(Class<T> clazz, List<Field> classFields, boolean classHasPrimitiveTypes) {
		JFixture fixture = new JFixture();
		fixture.customise().lazyInstance(boolean.class, () -> true);
		T firtsInstance = fixture.create(clazz);
		fixture.customise().lazyInstance(boolean.class, () -> false);
		T secondInstance = fixture.create(clazz);
		for (int i = 0; i < classFields.size(); i++) {
			Field field = classFields.get(i);
			makeAccessible(field);
			setField(field, firtsInstance, null);
			setField(field, secondInstance, null);
			if (i == classFields.size() - 1 && !classHasPrimitiveTypes) {
				// Test that instances are equal because all fields has been set to null
				new EqualsTester().addEqualityGroup(firtsInstance, secondInstance).testEquals();								
			} else {
				new EqualsTester().addEqualityGroup(firtsInstance).addEqualityGroup(secondInstance).testEquals();
			}
		}
	}

	private static <T> void testFirstInstanceNullableFields(Class<T> clazz, List<Field> classFields) {
		JFixture fixture = new JFixture();
		T firtsInstance = fixture.create(clazz);
		T secondInstance = fixture.create(clazz);
		for (Field field : classFields) {
			makeAccessible(field);
			setField(field, firtsInstance, null);
			new EqualsTester().addEqualityGroup(firtsInstance).addEqualityGroup(secondInstance).testEquals();
		}
	}
	
	private static <T> List<Field> getAllFields(Class<T> clazz) {
		List<Field> allFields = newArrayList();
		Class<?> current = clazz;
		allFields.addAll(asList(current.getDeclaredFields()));
		while(current.getSuperclass()!=null){
		    current = current.getSuperclass();
			allFields.addAll(asList(current.getDeclaredFields()));
		}
		return allFields;
	}
	

	public static List<Field> filterClassFields(List<Field> allClassFields) {
		List<Field> filteredFields = allClassFields.stream()
				.filter(fl -> !fl.getName().equals("$jacocoData"))
				.filter(fl -> !fl.getType().isPrimitive())
				.filter(fl -> !(fl.getType().isEnum() && fl.getType().getEnumConstants().length == 1))
				.filter(fl -> !isFinal(fl.getModifiers()))
				.collect(toList());
		return filteredFields;
	}
	
}