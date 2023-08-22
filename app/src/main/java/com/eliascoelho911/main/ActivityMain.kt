package com.eliascoelho911.main

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.eliascoelho911.animal.generated.AnimalProvider
import com.eliascoelho911.ksp.R
import com.eliascoelho911.ksp.databinding.ActivityMainBinding

class ActivityMain : AppCompatActivity(R.layout.activity_main) {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.bind(findViewById(R.id.root))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val animalProvider = AnimalProvider()

        animalProvider.getAll().forEach { animal ->
            val animalButton = AppCompatButton(this)
            animalButton.text = animal.name
            animalButton.setOnClickListener {
                Toast.makeText(this, animal.sound, Toast.LENGTH_SHORT).show()
            }
            binding.animalsContainer.addView(animalButton)
        }

        binding.findBtn.setOnClickListener {
            runCatching {
                animalProvider.get(binding.textField.text.toString())
            }.onSuccess {
                Toast.makeText(this, it.sound, Toast.LENGTH_SHORT).show()
            }.onFailure {
                Toast.makeText(this, "Animal not found", Toast.LENGTH_SHORT).show()
            }
        }
    }
}