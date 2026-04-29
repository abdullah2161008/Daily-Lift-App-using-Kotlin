package com.dailylift.app.ui.components

import android.content.Intent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.outlined.Eco
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dailylift.app.model.Quote
import com.dailylift.app.model.QuoteCategory

@Composable
fun QuoteCardView(
    quote: Quote,
    compact: Boolean = false,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    shareText: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val cornerRadius = if (compact) 16.dp else 20.dp
    val padding = if (compact) 16.dp else 20.dp

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(cornerRadius),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Box {
            // Subtle gradient tint overlay
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(RoundedCornerShape(cornerRadius))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                quote.category.gradientColors[0].copy(alpha = 0.07f),
                                quote.category.gradientColors[1].copy(alpha = 0.03f)
                            )
                        )
                    )
            )

            Column(modifier = Modifier.padding(padding)) {
                // Header: category tag + actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CategoryTag(quote.category)
                    ActionButtons(
                        compact = compact,
                        isFavorite = isFavorite,
                        onToggleFavorite = onToggleFavorite,
                        onShare = {
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, shareText)
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Share Quote"))
                        }
                    )
                }

                Spacer(modifier = Modifier.height(if (compact) 10.dp else 14.dp))

                // Quote text
                Text(
                    text = "\u201C${quote.text}\u201D",
                    fontFamily = FontFamily.Serif,
                    fontSize = if (compact) 15.sp else 18.sp,
                    lineHeight = if (compact) 22.sp else 28.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(if (compact) 12.dp else 16.dp))

                // Author
                Text(
                    text = "— ${quote.author}",
                    fontSize = if (compact) 12.sp else 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun CategoryTag(category: QuoteCategory) {
    val icon = categoryIcon(category)

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(category.accentColor.copy(alpha = 0.12f))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(12.dp),
            tint = category.accentColor
        )
        Text(
            text = category.displayName,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = category.accentColor
        )
    }
}

@Composable
private fun ActionButtons(
    compact: Boolean,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onShare: () -> Unit
) {
    var heartBounce by remember { mutableStateOf(false) }
    val heartScale by animateFloatAsState(
        targetValue = if (heartBounce) 1.35f else 1f,
        animationSpec = spring(dampingRatio = 0.4f, stiffness = 600f),
        finishedListener = { heartBounce = false },
        label = "heartScale"
    )

    val buttonSize = if (compact) 28.dp else 32.dp

    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        IconButton(
            onClick = onShare,
            modifier = Modifier.size(buttonSize)
        ) {
            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = "Share",
                modifier = Modifier.size(if (compact) 14.dp else 16.dp),
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        }

        IconButton(
            onClick = {
                heartBounce = true
                onToggleFavorite()
            },
            modifier = Modifier
                .size(buttonSize)
                .scale(heartScale)
        ) {
            Icon(
                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = "Favorite",
                modifier = Modifier.size(if (compact) 14.dp else 16.dp),
                tint = if (isFavorite) Color(0xFFFF6B8A) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        }
    }
}

fun categoryIcon(category: QuoteCategory): ImageVector {
    return when (category) {
        QuoteCategory.SUCCESS -> Icons.Default.Star
        QuoteCategory.LOVE -> Icons.Outlined.FavoriteBorder
        QuoteCategory.LIFE -> Icons.Outlined.Eco
        QuoteCategory.HUSTLE -> Icons.Default.Bolt
        QuoteCategory.MINDSET -> Icons.Default.Psychology
        QuoteCategory.HAPPINESS -> Icons.Default.WbSunny
    }
}
