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
import java.util.Locale

class UpdatePasswordActivity : CustomDrawerActivity() {


    override val layoutId: Int
        get() = R.layout.activity_update_password

    override val customTitle: String?
        get() = "Update Password"

    //----------------variable declaration---------
    var newpassword: EditText? = null
    var cpassword: EditText? = null
    var oldpassword: EditText? = null
    var update: Button? = null
    override var session: Session? = null
    var mDatabase: DatabaseReference? = null
    public override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        session = Session(this@UpdatePasswordActivity)
        mDatabase = FirebaseDatabase.getInstance(Config.FireBASEURL).getReference()
        newpassword = findViewById(R.id.newpassword)
        cpassword = findViewById(R.id.cpassword)
        oldpassword = findViewById(R.id.oldpassword)
        update = findViewById(R.id.update)
        //-----------Update button click listener that will perform function when clicked --------------------
        update?.setOnClickListener(object : View.OnClickListener {
            public override fun onClick(view: View) {
                ChangePassword()
            }
        })
    }
    //-------------------------function that's Update Password------------------------

    private fun ChangePassword() {
        //---------------------check if password fields is empty ooor not------------------------

        if (oldpassword!!.getText().toString().isEmpty()) {
            oldpassword!!.setError("Please Enter Old Password")
        } else if (newpassword!!.getText().toString().isEmpty()) {
            newpassword!!.setError("Please Enter New Password")
        } else {
            if (newpassword!!.getText().toString().length < 8) {
                newpassword!!.setError("New Password Must Contains 8 Letters at least")
                return
            }
            if ((newpassword!!.getText().toString() == newpassword!!.getText().toString().lowercase(
                    Locale.getDefault()
                ))
            ) {
                newpassword!!.setError("New Password Must Contains Capital and Small Letters")
                return
            }
            if (cpassword!!.getText().toString().isEmpty()) {
                cpassword!!.setError("Please Enter Confirm Password")
                return
            }
            if (!(newpassword!!.getText().toString() == cpassword!!.getText().toString())) {
                cpassword!!.setError("Please Check Confirm Password")
                return
            }

            //-------------------------get User Current password------------------------
            val query: DatabaseReference = mDatabase!!.child("Users").child((session?.getString("id"))!!)
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                public override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {

                        //----------------get User Current password equal current password edittext update it -------------------
                        if ((dataSnapshot.child("password").getValue()
                                .toString() == oldpassword!!.getText().toString())
                        ) {
                            mDatabase!!.child("Users").child((session?.getString("id"))!!)
                                .child("password").setValue(
                                    newpassword!!.getText().toString()
                                )
                            Toast.makeText(
                                this@UpdatePasswordActivity,
                                "Password Updated",
                                Toast.LENGTH_SHORT
                            ).show()
                            finish()
                        } else {
                            Toast.makeText(
                                this@UpdatePasswordActivity,
                                "Previous Password Im Correct !!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this@UpdatePasswordActivity,
                            "User Not found!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                public override fun onCancelled(databaseError: DatabaseError) {}
            })
        }
    }
}