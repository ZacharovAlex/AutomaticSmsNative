package com.example.automaticsms.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.automaticsms.R
import com.example.automaticsms.databinding.SmsItemBinding
import com.example.automaticsms.sms.SmsItem
import java.util.Date

class SmsAdapter(private val listener: SmsAdapterListener) :
    RecyclerView.Adapter<SmsAdapter.SmsViewHolder>() {
    private val _smsList = ArrayList<SmsItem>()

    class SmsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = SmsItemBinding.bind(view)

        @SuppressLint("SetTextI18n")
        fun bind(smsItem: SmsItem, listener: SmsAdapterListener) = with(binding) {
            smsView.setOnClickListener { listener.onClick(smsItem) }
            tvSmsFrom.text = "От: " + smsItem.address
            tvSmsDate.text = "Дата: " +Date(smsItem.timeStamp).toString()
            tvSmsBody.text = smsItem.messageBody
            if (smsItem.flag<0) imIsSent.setImageResource(R.drawable.ok)
            else imIsSent.setImageResource(R.drawable.cancel)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SmsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.sms_item, parent, false)
        return SmsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return _smsList.size
    }

    override fun onBindViewHolder(holder: SmsViewHolder, position: Int) {
        holder.bind(_smsList[position], listener)
    }

    fun addSms(item: SmsItem) {
        _smsList.add(item)
        // notifyDataSetChanged()
        notifyItemInserted(_smsList.size)
    }

    fun removeSms(item: SmsItem) {
        _smsList.remove(item)
        // notifyDataSetChanged()
        notifyItemRemoved(_smsList.size)
    }

    fun setSmsList(list: List<SmsItem>) {
        _smsList.clear()
        _smsList.addAll(list)
        _smsList.sortByDescending { it.id }
        notifyDataSetChanged()
    }

    interface SmsAdapterListener {
        fun onClick(smsItem: SmsItem) {}
    }
}