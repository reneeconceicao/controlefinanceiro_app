package com.firebaseapp.controlefinanceiro.widgets

import android.annotation.SuppressLint
import android.appwidget.AppWidgetManager
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.glance.BackgroundModifier
import androidx.glance.Button
import androidx.glance.ButtonDefaults
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.ImageProvider
import androidx.glance.appwidget.components.Scaffold
import androidx.glance.appwidget.components.TitleBar
import androidx.glance.appwidget.provideContent
import androidx.glance.color.ColorProviders
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.Text
import com.firebaseapp.controlefinanceiro.R
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.firebaseapp.controlefinanceiro.MainActivity
import com.firebaseapp.controlefinanceiro.data.entities.WordType
import com.firebaseapp.controlefinanceiro.defaults.padding12
import com.firebaseapp.controlefinanceiro.defaults.paddingSmall

class MoneyManagerAppWidget : GlanceAppWidget() {
   override suspend fun provideGlance(context: Context, id: GlanceId) {
       provideContent {
           GlanceTheme {
               Content()
           }
       }
   }
}

@SuppressLint("RestrictedApi")
@Composable
private fun Content() {
    val widgetIdKey = ActionParameters.Key<Int>(AppWidgetManager.EXTRA_APPWIDGET_ID)
    val context = LocalContext.current
    Scaffold(
        backgroundColor = GlanceTheme.colors.widgetBackground,
        modifier = GlanceModifier.fillMaxSize(),
    ) {
        Box(modifier = GlanceModifier.fillMaxSize(), contentAlignment = Alignment.Center) {

            Column(modifier = GlanceModifier.fillMaxWidth().padding(paddingSmall()), horizontalAlignment = Alignment.Start) {

                Row(modifier = GlanceModifier.fillMaxWidth(), horizontalAlignment = Alignment.End) {
                    Image(
                        modifier = GlanceModifier.size(20.dp).clickable (actionStartActivity<MainActivity>(
                            parameters = actionParametersOf(widgetIdKey to -1)
                        )),
                        provider = ImageProvider(R.drawable.__banknote_arrow_down),
                        contentDescription = null,
                        )
                }

                Button(
                    text = context.getString(R.string.expense),
                    onClick = actionStartActivity<MainActivity>(
                        parameters = actionParametersOf(widgetIdKey to 0),
                    ),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = ColorProvider(R.color.dark_red),
                        contentColor = ColorProvider(Color.White)
                    )
                )

                Spacer(GlanceModifier.height(8.dp))

                Button(
                    text = context.getString(R.string.income),
                    onClick = actionStartActivity<MainActivity>(
                        parameters = actionParametersOf(widgetIdKey to 1),
                    ),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = ColorProvider(R.color.dark_green),
                        contentColor = ColorProvider(Color.White)
                    )
                )
            }
        }
//        Text(text = "Hello World", style = TextStyle(color = GlanceTheme.colors.onSurface))
    }
}