package com.graduatio.scanandeat

import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.graduatio.scanandeat.Admin.Admins.AdminListActivity
import com.graduatio.scanandeat.User.UserActivity
import java.util.Locale

class MainActivity : AppCompatActivity() {


    //------------------variables declaration-----------------
    var phone: EditText? = null
    var password: EditText? = null
    var login: Button? = null
    var rest: TextView? = null
    var session: Session? = null
    var mDatabase: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //---------------------------app landuage and directions---------------------------
        val conf: Configuration = getResources().getConfiguration()
        conf.setLayoutDirection(Locale("en"))
        getResources().updateConfiguration(conf, getResources().getDisplayMetrics())


        //---------------------------get databases reference by database url---------------------------
        mDatabase = FirebaseDatabase.getInstance(Config.FireBASEURL).getReference()
        session = Session(this)

        //---------------------------check if loogged in or noot and current user role---------------------------
        if (session?.getBoolean("loggedin") == true) {
            if ((session?.getString("usertype") == "admin")) {


                val i= Intent(getBaseContext(), AdminListActivity::class.java)
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(i)
                finish()
            }else if ((session?.getString("usertype") == "user")) {


                val i= Intent(getBaseContext(), UserActivity::class.java)
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(i)
                finish()
            }

        }
        phone = findViewById(R.id.phone)
        password = findViewById(R.id.password)
        login = findViewById(R.id.login)
        rest = findViewById(R.id.rest)
        login?.setOnClickListener(object : View.OnClickListener {
            public override fun onClick(view: View) {
                Login()
            }
        })
        rest?.setOnClickListener(object : View.OnClickListener {
            public override fun onClick(view: View) {
                startActivity(Intent(getBaseContext(), ResetPasswordActivity::class.java))
            }
        })
        (findViewById<View>(R.id.register) as TextView).setOnClickListener(object :
            View.OnClickListener {
            public override fun onClick(view: View) {
                startActivity(Intent(getBaseContext(), RegisterActivity::class.java))
            }
        })
    }

    private fun Login() {
        //-------- check phone number and password not empty
        if (!phone!!.getText().toString().equals("") && !password!!.getText().toString().equals("")) {
            //-------- check if database contains user whit entered phone number and password
            val query: DatabaseReference = mDatabase!!.child("Users")
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                public override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        val data: Iterable<DataSnapshot> = dataSnapshot.getChildren()
                        for (dataSnapshot1: DataSnapshot in data) {
                            if (((dataSnapshot1.child("phone").getValue()
                                    .toString() == phone!!.getText()
                                    .toString()) && (dataSnapshot1.child("password").getValue()
                                    .toString() == password!!.getText().toString()))
                            ) {
                                //-------- if database contains user whit entered phone number and password save it to shared preferences
                                val usertype: String =
                                    dataSnapshot1.child("usertype").getValue().toString()
                                session?.put("id",dataSnapshot1.getKey().toString())
                                session?.put("name",dataSnapshot1.child("name").getValue().toString())
                                session?.put("phone",dataSnapshot1.child("phone").getValue().toString())
                                session?.put("deseas",dataSnapshot1.child("deseas").getValue().toString())
                                session?.put("loggedin",true)
                                session?.put("usertype",usertype)


                                if ((usertype == "admin")) {
                                    Toast.makeText(
                                        this@MainActivity,
                                        "Logged In",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    val i: Intent =
                                        Intent(getBaseContext(), AdminListActivity::class.java)
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                    startActivity(i)
                                    finish()
                                }else if ((usertype == "user")) {
                                    Toast.makeText(
                                        this@MainActivity,
                                        "Logged In",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    val i: Intent =
                                        Intent(getBaseContext(), UserActivity::class.java)
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                    startActivity(i)
                                    finish()
                                }

                            }
                        }
                        if (session?.getBoolean("loggedin") == false) {
                            Toast.makeText(
                                this@MainActivity,
                                "Username or Password incorrect!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                public override fun onCancelled(databaseError: DatabaseError) {}
            })
        } else if (phone!!.getText().toString().equals("")) {
            phone!!.setError("Please Enter E-mail Address")
        } else if (password!!.getText().toString().equals("")) {
            password!!.setError("Please Enter Password")
        }
    }
}