/*
 * Copyright 2010-2021 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.backend.konan.lower

import org.jetbrains.kotlin.backend.common.lower.AnnotationImplementationTransformer
import org.jetbrains.kotlin.backend.common.lower.createIrBuilder
import org.jetbrains.kotlin.backend.konan.*
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.*
import org.jetbrains.kotlin.ir.expressions.impl.IrInstanceInitializerCallImpl
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.backend.common.lower.*
import org.jetbrains.kotlin.descriptors.DescriptorVisibilities
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.descriptors.Visibilities
import org.jetbrains.kotlin.ir.*


internal class NativeAnnotationImplementationTransformer(context: Context, irFile: IrFile) :
        AnnotationImplementationTransformer(context, irFile) {

    override fun visitClassNew(declaration: IrClass): IrStatement {
        if (declaration.isAnnotationClass) {
            val irConstructor = declaration.declarations.filterIsInstance<IrConstructor>().single()
            assert(irConstructor.body == null)
            irConstructor.body = context.createIrBuilder(irConstructor.symbol, SYNTHETIC_OFFSET, SYNTHETIC_OFFSET).irBlockBody(irConstructor) {
                +irDelegatingConstructorCall(context.irBuiltIns.anyClass.owner.constructors.single())
                +IrInstanceInitializerCallImpl(startOffset, endOffset, declaration.symbol, context.irBuiltIns.unitType)
            }
        }
        return super.visitClassNew(declaration)
    }

    private val arrayContentEqualsMap = context.ir.symbols.arraysContentEquals

    override fun generatedEquals(irBuilder: IrBlockBodyBuilder, type: IrType, arg1: IrExpression, arg2: IrExpression): IrExpression {
        return if (type.isArray() || type.isPrimitiveArray()) {
            val requiredSymbol =
                    if (type.isPrimitiveArray())
                        arrayContentEqualsMap[type]
                    else
                        arrayContentEqualsMap.entries.singleOrNull { (k, _) -> k.isArray() }?.value
            if (requiredSymbol == null) {
                error("Can't find an Arrays.contentEquals method for array type ${type.render()}")
            }
            irBuilder.irCall(
                    requiredSymbol
            ).apply {
                extensionReceiver = arg1
                putValueArgument(0, arg2)
            }
        } else super.generatedEquals(irBuilder, type, arg1, arg2)
    }

    override fun IrClass.platformSetup() {
        visibility = DescriptorVisibilities.PRIVATE
        parent = irFile!!
    }
}