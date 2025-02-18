/*
 * Copyright 2010-2021 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.analysis.low.level.api.fir.compiler.based

import com.intellij.openapi.Disposable
import com.intellij.openapi.util.Disposer
import org.jetbrains.kotlin.analysis.low.level.api.fir.api.DiagnosticCheckerFilter
import org.jetbrains.kotlin.analysis.low.level.api.fir.api.getOrBuildFirFile
import org.jetbrains.kotlin.analysis.low.level.api.fir.createResolveStateForNoCaching
import org.jetbrains.kotlin.analysis.low.level.api.fir.transformers.FirLazyTransformerForIDE
import org.jetbrains.kotlin.analysis.project.structure.*
import org.jetbrains.kotlin.platform.jvm.JvmPlatforms
import org.jetbrains.kotlin.test.TestConfiguration
import org.jetbrains.kotlin.test.bind
import org.jetbrains.kotlin.test.builders.TestConfigurationBuilder
import org.jetbrains.kotlin.test.builders.testConfiguration
import org.jetbrains.kotlin.test.directives.FirDiagnosticsDirectives
import org.jetbrains.kotlin.test.directives.model.DirectivesContainer
import org.jetbrains.kotlin.test.frontend.fir.FirOutputArtifact
import org.jetbrains.kotlin.test.model.*
import org.jetbrains.kotlin.test.runners.AbstractKotlinCompilerTest
import org.jetbrains.kotlin.test.services.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInfo

abstract class AbstractCompilerBasedTest : AbstractKotlinCompilerTest() {
    private var _disposable: Disposable? = null
    protected val disposable: Disposable get() = _disposable!!

    @BeforeEach
    private fun intiDisposable(testInfo: TestInfo) {
        _disposable = Disposer.newDisposable("disposable for ${testInfo.displayName}")
    }

    @AfterEach
    private fun disposeDisposable() {
        _disposable?.let { Disposer.dispose(it) }
        _disposable = null
    }

    final override fun TestConfigurationBuilder.configuration() {
        globalDefaults {
            frontend = FrontendKinds.FIR
            targetPlatform = JvmPlatforms.defaultJvmPlatform
            dependencyKind = DependencyKind.Source
        }

        configureTest()
        defaultConfiguration(this)

        useAdditionalService(::TestKtModuleProvider)
        usePreAnalysisHandlers(::ModuleRegistrarPreAnalysisHandler.bind(disposable))
    }

    open fun TestConfigurationBuilder.configureTest() {}


    inner class LowLevelFirFrontendFacade(
        testServices: TestServices
    ) : FrontendFacade<FirOutputArtifact>(testServices, FrontendKinds.FIR) {

        override val directiveContainers: List<DirectivesContainer>
            get() = listOf(FirDiagnosticsDirectives)

        override fun analyze(module: TestModule): FirOutputArtifact {
            val moduleInfoProvider = testServices.projectModuleProvider
            val moduleInfo = moduleInfoProvider.getModule(module.name)

            val project = testServices.compilerConfigurationProvider.getProject(module)
            val resolveState = createResolveStateForNoCaching(moduleInfo, project)

            val allFirFiles = moduleInfo.testFilesToKtFiles.map { (testFile, psiFile) ->
                testFile to psiFile.getOrBuildFirFile(resolveState)
            }.toMap()

            val diagnosticCheckerFilter = if (FirDiagnosticsDirectives.WITH_EXTENDED_CHECKERS in module.directives) {
                DiagnosticCheckerFilter.EXTENDED_AND_COMMON_CHECKERS
            } else DiagnosticCheckerFilter.ONLY_COMMON_CHECKERS

            val analyzerFacade = LowLevelFirAnalyzerFacade(resolveState, allFirFiles, diagnosticCheckerFilter)
            return LowLevelFirOutputArtifact(resolveState.rootModuleSession, analyzerFacade)
        }
    }

    override fun runTest(filePath: String) {
        val configuration = testConfiguration(filePath, configuration)
        if (ignoreTest(filePath, configuration)) {
            return
        }
        val oldEnableDeepEnsure = FirLazyTransformerForIDE.enableDeepEnsure
        try {
            FirLazyTransformerForIDE.enableDeepEnsure = true
            super.runTest(filePath)
        } finally {
            FirLazyTransformerForIDE.enableDeepEnsure = oldEnableDeepEnsure
        }
    }

    private fun ignoreTest(filePath: String, configuration: TestConfiguration): Boolean {
        val modules = configuration.moduleStructureExtractor.splitTestDataByModules(filePath, configuration.directives)

        if (modules.modules.none { it.files.any { it.isKtFile } }) {
            return true // nothing to highlight
        }

        return false
    }
}
