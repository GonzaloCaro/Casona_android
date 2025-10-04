package com.example.casonaapp.utils

import android.content.Context
import android.content.Intent
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.Locale

class SpeechRecognitionHelper(private val context: Context) {

    companion object {
        private const val TAG = "SpeechRecognitionHelper"
    }

    private var speechRecognizer: SpeechRecognizer? = null
    private var isListening = false

    fun startListening(): Flow<SpeechRecognitionResult> = callbackFlow {
        Log.d(TAG, "Iniciando reconocimiento de voz...")

        // Verificar que SpeechRecognizer esté disponible
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            Log.e(TAG, "SpeechRecognizer no disponible en este dispositivo")
            trySend(SpeechRecognitionResult.Error("El reconocimiento de voz no está disponible en este dispositivo"))
            close()
            return@callbackFlow
        }

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
            setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: android.os.Bundle?) {
                    Log.d(TAG, "Listo para escuchar")
                    trySend(SpeechRecognitionResult.Listening)
                }

                override fun onBeginningOfSpeech() {
                    Log.d(TAG, "Inicio de habla detectado")
                    trySend(SpeechRecognitionResult.Speaking)
                }

                override fun onRmsChanged(rmsdB: Float) {
                    // Puedes usar esto para una visualización de volumen
                    trySend(SpeechRecognitionResult.RmsChanged(rmsdB))
                }

                override fun onBufferReceived(buffer: ByteArray?) {
                    // No necesario para la mayoría de los casos
                }

                override fun onEndOfSpeech() {
                    Log.d(TAG, "Fin de habla detectado")
                    trySend(SpeechRecognitionResult.FinishedSpeaking)
                }

                override fun onError(error: Int) {
                    val errorMessage = when (error) {
                        SpeechRecognizer.ERROR_AUDIO -> "Error de audio"
                        SpeechRecognizer.ERROR_CLIENT -> "Error del cliente"
                        SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Permisos insuficientes"
                        SpeechRecognizer.ERROR_NETWORK -> "Error de red"
                        SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Timeout de red"
                        SpeechRecognizer.ERROR_NO_MATCH -> "No se reconoció el habla"
                        SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Reconocedor ocupado"
                        SpeechRecognizer.ERROR_SERVER -> "Error del servidor"
                        SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Timeout de habla"
                        else -> "Error desconocido: $error"
                    }
                    Log.e(TAG, "Error en reconocimiento de voz: $errorMessage")
                    trySend(SpeechRecognitionResult.Error(errorMessage))
                    close()
                }

                override fun onResults(results: android.os.Bundle?) {
                    val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    val confidence = results?.getFloatArray(SpeechRecognizer.CONFIDENCE_SCORES)

                    if (!matches.isNullOrEmpty()) {
                        val bestMatch = matches[0]
                        val confidenceScore = confidence?.getOrNull(0) ?: 0.0f
                        Log.d(TAG, "Texto reconocido: '$bestMatch' (confianza: $confidenceScore)")
                        trySend(SpeechRecognitionResult.Success(bestMatch, confidenceScore))
                    } else {
                        trySend(SpeechRecognitionResult.Error("No se pudo reconocer el habla"))
                    }
                    close()
                }

                override fun onPartialResults(partialResults: android.os.Bundle?) {
                    val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    if (!matches.isNullOrEmpty()) {
                        val partialText = matches[0]
                        Log.d(TAG, "Resultado parcial: '$partialText'")
                        trySend(SpeechRecognitionResult.Partial(partialText))
                    }
                }

                override fun onEvent(eventType: Int, params: android.os.Bundle?) {
                    // Eventos especiales (no comúnmente usado)
                }
            })
        }

        // Configurar el intent para reconocimiento de voz
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Habla ahora...")
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        }

        isListening = true
        speechRecognizer?.startListening(intent)

        awaitClose {
            Log.d(TAG, "Cerrando reconocimiento de voz")
            stopListening()
        }
    }

    fun stopListening() {
        if (isListening) {
            speechRecognizer?.stopListening()
            isListening = false
        }
    }

    fun destroy() {
        speechRecognizer?.destroy()
        speechRecognizer = null
        isListening = false
    }
}

// Resultados del reconocimiento de voz
sealed class SpeechRecognitionResult {
    object Listening : SpeechRecognitionResult()
    object Speaking : SpeechRecognitionResult()
    object FinishedSpeaking : SpeechRecognitionResult()
    data class RmsChanged(val rmsdB: Float) : SpeechRecognitionResult()
    data class Partial(val text: String) : SpeechRecognitionResult()
    data class Success(val text: String, val confidence: Float) : SpeechRecognitionResult()
    data class Error(val message: String) : SpeechRecognitionResult()
}