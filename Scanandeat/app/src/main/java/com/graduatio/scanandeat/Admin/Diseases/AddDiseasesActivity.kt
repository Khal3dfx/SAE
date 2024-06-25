package com.graduatio.scanandeat.Admin.Diseases

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.graduatio.scanandeat.Config
import com.graduatio.scanandeat.CustomDrawerActivity
import com.graduatio.scanandeat.R

class AddDiseasesActivity : CustomDrawerActivity() {


    override val layoutId: Int
        get() = R.layout.activity_add_diseases

    override val customTitle: String?
        get() = "Add Diseases"

    //------------------variables declaration-----------------

    var name: EditText? = null
    var add: Button? = null
    var mDatabase: DatabaseReference? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //---------------------------get databases reference by database url---------------------------
        mDatabase = FirebaseDatabase.getInstance(Config.FireBASEURL).getReference()
        add = findViewById(R.id.add)
        name = findViewById(R.id.name)

        //----------------------------add button click listener that will perform function when clicked --------------------

        add?.setOnClickListener(object : View.OnClickListener {
            public override fun onClick(view: View) {
                Add()
            }
        })
    }
    //-------------------------function that's add data To database-------------------------

    private fun Add() {
        var flag: Boolean? = false //-----------flag to with initial value false when see any edittext empty or with wrong data will be true---------
        if (name!!.getText().toString().isEmpty()) {
            name!!.setError("Please Enter Diseases Name")
            flag = true
        }


        if (!flag!!) {

            // ---------------get uniqueKey to add child to database-------------------------
            val uniqueKey: String = name!!.getText().toString()
            mDatabase!!.child("Diseases").child(uniqueKey.toString())
                .child("name").setValue(name!!.getText().toString())

            Toast.makeText(this@AddDiseasesActivity, "Diseases Added", Toast.LENGTH_SHORT)
                .show()

            finish()
        }
    }
}