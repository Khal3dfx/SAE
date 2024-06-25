package com.graduatio.scanandeat.Admin.Foods.Ingredients

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
import com.graduatio.scanandeat.Admin.Foods.food
import com.graduatio.scanandeat.Admin.Ingredients.Ingredients
import com.graduatio.scanandeat.Config
import com.graduatio.scanandeat.CustomDrawerActivity
import com.graduatio.scanandeat.R
import com.graduatio.scanandeat.Session

class FoodIngredientActivity : CustomDrawerActivity() {


    override val layoutId: Int
        get() = R.layout.activity_ingredient_lfood

    override val customTitle: String?
        get() = "Food Ingredient"

    //------------------variables declaration-----------------
    var recyclerView: RecyclerView? = null
    var adapter: FoodIngredientsAdapter? = null
    var mDatabase: DatabaseReference? = null
    override var session: Session? = null

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
        adapter = FoodIngredientsAdapter(this@FoodIngredientActivity, arrayList)
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

        //----------------------------add floating click listener to add ingredient to food --------------------
        findViewById<View>(R.id.add).setOnClickListener {
            startActivity(
                Intent(baseContext, SelectFoodIngredientActivity::class.java)
            )
        }

    }
    //-------------------what to do when open or go back to this activity --------------------
    override fun onResume() {
        super.onResume()
        getData()
    }

    //---------------- get Food ingredient child from data base and check if type oof user admin add it to array list ----------
    private fun getData() {
        var firstlist =  ArrayList<String>()

        arrayList!!.clear()
        val query = mDatabase!!.child("Foods").child(food?.id.toString()).child("Ingredient")
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val data = dataSnapshot.children
                    for (dataSnapshot1 in data) {


                        firstlist!!.add(dataSnapshot1.key.toString())

                        adapter!!.notifyDataSetChanged()
                    }
                    adapter!!.notifyDataSetChanged()
                } else {
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })

        val query2 = mDatabase!!.child("Ingredients")
        query2.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val data = dataSnapshot.children
                    for (dataSnapshot1 in data) {

                        val dd = Ingredients()
                        dd.id = dataSnapshot1.key.toString()
                        dd.name = dataSnapshot1.child("name").value.toString()
                        dd.deseas = dataSnapshot1.child("deseas").value.toString()
                        if(firstlist.contains(dataSnapshot1.key.toString())) {
                            arrayList!!.add(dd)
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
        var arrayList: ArrayList<Ingredients>? = null
    }
}