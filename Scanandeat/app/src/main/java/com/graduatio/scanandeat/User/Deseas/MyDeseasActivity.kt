package com.graduatio.scanandeat.User.Deseas

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.graduatio.scanandeat.Admin.Diseases.Diseases
import com.graduatio.scanandeat.Config
import com.graduatio.scanandeat.CustomDrawerActivity
import com.graduatio.scanandeat.R
import com.graduatio.scanandeat.Session

class MyDeseasActivity : CustomDrawerActivity() {


    override val layoutId: Int
        get() = R.layout.activity_my_diseas

    override val customTitle: String?
        get() = "My Deseases"

    //------------------variables declaration-----------------
    var recyclerView: RecyclerView? = null
    var adapter: DiseasesAdapter? = null
    var mDatabase: DatabaseReference? = null
    override var session: Session? = null

    var cancel: Button? = null
    var save: Button? = null

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    var deseas = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        deseas = ""
        //---------------------------get databases reference by database url---------------------------
        mDatabase = FirebaseDatabase.getInstance(Config.FireBASEURL).reference
        session = Session(this)
        arrayList = ArrayList()
        recyclerView = findViewById(R.id.list)
        cancel = findViewById(R.id.cancel)
        save = findViewById(R.id.save)
        //------------------call constructor of adapter and send my arraylist to bind it in a list--------
        adapter = DiseasesAdapter(this@MyDeseasActivity, arrayList)
        recyclerView?.setLayoutManager(
            LinearLayoutManager(
                baseContext,
                RecyclerView.VERTICAL,
                false
            )
        )
        recyclerView?.setAdapter(adapter)
        recyclerView?.setNestedScrollingEnabled(false)

        cancel?.setOnClickListener(View.OnClickListener {
            finish()
        })

        save?.setOnClickListener(View.OnClickListener {
            deseas = ""
            for (aa in arrayList!!){

                if(aa.checked){
                    deseas = deseas + aa.id+","
                }

            }

            session?.put("deseas",deseas)

            mDatabase!!.child("Users").child((session?.getString("id"))!!).child("deseas")
                .setValue(
                    deseas)

            Toast.makeText(this@MyDeseasActivity, "Deseas Saved", Toast.LENGTH_SHORT)
                .show()
            finish()

        })

    }
    //-------------------what to do when open or go back to this activity --------------------
    override fun onResume() {
        super.onResume()
        data
    }
    //---------------- get Diseas child from data base and check if type oof user admin add it to array list ----------
    private val data: Unit
        private get() {
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
                            var mydeseas = session?.getString("deseas")!!.split(",")
                            for (aa in mydeseas){
                                if(dd.id == aa){
                                    dd.checked = true
                                }
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
        var arrayList: ArrayList<Diseases>? = null
    }
}