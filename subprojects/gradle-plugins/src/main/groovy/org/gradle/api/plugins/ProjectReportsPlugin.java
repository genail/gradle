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
package org.gradle.api.plugins;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.internal.IConventionAware;
import org.gradle.api.tasks.ConventionValue;
import org.gradle.api.tasks.diagnostics.DependencyReportTask;
import org.gradle.api.tasks.diagnostics.PropertyReportTask;
import org.gradle.api.tasks.diagnostics.TaskReportTask;

import java.io.File;

/**
 * <p>A {@link Plugin} which adds some project visualization report tasks to a project.</p>
 */
public class ProjectReportsPlugin implements Plugin<Project> {
    public static final String TASK_REPORT = "taskReport";
    public static final String PROPERTY_REPORT = "propertyReport";
    public static final String DEPENDENCY_REPORT = "dependencyReport";
    public static final String PROJECT_REPORT = "projectReport";

    public void apply(Project project) {
        project.getPlugins().apply(ReportingBasePlugin.class);
        project.getConvention().getPlugins().put("projectReports", new ProjectReportsPluginConvention(project));

        TaskReportTask taskReportTask = project.getTasks().add(TASK_REPORT, TaskReportTask.class);
        taskReportTask.setDescription("Generates a report about your tasks.");
        taskReportTask.conventionMapping("outputFile", new ConventionValue() {
            public Object getValue(Convention convention, IConventionAware conventionAwareObject) {
                return new File(convention.getPlugin(ProjectReportsPluginConvention.class).getProjectReportDir(), "tasks.txt");
            }
        });
        taskReportTask.conventionMapping("projects", new ConventionValue() {
            public Object getValue(Convention convention, IConventionAware conventionAwareObject) {
                return convention.getPlugin(ProjectReportsPluginConvention.class).getProjects();
            }
        });

        PropertyReportTask propertyReportTask = project.getTasks().add(PROPERTY_REPORT, PropertyReportTask.class);
        propertyReportTask.setDescription("Generates a report about your properties.");
        propertyReportTask.conventionMapping("outputFile", new ConventionValue() {
            public Object getValue(Convention convention, IConventionAware conventionAwareObject) {
                return new File(convention.getPlugin(ProjectReportsPluginConvention.class).getProjectReportDir(), "properties.txt");
            }
        });
        propertyReportTask.conventionMapping("projects", new ConventionValue() {
            public Object getValue(Convention convention, IConventionAware conventionAwareObject) {
                return convention.getPlugin(ProjectReportsPluginConvention.class).getProjects();
            }
        });

        DependencyReportTask dependencyReportTask = project.getTasks().add(DEPENDENCY_REPORT,
                DependencyReportTask.class);
        dependencyReportTask.setDescription("Generates a report about your library dependencies.");
        dependencyReportTask.conventionMapping("outputFile", new ConventionValue() {
            public Object getValue(Convention convention, IConventionAware conventionAwareObject) {
                return new File(convention.getPlugin(ProjectReportsPluginConvention.class).getProjectReportDir(), "dependencies.txt");
            }
        });
        dependencyReportTask.conventionMapping("projects", new ConventionValue() {
            public Object getValue(Convention convention, IConventionAware conventionAwareObject) {
                return convention.getPlugin(ProjectReportsPluginConvention.class).getProjects();
            }
        });


        Task projectReportTask = project.getTasks().add(PROJECT_REPORT);
        projectReportTask.dependsOn(TASK_REPORT, PROPERTY_REPORT, DEPENDENCY_REPORT);
        projectReportTask.setDescription("Generated a report about your project.");
    }
}
