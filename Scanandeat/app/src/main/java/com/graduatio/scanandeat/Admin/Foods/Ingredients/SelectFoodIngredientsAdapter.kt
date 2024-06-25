package com.graduatio.scanandeat.Admin.Foods.Ingredients

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.graduatio.scanandeat.Admin.Foods.food
import com.graduatio.scanandeat.Admin.Ingredients.Ingredients
import com.graduatio.scanandeat.Config
import com.graduatio.scanandeat.R

class SelectFoodIngredientsAdapter(private val mContext: Activity, private val itemsList: ArrayList<Ingredients>?) :
    RecyclerView.Adapter<SelectFoodIngredientsAdapter.SingleItemRowHolder>() {
    var mDatabase: DatabaseReference? = null

    init {

        mDatabase = FirebaseDatabase.getInstance(Config.FireBASEURL).reference

    }

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        i: Int
    ): SingleItemRowHolder {
        val v =
            LayoutInflater.from(viewGroup.context).inflate(R.layout.item_select_ingredients_food, null)
        return SingleItemRowHolder(v)
    }

    override fun onBindViewHolder(
        holder: SingleItemRowHolder,
        @SuppressLint("RecyclerView") pos: Int
    ) {


        val singleItem = itemsList!![pos]

        holder.deseases.text = singleItem.deseas
        holder.name.text = singleItem.name


        holder.select.setOnClickListener {
            val builder1 = AlertDialog.Builder(mContext)
            builder1.setMessage("Are You Sure You Want To Select?")
            builder1.setCancelable(true)
            builder1.setPositiveButton(
                "Yes"
            ) { dialog, id -> Select(singleItem.id, pos) }
            builder1.setNegativeButton(
                "No"
            ) { dialog, id -> dialog.cancel() }
            val alert11 = builder1.create()
            alert11.show()
        }
    }


    private fun Select(id: String?, i: Int) {

        mDatabase!!.child("Foods").child(food?.id.toString()).child("Ingredient")
            .child(id.toString()).setValue(id)
        notifyDataSetChanged()
        mContext.finish()


    }

    override fun getItemCount(): Int {

        return itemsList?.size ?: 0

    }

    inner class SingleItemRowHolder(view: View) : RecyclerView.ViewHolder(view) {

        var name: TextView
        var deseases: TextView
        var select: Button

        init {
            name = view.findViewById(R.id.name)
            deseases = view.findViewById(R.id.deseases)
            select = view.findViewById(R.id.select)

        }
    }
}