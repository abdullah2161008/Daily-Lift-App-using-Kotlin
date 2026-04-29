package com.dailylift.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dailylift.app.model.QuoteCategory
import com.dailylift.app.ui.QuoteViewModel
import com.dailylift.app.ui.components.QuoteCardView
import com.dailylift.app.ui.components.categoryIcon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowseScreen(viewModel: QuoteViewModel) {
    val filteredQuotes by viewModel.filteredQuotes.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val searchText by viewModel.searchText.collectAsState()
    var showSearch by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        topBar = {
            Column {
                LargeTopAppBar(
                    title = { Text("Browse") },
                    actions = {
                        IconButton(onClick = {
                            showSearch = !showSearch
                            if (!showSearch) {
                                viewModel.updateSearchText("")
                            }
                        }) {
                            Icon(
                                imageVector = if (showSearch) Icons.Default.Close else Icons.Default.Search,
                                contentDescription = "Search"
                            )
                        }
                    },
                    scrollBehavior = scrollBehavior
                )

                // Search bar
                AnimatedVisibility(
                    visible = showSearch,
                    enter = slideInVertically() + fadeIn(),
                    exit = slideOutVertically() + fadeOut()
                ) {
                    SearchBar(
                        text = searchText,
                        onTextChange = { viewModel.updateSearchText(it) },
                        focusRequester = focusRequester
                    )
                }

                // Category strip
                CategoryStrip(
                    selectedCategory = selectedCategory,
                    onSelectCategory = { viewModel.selectCategory(it) }
                )
            }
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { paddingValues ->
        if (filteredQuotes.isEmpty()) {
            EmptyState(searchText)
        } else {
            LazyColumn(
                contentPadding = PaddingValues(
                    top = paddingValues.calculateTopPadding() + 4.dp,
                    bottom = 100.dp,
                    start = 16.dp,
                    end = 16.dp
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Count header
                item {
                    Text(
                        text = "${filteredQuotes.size} quotes",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                items(filteredQuotes, key = { it.id }) { quote ->
                    QuoteCardView(
                        quote = quote,
                        compact = true,
                        isFavorite = viewModel.isFavorite(quote),
                        onToggleFavorite = { viewModel.toggleFavorite(quote) },
                        shareText = viewModel.shareText(quote)
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchBar(
    text: String,
    onTextChange: (String) -> Unit,
    focusRequester: FocusRequester
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )

        Spacer(modifier = Modifier.width(10.dp))

        Box(modifier = Modifier.weight(1f)) {
            if (text.isEmpty()) {
                Text(
                    text = "Search quotes or authors...",
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                )
            }
            BasicTextField(
                value = text,
                onValueChange = onTextChange,
                textStyle = TextStyle(
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary)
            )
        }

        if (text.isNotEmpty()) {
            IconButton(
                onClick = { onTextChange("") },
                modifier = Modifier.size(20.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Clear",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        }
    }
}

@Composable
private fun CategoryStrip(
    selectedCategory: QuoteCategory?,
    onSelectCategory: (QuoteCategory?) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // "All" pill
        CategoryPill(
            title = "All",
            icon = Icons.Default.Explore,
            isSelected = selectedCategory == null,
            accentColor = MaterialTheme.colorScheme.primary,
            onClick = { onSelectCategory(null) }
        )

        QuoteCategory.entries.forEach { category ->
            CategoryPill(
                title = category.displayName,
                icon = categoryIcon(category),
                isSelected = selectedCategory == category,
                accentColor = category.accentColor,
                onClick = {
                    onSelectCategory(if (selectedCategory == category) null else category)
                }
            )
        }
    }
}

@Composable
private fun CategoryPill(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    accentColor: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(
                if (isSelected) accentColor else accentColor.copy(alpha = 0.12f)
            )
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(13.dp),
            tint = if (isSelected) androidx.compose.ui.graphics.Color.White else accentColor
        )
        Text(
            text = title,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (isSelected) androidx.compose.ui.graphics.Color.White else accentColor
        )
    }
}

@Composable
private fun EmptyState(searchText: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            modifier = Modifier.size(52.dp),
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "No Quotes Found",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (searchText.isEmpty()) {
                "Try selecting a different category."
            } else {
                "No results for \"$searchText\". Try a different search."
            },
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            textAlign = TextAlign.Center
        )
    }
}
