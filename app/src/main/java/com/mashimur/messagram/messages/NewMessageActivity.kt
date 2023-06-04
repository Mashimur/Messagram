package com.mashimur.messagram.messages

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.mashimur.messagram.R
import com.mashimur.messagram.models.User
import com.squareup.picasso.Picasso


class NewMessageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)
        supportActionBar?.title = "Select User"
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview_newmessage)

        val adapter = GroupAdapter<GroupieViewHolder>()
        recyclerView.adapter = adapter
        fetchUsers()
    }

    companion object{
        val USER_KEY = "USER_KEY"
    }

    private fun fetchUsers() {
        val ref = FirebaseDatabase.getInstance().getReference("/users")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val recyclerView = findViewById<RecyclerView>(R.id.recyclerview_newmessage)
                val adapter = GroupAdapter<GroupieViewHolder>()

                dataSnapshot.children.forEach { childSnapshot ->
                    val user = childSnapshot.getValue(User::class.java)
                    if (user != null && user.uid != FirebaseAuth.getInstance().uid) {
                        adapter.add(UserItem(user))
                    }
                }

                adapter.setOnItemClickListener { item, view ->
                    val userItem = item as UserItem
                    val intent = Intent(view.context, ChatLogActivity::class.java)
                    intent.putExtra(USER_KEY, userItem.user)
                    startActivity(intent)
                    finish()
                }

                recyclerView.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}

class UserItem(val user: User) : Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        val usernameTextView = viewHolder.itemView.findViewById<TextView>(R.id.username_textview_new_message)
        usernameTextView.text = user.username

        val imageView = viewHolder.itemView.findViewById<ImageView>(R.id.imageview_new_message)
        Picasso.get().load(user.profileImageUrl).into(imageView)
    }

    override fun getLayout(): Int {
        return R.layout.user_row_new_message
    }
}
//class CustomAdapter: RecyclerView.Adapter<ViewHolder>{
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        TODO("Not yet implemented")
//    }
//}
