package ru.netology.nmedia.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.BASE_URL
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentAttachmentBinding
import ru.netology.nmedia.handler.loadContentImage
import ru.netology.nmedia.utils.StringArg

class AttachmentFragment(): Fragment() {

    companion object {
        var Bundle.urlArg: String? by StringArg

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentAttachmentBinding.inflate(
            inflater,
            container,
            false,
        )

        binding.apply {
            if(arguments?.urlArg != null) {
                attachmentImg.loadContentImage("${BASE_URL}/media/${arguments?.urlArg}")
            }

            requireActivity().addMenuProvider(
                object : MenuProvider {
                    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                        menuInflater.inflate(R.menu.image_menu, menu)
                    }

                    override fun onMenuItemSelected(menuItem: MenuItem): Boolean =
                        when(menuItem.itemId) {
                            R.id.cancel -> {
                                findNavController().navigateUp()
                                true
                            }
                            else -> false
                        }
                }
            )
        }

        return binding.root
    }
}