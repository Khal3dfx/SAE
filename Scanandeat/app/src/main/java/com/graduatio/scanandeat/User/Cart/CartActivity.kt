package com.graduatio.scanandeat.User.Cart

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
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

class CartActivity : CustomDrawerActivity() {


    override val layoutId: Int
        get() = R.layout.activity_cart

    override val customTitle: String?
        get() = "Cart"

    //------------------variables declaration-----------------
    var recyclerView: RecyclerView? = null
    var paynow: Button? = null
    var adapter: CartAdapter? = null
    var mDatabase: DatabaseReference? = null
    override var session: Session? = null


    var totalnum = 0.0;

    //------------------Back click listner on appbar-----------------
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //-----------get database reference -----
        mDatabase = FirebaseDatabase.getInstance(Config.FireBASEURL).reference
        session = Session(this)
        arrayList = ArrayList()
        recyclerView = findViewById(R.id.list)
        total = findViewById(R.id.total)
        adapter = CartAdapter(this@CartActivity, arrayList)
        recyclerView?.setLayoutManager(
            LinearLayoutManager(
                baseContext,
                RecyclerView.VERTICAL,
                false
            )
        )
        recyclerView?.setAdapter(adapter)
        recyclerView?.setNestedScrollingEnabled(false)
        paynow = findViewById(R.id.paynow)
        //----------------------------paynow button click listener that will perform function when clicked --------------------
        paynow?.setOnClickListener(View.OnClickListener {
            if(arrayList!!.isEmpty()){

                Toast.makeText(this@CartActivity, "Cart Empty", Toast.LENGTH_SHORT)
                    .show()


            }else{

                carts = arrayList!!
                startActivity(
                    Intent(baseContext, PaymentActivity::class.java)
                )
            }

        })

    }

    override fun onResume() {
        super.onResume()
        getData()
    }

    // ---------- get food from cart database and view it in list
    private fun getData() {
        arrayList!!.clear()
        totalnum = 0.0;
        var firstlist =  ArrayList<Foods>()
        val query = mDatabase!!.child("Foods")
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val data = dataSnapshot.children
                    for (dataSnapshot1 in data) {
                        val dd = Foods()
                        dd.id = dataSnapshot1.key.toString()
                        dd.name = dataSnapshot1.child("name").value.toString()
                        dd.price = dataSnapshot1.child("price").value.toString()
                        dd.calories = dataSnapshot1.child("calories").value.toString()
                        dd.image = dataSnapshot1.child("image").value.toString()

                        var ingred = "";
                        var Ingredientchild = dataSnapshot1.child("Ingredient").children
                        for (dataSnapshot2 in Ingredientchild) {

                            ingred = ingred + dataSnapshot2.key.toString()  + ","

                        }

                        dd.ingredient = ingred

                        firstlist.add(dd)

                        adapter!!.notifyDataSetChanged()
                    }

                    for (aa in firstlist){
                        val query2 = mDatabase!!.child("Cart").child(session?.getString("id")!!)
                        query2.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    val data = dataSnapshot.children
                                    for (dataSnapshot1 in data) {

                                        if(dataSnapshot1.child("foodid").value.toString().equals(aa.id)) {
                                            aa.cartid = dataSnapshot1.key.toString()
                                            aa.id = dataSnapshot1.child("foodid").value.toString()
                                            aa.quantity = dataSnapshot1.child("quantity").value.toString()
                                            totalnum = totalnum + (aa.quantity!!.toFloat() * aa.price!!.toFloat())

                                            arrayList!!.add(aa);
                                        }

                                        adapter!!.notifyDataSetChanged()
                                    }
                                    total?.text = totalnum.toString() + " SAR"
                                    adapter!!.notifyDataSetChanged()
                                } else {
                                }
                            }

                            override fun onCancelled(databaseError: DatabaseError) {}
                        })

                    }


                    adapter!!.notifyDataSetChanged()
                } else {
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })




    }

    // ---------- public data can access them from any class
    companion object {
        var arrayList: ArrayList<Foods>? = null
        var total:TextView? =null
    }
}