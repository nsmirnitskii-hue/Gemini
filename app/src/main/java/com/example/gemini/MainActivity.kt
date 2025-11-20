package com.example.gemini

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gemini.ui.theme.GeminiTheme
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GeminiTheme {
                ventanaIA(Modifier)
            }
        }
    }
}
@Composable
fun ventanaIA(modifier:Modifier) {
    val scope = CoroutineScope(Dispatchers.Default)
    var textoIA by remember { mutableStateOf("") }
    var cantidadLanzamientos by remember { mutableIntStateOf(0) }
    var pregunta by remember { mutableStateOf("") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally, // centra horizontalmente
        verticalArrangement = Arrangement.Top // empieza desde arriba
    ) {
        item {
            Text("Aquí esta IA")
            Text(cantidadLanzamientos.toString())
            Text(textoIA)
            OutlinedTextField(
                value = pregunta,
                onValueChange = { pregunta = it },
                label = { Text("Pregunta") }
            )
            Button({
                scope.launch {
                    // Antes del if de cantidad lanzamientos se ejecutaba en bucle un montón de solicitudes
                    // lo limpio es controlar que no se lanzen multiples solicitudes con un booleano
                    // pero con el numero podemos manipularlo para ver mas respues seguidas
                    cantidadLanzamientos += 1
                    val model = Firebase.ai(backend = GenerativeBackend.googleAI())
                        .generativeModel("gemini-2.5-flash")
                    // Provide a prompt that contains text
                    val prompt = pregunta
                    // model.generateContent tiene un delay, el de la IA mientras calcula la respuesta que nos va a dar.
                    try {
                        textoIA = "Se ha realizado la consulta al modelo"
                        val response = model.generateContent(prompt)
                        textoIA = response.text.toString()
                    } catch (e: Exception) {
                        textoIA = "Error al generar contenido: ${e.localizedMessage}"
                    }
                }
            }) { Text("Usar IA") }
        }
    }
}


