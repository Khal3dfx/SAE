package com.graduatio.scanandeat.User

import android.Manifest
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.graduatio.scanandeat.Admin.Foods.Foods
import com.graduatio.scanandeat.Admin.Ingredients.Ingredients
import com.graduatio.scanandeat.Config
import com.graduatio.scanandeat.CustomDrawerActivity
import com.graduatio.scanandeat.R
import com.mindorks.paracamera.Camera
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.UUID


class UserActivity : CustomDrawerActivity() {

    override val layoutId: Int
        get() = R.layout.activity_user

    override val customTitle: String?
        get() = "Home"

    //------------------variables declaration-----------------
    var upload: Button? = null
    var opencamera: Button? = null
    var image: ImageView? = null
    var img: String? = null
    var imgURL: String = ""
    var progressDialog: ProgressDialog? = null
    var resultarrayList: ArrayList<String>? = null
    var quantityarrayList: ArrayList<Int>? = null
    var lastarrayList: ArrayList<String>? = null
    var mDatabase: DatabaseReference? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        resultarrayList = ArrayList()
        quantityarrayList = ArrayList()
        lastarrayList = ArrayList()
        //-----------get database reference -----
        mDatabase = FirebaseDatabase.getInstance(Config.FireBASEURL).reference
        progressDialog= ProgressDialog(this,R.style.AppTheme_Dark_Dialog)


        image = findViewById(R.id.image);
        opencamera = findViewById(R.id.opencamera);
        upload = findViewById(R.id.upload);


        //----------open camera botton click listner to open camera when it clicked--------
        opencamera?.setOnClickListener(View.OnClickListener {

            if (checkStoragePermissions()) {
                TakePicture()
            } else {
                requestForStoragePermissions()
            }

        })

        upload?.isVisible = false

        upload?.setOnClickListener(View.OnClickListener {


            UploadImage();
        })


    }
    //----------check permission to access device storage to save image after capture--------
    fun checkStoragePermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            //----------------Android is 11 (R) or above
            Environment.isExternalStorageManager()
        } else {
            //-----------------Below android 11
            val write =
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            val read =
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            read == PackageManager.PERMISSION_GRANTED && write == PackageManager.PERMISSION_GRANTED
        }
    }

    private val STORAGE_PERMISSION_CODE = 23

    //----------request permission to access device storage to save image after capture--------
    private fun requestForStoragePermissions() {
        //--------------------Android is 11 (R) or above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                val intent = Intent()
                intent.action = Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
                val uri = Uri.fromParts("package", this.packageName, null)
                intent.data = uri
                storageActivityResultLauncher.launch(intent)
            } catch (e: Exception) {
                val intent = Intent()
                intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                storageActivityResultLauncher.launch(intent)
            }
        } else {
            //--------------Below android 11
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                STORAGE_PERMISSION_CODE
            )
        }
    }

    //----------when permission request finished --------
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.size > 0) {
                val write = grantResults[0] == PackageManager.PERMISSION_GRANTED
                val read = grantResults[1] == PackageManager.PERMISSION_GRANTED
                if (read && write) {
                    Toast.makeText(
                        this@UserActivity,
                        "Storage Permissions Granted",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this@UserActivity,
                        "Storage Permissions Denied",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

    }

    private val storageActivityResultLauncher = registerForActivityResult(
        StartActivityForResult()
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            //---------------------------Android is 11 (R) or above
            if (Environment.isExternalStorageManager()) {
                //--------------------Manage External Storage Permissions Granted
                Log.d(
                    ContentValues.TAG,
                    "onActivityResult: Manage External Storage Permissions Granted"
                )
            } else {
                Toast.makeText(
                    this@UserActivity,
                    "Storage Permissions Denied",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            //--------------------Below android 11
        }
    }

    var imageUri: Uri? = null

    //--------------------function to open camera
    private fun TakePicture() {
        try {
            val values = ContentValues()
            imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)!!
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
            startActivityForResult(intent, 1000)

        } catch (e: ActivityNotFoundException) {

        }

    }
    //--------------------after pick image we want to rotate it using this function
    private fun rotateImage(img: Bitmap, degree: Int): Bitmap? {
        val matrix = Matrix()
        matrix.postRotate(degree.toFloat())
        val rotatedImg = Bitmap.createBitmap(img, 0, 0, img.width, img.height, matrix, true)
        img.recycle()
        return rotatedImg
    }


    lateinit var selectedImage:Bitmap

    //--------------------after image capture will set image in image view
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode === 1000 && resultCode == RESULT_OK) {

            val imageUri: Uri = imageUri!!

            val imageStream = contentResolver.openInputStream(imageUri)
            selectedImage = BitmapFactory.decodeStream(imageStream)
            selectedImage = rotateImage(selectedImage,90)!!;

            if (selectedImage != null) {
                image?.setImageBitmap(selectedImage)
                img = encodeImage(selectedImage)
                upload?.isVisible = true

            } else {


            }
        }
    }
    //--------------------encode image to base64 to upload it to our server

    private fun encodeImage(bm: Bitmap): String? {

        val baos = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.JPEG, 95, baos)
        val byteArrayImage = baos.toByteArray()
        return Base64.encodeToString(byteArrayImage, Base64.DEFAULT)

    }

    //--------------------upload image to server ----------
    private fun UploadImage(){
        lastarrayList?.clear()
        resultarrayList?.clear()
        quantityarrayList?.clear()

        if(selectedImage == null){
            Toast.makeText(
                this@UserActivity,
                "Please Take photo",
                Toast.LENGTH_SHORT
            ).show()
            return
        }


        progressDialog?.setTitle("Please Wait...")
        progressDialog?.setCancelable(false)
        progressDialog?.show()
        val a = UUID.randomUUID().toString()


        var queue = Volley.newRequestQueue(this)
        val stringRequest: StringRequest = object : StringRequest(
            Method.POST,
            "https://mealsgradksa2023.000webhostapp.com/UploadImage.php",
            Response.Listener<String> { response ->
                progressDialog!!.dismiss()


//                Toast.makeText(
//                    this@UserActivity,
//                    response.toString(),
//                    Toast.LENGTH_SHORT
//                ).show()

                imgURL = "https://mealsgradksa2023.000webhostapp.com/images/"+response.toString();
                getImage()



            }, Response.ErrorListener {
                progressDialog!!.dismiss()
                Toast.makeText(
                    this@UserActivity,
                    "لا يوجد اتصال انترنت",
                    Toast.LENGTH_SHORT
                ).show()
            }) {

            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params.put("image", img!!)

                return params
            }


        }
        stringRequest.retryPolicy = DefaultRetryPolicy(
            30000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        queue.add(stringRequest)

    }


    //--------------------send image url to API to get food names ----------
    private fun getImage(){
        progressDialog?.setTitle("Please Wait...")
        progressDialog?.setCancelable(false)
        progressDialog?.show()

        var queue = Volley.newRequestQueue(this)
        val stringRequest: StringRequest = object : StringRequest(
            Method.POST,
            "https://detect.roboflow.com/foods-czdnp/3?api_key=GgvL6bZqnfCkqC1yDmHL&image="+imgURL,
            Response.Listener<String> { response ->

                Log.d("abooooooood",response.toString())

                val result = JSONObject(response.toString())
                var prodarray = result.getJSONArray("predictions")

                for (j in 0 until prodarray.length()) {

                    val classs = prodarray.getJSONObject(j)
                    resultarrayList?.add(classs.getString("class"))

                }

                for (it in 0..resultarrayList?.size!!-1) {

                    if(lastarrayList?.contains(resultarrayList?.get(it)) == false){

                        lastarrayList?.add(resultarrayList?.get(it)!!.toLowerCase())
                        quantityarrayList?.add(1)

                    }else if(lastarrayList?.contains(resultarrayList?.get(it))!!){

                        var aa = lastarrayList?.indexOf(resultarrayList?.get(it))!!
                        quantityarrayList?.set(aa,quantityarrayList?.get(aa)!! + 1)

                    }

                }
                if (lastarrayList?.isEmpty() == true){
                    progressDialog!!.dismiss()

                    val builder1 = AlertDialog.Builder(this@UserActivity)
                    builder1.setTitle("Not Found Food")
                    builder1.setCancelable(true)

                    builder1.setNegativeButton(
                        "OK"
                    ) { dialog, id -> dialog.cancel() }

                    val alert11 = builder1.create()
                    alert11.show()
                }else {
                    getFood()
                }

            }, Response.ErrorListener {
                progressDialog!!.dismiss()
                Toast.makeText(
                    this@UserActivity,
                    "لا يوجد اتصال انترنت",
                    Toast.LENGTH_SHORT
                ).show()
            }) {

            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()

                return params
            }


        }
        stringRequest.retryPolicy = DefaultRetryPolicy(
            30000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        queue.add(stringRequest)

    }

    //--------------------check if food that return from api found in our database----------
    private fun getFood(){
        val query = mDatabase!!.child("Foods")
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val data = dataSnapshot.children
                    for (dataSnapshot1 in data) {
                        val dd = Foods()
                        dd.id = dataSnapshot1.key.toString()
                        dd.name = dataSnapshot1.child("name").value.toString()
                        dd.price = dataSnapshot1.child("price").value.toString()
                        dd.calories = dataSnapshot1.child("calories").value.toString()
                        dd.image = dataSnapshot1.child("image").value.toString()
                        var ingred = "";
                        var Ingredientchild = dataSnapshot1.child("Ingredient").children
                        for (dataSnapshot2 in Ingredientchild) {

                            ingred = ingred + dataSnapshot2.key.toString()  + ","

                        }

                        dd.ingredient = ingred

                        val query2 = mDatabase!!.child("Ingredients")
                        query2.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    var ingtxt = ""
                                    val data = dataSnapshot.children
                                    for (dataSnapshot1 in data) {

                                        val ddd = Ingredients()
                                        ddd.id = dataSnapshot1.key.toString()
                                        ddd.name = dataSnapshot1.child("name").value.toString()
                                        ddd.deseas = dataSnapshot1.child("deseas").value.toString()

                                        var ingredientlist = dd?.ingredient!!.split(",")

                                        if(ingredientlist.contains(ddd.id)){

                                            ingtxt = ingtxt + dd.name + ","

                                            dd.deseas = ddd.deseas
                                        }


                                    }

                                    AddToCart(dd)




                                } else {
                                }
                            }

                            override fun onCancelled(databaseError: DatabaseError) {}
                        })





                    }
                    progressDialog!!.dismiss()


                } else {
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }
    //--------------------Add food that return from api found in our database to my carts----------
    private fun AddToCart(dd:Foods){

         if(lastarrayList?.contains(dd.name!!.toLowerCase())!!){
            if(dd.deseas == null) {
                dd.deseas = "" + "," + ""

            }

            var mydeseas = session?.getString("deseas")!!.split(",")
            var fooddeseas = dd.deseas!!.split(",")

            Log.d("abooooood mydeseas",mydeseas.toString())
            Log.d("abooooood fooddeseas",dd.name.toString())

            var flag = false
            for (aa in fooddeseas) {
                if(mydeseas.contains(aa.trim())  && !aa.equals("")){
                    flag = true
                    val builder1 = AlertDialog.Builder(this@UserActivity)
                    builder1.setTitle("This food contains an ingredient that interferes with your disease")
                    builder1.setMessage("Are You want to add food to cart?")
                    builder1.setCancelable(true)
                    builder1.setPositiveButton(
                        "Yes"
                    ) { dialog, id ->

                        var aa = lastarrayList?.indexOf(dd.name!!.toLowerCase())!!
                        mDatabase!!.child("Cart")
                            .child(session?.getString("id")!!).child(dd?.id!!).child("foodid").setValue(
                                dd?.id!!)

                        mDatabase!!.child("Cart")
                            .child(session?.getString("id")!!).child(dd?.id!! ).child("quantity").setValue(quantityarrayList?.get(aa).toString())

                        Toast.makeText(
                            this@UserActivity,
                            "Food Added To Cart!",
                            Toast.LENGTH_SHORT
                        ).show()


                    }
                    builder1.setNegativeButton(
                        "No"
                    ) { dialog, id -> dialog.cancel() }


                    val alert11 = builder1.create()
                    alert11.show()
                    break
                }
            }
            if(flag == false){
                val builder1 = AlertDialog.Builder(this@UserActivity)
                builder1.setMessage("Are You want to add food to cart?")
                builder1.setCancelable(true)
                builder1.setPositiveButton(
                    "Yes"
                ) { dialog, id ->


                    var aa = lastarrayList?.indexOf(dd.name!!.toLowerCase())!!
                    mDatabase!!.child("Cart")
                        .child(session?.getString("id")!!).child(dd?.id!!).child("foodid").setValue(
                            dd?.id!!)

                    mDatabase!!.child("Cart")
                        .child(session?.getString("id")!!).child(dd?.id!! ).child("quantity").setValue(quantityarrayList?.get(aa).toString())

                    Toast.makeText(
                        this@UserActivity,
                        "Food Added To Cart!",
                        Toast.LENGTH_SHORT
                    ).show()

                }
                builder1.setNegativeButton(
                    "No"
                ) { dialog, id -> dialog.cancel() }
                val alert11 = builder1.create()
                alert11.show()
            }



        }
    }

}