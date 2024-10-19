package com.sadaqaworks.yorubaquran.quran.presentation.ayah

import android.util.Log
import androidx.lifecycle.*
import com.sadaqaworks.yorubaquran.audio.AudioServiceState
import com.sadaqaworks.yorubaquran.audio.LocalAudio
import com.sadaqaworks.yorubaquran.quran.data.remote.dto.SurahDownloadDto
import com.sadaqaworks.yorubaquran.quran.data.remote.dto.VerseDownloadDto
import com.sadaqaworks.yorubaquran.quran.domain.model.*
import com.sadaqaworks.yorubaquran.quran.domain.repository.quranRepositoryInterface
import com.sadaqaworks.yorubaquran.shared.QuranPreference
import com.sadaqaworks.yorubaquran.util.*
import com.sadaqaworks.yorubaquran.util.Constant.SUDAIS
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import java.net.ConnectException
import javax.inject.Inject


@HiltViewModel
class AyahViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val quranPreference: QuranPreference,
    private val quranRepositoryInterface: quranRepositoryInterface,
    private val localAudio: LocalAudio
) : ViewModel() {

    private val lastSurah = quranPreference.getLastSurah()
    private val downloadBeforePlaying = quranPreference.downloadBeforePlaying()
    private val surahId = savedStateHandle.get<Int>("surahId")!!
    private val reciter = quranPreference.getReciterName()
    private val fontSize = quranPreference.getFontSize()
    private val autoPlay = quranPreference.autoPlay()
    private val autoScroll = quranPreference.autoScroll()

    private val continueReading = quranPreference.continueReading()
    private val positionOffset = when (lastSurah) {
        surahId -> {
            if (!continueReading) {
                Pair(0, 0)
            } else {
                val offset = quranPreference.getOffset()
                val position = quranPreference.getPosition()
                Pair(position, offset)
            }

        }

        else -> {
            Pair(0, 0)
        }
    }


    private val reciterId = reciter.mapReciterToId()

    // should only download sudais quran
    private val notifyUserAboutReciterSelection = downloadBeforePlaying && reciter != SUDAIS

    private var normalizeSearch = NormalizeSearch()

    private var verseIds: List<Int> = emptyList()
    private val stringListHelper = ListHelper<String>()
    private var searchJob: Job? = null


    private val _audioServiceState: MutableLiveData<AudioServiceState> = MutableLiveData(
        AudioServiceState()
    )

    val audioServiceState: LiveData<AudioServiceState>
        get() = _audioServiceState

    private
    val _versesToDownload = MutableLiveData<SurahDownloadDto>()

    val versesToDownload

            : LiveData<SurahDownloadDto>
        get() = _versesToDownload
    //private var downloadAudios: List<LocalAudioModel> = emptyList()


    private
    val _ayahUiState = MutableLiveData<AyahUiState>().apply {
        value = AyahUiState(
            notifyUserAboutReciterSelection = notifyUserAboutReciterSelection,
            playFromInternet = !downloadBeforePlaying,
            fontSize = fontSize.toFloat(),
            surahId = surahId,
            autoPlay = autoPlay,
            autoScroll = autoScroll,
            firstVisibleItemId = positionOffset.first,
            visibleItemOffset = positionOffset.second
        )
    }
    val ayahUiState

            : LiveData<AyahUiState>
        get() = _ayahUiState

    init {
        initialize()

    }

   private fun initialize(){

        getAllVerses(surahId)
        viewModelScope.async {
            getAllBookmark()
            getSurahDetails(surahId)
            loadLocalAudio()

        }
    }

    private fun loadLocalAudio(handleCompletion: Boolean = false) {
        var downloadedAudio: List<DownloadVerse> = emptyList()
        viewModelScope.launch() {
            downloadedAudio = localAudio.loadAudioFromLocal(surahId)

        }.invokeOnCompletion {
            _ayahUiState.value = ayahUiState.value?.copy(downloadedAudios = downloadedAudio)

            if (isAllDownloaded()) {
                _ayahUiState.value =
                    ayahUiState.value?.copy(notifyUserToDownloadRemaining = false)
            } else {
                _ayahUiState.value =
                    ayahUiState.value?.copy(notifyUserToDownloadRemaining = true)
            }
            if (handleCompletion) {
                handleCompletion()
            }
        }
    }


    private fun isAllDownloaded(): Boolean {
        val downloadAudios = ayahUiState.value?.downloadedAudios!!

        Log.d(
            "Audio",
            "Audio downloadAudios${downloadAudios.size} and ayah id size ${verseIds.size} will be paused"
        )

        return downloadAudios.size >= verseIds.size
    }

    fun handleUiEvent(ayahUIEvent: AyahUIEvent) {
        when (ayahUIEvent) {
            is AyahUIEvent.BookmarkButtonClick -> {
                bookmarkButtonClick(ayahUIEvent.verse)
            }

            is AyahUIEvent.ShareButtonClick -> {

            }

            is AyahUIEvent.NavigatedFromScreen -> {
                saveLastRead(ayahUIEvent.position, ayahUIEvent.offset)
            }

            is AyahUIEvent.PlayAyah -> {
                playFromStorage()
            }

            is AyahUIEvent.DownloadClick -> {

                downloadClick()
            }

            is AyahUIEvent.PlayFromInternet -> {
                playFromInternet()
            }

            is AyahUIEvent.OnSearchChange -> {
                searchChange(ayahUIEvent.searchQuery)
            }

            is AyahUIEvent.SearchClick -> {
                searchClick(ayahUIEvent.searchQuery)

            }

            is AyahUIEvent.SwitchReciter -> {

            }

            is AyahUIEvent.DownloadComplete -> {
                downloadCompleted(ayahUIEvent.isSuccessfully)
            }
            is AyahUIEvent.GetAllVerse ->{
                initialize()
            }

        }
    }

    private fun getAllVerses(surahId: Int) {
        viewModelScope.launch {
            val verses = quranRepositoryInterface.getAllAyah(surahId)
            _ayahUiState.value = _ayahUiState.value?.copy(
                ayahs = verses
            )
            verseIds = verses.map {
                it.verseId
            }
            Log.d("Ayah Id", "$verseIds")
            mapBookmarkAyah(verses)

        }
    }


    private fun mapBookmarkAyah(
        verse: List<Verse>
    ) {
        var bookmarkAyahModel = verse
        ayahUiState.value?.bookmarkIds?.let { bookmarks ->
            if (bookmarks.isEmpty()) {
                _ayahUiState.value = _ayahUiState.value?.copy(ayahs = bookmarkAyahModel)
                return
            }
            bookmarks.forEach { _ ->
                bookmarkAyahModel = bookmarkAyahModel.map {
                    if (it.verseId in bookmarks) {
                        it.copy(isBookmarked = true)
                    } else {
                        it
                    }
                }
            }
        }

        _ayahUiState.value = _ayahUiState.value?.copy(ayahs = bookmarkAyahModel)

    }

    private fun getAllBookmark() {
        viewModelScope.launch {
            quranRepositoryInterface.getBookmarksVerseBySurah(surahId).collect { resource ->

                when (resource) {
                    is Resource.Success -> {
                        if (resource.data != null) {

                            _ayahUiState.value =
                                ayahUiState.value?.copy(bookmarkIds = resource.data)
                        }

                    }

                    is Resource.Error -> {
                        addToMessages("${resource.errorMessage} occurs")
                    }

                    is Resource.Loading -> {

                    }
                }

            }
        }
    }

    private fun bookmarkButtonClick(verse: Verse) {
        _ayahUiState.value = ayahUiState.value?.copy(selectedAyah = verse)
        val ayahBookmark = ayahUiState.value?.bookmarkIds?.firstOrNull {
            it == verse.id
        }
        val ayahInBookmark = ayahBookmark != null
        if (ayahInBookmark) {
            deleteFromBookmark()
        } else {
            insertToBookmark()
        }
        _ayahUiState.value = ayahUiState.value?.copy(selectedAyah = null)
        getAllBookmark()
        getAllVerses(surahId)
    }

    private fun insertToBookmark() {
        viewModelScope.launch {
            val selectedAyah = ayahUiState.value?.selectedAyah!!
            quranRepositoryInterface.insertBookmark(
               verse = selectedAyah
            )
        }
    }

    private fun deleteFromBookmark() {
        viewModelScope.launch {
            val selectedAyah = ayahUiState.value?.selectedAyah!!
            quranRepositoryInterface.deleteBookmark(
               id = selectedAyah.id
            )
        }
    }

    private fun getSurahDetails(surahId: Int) {
        viewModelScope.launch {
            quranRepositoryInterface.getSurah(surahId).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _ayahUiState.value = ayahUiState.value?.copy()
                    }

                    is Resource.Success -> {
                        _ayahUiState.value =
                            ayahUiState.value?.copy(selectedSurah = resource.data)
                    }

                    is Resource.Error -> {
                        addToMessages("${resource.errorMessage} occurs")
                    }
                }
            }
        }
    }

    private suspend fun getSurahAudio() {
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
                        _ayahUiState.value =
                            ayahUiState.value!!.copy(surahAudio = resource.data!!.audioVerses)
                        val shouldPlayOnline = ayahUiState.value?.playFromInternet!!
                        if (shouldPlayOnline) {

                            return@collect
                        }
                        getAudioToDownload()
                    }
                }
            }
        }.await()
    }

    private fun getAudioToDownload() {

        if (isAllDownloaded()) {
            return
        }
        val downloadedAudios = ayahUiState.value?.downloadedAudios!!

        val downloadedAudiosId = downloadedAudios.map {
            it.id
        }
        val audiosData = ayahUiState.value!!.surahAudio!!

        val versesNotDownloaded = audiosData.filterNot { downloadedSurahModel ->
            downloadedAudiosId.contains(downloadedSurahModel.number)
        }
        val surahName = ayahUiState.value!!.selectedSurah?.translation
        val versesToDownload = versesNotDownloaded.map { ayah ->
            val fileUrl = ayah.audio
            val id = ayah.number
            val fileName = surahId.toString() + "_" + id.toString() + ".mp3"
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


    private fun downloadClick() {
        _ayahUiState.value = ayahUiState.value?.copy(isLoading = true)
        viewModelScope.launch {
            getSurahAudio()
        }

    }

    private fun downloadCompleted(isSuccessfully: Boolean) {

        try {
            _ayahUiState.value = ayahUiState.value?.copy(isLoading = false)
            if (isSuccessfully) {
                loadLocalAudio(autoPlay)
            }
        } catch (error: Exception) {
            addToMessages("${error.localizedMessage} occurs")
        } catch (error: ConnectException) {
            addToMessages("No internet connection available")
        }

    }

    private fun handleCompletion() {
        val isDownloadRemaining = ayahUiState.value?.notifyUserToDownloadRemaining!!
        if (isDownloadRemaining) {
            _ayahUiState.value = ayahUiState.value?.copy(notifyUserToDownloadRemaining = false)
        }
        _ayahUiState.value = ayahUiState.value?.copy(isLoading = false)

        playFromStorage()
    }

    private fun playFromStorage() {
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
                numberInSurah = numberInSurah ?: 0
            )

        }
        val convertAudiosToMedia = audios.convertToMedia()

        _audioServiceState.value = audioServiceState.value?.copy(audios = convertAudiosToMedia)
        _ayahUiState.value = ayahUiState.value?.copy(canPlay = true)

    }

    private fun getVerseId(id: Int): Int? {
        val ayahs = ayahUiState.value?.ayahs
        return ayahs?.firstOrNull() {
            it.id == id
        }?.verseId
    }

    private fun saveLastRead(position: Int, offset: Int) {

        quranPreference.saveLastSurah(surahId)
        quranPreference.saveOffset(offset)
        quranPreference.savePosition(position)

    }

    private fun playFromInternet() {
        _ayahUiState.value = ayahUiState.value?.copy(
            playFromInternet = true,
            notifyUserAboutReciterSelection = false,
            isLoading = true
        )
        viewModelScope.launch {
            getSurahAudio()
        }.invokeOnCompletion {
            val audioStreams = ayahUiState.value?.surahAudio
            val surahName = ayahUiState.value!!.selectedSurah?.arabic!!
            if (audioStreams.isNullOrEmpty()) {
                addToMessages("Something Went Wrong")
                _ayahUiState.value = ayahUiState.value?.copy(isLoading = false)
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
            _audioServiceState.value =
                audioServiceState.value?.copy(audios = audios.convertToMediaItem())
            _ayahUiState.value = ayahUiState.value?.copy(canPlay = true, isLoading = false)

        }
    }

    private fun addToMessages(message: String) {
        val messages = ayahUiState.value?.messages!!

        val newMessages = stringListHelper.addElement(message, messages)

        _ayahUiState.value = ayahUiState.value?.copy(messages = newMessages)
    }

    fun removeFromMessages() {
        val messages = ayahUiState.value?.messages!!

        val newMessages = stringListHelper.removeElement(messages)

        _ayahUiState.value = ayahUiState.value?.copy(messages = newMessages)
    }


    private fun searchClick(searchText: String) {
        _ayahUiState.value = ayahUiState.value?.copy(searchText = "", normalizedSearchText = "")
    }

    private fun searchChange(searchText: String) {
        if (searchText.isEmpty()) {
            _ayahUiState.value =
                ayahUiState.value!!.copy(searchIndex = emptyList(), normalizedSearchText = "")
            return
        }
        searchJob?.cancel()
        searchJob = Job()
        viewModelScope.launch(searchJob!!) {
            val normalizedSearch = arabicOrYorubaSearch(searchText)
            //  delay(500)
            _ayahUiState.value = ayahUiState.value?.copy(
                searchText = searchText,
                normalizedSearchText = normalizedSearch
            )
            val qurans = ayahUiState.value!!.ayahs
            val searchIds = qurans.filter { ayahModel ->
                val textFoundInTranslation =
                    stringFoundInText(normalizedSearch, ayahModel.translation)
                val textFoundInArabic = stringFoundInText(normalizedSearch, ayahModel.arabic)
                //  val textFoundInFootnote = stringFoundInText(normalizedSearch, ayahModel.footnote?:"")
                textFoundInArabic || textFoundInTranslation
            }.map { ayahModel ->
                ayahModel.verseId
            }

            _ayahUiState.value = ayahUiState.value!!.copy(searchIndex = searchIds)
        }
    }

    private fun stringFoundInText(
        textToSearch: String,
        wholeText: String
    )

            : Boolean {
        val searchPattern = Regex(textToSearch, RegexOption.IGNORE_CASE)
        return searchPattern.containsMatchIn(wholeText)

    }

    private fun arabicOrYorubaSearch(
        input: String
    )

            : String {

        val yorubaPattern = normalizeSearch.getYorubaMarks()
        Log.d("Normalizer", yorubaPattern)


        val pattern = Regex(yorubaPattern, RegexOption.IGNORE_CASE)

        val isYoruba = pattern.containsMatchIn(input)
        if (isYoruba) {
            return normalizeSearch.normalizeYoruba(input)
        }
        return normalizeSearch.normalizeArabic(input)

    }

    private fun String.mapReciterToId()
            : String {
        val reciterId = reciters.find {
            this == it.englishName
        }?.identifier!!
        return reciterId
    }
}

