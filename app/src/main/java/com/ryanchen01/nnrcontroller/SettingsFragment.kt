package com.ryanchen01.nnrcontroller

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.ryanchen01.nnrcontroller.databinding.FragmentSettingsBinding


class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private lateinit var dbHelper: DatabaseHelper
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dbHelper = DatabaseHelper(requireContext())
        val button = view.findViewById<Button>(R.id.button_second)
        val editText = view.findViewById<EditText>(R.id.token_textbox)
        val token = dbHelper.getToken()
        if(token != "") {
            editText.setText(token)
        }
        button.setOnClickListener {
            val newToken = editText.editableText.toString()
            dbHelper.updateToken(newToken)


            val navController = findNavController()
            if (navController.currentDestination?.id == R.id.SettingsFragment) {
                navController.navigate(R.id.action_SettingsFragment_to_FirstFragment)
            }
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
