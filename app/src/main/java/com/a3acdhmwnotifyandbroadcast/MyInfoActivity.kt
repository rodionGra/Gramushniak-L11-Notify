package com.a3acdhmwnotifyandbroadcast

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.a3acdhmwnotifyandbroadcast.databinding.ActivityMainBinding
import com.a3acdhmwnotifyandbroadcast.databinding.ActivityMyInfoBinding

class MyInfoActivity : AppCompatActivity() {

    companion object {
        private const val PERSON_NAME = "PERSON_NAME"
        private const val PERSON_AGE = "PERSON_AGE"

        fun start(context: Context, name: String, age: Int) {
            val intent = Intent(context, MyInfoActivity::class.java)
            intent.putExtra(PERSON_NAME, name)
            intent.putExtra(PERSON_AGE, age)
            context.startActivity(intent)
        }
    }

    private lateinit var binding: ActivityMyInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupBinding()
        setupData()
    }

    private fun setupBinding() {
        binding = ActivityMyInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }


    private fun setupData() {
        val name = intent.getStringExtra(PERSON_NAME)
        val age = intent.getIntExtra(PERSON_AGE, 0)
        binding.tvInfoPerson.text = "Name: $name ,age: $age"
    }
}