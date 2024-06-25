package com.graduatio.scanandeat.Admin.Diseases

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

class DiseasesListActivity : CustomDrawerActivity() {


    override val layoutId: Int
        get() = R.layout.activity_diseases_list

    override val customTitle: String?
        get() = "Diseases List"
    //------------------variables declaration-----------------
    var recyclerView: RecyclerView? = null
    var adapter: DiseasesAdapter? = null
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
        adapter = DiseasesAdapter(this@DiseasesListActivity, arrayList)
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
                Intent(baseContext, AddDiseasesActivity::class.java)
            )
        }

    }

    override fun onResume() {
        super.onResume()
        getData()
    }

    //---------------- get Diseas child from data base and check if type oof user admin add it to array list ----------
    private fun getData() {
        arrayList!!.clear()
        val query = mDatabase!!.child("Diseases")
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val data = dataSnapshot.children
                    for (dataSnapshot1 in data) {
                        val dd = Diseases()
                        dd.id = dataSnapshot1.key.toString()
                        dd.name = dataSnapshot1.child("name").value.toString()

                        arrayList!!.add(dd)
                        //---------------add data to array of object list -----
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
        var arrayList: ArrayList<Diseases>? = null
    }
}