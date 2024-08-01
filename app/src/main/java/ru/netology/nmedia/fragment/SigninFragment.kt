package ru.netology.nmedia.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentSigninBinding
import ru.netology.nmedia.viewModel.SigninViewModel

@AndroidEntryPoint
class SigninFragment: Fragment() {

    private val viewModel: SigninViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentSigninBinding.inflate(
            inflater,
            container,
            false,
        )
        binding.editLogin.requestFocus()

        binding.doLogin.setOnClickListener {
            val username = binding.editLogin.text.toString()
            if(username == "")
                binding.tvLoginError.visibility = View.VISIBLE else {
                binding.tvLoginError.visibility = View.GONE
            }

            val password = binding.editPassword.text.toString()
            if(password == "")
                binding.tvPasswordError.visibility = View.VISIBLE else {
                binding.tvPasswordError.visibility = View.GONE
            }
            if(password != "" && username != "") {
                viewModel.doLogin(username, password)
            }
        }

        viewModel.state.observe(viewLifecycleOwner) { state ->
            binding.progress.isVisible = state.loading
            if(state.error) {
                Snackbar.make(binding.root, R.string.error_sign_in, Snackbar.LENGTH_SHORT)
                    .show()
            }
        }

        viewModel.userAuthorized.observe(viewLifecycleOwner) {authorized ->
            if(authorized) {
                findNavController().navigateUp()
            }
        }

        return binding.root
    }
}