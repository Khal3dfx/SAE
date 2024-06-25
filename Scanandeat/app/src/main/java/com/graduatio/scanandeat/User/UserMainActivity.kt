package com.graduatio.scanandeat.User

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
import com.graduatio.scanandeat.User.Food.FoodAdapter


class UserMainActivity : CustomDrawerActivity() {


    override val layoutId: Int
        get() = R.layout.activity_user_main

    override val customTitle: String?
        get() = "Foods"

    //----------------variable declaration---------

    var recyclerView: RecyclerView? = null
    var adapter: FoodAdapter? = null
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
        recyclerView = findViewById(R.id.list)
        adapter = FoodAdapter(this@UserMainActivity, arrayList)
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
    //---------------- get  all Food to add to arraylist---------
    private val data: Unit

        private get() {
            arrayList!!.clear()
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
        var arrayList: ArrayList<Foods>? = null
    }
}