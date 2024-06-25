package com.graduatio.scanandeat.Admin.Foods

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.graduatio.scanandeat.Config
import com.graduatio.scanandeat.CustomDrawerActivity
import com.graduatio.scanandeat.R
import java.io.FileNotFoundException
import java.io.InputStream
import java.util.UUID


class AddFoodActivity : CustomDrawerActivity() {


    override val layoutId: Int
    get() = R.layout.activity_add_food

    override val customTitle: String?
    get() = "Add Food"

    //------------------variables declaration-----------------
    var name: EditText? = null
    var calories: EditText? = null
    var price: EditText? = null


    var add: Button? = null
    var mDatabase: DatabaseReference? = null
    var image: ImageView? = null

    private val READ_STORAGE_CODE = 1001
    private val PICK_IMAGE_REQUEST = 234
    var imagepath: Uri? = null
    var storage: FirebaseStorage? = null
    var storageReference: StorageReference? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //---------------------------get storage reference to save image---------------------------
        storage = FirebaseStorage.getInstance()
        storageReference = storage!!.reference

        //---------------------------get databases reference by database url---------------------------
        mDatabase = FirebaseDatabase.getInstance(Config.FireBASEURL).getReference()
        add = findViewById(R.id.add)
        image = findViewById(R.id.image);
        name = findViewById(R.id.name)
        calories = findViewById(R.id.calories)
        price = findViewById(R.id.price)


        //----------------------------image click listener to open gallery to select image --------------------
        image!!.setOnClickListener { imageBrowse() }

        add?.setOnClickListener(object : View.OnClickListener {
            public override fun onClick(view: View) {
                Add()
            }
        })
    }

    private fun Add() {
        var flag: Boolean? = false //-----------flag to with initial value false when see any edittext empty or with wrong data will be true---------
        if (name!!.getText().toString().isEmpty()) {
            name!!.setError("Please Enter Food Name")
            flag = true
        }


        if (calories!!.getText().toString().isEmpty()) {
            calories!!.setError("Please Enter calories")
            flag = true
        }

        if (price!!.getText().toString().isEmpty()) {
            price!!.setError("Please Enter price")
            flag = true
        }

        if (imagepath == null) {
            Toast.makeText(this, "Please Add Food Image", Toast.LENGTH_SHORT).show()
            flag = true
        }

        if (!flag!!) {

            // ---------------get uniqueKey to add child to database-------------------------
            val uniqueKey: String =
                mDatabase!!.child("Foods").push().getKey().toString()

            val a = UUID.randomUUID().toString()
            uploadImage(a, imagepath!!)

            mDatabase!!.child("Foods").child(uniqueKey.toString())
                .child("name").setValue(name!!.getText().toString())


            mDatabase!!.child("Foods").child(uniqueKey.toString())
                .child("price").setValue(price!!.getText().toString())


            mDatabase!!.child("Foods").child(uniqueKey.toString())
                .child("calories").setValue(calories!!.getText().toString())

            mDatabase!!.child("Foods").child(uniqueKey.toString())
                .child("image").setValue(a)


            Toast.makeText(this@AddFoodActivity, "Food Added", Toast.LENGTH_SHORT)
                .show()

            finish()
        }
    }

    // ---------------Upload image to storage-------------------------
    private fun uploadImage(name: String, uri: Uri) {
        val ref = storageReference!!.child("images/$name")
        ref.putFile(uri).addOnSuccessListener {

        }.addOnFailureListener {

        }
    }

    // ---------------Open Image Gallery-------------------------
    private fun imageBrowse() {
        if (isPermissionGranted(Manifest.permission.READ_MEDIA_IMAGES)) {
            val galleryIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST)
        } else if (isPermissionGranted(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            val galleryIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST)
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestPermission(Manifest.permission.READ_MEDIA_IMAGES, READ_STORAGE_CODE)
            } else {
                requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE, READ_STORAGE_CODE)
            }
        }
    }

    private fun isPermissionGranted(permission: String): Boolean {
        val result = ContextCompat.checkSelfPermission(this@AddFoodActivity, permission)
        return if (result == PackageManager.PERMISSION_GRANTED) true else false
    }

    // ---------------Request runtime permission to access photo-------------------------
    private fun requestPermission(permission: String, code: Int) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this@AddFoodActivity,
                permission
            )
        ) {
        }
        ActivityCompat.requestPermissions(this@AddFoodActivity, arrayOf<String>(permission), code)
    }

    // ---------------when goo back from gallery get image uri and view it in image view-------------------------
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST) {
                val imageUri = data?.data
                val imageStream: InputStream?
                try {
                    imageStream = contentResolver.openInputStream(imageUri!!)
                    val selectedImage = BitmapFactory.decodeStream(imageStream)
                    imagepath = imageUri
                    image!!.setImageBitmap(selectedImage)
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                }
            }
        }
    }
}