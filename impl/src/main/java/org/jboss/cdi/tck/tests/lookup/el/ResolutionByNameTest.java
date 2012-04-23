/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.cdi.tck.tests.lookup.el;

import static org.jboss.cdi.tck.TestGroups.EL;
import static org.jboss.cdi.tck.TestGroups.LIFECYCLE;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.spi.Context;
import javax.enterprise.inject.spi.Bean;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.cdi.tck.AbstractTest;
import org.jboss.cdi.tck.shrinkwrap.WebArchiveBuilder;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.test.audit.annotations.SpecAssertion;
import org.jboss.test.audit.annotations.SpecAssertions;
import org.jboss.test.audit.annotations.SpecVersion;
import org.testng.annotations.Test;

@SpecVersion(spec = "cdi", version = "20091101")
public class ResolutionByNameTest extends AbstractTest {

    @Deployment
    public static WebArchive createTestArchive() {
        return new WebArchiveBuilder().withTestClassPackage(ResolutionByNameTest.class).build();
    }

    @Test
    @SpecAssertion(section = "6.4.3", id = "a")
    public void testQualifiedNameLookup() {
        assert getCurrentConfiguration().getEl().evaluateValueExpression(getCurrentManager(),
                "#{(game.value == 'foo' and game.value == 'foo') ? game.value == 'foo' : false}", Boolean.class);
        assert getInstanceByType(Counter.class).getCount() == 1;
    }

    @Test(groups = LIFECYCLE)
    @SpecAssertions({ @SpecAssertion(section = "6.5.2", id = "a"), @SpecAssertion(section = "6.5.2", id = "b") })
    public void testContextCreatesNewInstanceForInjection() {
        Context requestContext = getCurrentManager().getContext(RequestScoped.class);
        Bean<Tuna> tunaBean = getBeans(Tuna.class).iterator().next();
        assert requestContext.get(tunaBean) == null;
        TunaFarm tunaFarm = getCurrentConfiguration().getEl().evaluateValueExpression(getCurrentManager(), "#{tunaFarm}",
                TunaFarm.class);
        assert tunaFarm.tuna != null;
        long timestamp = tunaFarm.tuna.getTimestamp();
        // Lookup once again - do not create new instance - contextual instance already exists
        Tuna tuna = requestContext.get(tunaBean);
        assert tuna != null;
        assert timestamp == tuna.getTimestamp();
    }

    @Test(groups = { EL })
    @SpecAssertion(section = "12.5", id = "c")
    public void testUnresolvedNameReturnsNull() {
        assert getCurrentManager().getELResolver().getValue(
                getCurrentConfiguration().getEl().createELContext(getCurrentManager()), null, "nonExistingTuna") == null;
    }

    @Test(groups = EL)
    @SpecAssertions({ @SpecAssertion(section = "12.5", id = "d"), @SpecAssertion(section = "2.5", id = "a") })
    public void testELResolverReturnsContextualInstance() {
        Salmon salmon = getInstanceByType(Salmon.class);
        salmon.setAge(3);
        assert getCurrentConfiguration().getEl().evaluateValueExpression(getCurrentManager(), "#{salmon.age}", Integer.class) == 3;
    }
}