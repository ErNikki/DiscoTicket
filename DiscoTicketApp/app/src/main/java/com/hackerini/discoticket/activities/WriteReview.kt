package com.hackerini.discoticket.activities

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.hackerini.discoticket.R
import com.hackerini.discoticket.fragments.elements.ImageReviewElement
import com.hackerini.discoticket.fragments.views.ConfirmReview
import com.hackerini.discoticket.objects.Club
import com.hackerini.discoticket.objects.Review
import com.hackerini.discoticket.objects.User
import com.hackerini.discoticket.room.RoomManager
import com.hackerini.discoticket.utils.ExifUtil
import com.hackerini.discoticket.utils.ReviewsManager
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class WriteReview : AppCompatActivity() {

    var images :MutableList<Bitmap> = mutableListOf<Bitmap>()
    var bitmap: Bitmap? =null
    lateinit var photoLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_review)


        val club = intent.getSerializableExtra("club") as Club
        val originalReview = intent.getSerializableExtra("review") as Review?


        val userDao = RoomManager(this).db.userDao()

        val rating = findViewById<RatingBar>(R.id.writeReviewRatingBar)
        val description = findViewById<EditText>(R.id.writeReviewDescriptionText)
        val addReviewButton = findViewById<Button>(R.id.writeReviewAddReviewButton)
        val takePhotoButton = findViewById<ImageButton>(R.id.writeReviewAddFotoButton)
        photoLayout= findViewById<LinearLayout>(R.id.writeReviewFotoLayout)

        val explanation = findViewById<TextView>(R.id.writeReviewExplanation)
        explanation.text =
            "Pubblicando una recensione otterrai " + resources.getInteger(R.integer.pointPerReview) + " punti"

        if (originalReview != null) {
            rating.rating = originalReview.rating.toFloat()
            description.setText(originalReview.description)
            addReviewButton.text = "Modifica"

            CoroutineScope(Dispatchers.Default).launch {
                // do some long running operation or something
                originalReview.images.forEach { i ->
                    images.add(Picasso.get().load(i).get())
                }
                val transaction = supportFragmentManager.beginTransaction()
                images.forEach { i ->
                    val fragment = ImageReviewElement.newInstance(images.indexOf(i))
                    fragment.getBitmap = ::getBitmap
                    fragment.deleteBitmap = ::deleteImageBitmap
                    transaction.add(R.id.writeReviewFotoLayout, fragment)
                }
                transaction.commit()
            }

            /*
            images= originalReview.aux.toMutableList()

            val transaction = supportFragmentManager.beginTransaction()
            images.forEach {
                i->
                val fragment = ImageReviewElement.newInstance(images.indexOf(i))
                fragment.getBitmap=::getBitmap
                fragment.deleteBitmap=::deleteImageBitmap
                transaction.add(R.id.writeReviewFotoLayout, fragment)
            }
            transaction.commit()

             */

        }

        askPermissions(this)

        takePhotoButton.setOnClickListener {

            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED

            ){
                val alertDialogBuilder = AlertDialog.Builder(this)
                var message=""
                if(ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.CAMERA
                    ) != PackageManager.PERMISSION_GRANTED){
                    message= "$message camera"
                }

                if(ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED){
                    message= "$message WRITE_EXTERNAL_STORAGE"
                }

                if(ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED){
                    message= "$message READ_EXTERNAL_STORAGE"
                }
                alertDialogBuilder.setMessage("Controlla che i seguenti permessi siano abilitati: $message")
                alertDialogBuilder.show()
            }
            else{

                if(images.size<2) {
                    openCamera()
                }
                else{
                    //da sistemare!
                    val alertDialogBuilder = AlertDialog.Builder(this)
                    alertDialogBuilder.setMessage("Hai già aggiunto il numero massimo di foto!")
                    alertDialogBuilder.show()
                }
            }


        }

        addReviewButton.setOnClickListener {

            val context: Context = this
            if (User.isLogged(this)) {
                val review = Review(User.getLoggedUser(this)!!)
                review.description = description.text.toString()
                review.date = SimpleDateFormat("dd/MM/yyy", Locale.getDefault()).format(Date())
                review.clubId = club.id
                review.aux= images.toTypedArray()

                if (originalReview != null) {
                    review.id = originalReview.id
                }

                if (rating.rating != 0f) {
                    review.rating = rating.rating.toDouble()
                    if (originalReview != null) {
                        //Edit
                        val result = ReviewsManager.editReview2(review)
                        if (result) {
                            val builder = MaterialAlertDialogBuilder(context)
                            //review.reviewId = originalReview.reviewId
                            builder.setTitle("Recensione modificata")
                            builder.setMessage("La tua recensione è stata aggiornata con successo")
                            //reviewDao.editReview(review)
                            builder.setPositiveButton("Ok") { dialog, _ -> finish() }
                            builder.create().show()
                        } else {
                            val builder = MaterialAlertDialogBuilder(context)
                            review.reviewId = originalReview.reviewId
                            builder.setTitle("Recensione non modificata")
                            builder.setMessage("si è verificato un problema e la tua recensione non è stata aggiornata")
                            //reviewDao.editReview(review)
                            builder.setPositiveButton("Ok") { dialog, _ -> finish() }
                            builder.create().show()
                        }

                    } else {
                        //New review

                        val dialog = ConfirmReview.newInstance(review)
                        dialog.show(supportFragmentManager, "fdfdf")
                        dialog.onConfirmAction = { r ->
                            var flag : Boolean = ReviewsManager.insert2(r)
                            //reviewDao.insert(r)
                            userDao.incrementsPoints(
                                resources.getInteger(R.integer.pointPerReview),
                                User.getLoggedUser(this)!!.id
                            )
                            dialog.dismiss()
                            if (flag) {
                                var builder2 = MaterialAlertDialogBuilder(context)
                                builder2.setTitle("Recensione aggiunta")
                                builder2.setMessage(
                                    "Grazie per aver lasciato una recensione!\nHai ottenuto ${
                                        resources.getInteger(R.integer.pointPerReview)
                                    } punti che potrai usare per ottenere buoni sconto"
                                )
                                builder2.setPositiveButton("Ok") { dialog, _ -> finish() }
                                builder2.create().show()
                            } else {
                                var builder2 = MaterialAlertDialogBuilder(context)
                                builder2.setTitle("Recensione non aggiunta")
                                builder2.setMessage(
                                    "c'è stato un problema nell'inserimento della recensione"
                                )
                                builder2.setPositiveButton("Ok") { dialog, _ -> finish() }
                                builder2.create().show()
                            }

                        }

                    }
                } else {
                    val builder = MaterialAlertDialogBuilder(context)
                    builder.setTitle("Attenzione: la recensione non contiene una valutazione")
                    builder.setMessage("Per lasciare una recensione devi aggiungere una valutazione")
                    builder.setPositiveButton("Ok") { dialog, _ -> dialog.dismiss() }
                    builder.create().show()
                }
            } else {

                val builder = MaterialAlertDialogBuilder(context)
                builder.setTitle("Accesso non effettuato")
                builder.setMessage("Per proseguire è necessario effettuare l'accesso")
                builder.setPositiveButton("Accedi") { _, _ ->
                    context.startActivity(Intent(context, Login::class.java))
                }
                builder.setNegativeButton("Annulla") { dialog, _ ->
                    dialog.dismiss()
                }
                builder.create().show()
            }


        }

    }

    override fun onResume() {
        super.onResume()
        /*
        val imageView = ImageView(this)
        imageView.setImageBitmap(bitmap)
        photoLayout.addView((imageView))
        */
        val photos = findViewById<LinearLayout>(R.id.writeReviewFotoLayout)
        photos.removeAllViews()
        val transaction = supportFragmentManager.beginTransaction()
        images.forEach {
            i->
            val fragment = ImageReviewElement.newInstance(images.indexOf(i))
            fragment.getBitmap=::getBitmap
            fragment.deleteBitmap=::deleteImageBitmap
            transaction.add(R.id.writeReviewFotoLayout, fragment)
        }
        transaction.commit()

    }

    private fun getBitmap(position:Int):Bitmap{
        return this.images[position]
    }
    private fun deleteImageBitmap(position:Int){
        images.removeAt(position)
        val photos = findViewById<LinearLayout>(R.id.writeReviewFotoLayout)
        photos.removeAllViews()
        val transaction = supportFragmentManager.beginTransaction()
        images.forEach {
                i->
            val fragment = ImageReviewElement.newInstance(images.indexOf(i))
            fragment.getBitmap=::getBitmap
            fragment.deleteBitmap=::deleteImageBitmap
            transaction.add(R.id.writeReviewFotoLayout, fragment)
        }
        transaction.commit()
    }

    lateinit var photoFile: File
    private fun openCamera() {

        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        photoFile= createImageFile();

        if (photoFile!=null){
            val imageUri = FileProvider.getUriForFile(
                this,
                "com.hackerini.discoticket.provider",  //(use your app signature + ".provider" )
                photoFile
            )

            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
            resultLauncher.launch(cameraIntent)
        }

    }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

            if (result.resultCode == Activity.RESULT_OK) {
                val myBitmap = BitmapFactory.decodeFile(currentPhotoPath)
                val orientedBitmap = ExifUtil.rotateBitmap(currentPhotoPath,myBitmap)
                images.add(orientedBitmap)
                /*
                if (result?.data != null) {
                    //bitmap = result.data?.extras?.get("data") as? Bitmap
                    //Log.d("dimensions", bitmap!!.width.toString())
                    //Log.d("dimensions", bitmap!!.height.toString())
                    Log.d("pathToPhoto2",currentPhotoPath)
                    images.add(BitmapFactory.decodeFile(currentPhotoPath))
                }*/

            }
        }


    lateinit var currentPhotoPath: String

    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    private fun askPermissions(context: Context){
        //Log.d("asking","asking")
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED

        ) {
            Log.d("asking","asking")
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                2
            );
        }
    }




}