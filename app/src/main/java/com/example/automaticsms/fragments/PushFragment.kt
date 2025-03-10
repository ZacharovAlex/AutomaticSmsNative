package com.example.automaticsms.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.automaticsms.adapters.PushAdapter
import com.example.automaticsms.databinding.FragmentPushBinding
import com.example.automaticsms.db.SmsDb
import com.example.automaticsms.push.PushItem

class PushFragment : Fragment(), PushAdapter.PushAdapterListener {
    private lateinit var binding: FragmentPushBinding
    private lateinit var adapter: PushAdapter
    private lateinit var db: SmsDb

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPushBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        db = SmsDb.getDb(requireActivity())
        db.getDao().getAllPush().asLiveData().observe(viewLifecycleOwner) { pushList ->
            adapter.setPushList(pushList)
        }
    }

    private fun setupRecyclerView() {
        adapter = PushAdapter(this)
        binding.rcView.layoutManager = LinearLayoutManager(activity)
        binding.rcView.adapter = adapter
    }

    override fun onClick(pushItem: PushItem) {
     //      startActivity(Intent(activity, PushDetails::class.java).putExtra("push", pushItem))
    }

    companion object {
        @JvmStatic
        fun newInstance() = PushFragment()
    }
}