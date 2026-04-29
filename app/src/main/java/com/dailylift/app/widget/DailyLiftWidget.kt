package com.dailylift.app.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.clickable
import androidx.glance.action.actionStartActivity
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.FontStyle
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.dailylift.app.MainActivity
import com.dailylift.app.data.QuotesData
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DailyLiftWidget : GlanceAppWidget() {

    override val sizeMode = SizeMode.Single

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val quote = QuotesData.todayQuote()
        val dateStr = SimpleDateFormat("MMM d", Locale.getDefault()).format(Date())

        // Convert Compose color to Android color int for Glance
        val gradientColor = quote.category.gradientColors[0]
        val colorInt = android.graphics.Color.argb(
            (gradientColor.alpha * 255).toInt(),
            (gradientColor.red * 255).toInt(),
            (gradientColor.green * 255).toInt(),
            (gradientColor.blue * 255).toInt()
        )

        provideContent {
            GlanceTheme {
                WidgetContent(
                    quoteText = quote.text,
                    author = quote.author,
                    categoryName = quote.category.displayName,
                    dateString = dateStr,
                    bgColorInt = colorInt
                )
            }
        }
    }
}

@Composable
private fun WidgetContent(
    quoteText: String,
    author: String,
    categoryName: String,
    dateString: String,
    bgColorInt: Int
) {
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(androidx.glance.color.ColorProvider(
                day = androidx.compose.ui.graphics.Color(bgColorInt),
                night = androidx.compose.ui.graphics.Color(bgColorInt)
            ))
            .clickable(actionStartActivity<MainActivity>())
            .cornerRadius(16.dp)
            .padding(14.dp)
    ) {
        // Header
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = GlanceModifier.defaultWeight()) {
                Text(
                    text = "DAILY LIFT",
                    style = TextStyle(
                        color = ColorProvider(Color.White.copy(alpha = 0.6f)),
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = dateString,
                    style = TextStyle(
                        color = ColorProvider(Color.White.copy(alpha = 0.85f)),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
            }
            Text(
                text = categoryName.uppercase(),
                style = TextStyle(
                    color = ColorProvider(Color.White.copy(alpha = 0.7f)),
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }

        Spacer(modifier = GlanceModifier.height(8.dp))

        // Quote
        Text(
            text = "\u201C$quoteText\u201D",
            style = TextStyle(
                color = ColorProvider(Color.White),
                fontSize = 13.sp,
                fontStyle = FontStyle.Italic
            ),
            maxLines = 4
        )

        Spacer(modifier = GlanceModifier.defaultWeight())

        // Author
        Text(
            text = "— $author",
            style = TextStyle(
                color = ColorProvider(Color.White.copy(alpha = 0.75f)),
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium
            )
        )
    }
}

class DailyLiftWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = DailyLiftWidget()
}
