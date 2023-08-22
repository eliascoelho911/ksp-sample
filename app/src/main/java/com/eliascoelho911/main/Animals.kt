package com.eliascoelho911.main

import com.eliascoelho911.animal.Animal
import com.eliascoelho911.animal.DeclareAnimal

@DeclareAnimal
class Dog : Animal {
    override val name: String = "dog"
    override val sound: String = "woof woof!"
}

@DeclareAnimal
class Cat : Animal {
    override val name: String = "cat"
    override val sound: String = "meow!"
}

@DeclareAnimal
class Cow : Animal {
    override val name: String = "cow"
    override val sound: String = "moo!"
}