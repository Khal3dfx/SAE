package com.graduatio.scanandeat.Admin.Orders

import android.os.Bundle
import android.util.Log
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
import com.graduatio.scanandeat.User.Orders.MyOrderActivityActivity.Companion.arrayList
import com.graduatio.scanandeat.User.Orders.MyOrderActivityActivity.Companion.foodArrayList
import com.graduatio.scanandeat.User.Orders.Orders

class OrdersActivity : CustomDrawerActivity() {


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
        adapter = OrdersAdapter(this@OrdersActivity, arrayList)
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
        getData()
    }
    //---------------- get  all Order to add to arraylist---------

    private fun getData() {
        arrayList!!.clear()
        val query = mDatabase!!.child("Orders")
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val data = dataSnapshot.children
                    for (dataSnapshot1 in data) {

                        val dd = Orders()
                        dd.userid = dataSnapshot1.key.toString()

                        val query2 = mDatabase!!.child("Users").child(dd.userid!!)
                        query2.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    dd.mobile = dataSnapshot.child("phone").value.toString()
                                    dd.username = dataSnapshot.child("name").value.toString()
                                    adapter!!.notifyDataSetChanged()

                                } else {
                                }
                            }

                            override fun onCancelled(databaseError: DatabaseError) {}
                        })


                        val data22 = dataSnapshot.child(dd.userid!!).children
                        for (dataSnapshot122 in data22) {

                            dd.id = dataSnapshot.child(dd.userid!!).child(dataSnapshot122.key.toString()).key.toString()
                            dd.datetime = dataSnapshot122.child("datetime").value.toString()!!
                            dd.total = dataSnapshot122.child("total").value.toString()!!

                            val data222 = dataSnapshot122.child("foods").children

                            var foodslis = ArrayList<String>()
                            for (dataSnapshot1222 in data222) {
                                foodslis.add(dataSnapshot1222.key.toString())
                            }

                            for (details in foodslis) {
                                var aa = Foods()
                                aa.id = dataSnapshot122.child("foods").child(details)
                                    .child("foodid").value.toString()!!;
                                aa.quantity =
                                    dataSnapshot122.child("foods").child(details)
                                        .child("quantity").value.toString()!!;


                                aa.price = dataSnapshot122.child("foods").child(details)
                                    .child("price").value.toString()!!;


                                dd?.foods?.add(aa);

                            }

                            Log.d("foodslis",foodslis.size.toString())

                            arrayList!!.add(dd)
                            adapter!!.notifyDataSetChanged()

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

}