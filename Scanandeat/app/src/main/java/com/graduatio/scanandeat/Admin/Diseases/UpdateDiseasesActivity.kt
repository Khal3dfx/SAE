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

var diseases: Diseases? = null


class UpdateDiseasesActivity : CustomDrawerActivity() {


    override val layoutId: Int
        get() = R.layout.activity_update_diseases

    override val customTitle: String?
        get() = "Update Diseases"

    //------------------variables declaration-----------------
    var name: EditText? = null
    var update: Button? = null
    var mDatabase: DatabaseReference? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //---------------------------get databases reference by database url---------------------------
        mDatabase = FirebaseDatabase.getInstance(Config.FireBASEURL).getReference()
        update = findViewById(R.id.update)
        name = findViewById(R.id.name)

        name?.setText(diseases?.name)


        //----------------------------update button click listener that will perform function when clicked --------------------
        update?.setOnClickListener(object : View.OnClickListener {
            public override fun onClick(view: View) {
                Update()
            }
        })
    }


    //-------------------------function that's update data in database-------------------------
    private fun Update() {
        var flag: Boolean? = false //-----------flag to with initial value false when see any edittext empty or with wrong data will be true---------
        if (name!!.getText().toString().isEmpty()) {
            name!!.setError("Please Enter Diseases Name")
            flag = true
        }


        if (!flag!!) {
            val uniqueKey: String? = diseases?.id

            mDatabase!!.child("Diseases").child(uniqueKey.toString())
                .child("name").setValue(name!!.getText().toString())

            Toast.makeText(this@UpdateDiseasesActivity, "Diseases Updated", Toast.LENGTH_SHORT)
                .show()

            finish()
        }
    }
}