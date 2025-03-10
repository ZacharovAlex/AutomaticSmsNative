package com.example.automaticsms.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.automaticsms.adapters.SmsAdapter
import com.example.automaticsms.databinding.FragmentSmsBinding
import com.example.automaticsms.db.SmsDb
import com.example.automaticsms.sms.SmsItem

class SmsFragment : Fragment(), SmsAdapter.SmsAdapterListener {
    private lateinit var binding: FragmentSmsBinding
    private lateinit var adapter: SmsAdapter
    private lateinit var db: SmsDb

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSmsBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        db = SmsDb.getDb(requireActivity())
        db.getDao().getAllSms().asLiveData().observe(viewLifecycleOwner) { smsList ->
            adapter.setSmsList(smsList)
        }
    }

    private fun setupRecyclerView() {
        adapter = SmsAdapter(this)
        binding.rcView.layoutManager = LinearLayoutManager(activity)
        binding.rcView.adapter = adapter
    }

    override fun onClick(smsItem: SmsItem) {
    //       startActivity(Intent(activity, smsDetails::class.java).putExtra("sms", smsItem))
    }

    companion object {
        @JvmStatic
        fun newInstance() = SmsFragment()
    }
}