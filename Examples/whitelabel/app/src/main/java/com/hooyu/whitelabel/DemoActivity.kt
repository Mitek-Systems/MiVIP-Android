package com.hooyu.whitelabel

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hooyu.whitelabel.databinding.DemoActivityMainBinding

class DemoActivity : AppCompatActivity() {

    private lateinit var binding: DemoActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DemoActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

}