package com.example.periodtracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import com.example.periodtracker.ui.theme.black
import kotlinx.coroutines.launch
import android.content.Intent
import androidx.compose.ui.platform.LocalContext


private const val CALENDAR_ROWS = 5
private const val CALENDAR_COLUMNS = 7

class prediction : ComponentActivity() {
    private val months = listOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )

    // Mutable state to hold history records
    private var historyRecords by mutableStateOf(mutableStateListOf<HistoryRecord>())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var currentMonthIndex by remember { mutableStateOf(0) }
            var clickedCalendarElem by remember { mutableStateOf<CalendarInput?>(null) }
            var showQuestionnaire by remember { mutableStateOf(false) }
            var responses by remember { mutableStateOf(mapOf<String, String>()) }
            var showThankYouPage by remember { mutableStateOf(false) }
            var calculatedDate by remember { mutableStateOf("") }
            var showHistoryScreen by remember { mutableStateOf(false) }

            // Function to add a history record
            fun addToHistory(day: Int, month: String, year: Int, responses: Map<String, String>) {
                val historyRecord = HistoryRecord(day, month, year, responses)
                historyRecords.add(historyRecord)
                showHistoryScreen = true
            }

            if (showHistoryScreen) {
                HistoryScreen(
                    historyRecords = historyRecords,
                    onBackToCalendar = { showHistoryScreen = false }
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Gray)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.bg),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Transparent),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (!showThankYouPage) {
                            MonthSelector(
                                months = months,
                                currentMonthIndex = currentMonthIndex,
                                onMonthSelected = { selectedMonthIndex ->
                                    currentMonthIndex = selectedMonthIndex
                                }
                            )

                            Calendar(
                                calendarInput = createCalendarList(),
                                onDayClick = { day ->
                                    clickedCalendarElem = CalendarInput(day)
                                    showQuestionnaire = true
                                },
                                month = months[currentMonthIndex],
                                modifier = Modifier
                                    .padding(10.dp)
                                    .fillMaxWidth()
                                    .aspectRatio(1.3f)
                            )

                            clickedCalendarElem?.let {
                                if (showQuestionnaire) {
                                    AlertDialog(
                                        onDismissRequest = { showQuestionnaire = false },
                                        confirmButton = {
                                            Button(onClick = { showQuestionnaire = false }) {
                                                Text("OK")
                                            }
                                        },
                                        text = { Text("You selected day: ${it.day}") }

                                    )
                                } else {
                                    Questionnaire(
                                        selectedDate = clickedCalendarElem?.day,
                                        months = months,
                                        currentMonthIndex = currentMonthIndex,
                                        questions = listOf(
                                            "a. How is the pain?",
                                            "b. How is the flow?",
                                            "c. How are you feeling today?",
                                            "d. Are you taking any medication?"
                                        ),
                                        responses = responses,
                                        onResponseChange = { question, response ->
                                            responses = responses + (question to response)
                                        },
                                        onSubmit = {
                                            calculatedDate = calculateNewDate(
                                                clickedCalendarElem!!.day,
                                                currentMonthIndex,
                                                months
                                            )
                                            // Add to history records
                                            addToHistory(
                                                clickedCalendarElem!!.day,
                                                months[currentMonthIndex],
                                                java.util.Calendar.getInstance().get(java.util.Calendar.YEAR),
                                                responses
                                            )
                                            showThankYouPage = true
                                        }
                                    )
                                }
                            }
                        } else {
                            ThankYouPage(
                                calculatedDate = calculatedDate,
                                onBackToCalendar = {
                                    showThankYouPage = false
                                },
                                onLogDate = {
                                    showHistoryScreen = true
                                }
                            )
                        }
                    }
                }
            }
        }
    }
    private fun createCalendarList(): List<CalendarInput> {
        val calendarInputs = mutableListOf<CalendarInput>()
        for (i in 1..31) {
            calendarInputs.add(CalendarInput(i))
        }
        return calendarInputs
    }

    private fun calculateNewDate(day: Int, monthIndex: Int, months: List<String>): String {
        val calendar = java.util.Calendar.getInstance()
        calendar.set(java.util.Calendar.MONTH, monthIndex)
        calendar.set(java.util.Calendar.DAY_OF_MONTH, day)
        calendar.add(java.util.Calendar.DAY_OF_MONTH, 28)
        val newMonth = calendar.get(java.util.Calendar.MONTH)
        val newDay = calendar.get(java.util.Calendar.DAY_OF_MONTH)
        return "$newDay ${months[newMonth]}"
    }
}

@Composable
fun ThankYouPage(
    calculatedDate: String,
    onBackToCalendar: () -> Unit,
    onLogDate: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Your next period date:",
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp
        )
        Text(
            text = calculatedDate,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp
        )
        Row(
            modifier = Modifier.padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(onClick = onBackToCalendar) {
                Text(text = "Back to Calendar")
            }
            Button(onClick = onLogDate) {
                Text(text = "Log history")
            }
        }
    }
}


@Composable
fun MonthSelector(months: List<String>, currentMonthIndex: Int, onMonthSelected: (Int) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { onMonthSelected((currentMonthIndex - 1 + 12) % 12) }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Previous Month")
            }
            Text(
                text = months[currentMonthIndex],
                fontWeight = FontWeight.SemiBold,
                color = black,
                fontSize = 20.sp
            )
            IconButton(onClick = { onMonthSelected((currentMonthIndex + 1) % 12) }) {
                Icon(Icons.Default.ArrowForward, contentDescription = "Next Month")
            }
        }
    }
}

@Composable
fun Calendar(
    modifier: Modifier = Modifier,
    calendarInput: List<CalendarInput>,
    onDayClick: (Int) -> Unit,
    strokeWidth: Float = 15f,
    month: String
) {
    var canvasSize by remember { mutableStateOf(Size.Zero) }
    var clickAnimationOffset by remember { mutableStateOf(Offset.Zero) }
    var animationRadius by remember { mutableStateOf(0f) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = month,
            fontWeight = FontWeight.SemiBold,
            color = black,
            fontSize = 40.sp
        )
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(true) {
                    detectTapGestures(
                        onTap = { offset ->
                            val column = (offset.x / canvasSize.width * CALENDAR_COLUMNS).toInt() + 1
                            val row = (offset.y / canvasSize.height * CALENDAR_ROWS).toInt() + 1
                            val day = column + (row - 1) * CALENDAR_COLUMNS
                            if (day <= calendarInput.size) {
                                onDayClick(day)
                                clickAnimationOffset = offset
                                scope.launch {
                                    animate(0f, 225f, animationSpec = tween(300)) { value, _ ->
                                        animationRadius = value
                                    }
                                }
                            }
                        }
                    )
                }
        ) {
            val canvasHeight = size.height
            val canvasWidth = size.width
            canvasSize = Size(canvasWidth, canvasHeight)
            val ySteps = canvasHeight / CALENDAR_ROWS
            val xSteps = canvasWidth / CALENDAR_COLUMNS

            val textHeight = 17.dp.toPx()
            for (i in calendarInput.indices) {
                val column = i % CALENDAR_COLUMNS
                val row = i / CALENDAR_COLUMNS
                val textPositionX = xSteps * column + strokeWidth
                val textPositionY = row * ySteps + textHeight + strokeWidth / 2
                drawContext.canvas.nativeCanvas.apply {
                    drawText(
                        "${calendarInput[i].day}",
                        textPositionX,
                        textPositionY,
                        android.graphics.Paint().apply {
                            textSize = textHeight
                            color = black.toArgb()
                            isFakeBoldText = true
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun QuestionSelectableOptions(
    question: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    emojis: Map<String, String>
) {
    Column {
        Text(
            text = question,
            color = Color.Black,
            fontSize = 18.sp
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            options.forEach { option ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable { onOptionSelected(option) }
                        .background(if (selectedOption == option) Color(0xFFFF4081) else Color.Transparent)
                        .padding(8.dp)
                ) {
                    Text(
                        text = emojis[option] ?: option,
                        fontSize = 24.sp
                    )
                    Text(text = option)
                }
            }
        }
    }
}

@Composable
fun Questionnaire(
    selectedDate: Int?,
    months: List<String>,
    currentMonthIndex: Int,
    questions: List<String>,
    responses: Map<String, String>,
    onResponseChange: (String, String) -> Unit,
    onSubmit: () -> Unit
) {
    val painOptions = listOf("Heavy", "Normal", "Light")
    val flowOptions = listOf("Light", "Normal","Heavy")
    val feelingOptions = listOf("Moody", "Light", "Irritate")
    val medicationOptions = listOf("Yes", "No")

    val feelingEmojis = mapOf(
        "Moody" to "\uD83D\uDE1E", // 😞
        "Light" to "\uD83D\uDE42", // 🙂
        "Irritate" to "\uD83D\uDE21"  // 😡
    )

    val painEmojis = mapOf(
        "Heavy" to "\uD83D\uDE29", // 💩
        "Normal" to "\uD83D\uDCA7", // 💧
        "Light" to "\uD83D\uDCA8"  // 💨
    )

    val flowEmojis = mapOf(
        "Light" to "\uD83D\uDC4D", // 👍
        "Normal" to "\uD83D\uDC4C", // 👌
        "Heavy" to "\uD83D\uDC4E"   // 👎
    )

    val medicationEmojis = mapOf(
        "Yes" to "\uD83D\uDC47", // 👇
        "No" to "\uD83D\uDC46"   // 👆
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        selectedDate?.let { date ->
            Text(
                text = "You selected date: $date ${months[currentMonthIndex]}",
                color = Color.Black,
                fontSize = 18.sp
            )
        }

        // Pain question
        QuestionSelectableOptions(
            question = questions[0],
            options = painOptions,
            selectedOption = responses[questions[0]] ?: "",
            onOptionSelected = { onResponseChange(questions[0], it) },
            emojis = painEmojis
        )

        // Flow question
        QuestionSelectableOptions(
            question = questions[1],
            options = flowOptions,
            selectedOption = responses[questions[1]] ?: "",
            onOptionSelected = { onResponseChange(questions[1], it) },
            emojis = flowEmojis
        )

        // Feeling question with emojis
        QuestionEmojiSelector(
            question = questions[2],
            options = feelingOptions,
            emojis = feelingEmojis,
            selectedOption = responses[questions[2]] ?: "",
            onOptionSelected = { onResponseChange(questions[2], it) }
        )

        // Medication question
        QuestionSelectableOptions(
            question = questions[3],
            options = medicationOptions,
            selectedOption = responses[questions[3]] ?: "",
            onOptionSelected = { onResponseChange(questions[3], it) },
            emojis = medicationEmojis
        )

        // Show the "Submit" button only if all questions are answered
        if (responses.size == questions.size) {
            Button(
                onClick = onSubmit,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Text(text = "Submit")
            }
        }
    }
}

@Composable
fun QuestionEmojiSelector(
    question: String,
    options: List<String>,
    emojis: Map<String, String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    Column {
        Text(
            text = question,
            color = Color.Black,
            fontSize = 18.sp
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            options.forEach { option ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable { onOptionSelected(option) }
                        .background(if (selectedOption == option) Color(0xFFFF4081) else Color.Transparent)
                        .padding(8.dp)
                ) {
                    Text(
                        text = emojis[option] ?: option,
                        fontSize = 24.sp
                    )
                    Text(text = option)
                }
            }
        }
    }
}

data class CalendarInput(
    val day: Int,
    val toDos: List<String> = emptyList()
)

@Composable
fun HistoryScreen(
    historyRecords: List<HistoryRecord>,
    onBackToCalendar: () -> Unit,
) {
    val context = LocalContext.current

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Background image
        Image(
            painter = painterResource(id = R.drawable.history), // Replace with your background image resource
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds // Adjust content scale as needed
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "History Screen",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = Color.Black
                // Adjust text color to be visible on the background
            )

            // Display each history record
            historyRecords.forEach { record ->
                Column(
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Date: ${record.day} ${record.month} ${record.year}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.Black // Adjust text color to be visible on the background
                    )

                    // Display responses for each question
                    record.responses.forEach { (question, response) ->
                        Text(
                            text = "$question: $response",
                            fontSize = 16.sp,
                            modifier = Modifier.padding(top = 4.dp),
                            color = Color.Black // Adjust text color to be visible on the background
                        )
                    }

                    // Button to view detailed results or insights
                    Button(
                        onClick = {
                            val intent = Intent(context, view::class.java)
                            // Optionally, you can pass data via Intent extras if needed
                            // intent.putExtra("record_id", record.id)
                            context.startActivity(intent)
                        },
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        Text(text = "View Results")
                    }
                }
            }

            // Button to navigate back to the calendar
            Button(onClick = onBackToCalendar, modifier = Modifier.padding(top = 16.dp)) {
                Text(text = "Back to Calendar")
            }
        }
    }
}



data class HistoryRecord(
    val day: Int,
    val month: String,
    val year: Int,
    val responses: Map<String, String>
)