package com.example.datamask

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import foreground.Worker
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private lateinit var mTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_first, container, false)
        mTextView = v.findViewById(R.id.textview_first)
        mTextView.setMovementMethod(ScrollingMovementMethod())

        return v
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<Button>(R.id.button_first).setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }

        activity?.let { it1 -> WorkManager.getInstance(it1.applicationContext).cancelAllWork() }

        view.findViewById<Button>(R.id.button_first2).setOnClickListener {
            activity?.let { it1 -> WorkManager.getInstance(it1.applicationContext).cancelAllWork() }

            val workRequest = OneTimeWorkRequestBuilder<Worker>()
                .addTag("ISGN")
                .setInitialDelay(1, TimeUnit.SECONDS)
                .build()

            activity?.let { it1 -> WorkManager.getInstance(it1.applicationContext).enqueue(workRequest) }


        }

        Timer().schedule(object : TimerTask() {
            override fun run() {
                activity?.runOnUiThread(Runnable { updateTextView() })
            }
        }, 0, 1000)

    }

    fun updateTextView() {
        mTextView.setText(getFilesList())
    }

    fun getFilesList(): String {

        val dataDir = File(activity?.applicationContext?.filesDir?.absolutePath ?: "/data/user/0", "/data_mask")
        if (!dataDir.exists())
            dataDir.mkdir()

        return dataDir.listFiles()?.asList()?.filter { it.name != "current_file.txt" }?.joinToString(separator = "\n") { it.name }.toString()
    }

    @SuppressLint("SdCardPath")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mTextView.setText(getFilesList())
    }
}