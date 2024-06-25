package com.graduatio.scanandeat.User.Deseas

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.graduatio.scanandeat.Admin.Diseases.Diseases
import com.graduatio.scanandeat.Config
import com.graduatio.scanandeat.R
import com.graduatio.scanandeat.Session

class DiseasesAdapter(private val mContext: Activity, private val itemsList: ArrayList<Diseases>?) :
    RecyclerView.Adapter<DiseasesAdapter.SingleItemRowHolder>() {
    var mDatabase: DatabaseReference? = null
    var  sess: Session? = null

    init {
        mDatabase = FirebaseDatabase.getInstance(Config.FireBASEURL).reference
        sess= Session(context = mContext);
    }

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        i: Int
    ): SingleItemRowHolder {
        val v =
            LayoutInflater.from(viewGroup.context).inflate(R.layout.item_diseas_user, null)
        return SingleItemRowHolder(v)
    }

    override fun onBindViewHolder(
        holder: SingleItemRowHolder,
        @SuppressLint("RecyclerView") pos: Int
    ) {
        val singleItem = itemsList!![pos]
        holder.name.text = singleItem.name

        if(singleItem.checked){
            holder.checkbox.isChecked = true
        }else{
            holder.checkbox.isChecked = false
        }

        holder.checkbox.setOnClickListener(View.OnClickListener {
            if(singleItem.checked){
                singleItem.checked = false;
                notifyDataSetChanged()
//                var deseas:String = "";
//
//
//                for (aa in itemsList){
//                    if(aa.checked){
//
//
//                        deseas = deseas + aa.id+","
//                    }
//
//                }
//                sess?.put("deseas",deseas)
//                mDatabase!!.child("Users").child((sess?.getString("id"))!!).child("deseas")
//                    .setValue(
//                        deseas)


            }else{
                singleItem.checked = true;
                notifyDataSetChanged()

//                var deseas:String = "";
//
//                for (aa in itemsList){
//                    if(aa.checked){
//
//
//
//                        deseas = deseas + aa.id+","
//                    }
//
//                }
//
//                sess?.put("deseas",deseas )
//                mDatabase!!.child("Users").child((sess?.getString("id"))!!).child("deseas")
//                    .setValue(
//                        deseas)

            }


        })


    }



    override fun getItemCount(): Int {
        return itemsList?.size ?: 0
    }



    inner class SingleItemRowHolder(view: View) : RecyclerView.ViewHolder(view) {
        var name: TextView
        var checkbox: CheckBox

        init {
            name = view.findViewById(R.id.name)
            checkbox = view.findViewById(R.id.checkbox)
        }
    }
}