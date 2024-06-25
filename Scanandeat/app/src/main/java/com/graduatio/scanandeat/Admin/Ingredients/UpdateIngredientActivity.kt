package com.graduatio.scanandeat.Admin.Ingredients

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.abdeveloper.library.MultiSelectDialog
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

var ingredient: Ingredients? = null


class UpdateIngredientsActivity : CustomDrawerActivity() {


    override val layoutId: Int
        get() = R.layout.activity_update_ingredients

    override val customTitle: String?
        get() = "Update Ingredients"


    var name: EditText? = null
    var update: Button? = null
    var userflag: Boolean = true
    var mDatabase: DatabaseReference? = null

    var selectdesease: EditText? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDatabase = FirebaseDatabase.getInstance(Config.FireBASEURL).getReference()
        update = findViewById(R.id.update)
        name = findViewById(R.id.name)
        selectdesease = findViewById(com.graduatio.scanandeat.R.id.selectdeseas)

        name?.setText(ingredient?.name)
        selectdesease?.setText(ingredient?.deseas)


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
                    .onSubmit(object : MultiSelectDialog.SubmitCallbackListener {
                        override fun onSelected(
                            selectedIds: ArrayList<Int>,
                            selectedNames: ArrayList<String>,
                            dataString: String
                        ) {

                            var selected = ""
                            for (i in 0 until selectedIds.size) {
                                selected = selected + selectedNames[i]+", "
                            }
                            selectdesease?.setText(selected.removeSuffix(", "))


                        }

                        override fun onCancel() {
                            Log.d(ContentValues.TAG, "Dialog cancelled")
                        }
                    })

                multiSelectDialog.show(supportFragmentManager, "multiSelectDialog")

            }
        })

        update?.setOnClickListener(object : View.OnClickListener {
            public override fun onClick(view: View) {
                Update()
            }
        })
    }
    // ---------- function to Update ingredient

    private fun Update() {
        var flag: Boolean? = false
        if (name!!.getText().toString().isEmpty()) {
            name!!.setError("Please Enter Ingredient Name")
            flag = true
        }

        if (selectdesease!!.getText().toString().isEmpty()) {
            selectdesease!!.setError("Please Enter Ingredients Name")
            flag = true
        }


        if (!flag!!) {
            val uniqueKey: String? = ingredient?.id


            mDatabase!!.child("Ingredients").child(uniqueKey.toString())
                .child("name").setValue(name!!.getText().toString())


            mDatabase!!.child("Ingredients").child(uniqueKey.toString())
                .child("deseas").setValue(selectdesease!!.getText().toString())

            Toast.makeText(this@UpdateIngredientsActivity, "Ingredients Updated", Toast.LENGTH_SHORT)
                .show()

            finish()
        }
    }
    //-------------------what to do when open or go back to this activity --------------------
    override fun onResume() {
        super.onResume()

        getData()
    }

    val listitems = ArrayList<MultiSelectModel>()


    //---------------- get  all deseas to pick from it that conflict with food ingredient---------
        private fun getData() {
            listitems.clear()
            val query = mDatabase!!.child("Diseases")
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        val data = dataSnapshot.children

                        for (dataSnapshot1 in data) {
                            val dd = Diseases()
                            dd.id = dataSnapshot1.key.toString()
                            dd.name = dataSnapshot1.child("name").value.toString()
                            val aa = Random.nextInt(180)


                            var bb = MultiSelectModel(aa,dd.name)


                            listitems.add(bb)//---------------add data to array list -----


                        }




                    } else {
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
        }


}