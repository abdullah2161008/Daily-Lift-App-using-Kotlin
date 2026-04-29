package com.dailylift.app.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dailylift.app.data.QuotesData
import com.dailylift.app.model.Quote
import com.dailylift.app.model.QuoteCategory
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class QuoteViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("DailyLift", Context.MODE_PRIVATE)
    private val gson = Gson()

    // Today and tomorrow quotes
    private val _todayQuote = MutableStateFlow(QuotesData.todayQuote())
    val todayQuote: StateFlow<Quote> = _todayQuote.asStateFlow()

    private val _tomorrowQuote = MutableStateFlow(QuotesData.quoteForDayOffset(1))
    val tomorrowQuote: StateFlow<Quote> = _tomorrowQuote.asStateFlow()

    // Tomorrow preview state
    private val _showingTomorrowPreview = MutableStateFlow(false)
    val showingTomorrowPreview: StateFlow<Boolean> = _showingTomorrowPreview.asStateFlow()

    // All quotes
    val allQuotes: List<Quote> = QuotesData.allQuotes

    // Category filter
    private val _selectedCategory = MutableStateFlow<QuoteCategory?>(null)
    val selectedCategory: StateFlow<QuoteCategory?> = _selectedCategory.asStateFlow()

    // Search
    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText.asStateFlow()

    // Filtered quotes (combines category + search)
    val filteredQuotes: StateFlow<List<Quote>> = combine(
        _selectedCategory,
        _searchText
    ) { category, search ->
        var base = QuotesData.allQuotes

        if (category != null) {
            base = base.filter { it.category == category }
        }

        val trimmed = search.trim()
        if (trimmed.isNotEmpty()) {
            val query = trimmed.lowercase()
            base = base.filter {
                it.text.lowercase().contains(query) ||
                        it.author.lowercase().contains(query)
            }
        }

        base
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), QuotesData.allQuotes)

    // Favorites
    private val _favoriteQuotes = MutableStateFlow<List<Quote>>(emptyList())
    val favoriteQuotes: StateFlow<List<Quote>> = _favoriteQuotes.asStateFlow()

    init {
        loadFavorites()
    }

    // MARK: - Category Filter

    fun selectCategory(category: QuoteCategory?) {
        _selectedCategory.value = category
    }

    // MARK: - Search

    fun updateSearchText(text: String) {
        _searchText.value = text
    }

    // MARK: - Tomorrow Preview

    fun toggleTomorrowPreview() {
        _showingTomorrowPreview.value = !_showingTomorrowPreview.value
    }

    fun setTomorrowPreview(showing: Boolean) {
        _showingTomorrowPreview.value = showing
    }

    // MARK: - Favorites

    fun isFavorite(quote: Quote): Boolean {
        return _favoriteQuotes.value.any { it.id == quote.id }
    }

    fun toggleFavorite(quote: Quote) {
        val current = _favoriteQuotes.value.toMutableList()
        val existing = current.find { it.id == quote.id }
        if (existing != null) {
            current.remove(existing)
        } else {
            current.add(0, quote)
        }
        _favoriteQuotes.value = current
        saveFavorites()
    }

    fun removeFavoriteAt(index: Int) {
        val current = _favoriteQuotes.value.toMutableList()
        if (index in current.indices) {
            current.removeAt(index)
            _favoriteQuotes.value = current
            saveFavorites()
        }
    }

    fun clearAllFavorites() {
        _favoriteQuotes.value = emptyList()
        saveFavorites()
    }

    private fun saveFavorites() {
        viewModelScope.launch {
            val ids = _favoriteQuotes.value.map { it.id }
            val json = gson.toJson(ids)
            prefs.edit().putString("favorite_ids", json).apply()
        }
    }

    private fun loadFavorites() {
        val json = prefs.getString("favorite_ids", null) ?: return
        try {
            val type = object : TypeToken<List<String>>() {}.type
            val ids: List<String> = gson.fromJson(json, type)
            val idSet = ids.toSet()
            _favoriteQuotes.value = QuotesData.allQuotes.filter { it.id in idSet }
        } catch (_: Exception) {
            // Ignore corrupt data
        }
    }

    // MARK: - Refresh

    fun refreshTodayQuote() {
        _todayQuote.value = QuotesData.todayQuote()
        _tomorrowQuote.value = QuotesData.quoteForDayOffset(1)
    }

    // MARK: - Share

    fun shareText(quote: Quote): String {
        return "\"${quote.text}\"\n\n— ${quote.author}\n\nShared via DailyLift"
    }
}
