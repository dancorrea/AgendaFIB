package com.dscorrea.agenda

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.DatePicker
import com.dscorrea.agenda.Constants.dateFormatter
import com.dscorrea.db.Contato
import com.dscorrea.db.ContatoRepository
import kotlinx.android.synthetic.main.activity_contato.*
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

class ContatoActivity : AppCompatActivity() {

    var cal = Calendar.getInstance()
    var datanascimento: Button? = null
    private var contato: Contato? = null
    private val localArquivoFoto: String? = null
    private var mCurrentPhotoPath: String? = null
    val REQUEST_IMAGE_CAPTURE = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contato)

        val myChildToolbar = toolbar_child
        setSupportActionBar(myChildToolbar)

        // Get a support ActionBar corresponding to this toolbar
        val ab = supportActionBar

        // Enable the Up button
        ab!!.setDisplayHomeAsUpEnabled(true)

        val dateSetListener = object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int) {
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateInView()
            }
        }

        datanascimento = txtDatanascimento
        datanascimento!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                DatePickerDialog(this@ContatoActivity,
                        dateSetListener,
                        // set DatePickerDialog to point to today's date when it loads up
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH)).show()
            }
        })

        btnCadastro?.setOnClickListener {
            contato?.nome = txtNome?.text.toString()
            contato?.endereco = txtEndereco?.text.toString()
            contato?.telefone = txtTelefone?.text.toString().toLong()
            contato?.dataNascimento = cal.timeInMillis
            contato?.email = txtEmail?.text.toString()
            contato?.site = txtSite?.text.toString()

            if (contato?.id == 0L) {
                ContatoRepository(this).create(contato!!)
            } else {
                ContatoRepository(this).update(contato!!)
            }
            finish()
        }

        imgContato.setOnClickListener{
            dispatchTakePictureIntentSimple();
        }

    }

    override fun onResume() {
        super.onResume()
        val intent = intent
        if (intent != null) {
            if (intent.getSerializableExtra("contato") != null) {
                contato = intent.getSerializableExtra("contato") as Contato

                txtNome?.setText(contato?.nome)
                txtEndereco?.setText(contato?.endereco)
                txtTelefone.setText(contato?.telefone.toString())

                if (contato?.dataNascimento != null) {
                    datanascimento?.setText(dateFormatter?.format(Date(contato?.dataNascimento!!)))
                } else {
                    datanascimento?.setText(dateFormatter?.format(Date()))
                }

                txtEmail.setText(contato?.email)
                txtSite?.setText(contato?.site)
            } else {
                contato = Contato()
            }
        }
    }

    private fun updateDateInView() {
        val myFormat = "dd/MM/yyyy" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        datanascimento!!.text = sdf.format(cal.getTime())
    }

    // CAMERA
    private fun dispatchTakePictureIntentSimple() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }
    }

    @Throws(IOException::class)
    private fun storeImage(image: Bitmap) {

        val pictureFile = createImageFile()
        if (pictureFile == null) {
            Log.d("ERRO", "Error creating media file, check storage permissions: ")// e.getMessage());
            return
        }
        try {
            val fos = FileOutputStream(pictureFile)
            image.compress(Bitmap.CompressFormat.PNG, 90, fos)
            fos.close()
        } catch (e: FileNotFoundException) {
            Log.d("ERRO", "File not found: " + e.message)
        } catch (e: IOException) {
            Log.d("ERRO", "Error accessing file: " + e.message)
        }
    }


    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
                imageFileName, /* prefix */
                ".jpg", /* suffix */
                storageDir      /* directory */
        )

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath()
        return image
    }

    private fun readBitmapFile(path: String) {
        var bitmap: Bitmap? = null
        val f = File(path)
        val options = BitmapFactory.Options()
        options.inPreferredConfig = Bitmap.Config.ARGB_8888

        try {
            bitmap = BitmapFactory.decodeStream(FileInputStream(f), null, options)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        imgContato.setImageBitmap(bitmap)
    }
}


