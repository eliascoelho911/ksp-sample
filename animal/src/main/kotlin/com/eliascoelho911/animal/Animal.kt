package com.eliascoelho911.animal

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class DeclareAnimal

interface Animal {
    val name: String
    val sound: String
}