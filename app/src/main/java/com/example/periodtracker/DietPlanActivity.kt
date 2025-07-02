package com.example.periodtracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class DietPlanActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DietPlanScreen()
        }
    }
}

@Composable
fun DietPlanScreen() {
    val backgroundImage = painterResource(id = R.drawable.food) // Replace with your image resource

    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // Background Image
                Image(
                    painter = backgroundImage,
                    contentDescription = "Background Image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Main Content (Wrap in LazyColumn for scrollable content)
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    item {
                        Text(
                            text = "5-Day Diet Plan for Menstrual Cycle",
                            fontSize = 24.sp,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }

                    // Day 1: Focus on Iron and Hydration
                    item {
                        FlowChartBox("Day 1",
                            "Iron-rich foods, Hydration\n" +
                                    "Leafy greens (Spinach, Kale), Red meat, Water, \n" +
                                    "  Beetroot, Pumpkin seeds, Tofu",
                            Color.Red
                        )
                    }

                    // Day 2: Focus on Iron and Hydration
                    item {
                        FlowChartBox("Day 2",
                            "Iron-rich foods, Hydration\n" +
                                    "Spinach, Lentils, Coconut Water, \n" +
                                    "  Chickpeas, Red bell peppers, Watermelon",
                            Color.Magenta
                        )
                    }

                    // Day 3: Focus on Comfort Foods
                    item {
                        FlowChartBox("Day 3",
                            "Comfort foods for energy\n" +
                                    "Whole Grains (Oats, Quinoa), Chicken, Green Tea, \n" +
                                    "  Sweet Potatoes, Avocados, Yogurt",
                            Color.Yellow
                        )
                    }

                    // Day 4: Focus on Healthy Fats and Fiber
                    item {
                        FlowChartBox("Day 4",
                            "Healthy Fats and Fiber\n" +
                                    "Avocados, Nuts (Almonds, Walnuts), Oats, \n" +
                                    "  Chia seeds, Flaxseeds, Olive oil",
                            Color.Green
                        )
                    }

                    // Day 5: Focus on Relaxation and Comfort
                    item {
                        FlowChartBox("Day 5",
                            "Foods to support energy and calm\n" +
                                    "Dark Chocolate, Banana, Herbal Tea (Chamomile, Peppermint), \n" +
                                    "  Berries, Almond butter, Whole Grain crackers",
                            Color.Cyan
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FlowChartBox(title: String, content: String, color: Color) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = CircleShape, // Make the box circular
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.2f)) // Set background color with transparency
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(color = color, fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = content,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
