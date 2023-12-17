package com.ryanchen01.nnrcontroller

import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import com.ryanchen01.nnrcontroller.databinding.FragmentFirstBinding
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val constraintLayout = view.findViewById<ConstraintLayout>(R.id.firstFragmentConstraintLayout)
        val swipeRefreshLayout = view.findViewById<SwipeRefreshLayout>(R.id.swipeRefreshLayout)

        // Initialize the database helper
        val dbHelper = DatabaseHelper(requireContext())
        swipeRefreshLayout.setOnRefreshListener {
            if (dbHelper.getToken() != "") {
                refreshRuleContent(requireContext())
                constraintLayout.removeAllViews()
            } else {
                Snackbar.make(view, "Please set your token first", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
                swipeRefreshLayout.isRefreshing = false
                return@setOnRefreshListener
            }
            // Refresh the content
            val itemList = dbHelper.getRules()
            itemList.forEachIndexed { index, item ->
                val cardView = LayoutInflater.from(context).inflate(R.layout.item_card_view, constraintLayout, false)
                if(item == "None") {
                    cardView.findViewById<TextView>(R.id.textViewName).text = "None"
                    cardView.findViewById<TextView>(R.id.textViewFrom).text = "Refresh or add rules"
                    cardView.findViewById<TextView>(R.id.textViewTo).text = ""
                    cardView.id = View.generateViewId()
                    val layoutParams = ConstraintLayout.LayoutParams(
                        ConstraintLayout.LayoutParams.MATCH_PARENT,
                        300
                    )

                    if (index > 0) {
                        // For subsequent items, set constraints relative to the previous item
                        layoutParams.topToBottom = constraintLayout.getChildAt(index - 1).id
                        layoutParams.topMargin = 16 // Adjust the margin as needed
                    }

                    cardView.layoutParams = layoutParams
                    constraintLayout.addView(cardView)
                    return@forEachIndexed
                }
                // Set content for your card view here, for example, setting a text view
                val jsonObject = JSONObject(item)
                val name = jsonObject.getString("name")
                val fromText = "From: \t" + jsonObject.getString("host") + ":" + jsonObject.getString("port")
                val toText = "To: \t" + jsonObject.getString("remote") + ":" + jsonObject.getString("rport")
                cardView.findViewById<TextView>(R.id.textViewName).text = name
                cardView.findViewById<TextView>(R.id.textViewFrom).text = fromText
                cardView.findViewById<TextView>(R.id.textViewTo).text = toText
                cardView.id = View.generateViewId()
                val layoutParams = ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.MATCH_PARENT,
                    300
                )

                if (index > 0) {
                    // For subsequent items, set constraints relative to the previous item
                    layoutParams.topToBottom = constraintLayout.getChildAt(index - 1).id
                    layoutParams.topMargin = 16 // Adjust the margin as needed
                }

                cardView.layoutParams = layoutParams
                constraintLayout.addView(cardView)
            }
            Snackbar.make(view, "Refresh successful", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
            swipeRefreshLayout.isRefreshing = false
            return@setOnRefreshListener
        }
        val itemList = dbHelper.getRules()
        itemList.forEachIndexed { index, item ->
            val cardView = LayoutInflater.from(context).inflate(R.layout.item_card_view, constraintLayout, false)
            if(item == "None") {
                cardView.findViewById<TextView>(R.id.textViewName).text = "None"
                cardView.findViewById<TextView>(R.id.textViewFrom).text = "Refresh or add rules"
                cardView.findViewById<TextView>(R.id.textViewTo).text = ""
                cardView.id = View.generateViewId()
                val layoutParams = ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.MATCH_PARENT,
                    300
                )

                if (index > 0) {
                    // For subsequent items, set constraints relative to the previous item
                    layoutParams.topToBottom = constraintLayout.getChildAt(index - 1).id
                    layoutParams.topMargin = 16 // Adjust the margin as needed
                }

                cardView.layoutParams = layoutParams
                constraintLayout.addView(cardView)
                return@forEachIndexed
            }
            // Set content for your card view here, for example, setting a text view
            val jsonObject = JSONObject(item)
            val name = jsonObject.getString("name")
            val fromText = "From: \t" + jsonObject.getString("host") + ":" + jsonObject.getString("port")
            val toText = "To: \t" + jsonObject.getString("remote") + ":" + jsonObject.getString("rport")
            cardView.findViewById<TextView>(R.id.textViewName).text = name
            cardView.findViewById<TextView>(R.id.textViewFrom).text = fromText
            cardView.findViewById<TextView>(R.id.textViewTo).text = toText
            cardView.id = View.generateViewId()
            val layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                300
            )

            if (index > 0) {
                // For subsequent items, set constraints relative to the previous item
                layoutParams.topToBottom = constraintLayout.getChildAt(index - 1).id
                layoutParams.topMargin = 16 // Adjust the margin as needed
            }

            cardView.layoutParams = layoutParams

            cardView.setOnClickListener { view ->
                // Copy fromText to clipboard
                val clipboard = context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = android.content.ClipData.newPlainText("Copied Text", cardView.findViewById<TextView>(R.id.textViewFrom).text.subSequence(7, cardView.findViewById<TextView>(R.id.textViewFrom).text.length))
                clipboard.setPrimaryClip(clip)
                Snackbar.make(view, "Copied" + cardView.findViewById<TextView>(R.id.textViewFrom).text.subSequence(7, cardView.findViewById<TextView>(R.id.textViewFrom).text.length), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            }

            cardView.setOnLongClickListener(View.OnLongClickListener {
                // User chose the "Settings" item, go to second fragment if currently on first fragment
                val navController = findNavController()
                if (navController.currentDestination?.id == R.id.FirstFragment) {
                    val bundle = Bundle()
                    bundle.putString("rid", jsonObject.getString("rid"))
                    navController.navigate(R.id.action_FirstFragment_to_EditFragment, bundle)
                }

                return@OnLongClickListener true
            })
            constraintLayout.addView(cardView)
        }

    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}