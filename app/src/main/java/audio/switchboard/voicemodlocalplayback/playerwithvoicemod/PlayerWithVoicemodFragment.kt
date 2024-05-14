package audio.switchboard.voicemodlocalplayback.playerwithvoicemod

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import audio.switchboard.voicemodlocalplayback.utils.voices
import com.synervoz.switchboard.sdk.Codec
import com.synervoz.switchboard.sdk.utils.AssetLoader
import com.synervoz.switchboard.ui.customviews.SBAudioFileView
import com.synervoz.switchboard.ui.customviews.SBButtonView
import com.synervoz.switchboard.ui.customviews.SBSpinnerView
import com.synervoz.switchboard.ui.customviews.SBSwitchView
import com.synervoz.switchboard.ui.customviews.containers.SBHorizontalStack
import com.synervoz.switchboard.ui.customviews.containers.SBVerticalStack

class PlayerWithVoicemodFragment : Fragment() {

    private lateinit var example: PlayerWithVoicemodAudioEngine

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        val audioFiles = listOf("Female-Vocal.wav", "speech-female-T001.wav", "speech-male-T003.wav")

        example = PlayerWithVoicemodAudioEngine(requireContext())
        example.playerNode.load(AssetLoader.load(requireContext(), "Female-Vocal.wav"), Codec.WAV)
        example.voicemodNode.loadVoice("baby")
        example.startEngine()
        val view =  SBVerticalStack(requireContext()).addSBViews(listOf(
            SBAudioFileView(
                requireContext(),
                title = "Audio File",
                0,
                audioFiles
            ) { selectedFile: String ->
                val wasPlaying = example.isPlayingAudio()
                example.stop()
                example.load(
                    AssetLoader.load(requireContext(), selectedFile),
                    Codec.createFromFileName(selectedFile)
                )
                if (wasPlaying) {
                    example.play()
                }
            },
            SBSpinnerView(
                requireContext(),
                title = "Voice",
                0,
                voices.map { it }) { selectedVoice: String ->
                example.voicemodNode.loadVoice(selectedVoice)
            },
            SBSwitchView(context = requireContext(), title = "Bypass" , initialState = example.voicemodNode.bypassEnabled) { isChecked ->
                example.voicemodNode.bypassEnabled = isChecked
            },
            SBSwitchView(context = requireContext(), title = "Mute" , initialState = example.voicemodNode.muteEnabled) { isChecked ->
                example.voicemodNode.muteEnabled = isChecked
            },
            SBHorizontalStack(requireContext())
                .addSBViews(
                    listOf(
                        SBButtonView(context = requireContext(),
                            title = "Play") { button: Button ->
                            example.play()
                        },
                        SBButtonView(context = requireContext(),
                            title = "Stop") { button: Button ->
                            example.stop()
                        }
                    )
                ),
        )).getView()
        return view.rootView
    }

    override fun onDestroyView() {
        super.onDestroyView()
        example.stopEngine()
        example.close()
    }
}