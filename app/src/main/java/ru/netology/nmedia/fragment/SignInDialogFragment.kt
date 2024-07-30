package ru.netology.nmedia.fragment

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.R

class SignInDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        AlertDialog.Builder(requireContext())
            .setMessage(getString(R.string.sign_in_alert))
            .setPositiveButton(getString(R.string.sign_in)) { dialog,_ ->
                dialog.dismiss()
                findNavController()
                    .navigate(R.id.action_to_signinFragment)
            }
            .setNegativeButton(getString(R.string.close)) {dialog,_ ->
                dialog.dismiss()
            }
            .create()

    companion object {
        const val TAG = "SignInDialog"
    }
}