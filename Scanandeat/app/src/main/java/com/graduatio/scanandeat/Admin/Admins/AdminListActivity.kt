package com.graduatio.scanandeat.Admin.Admins

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.graduatio.scanandeat.Config
import com.graduatio.scanandeat.CustomDrawerActivity
import com.graduatio.scanandeat.R
import com.graduatio.scanandeat.Session

class AdminListActivity : CustomDrawerActivity() {


    override val layoutId: Int
        get() = R.layout.activity_admin_list

    override val customTitle: String?
        get() = "Admins"

    //------------------variables declaration-----------------
    var recyclerView: RecyclerView? = null
    var adapter: AdminsAdapter? = null
    var mDatabase: DatabaseReference? = null
    override var session: Session? = null

    //------------------what to do when click on back on app bar -----------------
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //---------------------------get databases reference by database url---------------------------
        mDatabase = FirebaseDatabase.getInstance(Config.FireBASEURL).reference
        session = Session(this)
        arrayList = ArrayList()
        recyclerView = findViewById(R.id.list)
        //------------------call constructor of adapter and send my arraylist to bind it in a list--------
        adapter = AdminsAdapter(this@AdminListActivity, arrayList)
        recyclerView?.setLayoutManager(
            LinearLayoutManager(
                baseContext,
                RecyclerView.VERTICAL,
                false
            )
        )
        //-------------set recyclerview adapter
        recyclerView?.setAdapter(adapter)
        recyclerView?.setNestedScrollingEnabled(false)

        //----------------------------add floating click listener that will perform function when clicked --------------------
        findViewById<View>(R.id.add).setOnClickListener {
            startActivity(
                Intent(baseContext, AddAdminActivity::class.java)
            )
        }

    }

    //-------------------what to do when open or go back to this activity --------------------
    override fun onResume() {
        super.onResume()
        getData()
    }

    //---------------- get users child from data base and check if type oof user admin add it to array list ----------
    private fun getData() {
        arrayList!!.clear()
        val query = mDatabase!!.child("Users")
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val data = dataSnapshot.children
                    for (dataSnapshot1 in data) {
                        val dd = Admins()
                        dd.id = dataSnapshot1.key.toString()
                        dd.name = dataSnapshot1.child("name").value.toString()
                        dd.phone = dataSnapshot1.child("phone").value.toString()
                        dd.usertype = dataSnapshot1.child("usertype").value.toString()
                        dd.password = dataSnapshot1.child("password").value.toString()

                        if (dataSnapshot1.child("usertype").value.toString() == "admin") {
                            if(dd.id?.equals(session?.getString("id")) == false) {
                                arrayList!!.add(dd)
                                //---------------add data to array of object list -----

                            }
                        }
                        adapter!!.notifyDataSetChanged()
                    }
                    adapter!!.notifyDataSetChanged()
                } else {
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    companion object {
        var arrayList: ArrayList<Admins>? = null
    }
}