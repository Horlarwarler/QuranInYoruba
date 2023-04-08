package com.sadaqaworks.yorubaquran .quran.presentation.ayah

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.*
import com.sadaqaworks.yorubaquran.audio.AudioServiceState
import com.sadaqaworks.yorubaquran.audio.LocalAudio
import com.sadaqaworks.yorubaquran.quran.data.mapper.convertToBookmark
import com.sadaqaworks.yorubaquran.quran.data.remote.dto.SurahDownloadDto
import com.sadaqaworks.yorubaquran.quran.data.remote.dto.VerseDownloadDto
import com.sadaqaworks.yorubaquran.quran.domain.model.*
import com.sadaqaworks.yorubaquran.quran.domain.repository.quranRepositoryInterface
import com.sadaqaworks.yorubaquran.quran.presentation.ayah.AyahUIEvent
import com.sadaqaworks.yorubaquran.quran.presentation.ayah.AyahUiState
import com.sadaqaworks.yorubaquran.util.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import java.net.ConnectException
import javax.inject.Inject


@HiltViewModel
class AyahViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val sharedPreferences: SharedPreferences,
    private val quranRepositoryInterface: quranRepositoryInterface,
    private val localAudio: LocalAudio
) :ViewModel() {

    private val lastReadVerse = sharedPreferences.getInt("lastReadVerse", 0)

    private val downloadSurahBeforePlaying = sharedPreferences.getBoolean("downloadBeforePlaying",false)

    private val defaultReciterName = "Abdurrahmaan As-Sudais"
    private val reciter = sharedPreferences.getString("reciter",defaultReciterName)!!
    private val fontSize = sharedPreferences.getInt("fontSize", 20).toFloat()
    private val autoPlay = sharedPreferences.getBoolean("autoPlay", true)
    private val autoScroll = sharedPreferences.getBoolean("autoScroll", false)

    private val reciterId = reciter.mapReciterToId()

    private val surahId = savedStateHandle.get<Int>("surahId")!!
    // should only download sudais quran
    private val notifyUserAboutReciterSelection = downloadSurahBeforePlaying && reciter != defaultReciterName

    private var downloadJob:Job? = null

    private var normalizeSearch = NormalizeSearch()

    private var ayahsId: List<Int> = emptyList()
    private val stringListHelper = ListHelper<String>()
    private var audiosToDownload: List<VerseAudioData> = emptyList()
    private var searchJob:Job? = null


    private val _audioServiceState = MutableLiveData<AudioServiceState>().apply {
        value = AudioServiceState()
    }

    val audioServiceState:LiveData<AudioServiceState>
        get() = _audioServiceState

    private val _versesToDownload = MutableLiveData<SurahDownloadDto> ()

    val versesToDownload :LiveData<SurahDownloadDto>
        get() = _versesToDownload
    //private var downloadAudios: List<LocalAudioModel> = emptyList()


    private val _ayahUiState = MutableLiveData<AyahUiState>().apply {
        value = AyahUiState(
            lastReadVerse = lastReadVerse,
            notifyUserAboutReciterSelection = notifyUserAboutReciterSelection,
            playFromInternet = !downloadSurahBeforePlaying,
            fontSize = fontSize,
            surahId = surahId,
            autoPlay = autoPlay,
            autoScroll = autoScroll
        )
    }
    val ayahUiState:LiveData<AyahUiState>
        get() = _ayahUiState

    init {

        viewModelScope.async {

           getAllBookmark()
            getAllAyah(surahId)
            getSurahDetails(surahId)
            loadLocalAudio()

        }


    }

    private   fun  loadLocalAudio(handleCompletion:Boolean = false){
        var downloadedAudio: List<DownloadVerse> = emptyList()
        viewModelScope.launch() {
             downloadedAudio = localAudio.loadAudioFromLocal(surahId)

        }.invokeOnCompletion {
            _ayahUiState.value = ayahUiState.value?.copy( downloadedAudios = downloadedAudio)

            if (isAllDownloaded()){
                _ayahUiState.value = ayahUiState.value?.copy(notifyUserToDownloadRemaining = false,)
            }
            else {
                _ayahUiState.value = ayahUiState.value?.copy(notifyUserToDownloadRemaining = true)
            }
            if (handleCompletion){
                handleCompletion()
            }
        }
    }


    private fun isAllDownloaded():Boolean{
        val downloadAudios = ayahUiState.value?.downloadedAudios!!

        Log.d("Audio", "Audio downloadAudios${downloadAudios.size} and ayah id size ${ayahsId.size} will be paused")

        return downloadAudios.size >= ayahsId.size
    }
    fun handleUiEvent(ayahUIEvent: AyahUIEvent) {
        when(ayahUIEvent){
            is AyahUIEvent.BookmarkButtonClick ->{
                    bookmarkButtonClick(ayahUIEvent.verse)
            }
            is AyahUIEvent.ShareButtonClick -> {

            }
            is AyahUIEvent.NavigatedFromScreen -> {
                saveLastRead(ayahUIEvent.position+1)
            }

            is AyahUIEvent.PlayAyah -> {
                playFromStorage()
            }

            is AyahUIEvent.DownloadClick ->{

                downloadClick()
            }
            is AyahUIEvent.PlayFromInternet -> {
                playFromInternet()
            }
            is AyahUIEvent.OnSearchChange -> {
                searchChange(ayahUIEvent.searchQuery)
            }
            is AyahUIEvent.SearchClick ->{
                searchClick(ayahUIEvent.searchQuery)

            }
            is AyahUIEvent.SwitchReciter ->{

            }
            is AyahUIEvent.DownloadComplete ->{
                downloadCompleted(ayahUIEvent.isSuccessfully)
            }

        }
    }

    private fun getAllAyah(surahId:Int){
        viewModelScope.launch {
            quranRepositoryInterface.getAllAyah( surahId).collect{
                resource ->

                when(resource){
                    is Resource.Success ->{
                        if (resource.data != null){
                            ayahsId = resource.data.map {
                                it.id
                            }
                            Log.d("Ayah Id", "${ayahsId}")
                            mapBookmarkAyah(resource.data)
                        }
                    }
                    is Resource.Error ->{
                        addToMessages("${resource.errorMessage} occurs")
                    }
                    is Resource.Loading ->{

                    }
                }

            }
        }
    }

    private  fun mapBookmarkAyah(verse: List<Verse>){
        var bookmarkAyahModel  = verse
        ayahUiState.value?.bookmarkIds?.let {
                bookmarks ->
            if (bookmarks.isEmpty()){
                _ayahUiState.value = _ayahUiState.value?.copy(ayahs = bookmarkAyahModel)
                return
            }
            bookmarks.forEach { _ ->
               bookmarkAyahModel =  bookmarkAyahModel.map {
                   if(it.id in bookmarks){
                       it.copy(isBookmarked = true)
                   }
                   else{
                       it
                   }
                }
            }
        }

        _ayahUiState.value = _ayahUiState.value?.copy(ayahs = bookmarkAyahModel)

    }
    private fun getAllBookmark(){
            viewModelScope.launch {
                quranRepositoryInterface.getAllBookmark().collect{
                    resource ->

                    when(resource){
                        is Resource.Success ->{
                            if (resource.data != null){
                                val bookmarksAyahId = resource.data.map {
                                    it.id
                                }
                                _ayahUiState.value = ayahUiState.value?.copy(bookmarkIds =  bookmarksAyahId)
                            }

                        }
                        is Resource.Error ->{
                            addToMessages("${resource.errorMessage} occurs")
                        }
                        is Resource.Loading ->{

                        }
                    }

                }
            }
    }

    private suspend fun getAyah(ayahId:Int){
        viewModelScope.async {
            quranRepositoryInterface.getAyah(ayahId).collect{
                resource ->
                when(resource) {
                    is Resource.Loading -> {
                        _ayahUiState.value = ayahUiState.value?.copy()
                    }
                    is Resource.Success -> {
                        _ayahUiState.value = ayahUiState.value?.copy(currentAyahIdInSurah = resource.data?.verseId)
                    }

                    is Resource.Error -> {
                        addToMessages("${resource.errorMessage} occurs")

                    }
                }
            }
        }.await()
    }

    private  fun bookmarkButtonClick(verse: Verse){
        _ayahUiState.value = ayahUiState.value?.copy(selectedAyah = verse)
        val ayahBookmark = ayahUiState.value?.bookmarkIds?.firstOrNull {
                it == verse.id
            }
        val ayahInBookmark = ayahBookmark != null
        if (ayahInBookmark){
            deleteFromBookmark()
        }
        else{
            insertToBookmark()
        }
        _ayahUiState.value = ayahUiState.value?.copy(selectedAyah = null)
        getAllBookmark()
        getAllAyah(surahId)
    }
    fun getBookmark(ayahId: Int):Bookmark {
        viewModelScope.launch {
            quranRepositoryInterface.getBookmark(ayahId).collect{

                return@collect
            }
        }
        TODO()
    }

    private fun insertToBookmark() {
        viewModelScope.launch {
            val selectedAyah = ayahUiState.value?.selectedAyah!!
           val bookmark = selectedAyah.convertToBookmark()
            quranRepositoryInterface.insertBookmark(bookmark)
        }
    }

    private fun deleteFromBookmark() {
        viewModelScope.launch {
            val selectedAyah = ayahUiState.value?.selectedAyah!!
            val id = selectedAyah.id
            quranRepositoryInterface.deleteBookmark(id)
        }
    }

    private  fun getSurahDetails(surahId: Int){
        viewModelScope.launch {
            quranRepositoryInterface.getSurah(surahId).collect{
                    resource ->
                when(resource) {
                    is Resource.Loading -> {
                        _ayahUiState.value = ayahUiState.value?.copy()
                    }
                    is Resource.Success -> {
                        _ayahUiState.value = ayahUiState.value?.copy(selectedSurah = resource.data)
                    }
                    is Resource.Error -> {
                        addToMessages("${resource.errorMessage} occurs")
                    }
                }
            }
        }
    }

    private suspend fun getSurahAudio( ){
        viewModelScope.async {
            quranRepositoryInterface.getSurahAudio(surahId, reciterId).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        Log.d("Log", "Loading")

                    }
                    is Resource.Error -> {
                        addToMessages("${resource.errorMessage}")

                    }
                    is Resource.Success -> {
                        _ayahUiState.value = ayahUiState.value!!.copy(surahAudio = resource.data!!.audioVerses)
                        val shouldPlayOnline = ayahUiState.value?.playFromInternet!!
                        if (shouldPlayOnline)
                        {

                            return@collect
                        }
                        getAudioToDownload()
                    }
                }
            }
        }.await()
    }

    private fun getAudioToDownload(){

        if (isAllDownloaded()){
            return
        }
        val downloadedAudios = ayahUiState.value?.downloadedAudios!!

        val downloadedAudiosId = downloadedAudios.map {
            it.id
        }
        val audiosData = ayahUiState.value!!.surahAudio!!

        val versesNotDownloaded = audiosData.filterNot {
                downloadedSurahModel ->
            downloadedAudiosId.contains(downloadedSurahModel.number)
        }
        val surahName = ayahUiState.value!!.selectedSurah?.translation
        val versesToDownload = versesNotDownloaded.map {ayah->
            val fileUrl = ayah.audio
            val id = ayah.number
            val fileName =  surahId.toString() + "_" + id.toString()+ ".mp3"
            VerseDownloadDto(
                fileUrl = fileUrl,
                fileName = fileName
            )
        }
        val surahAudioDto = SurahDownloadDto(
            surahName = surahName!!,
            versesToDownload
        )
        _versesToDownload.value = surahAudioDto

    }



    private  fun downloadClick(){
        _ayahUiState.value = ayahUiState.value?.copy(isLoading = true)
        viewModelScope.launch {
            getSurahAudio()
        }

    }
    private  fun downloadCompleted(isSuccessfully:Boolean){

        try {
            _ayahUiState.value = ayahUiState.value?.copy(isLoading = false)
            if (isSuccessfully){
                loadLocalAudio(autoPlay)
            }
        }

        catch (error:Exception){
            addToMessages("${error.localizedMessage } occurs")
        }

        catch (error:ConnectException){
            addToMessages("No internet connection available")
        }

    }

    private fun handleCompletion(){
        val isDownloadRemaining = ayahUiState.value?.notifyUserToDownloadRemaining!!
        if (isDownloadRemaining){
            _ayahUiState.value = ayahUiState.value?.copy(notifyUserToDownloadRemaining = false)
        }
        _ayahUiState.value = ayahUiState.value?.copy(isLoading = false)

        playFromStorage()
    }

    private fun playFromStorage(){
        loadLocalAudio(handleCompletion = false)
        val downloadedAudios = ayahUiState.value?.downloadedAudios!!
        val surahName = ayahUiState.value!!.selectedSurah?.arabic!!

        val audios = downloadedAudios.map {
           val numberInSurah = getVerseId(it.id)
            DownloadVerse(
                id = it.id,
                uri = it.uri,
                surahName = surahName,
                surahId = this.surahId,
                numberInSurah = numberInSurah?:0
            )

        }
        val convertAudiosToMedia = audios.convertToMedia()

        _audioServiceState.value = audioServiceState.value?.copy(audios = convertAudiosToMedia,)
        _ayahUiState.value = ayahUiState.value?.copy(canPlay = true)

    }

    private fun getVerseId(id:Int):Int?{
        val ayahs = ayahUiState.value?.ayahs
       return ayahs?.firstOrNull(){
            it.id == id
        }?.verseId
    }

    private fun saveLastRead(lastReadVerse:Int){
        val surahName = ayahUiState.value?.selectedSurah?.translation!!
        sharedPreferences.edit().apply {
            putString("lastReadSurah", surahName)
            putInt("lastReadVerse", lastReadVerse)
            apply()
        }
    }

    private fun isCompleted():Boolean{
        return ayahUiState.value?.isCompleted == true
    }

    private fun playFromInternet(){
        _ayahUiState.value  = ayahUiState.value?.copy(playFromInternet = true, notifyUserAboutReciterSelection = false, isLoading = true)
        viewModelScope.launch {
            getSurahAudio()
        }.invokeOnCompletion {
            val audioStreams = ayahUiState.value?.surahAudio
            val surahName = ayahUiState.value!!.selectedSurah?.arabic!!
            if (audioStreams == null || audioStreams.isEmpty()){
                addToMessages("Something Went Wrong")
                _ayahUiState.value = ayahUiState.value?.copy( isLoading = false)
                return@invokeOnCompletion
            }
            val audios = audioStreams.map {
                VerseAudioData(
                    number = it.number,
                    audio = it.audio,
                    audioSecondary = it.audioSecondary,
                    numberInSurah = it.numberInSurah,
                    surahId = surahId,
                    surahName = surahName

                )
            }
            _audioServiceState.value = audioServiceState.value?.copy(audios =audios.convertToMediaItem(),  )
            _ayahUiState.value = ayahUiState.value?.copy(canPlay = true, isLoading = false )

        }
    }

    private fun addToMessages(message:String){
        val messages = ayahUiState.value?.messages!!

        val newMessages = stringListHelper.addElement(message, messages)

        _ayahUiState.value  = ayahUiState.value?.copy(messages = newMessages)
    }

    fun removeFromMessages(){
        val messages = ayahUiState.value?.messages!!

        val newMessages = stringListHelper.removeElement( messages)

        _ayahUiState.value  = ayahUiState.value?.copy(messages = newMessages)
    }


    private fun searchClick(searchText: String) {
        _ayahUiState.value = ayahUiState.value?.copy(searchText="", normalizedSearchText = "" )
    }

    private fun searchChange(searchText: String) {
        if (searchText.isEmpty()){
            _ayahUiState.value = ayahUiState.value!!.copy(searchIndex = emptyList(), normalizedSearchText = "")
            return
        }
        searchJob?.cancel()
        searchJob = Job()
        viewModelScope.launch(searchJob!!) {
            val normalizedSearch = arabicOrYorubaSearch(searchText)
          //  delay(500)
            _ayahUiState.value = ayahUiState.value?.copy(searchText=searchText, normalizedSearchText = normalizedSearch )
            val qurans = ayahUiState.value!!.ayahs
            val searchIds= qurans.filter {
                ayahModel ->
               val textFoundInTranslation = stringFoundInText(normalizedSearch, ayahModel.translation)
               val textFoundInArabic= stringFoundInText(normalizedSearch, ayahModel.arabic)
             //  val textFoundInFootnote = stringFoundInText(normalizedSearch, ayahModel.footnote?:"")
                textFoundInArabic || textFoundInTranslation
            }.map {
                ayahModel ->
                ayahModel.verseId
            }

            _ayahUiState.value = ayahUiState.value!!.copy(searchIndex = searchIds)
        }
    }
    private fun stringFoundInText(
        textToSearch:String,
        wholeText:String
    ):Boolean{
        val searchPattern = Regex(textToSearch, RegexOption.IGNORE_CASE)
        return  searchPattern.containsMatchIn(wholeText)

    }

    private fun arabicOrYorubaSearch(
        input: String
    ): String {

        val yorubaPattern = normalizeSearch.getYorubaMarks()
        Log.d("Normalizer", yorubaPattern)


        val pattern = Regex(yorubaPattern, RegexOption.IGNORE_CASE)

        val isYoruba = pattern.containsMatchIn(input)
        if (isYoruba) {
            return normalizeSearch.normalizeYoruba(input)
        }
        return normalizeSearch.normalizeArabic(input)

    }

    private fun String.mapReciterToId():String{
        val reciterId = reciters.find {
            this == it.englishName
        }?.identifier!!
        return reciterId
    }
}

