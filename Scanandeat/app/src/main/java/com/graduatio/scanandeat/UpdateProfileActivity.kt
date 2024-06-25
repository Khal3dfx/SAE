package com.graduatio.scanandeat

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class UpdateProfileActivity : CustomDrawerActivity() {


    override val layoutId: Int
        get() = R.layout.activity_update_profile

    override val customTitle: String?
        get() = "Update Profile"

    //----------------variable declaration---------
    var phone: EditText? = null
    var name: EditText? = null

    var update: Button? = null
    override var session: Session? = null

    var mDatabase: DatabaseReference? = null
    public override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        session = Session(this)
        mDatabase = FirebaseDatabase.getInstance(Config.FireBASEURL).getReference()


        update = findViewById(R.id.update)
        phone = findViewById(R.id.phone)
        name = findViewById(R.id.name)

        name?.setText(session?.getString("name"))

        phone?.setText(session?.getString("phone"))

        //-----------Update button click listener that will perform function when clicked --------------------
        update?.setOnClickListener(object : View.OnClickListener {
            public override fun onClick(view: View) {
                Update()
            }
        })
    }
    //-------------------------function that's Update User------------------------

    private fun Update() {
        //---------------flag to with initial value false when see any edittext empty or with wrong data will be true---------
        var flag: Boolean? = false
        if (name!!.getText().toString().isEmpty()) {
            name!!.setError("Please Enter Name")
            flag = true
        }

        if (phone!!.getText().toString().isEmpty()) {
            phone!!.setError("Please Enter Phone number")
            flag = true
        } else {
            if (phone!!.getText().toString().length != 10) {
                phone!!.setError("Please Enter Valid Phone number")
                flag = true
            } else if (phone!!.getText().toString().get(0) != '0' || phone!!.getText().toString()
                    .get(1) != '5'
            ) {
                phone!!.setError("Please Enter Valid Phone number")
                flag = true
            }
        }
        if (!flag!!) {
            val query: DatabaseReference = mDatabase!!.child("Users")
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                public override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        val data: Iterable<DataSnapshot> = dataSnapshot.getChildren()
                        var flag: Boolean? = false
                        //-------------------------check if user phone number used before or not-------------------------
                        for (dataSnapshot1: DataSnapshot in data) {
                            if (((dataSnapshot1.child("mobile").getValue()
                                    .toString() == phone!!.getText()
                                    .toString()) && !(dataSnapshot1.getKey()
                                    .toString() == session?.getString("id")))
                            ) {
                                flag = true
                                if ((dataSnapshot1.child("phone").getValue()
                                        .toString() == phone!!.getText().toString())
                                ) {
                                    Toast.makeText(
                                        this@UpdateProfileActivity,
                                        "Phone Number Already used!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                break
                            }
                        }
                        if (!flag!!) {
                            // -------------------------if phone number not used before not used will Update it to data bases-------------------------
                            mDatabase!!.child("Users").child((session?.getString("id"))!!).child("name")
                                .setValue(
                                    name!!.getText().toString()
                                )
                            mDatabase!!.child("Users").child((session?.getString("id"))!!).child("phone")
                                .setValue(
                                    phone!!.getText().toString()
                                )
                            session?.put("name",name!!.getText().toString())
                            session?.put("phone",phone!!.getText().toString())


                            Toast.makeText(getBaseContext(), "Profile Updated", Toast.LENGTH_SHORT)
                                .show()
                            finish()
                        }
                    }
                }

                public override fun onCancelled(databaseError: DatabaseError) {}
            })
        }
    }
}
