package com.graduatio.scanandeat.Admin.Ingredients

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.graduatio.scanandeat.Config
import com.graduatio.scanandeat.R

class IngredientsAdapter(private val mContext: Activity, private val itemsList: ArrayList<Ingredients>?) :
    RecyclerView.Adapter<IngredientsAdapter.SingleItemRowHolder>() {
    var mDatabase: DatabaseReference? = null

    init {

        mDatabase = FirebaseDatabase.getInstance(Config.FireBASEURL).reference

    }

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        i: Int
    ): SingleItemRowHolder {
        val v =
            LayoutInflater.from(viewGroup.context).inflate(R.layout.item_ingredients, null)
        return SingleItemRowHolder(v)
    }

    override fun onBindViewHolder(
        holder: SingleItemRowHolder,
        @SuppressLint("RecyclerView") pos: Int
    ) {

        //------------------Binding view and set data -----------------
        val singleItem = itemsList!![pos]

        holder.deseases.text = singleItem.deseas
        holder.name.text = singleItem.name


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


        holder.update.setOnClickListener {


            ingredient = singleItem
            mContext.startActivity(
                Intent(mContext, UpdateIngredientsActivity::class.java)
            )

        }

    }

    private fun Delete(id: String?, i: Int) {

        mDatabase!!.child("Ingredients").child(id!!).removeValue()
        Toast.makeText(mContext, "Deleted", Toast.LENGTH_SHORT).show()
        itemsList!!.removeAt(i)
        notifyDataSetChanged()

    }

    override fun getItemCount(): Int {

        return itemsList?.size ?: 0

    }

    inner class SingleItemRowHolder(view: View) : RecyclerView.ViewHolder(view) {

        var name: TextView
        var deseases: TextView
        var delete: Button
        var update: Button

        init {
            name = view.findViewById(R.id.name)
            deseases = view.findViewById(R.id.deseases)
            delete = view.findViewById(R.id.delete)
            update = view.findViewById(R.id.update)
        }
    }
}