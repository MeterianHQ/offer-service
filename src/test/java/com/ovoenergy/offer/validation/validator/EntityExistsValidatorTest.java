package com.ovoenergy.offer.validation.validator;

import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.JpaRepository;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

public class EntityExistsValidatorTest extends AbstractConstraintValidatorTest {

    @InjectMocks
    private EntityExistsValidator entityExistsValidator;
    @Mock
    private ApplicationContext applicationContext;
    @Mock
    private JpaRepository<?, Long> repository;

    @Before
    public void setUp() {
        when(applicationContext.getBean(any(Class.class))).thenReturn(repository);
        EntityExistsConstraint entityExistsConstraint = AbstractConstraintValidatorTest.annotation(
                EntityExistsConstraint.class,
                ImmutableMap.of("repository", this.repository.getClass())
        );
        entityExistsValidator.initialize(entityExistsConstraint);
    }

    @Test
    public void testFieldValueIsNull() {
        Long value = null;

        boolean valid = entityExistsValidator.isValid(value, context);

        assertThat(valid, is(true));

        verify(applicationContext, only()).getBean(any(Class.class));
        verifyNoMoreInteractions(applicationContext);
        verifyZeroInteractions(repository);
    }

    @Test
    public void testEntityExists() {
        Long value = 1L;
        when(repository.exists(anyLong())).thenReturn(true);

        boolean valid = entityExistsValidator.isValid(value, context);

        assertThat(valid, is(true));

        verify(applicationContext, only()).getBean(any(Class.class));
        verify(repository, only()).exists(eq(value));
        verifyNoMoreInteractions(applicationContext, repository);
    }

    @Test
    public void testEntityNotExists() {
        Long value = 1L;
        when(repository.exists(anyLong())).thenReturn(false);

        boolean valid = entityExistsValidator.isValid(value, context);

        assertThat(valid, is(false));

        verify(applicationContext, only()).getBean(any(Class.class));
        verify(repository, only()).exists(eq(value));
        verifyNoMoreInteractions(applicationContext, repository);
    }

}