package com.example.testapp

import android.os.Bundle
import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.bumptech.glide.Glide
import com.example.testapp.ui.theme.TestAppTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TestAppTheme {
                TextToGifApp()
            }
        }
    }
}

@Composable
fun TextToGifApp() {
    var text by remember { mutableStateOf("") }
    var letters by remember { mutableStateOf(listOf<String>()) }
    var showTitle by remember { mutableStateOf(false) }
    val isDarkTheme = isSystemInDarkTheme()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(if (isDarkTheme) Color.Black else Color.White)
            .padding(16.dp),
    ) {
        CustomTextField(text, onTextChange = { newText ->
            text = newText.uppercase()
        }, isDarkTheme)

        Spacer(modifier = Modifier.height(20.dp))

        CustomButton(onClick = {
            letters = splitTextToLetters(text)
            showTitle = true
        })

        Spacer(modifier = Modifier.height(20.dp))

        if (showTitle) {
            Text(
                text = "Traducción a LSM",
                style = TextStyle(fontSize = 24.sp, color = if (isDarkTheme) Color.White else Color.Black)
            )
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 10.dp),
                thickness = 2.dp,
                color = if (isDarkTheme) Color.Gray else Color.LightGray
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        SequentialLetterGifs(letters)
    }
}

fun splitTextToLetters(text: String): List<String> {
    val words = listOf(
        "HOLA", "ADIOS", "BUENAS NOCHES", "BUENAS TARDES", "BUENOS DIAS",
        "COMO ESTAS", "NOS VEMOS A LA PROXIMA", "NOS VEMOS MAÑANA", "NOS VEMOS PRONTO",
        "DIA", "MES", "AÑO", "LUNES", "MARTES", "MIERCOLES", "JUEVES", "VIERNES", "SABADO", "DOMINGO", "LL",
        "ENERO", "FEBRERO", "MARZO", "ABRIL", "MAYO", "JUNIO", "JULIO", "AGOSTO", "SEPTIEMBRE", "OCTUBRE", "NOVIEMBRE", "DICIEMBRE",
        "ABUELO", "ABUELA", "MAMÁ", "PAPÁ", "HERMANO", "HERMANA", "TÍO", "TÍA", "PRIMO", "PRIMA", "HIJA", "HIJO", "SOBRINA", "SOBRINO",
        "ESPOSA", "ESPOSO", "NOVIA", "NOVIO", "NUERA", "YERNO", "CUÑADA", "CUÑADO", "PINTAR", "NADAR", "CORRER",
        "ESTUDIAR", "VER VIDEOS", "VER VIDEOS EN TIKTOK", "VER VIDEOS EN INSTAGRAM", "VER VIDEOS EN YOUTUBE",
        "VER VIDEOS EN FACEBOOK", "JUGAR", "JUGAR VIDEOJUEGOS", "FUTBOL", "FUTBOL AMERICANO", "BÁSQUETBOL", "BORRADOR", "CARGADOR",
        "CELULAR", "CLASE DE ARTE", "CLASE DE COMPUTACIÓN", "CLASE DE DEPORTES", "CLASE DE ESPAÑOL", "CLASE DE INGLES", "CLASE DE LSM",
        "CLASE", "CLASE DE NATACIÓN", "COMPUTADORA", "ESCRITORIO", "GOMA", "INTERNET", "LAPIZ", "MESA",
        "MOCHILA", "PEGAMENTO", "PIZARRON", "PLUMA", "PUERTA", "SILLA", "TABLET", "TIJERAS",
    )

    val numbers = (0..20).map { it.toString() }
    val letters = mutableListOf<String>()
    var remainingText = text.uppercase().trim()

    while (remainingText.isNotEmpty()) {
        var foundWord = false

        for (word in words.sortedByDescending { it.length }) {
            if (remainingText.startsWith(word)) {
                letters.add(word)
                remainingText = remainingText.removePrefix(word).trim()
                foundWord = true
                break
            }
        }

        if (!foundWord) {
            for (number in numbers.sortedByDescending { it.length }) {
                if (remainingText.startsWith(number)) {
                    letters.add(number)
                    remainingText = remainingText.removePrefix(number).trim()
                    foundWord = true
                    break
                }
            }
        }

        if (!foundWord && remainingText.isNotEmpty()) {
            letters.add(remainingText[0].toString())
            remainingText = remainingText.drop(1).trim()
        }
    }

    return letters
}

@Composable
fun CustomTextField(value: String, onTextChange: (String) -> Unit, isDarkTheme: Boolean) {
    var textState by remember { mutableStateOf(TextFieldValue(value)) }

    Box(modifier = Modifier.fillMaxWidth()) {
        if (textState.text.isEmpty()) {
            Text(
                text = "Escribe algo...",
                style = TextStyle(color = Color.Gray, fontSize = 24.sp),
                modifier = Modifier.padding(16.dp)
            )
        }

        BasicTextField(
            value = textState,
            onValueChange = { newText ->
                textState = newText
                onTextChange(newText.text)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(16.dp),
            singleLine = true,
            textStyle = TextStyle(
                color = if (isDarkTheme) Color.White else Color.Black,
                fontSize = 24.sp
            )
        )
    }
}

@Composable
fun CustomButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Blue,
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(30.dp),
        modifier = Modifier
            .padding(30.dp)
            .size(200.dp, 60.dp)
    ) {
        Text(text = "Traducir", fontSize = 22.sp)
    }
}

@Composable
fun SequentialLetterGifs(letters: List<String>) {
    var currentLetterIndex by remember { mutableIntStateOf(0) }
    var showNextLetter by remember { mutableStateOf(true) }
    var finishedSequence by remember { mutableStateOf(false) }

    LaunchedEffect(letters) {
        if (letters.isNotEmpty()) {
            finishedSequence = false
            for (i in letters.indices) {
                currentLetterIndex = i
                showNextLetter = false
                delay(100L)
                showNextLetter = true
                delay(2000L)
            }
            finishedSequence = true
        }
    }

    if (letters.isNotEmpty()) {
        if (showNextLetter && !finishedSequence) {
            LetterGif(letter = letters[currentLetterIndex])
        } else if (finishedSequence) {
            ShowAllGifs(letters)
        }
    }
}

@Composable
fun LetterGif(letter: String) {
    val gifRes = getGifResource(letter)

    if (gifRes != null) {
        AndroidView(factory = { context ->
            val imageView = ImageView(context)
            Glide.with(context)
                .asGif()
                .load(gifRes)
                .into(imageView)
            imageView
        }, modifier = Modifier
            .size(500.dp)
            .padding(5.dp))
    }
}

fun getGifResource(letter: String): Int? {
    return when (letter) {
        "BUENOS DIAS" -> R.drawable.buenos_dias
        "HOLA" -> R.drawable.hola
        "ADIOS" -> R.drawable.adios
        "BUENAS NOCHES" -> R.drawable.buenas_noches
        "BUENAS TARDES" -> R.drawable.buenas_tardes
        "COMO ESTAS" -> R.drawable.como_estas
        "NOS VEMOS A LA PROXIMA" -> R.drawable.nos_vemos_a_la_proxima
        "NOS VEMOS MAÑANA" -> R.drawable.nos_vemos_manana
        "NOS VEMOS PRONTO" -> R.drawable.nos_vemos_pronto

        "DIA" -> R.drawable.dia
        "MES" -> R.drawable.mes
        "AÑO" -> R.drawable.anio
        "LUNES" -> R.drawable.lunes
        "MARTES" -> R.drawable.martes
        "MIERCOLES" -> R.drawable.miercoles
        "JUEVES" -> R.drawable.jueves
        "VIERNES" -> R.drawable.viernes
        "SABADO" -> R.drawable.sabado
        "DOMINGO" -> R.drawable.domingo
        "LL" -> R.drawable.ll
        "ENERO" -> R.drawable.enero
        "FEBRERO" -> R.drawable.febrero
        "MARZO" -> R.drawable.marzo
        "ABRIL" -> R.drawable.abril
        "MAYO" -> R.drawable.mayo
        "JUNIO" -> R.drawable.junio
        "JULIO" -> R.drawable.julio
        "AGOSTO" -> R.drawable.agosto
        "SEPTIEMBRE" -> R.drawable.septiembre
        "OCTUBRE" -> R.drawable.octubre
        "NOVIEMBRE" -> R.drawable.noviembre
        "DICIEMBRE" -> R.drawable.diciembre

        "ABUELO" -> R.drawable.abuelo
        "ABUELA" -> R.drawable.abuela
        "TÍO" -> R.drawable.tio
        "TÍA" -> R.drawable.tia
        "MAMÁ" -> R.drawable.mama
        "PAPÁ" -> R.drawable.papa
        "HERMANO" -> R.drawable.hermano
        "HERMANA" -> R.drawable.hermana
        "PRIMO" -> R.drawable.primo
        "PRIMA" -> R.drawable.prima
        "HIJA" -> R.drawable.hija
        "HIJO" -> R.drawable.hijo
        "SOBRINO" -> R.drawable.sobrino
        "SOBRINA" -> R.drawable.sobrina
        "ESPOSO" -> R.drawable.esposo
        "ESPOSA" -> R.drawable.esposa
        "NOVIO" -> R.drawable.novio
        "NOVIA" -> R.drawable.novia
        "NUERA" -> R.drawable.nuera
        "YERNO" -> R.drawable.yerno
        "CUÑADA" -> R.drawable.cunada
        "CUÑADO" -> R.drawable.cunado

        "PINTAR" -> R.drawable.pintar
        "NADAR" -> R.drawable.nadar
        "CORRER" -> R.drawable.correr
        "ESTUDIAR" -> R.drawable.estudiar
        "VER VIDEOS" -> R.drawable.ver_videos
        "VER VIDEOS EN TIKTOK" -> R.drawable.ver_videos_en_tiktok
        "VER VIDEOS EN INSTAGRAM" -> R.drawable.ver_videos_en_instagram
        "VER VIDEOS EN YOUTUBE" -> R.drawable.ver_videos_en_youtube
        "VER VIDEOS EN FACEBOOK" -> R.drawable.ver_videos_en_facebook
        "JUGAR" -> R.drawable.jugar
        "JUGAR VIDEOJUEGOS" -> R.drawable.jugar_videojuegos
        "FUTBOL" -> R.drawable.futbol
        "FUTBOL AMERICANO" -> R.drawable.futbol_americano
        "BÁSQUETBOL" -> R.drawable.basquetbol

        "BORRADOR" -> R.drawable.borrador
        "CARGADOR" -> R.drawable.cargador
        "CELULAR" -> R.drawable.celular
        "CLASE DE ARTE" -> R.drawable.clase_de_arte
        "CLASE DE COMPUTACIÓN" -> R.drawable.clase_de_computacion
        "CLASE DE DEPORTES" -> R.drawable.clase_de_deportes
        "CLASE DE ESPAÑOL" -> R.drawable.clase_de_espanol
        "CLASE DE INGLES" -> R.drawable.clase_de_ingles
        "CLASE DE LSM" -> R.drawable.clase_de_lsm
        "CLASE DE NATACIÓN" -> R.drawable.clases_de_natacion
        "CLASE" -> R.drawable.clase
        "COMPUTADORA" -> R.drawable.computadora
        "ESCRITORIO" -> R.drawable.escritorio
        "GOMA" -> R.drawable.goma
        "INTERNET" -> R.drawable.internet
        "LAPIZ" -> R.drawable.lapiz
        "MESA" -> R.drawable.mesa
        "MOCHILA" -> R.drawable.mochila
        "PEGAMENTO" -> R.drawable.pegamento
        "PIZARRÓN" -> R.drawable.pizarron
        "PLUMA" -> R.drawable.pluma
        "PUERTA" -> R.drawable.puerta
        "SILLA" -> R.drawable.silla
        "TABLET" -> R.drawable.tablet
        "TIJERAS" -> R.drawable.tijeras

        "0" -> R.drawable.cero
        "1" -> R.drawable.uno
        "2" -> R.drawable.dos
        "3" -> R.drawable.tres
        "4" -> R.drawable.cuatro
        "5" -> R.drawable.cinco
        "6" -> R.drawable.seis
        "7" -> R.drawable.siete
        "8" -> R.drawable.ocho
        "9" -> R.drawable.nueve
        "10" -> R.drawable.diez
        "11" -> R.drawable.once
        "12" -> R.drawable.doce
        "13" -> R.drawable.trece
        "14" -> R.drawable.catore
        "15" -> R.drawable.quince
        "16" -> R.drawable.dieeis
        "17" -> R.drawable.diecete
        "18" -> R.drawable.dieocho
        "19" -> R.drawable.dieeve
        "20" -> R.drawable.veinte




        else -> {
            when (letter) {
                "A" -> R.drawable.a
                "B" -> R.drawable.b
                "C" -> R.drawable.c
                "D" -> R.drawable.d
                "E" -> R.drawable.e
                "F" -> R.drawable.f
                "G" -> R.drawable.g
                "H" -> R.drawable.h
                "I" -> R.drawable.i
                "J" -> R.drawable.j
                "K" -> R.drawable.k
                "L" -> R.drawable.l
                "M" -> R.drawable.m
                "N" -> R.drawable.n
                "O" -> R.drawable.o
                "P" -> R.drawable.p
                "Q" -> R.drawable.q
                "R" -> R.drawable.r
                "S" -> R.drawable.s
                "T" -> R.drawable.t
                "U" -> R.drawable.u
                "V" -> R.drawable.v
                "W" -> R.drawable.w
                "X" -> R.drawable.x
                "Y" -> R.drawable.y
                "Z" -> R.drawable.z
                else -> null
            }
        }
    }
}

@Composable
fun ShowAllGifs(letters: List<String>) {
    LazyRow(modifier = Modifier.fillMaxWidth()) {
        items(letters.size) { index ->
            LetterGif(letter = letters[index])
        }
    }
}
