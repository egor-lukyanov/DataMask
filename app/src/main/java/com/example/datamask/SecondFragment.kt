package com.example.datamask

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import java.io.File


/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {

    private lateinit var mTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val v = inflater.inflate(R.layout.fragment_second, container, false)
        mTextView = v.findViewById(R.id.textview_second)
        mTextView.setMovementMethod(ScrollingMovementMethod())
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dataDir = File(activity?.applicationContext?.filesDir?.absolutePath ?: "/data/user/0", "/data_mask")
        val f = dataDir.listFiles().let {
            if (it?.size!! > 0)
                it.last()
            else
                null
        }
        mTextView.setText("""${f?.nameWithoutExtension ?: "No files..."}
            |
            |${f?.readText() ?: ""}""".trimMargin())

        view.findViewById<Button>(R.id.button_second).setOnClickListener {
            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
        }
    }
}