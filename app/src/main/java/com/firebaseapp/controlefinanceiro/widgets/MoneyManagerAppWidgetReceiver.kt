package com.firebaseapp.controlefinanceiro.widgets

import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver

class MoneyManagerAppWidgetReceiver : GlanceAppWidgetReceiver() {
   override val glanceAppWidget: GlanceAppWidget = MoneyManagerAppWidget()
}