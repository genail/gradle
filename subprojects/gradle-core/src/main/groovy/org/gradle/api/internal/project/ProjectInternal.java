/*
 * Copyright 2010 the original author or authors.
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

package org.gradle.api.internal.project;

import org.gradle.api.Project;
import org.gradle.api.ProjectEvaluationListener;
import org.gradle.api.artifacts.Module;
import org.gradle.api.internal.DomainObjectContext;
import org.gradle.api.internal.DynamicObject;
import org.gradle.api.internal.GradleInternal;
import org.gradle.api.internal.file.FileOperations;
import org.gradle.api.internal.file.FileResolver;
import org.gradle.api.internal.tasks.TaskContainerInternal;
import org.gradle.api.logging.StandardOutputCapture;
import org.gradle.groovy.scripts.ScriptAware;
import org.gradle.groovy.scripts.ScriptSource;

public interface ProjectInternal extends Project, ProjectIdentifier, ScriptAware, FileOperations, DomainObjectContext {
    ProjectInternal getParent();

    Project evaluate();

    TaskContainerInternal getTasks();

    ScriptSource getBuildScriptSource();

    void addChildProject(ProjectInternal childProject);

    IProjectRegistry<ProjectInternal> getProjectRegistry();

    DynamicObject getInheritedScope();

    GradleInternal getGradle();

    ProjectEvaluationListener getProjectEvaluationBroadcaster();

    FileResolver getFileResolver();

    ServiceRegistryFactory getServiceRegistryFactory();

    Module getModule();

    StandardOutputCapture getStandardOutputCapture();
}
