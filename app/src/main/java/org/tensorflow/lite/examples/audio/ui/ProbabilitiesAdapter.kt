/*
 * Copyright 2022 The TensorFlow Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *             http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.tensorflow.lite.examples.audio.ui

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.tensorflow.lite.examples.audio.R
import org.tensorflow.lite.examples.audio.databinding.ItemProbabilityBinding
import org.tensorflow.lite.support.label.Category

internal class ProbabilitiesAdapter : RecyclerView.Adapter<ProbabilitiesAdapter.ViewHolder>() {
    var categoryList: List<Category> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemProbabilityBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val category = categoryList[position]
        if(category.label.equals("Speech")){
            holder.bind("말하기소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Child speech, kid speaking")){
            holder.bind("어린이 말하기, 어린이 말하기소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Conversation")){
            holder.bind("대화소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Narration, monologue")){
            holder.bind("내레이션, 독백소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Babbling")){
            holder.bind("옹알이소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Speech synthesizer")){
            holder.bind("음성 합성기소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Shout")){
            holder.bind("Shout소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Bellow")){
            holder.bind("Bellow소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Whoop")){
            holder.bind("Whoop소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Yell")){
            holder.bind("Yell소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Children shouting")){
            holder.bind("소리 지르는 어린이소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Screaming")){
            holder.bind("비명소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Whispering")){
            holder.bind("속삭이는 소리소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Laughter")){
            holder.bind("웃음소리소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Baby laughter")){
            holder.bind("아기 웃음소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Giggle")){
            holder.bind("낄낄거리기소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Snicker")){
            holder.bind("스니커소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Belly laugh")){
            holder.bind("배꼽 웃음소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Chuckle, chortle")){
            holder.bind("낄낄, 낄낄소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Crying, sobbing")){
            holder.bind("울음, 흐느낌소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Baby cry, infant cry")){
            holder.bind("아기 울음, 유아 울음소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Whimper")){
            holder.bind("Whimper소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Wail, moan")){
            holder.bind("울음, 신음소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Sigh")){
            holder.bind("한숨소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Singing")){
            holder.bind("노래소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Choir")){
            holder.bind("합창단소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Yodeling")){
            holder.bind("요들송소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Chant")){
            holder.bind("Chant소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Mantra")){
            holder.bind("만트라소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Child singing")){
            holder.bind("어린이 노래소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Synthetic singing")){
            holder.bind("합성 노래소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Rapping")){
            holder.bind("랩소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Humming")){
            holder.bind("허밍소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Groan")){
            holder.bind("Groan소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Grunt")){
            holder.bind("Grunt소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Whistling")){
            holder.bind("휘파람소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Breathing")){
            holder.bind("호흡소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Wheeze")){
            holder.bind("Wheeze소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Snoring")){
            holder.bind("코골이소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Gasp")){
            holder.bind("Gasp소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Pant")){
            holder.bind("헐떡거림소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Snort")){
            holder.bind("Snort소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Cough")){
            holder.bind("Cough소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Throat clearing")){
            holder.bind("인후통소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Sneeze")){
            holder.bind("재채기소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Sniff")){
            holder.bind("Sniff소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Run")){
            holder.bind("Run소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Shuffle")){
            holder.bind("셔플소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Walk, footsteps")){
            holder.bind("걷기, 발걸음소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Chewing, mastication")){
            holder.bind("씹기, 저작소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Biting")){
            holder.bind("물기소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Gargling")){
            holder.bind("양치질소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Stomach rumble")){
            holder.bind("복부 럼블소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Burping, eructation")){
            holder.bind("트림, 사정소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Hiccup")){
            holder.bind("딸꾹질소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Fart")){
            holder.bind("방귀소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Hands")){
            holder.bind("손소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Finger snapping")){
            holder.bind("손가락 스냅소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Clapping")){
            holder.bind("박수 소리소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Heart sounds, heartbeat")){
            holder.bind("심장 소리, 심장 박동소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Heart murmur")){
            holder.bind("심장 잡음소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Cheering")){
            holder.bind("응원소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Applause")){
            holder.bind("박수소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Chatter")){
            holder.bind("수다소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Crowd")){
            holder.bind("군중소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Hubbub, speech noise, speech babble")){
            holder.bind("윙윙거리는 소리, 말소리, 옹알이 소리소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Children playing")){
            holder.bind("노는 아이들소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Animal")){
            holder.bind("동물소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Domestic animals, pets")){
            holder.bind("가축, 애완동물소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Dog")){
            holder.bind("개소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Bark")){
            holder.bind("Bark소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Yip")){
            holder.bind("Yip소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Howl")){
            holder.bind("Howl소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Bow-wow")){
            holder.bind("Bow-wow소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Growling")){
            holder.bind("Growling소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Whimper (dog)")){
            holder.bind("윙윙(개)소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Cat")){
            holder.bind("고양이소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Purr")){
            holder.bind("Purr소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Meow")){
            holder.bind("Meow소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Hiss")){
            holder.bind("Hiss소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Caterwaul")){
            holder.bind("Caterwaul소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Livestock, farm animals, working animals")){
            holder.bind("가축, 농장 동물, 일하는 동물소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Horse")){
            holder.bind("말소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Clip-clop")){
            holder.bind("클립-클롭소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Neigh, whinny")){
            holder.bind("이웃, 우는 소리소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Cattle, bovinae")){
            holder.bind("소, 소소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Moo")){
            holder.bind("Moo소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Cowbell")){
            holder.bind("Cowbell소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Pig")){
            holder.bind("Pig소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Oink")){
            holder.bind("Oink소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Goat")){
            holder.bind("Goat소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Bleat")){
            holder.bind("Bleat소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Sheep")){
            holder.bind("Sheep소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Fowl")){
            holder.bind("Fowl소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Chicken, rooster")){
            holder.bind("닭, 수탉소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Cluck")){
            holder.bind("Cluck소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Crowing, cock-a-doodle-doo")){
            holder.bind("울음소리, 두두두두소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Turkey")){
            holder.bind("칠면조소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Gobble")){
            holder.bind("Gobble소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Duck")){
            holder.bind("Duck소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Quack")){
            holder.bind("Quack소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Goose")){
            holder.bind("Goose소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Honk")){
            holder.bind("Honk소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Wild animals")){
            holder.bind("야생 동물소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Roaring cats (lions, tigers)")){
            holder.bind("포효하는 고양이(사자, 호랑이)소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Roar")){
            holder.bind("Roar소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Bird")){
            holder.bind("Bird소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Bird vocalization, bird call, bird song")){
            holder.bind("새 발성, 새 울음소리, 새 노래소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Chirp, tweet")){
            holder.bind("지저귀는 소리, 지저귀는 소리소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Squawk")){
            holder.bind("스쿼크소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Pigeon, dove")){
            holder.bind("비둘기, 비둘기소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Coo")){
            holder.bind("Coo소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Crow")){
            holder.bind("까마귀소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Caw")){
            holder.bind("Caw소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Owl")){
            holder.bind("Owl소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Hoot")){
            holder.bind("Hoot소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Bird flight, flapping wings")){
            holder.bind("새의 비행, 날갯짓소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Canidae, dogs, wolves")){
            holder.bind("개과 동물, 개, 늑대소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Rodents, rats, mice")){
            holder.bind("설치류, 쥐, 생쥐소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Mouse")){
            holder.bind("마우스소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Patter")){
            holder.bind("Patter소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Insect")){
            holder.bind("곤충소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Cricket")){
            holder.bind("크리켓소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Mosquito")){
            holder.bind("모기소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Fly, housefly")){
            holder.bind("파리, 집파리소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Buzz")){
            holder.bind("Buzz소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Bee, wasp, etc.")){
            holder.bind("벌, 말벌 등소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Frog")){
            holder.bind("개구리소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Croak")){
            holder.bind("Croak소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Snake")){
            holder.bind("뱀소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Rattle")){
            holder.bind("딸랑이소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Whale vocalization")){
            holder.bind("고래 발성소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Music")){
            holder.bind("음악소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Musical instrument")){
            holder.bind("악기소리 같습니다.", category.score, category.index)
        }
        else if(category.label.equals("Plucked string instrument")){
            holder.bind("현악기소리 같습니다.", category.score, category.index)
        }

    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    class ViewHolder(private val binding: ItemProbabilityBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private var primaryProgressColorList: IntArray
        private var backgroundProgressColorList: IntArray

        init {
            primaryProgressColorList =
                binding.root.resources.getIntArray((R.array.colors_progress_primary))
            backgroundProgressColorList =
                binding.root.resources.getIntArray((R.array.colors_progress_background))
        }

        fun bind(label: String, score: Float, index: Int) {
            with(binding) {
                labelTextView.text = label

                progressBar.progressBackgroundTintList =
                    ColorStateList.valueOf(
                        backgroundProgressColorList[index % backgroundProgressColorList.size])

                progressBar.progressTintList =
                    ColorStateList.valueOf(
                        primaryProgressColorList[index % primaryProgressColorList.size])

                val newValue = (score * 100).toInt()
                progressBar.progress = newValue
            }
        }
    }
}
