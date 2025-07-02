package com.example.periodtracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class notification : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val answers = intent.getStringArrayListExtra("answers")
        val dbHelper = DatabaseHelper(this)
        val pastAnswers = dbHelper.getAllAnswers()
        setContent {
            GreetingScreen(answers, pastAnswers)
        }
    }
}

@Composable
fun GreetingScreen(answers: List<String?>?, pastAnswers: List<Pair<String, String>>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Current Answers:")
            Spacer(modifier = Modifier.height(8.dp))
        }

        answers?.forEachIndexed { index, answer ->
            item {
                Text(text = "Question ${index + 1}: ${answer ?: "No answer"}")
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Past Answers:")
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(pastAnswers) { (question, answer) ->
            Text(text = "$question: $answer")
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
