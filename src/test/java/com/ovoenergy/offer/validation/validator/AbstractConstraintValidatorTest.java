package com.ovoenergy.offer.validation.validator;

import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.junit.Before;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.validation.ConstraintValidatorContext;

import java.lang.annotation.Annotation;
import java.lang.reflect.Proxy;
import java.util.Map;

import static org.mockito.Mockito.when;

public class AbstractConstraintValidatorTest {
    @Mock
    protected HibernateConstraintValidatorContext context;

    @Mock
    protected ConstraintValidatorContext.ConstraintViolationBuilder builder;

    @Mock
    protected ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext nodeBuilderContext;

    @Captor
    protected ArgumentCaptor<String> expressionVariableNameCaptor;

    @Captor
    protected ArgumentCaptor<Object> expressionVariableValueCaptor;

    @Captor
    protected ArgumentCaptor<String> propertyNodeCaptor;

    @Captor
    protected ArgumentCaptor<String> messageCaptor;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        when(context.unwrap(HibernateConstraintValidatorContext.class)).thenReturn(context);
        when(context.addExpressionVariable(
                expressionVariableNameCaptor.capture(),
                expressionVariableValueCaptor.capture())
        ).thenReturn(context);
        when(context.buildConstraintViolationWithTemplate(messageCaptor.capture())).thenReturn(builder);
        when(builder.addPropertyNode(propertyNodeCaptor.capture())).thenReturn(nodeBuilderContext);
    }

    @SuppressWarnings("unchecked")
    public static <A extends Annotation> A annotation(final Class<A> annotationType, final Map<String, Object> methodNameToValue) {
        return (A) Proxy.newProxyInstance(annotationType.getClassLoader(),
                new Class[]{annotationType},
                (proxy, method, args) -> methodNameToValue.get(method.getName())
        );
    }

}
