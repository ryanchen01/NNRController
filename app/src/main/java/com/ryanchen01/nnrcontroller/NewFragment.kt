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
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.ryanchen01.nnrcontroller.databinding.FragmentNewBinding
import org.json.JSONObject

class NewFragment : Fragment() {
    private var _binding: FragmentNewBinding? = null
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var type_adapter: ArrayAdapter<String>
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentNewBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dbHelper = DatabaseHelper(requireContext())

        val typeList = mutableListOf<String>("tcp", "udp", "tcp+udp")
        val type_spinner = view.findViewById<Spinner>(R.id.new_type_spinner)
        type_adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, typeList)
        type_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        type_spinner.adapter = type_adapter
        type_spinner.setSelection(0)

        val serversList = dbHelper.getServers()
        // Fetch server list
        val status = refreshServerContent(requireContext()) // 0: failed, 1: success
        if (status == 0) {
            Snackbar.make(view, "Cannot fetch server list, please check your network connection", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        val server_spinner = view.findViewById<Spinner>(R.id.new_server_spinner)

        var serverNameList = mutableListOf<String>()
        serversList.forEachIndexed{index, item ->
            if(item != "None") {
                val serverObject = JSONObject(item)
                val serverName = serverObject.getString("name")
                val serverId = serverObject.getString("sid")
                // Add serverName to Spinner
                serverNameList.add(serverName)
            }
        }
        adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, serverNameList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        server_spinner.adapter = adapter
        server_spinner.setSelection(0)


        val saveButton = view.findViewById<Button>(R.id.new_button_save)
        saveButton.setOnClickListener {
            val serverName = server_spinner.selectedItem.toString()
            val serverObject = JSONObject(dbHelper.getServerByName(serverName))
            val ruleObject = JSONObject()
            val remote_ip_textbox = view.findViewById<EditText>(R.id.new_remote_ip)
            remote_ip_textbox.filters = arrayOf(InputFilter { source, _, _, _, _, _ ->
                if (source.isEmpty()) {
                    return@InputFilter null
                }
                val allowedCharacters = "0123456789."
                source.filter { it in allowedCharacters }
            })
            ruleObject.put("sid", serverObject.getString("sid"))
            ruleObject.put("remote", remote_ip_textbox.text.toString())
            ruleObject.put("rport", view.findViewById<EditText>(R.id.new_rport).text.toString().toInt())
            ruleObject.put("type", type_spinner.selectedItem.toString())
            ruleObject.put("name", view.findViewById<EditText>(R.id.new_name_textbox).text.toString())
            ruleObject.put("setting","{\"loadbalanceMode\": \"fallback\"}")
            val status = addRule(requireContext(), ruleObject)
            if (status == 0) {
                Snackbar.make(view, "Cannot add rule, please check your network connection", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            } else {
                Snackbar.make(view, "Rule added", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            }
            val navController = findNavController()
            if (navController.currentDestination?.id == R.id.NewFragment) {
                navController.navigate(R.id.action_NewFragment_to_FirstFragment)
            }

        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}