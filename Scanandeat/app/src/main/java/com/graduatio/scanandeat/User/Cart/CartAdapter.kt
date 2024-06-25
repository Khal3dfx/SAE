package com.graduatio.scanandeat.User.Cart

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.storage.images.FirebaseImageLoader
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.graduatio.scanandeat.Admin.Foods.Foods
import com.graduatio.scanandeat.Config
import com.graduatio.scanandeat.R
import com.graduatio.scanandeat.Session
import com.graduatio.scanandeat.User.Food.FoodDetailsActivity
import com.graduatio.scanandeat.User.Food.food
import com.travijuu.numberpicker.library.Enums.ActionEnum
import com.travijuu.numberpicker.library.Interface.ValueChangedListener
import com.travijuu.numberpicker.library.NumberPicker

class CartAdapter(private val mContext: Activity, private val itemsList: ArrayList<Foods>?) :
    RecyclerView.Adapter<CartAdapter.SingleItemRowHolder>() {
    var mDatabase: DatabaseReference? = null
    var  session: Session? = null

    init {
        mDatabase = FirebaseDatabase.getInstance(Config.FireBASEURL).reference
        session= Session(context = mContext);

    }

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        i: Int
    ): SingleItemRowHolder {
        val v =
            LayoutInflater.from(viewGroup.context).inflate(R.layout.item_cart, null)
        return SingleItemRowHolder(v)
    }

    override fun onBindViewHolder(
        holder: SingleItemRowHolder,
        @SuppressLint("RecyclerView") pos: Int
    ) {
        val singleItem = itemsList!![pos]
        holder.name.text = singleItem.name
        holder.price.text = singleItem.price + " SAR "


        val storage = FirebaseStorage.getInstance()
        val storageReference = storage.reference.child("images/" + singleItem.image)

        Glide.with(mContext)
            .using(FirebaseImageLoader())
            .load(storageReference)
            .placeholder(mContext.resources.getDrawable(R.drawable.logo))
            .into(holder.image)



        holder.itemView.setOnClickListener(View.OnClickListener {
            food = singleItem

            mContext.startActivity(
                Intent(mContext, FoodDetailsActivity::class.java)
            )
        })

        holder.numberpicker.value = singleItem.quantity!!.toInt()

        holder.numberpicker?.setValueChangedListener(ValueChangedListener { value, action ->
            if(action == ActionEnum.INCREMENT){

                mDatabase!!.child("Cart")
                    .child(session?.getString("id")!!).child(singleItem?.cartid!!).child("quantity").setValue(value.toString())
                singleItem.quantity = value.toString()
                CartActivity.total?.text = (CartActivity.total?.text!!.split(" ")[0].toString().toFloat() + singleItem?.price!!.toFloat()).toString() + " SAR"
                notifyDataSetChanged()
            }else{




                if(value == 0){

                    mDatabase!!.child("Cart")
                        .child(session?.getString("id")!!).child(singleItem?.cartid!!).removeValue()
                    CartActivity.total?.text = (CartActivity.total?.text!!.split(" ")[0].toString().toFloat() - singleItem?.price!!.toFloat()).toString() + " SAR"
                    itemsList.removeAt(pos)
                    notifyDataSetChanged()
                }else{
                    mDatabase!!.child("Cart")
                        .child(session?.getString("id")!!).child(singleItem?.cartid!!).child("quantity").setValue(value.toString())

                    singleItem.quantity = value.toString()

                    CartActivity.total?.text = (CartActivity.total?.text!!.split(" ")[0].toString().toFloat() - singleItem?.price!!.toFloat()).toString() + " SAR"
                    notifyDataSetChanged()
                }

            }

        })


    }

    override fun getItemCount(): Int {
        return itemsList?.size ?: 0
    }

    inner class SingleItemRowHolder(view: View) : RecyclerView.ViewHolder(view) {
        var name: TextView
        var price: TextView
        var image :ImageView
        var numberpicker: NumberPicker

        init {
            name = view.findViewById(R.id.name)
            price = view.findViewById(R.id.price)
            image = view.findViewById(R.id.image)
            numberpicker = view.findViewById(R.id.numberpicker)

        }
    }
}