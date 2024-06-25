package com.graduatio.scanandeat.User.Food

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.firebase.ui.storage.images.FirebaseImageLoader
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.graduatio.scanandeat.Admin.Foods.Foods
import com.graduatio.scanandeat.Admin.Ingredients.Ingredients
import com.graduatio.scanandeat.Config
import com.graduatio.scanandeat.CustomDrawerActivity
import com.graduatio.scanandeat.R
import com.graduatio.scanandeat.Session
import com.travijuu.numberpicker.library.Interface.ValueChangedListener
import com.travijuu.numberpicker.library.NumberPicker


var food: Foods? = null

class FoodDetailsActivity : CustomDrawerActivity() {


    override val layoutId: Int
        get() = R.layout.activity_food_details

    override val customTitle: String?
        get() = "Food Details"



    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    //------------------variables declaration-----------------
    var name: TextView? = null
    var price: TextView? = null
    var calories: TextView? = null
    var ingredient: TextView? = null
    var image : ImageView? = null
    var addToCart : Button? = null
    var numberpicker:NumberPicker? = null
    var mDatabase: DatabaseReference? = null
    var deseas = "";

    var quanvalue = 1
    override var session: Session? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //---------------------------get databases reference by database url---------------------------
        mDatabase = FirebaseDatabase.getInstance(Config.FireBASEURL).reference


        name = findViewById(R.id.name)
        numberpicker = findViewById(R.id.numberpicker)
        price = findViewById(R.id.price)
        image = findViewById(R.id.image)
        calories = findViewById(R.id.calories)
        ingredient = findViewById(R.id.ingredient)
        addToCart = findViewById(R.id.addToCart);

        session = Session(this)


        name!!.text = food?.name
        price!!.text = food?.price + " SAR "
        calories!!.text = food?.calories


        val storage = FirebaseStorage.getInstance()
        val storageReference = storage.reference.child("images/" + food?.image)

        Glide.with(baseContext)
            .using(FirebaseImageLoader())
            .load(storageReference)
            .placeholder(resources.getDrawable(R.drawable.logo))
            .into(image)


        addToCart?.setOnClickListener(View.OnClickListener {
            var mydeseas = session?.getString("deseas")!!.split(",")
            var fooddeseas = deseas.split(",")

            Log.d("abooooood mydeseas",mydeseas.toString())
            Log.d("abooooood fooddeseas",fooddeseas.toString())

            var flag = false
            for (aa in fooddeseas) {
                if(mydeseas.contains(aa.trim())  && !aa.equals("")){
                    flag = true
                    val builder1 = AlertDialog.Builder(this@FoodDetailsActivity)
                    builder1.setTitle("This food contains an ingredient that interferes with your disease")
                    builder1.setMessage("Are You want to add food to cart?")
                    builder1.setCancelable(true)
                    builder1.setPositiveButton(
                        "Yes"
                    ) { dialog, id -> AddToCart() }
                    builder1.setNegativeButton(
                        "No"
                    ) { dialog, id -> dialog.cancel() }


                    val alert11 = builder1.create()
                    alert11.show()
                    break
                }
            }
            if(flag == false){
                val builder1 = AlertDialog.Builder(this@FoodDetailsActivity)
                builder1.setMessage("Are You want to add food to cart?")
                builder1.setCancelable(true)
                builder1.setPositiveButton(
                    "Yes"
                ) { dialog, id -> AddToCart() }
                builder1.setNegativeButton(
                    "No"
                ) { dialog, id -> dialog.cancel() }
                val alert11 = builder1.create()
                alert11.show()
            }

        })

        numberpicker?.setValueChangedListener(ValueChangedListener { value, action ->
            quanvalue = value

        })


    }

    //------------------function to add food to cart-----------------
    private fun AddToCart() {


        mDatabase!!.child("Cart")
            .child(session?.getString("id")!!).child(food?.id!!).child("foodid").setValue(food?.id!!)


        mDatabase!!.child("Cart")
            .child(session?.getString("id")!!).child(food?.id!! ).child("quantity").setValue(quanvalue.toString())



        Toast.makeText(
            this@FoodDetailsActivity,
            "Food Added To Cart!",
            Toast.LENGTH_SHORT
        ).show()

        finish()
    }

    //-------------------what to do when open or go back to this activity --------------------
    override fun onResume() {
        super.onResume()
        data
    }

    //---------------- get Ingredients of food from data base and check if type oof user admin add it to array list ----------
    private val data: Unit
        private get() {
            deseas= "";
            val query2 = mDatabase!!.child("Ingredients")
            query2.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        var ingtxt = ""
                        val data = dataSnapshot.children
                        for (dataSnapshot1 in data) {

                            val dd = Ingredients()
                            dd.id = dataSnapshot1.key.toString()
                            dd.name = dataSnapshot1.child("name").value.toString()
                            dd.deseas = dataSnapshot1.child("deseas").value.toString()

                            var ingredientlist = food?.ingredient!!.split(",")

                            if(ingredientlist.contains(dd.id)){

                                ingtxt = ingtxt + dd.name + ","
                                deseas  =deseas + dd.deseas
                            }

                        }

                        ingredient?.text = ingtxt.removeSuffix(",")
                        deseas  =deseas.removeSuffix(",")

                    } else {
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
        }

}