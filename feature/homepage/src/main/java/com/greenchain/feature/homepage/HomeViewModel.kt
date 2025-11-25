package com.greenchain.feature.homepage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.greenchain.feature.homepage.data.HomeQuoteRepository
// Eliminam dependenta problematica
// import com.greenchain.feature.profile.data.UserRepository
// import com.greenchain.feature.profile.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore // Folosim Firestore direct
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeQuoteRepository: HomeQuoteRepository,
    private val firestore: FirebaseFirestore, // Injectam Firestore direct
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _quoteText = MutableStateFlow<String?>(null)
    val quoteText: StateFlow<String?> = _quoteText.asStateFlow()

    // Starea quest-ului: null = loading, false = incomplet, true = complet
    private val _isQuestCompleted = MutableStateFlow(false)
    val isQuestCompleted: StateFlow<Boolean> = _isQuestCompleted.asStateFlow()

    init {
        loadRandomQuote()
        observeQuestStatus()
    }

    fun loadRandomQuote() {
        viewModelScope.launch {
            _quoteText.value = homeQuoteRepository.getRandomQuote().text
        }
    }

    private fun observeQuestStatus() {
        val uid = auth.currentUser?.uid ?: return

        // Ascultam documentul utilizatorului direct
        val docRef = firestore.collection("users").document(uid)

        docRef.addSnapshotListener { snapshot, e ->
            if (e != null || snapshot == null || !snapshot.exists()) return@addSnapshotListener

            // Citim manual campul lastDailyQuestDate
            val lastDate = snapshot.getString("lastDailyQuestDate")
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

            _isQuestCompleted.value = (lastDate == today)
        }
    }
}
