/*
 * Copyright 2010-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.tools.projectWizard.cli;

import com.intellij.testFramework.TestDataPath;
import org.jetbrains.kotlin.test.JUnit3RunnerWithInners;
import org.jetbrains.kotlin.test.KotlinTestUtils;
import org.jetbrains.kotlin.test.TestMetadata;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.regex.Pattern;

/** This class is generated by {@link org.jetbrains.kotlin.generators.tests.TestsPackage}. DO NOT MODIFY MANUALLY */
@SuppressWarnings("all")
@TestMetadata("libraries/tools/new-project-wizard/new-project-wizard-cli/testData/projectTemplatesBuildFileGeneration")
@TestDataPath("$PROJECT_ROOT")
@RunWith(JUnit3RunnerWithInners.class)
public class ProjectTemplateBuildFileGenerationTestGenerated extends AbstractProjectTemplateBuildFileGenerationTest {
    private void runTest(String testDataFilePath) throws Exception {
        KotlinTestUtils.runTest(this::doTest, this, testDataFilePath);
    }

    public void testAllFilesPresentInProjectTemplatesBuildFileGeneration() throws Exception {
        KotlinTestUtils.assertAllTestsPresentByMetadataWithExcluded(this.getClass(), new File("libraries/tools/new-project-wizard/new-project-wizard-cli/testData/projectTemplatesBuildFileGeneration"), Pattern.compile("^([^\\.]+)$"), null, false);
    }

    @TestMetadata("backendApplication")
    public void testBackendApplication() throws Exception {
        runTest("libraries/tools/new-project-wizard/new-project-wizard-cli/testData/projectTemplatesBuildFileGeneration/backendApplication/");
    }

    @TestMetadata("consoleApplication")
    public void testConsoleApplication() throws Exception {
        runTest("libraries/tools/new-project-wizard/new-project-wizard-cli/testData/projectTemplatesBuildFileGeneration/consoleApplication/");
    }

    @TestMetadata("frontendApplication")
    public void testFrontendApplication() throws Exception {
        runTest("libraries/tools/new-project-wizard/new-project-wizard-cli/testData/projectTemplatesBuildFileGeneration/frontendApplication/");
    }

    @TestMetadata("fullStackWebApplication")
    public void testFullStackWebApplication() throws Exception {
        runTest("libraries/tools/new-project-wizard/new-project-wizard-cli/testData/projectTemplatesBuildFileGeneration/fullStackWebApplication/");
    }

    @TestMetadata("multiplatformApplication")
    public void testMultiplatformApplication() throws Exception {
        runTest("libraries/tools/new-project-wizard/new-project-wizard-cli/testData/projectTemplatesBuildFileGeneration/multiplatformApplication/");
    }

    @TestMetadata("multiplatformLibrary")
    public void testMultiplatformLibrary() throws Exception {
        runTest("libraries/tools/new-project-wizard/new-project-wizard-cli/testData/projectTemplatesBuildFileGeneration/multiplatformLibrary/");
    }

    @TestMetadata("multiplatformMobileApplication")
    public void testMultiplatformMobileApplication() throws Exception {
        runTest("libraries/tools/new-project-wizard/new-project-wizard-cli/testData/projectTemplatesBuildFileGeneration/multiplatformMobileApplication/");
    }

    @TestMetadata("multiplatformMobileLibrary")
    public void testMultiplatformMobileLibrary() throws Exception {
        runTest("libraries/tools/new-project-wizard/new-project-wizard-cli/testData/projectTemplatesBuildFileGeneration/multiplatformMobileLibrary/");
    }

    @TestMetadata("nativeApplication")
    public void testNativeApplication() throws Exception {
        runTest("libraries/tools/new-project-wizard/new-project-wizard-cli/testData/projectTemplatesBuildFileGeneration/nativeApplication/");
    }

    @TestMetadata("сomposeMultiplatformApplication")
    public void testComposeMultiplatformApplication() throws Exception {
        runTest("libraries/tools/new-project-wizard/new-project-wizard-cli/testData/projectTemplatesBuildFileGeneration/composeMultiplatformApplication/");
    }

    @TestMetadata("composeDesktopApplication")
    public void testComposeDesktopApplication() throws Exception {
        runTest("libraries/tools/new-project-wizard/new-project-wizard-cli/testData/projectTemplatesBuildFileGeneration/composeDesktopApplication/");
    }
}
