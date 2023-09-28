package com.soda.soda.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.soda.soda.R
import com.soda.soda.databinding.FragmentSurrondCustomBinding
import com.soda.soda.helper.AudioClassificationHelper
import com.soda.soda.helper.DECIBEL_THRESHOLD

private const val TAG = "SurroundCustomFragment"

class SurroundCustomFragment : Fragment() {

    private var _binding: FragmentSurrondCustomBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSurrondCustomBinding.inflate(inflater, container, false)
        setupRadioGroup()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val surroundState = loadSurround(requireContext())
        if(surroundState != null){
            if(surroundState == "default"){
                binding.defaultButton.isChecked = true
                setSurroundSaved(requireContext(), "default")
            }
            else if(surroundState == "crowded"){
                binding.crowdedButton.isChecked = true
                AudioClassificationHelper.excludedLabel = listOf("Silence", "Speech")
            }
            else if(surroundState == "music"){
                binding.musicButton.isChecked = true
                AudioClassificationHelper.excludedLabel = listOf(
                    "Silence",
                    "Music",
                    "Musical instrument",
                    "Plucked string instrument",
                    "Guitar",
                    "Electric guitar",
                    "Bass guitar",
                    "Acoustic guitar",
                    "Steel guitar, slide guitar",
                    "Tapping (guitar technique)",
                    "Strum",
                    "Banjo",
                    "Sitar",
                    "Mandolin",
                    "Zither",
                    "Ukulele",
                    "Keyboard (musical)",
                    "Piano",
                    "Electric piano",
                    "Organ",
                    "Electronic organ",
                    "Hammond organ",
                    "Synthesizer",
                    "Sampler",
                    "Harpsichord",
                    "Percussion",
                    "Drum kit",
                    "Drum machine",
                    "Drum",
                    "Snare drum",
                    "Rimshot",
                    "Drum roll",
                    "Bass drum",
                    "Timpani",
                    "Tabla",
                    "Cymbal",
                    "Hi-hat",
                    "Wood block",
                    "Tambourine",
                    "Rattle (instrument)",
                    "Maraca",
                    "Gong",
                    "Tubular bells",
                    "Mallet percussion",
                    "Marimba, xylophone",
                    "Glockenspiel",
                    "Vibraphone",
                    "Steelpan",
                    "Orchestra",
                    "Brass instrument",
                    "French horn",
                    "Trumpet",
                    "Trombone",
                    "Bowed string instrument",
                    "String section",
                    "Violin, fiddle",
                    "Pizzicato",
                    "Cello",
                    "Double bass",
                    "Wind instrument, woodwind instrument",
                    "Flute",
                    "Saxophone",
                    "Clarinet",
                    "Harp",
                    "Bell",
                    "Church bell",
                    "Jingle bell",
                    "Bicycle bell",
                    "Tuning fork",
                    "Chime",
                    "Wind chime",
                    "Change ringing (campanology)",
                    "Harmonica",
                    "Accordion",
                    "Bagpipes",
                    "Didgeridoo",
                    "Shofar",
                    "Theremin",
                    "Singing bowl",
                    "Scratching (performance technique)",
                    "Pop music",
                    "Hip hop music",
                    "Beatboxing",
                    "Rock music",
                    "Heavy metal",
                    "Punk rock",
                    "Grunge",
                    "Progressive rock",
                    "Rock and roll",
                    "Psychedelic rock",
                    "Rhythm and blues",
                    "Soul music",
                    "Reggae",
                    "Country",
                    "Swing music",
                    "Bluegrass",
                    "Funk",
                    "Folk music",
                    "Middle Eastern music",
                    "Jazz",
                    "Disco",
                    "Classical music",
                    "Opera",
                    "Electronic music",
                    "House music",
                    "Techno",
                    "Dubstep",
                    "Drum and bass",
                    "Electronica",
                    "Electronic dance music",
                    "Ambient music",
                    "Trance music",
                    "Music of Latin America",
                    "Salsa music",
                    "Flamenco",
                    "Blues",
                    "Music for children",
                    "New-age music",
                    "Vocal music",
                    "A capella",
                    "Music of Africa",
                    "Afrobeat",
                    "Christian music",
                    "Gospel music",
                    "Music of Asia",
                    "Carnatic music",
                    "Music of Bollywood",
                    "Ska",
                    "Traditional music",
                    "Independent music",
                    "Song",
                    "Background music",
                    "Theme music",
                    "Jingle (music)",
                    "Soundtrack music",
                    "Lullaby",
                    "Video game music",
                    "Christmas music",
                    "Dance music",
                    "Wedding music",
                    "Happy music",
                    "Sad music",
                    "Tender music",
                    "Exciting music",
                    "Angry music",
                    "Scary music")
            }
        }
    }

    private fun setupRadioGroup() {
        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.crowded_button -> {
                    AudioClassificationHelper.excludedLabel = listOf("Silence", "Speech")
                    setSurroundSaved(requireContext(), "crowded")
                }
                R.id.default_button -> {
                    AudioClassificationHelper.excludedLabel = listOf("Silence")
                    setSurroundSaved(requireContext(), "default")
                }
                R.id.music_button -> {
                    AudioClassificationHelper.excludedLabel = listOf(
                        "Silence",
                        "Music",
                        "Musical instrument",
                        "Plucked string instrument",
                        "Guitar",
                        "Electric guitar",
                        "Bass guitar",
                        "Acoustic guitar",
                        "Steel guitar, slide guitar",
                        "Tapping (guitar technique)",
                        "Strum",
                        "Banjo",
                        "Sitar",
                        "Mandolin",
                        "Zither",
                        "Ukulele",
                        "Keyboard (musical)",
                        "Piano",
                        "Electric piano",
                        "Organ",
                        "Electronic organ",
                        "Hammond organ",
                        "Synthesizer",
                        "Sampler",
                        "Harpsichord",
                        "Percussion",
                        "Drum kit",
                        "Drum machine",
                        "Drum",
                        "Snare drum",
                        "Rimshot",
                        "Drum roll",
                        "Bass drum",
                        "Timpani",
                        "Tabla",
                        "Cymbal",
                        "Hi-hat",
                        "Wood block",
                        "Tambourine",
                        "Rattle (instrument)",
                        "Maraca",
                        "Gong",
                        "Tubular bells",
                        "Mallet percussion",
                        "Marimba, xylophone",
                        "Glockenspiel",
                        "Vibraphone",
                        "Steelpan",
                        "Orchestra",
                        "Brass instrument",
                        "French horn",
                        "Trumpet",
                        "Trombone",
                        "Bowed string instrument",
                        "String section",
                        "Violin, fiddle",
                        "Pizzicato",
                        "Cello",
                        "Double bass",
                        "Wind instrument, woodwind instrument",
                        "Flute",
                        "Saxophone",
                        "Clarinet",
                        "Harp",
                        "Bell",
                        "Church bell",
                        "Jingle bell",
                        "Bicycle bell",
                        "Tuning fork",
                        "Chime",
                        "Wind chime",
                        "Change ringing (campanology)",
                        "Harmonica",
                        "Accordion",
                        "Bagpipes",
                        "Didgeridoo",
                        "Shofar",
                        "Theremin",
                        "Singing bowl",
                        "Scratching (performance technique)",
                        "Pop music",
                        "Hip hop music",
                        "Beatboxing",
                        "Rock music",
                        "Heavy metal",
                        "Punk rock",
                        "Grunge",
                        "Progressive rock",
                        "Rock and roll",
                        "Psychedelic rock",
                        "Rhythm and blues",
                        "Soul music",
                        "Reggae",
                        "Country",
                        "Swing music",
                        "Bluegrass",
                        "Funk",
                        "Folk music",
                        "Middle Eastern music",
                        "Jazz",
                        "Disco",
                        "Classical music",
                        "Opera",
                        "Electronic music",
                        "House music",
                        "Techno",
                        "Dubstep",
                        "Drum and bass",
                        "Electronica",
                        "Electronic dance music",
                        "Ambient music",
                        "Trance music",
                        "Music of Latin America",
                        "Salsa music",
                        "Flamenco",
                        "Blues",
                        "Music for children",
                        "New-age music",
                        "Vocal music",
                        "A capella",
                        "Music of Africa",
                        "Afrobeat",
                        "Christian music",
                        "Gospel music",
                        "Music of Asia",
                        "Carnatic music",
                        "Music of Bollywood",
                        "Ska",
                        "Traditional music",
                        "Independent music",
                        "Song",
                        "Background music",
                        "Theme music",
                        "Jingle (music)",
                        "Soundtrack music",
                        "Lullaby",
                        "Video game music",
                        "Christmas music",
                        "Dance music",
                        "Wedding music",
                        "Happy music",
                        "Sad music",
                        "Tender music",
                        "Exciting music",
                        "Angry music",
                        "Scary music")
                    setSurroundSaved(requireContext(), "music")
                }
            }
            Log.d(TAG, "setupRadioGroup: ${AudioClassificationHelper.excludedLabel}")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // 메모리 누수 방지를 위해 null로 설정
    }

    private fun loadSurround(context: Context): String? {
        val sharedPref = context.getSharedPreferences("surround_saved_pref", Context.MODE_PRIVATE)
        return sharedPref.getString("surround", null)
    }

    private fun setSurroundSaved(context: Context, value: String){
        val sharedPref = context.getSharedPreferences("surround_saved_pref", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString("surround", value)
        editor.apply()
    }

}
