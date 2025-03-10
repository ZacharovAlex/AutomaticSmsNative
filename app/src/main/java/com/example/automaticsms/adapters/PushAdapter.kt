package com.example.automaticsms.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.automaticsms.R
import com.example.automaticsms.databinding.SmsItemBinding
import com.example.automaticsms.push.PushItem
import java.util.Date

class PushAdapter(private val listener: PushAdapterListener) :
    RecyclerView.Adapter<PushAdapter.PushViewHolder>() {
    private val _pushList = ArrayList<PushItem>()

    class PushViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = SmsItemBinding.bind(view)

        @SuppressLint("SetTextI18n")
        fun bind(pushItem: PushItem, listener: PushAdapterListener) = with(binding) {
            smsView.setOnClickListener { listener.onClick(pushItem) }
            tvSmsFrom.text = "От: " + pushItem.packageName+" "+pushItem.messageTitle
            tvSmsDate.text = "Дата: " + Date(pushItem.timeStamp).toString()
            tvSmsBody.text = pushItem.messageText
            if (pushItem.flag<0) imIsSent.setImageResource(R.drawable.ok)
            else imIsSent.setImageResource(R.drawable.cancel)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PushViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.sms_item, parent, false)
        return PushViewHolder(view)
    }

    override fun getItemCount(): Int {
        return _pushList.size
    }

    override fun onBindViewHolder(holder: PushViewHolder, position: Int) {
        holder.bind(_pushList[position], listener)
    }

    fun addPush(item: PushItem) {
        _pushList.add(item)
        // notifyDataSetChanged()
        notifyItemInserted(_pushList.size)
    }

    fun removePush(item: PushItem) {
        _pushList.remove(item)
        // notifyDataSetChanged()
        notifyItemRemoved(_pushList.size)
    }

    fun setPushList(list: List<PushItem>) {
        _pushList.clear()
        _pushList.addAll(list)
        _pushList.sortByDescending { it.id }
        notifyDataSetChanged()
    }

    interface PushAdapterListener {
        fun onClick(pushItem: PushItem) {}
    }
}