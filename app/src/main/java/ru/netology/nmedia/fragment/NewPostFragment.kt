package ru.netology.nmedia.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.databinding.FragmentNewPostBinding
import ru.netology.nmedia.utils.AndroidUtils.hideKeyboard
import ru.netology.nmedia.utils.StringArg
import ru.netology.nmedia.viewModel.PostViewModel

class NewPostFragment() : Fragment() {

    companion object {
        var Bundle.textArg: String? by StringArg
    }

    private val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentNewPostBinding.inflate(
            inflater,
            container,
            false,
        )
        binding.content.requestFocus()

        arguments?.textArg?.let(binding.content::setText)

        binding.save.setOnClickListener {
            viewModel.changeContent(binding.content.text.toString())
            viewModel.save()
            hideKeyboard(binding.root)
            findNavController().navigateUp()
        }

        binding.cancelButton.setOnClickListener {
            viewModel.cancelEdit()
            findNavController().navigateUp()
        }

        return binding.root
    }
}