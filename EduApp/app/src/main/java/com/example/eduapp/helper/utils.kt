package com.example.eduapp.helper

import android.content.Context
import android.graphics.BitmapFactory
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import java.io.IOException
import java.io.InputStream

/**
 * Utility function to load an ImageBitmap from an asset path.
 */
fun loadAssetImage(context: Context, path: String): ImageBitmap? {
    return try {
        // Explicitly specifying the type 'InputStream' helps resolve the 'use' extension
        context.assets.open(path).use { inputStream: InputStream ->
            BitmapFactory.decodeStream(inputStream)?.asImageBitmap()
        }
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }
}

/**
 * Composable function that remembers the loaded ImageBitmap.
 */
@Composable
fun rememberAssetImage(path: String): ImageBitmap? {
    val context = LocalContext.current
    return remember(path) {
        loadAssetImage(context, path)
    }
}
