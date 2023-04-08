package com.sadaqaworks.yorubaquran.download

import android.content.Context
import android.util.Log
import com.sadaqaworks.yorubaquran.util.CustomError
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import java.net.ConnectException

class KtorDownloaderImpl(
    private val httpClient: HttpClient,
    private val context: Context
) : DownloaderInterface{

    override suspend fun downloadFile(fileUrl: String, fileName: String): Long {
        try {
            val response = httpClient.get(fileUrl)
            if (response.status.value != 200){
                throw  CustomError(response.status.description)
            }
            val fileToSave = response.body<ByteArray>()

            saveFile(fileName,fileToSave)
        }
        catch (error:ConnectException){
            throw CustomError("Internet Not Available")
        }
        catch (error:ServerResponseException){
            throw CustomError("Server is not available")
        }
        catch (e:Exception){
            Log.d("Log", "Error in downloading or saving ${e.localizedMessage}")

        }
        return  1
    }
    private fun saveFile(fileName:String,fileToSave:ByteArray){
        context.openFileOutput(fileName, Context.MODE_PRIVATE).use {
            it.write(fileToSave)
            it.close()
            it.flush()
        }

    }

}