package com.example.eduapp.screen

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.eduapp.helper.rememberAssetImage

@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun GameScreen(currentContext: Context, navController: NavHostController,
               imagePath: String = "2/level02_pic03_4.jpg",
               modifier: Modifier = Modifier) {
    val imageBitmap = rememberAssetImage(imagePath)
    Scaffold(
        topBar = { TopAppBar(title = { Text("Game Screen") }) }
    ) {
            innerPadding ->
        Column(modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {
            Text(
                text = "Displaying Asset Image",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (imageBitmap != null) {
                Image(
                    bitmap = imageBitmap,
                    contentDescription = "Asset Image: $imagePath",
                    modifier = Modifier.size(300.dp)
                )
            } else {
                Text(
                    text = "Error: Could not load image at $imagePath",
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Path: assets/$imagePath",
                style = MaterialTheme.typography.bodySmall
            )
            Button(onClick = {navController.navigate("score")})
            { Text("Go to Score") }
        }
    }
}
