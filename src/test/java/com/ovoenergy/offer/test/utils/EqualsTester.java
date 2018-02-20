package com.ovoenergy.offer.test.utils;

import com.google.common.base.Equivalence;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public final class EqualsTester {
    private static final int REPETITIONS = 3;

    private final List<List<Object>> equalityGroups = Lists.newArrayList();
    private final RelationshipTester.ItemReporter itemReporter;

    /**
     * Constructs an empty EqualsTester instance
     */
    public EqualsTester() {
        this(new RelationshipTester.ItemReporter());
    }

    EqualsTester(RelationshipTester.ItemReporter itemReporter) {
        this.itemReporter = checkNotNull(itemReporter);
    }

    /**
     * Adds {@code equalityGroup} with objects that are supposed to be equal to
     * each other and not equal to any other equality groups added to this tester.
     */
    public EqualsTester addEqualityGroup(Object... equalityGroup) {
        checkNotNull(equalityGroup);
        equalityGroups.add(ImmutableList.copyOf(equalityGroup));
        return this;
    }

    /**
     * Run tests on equals method, throwing a failure on an invalid test
     */
    public EqualsTester testEquals() {
        RelationshipTester<Object> delegate = new RelationshipTester<>(
                Equivalence.equals(), "Object#equals", "Object#hashCode", itemReporter);
        equalityGroups.forEach(delegate::addRelatedGroup);
        for (int run = 0; run < REPETITIONS; run++) {
            testItems();
            delegate.test();
        }
        return this;
    }

    private void testItems() {
        for (Object item : Iterables.concat(equalityGroups)) {
            assertTrue(item + " must not be Object#equals to null", !item.equals(null));
            assertTrue(item + " must not be Object#equals to an arbitrary object of another class",
                    !item.equals(NotAnInstance.EQUAL_TO_NOTHING));
            assertEquals(item + " must be Object#equals to itself", item, item);
            assertEquals("the Object#hashCode of " + item + " must be consistent",
                    item.hashCode(), item.hashCode());
        }
    }

    /**
     * Class used to test whether equals() correctly handles an instance
     * of an incompatible class.  Since it is a private inner class, the
     * invoker can never pass in an instance to the tester
     */
    private enum NotAnInstance {
        EQUAL_TO_NOTHING
    }
}

