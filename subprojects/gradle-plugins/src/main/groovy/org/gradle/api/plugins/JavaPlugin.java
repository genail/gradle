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

package org.gradle.api.plugins;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.file.FileCollection;
import org.gradle.api.internal.artifacts.publish.ArchivePublishArtifact;
import org.gradle.api.internal.plugins.EmbeddableJavaProject;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.bundling.Jar;
import org.gradle.api.tasks.javadoc.Javadoc;
import org.gradle.api.tasks.testing.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.Callable;

/**
 * <p>A {@link Plugin} which compiles and tests Java source, and assembles it into a JAR file.</p>
 *
 * @author Hans Dockter
 */
public class JavaPlugin implements Plugin<Project> {
    public static final String PROCESS_RESOURCES_TASK_NAME = "processResources";
    public static final String CLASSES_TASK_NAME = "classes";
    public static final String COMPILE_JAVA_TASK_NAME = "compileJava";
    public static final String PROCESS_TEST_RESOURCES_TASK_NAME = "processTestResources";
    public static final String TEST_CLASSES_TASK_NAME = "testClasses";
    public static final String COMPILE_TEST_JAVA_TASK_NAME = "compileTestJava";
    public static final String TEST_TASK_NAME = "test";
    public static final String JAR_TASK_NAME = "jar";
    public static final String JAVADOC_TASK_NAME = "javadoc";

    public static final String COMPILE_CONFIGURATION_NAME = "compile";
    public static final String RUNTIME_CONFIGURATION_NAME = "runtime";
    public static final String TEST_RUNTIME_CONFIGURATION_NAME = "testRuntime";
    public static final String TEST_COMPILE_CONFIGURATION_NAME = "testCompile";

    public void apply(Project project) {
        project.getPlugins().apply(JavaBasePlugin.class);

        JavaPluginConvention javaConvention = (JavaPluginConvention) project.getConvention().getPlugins().get("java");
        project.getConvention().getPlugins().put("embeddedJavaProject", new EmbeddableJavaProjectImpl(javaConvention));

        configureConfigurations(project);

        configureSourceSets(javaConvention);

        configureJavaDoc(javaConvention);
        configureTest(project, javaConvention);
        configureArchives(project, javaConvention);
        configureBuild(project);
    }

    private void configureSourceSets(final JavaPluginConvention pluginConvention) {
        final Project project = pluginConvention.getProject();

        pluginConvention.getSourceSets().allObjects(new Action<SourceSet>() {
            public void execute(SourceSet sourceSet) {
                sourceSet.setCompileClasspath(project.getConfigurations().getByName(COMPILE_CONFIGURATION_NAME));
                sourceSet.setRuntimeClasspath(sourceSet.getClasses().plus(project.getConfigurations().getByName(
                        RUNTIME_CONFIGURATION_NAME)));
            }
        });
        SourceSet main = pluginConvention.getSourceSets().add(SourceSet.MAIN_SOURCE_SET_NAME);

        SourceSet test = pluginConvention.getSourceSets().add(SourceSet.TEST_SOURCE_SET_NAME);
        test.setCompileClasspath(project.files(main.getClasses(), project.getConfigurations().getByName(
                TEST_COMPILE_CONFIGURATION_NAME)));
        test.setRuntimeClasspath(project.files(test.getClasses(), main.getClasses(),
                project.getConfigurations().getByName(TEST_RUNTIME_CONFIGURATION_NAME)));
    }

    private void configureJavaDoc(final JavaPluginConvention pluginConvention) {
        Project project = pluginConvention.getProject();

        SourceSet mainSourceSet = pluginConvention.getSourceSets().getByName(SourceSet.MAIN_SOURCE_SET_NAME);
        Javadoc javadoc = project.getTasks().add(JAVADOC_TASK_NAME, Javadoc.class);
        javadoc.setDescription("Generates the javadoc for the source code.");
        javadoc.setClasspath(mainSourceSet.getClasses().plus(mainSourceSet.getCompileClasspath()));
        javadoc.setSource(mainSourceSet.getAllJava());
        addDependsOnTaskInOtherProjects(javadoc, true, JAVADOC_TASK_NAME, COMPILE_CONFIGURATION_NAME);
    }

    private void configureArchives(final Project project, final JavaPluginConvention pluginConvention) {
        project.getTasks().getByName(JavaBasePlugin.CHECK_TASK_NAME).dependsOn(TEST_TASK_NAME);
        Jar jar = project.getTasks().add(JAR_TASK_NAME, Jar.class);
        jar.getManifest().from(pluginConvention.getManifest());
        jar.setDescription("Generates a jar archive with all the compiled classes.");
        jar.from(pluginConvention.getSourceSets().getByName(SourceSet.MAIN_SOURCE_SET_NAME).getClasses());
        jar.getMetaInf().from(new Callable() {
            public Object call() throws Exception {
                return pluginConvention.getMetaInf();
            }
        });

        project.getConfigurations().getByName(Dependency.ARCHIVES_CONFIGURATION).addArtifact(new ArchivePublishArtifact(
                jar));
    }

    private void configureBuild(Project project) {
        addDependsOnTaskInOtherProjects(project.getTasks().getByName(JavaBasePlugin.BUILD_NEEDED_TASK_NAME), true,
                JavaBasePlugin.BUILD_TASK_NAME, TEST_RUNTIME_CONFIGURATION_NAME);
        addDependsOnTaskInOtherProjects(project.getTasks().getByName(JavaBasePlugin.BUILD_DEPENDENTS_TASK_NAME), false,
                JavaBasePlugin.BUILD_TASK_NAME, TEST_RUNTIME_CONFIGURATION_NAME);
    }

    private void configureTest(final Project project, final JavaPluginConvention pluginConvention) {
        Test test = project.getTasks().add(TEST_TASK_NAME, Test.class);
        test.setDescription("Runs the unit tests.");
        test.getConventionMapping().map("testClassesDir", new Callable<Object>() {
            public Object call() throws Exception {
                return pluginConvention.getSourceSets().getByName(SourceSet.TEST_SOURCE_SET_NAME).getClassesDir();
            }
        });
        test.getConventionMapping().map("classpath", new Callable<Object>() {
            public Object call() throws Exception {
                return pluginConvention.getSourceSets().getByName(SourceSet.TEST_SOURCE_SET_NAME).getRuntimeClasspath();
            }
        });
        test.getConventionMapping().map("testSrcDirs", new Callable<Object>() {
            public Object call() throws Exception {
                return new ArrayList<File>(pluginConvention.getSourceSets().getByName(SourceSet.TEST_SOURCE_SET_NAME)
                        .getJava().getSrcDirs());
            }
        });
    }

    void configureConfigurations(final Project project) {
        ConfigurationContainer configurations = project.getConfigurations();
        Configuration compileConfiguration = configurations.add(COMPILE_CONFIGURATION_NAME).setVisible(false).
                setDescription("Classpath for compiling the sources.");
        Configuration runtimeConfiguration = configurations.add(RUNTIME_CONFIGURATION_NAME).setVisible(false)
                .extendsFrom(compileConfiguration).
                        setDescription("Classpath for running the compiled sources.");

        Configuration compileTestsConfiguration = configurations.add(TEST_COMPILE_CONFIGURATION_NAME).setVisible(false)
                .extendsFrom(compileConfiguration).setDescription("Classpath for compiling the test sources.");

        configurations.add(TEST_RUNTIME_CONFIGURATION_NAME).setVisible(false).extendsFrom(runtimeConfiguration,
                compileTestsConfiguration).
                setDescription("Classpath for running the test sources.");

        configurations.getByName(Dependency.DEFAULT_CONFIGURATION).extendsFrom(runtimeConfiguration);
    }

    /**
     * Adds a dependency on tasks with the specified name in other projects.  The other projects are determined from
     * project lib dependencies using the specified configuration name. These may be projects this project depends on or
     * projects that depend on this project based on the useDependOn argument.
     *
     * @param task Task to add dependencies to
     * @param useDependedOn if true, add tasks from projects this project depends on, otherwise use projects that depend
     * on this one.
     * @param otherProjectTaskName name of task in other projects
     * @param configurationName name of configuration to use to find the other projects
     */
    private void addDependsOnTaskInOtherProjects(final Task task, boolean useDependedOn, String otherProjectTaskName,
                                                 String configurationName) {
        Project project = task.getProject();
        final Configuration configuration = project.getConfigurations().getByName(configurationName);
        task.dependsOn(configuration.getTaskDependencyFromProjectDependency(useDependedOn, otherProjectTaskName));
    }

    private static class EmbeddableJavaProjectImpl implements EmbeddableJavaProject {
        private final JavaPluginConvention convention;

        public EmbeddableJavaProjectImpl(JavaPluginConvention convention) {
            this.convention = convention;
        }

        public Collection<String> getRebuildTasks() {
            return Arrays.asList(BasePlugin.CLEAN_TASK_NAME, JavaBasePlugin.BUILD_TASK_NAME);
        }

        public FileCollection getRuntimeClasspath() {
            return convention.getSourceSets().getByName(SourceSet.MAIN_SOURCE_SET_NAME).getRuntimeClasspath();
        }
    }
}
