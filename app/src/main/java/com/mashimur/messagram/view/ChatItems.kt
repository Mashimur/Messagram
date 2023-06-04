package com.mashimur.messagram.view

import android.widget.ImageView
import android.widget.TextView
import com.mashimur.messagram.R
import com.mashimur.messagram.models.User
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import java.text.SimpleDateFormat

class ChatFromItem(val text: String, val user: User, val timestamp: Long) : Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        val textView = viewHolder.itemView.findViewById<TextView>(R.id.textView_from_row)
        val textView2 = viewHolder.itemView.findViewById<TextView>(R.id.timestamp_from_row)
        textView.text = text
        val sdf = SimpleDateFormat("HH:mm")
        textView2.text = sdf.format(timestamp * 1000)

        val imageView = viewHolder.itemView.findViewById<ImageView>(R.id.imageView_from_row)
        val uri = user.profileImageUrl
        Picasso.get().load(uri).into(imageView)
    }
    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }
}

class ChatToItem(val text: String, val user: User, val timestamp: Long): Item<GroupieViewHolder>(){
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        val textView = viewHolder.itemView.findViewById<TextView>(R.id.textView_to_row)

        val textView2 = viewHolder.itemView.findViewById<TextView>(R.id.textView_time_to_row)
        textView.text = text
        val sdf = SimpleDateFormat("HH:mm")
        textView2.text = sdf.format(timestamp * 1000)

        val imageView = viewHolder.itemView.findViewById<ImageView>(R.id.imageView_to_row)
        textView.text = text

        val uri = user.profileImageUrl
        Picasso.get().load(uri).into(imageView)
    }
    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }
}
