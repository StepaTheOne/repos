package com.example.mydialerwithfragment

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil

class DiffCallBack : DiffUtil.ItemCallback<Contact>() {

    override fun areItemsTheSame(oldItem: Contact, newItem: Contact): Boolean =
        oldItem == newItem

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: Contact, newItem: Contact): Boolean =
        oldItem == newItem
}