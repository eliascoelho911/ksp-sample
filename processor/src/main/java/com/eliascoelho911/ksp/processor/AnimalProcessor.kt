package com.eliascoelho911.ksp.processor

import com.eliascoelho911.animal.DeclareAnimal
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSNode
import com.google.devtools.ksp.validate
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.writeTo

private const val GENERATE_CLASS_NAME = "AnimalProvider"
private const val GENERATE_FILE_PACKAGE = "com.eliascoelho911.animal.generated"

class AnimalProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger
) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val animals = getAnimals(resolver)

        if (!animals.iterator().hasNext()) return emptyList()

        genFile(animals).writeTo(codeGenerator, Dependencies(false, *resolver.getAllFiles().toList().toTypedArray()))

        return emptyList()
    }

    private fun getAnimals(resolver: Resolver): Map<String, KSClassDeclaration> {
        return resolver
            .getSymbolsWithAnnotation(DeclareAnimal::class.qualifiedName.orEmpty())
            .filterIsInstance<KSClassDeclaration>()
            .filter(KSNode::validate)
            .associateBy {
                it.toClassName().simpleName
            }
    }

    private fun genFile(
        animals: Map<String, KSClassDeclaration>
    ): FileSpec {
        return FileSpec.builder(
            packageName = GENERATE_FILE_PACKAGE,
            fileName = GENERATE_CLASS_NAME
        ).addType(
            TypeSpec.classBuilder(GENERATE_CLASS_NAME)
                .addFunction(
                    FunSpec.builder("get")
                        .addParameter("name", String::class)
                        .returns(getAnimalSuperClassName())
                        .beginControlFlow("return when (name)")
                        .apply {
                            animals.forEach { (name, animal) ->
                                addStatement("%S -> %T()", name, animal.toClassName())
                            }
                        }
                        .addStatement(
                            "else -> throw IllegalArgumentException(%S)",
                            "Animal not found"
                        )
                        .endControlFlow()
                        .build()
                )
                .addFunction(
                    FunSpec.builder("getAll")
                        .returns(
                            List::class.asClassName().parameterizedBy(getAnimalSuperClassName())
                        )
                        .addStatement(
                            "return listOf(%L)",
                            animals.values
                                .map { it.toClassName().simpleName }
                                .joinToString { "$it()" })
                        .build()
                ).build()
        ).build()
    }

    private fun getAnimalSuperClassName(): ClassName {
        return ClassName(
            packageName = "com.eliascoelho911.animal",
            simpleNames = listOf("Animal")
        )
    }
}