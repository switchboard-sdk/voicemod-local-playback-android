package audio.switchboard.voicemodlocalplayback.voicemodafterrecording

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import audio.switchboard.voicemodlocalplayback.playerwithvoicemod.PlayerWithVoicemodAudioEngine
import audio.switchboard.voicemodlocalplayback.utils.voices
import com.synervoz.switchboard.sdk.Codec
import com.synervoz.switchboard.sdk.utils.AssetLoader
import com.synervoz.switchboard.sdk.utils.FileExporter
import com.synervoz.switchboard.ui.customviews.SBAudioFileView
import com.synervoz.switchboard.ui.customviews.SBButtonView
import com.synervoz.switchboard.ui.customviews.SBSpinnerView
import com.synervoz.switchboard.ui.customviews.SBSwitchView
import com.synervoz.switchboard.ui.customviews.SBTextView
import com.synervoz.switchboard.ui.customviews.containers.SBHorizontalStack
import com.synervoz.switchboard.ui.customviews.containers.SBVerticalStack

class VoicemodAfterRecordingFragment: Fragment() {

    private lateinit var example: VoicemodAfterRecordingAudioEngine

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {


        example = VoicemodAfterRecordingAudioEngine(requireContext())
        example.voicemodNode.loadVoice("baby")
        val view =  SBVerticalStack(requireContext()).addSBViews(listOf(
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
            SBTextView(requireContext(), "Recorder"),
            SBHorizontalStack(requireContext())
                .addSBViews(
                    listOf(
                        SBButtonView(context = requireContext(),
                            title = "Start") { button: Button ->
                            example.record()
                        },
                        SBButtonView(context = requireContext(),
                            title = "Stop") { button: Button ->
                            example.stopRecord()
                        }
                    )
                ),
            SBTextView(requireContext(), "Playback"),
            SBHorizontalStack(requireContext())
                .addSBViews(
                    listOf(
                        SBButtonView(context = requireContext(),
                            title = "Start") { button: Button ->
                            example.play()
                        },
                        SBButtonView(context = requireContext(),
                            title = "Stop") { button: Button ->
                            example.stopPlayer()
                        }
                    )
                ),
            SBButtonView(
                context = requireContext(),
                title = "Export Recording"
            ) { button: Button ->
                exportFile(example.recordingFilePath)
            }
        )).getView()
        return view.rootView
    }

    private fun exportFile(filePath: String) {
        FileExporter.export(requireActivity(), filePath)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        example.stop()
        example.close()
    }
}