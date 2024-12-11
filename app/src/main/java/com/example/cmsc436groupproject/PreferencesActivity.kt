package com.example.cmsc436groupproject.controller

import android.os.Bundle
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cmsc436groupproject.R
import com.example.cmsc436groupproject.LocalPreferences
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener

class PreferencesActivity : AppCompatActivity() {

    private lateinit var groupSizeSeekBar: SeekBar
    private lateinit var groupSizeText: TextView
    private lateinit var submitButton: Button
    private var locationInput: String? = null
    private var groupSize: Int = 3 // Default group size

    private lateinit var localPreferences: LocalPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preferences)

        // Initialize Places API
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, "YOUR_API_KEY_HERE")
        }

        localPreferences = LocalPreferences(this)

        // Initialize UI components
        groupSizeSeekBar = findViewById(R.id.group_size_seekbar)
        groupSizeText = findViewById(R.id.group_size_text)
        submitButton = findViewById(R.id.submit_button)

        // Group size SeekBar listener
        groupSizeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                groupSize = progress
                groupSizeText.text = "Preferred Group Size: $groupSize"
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Set up Places AutocompleteSupportFragment
        val autocompleteFragment =
            supportFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment
        autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS))
        autocompleteFragment.setHint("Search for a location")

        // Handle place selection
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                locationInput = place.address
                Toast.makeText(this@PreferencesActivity, "Location selected: $locationInput", Toast.LENGTH_SHORT).show()
            }

            override fun onError(status: com.google.android.gms.common.api.Status) {
                Toast.makeText(this@PreferencesActivity, "Error: ${status.statusMessage}", Toast.LENGTH_SHORT).show()
            }
        })

        // Submit button functionality
        submitButton.setOnClickListener {
            localPreferences.savePreferences(groupSize, locationInput)
            Toast.makeText(this, "Preferences saved!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
