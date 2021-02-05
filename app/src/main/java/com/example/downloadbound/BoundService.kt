package com.example.downloadbound

import android.app.Service
import android.content.Intent
import android.os.*
import android.util.Log
import android.widget.Toast
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL


open class BoundService : Service() {
     override fun onBind(intent: Intent?): IBinder {
         Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show()
         return binder
     }

    private val binder: Binder = LocalBinder()

    class LocalBinder : Binder() {
        val service: BoundService
            get() {
                return BoundService()
            }
    }
    override fun onCreate() {
        Toast.makeText(this, "Service Created", Toast.LENGTH_LONG).show()
    }
    fun downloadFile(urlStr: String?) {
        val backTask = BackTask()
        backTask.execute(urlStr)

    }
    override fun onDestroy() {
        Toast.makeText(this, "Service Stopped", Toast.LENGTH_LONG).show()
    }

    // A separate process to download a file from internet
    private  class BackTask : AsyncTask<String?, Int?, Void?>() {
        var finished : Boolean = false
        var progress : String? = null
//        val intent = Intent( this, MainActivity::class.java)
        override fun onPreExecute() {}
        override fun doInBackground(vararg params: String?): Void? {
            val url: URL
            var count: Int
            val con: HttpURLConnection
            var `is`: InputStream? = null
            var fos: FileOutputStream? = null
            try {
                url = URL(params[0])
                try {

                    // Open connection
                    con = url.openConnection() as HttpURLConnection
                    // read stream
                    `is` = con.inputStream
                    val pathR: String = url.path
                    // output file path
                    val filename = pathR.substring(pathR.lastIndexOf('/') + 1)
                    val path: String = Environment.getExternalStorageDirectory()
                        .toString() + "/DCIM/" + filename
                    //write to file
                    fos = FileOutputStream(path)
                    val lengthOfFile: Int = con.contentLength
                    val data = ByteArray(1024)
                    while (`is`.read(data).also { count = it } != -1) {
                        fos.write(data, 0, count)
                    }
                    fos.flush()
                } catch (e: Exception) {
                    e.printStackTrace()
                    val errorMessage = e.message
                    if (errorMessage != null) {
                        Log.e("BoundService", errorMessage)
                    }
                } finally {
                    if (`is` != null) try {
                        `is`.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                    if (fos != null) try {
                        fos.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            } catch (e: MalformedURLException) {
                e.printStackTrace()
            }
            return null
        }

        public override fun onProgressUpdate(vararg values: Int?) {
            super.onProgressUpdate(*values)
                 progress = ("completed..." + values[0] + "%")
        }

        override fun onPostExecute(result: Void?) {
            finished = true

        }
    }
}