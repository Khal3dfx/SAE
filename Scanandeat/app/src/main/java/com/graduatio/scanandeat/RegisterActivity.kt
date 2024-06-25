package com.graduatio.scanandeat

import androidx.appcompat.app.AppCompatActivity
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
import java.util.Locale

class RegisterActivity : AppCompatActivity() {

    //------------------variables declaration-----------------
    var phone: EditText? = null
    var password: EditText? = null
    var cpassword: EditText? = null
    var name: EditText? = null
    var add: Button? = null
    var userflag: Boolean = true
    var mDatabase: DatabaseReference? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        //---------------------------get databases reference by database url---------------------------
        mDatabase = FirebaseDatabase.getInstance(Config.FireBASEURL).getReference()
        add = findViewById(R.id.add)
        password = findViewById(R.id.password)
        cpassword = findViewById(R.id.cpassword)
        phone = findViewById(R.id.phone)
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
        //---------------flag to with initial value false when see any edittext empty or with wrong data will be true---------
        var flag: Boolean? = false
        if (name!!.getText().toString().isEmpty()) {
            name!!.setError("Please Enter Name")
            flag = true
        }

        if (phone!!.getText().toString().isEmpty()) {
            phone!!.setError("Please Enter phone Number")
            flag = true
        } else {
            if (phone!!.getText().toString().length != 10) {
                phone!!.setError("Please Enter Valid phone Number")
                flag = true
            } else if (phone!!.getText().toString().get(0) != '0' || phone!!.getText().toString()
                    .get(1) != '5'
            ) {
                phone!!.setError("Please Enter Valid phone Number")
                flag = true
            }
        }

        if (cpassword!!.getText().toString().isEmpty()) {
            cpassword!!.setError("Please Enter Confirm Password")
            flag = true
        }
        if (!(password!!.getText().toString() == cpassword!!.getText().toString())) {
            cpassword!!.setError("Please Enter Check Confirm Password")
            flag = true
        }
        if (password!!.getText().toString().isEmpty()) {
            password!!.setError("Please Enter Password")
            flag = true
        } else {
            if ((password!!.getText().toString() == password!!.getText().toString()
                    .lowercase(Locale.getDefault()))
            ) {
                password!!.setError("Password Must contains Capital and small Letter")
                flag = true
            }
            if (password!!.getText().toString().length < 8) {
                password!!.setError("Password Must contains 8 Letters")
                flag = true
            }
        }
        if (!flag!!) {
            userflag = true
            //-------------------------check if user phone number used before or not-------------------------
            val query: DatabaseReference = mDatabase!!.child("Users")
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                public override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val data: Iterable<DataSnapshot> = dataSnapshot.getChildren()
                    for (dataSnapshot1: DataSnapshot in data) {

                        if ((dataSnapshot1.child("phone").getValue()
                                .toString() == phone!!.getText().toString())
                        ) {
                            Toast.makeText(
                                this@RegisterActivity,
                                "Phone Number Already Used!",
                                Toast.LENGTH_SHORT
                            ).show()
                            userflag = false
                            break
                        }
                    }
                    // -------------------------if phone number not used before not used will add it to data bases-------------------------
                    if (userflag) {
                        val uniqueKey: String =
                            mDatabase!!.child("Users").push().getKey().toString()
                        mDatabase!!.child("Users").child(uniqueKey.toString())
                            .child("name").setValue(name!!.getText().toString())

                        mDatabase!!.child("Users").child(uniqueKey.toString())
                            .child("password").setValue(password!!.getText().toString())
                        mDatabase!!.child("Users").child(uniqueKey.toString())
                            .child("phone").setValue(phone!!.getText().toString())
                        mDatabase!!.child("Users").child(uniqueKey.toString())
                            .child("usertype").setValue("user")

                        Toast.makeText(this@RegisterActivity, "User Registered", Toast.LENGTH_SHORT)
                            .show()
                        finish()
                    }
                }

                public override fun onCancelled(databaseError: DatabaseError) {}
            })
        }
    }
}