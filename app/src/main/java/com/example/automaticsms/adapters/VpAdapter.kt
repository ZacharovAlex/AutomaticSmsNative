package com.example.automaticsms.adapters

import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.automaticsms.MainActivity
import com.example.automaticsms.fragments.MainFragment
import com.example.automaticsms.fragments.PushFragment
import com.example.automaticsms.fragments.SmsFragment

class VpAdapter(mainActivity: MainActivity): FragmentStateAdapter(mainActivity) {

    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): androidx.fragment.app.Fragment {
        return if (position == 0) {
            PushFragment.newInstance()
        } else if (position == 1) {
            MainFragment.newInstance()
        } else {
            SmsFragment.newInstance()
        }
    }
}