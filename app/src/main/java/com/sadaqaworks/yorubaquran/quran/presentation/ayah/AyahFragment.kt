package com.sadaqaworks.yorubaquran.quran.presentation.ayah

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.sadaqaworks.yorubaquran.R
import com.sadaqaworks.yorubaquran.audio.AudioService
import com.sadaqaworks.yorubaquran.databinding.FragmentAyahBinding
import com.sadaqaworks.yorubaquran.download.DownloadService
import com.sadaqaworks.yorubaquran.internet.InternetConnectionState
import com.sadaqaworks.yorubaquran.quran.data.remote.dto.SurahDownloadDto
import com.sadaqaworks.yorubaquran.quran.domain.model.SurahDetails
import com.sadaqaworks.yorubaquran.quran.domain.model.Verse
import com.sadaqaworks.yorubaquran.shared.SharedViewModel
import com.sadaqaworks.yorubaquran.util.CustomError
import com.sadaqaworks.yorubaquran.util.CustomToast
import com.sadaqaworks.yorubaquran.util.shareClick
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.util.Locale


@AndroidEntryPoint
class AyahFragment : Fragment() {
    private lateinit var fragmentAyahBinding: FragmentAyahBinding
    private val ayahViewModel by viewModels<AyahViewModel>()

    private val sharedViewModel: SharedViewModel by activityViewModels()

    private var firstVisibleItemId: Int = 0
    private var visibleItemOffset: Int = 0

    private var shouldScroll: Boolean = true


    lateinit var linearLayoutManager: LinearLayoutManager

    private var mediaBrowserCompat: MediaBrowserCompat? = null
    private var mediaController: MediaControllerCompat? = null
    private var isAnimated: Boolean = false
    private lateinit var ayahAdapter: AyahAdapter
    private var lastPlayedId: Int? = null
    private var internetConnectionState: InternetConnectionState =
        InternetConnectionState.UNAVAILABLE
    private var currentSurahIsPlaying: Boolean = false
    private var startService: Boolean = false
    private var autoScroll: Boolean = false
    private var searchScrollIndex: Int? = null

    var searchIndexes: List<Int> = emptyList()
    private var phoneIsSmallDevice: Boolean = false
    private var surahId: Int? = null

    companion object {
        const val ACTION_COMPLETE = "com.crezent.quraninyoruba.download_complete"
    }

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == ACTION_COMPLETE) {
                // download is complete
                val isSuccessfully = intent.getBooleanExtra("isSuccessfully", false)
                startService = isSuccessfully

                ayahViewModel.handleUiEvent(AyahUIEvent.DownloadComplete(isSuccessfully))

            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        fragmentAyahBinding = FragmentAyahBinding.inflate(inflater)
        val height = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            activity?.windowManager?.currentWindowMetrics?.bounds?.height()
        } else {
            activity?.windowManager?.defaultDisplay?.height
        }
        if (height != null) {
            phoneIsSmallDevice = height < 900
        }
        if (phoneIsSmallDevice) {
            hideSurahDetails()
        }
        return fragmentAyahBinding.root
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val uiState = ayahViewModel.ayahUiState.value ?: return
        linearLayoutManager = LinearLayoutManager(requireContext())

        firstVisibleItemId = uiState.firstVisibleItemId
        visibleItemOffset = uiState.visibleItemOffset
        Log.d("TAG", "first visible $firstVisibleItemId and offset $visibleItemOffset")

        setupAdapter(uiState)
        setupUiListeners()
        observeViewModels()
        setupMediaBrowser()
        handleSearchButtonEvent(ayahAdapter)
    }



    private fun setupUiListeners() = with(fragmentAyahBinding) {
        playIcon.setOnClickListener { playOrPauseButtonClick() }
        closeDialogButtonClick()

        forwardIcon.setOnClickListener { mediaController?.transportControls?.skipToNext() }
        rewindIcon.setOnClickListener { mediaController?.transportControls?.skipToPrevious() }

        bookmarkIcon.setOnClickListener {
            findNavController().navigate(R.id.action_ayahFragment_to_bookmarkFragment)
        }
        backButton.setOnClickListener {
            if (searchIsVisible()) {
                showAppIcon()
            } else {
                findNavController().navigateUp()
            }
        }

        actionUp.setOnClickListener { scrollClick(true) }
        actionDown.setOnClickListener { scrollClick(false) }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun observeViewModels() {
        sharedViewModel.shareViewModelState.observe(viewLifecycleOwner) { state ->
            internetConnectionState = state.internetConnectionState
        }
        ayahViewModel.audioServiceState.observe(viewLifecycleOwner) { state ->
            if (state.audios.isNotEmpty() && !currentSurahIsPlaying) {
                sendMediaItemsBroadcast(state.audios)
            }
        }

        ayahViewModel.ayahUiState.observe(viewLifecycleOwner) { state ->
            state ?: return@observe
            updateUiFromState(state)

            // ✅ If surahNumber changes, update adapter dynamically
            state.selectedSurah?.let { surah ->
                if (::ayahAdapter.isInitialized) {
                    ayahAdapter.surahNumber = surah.surahId
                    ayahAdapter.notifyDataSetChanged()
                }
            }
        }

        ayahViewModel.versesToDownload.observe(viewLifecycleOwner) { surah ->
            if (surah.verses.isNotEmpty() && !startService) {
                startDownloadService(surah)
            }
        }
    }

    private fun setupMediaBrowser() {
        mediaBrowserCompat = MediaBrowserCompat(
            requireContext(),
            ComponentName(requireActivity().applicationContext, AudioService::class.java),
            connectionCallback,
            null
        )
        mediaBrowserCompat?.connect()
    }

    private fun setupAdapter(uiState: AyahUiState) {
        ayahAdapter = AyahAdapter(
            fontSize = uiState.fontSize,
            onShareClick = { ayah -> activity?.shareClick(ayah) },
            onBookmarkClick = { verse ->
                ayahViewModel.handleUiEvent(AyahUIEvent.BookmarkButtonClick(verse))
            },
            surahNumber = uiState.selectedSurah?.surahId ?: 1 // temporary, will update later
        )
        bindAdapterToRecyclerView()
    }


    /** -------------------------------
     *  Helper Methods
     *  ------------------------------- */
    private fun updateUiFromState(state: AyahUiState) {
        setAdapter(state.ayahs)

        if (!isAnimated) {
            setAnimation()
            viewLifecycleOwner.lifecycleScope.launch {
                delay(1000)
                isAnimated = true
            }
        }

        showDownloadProgress(state.isLoading)
        state.selectedSurah?.let { setSurahDetails(it) }
        searchIndexes = state.searchIndex
        sendToastEvent(state)

        surahId = state.surahId
        autoScroll = state.autoScroll
    }

    private fun sendMediaItemsBroadcast(mediaItems: List<MediaBrowserCompat.MediaItem>) {
        val intent = Intent("com.quran.media_items").apply {
            putParcelableArrayListExtra("media_items", ArrayList(mediaItems))
            setPackage(requireContext().packageName)
        }
        requireContext().sendBroadcast(intent)
    }


    private fun setSurahDetails(surahDetails: SurahDetails) {
        fragmentAyahBinding.surahName.text = surahDetails.translation.capitalize(Locale.ROOT)
        fragmentAyahBinding.surahDetails.ayahNo.text = "Ayah No: " + surahDetails.ayahNumber
        fragmentAyahBinding.surahDetails.surahArabic.text = surahDetails.arabic
        fragmentAyahBinding.surahDetails.surahNazzil.text = surahDetails.type
        fragmentAyahBinding.surahDetails.surahTranslation.text =
            surahDetails.translation.capitalize(Locale.ROOT)
    }


    private fun scrollClick(scrollUp: Boolean = true) {
        val customToast = CustomToast(requireContext())

        if (searchIndexes.isEmpty()) {
            customToast.showToast("Nothing In Search")
            return
        }

        searchScrollIndex ?: kotlin.run {
            searchScrollIndex = 0
            val elementToScrollTo = searchIndexes[searchScrollIndex!!]
            scrollTo(elementToScrollTo)
            return
        }

        if (scrollUp) {
            searchScrollIndex = searchScrollIndex!! - 1
            if (searchScrollIndex!! < 0) {
                searchScrollIndex = searchIndexes.size - 1
            }

        } else {
            searchScrollIndex = searchScrollIndex!! + 1
            if (searchScrollIndex == searchIndexes.size) {
                searchScrollIndex = 0
            }

        }
        customToast.showToast("ScrollUp $scrollUp and index $searchScrollIndex")

        scrollTo(searchIndexes[searchScrollIndex!!])
    }

    private fun playOrPauseButtonClick() {

        val audioIsPlaying = isPlaying()
        val audioIsPaused = isPaused()
        val ayahUiState = ayahViewModel.ayahUiState.value!!


        val notifyUserAboutReciterSelection =
            ayahViewModel.ayahUiState.value?.notifyUserAboutReciterSelection!!

        val notifyUserToDownload =
            ayahUiState.downloadedAudios.isEmpty() || ayahUiState.notifyUserToDownloadRemaining
        val displayDownloadDialog = notifyUserToDownload &&
                !ayahUiState.playFromInternet && !currentSurahIsPlaying
                && !notifyUserAboutReciterSelection
        val displayReciterDialog = notifyUserAboutReciterSelection && !currentSurahIsPlaying
        //when reciter notification is not not showing

        if (ayahUiState.isLoading) {
            //Animate Downloading
            return
        }


        // Done
        if (displayReciterDialog) {
            displayReciterErrorDialog()
            return
        }

        if (displayDownloadDialog) {
            displayDownloadDialog()
            return
        }

        if (ayahUiState.playFromInternet && !currentSurahIsPlaying) {
            playFromInternetClick {
                playFromInternet()
            }
            return
        }
        if (!currentSurahIsPlaying) {
            startPlaying()
            return
        }
        if (audioIsPlaying) {
            pausePlaying()
            return
        }
        if (audioIsPaused) {
            resumePlaying()
            return
        }

    }

    private fun playFromInternet() {

        ayahViewModel.handleUiEvent(AyahUIEvent.PlayFromInternet)

    }

    private fun pausePlaying() {
        mediaController?.transportControls?.pause()
    }

    private fun resumePlaying() {
        mediaController?.transportControls?.play()

    }

    private fun startPlaying() {
        val isPlayingOrPaused = isPlayedOrPaused()
        if (isPlayingOrPaused) {
            mediaController?.transportControls?.stop()
        }
        ayahViewModel.handleUiEvent(AyahUIEvent.PlayAyah)
        currentSurahIsPlaying = true
    }

    private fun displayReciterErrorDialog() {
        showReciterError(
            {
                ayahViewModel.handleUiEvent(AyahUIEvent.SwitchReciter)
            }
        ) {
            playFromInternet()
        }
        return
    }

    private fun displayDownloadDialog() {
        showDownloadDialog(
            {
                ayahViewModel.handleUiEvent(AyahUIEvent.DownloadClick)

            }
        ) {
            playFromInternet()
        }
        return
    }

    private fun closeDialogButtonClick() {
        fragmentAyahBinding.customDialog.closeButton.setOnClickListener {
            hideDialog()
        }
    }

    private fun startDownloadService(surahDownloadDto: SurahDownloadDto) {
        startService = true
        val intent = Intent(requireActivity(), DownloadService::class.java)
        val encodeToString = Json.encodeToString(surahDownloadDto)
        Log.d("TAG", "ENCODE TO STRING $encodeToString")
        intent.putExtra("surah_download", encodeToString)
        intent.setPackage(requireContext().packageName)
        requireActivity().startService(intent)

    }

    private fun setAdapter(ayahs: List<Verse>) {
        ayahAdapter.changeAyahs(ayahs)
    }

    private val scrollListener: RecyclerView.OnScrollListener =
        object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    visibleItemOffset = recyclerView.computeVerticalScrollOffset()
                    firstVisibleItemId = linearLayoutManager.findFirstVisibleItemPosition()
                    recyclerView.computeVerticalScrollOffset()
                }
                super.onScrollStateChanged(recyclerView, newState)
            }
        }

    private fun bindAdapterToRecyclerView() {
        //fragmentAyahBinding.surahListRecycler.layoutManager = linearLayoutManager
        fragmentAyahBinding.surahListRecycler.apply {
            layoutManager = linearLayoutManager
            adapter = ayahAdapter
            addOnScrollListener(scrollListener)
        }
    }

    private fun sendToastEvent(ayahUiState: AyahUiState) {
        val messages = ayahUiState.messages
        val messageIsEmpty = messages.isEmpty()
        if (messageIsEmpty) {
            return
        }
        val currentMessage = messages[0]
        showToastError(currentMessage)
        ayahViewModel.removeFromMessages()
    }

    private fun setPlayIcon() {
        fragmentAyahBinding.playIcon.setImageResource(R.drawable.play_icon)
    }

    private fun setPauseIcon() {
        fragmentAyahBinding.playIcon.setImageResource(R.drawable.pause_icon)
    }

    private fun setAnimation() {
        val animation = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation)
        fragmentAyahBinding.surahListRecycler.layoutAnimation = animation
        // isAnimated = true


    }

    private fun searchChanges(searchQuery: String?, adapter: AyahAdapter) {
        searchQuery?.let {
            ayahViewModel.handleUiEvent(AyahUIEvent.OnSearchChange(it))
            val normalizeSearch = ayahViewModel.ayahUiState.value?.normalizedSearchText
            adapter.changeSearchText(normalizeSearch!!)

        }
    }


    private fun searchClick(searchQuery: String?) {
        searchQuery?.let {
            ayahViewModel.handleUiEvent(AyahUIEvent.SearchClick(it))
        }

        showAppIcon()
    }

    private fun handleSearchButtonEvent(adapter: AyahAdapter) {
        val searchQueryListener: SearchView.OnQueryTextListener =
            object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(searchQuery: String?): Boolean {
                    searchClick(searchQuery)
                    return true
                }

                override fun onQueryTextChange(searhQuery: String?): Boolean {
                    searchChanges(searhQuery, adapter)
                    fragmentAyahBinding.ayahCount.setText(searchIndexes.size.toString())
                    return true
                }
            }

        val closeListener = SearchView.OnCloseListener {
            val isSearchEmpty = fragmentAyahBinding.search.query.isEmpty()
            if (isSearchEmpty) {
                showAppIcon()
                return@OnCloseListener true
            }
            //set search bar empty
            fragmentAyahBinding.search.setQuery("", false)
            true
        }

        fragmentAyahBinding.search.apply {
            setOnSearchClickListener {
                searchIconClick()
            }
            setOnQueryTextListener(searchQueryListener)

            setOnCloseListener(closeListener)

        }
    }

    private fun searchIconClick() {
        hideAppIcons()
        fragmentAyahBinding.ayahCount.visibility = View.VISIBLE
        fragmentAyahBinding.actionDown.visibility = View.VISIBLE
        fragmentAyahBinding.actionUp.visibility = View.VISIBLE

    }

    private fun hideAppIcons() {
        fragmentAyahBinding.surahName.visibility = View.INVISIBLE
        fragmentAyahBinding.bookmarkIcon.visibility = View.GONE
        fragmentAyahBinding.playingMenu.visibility = View.GONE
        fragmentAyahBinding.surahDetails.root.visibility = View.GONE
        fragmentAyahBinding.backButton.visibility = View.GONE
        expandSearch()

    }

    private fun expandSearch() {

        val searchIconParam = fragmentAyahBinding.search.layoutParams as LayoutParams

        // set the start to start of parent

        //  searchIconParam.startToStart = fragmentAyahBinding.ayahFragment.id
        searchIconParam.endToStart = fragmentAyahBinding.actionUp.id
        searchIconParam.marginEnd = 5
        searchIconParam.leftMargin = 10
        fragmentAyahBinding.search.layoutParams = searchIconParam
        fragmentAyahBinding.search.requestLayout()
        fragmentAyahBinding.search.setIconifiedByDefault(false) //show search bar

    }


    private fun showAppIcon() {
        //search bar background is transparent
        fragmentAyahBinding.search.setIconifiedByDefault(true) //hide search bar
        fragmentAyahBinding.bookmarkIcon.visibility = View.VISIBLE
        fragmentAyahBinding.backButton.visibility = View.VISIBLE
        fragmentAyahBinding.surahName.visibility = View.VISIBLE
        fragmentAyahBinding.search.setQuery("", false)
        fragmentAyahBinding.search.clearFocus()
        fragmentAyahBinding.search.setBackgroundResource(R.drawable.white_background)
        fragmentAyahBinding.playingMenu.visibility = View.VISIBLE
        fragmentAyahBinding.surahDetails.root.visibility = View.VISIBLE
        hideSearchOptions()


    }

    private fun hideSearchOptions() {
        fragmentAyahBinding.ayahCount.visibility = View.GONE
        fragmentAyahBinding.ayahCount.setText("")
        fragmentAyahBinding.actionDown.visibility = View.GONE
        fragmentAyahBinding.actionUp.visibility = View.GONE


        val layoutParams = fragmentAyahBinding.search.layoutParams as LayoutParams
        // layoutParams.startToStart = fragmentAyahBinding.bookmarkIcon.id
        layoutParams.endToStart = fragmentAyahBinding.bookmarkIcon.id
        //  layoutParams.startToStart = fragmentAyahBinding.ayahFragment.id
        //layoutParams.marginStart = 30
        fragmentAyahBinding.search.layoutParams = layoutParams
        fragmentAyahBinding.search.requestLayout()


    }

    private fun showDownloadProgress(isloading: Boolean) {
        if (isloading) {
            fragmentAyahBinding.progressBar.visibility = View.VISIBLE
        } else {
            fragmentAyahBinding.progressBar.visibility = View.GONE
        }
    }

    private fun searchIsVisible(): Boolean {
        return !fragmentAyahBinding.search.isIconified
    }

    private fun isPaused(): Boolean {
        val currentState = mediaController?.playbackState?.state

        return currentState == PlaybackStateCompat.STATE_PAUSED
    }

    private fun isPlaying(): Boolean {

        val currentState = mediaController?.playbackState?.state
        return currentState == PlaybackStateCompat.STATE_PLAYING

    }

    private fun isPlayedOrPaused(): Boolean {
        return isPlaying() || isPaused()
    }


    private fun showDownloadDialog(
        download: () -> Unit,
        playFromInternet: () -> Unit,
    ) {
        showDialog(
            title = "Download Surah",
            description = "It appears that you have not download this surah on your local device or some part are missing. Do you want to download it now",
            positiveTextButton = "Download",
            positiveButtonClick = {
                downloadButtonClick(download)
            },
            negativeButtonClick = {
                playFromInternetClick(playFromInternet)
            }
        )

    }

    private fun showReciterError(
        switchReciter: () -> Unit,
        playFromInternet: () -> Unit,
    ) {
        showDialog(
            title = "Switch Reciter",
            description = "The reciter you choose to play audio from can only be played online, you can switch Abdurrahmaan As-Sudais or play online",
            positiveTextButton = "Switch To Sudais",
            positiveButtonClick = {
                hideDialog()
                switchReciter()
            },
            negativeButtonClick = {
                playFromInternetClick(playFromInternet)
            }
        )

    }

    private fun downloadButtonClick(
        download: () -> Unit,
    ) {
        try {
            accessInternetConnection()
            hideDialog()
            download()
        } catch (error: CustomError) {
            showInternetError()
        } catch (error: Exception) {
            showToastError(error.message ?: "Unknown Error")
        }
    }

    private fun playFromInternetClick(
        playFromInternet: () -> Unit
    ) {
        try {
            accessInternetConnection()
            hideDialog()
            playFromInternet()
        } catch (error: CustomError) {
            showInternetError()
        } catch (error: Exception) {
            showToastError(error.message ?: "Unknown Error")
        }
    }

    private fun showToastError(errorMessage: String) {
        val customToast = CustomToast(requireContext())
        customToast.showToast(
            errorMessage, R.drawable.custom_toast_yellow_background, R.drawable.baseline_error_24
        )
    }

    private fun showInternetError() {
        val customToast = CustomToast(requireContext())
        customToast.showToast(
            "No Internet Connection",
            R.drawable.custom_toast_yellow_background,
            R.drawable.no_internet
        )
    }

    private fun accessInternetConnection() {
        if (!internetIsAvailable()) {
            throw CustomError(error = "Internet Connection Error")
        }
    }

    private fun internetIsAvailable(): Boolean {
        return internetConnectionState == InternetConnectionState.AVAILABLE
    }


    private fun showDialog(
        title: String,
        description: String,
        positiveTextButton: String,
        positiveButtonClick: () -> Unit,
        negativeButtonClick: () -> Unit
    ) {

        fragmentAyahBinding.customDialog.root.visibility = View.VISIBLE
        fragmentAyahBinding.customDialog.closeButton.visibility = View.VISIBLE
        fragmentAyahBinding.customDialog.apply {
            root.visibility = View.VISIBLE
            dialogTitle.text = title
            dialogDescription.text = description
            positiveButton.text = positiveTextButton
            negativeButton.text = "Play From Internet"
            positiveButton.setOnClickListener {
                positiveButtonClick()
            }
            negativeButton.setOnClickListener {
                negativeButtonClick()
            }
        }

    }

    private fun hideDialog() {
        fragmentAyahBinding.customDialog.root.visibility = View.INVISIBLE
    }


    private val connectionCallback: MediaBrowserCompat.ConnectionCallback = object :
        MediaBrowserCompat.ConnectionCallback() {
        override fun onConnected() {
            super.onConnected()
            // Get the token for the MediaSession
            mediaBrowserCompat?.sessionToken.also { token ->
                // Create a MediaControllerCompat
                val mediaController = MediaControllerCompat(requireContext(), token!!)
                // Save the controller
                MediaControllerCompat.setMediaController(requireActivity(), mediaController)
            }
            val mediaId = "media_root_id"
            mediaBrowserCompat?.subscribe(mediaId, subscriptionCallback)
            // Finish building the UI
            buildTransportControls()
        }

        override fun onConnectionFailed() {
            super.onConnectionFailed()
        }

        override fun onConnectionSuspended() {

        }
    }

    private val subscriptionCallback = object : MediaBrowserCompat.SubscriptionCallback() {
        override fun onChildrenLoaded(
            parentId: String,
            children: MutableList<MediaBrowserCompat.MediaItem>
        ) {
            super.onChildrenLoaded(parentId, children)
            if (children.isEmpty()) {
                return
            }

            val firstChildrenId = children[0].description.extras?.getInt("surahId")
            if (firstChildrenId == surahId && isPlayedOrPaused()) {
                currentSurahIsPlaying = true
                if (isPlaying()) {
                    hideSurahDetails()
                    setPauseIcon()
                } else if (isPaused()) {
                    //showSurahDetails()
                    setPlayIcon()
                }

            }

        }
    }

    fun buildTransportControls() {
        mediaController = MediaControllerCompat.getMediaController(requireActivity())

        // Register a Callback to stay in sync
        mediaController?.registerCallback(controllerCallback)
    }

    private var controllerCallback: MediaControllerCompat.Callback = object :
        MediaControllerCompat.Callback() {

        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            super.onPlaybackStateChanged(state)
            val playingStateChangeToPause = state?.state == PlaybackStateCompat.STATE_PAUSED
            val playingStateChangeToPlaying = state?.state == PlaybackStateCompat.STATE_PLAYING

            if (playingStateChangeToPause) {
                //showSurahDetails()
                setPlayIcon()
            }
            if (playingStateChangeToPlaying) {
                hideSurahDetails()
                setPauseIcon()
            }

        }

        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            super.onMetadataChanged(metadata)
            val lastVisiblePosition = linearLayoutManager.findLastVisibleItemPosition() + 1
            val currentNumberInSurah = metadata?.getLong("numberInSurah")!!.toInt()
            val currentIdInQuran = metadata.description?.mediaId?.toInt()!!

            //If a surah play, then the first item should be the one playing
//            val verseNotInView =
//                currentNumberInSurah > lastVisiblePosition || lastVisiblePosition > currentNumberInSurah
//            if (verseNotInView && currentSurahIsPlaying && autoScroll) {
//                scrollTo(currentNumberInSurah)
//            }
            if (currentSurahIsPlaying && autoScroll) {
                scrollTo(currentNumberInSurah)
            }
            if (lastPlayedId != currentIdInQuran) {
                setPlayingId(currentIdInQuran)
                lastPlayedId = currentIdInQuran
            }

        }
    }

    private fun hideSurahDetails() {
        val alreadyHidden = fragmentAyahBinding.surahDetails.root.isGone
        if (alreadyHidden) {
            return
        }
        fragmentAyahBinding.surahDetails.root.visibility = View.GONE
        fragmentAyahBinding.surahListRecycler.top = fragmentAyahBinding.surahName.bottom
    }

    private fun showSurahDetails() {
        val alreadyShown = fragmentAyahBinding.surahDetails.root.isVisible
        if (phoneIsSmallDevice || alreadyShown) {
            return
        }
        fragmentAyahBinding.surahDetails.root.visibility = View.VISIBLE
        fragmentAyahBinding.surahListRecycler.top = fragmentAyahBinding.surahDetails.root.bottom

    }

    private fun scrollTo(index: Int) {
        // fragmentAyahBinding.surahListRecycler.smoothScrollToPosition(index - 1)
        linearLayoutManager.scrollToPositionWithOffset(index - 1, 0)
        //  fragmentAyahBinding.surahListRecycler.scrollToPosition(index-1)

    }

    private fun setPlayingId(id: Int) {
        ayahAdapter.changePlayingId(id)

    }


    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onResume() {
        super.onResume()
        val bottomNavigation = activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigation?.visibility = View.GONE
        Log.d("TAG", "ONRESUME")
        val intentFilter = IntentFilter(ACTION_COMPLETE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireContext().registerReceiver(
                broadcastReceiver, intentFilter,
                Context.RECEIVER_NOT_EXPORTED
            )
        } else {
            requireContext().registerReceiver(broadcastReceiver, intentFilter)
        }
        ayahViewModel.handleUiEvent(ayahUIEvent = AyahUIEvent.GetAllVerse)


    }

    override fun onPause() {
        shouldScroll = false
        Log.d("TAG", "ONPAUSE")

        val bottomNavigation = activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)

        bottomNavigation?.visibility = View.GONE
        ayahViewModel.handleUiEvent(
            AyahUIEvent.NavigatedFromScreen(
                firstVisibleItemId,
                visibleItemOffset
            )
        )
        super.onPause()


    }

    override fun onDestroy() {
        super.onDestroy()
        mediaBrowserCompat?.disconnect()
        mediaBrowserCompat?.unsubscribe("media_id")
        requireContext().unregisterReceiver(broadcastReceiver)

    }


}

