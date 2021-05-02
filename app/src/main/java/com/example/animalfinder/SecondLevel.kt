package com.example.animalfinder

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import java.util.*


class SecondLevel : AppCompatActivity() {

    var tts: TextToSpeech? = null
    val get_info:  Get_Informations = Get_Informations()
    val animals_image = get_info.get_images()
    val animals_sound = get_info.get_sounds()
    private lateinit var timer: CountDownTimer
    var mediaPlayer: MediaPlayer?=null
    var point = 0
    var highScore: Int? = null
    lateinit var sharedPref: SharedPreferences
    var choosenAnimal = 0
    var animalSound = 0


    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.level2)

        // Taking current point from the previous level
        point = intent.getIntExtra("point", point)
        //Showing current score in UI
        findViewById<TextView>(R.id.txtPoint).setText("Points: $point")

        // Saving higher score in shared preferences
        sharedPref = this.getSharedPreferences("mypref", Context.MODE_PRIVATE)
        highScore = sharedPref.getInt("highScore", 0)
        //Showing higher score in UI
        findViewById<TextView>(R.id.higherscore).setText("High Score: $highScore")

        // getting questions
        getAnimal()


        // telling animal name
        val animalName = findViewById<TextView>(R.id.txtAnimalName)
        var speak =  animalName.text.toString()
        tts = TextToSpeech(applicationContext, TextToSpeech.OnInitListener { status ->
            if(status == TextToSpeech.SUCCESS){
                tts!!.language = Locale.UK
                tts!!.speak(speak, TextToSpeech.QUEUE_FLUSH, null, "")

                while (tts!!.isSpeaking){
                    //waiting finish the speaking
                }
                if (tts!!.isSpeaking == false){
                    mediaPlayer = MediaPlayer.create(this, animalSound)
                    mediaPlayer?.start()
                }
            }
        })

        val builder = AlertDialog.Builder(this)
        // Creating timer
        timer = object: CountDownTimer(18000, 1000){
            override fun onFinish() {
                tts!!.speak("Game Over. Your score is $point, finish game ", TextToSpeech.QUEUE_FLUSH, null, "")
                val intent = Intent(applicationContext,MainActivity::class.java)
                builder.setTitle("Game Over")
                builder.setMessage("Your Score is $point, Finish Game")
                point = 0
                builder.setPositiveButton("Yes", DialogInterface.OnClickListener{
                    dialog, which ->
                    startActivity(intent)
                    finish()
                })
                builder.show()
            }

            override fun onTick(millisUntilFinished: Long) {
                findViewById<TextView>(R.id.kronometre).setText("Time: ${millisUntilFinished/1000} sec")
            }
        }.start()
    }
    override fun onBackPressed() {
        super.onBackPressed()
        timer.cancel()
    }

    override fun onDestroy() {
        super.onDestroy()
        timer.cancel()
    }
    private fun getAnimal() {
        // Getting animal name from the file
        val scanner: Scanner =  Scanner(resources.openRawResource(R.raw.animalname))
        val animals_name = get_info.read_file_content(scanner)
        scanner.close()

        // getting images for question
        val controller = get_info.choose_animalList(3)
        // getting animal name for choosen images
        choosenAnimal = get_info.choose_one_Animal(3, controller)
        animalSound = animals_sound[choosenAnimal]
        val animal_name = animals_name[choosenAnimal]
        // showing animal name in UI
        val txt_name = findViewById<TextView>(R.id.txtAnimalName)
        txt_name.setText(animal_name)



        // Showing animal images in UI
        val img = findViewById<ImageView>(R.id.image1)
        img.setImageResource(animals_image[controller[0]])
        img.setTag(animals_name[controller[0]])

        val img1 = findViewById<ImageView>(R.id.image2)
        img1.setTag(animals_name[controller[1]])
        img1.setImageResource(animals_image[controller[1]])

        val img2 = findViewById<ImageView>(R.id.image3)
        img2.setTag(animals_name[controller[2]])
        img2.setImageResource(animals_image[controller[2]])

        return
    }

    // If user click the animal name, the machine tell the name.
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun speak_name(view: View){
        val str = findViewById<TextView>(R.id.txtAnimalName).text.toString()
        tts!!.speak(str, TextToSpeech.QUEUE_FLUSH, null, "")

    }

    // Answer contolling
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun answer_controller(view: View) {
        //Reading images and text
        val img1 = findViewById<ImageView>(R.id.image1)
        val img2 = findViewById<ImageView>(R.id.image2)
        val img3 = findViewById<ImageView>(R.id.image3)
        val txt = findViewById<TextView>(R.id.txtAnimalName).text.toString()

        // Controlling answer
        if(view.getId()  == R.id.image1 && txt == img1.tag.toString() ||
            view.getId() == R.id.image2 && txt == img2.tag.toString() ||
            view.getId() == R.id.image3 && txt == img3.tag.toString()   ){
            point++
            // Controlling higher score is higher than current score
            if(point > highScore!!){
                sharedPref.edit().putInt("highScore", point).apply()
                highScore = sharedPref.getInt("highScore", 0)
                findViewById<TextView>(R.id.higherscore).setText("High Score: $highScore")
            }
            tts!!.speak("True", TextToSpeech.QUEUE_FLUSH, null, "")
            // Doing this level must have 3 true answer point will be start 3
            if(point == 6){
                tts!!.speak("Congratulations", TextToSpeech.QUEUE_FLUSH, null, "")
                val intent = Intent(this, ThirdLevel::class.java)
                intent.putExtra("point", point)
                startActivity(intent)
                finish()
            }
            else{
                timer.cancel()
                timer.start()
                findViewById<TextView>(R.id.txtPoint).setText("Points: $point")
                getAnimal()
                tts!!.speak(findViewById<TextView>(R.id.txtAnimalName).text.toString(), TextToSpeech.QUEUE_ADD, null ,"")
                while (tts!!.isSpeaking){
                    //waiting the finishing the speech
                }
                if (tts!!.isSpeaking == false){
                    mediaPlayer = MediaPlayer.create(this, animalSound)
                    mediaPlayer?.start()
                }
            }

        }
        // Wrong answer will finnish the game
        else{
            val builder = AlertDialog.Builder(this)
            tts!!.speak("Game Over. Your score is $point, finish game ", TextToSpeech.QUEUE_FLUSH, null, "")
            val intent = Intent(applicationContext,MainActivity::class.java)
            builder.setTitle("Game Over")
            builder.setMessage("Your Score is $point, Finish Game")
            point = 0
            builder.setPositiveButton("Yes", DialogInterface.OnClickListener{
                dialog, which ->
                startActivity(intent)
                finish()
            })
            builder.show()
        }


    }

}

