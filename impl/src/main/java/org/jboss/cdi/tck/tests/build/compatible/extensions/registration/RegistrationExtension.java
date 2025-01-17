package org.jboss.cdi.tck.tests.build.compatible.extensions.registration;

import jakarta.enterprise.inject.build.compatible.spi.BeanInfo;
import jakarta.enterprise.inject.build.compatible.spi.BuildCompatibleExtension;
import jakarta.enterprise.inject.build.compatible.spi.Messages;
import jakarta.enterprise.inject.build.compatible.spi.ObserverInfo;
import jakarta.enterprise.inject.build.compatible.spi.Registration;
import jakarta.enterprise.inject.build.compatible.spi.Types;
import jakarta.enterprise.inject.build.compatible.spi.Validation;

import java.util.concurrent.atomic.AtomicInteger;

public class RegistrationExtension implements BuildCompatibleExtension {
    private final AtomicInteger beanCounter = new AtomicInteger();
    private final AtomicInteger beanMyQualifierCounter = new AtomicInteger();
    private final AtomicInteger observerCounter = new AtomicInteger();

    @Registration(types = MyService.class)
    public void beans(BeanInfo bean) {
        beanCounter.incrementAndGet();

        if (bean.qualifiers().stream().anyMatch(it -> it.name().equals(MyQualifier.class.getName()))) {
            beanMyQualifierCounter.incrementAndGet();
        }
    }

    @Registration(types = Object.class)
    public void observers(ObserverInfo observer, Types types) {
        if (observer.declaringClass().superInterfaces().contains(types.of(MyService.class))) {
            observerCounter.incrementAndGet();
        }
    }

    @Validation
    public void test(Messages msg) {
        if (beanCounter.get() != 2) {
            msg.error("Should see 2 beans of type MyService");
        }

        if (beanMyQualifierCounter.get() != 1) {
            msg.error("Should see 1 bean of type MyService with qualifier MyQualifier");
        }

        if (observerCounter.get() != 1) {
            msg.error("Should see 1 observer declared in class that implements MyService");
        }
    }
}
