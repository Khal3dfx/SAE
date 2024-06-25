package com.graduatio.scanandeat.Admin.Foods

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.storage.images.FirebaseImageLoader
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.graduatio.scanandeat.Admin.Foods.Ingredients.FoodIngredientActivity
import com.graduatio.scanandeat.Config
import com.graduatio.scanandeat.R

class FoodAdapter(private val mContext: Activity, private val itemsList: ArrayList<Foods>?) :
    RecyclerView.Adapter<FoodAdapter.SingleItemRowHolder>() {
    var mDatabase: DatabaseReference? = null

    init {
        mDatabase = FirebaseDatabase.getInstance(Config.FireBASEURL).reference
    }

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        i: Int
    ): SingleItemRowHolder {
        val v =
            LayoutInflater.from(viewGroup.context).inflate(R.layout.item_foods, null)
        return SingleItemRowHolder(v)
    }

    override fun onBindViewHolder(
        holder: SingleItemRowHolder,
        @SuppressLint("RecyclerView") pos: Int
    ) {
        //------------------Binding view and set data -----------------
        val singleItem = itemsList!![pos]
        holder.name.text = singleItem.name
        holder.price.text = singleItem.price + " SAR "
        holder.calories.text = singleItem.calories


        val storage = FirebaseStorage.getInstance()
        val storageReference = storage.reference.child("images/" + singleItem.image)

        Glide.with(mContext)
            .using(FirebaseImageLoader())
            .load(storageReference)
            .placeholder(mContext.resources.getDrawable(R.drawable.logo))
            .into(holder.image)



        holder.delete.setOnClickListener {
            val builder1 = AlertDialog.Builder(mContext)
            builder1.setMessage("Are You Sure You Want To Delete?")
            builder1.setCancelable(true)
            builder1.setPositiveButton(
                "Yes"
            ) { dialog, id -> Delete(singleItem.id, pos) }
            builder1.setNegativeButton(
                "No"
            ) { dialog, id -> dialog.cancel() }
            val alert11 = builder1.create()
            alert11.show()
        }



        holder.ingredient.setOnClickListener {

            food = singleItem

            mContext.startActivity(
                Intent(mContext, FoodIngredientActivity::class.java)
            )


        }
        holder.update.setOnClickListener {

            food = singleItem

            mContext.startActivity(
                Intent(mContext, UpdateFoodActivity::class.java)
            )

        }

    }

    private fun Delete(id: String?, i: Int) {
        mDatabase!!.child("Foods").child(id!!).removeValue()
        Toast.makeText(mContext, "Deleted", Toast.LENGTH_SHORT).show()
        itemsList!!.removeAt(i)
        notifyDataSetChanged()
    }

    //---------- get item count ----------
    override fun getItemCount(): Int {
        return itemsList?.size ?: 0
    }

    inner class SingleItemRowHolder(view: View) : RecyclerView.ViewHolder(view) {
        //------------------variables declaration-----------------

        var name: TextView
        var price: TextView
        var calories: TextView
        var delete: Button
        var update: Button
        var ingredient:Button
        var image :ImageView

        init {
            name = view.findViewById(R.id.name)
            price = view.findViewById(R.id.price)
            calories = view.findViewById(R.id.calories)
            image = view.findViewById(R.id.image)
            delete = view.findViewById(R.id.delete)
            update = view.findViewById(R.id.update)
            ingredient = view.findViewById(R.id.ingredient)
        }
    }
}