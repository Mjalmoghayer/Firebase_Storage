package com.example.firebase_storage

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.firebase_storage.databinding.ActivityMainBinding
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var storageReference: FirebaseStorage

    //val upload: ImageView? = null
    var pdfUri: Uri? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //initialising firebase storage
        storageReference = FirebaseStorage.getInstance()

        // download file from firebase storage
        binding.downloadpdf.setOnClickListener {
            //download file from firebase storage with generated name
            val setName = binding.setFileName.text.toString()
            binding.setFileName.setText("")

            if (setName.isEmpty()) {
                binding.setFileName.error = "Please enter file name"
                binding.setFileName.requestFocus()
                return@setOnClickListener
            } else {
                val ref: StorageReference = storageReference.reference.child("$setName.pdf")
                ref.downloadUrl.addOnSuccessListener { uri ->
                    val url = uri.toString()
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.setDataAndType(Uri.parse(url), "application/pdf")
                    startActivity(intent)
                    Toast.makeText(this@MainActivity, "Downloaded Successfully", Toast.LENGTH_SHORT)
                        .show()

                    binding.setFileName.hint = "File downloaded: ${setName}.pdf"
                }.addOnFailureListener {
                    Toast.makeText(this@MainActivity, "Downloaded Failed", Toast.LENGTH_SHORT)
                        .show()
                }
            }

        }


        /*
        After Clicking on this we will be
        redirected to choose pdf
        */
        binding.uploadpdf.setOnClickListener {
            binding.setFileName.hint = "Enter file name"
            binding.setFileName.isEnabled = true
            val name = binding.setFileName.text.toString()


            val pdfIntent = Intent()
            pdfIntent.action = Intent.ACTION_GET_CONTENT

            // We will be redirected to choose pdf
            pdfIntent.type = "application/pdf"

            if (name.isEmpty()) {
                binding.setFileName.error = "Please enter file name"
                binding.setFileName.requestFocus()
                return@setOnClickListener
            } else {
                startActivityForResult(pdfIntent, 1)
                binding.setFileName.hint = ("File uploaded: ${name}.pdf")
            }
        }

    }

    var dialog: ProgressDialog? = null

    @SuppressLint("SetTextI18n")
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        binding.setFileName.isEnabled = true
        val name = binding.setFileName.text.toString()
        //initialising firebase storage

        if (resultCode == RESULT_OK) {

            // Here we are initialising the progress dialog box
            dialog = ProgressDialog(this)
            dialog!!.setMessage("Uploading")

            /*
        this will show message uploading
        while pdf is uploading
        */
            dialog!!.show()
            pdfUri = data!!.data
            // Here we are uploading the pdf in firebase storage with the name of current time
            val filepath = storageReference.reference.child("$name.pdf")
            //Toast.makeText(this@MainActivity, filepath.name, Toast.LENGTH_SHORT).show()
            filepath.putFile(pdfUri!!).addOnCompleteListener {
                if (it.isSuccessful) {
                    binding.setFileName.isEnabled = false
                    Toast.makeText(
                        this@MainActivity, "Uploaded Successfully", Toast.LENGTH_SHORT
                    ).show()
                } else {
                    dialog!!.dismiss()
                    Toast.makeText(this@MainActivity, "UploadedFailed", Toast.LENGTH_SHORT).show()
                }

            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    /*
                After uploading is done it progress
                dialog box will be dismissed
                */
                    dialog!!.dismiss()
                    val uri = task.result
                    uri.toString()
                    Toast.makeText(
                        this@MainActivity, "Uploaded Successfully", Toast.LENGTH_SHORT
                    ).show()
                } else {
                    dialog!!.dismiss()
                    Toast.makeText(this@MainActivity, "UploadedFailed", Toast.LENGTH_SHORT).show()
                }

            }
        }

    }
}
