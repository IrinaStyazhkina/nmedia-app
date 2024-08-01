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
import ru.netology.nmedia.databinding.FragmentSignupBinding
import ru.netology.nmedia.viewModel.SignupViewModel

@AndroidEntryPoint

class SignUpFragment: Fragment() {

    private val viewModel: SignupViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentSignupBinding.inflate(
            inflater,
            container,
            false,
        )
        binding.editName.requestFocus()

        binding.doRegister.setOnClickListener {
            val name = binding.editName.text.toString()
            if(name == "")
                binding.tvNameError.visibility = View.VISIBLE else {
                binding.tvNameError.visibility = View.GONE
            }

            val login = binding.editLogin.text.toString()
            if(login == "")
                binding.tvLoginError.visibility = View.VISIBLE else {
                binding.tvLoginError.visibility = View.GONE
            }

            val password = binding.editPassword.text.toString()
            if(password == "")
                binding.tvPasswordError.visibility = View.VISIBLE else {
                binding.tvPasswordError.visibility = View.GONE
            }
            val passwordRepeat = binding.editPasswordRepeat.text.toString()
            if(passwordRepeat == "" || password != passwordRepeat)
                binding.tvPasswordRepeatError.visibility = View.VISIBLE else {
                binding.tvPasswordRepeatError.visibility = View.GONE
            }

            if(password != "" && login != "" && name != "" && password == passwordRepeat) {
                viewModel.doRegister(login, password, name)
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