package audio.switchboard.voicemodlocalplayback.voicemodafterrecording

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import audio.switchboard.voicemodlocalplayback.utils.voices
import com.synervoz.switchboard.sdk.utils.FileExporter
import com.synervoz.switchboard.ui.customviews.SBButtonView
import com.synervoz.switchboard.ui.customviews.SBSpinnerView
import com.synervoz.switchboard.ui.customviews.SBSwitchView
import com.synervoz.switchboard.ui.customviews.SBTextView
import com.synervoz.switchboard.ui.customviews.containers.SBHorizontalStack
import com.synervoz.switchboard.ui.customviews.containers.SBVerticalStack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class VoicemodAfterRecordingFragment : Fragment() {

    private lateinit var example: VoicemodAfterRecordingAudioEngine
    lateinit var exportButton: SBButtonView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        exportButton = SBButtonView(
            context = requireContext(),
            title = "Export"
        ) { button: Button ->
            exportFile()
        }
        example = VoicemodAfterRecordingAudioEngine(requireContext())
        example.loadVoiceFilter("baby")
        val view = SBVerticalStack(requireContext()).addSBViews(listOf(
            SBSpinnerView(
                requireContext(),
                title = "Voice",
                0,
                voices.map { it }) { selectedVoice: String ->
                example.loadVoiceFilter(selectedVoice)
            },
            SBSwitchView(context = requireContext(), title = "Background Sounds" , initialState = example.voicemodNode.backgroundSoundsEnabled) { isChecked ->
                example.voicemodNode.backgroundSoundsEnabled = isChecked
            },
            SBSwitchView(
                context = requireContext(),
                title = "Bypass",
                initialState = example.voicemodNode.bypassEnabled
            ) { isChecked ->
                example.voicemodNode.bypassEnabled = isChecked
            },
            SBTextView(requireContext(), "Recorder"),
            SBHorizontalStack(requireContext())
                .addSBViews(
                    listOf(
                        SBButtonView(
                            context = requireContext(),
                            title = "Start"
                        ) { button: Button ->
                            example.record()
                        },
                        SBButtonView(
                            context = requireContext(),
                            title = "Stop"
                        ) { button: Button ->
                            example.stopRecord()
                        }
                    )
                ),
            SBTextView(requireContext(), "Playback"),
            SBHorizontalStack(requireContext())
                .addSBViews(
                    listOf(
                        SBButtonView(
                            context = requireContext(),
                            title = "Start"
                        ) { button: Button ->
                            example.play()
                        },
                        SBButtonView(
                            context = requireContext(),
                            title = "Stop"
                        ) { button: Button ->
                            example.stopPlayer()
                        }
                    )
                ),
            exportButton
        )).getView()
        return view.rootView
    }

    private fun exportFile() {
        example.stopAudioEngine()
        val filePath = example.renderMix()
        FileExporter.export(requireActivity(), filePath)
        example.startAudioEngine()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        example.stopAudioEngine()
        example.close()
    }
}