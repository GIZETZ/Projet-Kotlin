package com.example.musep50.ui

import android.app.Activity
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import com.example.musep50.R

fun Activity.applyEdgeToEdgeFromPrefs() {
    val prefs = getSharedPreferences("musep50_prefs", Activity.MODE_PRIVATE)
    val edgeToEdge = prefs.getBoolean("pref_edge_to_edge", false)

    WindowCompat.setDecorFitsSystemWindows(window, !edgeToEdge)

    val content = findViewById<ViewGroup>(android.R.id.content)
    val root = content.getChildAt(0)
    root?.fitsSystemWindows = !edgeToEdge
}

fun Activity.applyBackgroundFromPrefs() {
    val prefs = getSharedPreferences("musep50_prefs", Activity.MODE_PRIVATE)
    when (prefs.getString("pref_bg_style", "default")) {
        "orange_white" -> setRootBackground(drawable = R.drawable.bg_radial_orange_sky_white)
        "blue_orange" -> setRootBackground(drawable = R.drawable.bg_radial_blue_orange_sky)
        else -> setRootBackground(color = R.color.colorBackground)
    }
}

private fun Activity.setRootBackground(@DrawableRes drawable: Int? = null, @ColorRes color: Int? = null) {
    val content = findViewById<ViewGroup>(android.R.id.content)
    val root = content.getChildAt(0) ?: return
    when {
        drawable != null -> root.background = ContextCompat.getDrawable(this, drawable)
        color != null -> root.setBackgroundColor(ContextCompat.getColor(this, color))
    }
}

fun Activity.applyAppearancePrefs() {
    applyEdgeToEdgeFromPrefs()
    applyBackgroundFromPrefs()
}
