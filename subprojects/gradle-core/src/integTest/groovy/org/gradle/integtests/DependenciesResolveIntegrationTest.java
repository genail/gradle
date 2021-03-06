/*
 * Copyright 2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gradle.integtests;

import org.gradle.integtests.fixtures.GradleDistribution;
import org.gradle.integtests.fixtures.GradleDistributionExecuter;
import org.gradle.integtests.fixtures.Sample;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

/**
 * @author Hans Dockter
 */
@RunWith (DistributionIntegrationTestRunner.class)
public class DependenciesResolveIntegrationTest {
    @Rule public final GradleDistribution dist = new GradleDistribution();
    @Rule public final GradleDistributionExecuter executer = new GradleDistributionExecuter();
    @Rule public final Sample sample = new Sample("dependencies");

    @Test
    public void testResolve() {
        dist.requireOwnUserHomeDir();

        // the actual testing is done in the build script.
        File projectDir = sample.getDir();
        executer.inDirectory(projectDir).withTasks("test").run();
    }   
}
