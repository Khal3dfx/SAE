package com.graduatio.scanandeat.User.Orders

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.graduatio.scanandeat.Admin.Foods.Foods
import com.graduatio.scanandeat.Config
import com.graduatio.scanandeat.CustomDrawerActivity
import com.graduatio.scanandeat.R
import com.graduatio.scanandeat.Session

class MyOrderActivityActivity : CustomDrawerActivity() {


    override val layoutId: Int
        get() = R.layout.activity_my_order_activity

    override val customTitle: String?
        get() = "My Order"

    //------------------variables declaration-----------------
    var recyclerView: RecyclerView? = null
    var adapter: OrdersAdapter? = null
    var mDatabase: DatabaseReference? = null
    override var session: Session? = null


    //---------------------------appbar back click---------------------------
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
        foodArrayList = ArrayList()
        recyclerView = findViewById(R.id.list)
        adapter = OrdersAdapter(this@MyOrderActivityActivity, arrayList)
        recyclerView?.setLayoutManager(
            LinearLayoutManager(
                baseContext,
                RecyclerView.VERTICAL,
                false
            )
        )
        recyclerView?.setAdapter(adapter)
        recyclerView?.setNestedScrollingEnabled(false)

    }
    //-------------------what to do when open or go back to this activity --------------------
    override fun onResume() {
        super.onResume()
        data
    }
    //---------------- get  all Order to add to arraylist---------
    private val data: Unit

        private get() {
            arrayList!!.clear()
            val query = mDatabase!!.child("Orders").child(session?.getString("id")!!)
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        val data = dataSnapshot.children
                        for (dataSnapshot1 in data) {
                            val dd = Orders()
                            dd.id = dataSnapshot1.key.toString()
                            dd.datetime = dataSnapshot1.child("datetime").value.toString()!!
                            dd.total = dataSnapshot1.child("total").value.toString()!!
                            val data22 = dataSnapshot1.child("foods").children

                            for (dataSnapshot12 in data22) {
                                var aa = Foods()
                                aa.id = dataSnapshot12.child("foodid").value.toString()!!;
                                aa.quantity = dataSnapshot12.child("quantity").value.toString()!!;
                                aa.price = dataSnapshot12.child("price").value.toString()!!;
                                dd?.foods?.add(aa);
                            }

                            arrayList!!.add(dd)

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
        var arrayList: ArrayList<Orders>? = null
        var foodArrayList: ArrayList<Foods>? = null


    }
}