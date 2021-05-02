package com.example.animalfinder

import android.os.CountDownTimer
import androidx.appcompat.app.AppCompatActivity
import java.util.*
import kotlin.collections.ArrayList

class Get_Informations: AppCompatActivity() {
    val r = Random()
    var animalsName = ArrayList<String>()
    private lateinit var timer: CountDownTimer
    val animalSounds = arrayOf(R.raw.ayi, R.raw.kus, R.raw.kedi, R.raw.tavuk, R.raw.inek, R.raw.geyik, R.raw.kopek, R.raw.yunus, R.raw.essek, R.raw.fil,
                                R.raw.kurbaga, R.raw.at, R.raw.aslan, R.raw.maymun, R.raw.fare, R.raw.tavsan, R.raw.koyun, R.raw.sincap, R.raw.kaplan, R.raw.kaplumbaga)

    var choosenAnimal = 0
    fun get_images(): Array<Int> {
        val animal_images = arrayOf(R.raw.bear, R.raw.bird, R.raw.cat, R.raw.chicken, R.raw.cow, R.raw.deer,
                                                R.raw.dog, R.raw.dolphin, R.raw.donkey, R.raw.elephant, R.raw.frog,
                                                R.raw.horse, R.raw.lion, R.raw.monkey, R.raw.mouse, R.raw.rabbit, R.raw.sheep,
                                                R.raw.squirrel, R.raw.tiger, R.raw.turtle)
        return animal_images
    }
    fun get_sounds():Array<Int>{
        return animalSounds
    }

    // Selecting images
    fun choose_animalList(size: Int): ArrayList<Int>{

        var controller = ArrayList<Int>()
        for(i in 0..size-1){
            controller.add(r.nextInt(20))
        }
        for(i  in 0.. controller.size-1){
            for( j in 0..controller.size-1){

                if(i != j) {
                    while(controller[i] == controller[j]){

                        controller[j] = r.nextInt(20)

                    }

                }
            }
        }
        return controller
    }
    // Selecting Animal name
    fun choose_one_Animal(size: Int, animals: ArrayList<Int>) : Int{
        choosenAnimal = r.nextInt(size)
        return animals[choosenAnimal]
    }

    
    //Reading Animal name from file
    fun read_file_content(scanner: Scanner): ArrayList<String>{
        while (scanner.hasNext()){
            animalsName.add(scanner.nextLine())
        }
        scanner.close()
        return animalsName
    }


}