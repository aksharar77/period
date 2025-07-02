package com.example.periodtracker

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalUriHandler
//import kotlin.coroutines.jvm.internal.CompletedContinuation.context

data class QuestionAnswer(val question: String, val description: String, val link: String)

@Composable
fun ViewResultsPage() {
    val context = LocalContext.current // Access context here
    val questionAnswers = listOf(
        QuestionAnswer(
            question = "Heavy flow 💧",
            description = "If you are experiencing heavy flow, it is important to monitor the situation closely. Heavy menstrual bleeding, also known as menorrhagia, can be a sign of an underlying health issue.",
            link = "https://youtu.be/hcA5L8T-aZQ?feature=shared"
        ),
        QuestionAnswer(
            question = "Light flow 🌸",
            description = "If you are experiencing light flow, it can be normal, but it can also be a sign of hormonal imbalances, stress, or other health issues.",
            link = "https://youtu.be/I1byCFDqHRA?feature=shared"
        ),
        QuestionAnswer(
            question = "Pain 😣",
            description = "If you are experiencing pain, especially severe menstrual cramps, it could be a sign of dysmenorrhea or other conditions such as endometriosis or fibroids.",
            link = "https://youtu.be/vU3LmEc-hCI?feature=shared"
        ),
        QuestionAnswer(
            question = "Mood swings 😞",
            description = "If you are feeling moody, it might be due to hormonal changes that occur during the menstrual cycle. Mood swings, irritability, and emotional sensitivity are common symptoms of premenstrual syndrome (PMS).",
            link = "https://youtu.be/tww5gXrgk50?feature=shared"
        ),
        QuestionAnswer(
            question = "Irritability 😠",
            description = "If you are feeling irritated, it could be related to hormonal fluctuations associated with your menstrual cycle. Irritability is a common symptom of PMS and can be exacerbated by stress, lack of sleep, and other factors.",
            link = "https://youtu.be/iclPN4bj3WQ?feature=shared"
        ),
        QuestionAnswer(
            question = "Medications 💊",
            description = "If you are taking medications, it is important to know what kind of medications you are using and their potential effects on your menstrual cycle. Common medications that can influence your period include hormonal contraceptives, pain relievers, and certain psychiatric medications.",
            link = "https://youtu.be/FqHi327CrgI?feature=shared"
        )
    )

    val uriHandler = LocalUriHandler.current


    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Background image
        Image(
            painter = painterResource(id = R.drawable.view), // Replace with your background image resource
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        // Content
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            questionAnswers.forEach { qa ->
                Column(
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Text(
                        text = qa.question,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = qa.description,
                        fontSize = 16.sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Watch related video",
                        color = Color.Blue,
                        modifier = Modifier
                            .clickable { uriHandler.openUri(qa.link) }
                            .padding(8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val intent = Intent(context, FlowActivity::class.java)
                    // Optionally pass data via Intent extras
                    // intent.putExtra("key", "value")
                    context.startActivity(intent)
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(text = "exercise")
            }
        }

        }
    }
//}

class view : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ViewResultsPage()
        }
    }
}

