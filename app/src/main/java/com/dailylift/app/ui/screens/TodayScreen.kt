package com.dailylift.app.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dailylift.app.model.Quote
import com.dailylift.app.ui.QuoteViewModel
import com.dailylift.app.ui.components.categoryIcon
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TodayScreen(viewModel: QuoteViewModel) {
    val todayQuote by viewModel.todayQuote.collectAsState()
    val tomorrowQuote by viewModel.tomorrowQuote.collectAsState()
    val showTomorrow by viewModel.showingTomorrowPreview.collectAsState()
    val context = LocalContext.current

    val displayQuote = if (showTomorrow) tomorrowQuote else todayQuote

    // Appear animation
    var appeared by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { appeared = true }

    val appearAlpha by animateFloatAsState(
        targetValue = if (appeared) 1f else 0f,
        animationSpec = tween(700),
        label = "appear"
    )

    // Gradient animation (floating orbs)
    val infiniteTransition = rememberInfiniteTransition(label = "gradient")
    val orbRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orbRotation"
    )

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Animated gradient background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = displayQuote.category.gradientColors.map {
                            it.copy(alpha = 0.85f)
                        }
                    )
                )
        )

        // Depth overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.2f),
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.05f)
                        )
                    )
                )
        )

        // Floating orbs
        Box(
            modifier = Modifier
                .size(280.dp)
                .offset(x = (-80).dp, y = (-120).dp)
                .rotate(orbRotation * 0.5f)
                .blur(60.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.12f))
        )
        Box(
            modifier = Modifier
                .size(200.dp)
                .offset(x = 100.dp, y = 200.dp)
                .rotate(-orbRotation * 0.3f)
                .blur(50.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.08f))
        )

        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 24.dp)
                .alpha(appearAlpha),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top bar
            TopBar(
                showTomorrow = showTomorrow,
                displayQuote = displayQuote
            )

            Spacer(modifier = Modifier.weight(1f))

            // Quote content
            QuoteContent(
                quote = displayQuote,
                appeared = appeared
            )

            Spacer(modifier = Modifier.weight(1f))

            // Bottom actions
            BottomActions(
                showTomorrow = showTomorrow,
                displayQuote = displayQuote,
                isFavorite = viewModel.isFavorite(displayQuote),
                onToggleFavorite = { viewModel.toggleFavorite(displayQuote) },
                onToggleTomorrow = { viewModel.toggleTomorrowPreview() },
                onShare = {
                    val sendIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, viewModel.shareText(displayQuote))
                        type = "text/plain"
                    }
                    context.startActivity(Intent.createChooser(sendIntent, "Share Quote"))
                },
                onCopy = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("Quote", "\"${displayQuote.text}\" — ${displayQuote.author}")
                    clipboard.setPrimaryClip(clip)
                    Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                }
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun TopBar(showTomorrow: Boolean, displayQuote: Quote) {
    val dateFormat = remember { SimpleDateFormat("MMMM d", Locale.getDefault()) }
    val dateString = remember { dateFormat.format(Date()) }
    val icon = categoryIcon(displayQuote.category)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = if (showTomorrow) "TOMORROW" else "TODAY",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White.copy(alpha = 0.7f),
                letterSpacing = 1.5.sp
            )
            Text(
                text = dateString,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        // Category pill
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(Color.White.copy(alpha = 0.18f))
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = Color.White.copy(alpha = 0.9f)
            )
            Text(
                text = displayQuote.category.displayName,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White.copy(alpha = 0.9f)
            )
        }
    }
}

@Composable
private fun QuoteContent(quote: Quote, appeared: Boolean) {
    val quoteScale by animateFloatAsState(
        targetValue = if (appeared) 1f else 0.92f,
        animationSpec = spring(dampingRatio = 0.8f, stiffness = 300f),
        label = "quoteScale"
    )

    Column(
        modifier = Modifier.scale(quoteScale),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Opening quote mark
        Text(
            text = "\u201C",
            fontFamily = FontFamily.Serif,
            fontSize = 96.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White.copy(alpha = 0.25f),
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = 24.dp),
            textAlign = TextAlign.Start
        )

        // Quote text
        Text(
            text = quote.text,
            fontFamily = FontFamily.Serif,
            fontSize = 26.sp,
            lineHeight = 38.sp,
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 4.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Author
        Text(
            text = "— ${quote.author}",
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White.copy(alpha = 0.75f)
        )
    }
}

@Composable
private fun BottomActions(
    showTomorrow: Boolean,
    displayQuote: Quote,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onToggleTomorrow: () -> Unit,
    onShare: () -> Unit,
    onCopy: () -> Unit
) {
    // Heart animation
    var heartBounce by remember { mutableStateOf(false) }
    val heartScale by animateFloatAsState(
        targetValue = if (heartBounce) 1.4f else 1f,
        animationSpec = spring(dampingRatio = 0.4f, stiffness = 500f),
        finishedListener = { heartBounce = false },
        label = "heartScale"
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // Peek tomorrow / back to today
        TextButton(onClick = onToggleTomorrow) {
            Icon(
                imageVector = if (showTomorrow) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = Color.White.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (showTomorrow) "Back to today" else "Peek at tomorrow",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.6f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Action buttons row
        Row(
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Favorite
            ActionCircleButton(
                onClick = {
                    heartBounce = true
                    onToggleFavorite()
                },
                modifier = Modifier.scale(heartScale)
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    modifier = Modifier.size(22.dp),
                    tint = Color.White
                )
            }

            // Share
            ActionCircleButton(onClick = onShare) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Share",
                    modifier = Modifier.size(20.dp),
                    tint = Color.White
                )
            }

            // Copy
            ActionCircleButton(onClick = onCopy) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = "Copy",
                    modifier = Modifier.size(20.dp),
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
private fun ActionCircleButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .size(54.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.18f))
    ) {
        content()
    }
}
