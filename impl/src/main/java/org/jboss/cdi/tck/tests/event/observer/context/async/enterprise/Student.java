/*
 * JBoss, Home of Professional Open Source
 * Copyright 2014, Red Hat, Inc., and individual contributors
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
package org.jboss.cdi.tck.tests.event.observer.context.async.enterprise;

import java.util.concurrent.ExecutionException;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RunAs;
import javax.ejb.Stateful;
import javax.enterprise.event.Event;
import javax.inject.Inject;

@Stateful
@RunAs("student")
@PermitAll
public class Student {
    
    public static String STUDENT_MESSAGE = "student message";

    @Inject
    Event<Text> printer;
    
    public Text print() throws ExecutionException, InterruptedException {
        return printer.fireAsync(new Text(STUDENT_MESSAGE)).toCompletableFuture().get();
    }

}
