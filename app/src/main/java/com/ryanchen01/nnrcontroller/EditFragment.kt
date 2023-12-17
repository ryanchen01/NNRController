package com.ryanchen01.nnrcontroller

import android.os.Bundle
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.ryanchen01.nnrcontroller.databinding.FragmentEditBinding
import org.json.JSONObject

class EditFragment : Fragment() {
    private var _binding: FragmentEditBinding? = null
    private lateinit var dbHelper: DatabaseHelper

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentEditBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var ruleObject = JSONObject()

        val rid = arguments?.getString("rid") ?: "None"
        dbHelper = DatabaseHelper(requireContext())
        val ruleString = dbHelper.getRule(rid)
        if (dbHelper.getToken() == "") {
            Snackbar.make(view, "Cannot fetch server list, please set your token first", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        // Fetch server list
        val status = refreshServerContent(requireContext()) // 0: failed, 1: success
        if (status == 0) {
            Snackbar.make(view, "Cannot fetch server list, please check your network connection", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        if (ruleString != "None") {
            ruleObject = JSONObject(ruleString)
            val name = ruleObject.getString("name")
            val remote = ruleObject.getString("remote")
            val port = ruleObject.getString("rport")
            view.findViewById<EditText>(R.id.name_textbox).setText(name)
            view.findViewById<EditText>(R.id.remote_ip).setText(remote)
            view.findViewById<EditText>(R.id.rport).setText(port)
            val server = dbHelper.getServerBySID(ruleObject.getString("sid"))
            if (server != "None") {
                val serverObject = JSONObject(server)
                val serverName = serverObject.getString("name")
                view.findViewById<TextView>(R.id.server_label).text = "Server: " + serverName

            }
        }
        val editbutton = view.findViewById<Button>(R.id.button_edit)
        val navController = findNavController()
        editbutton.setOnClickListener {
            //val serverName = server_spinner.selectedItem.toString()
            var newRuleObject = JSONObject(ruleObject.toString())
            val remote_ip_textbox = view.findViewById<EditText>(R.id.remote_ip)
            remote_ip_textbox.filters = arrayOf(InputFilter { source, _, _, _, _, _ ->
                if (source.isEmpty()) {
                    return@InputFilter null
                }
                val allowedCharacters = "0123456789."
                source.filter { it in allowedCharacters }
            })
            newRuleObject.put("name", view.findViewById<EditText>(R.id.name_textbox).text.toString())
            newRuleObject.put("remote", remote_ip_textbox.text.toString())
            newRuleObject.put("rport", view.findViewById<EditText>(R.id.rport).text.toString().toInt())
            val status = editRule(requireContext(), newRuleObject)
            if (status == 0) {
                Snackbar.make(view, "Cannot edit rule, please check your network connection", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            } else {
                Snackbar.make(view, "Edit successful", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            }

            if (navController.currentDestination?.id == R.id.EditFragment) {
                navController.navigate(R.id.action_EditFragment_to_FirstFragment)
            }
        }

        val deletebutton = view.findViewById<Button>(R.id.button_delete)
        deletebutton.setOnClickListener {
            val status = deleteRule(requireContext(), ruleObject)
            if (status == 0) {
                Snackbar.make(view, "Cannot delete rule, please check your network connection", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            } else {
                Snackbar.make(view, "Delete successful", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            }

            if (navController.currentDestination?.id == R.id.EditFragment) {
                navController.navigate(R.id.action_EditFragment_to_FirstFragment)
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}