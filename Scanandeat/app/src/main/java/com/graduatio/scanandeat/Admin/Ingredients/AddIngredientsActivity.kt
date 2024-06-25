package com.graduatio.scanandeat.Admin.Ingredients

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.abdeveloper.library.MultiSelectDialog
import com.abdeveloper.library.MultiSelectDialog.SubmitCallbackListener
import com.abdeveloper.library.MultiSelectModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.graduatio.scanandeat.Admin.Diseases.Diseases
import com.graduatio.scanandeat.Config
import com.graduatio.scanandeat.CustomDrawerActivity
import com.graduatio.scanandeat.R
import kotlin.random.Random


class AddIngredientsActivity : CustomDrawerActivity() {



    override val layoutId: Int
        get() = R.layout.activity_add_ingredients

    override val customTitle: String?
        get() = "Add Ingredients"

    //------------------variables declaration-----------------
    var name: EditText? = null
    var selectdesease: EditText? = null

    var add: Button? = null
    var mDatabase: DatabaseReference? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //---------------------------get databases reference by database url---------------------------
        mDatabase = FirebaseDatabase.getInstance(Config.FireBASEURL).getReference()
        add = findViewById(R.id.add)
        name = findViewById(R.id.name)


        selectdesease = findViewById(com.graduatio.scanandeat.R.id.selectdeseas)

        selectdesease?.setOnClickListener(object : View.OnClickListener {
            public override fun onClick(view: View) {
                val multiSelectDialog = MultiSelectDialog()
                    .title("Select Deaseas ")
                    .titleSize(25f)
                    .positiveText("Done")
                    .negativeText("Cancel")
                    .setMinSelectionLimit(1)
                    .setMaxSelectionLimit(listitems.size)
                    .multiSelectList(listitems)
                    .onSubmit(object : SubmitCallbackListener {
                        override fun onSelected(
                            selectedIds: ArrayList<Int>,
                            selectedNames: ArrayList<String>,
                            dataString: String
                        ) {
                            var selected = ""

                            for (i in 0 until selectedIds.size) {
                                selected = selected + selectedNames[i] + ", "

                            }
                            selectdesease?.setText(selected.removeSuffix(", "))

                        }

                        override fun onCancel() {
                            Log.d(TAG, "Dialog cancelled")
                        }
                    })

                multiSelectDialog.show(supportFragmentManager, "multiSelectDialog")

            }
        })

        //----------------------------add button click listener that will perform function when clicked --------------------
        add?.setOnClickListener(object : View.OnClickListener {
            public override fun onClick(view: View) {
                Add()
            }
        })

     }
    //-------------------what to do when open or go back to this activity --------------------
    override fun onResume() {
        super.onResume()

        getAll()
    }

    val listitems = ArrayList<MultiSelectModel>()


    //---------------- get  all deseas to pick from it that conflict with food ingredient---------
    private fun getAll() {
        listitems.clear()
        val query = mDatabase!!.child("Diseases")
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val data = dataSnapshot.children

                    for (dataSnapshot1 in data) {
                        val dd = Diseases()
                        val aa = Random.nextInt(180)

                        dd.id = dataSnapshot1.key.toString()
                        dd.name = dataSnapshot1.child("name").value.toString()


                        var bb = MultiSelectModel(aa,dd.name)
                        listitems.add(bb)//---------------add data to array list -----


                    }




                } else {
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }


    // ---------- function to add ingredient to database
    private fun Add() {
        var flag: Boolean? = false
        if (name!!.getText().toString().isEmpty()) {
            name!!.setError("Please Enter Ingredients Name")
            flag = true
        }

        if (selectdesease!!.getText().toString().isEmpty()) {
            selectdesease!!.setError("Please Enter Ingredients Name")
            flag = true
        }


        if (!flag!!) {

            val uniqueKey: String =
                mDatabase!!.child("Ingredients").push().getKey().toString()

            mDatabase!!.child("Ingredients").child(uniqueKey.toString())
                .child("name").setValue(name!!.getText().toString())


            mDatabase!!.child("Ingredients").child(uniqueKey.toString())
                .child("deseas").setValue(selectdesease!!.getText().toString())

            Toast.makeText(this@AddIngredientsActivity, "Ingredients Added", Toast.LENGTH_SHORT)
                .show()

            finish()
        }
    }
}