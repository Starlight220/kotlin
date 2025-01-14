/*
 * Copyright 2010-2021 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package kotlin.js

/**
 * Exports top-level declaration on JS platform.
 *
 * Can only be applied to top-level functions.
 */
@ExperimentalJsExport
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.FUNCTION)
public actual annotation class JsExport