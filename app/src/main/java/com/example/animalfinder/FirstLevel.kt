package com.example.animalfinder

import android.annotation.SuppressLint
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
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.util.*


class FirstLevel : AppCompatActivity() {
    var tts: TextToSpeech? = null
    val get_info:  Get_Informations = Get_Informations()
    // Deffining an array of images
    val animals_image = get_info.get_images()
    val animals_sound = get_info.get_sounds()
    private lateinit var timer: CountDownTimer
    var mediaPlayer: MediaPlayer?=null
    var point = 0
    var highScore: Int? = null
    lateinit var sharedPref: SharedPreferences
    var choosenAnimal = 0
    var animalSound = 0


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.level1)


        // adding Current point in uı
        findViewById<TextView>(R.id.txtPoint).setText("Points: $point")

        // Saving Highest point in shared preferences
        sharedPref = this.getSharedPreferences("mypref", Context.MODE_PRIVATE)
        highScore = sharedPref.getInt("highScore", 0)

        // Writing higher score in uı
        findViewById<TextView>(R.id.higherscore).setText("High Score: $highScore")
        // Geting random images and animal name
        getAnimal()




        // Telling animal name
        val animalName = findViewById<TextView>(R.id.txtAnimalName)
        var speak =  animalName.text.toString()
        // Creating Text2Speech
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
        // Creating Timer object and Showing in UI
        timer = object: CountDownTimer(21000, 1000){
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

    // Getting animal
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun getAnimal() {
        // Getting animal name from file
        val scanner: Scanner =  Scanner(resources.openRawResource(R.raw.animalname))
        val animals_name = get_info.read_file_content(scanner)
        scanner.close()

        // Getting Animal ID for images that showing
        val controller = get_info.choose_animalList(2)
        // Choosing random text from choosing images
        choosenAnimal = get_info.choose_one_Animal(2, controller)

        animalSound = animals_sound[choosenAnimal]

        // adding animal name on the screen
        val animal_name = animals_name[choosenAnimal]
        val txt_name = findViewById<TextView>(R.id.txtAnimalName)
        txt_name.setText(animal_name)



        // adding images to imageview
        val img = findViewById<ImageView>(R.id.image1)
        img.setImageResource(animals_image[controller[0]])
        img.setTag(animals_name[controller[0]])
        val img1 = findViewById<ImageView>(R.id.image2)
        img1.setTag(animals_name[controller[1]])
        img1.setImageResource(animals_image[controller[1]])


        return
    }

    // If user click the animal name, the machine tell the name.
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun speak_name(view: View){
        val str = findViewById<TextView>(R.id.txtAnimalName).text.toString()
        tts!!.speak(str, TextToSpeech.QUEUE_FLUSH, null, "")

    }


    // answer controlling

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun answer_controller(view: View) {

        //Reading images and text
        val img1 = findViewById<ImageView>(R.id.image1)
        val img2 = findViewById<ImageView>(R.id.image2)
        val txt = findViewById<TextView>(R.id.txtAnimalName).text.toString()

        // Controlling answer
        if(view.getId() == R.id.image1 && txt == img1.tag.toString() ||
                view.getId() ==R.id.image2 && txt == img2.tag.toString() ){
            point++
            tts!!.speak("True",TextToSpeech.QUEUE_FLUSH, null, "")
            // Controlling higher score is higher than current score
            if(point > highScore!!){
                sharedPref.edit().putInt("highScore", point).apply()
                highScore = sharedPref.getInt("highScore", 0)
                findViewById<TextView>(R.id.higherscore).setText("High Score: $highScore")
            }

            // Doing this level must have 3 true answer
            if(point == 3){
                tts!!.speak("Congratulations", TextToSpeech.QUEUE_FLUSH, null, "")
                val intent = Intent(this, SecondLevel::class.java)
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
                    //waiting finish the speaking
                }
                if (tts!!.isSpeaking == false){
                    mediaPlayer = MediaPlayer.create(this, animalSound)
                    mediaPlayer?.start()
                }
            }



        }
        // Wrong answer will finnish the game
        else {
            val builder = android.app.AlertDialog.Builder(this)
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

