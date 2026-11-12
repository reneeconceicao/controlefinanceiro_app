package com.firebaseapp.controlefinanceiro.helpers

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.core.content.ContextCompat
import com.firebaseapp.compose_lucide_icons.helpers.IconsHelper.allIcons
import com.firebaseapp.controlefinanceiro.R

class ColorsHelper(context: Context) {
    private val chartColors: Map<String, Color> =
        context.resources.obtainTypedArray(
            R.array.chart_colors
        ).let { array ->
            buildMap {
                for (i in 0 until array.length()) {
                    val id = array.getResourceId(i, 0)
                    if (id != 0) {
                        val name = context.resources.getResourceEntryName(id)
                        put(name, Color(ContextCompat.getColor(context, id)))
                    }
                }
                array.recycle()
            }
        }


    @Composable
    fun colorByName(colorName: String): Color = chartColors[colorName] ?: Color.Transparent

    fun colorByNameNonComposable(colorName: String): Color = chartColors[colorName] ?: Color.Transparent

}