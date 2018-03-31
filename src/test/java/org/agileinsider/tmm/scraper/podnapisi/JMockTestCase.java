package org.agileinsider.tmm.scraper.podnapisi;

import org.jmock.Mockery;
import org.jmock.internal.ExpectationBuilder;

public class JMockTestCase {
    private Mockery context = new Mockery();

    public <T> T mock(Class<T> toMock) {
        return context.mock(toMock);
    }

    public void checking(ExpectationBuilder expectations) {
        context.checking(expectations);
    }
}
