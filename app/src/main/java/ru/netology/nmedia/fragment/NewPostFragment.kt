package ru.netology.nmedia.fragment

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentNewPostBinding
import ru.netology.nmedia.utils.AndroidUtils.hideKeyboard
import ru.netology.nmedia.utils.StringArg
import ru.netology.nmedia.viewModel.PostViewModel

class NewPostFragment() : Fragment() {

    companion object {
        var Bundle.textArg: String? by StringArg
        var intermediateText: String? = null
    }

    private val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    private val photoLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if(it.resultCode != Activity.RESULT_OK) {
            return@registerForActivityResult
        }

        val uri = requireNotNull(it.data?.data)
        val file = uri.toFile()

        viewModel.setPhoto(uri, file)
    }

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

        if(arguments?.textArg != null) {
            arguments?.textArg?.let(binding.content::setText)
        } else if (intermediateText != null){
            binding.content.setText(intermediateText)
        }

        viewModel.postCreated.observe(viewLifecycleOwner) {
            if (it == true) {
                viewModel.loadPosts()
                intermediateText = null
                findNavController().navigateUp()
            } else {
                Toast.makeText(activity, R.string.error_loading, Toast.LENGTH_LONG).show()
            }
        }

        requireActivity().addMenuProvider(
            object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.save_menu, menu)
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean =
                    when(menuItem.itemId) {
                        R.id.save -> {
                            viewModel.changeContent(binding.content.text.toString())
                            viewModel.save()
                            hideKeyboard(binding.root)
                            true
                        }
                        R.id.cancel -> {
                            viewModel.cancelEdit()
                            intermediateText = null
                            findNavController().navigateUp()
                            true
                        }
                        else -> false
                    }
            },
            viewLifecycleOwner,
        )

        viewModel.photo.observe(viewLifecycleOwner) { photo ->
            if(photo == null) {
                binding.photoContainer.isVisible = false
                return@observe
            }

            binding.photoContainer.isVisible = true
            binding.photo.setImageURI(photo.uri)
        }

        binding.gallery.setOnClickListener{
            ImagePicker.Builder(this)
                .galleryOnly()
                .crop()
                .maxResultSize(2048, 2048)
                .createIntent(photoLauncher::launch)
        }

        binding.takePhoto.setOnClickListener {
            ImagePicker.Builder(this)
                .cameraOnly()
                .crop()
                .maxResultSize(2048, 2048)
                .createIntent(photoLauncher::launch)
        }

        binding.removeAttachment.setOnClickListener {
            viewModel.clearPhoto()
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            requireActivity(),
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    intermediateText = binding.content.text?.toString()
                    findNavController().navigateUp()
                }
            }
        )

        return binding.root
    }
}