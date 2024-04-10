package audio.switchboard.voicemodlocalplayback

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.synervoz.switchboard.sdk.Codec
import com.synervoz.switchboard.sdk.utils.AssetLoader
import com.synervoz.switchboard.ui.customviews.SBAudioFileView
import com.synervoz.switchboard.ui.customviews.SBButtonView
import com.synervoz.switchboard.ui.customviews.SBSpinnerView
import com.synervoz.switchboard.ui.customviews.SBSwitchView
import com.synervoz.switchboard.ui.customviews.containers.SBHorizontalStack
import com.synervoz.switchboard.ui.customviews.containers.SBVerticalStack

class MainActivity : AppCompatActivity() {

    private lateinit var example: PlayerWithVoicemodAudioEngine

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val voices = listOf(
            "baby",
            "blocks",
            "cave",
            "deep",
            "magic-chords",
            "out-of-range",
            "pilot",
            "speechifier",
            "the-narrator",
            "trap-tune",
        )

        val audioFiles = listOf("Female-Vocal.wav", "speech-female-T001.wav", "speech-male-T003.wav")

        example = PlayerWithVoicemodAudioEngine(this)
        example.playerNode.load(AssetLoader.load(this, "Female-Vocal.wav"), Codec.WAV)
        example.voicemodNode.loadVoice("baby")
        example.startEngine()
        val view =  SBVerticalStack(this).addSBViews(listOf(
            SBAudioFileView(
                this,
                title = "Audio File",
                0,
                audioFiles
            ) { selectedFile: String ->
                val wasPlaying = example.isPlayingAudio()
                example.stop()
                example.load(
                    AssetLoader.load(this, selectedFile),
                    Codec.createFromFileName(selectedFile)
                )
                if (wasPlaying) {
                    example.play()
                }
            },
            SBSpinnerView(
                this,
                title = "Voice",
                0,
                voices.map { it }) { selectedVoice: String ->
                example.voicemodNode.loadVoice(selectedVoice)
            },
            SBSwitchView(context = this, title = "Bypass" , initialState = example.voicemodNode.bypassEnabled) { isChecked ->
                example.voicemodNode.bypassEnabled = isChecked
            },
            SBSwitchView(context = this, title = "Mute" , initialState = example.voicemodNode.muteEnabled) { isChecked ->
                example.voicemodNode.muteEnabled = isChecked
            },
            SBHorizontalStack(this)
                .addSBViews(
                    listOf(
                        SBButtonView(context = this,
                            title = "Play") { button: Button ->
                            example.play()
                        },
                        SBButtonView(context = this,
                            title = "Stop") { button: Button ->
                            example.stop()
                        }
                    )
                ),
        )).getView()
        setContentView(view)
    }

    override fun onDestroy() {
        super.onDestroy()
        example.stopEngine()
        example.close()
    }
}