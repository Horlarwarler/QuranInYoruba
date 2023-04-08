package com.sadaqaworks.yorubaquran.audio

import android.content.Context
import android.util.Log
import androidx.core.net.toUri
import com.sadaqaworks.yorubaquran.quran.domain.model.DownloadVerse
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LocalAudio @Inject constructor(
   @ApplicationContext private val context: Context
) {


   suspend fun loadAudioFromLocal(surahId: Int):List<DownloadVerse>{
      val files = context.filesDir.listFiles()
      return withContext(Dispatchers.IO){
         files?.filter {file ->
            Log.d("Log", "Count 1")
            file.isFile && file.canRead() && file.name.endsWith(".mp3")

         }?.filter {
               Log.d("Log", "Count 2")
               filterSurah(surahId,  it.name)
         }?.map {
            val ayahId = extractAyahId(fileName = it.name)
            val uri = it.toUri()
            DownloadVerse(ayahId,uri)
         }.also {

         }

      }?: emptyList()

   }



   private  fun filterSurah(surahId: Int,  fileName:String):Boolean{

      val regex = "^($surahId)_".toRegex()
      return regex.containsMatchIn(fileName)
   }
   private fun extractAyahId(fileName: String): Int {
      val slashStart = fileName.indexOf('_')
      val stopStart = fileName.indexOf('.')
      return fileName.substring(slashStart + 1, stopStart).toInt()

   }
}