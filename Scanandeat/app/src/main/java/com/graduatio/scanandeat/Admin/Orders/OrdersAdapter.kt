package com.graduatio.scanandeat.Admin.Orders

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.graduatio.scanandeat.Admin.Foods.Foods
import com.graduatio.scanandeat.Config
import com.graduatio.scanandeat.R
import com.graduatio.scanandeat.User.Orders.FoodInOrderActivity
import com.graduatio.scanandeat.User.Orders.MyOrderActivityActivity.Companion.foodArrayList
import com.graduatio.scanandeat.User.Orders.Orders

class OrdersAdapter(private val mContext: Activity, private val itemsList: ArrayList<Orders>?) :
    RecyclerView.Adapter<OrdersAdapter.SingleItemRowHolder>() {
    var mDatabase: DatabaseReference? = null

    init {
        mDatabase = FirebaseDatabase.getInstance(Config.FireBASEURL).reference
    }

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        i: Int
    ): SingleItemRowHolder {
        val v =
            LayoutInflater.from(viewGroup.context).inflate(R.layout.item_orders_admin, null)
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

        holder.username.text = singleItem.username
        holder.mobile.text = singleItem.mobile


        holder.details.setOnClickListener {
            var foodArrayList2 = ArrayList<Foods>()

            foodArrayList?.clear()
            foodArrayList2?.addAll(singleItem.foods)

            foodArrayList2?.distinctBy { Pair(it.id, it.name) }
                ?.let { it1 -> foodArrayList?.addAll(it1) }


            Log.d("abooooood size",singleItem.foods.size.toString());


            mContext.startActivity(
                Intent(mContext, FoodInOrderActivity::class.java)
            )

        }

    }

    //---------- get item count ----------
    override fun getItemCount(): Int {
        return itemsList?.size ?: 0
    }

    inner class SingleItemRowHolder(view: View) : RecyclerView.ViewHolder(view) {

        //------------------variables declaration-----------------
        var datetime: TextView
        var orderid: TextView
        var mobile: TextView
        var username: TextView
        var total: TextView
        var details: Button


        init {
            datetime = view.findViewById(R.id.datetime)
            orderid = view.findViewById(R.id.orderid)
            mobile = view.findViewById(R.id.mobile)
            username = view.findViewById(R.id.username)
            total = view.findViewById(R.id.total)
            details = view.findViewById(R.id.details)
        }
    }
}