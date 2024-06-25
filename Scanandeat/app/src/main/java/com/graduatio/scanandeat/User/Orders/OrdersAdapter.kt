package com.graduatio.scanandeat.User.Orders

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.graduatio.scanandeat.Config
import com.graduatio.scanandeat.R
import com.graduatio.scanandeat.User.Orders.MyOrderActivityActivity.Companion.foodArrayList

class OrdersAdapter(private val mContext: Activity, private val itemsList: ArrayList<Orders>?) :
    RecyclerView.Adapter<OrdersAdapter.SingleItemRowHolder>() {
    // ----------- constructor
    var mDatabase: DatabaseReference? = null

    init {
        mDatabase = FirebaseDatabase.getInstance(Config.FireBASEURL).reference
    }

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        i: Int
    ): SingleItemRowHolder {
        val v =
            LayoutInflater.from(viewGroup.context).inflate(R.layout.item_orders_user, null)
        return SingleItemRowHolder(v)
    }

    override fun onBindViewHolder(
        holder: SingleItemRowHolder,
        @SuppressLint("RecyclerView") pos: Int
    ) {

        //------------------Binding view and set data -----------------
        val singleItem = itemsList!![pos]
        holder.datetime.text = singleItem.datetime
        holder.total.text = singleItem.total + " SAR"
        holder.orderid.text = singleItem.id



        holder.details.setOnClickListener {

            foodArrayList?.clear()
            foodArrayList?.addAll(singleItem.foods)
            mContext.startActivity(
                Intent(mContext, FoodInOrderActivity::class.java)
            )

        }

    }


    override fun getItemCount(): Int {
        return itemsList?.size ?: 0
    }

    inner class SingleItemRowHolder(view: View) : RecyclerView.ViewHolder(view) {

        //------------------variables declaration-----------------
        var datetime: TextView
        var orderid: TextView
        var total: TextView
        var details: Button

        init {
            datetime = view.findViewById(R.id.datetime)
            orderid = view.findViewById(R.id.orderid)
            total = view.findViewById(R.id.total)
            details = view.findViewById(R.id.details)
        }
    }
}