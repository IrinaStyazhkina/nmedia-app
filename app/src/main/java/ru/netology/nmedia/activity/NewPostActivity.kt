package ru.netology.nmedia.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.netology.nmedia.databinding.ActivityNewPostBinding
import ru.netology.nmedia.utils.AndroidUtils.hideKeyboard

class NewPostActivity() : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityNewPostBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.content.requestFocus()
        intent?.let {
            val text = it.getStringExtra(Intent.EXTRA_TEXT)
            binding.content.setText(text)
        }

        binding.save.setOnClickListener {
            val intent = Intent()
            if(binding.content.text.isNullOrBlank()) {
                setResult(Activity.RESULT_CANCELED, intent)
            } else {
                val data = binding.content.text.toString()
                intent.putExtra(Intent.EXTRA_TEXT, data)
                setResult(Activity.RESULT_OK, intent)
            }
            hideKeyboard(binding.root)
            finish()
        }

        binding.cancelButton.setOnClickListener {
            val intent = Intent()
            setResult(Activity.RESULT_CANCELED, intent)
            finish()
        }
    }
}