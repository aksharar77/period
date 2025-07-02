package com.example.periodtracker

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

class Form : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PeriodTrackerApp()
        }
    }
}

@Composable
fun PeriodTrackerApp() {
    val questions = listOf(
        "1. How often do you have periods?" to listOf("Regular", "Irregular"),
        "2. How many days does your period usually last?" to null,
        "3. Which symptoms do you usually experience during your period?" to listOf(
            "Cramps", "Fatigue", "Headaches", "Mood Swings"
        ),
        "4. How do you manage period pain?" to listOf("Painkillers", "Heat Therapy", "Other"),
        "5. Do you experience heavy bleeding during your periods?" to listOf("Yes", "No")
    )

    // State for answers
    val answers = remember { mutableStateOf(List<String?>(questions.size) { null }) }
    val showAnswers = remember { mutableStateOf(false) }

    // Background color and image
    val backgroundImage = painterResource(id = R.drawable.period)
    val context = LocalContext.current

    // Scrollable Column
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Gray) // Apply background color to the whole Box
    ) {
        Image(
            painter = backgroundImage,
            contentDescription = "Background Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds // Ensure the image covers the entire screen
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()) // Make the Column scrollable
        ) {
            // Loop through each question
            questions.forEachIndexed { index, (question, options) ->
                Text(text = question, modifier = Modifier.padding(top = 16.dp))

                if (options != null) {
                    // Checkbox options
                    options.forEach { option ->
                        Row(
                            modifier = Modifier.padding(bottom = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = answers.value[index] == option,
                                onCheckedChange = { isChecked ->
                                    val newAnswers = answers.value.toMutableList()
                                    newAnswers[index] = if (isChecked) option else null
                                    answers.value = newAnswers
                                },
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(option)
                        }
                    }
                } else {
                    // Input field for number of days
                    OutlinedTextField(
                        value = answers.value[index] ?: "",
                        onValueChange = { newValue ->
                            val newAnswers = answers.value.toMutableList()
                            newAnswers[index] = newValue
                            answers.value = newAnswers
                        },
                        label = { Text("Enter number of days") }
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Submit Button
            Button(
                onClick = {
                    submitForm(context, questions, answers.value)
                    showAnswers.value = true // Show answers after submission
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Submit")
            }

          

            // Show answers after submission
            if (showAnswers.value) {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Your Answers:")
                questions.forEachIndexed { index, (question, _) ->
                    Text("${question}: ${answers.value[index] ?: "No answer"}")
                }
            }
        }
    }
}

fun submitForm(context: Context, questions: List<Pair<String, List<String>?>>, answers: List<String?>) {
    val dbHelper = DatabaseHelper(context)

    // Save answers to the database
    questions.forEachIndexed { index, pair ->
        val question = pair.first
        val answer = answers[index] ?: "No answer"
        dbHelper.insertAnswer(question, answer)
    }

    // Show a toast notification
    Toast.makeText(context, "Form submitted successfully!", Toast.LENGTH_LONG).show()
}



