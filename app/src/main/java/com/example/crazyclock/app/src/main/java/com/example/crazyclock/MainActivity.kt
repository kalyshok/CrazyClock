package com.example.crazyclock

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Установка экрана загрузки
        installSplashScreen()
        
        super.onCreate(savedInstanceState)
        
        // Создаем наши часы и выводим их на экран
        val clockView = CrazyUltimateClockView(this, null)
        setContentView(clockView)
        
        // Делаем на весь экран (скрываем лишние панели)
        val controller = WindowCompat.getInsetsController(window, window.decorView)
        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }
}
