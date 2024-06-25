package com.graduatio.scanandeat.User.Cart

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.graduatio.scanandeat.Admin.Foods.Foods
import com.graduatio.scanandeat.Config
import com.graduatio.scanandeat.CustomDrawerActivity
import com.graduatio.scanandeat.R
import com.graduatio.scanandeat.Session
import com.graduatio.scanandeat.User.UserActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

var carts: ArrayList<Foods>? = null

class PaymentActivity : CustomDrawerActivity() {


    override val layoutId: Int
        get() = R.layout.activity_payment

    override val customTitle: String?
        get() = "Payment Activity"


    //------------------variables declaration-----------------
    var cardholder: EditText? = null
    var cvv: EditText? = null
    var cardnumber: EditText? = null
    var total: TextView? = null
    var paynow: TextView? = null
    var mDatabase: DatabaseReference? = null
    override var session: Session? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //------------------get Database reference by url-----------------

        mDatabase = FirebaseDatabase.getInstance(Config.FireBASEURL).reference
        session = Session(this)
        cardholder = findViewById(R.id.cardholder);
        cvv = findViewById(R.id.cvv);
        cardnumber = findViewById(R.id.cardnumber);
        total = findViewById(R.id.total);
        paynow = findViewById(R.id.paynow);

        total?.text = CartActivity.total?.text!!

        //----------------------------paynow button click listener that will perform function when clicked --------------------
        paynow?.setOnClickListener {
                var flag = true
                if (cardholder?.text.toString().isEmpty()) {
                    cardholder?.setError("Please Enter Card Holdername")
                    flag = false
                }
                if (cardnumber?.text.toString().isEmpty()) {
                    cardnumber?.setError("Please Enter Card number")
                    flag = false
                }
                if (cvv?.text.toString().isEmpty()) {
                    cvv?.setError("Please Enter CVV")
                    flag = false
                }
                if (cardnumber?.text.toString().length < 16) {
                    cardnumber?.setError("Please Enter valid Card number")
                    flag = false
                }
                if (cvv?.text.toString().length < 3) {
                    cvv?.setError("Please Enter valid CVV")
                    flag = false
                }
                if (flag) {
                    AddOrder();
                }

        }


    }

    //------function that store order data to database -----------
    private fun AddOrder() {


        val uniqueKey: String = ((0..1000000).random()).toString()

        mDatabase!!.child("Orders").child(session?.getString("id")!!).child(uniqueKey.toString())
            .child("total").setValue(CartActivity.total?.text!!.split(" ")[0])

        val sdf = SimpleDateFormat("yyyy MM dd HH:mm:ss", Locale.ENGLISH)
        val currentDateandTime: String = sdf.format(Date())

        mDatabase!!.child("Orders").child(session?.getString("id")!!).child(uniqueKey.toString())
            .child("datetime").setValue(currentDateandTime)

        for (aa in carts!!) {


            val uniqueKey2: String =
                mDatabase!!.child("Orders").child(session?.getString("id")!!)
                    .child(uniqueKey.toString()).child("foods").push().getKey().toString()

            mDatabase!!.child("Orders").child(session?.getString("id")!!)
                .child(uniqueKey.toString()).child("foods").child(uniqueKey2)
                .child("foodid").setValue(aa?.id)

            mDatabase!!.child("Orders").child(session?.getString("id")!!)
                .child(uniqueKey.toString()).child("foods").child(uniqueKey2)
                .child("quantity").setValue(aa?.quantity)

            mDatabase!!.child("Orders").child(session?.getString("id")!!)
                .child(uniqueKey.toString()).child("foods").child(uniqueKey2)
                .child("price").setValue(aa?.price)


        }

        mDatabase!!.child("Cart")
            .child(session?.getString("id")!!).removeValue()

        Toast.makeText(
            this@PaymentActivity,
            "Order Added",
            Toast.LENGTH_SHORT
        ).show()

        val i= Intent(getBaseContext(), UserActivity::class.java)
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(i)
        finish()

    }

}