package com.example.periodtracker

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.periodtracker.ui.theme.PeriodtrackerTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppContent(auth)
        }
    }
}

@Composable
fun AppContent(auth: FirebaseAuth) {
    var showSplashScreen by remember { mutableStateOf(true) }

    LaunchedEffect(showSplashScreen) {
        delay(2000)
        showSplashScreen = false
    }

    Crossfade(targetState = showSplashScreen, label = "") { isSplashScreenVisible ->
        if (isSplashScreenVisible) {
            SplashScreen {
                showSplashScreen = false
            }
        } else {
            AuthOrMainScreen(auth)
        }
    }
}

@Composable
fun SplashScreen(navigateToAuthOrMainScreen: () -> Unit) {
    var rotationState by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(true) {
        delay(2000)
        navigateToAuthOrMainScreen()
    }
    LaunchedEffect(rotationState) {
        while (true) {
            delay(16)
            rotationState += 1f
        }
    }

    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = TweenSpec(durationMillis = 500),
        label = ""
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.jani),
            contentDescription = "",
            modifier = Modifier
                .size(150.dp)
                .clip(CircleShape)
                .scale(scale)
                .rotate(rotationState)
        )
    }
}



@Composable
fun AuthOrMainScreen(auth: FirebaseAuth) {
    var user by remember { mutableStateOf(auth.currentUser) }
    var showHelloScreen by remember { mutableStateOf(false) }

    if (user == null) {
        AuthScreen(auth = auth, onSignedIn = { signedInUser ->
            user = signedInUser
            showHelloScreen = true
        })
    } else {
        MainScreen(user = user!!, onSignOut = {
            auth.signOut()
            user = null
        })
    }
}


@Composable
fun AuthScreen(auth: FirebaseAuth, onSignedIn: (FirebaseUser) -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var isSignIn by remember { mutableStateOf(true) }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var myErrorMessage by remember { mutableStateOf<String?>(null) }

    val imagePainter: Painter = painterResource(id = R.drawable.febu)

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = imagePainter, contentDescription = "",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )
        Card(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.25f))
                .padding(25.dp)
                .clip(RoundedCornerShape(16.dp)),
            elevation = CardDefaults.cardElevation()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (!isSignIn) {
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = firstName, onValueChange = { firstName = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        label = { Text("First Name") },
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = lastName, onValueChange = { lastName = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        label = { Text("Last Name") },
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = email, onValueChange = { email = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    label = { Text("Email") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Email
                    ),
                    visualTransformation = VisualTransformation.None
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = password, onValueChange = { password = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    label = { Text("Password") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Password
                    ),
                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                            val icon = if (isPasswordVisible) Icons.Default.Lock else Icons.Default.Lock
                            Icon(imageVector = icon, contentDescription = "Toggle password visibility")
                        }
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
                if (myErrorMessage != null) {
                    Text(
                        text = myErrorMessage!!,
                        color = Color.Red,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        if (isSignIn) {
                            signIn(auth, email, password, onSignedIn = { signedInUser ->
                                onSignedIn(signedInUser)
                            }, onSignInError = { errorMessage ->
                                myErrorMessage = errorMessage
                            })
                        } else {
                            signUp(auth, email, password, firstName, lastName, onSignedIn = { signedInUser ->
                                onSignedIn(signedInUser)
                            }, onSignUpError = { errorMessage ->
                                myErrorMessage = errorMessage
                            })
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .padding(8.dp)
                ) {
                    Text(text = if (isSignIn) "Sign In" else "Sign Up", fontSize = 18.sp)
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    ClickableText(
                        text = buildAnnotatedString {
                            withStyle(style = SpanStyle(color = Color.Red)) {
                                append(if (isSignIn) "Don't have an account? Sign Up" else "Already have an account? Sign In")
                            }
                        },
                        onClick = {
                            myErrorMessage = null
                            email = ""
                            password = ""
                            isSignIn = !isSignIn
                        },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}
@Composable
fun MainScreen(user: FirebaseUser, onSignOut: () -> Unit) {
    val context = LocalContext.current // Retrieve the context

    val userProfile = remember { mutableStateOf<User?>(null) }

    LaunchedEffect(user.uid) {
        val firestore = FirebaseFirestore.getInstance()
        val userDocRef = firestore.collection("users").document(user.uid)
        userDocRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val firstName = document.getString("firstName")
                    val lastName = document.getString("lastName")
                    userProfile.value = User(firstName, lastName, user.email ?: "")
                }
            }.addOnFailureListener{e->}
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Background image
        val backgroundImage: Painter = painterResource(id = R.drawable.pi)
        Image(
            painter = backgroundImage,
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds // Fill the bounds of the screen
        )

        // Text and buttons
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            userProfile.value?.let { user ->
                Text(
                    text = "Welcome, ${user.firstName} ${user.lastName}!",
                    fontSize = 28.sp,
                    color = Color.Black, // Text color
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            Text(
                text = "Welcome to Period App",
                fontSize = 20.sp,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Explore and manage your period cycle with ease.",
                fontSize = 14.sp,
                color = Color.Black, // Text color
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        // Navigate to Form activity
                        val intent = Intent(context, Form::class.java)
                        context.startActivity(intent)
                    },
                    modifier = Modifier.weight(1f)
                        .height(60.dp)
                        .padding(end = 8.dp)
                ) {
                    Text("Go to Form")
                }
                Button(
                    onClick = { onSignOut() },
                    modifier = Modifier.weight(1f)
                        .height(60.dp)
                        .padding(start = 8.dp)
                ) {
                    Text("Sign Out")
                }
            }

            // Calendar Button
            CalendarButton(
                onClick = {
                    val intent = Intent(context, prediction::class.java)
                    context.startActivity(intent)
                    // Handle calendar button click
                    // For example, navigate to the calendar screen
                },
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
    }
}




private fun signIn(
    auth: FirebaseAuth,
    email: String,
    password: String,
    onSignedIn: (FirebaseUser) -> Unit,
    onSignInError: (String) -> Unit
) {
    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                onSignedIn(user!!)
            } else {
                val errorMessage = task.exception?.message ?: "Invalid email or password"
                Log.e("AuthError", errorMessage)
                onSignInError(errorMessage)
            }
        }
}

private fun signUp(
    auth: FirebaseAuth,
    email: String,
    password: String,
    firstName: String,
    lastName: String,
    onSignedIn: (FirebaseUser) -> Unit,
    onSignUpError: (String) -> Unit
) {
    auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                val userProfile = hashMapOf(
                    "firstName" to firstName,
                    "lastName" to lastName,
                    "email" to email
                )
                val firestore = FirebaseFirestore.getInstance()
                firestore.collection("users")
                    .document(user!!.uid)
                    .set(userProfile)
                    .addOnSuccessListener {
                        onSignedIn(user)
                    }
                    .addOnFailureListener { e ->
                        val errorMessage = e.message ?: "Sign up failed"
                        Log.e("FirestoreError", errorMessage)
                        onSignUpError(errorMessage)
                    }
            } else {
                val errorMessage = task.exception?.message ?: "Sign up failed"
                Log.e("AuthError", errorMessage)
                onSignUpError(errorMessage)
            }
        }
}

@Composable
fun CalendarButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
            .padding(8.dp),
        enabled = enabled
    ) {
        Text(text = "Calendar", fontSize = 18.sp)
    }
}
@Preview(showBackground = true)
@Composable
fun PreviewAuthOrMainScreen() {
    AuthOrMainScreen(FirebaseAuth.getInstance())
}

data class User(val firstName: String?, val lastName: String?, val email: String)
