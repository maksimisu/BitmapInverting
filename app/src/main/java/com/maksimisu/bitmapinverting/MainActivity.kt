package com.maksimisu.bitmapinverting

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.blue
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.green
import androidx.core.graphics.red
import com.maksimisu.bitmapinverting.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    
    class LooperThread : Thread() {
        var handler: Handler? = null

        override fun run() {
            Looper.prepare()
            handler = Looper.myLooper()?.let {
                Handler(it)
            }
            Looper.loop()
        }

    }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        var bitmap = BitmapFactory.decodeResource(resources, R.drawable.image)
        binding.imageView.setImageBitmap(bitmap)

        val mainThreadHandler = Handler(Looper.getMainLooper())

        val bgThread = LooperThread().apply {
            start()
        }

        binding.btnHandler.setOnClickListener {

            bitmap = binding.imageView.drawable.toBitmap()

            val newBitmap = Bitmap.createBitmap(
                bitmap.width,
                bitmap.height,
                Bitmap.Config.ARGB_8888
            )

            var inverted = 0

            binding.btnHandler.isEnabled = false
            binding.btnRunnable.isEnabled = false

            bgThread.handler?.post {

                val size = bitmap.width * bitmap.height
                for (x in 0 until bitmap.width) {
                    for (y in 0 until bitmap.height) {
                        val color = bitmap.getPixel(x, y)
                        val newColor =
                            Color.rgb(255 - color.red, 255 - color.green, 255 - color.blue)
                        newBitmap.setPixel(x, y, newColor)
                        inverted++
                        if (inverted % 10000 == 0 || inverted == size) {
                            mainThreadHandler.post {
                                binding.tvProgress.text = "$inverted/$size"
                            }
                        }
                    }
                }
                mainThreadHandler.post {
                    binding.imageView.setImageBitmap(newBitmap)
                    binding.btnHandler.isEnabled = true
                    binding.btnRunnable.isEnabled = true
                }
            }

        }

        binding.btnRunnable.setOnClickListener {

            bitmap = binding.imageView.drawable.toBitmap()

            val newBitmap = Bitmap.createBitmap(
                bitmap.width,
                bitmap.height,
                Bitmap.Config.ARGB_8888
            )

            var inverted = 0

            binding.btnHandler.isEnabled = false
            binding.btnRunnable.isEnabled = false

            val task = Runnable() {
                run {
                    val size = bitmap.width * bitmap.height
                    for (x in 0 until bitmap.width) {
                        for (y in 0 until bitmap.height) {
                            val color = bitmap.getPixel(x, y)
                            val newColor =
                                Color.rgb(255 - color.red, 255 - color.green, 255 - color.blue)
                            newBitmap.setPixel(x, y, newColor)
                            inverted++
                            if (inverted % 10000 == 0 || inverted == size) {
                                runOnUiThread {
                                    binding.tvProgress.text = "$inverted/$size"
                                }
                            }
                        }
                    }
                    runOnUiThread {
                        binding.imageView.setImageBitmap(newBitmap)
                        binding.btnHandler.isEnabled = true
                        binding.btnRunnable.isEnabled = true
                    }
                }
            }
            val thread = Thread(task)
            thread.start()

        }

    }
}