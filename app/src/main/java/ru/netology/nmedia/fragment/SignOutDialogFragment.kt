package ru.netology.nmedia.fragment

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.R
import ru.netology.nmedia.auth.AppAuth
import javax.inject.Inject

class SignOutDialogFragment @Inject constructor(
    private val appAuth: AppAuth
) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        AlertDialog.Builder(requireContext())
            .setMessage(getString(R.string.sign_out_alert))
            .setPositiveButton(getString(R.string.sign_out)) { dialog, _ ->
                appAuth.removeAuth()
                dialog.dismiss()
                findNavController()
                    .navigateUp()
            }
            .setNegativeButton(getString(R.string.close)) { dialog, _ ->
                dialog.dismiss()
            }
            .create()

    companion object {
        const val TAG = "SignOutDialog"
    }
}