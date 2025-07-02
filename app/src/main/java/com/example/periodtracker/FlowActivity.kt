package com.example.periodtracker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class FlowActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MenstruationReliefApp()
        }
    }
}

@Composable
fun MenstruationReliefApp() {
    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Box {
                // Background Image
                Image(
                    painter = painterResource(id = R.drawable.pink), // Replace with your background image resource
                    contentDescription = "Background",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                // Main Content
                MenstruationReliefContent()
            }
        }

    }
}

@Composable
fun MenstruationReliefContent() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text(
                text = "Menstruation Relief",
                fontSize = 24.sp,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        // Image 1: Heating Pad
        item {
            ReliefItem(
                imageRes = R.drawable.heating_pad,
                description = "Heating pads can help relieve cramps.",
                linkText = "Learn more about heating pads",
                linkUrl = "https://www.healthline.com/health/heating-pad-for-back-pain" // Heating pad URL
            )
        }

        // Image 2: Yoga Pose
        item {
            ReliefItem(
                imageRes = R.drawable.yoga_pose,
                description = "Gentle yoga poses promote relaxation and ease discomfort.",
                linkText = "Explore yoga poses",
                linkUrl = "https://www.youtube.com/watch?v=VaVIvmQx_Xw" // Yoga poses URL
            )
        }

        // Image 3: Herbal Tea
        item {
            ReliefItem(
                imageRes = R.drawable.herbal_tea,
                description = "Herbal teas, like chamomile, soothe the body.",
                linkText = "Benefits of herbal tea",
                linkUrl = "https://www.youtube.com/watch?v=lc7XvYAOq-Q" // Herbal tea URL
            )
        }

        // Diet Plan Button - At the end of the list
        item {
            DietPlanButton()
        }
    }
}

@Composable
fun ReliefItem(
    imageRes: Int,
    description: String,
    linkText: String,
    linkUrl: String // Add linkUrl parameter to handle different URLs for each item
) {
    val context = LocalContext.current // Access context here

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent) // Make card transparent
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize() // Ensure the column fills available space
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Image
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = null,
                modifier = Modifier
                    .size(200.dp)
                    .padding(bottom = 8.dp),
                contentScale = ContentScale.Crop
            )
            // Description Text
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp)
            )
            // Link Text - Make it clickable
            androidx.compose.foundation.text.ClickableText(
                text = AnnotatedString(linkText),
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color.Blue,
                    textDecoration = TextDecoration.Underline
                ),
                onClick = { offset ->
                    // Handle the link click event, open URL
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(linkUrl))
                    context.startActivity(intent)
                },
                modifier = Modifier
                    .padding(top = 8.dp)
                    .align(Alignment.CenterHorizontally)
            )
            // Spacer to push the button to the bottom of the card
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun DietPlanButton() {
    val context = LocalContext.current // Access context here
    Button(
        onClick = {
            val intent = Intent(context, DietPlanActivity::class.java)
            context.startActivity(intent)
        },
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Text(text = "Diet Plan")
    }
}
