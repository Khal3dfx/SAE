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
import com.graduatio.scanandeat.Admin.Ingredients.Ingredients
import com.graduatio.scanandeat.Config
import com.graduatio.scanandeat.CustomDrawerActivity
import com.graduatio.scanandeat.R
import com.graduatio.scanandeat.Session

class SelectFoodIngredientActivity : CustomDrawerActivity() {


    override val layoutId: Int
        get() = R.layout.activity_ingredient_select_food

    override val customTitle: String?
        get() = "Select Food Ingredient"

    //------------------variables declaration-----------------
    var recyclerView: RecyclerView? = null
    var adapter: SelectFoodIngredientsAdapter? = null
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
        adapter = SelectFoodIngredientsAdapter(this@SelectFoodIngredientActivity, arrayList)
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


    //--------------- getAll Ingredients to add it to food ---------
    private fun getData() {
        arrayList!!.clear()
        val query = mDatabase!!.child("Ingredients")
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val data = dataSnapshot.children
                    for (dataSnapshot1 in data) {
                        val dd = Ingredients()
                        dd.id = dataSnapshot1.key.toString()
                        dd.name = dataSnapshot1.child("name").value.toString()
                        dd.deseas = dataSnapshot1.child("deseas").value.toString()

                        //--------------- add Ingredient  to arraylist ---------
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
        var arrayList: ArrayList<Ingredients>? = null
    }
}