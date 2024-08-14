package audio.switchboard.voicemodlocalplayback.voicemodafterrecording

import android.content.Context
import com.synervoz.switchboard.sdk.Codec
import com.synervoz.switchboard.sdk.SwitchboardSDK
import com.synervoz.switchboard.sdk.audioengine.AudioEngine
import com.synervoz.switchboard.sdk.audioengine.PerformanceMode
import com.synervoz.switchboard.sdk.audiograph.AudioGraph
import com.synervoz.switchboard.sdk.audiograph.OfflineGraphRenderer
import com.synervoz.switchboard.sdk.audiographnodes.AudioPlayerNode
import com.synervoz.switchboard.sdk.audiographnodes.MonoToMultiChannelNode
import com.synervoz.switchboard.sdk.audiographnodes.MultiChannelToMonoNode
import com.synervoz.switchboard.sdk.audiographnodes.RecorderNode
import com.synervoz.switchboardrnnoise.audiographnodes.RNNoiseFilterNode
import com.synervoz.switchboardvoicemod.audiographnodes.VoicemodNode

class VoicemodAfterRecordingAudioEngine(context: Context) {
    val audioGraph = AudioGraph()
    val audioPlayerNode = AudioPlayerNode()
    val recorderNode = RecorderNode()
    val voicemodNode = VoicemodNode()
    val monoToMultiChannelNode = MonoToMultiChannelNode()
    val multiChannelToMonoNode = MultiChannelToMonoNode()
    val noiseFilterNode = RNNoiseFilterNode()
    val audioEngine = AudioEngine(context, performanceMode = PerformanceMode.LOW_LATENCY)

    var audioFileFormat: Codec = Codec.MP3
    lateinit var rawRecordingFilePath: String
    private var mixedFilePath =
        SwitchboardSDK.getTemporaryDirectoryPath() + "mix." + audioFileFormat.fileExtension

    var noiseFilterEnabled: Boolean
        get() = noiseFilterNode.isEnabled
        set(value) {
            noiseFilterNode.isEnabled = value
        }

    init {
        audioGraph.addNode(audioPlayerNode)
        audioGraph.addNode(recorderNode)
        audioGraph.addNode(voicemodNode)
        audioGraph.addNode(monoToMultiChannelNode)
        audioGraph.addNode(multiChannelToMonoNode)
        audioGraph.addNode(noiseFilterNode)

        audioGraph.connect(audioGraph.inputNode, recorderNode)
        audioGraph.connect(audioPlayerNode, multiChannelToMonoNode)
        audioGraph.connect(multiChannelToMonoNode, noiseFilterNode)
        audioGraph.connect(noiseFilterNode, voicemodNode)
        audioGraph.connect(voicemodNode, monoToMultiChannelNode)
        audioGraph.connect(monoToMultiChannelNode, audioGraph.outputNode)
        noiseFilterNode.isEnabled = false

        startAudioEngine()
    }

    fun record() {
        stopPlayer()
        audioEngine.microphoneEnabled = true
        recorderNode.start()
    }

    fun stopRecord() {
        audioEngine.microphoneEnabled = false
        rawRecordingFilePath =
            SwitchboardSDK.getTemporaryDirectoryPath() + "test_recording" + "." + audioFileFormat.fileExtension
        recorderNode.stop(rawRecordingFilePath, audioFileFormat)
        audioPlayerNode.load(rawRecordingFilePath, audioFileFormat)
    }

    fun play() {
        stopRecord()
        audioPlayerNode.play()
    }

    fun pause() {
        audioPlayerNode.pause()
    }

    fun stopPlayer() {
        audioPlayerNode.stop()
    }

    fun stopAudioEngine() {
        audioEngine.stop()
    }

    fun startAudioEngine() {
        audioEngine.start(audioGraph)
    }

    fun loadVoiceFilter(voiceFilter: String) {
        voicemodNode.loadVoice(voiceFilter)
    }

    fun renderMix(): String {
        val audioGraphToRender = AudioGraph()
        audioGraphToRender.addNode(audioPlayerNode)
        audioGraphToRender.addNode(voicemodNode)
        audioGraphToRender.addNode(monoToMultiChannelNode)
        audioGraphToRender.addNode(multiChannelToMonoNode)
        audioGraphToRender.addNode(noiseFilterNode)

        audioGraphToRender.connect(audioPlayerNode, multiChannelToMonoNode)
        audioGraphToRender.connect(multiChannelToMonoNode, noiseFilterNode)
        audioGraphToRender.connect(noiseFilterNode, voicemodNode)
        audioGraphToRender.connect(voicemodNode, monoToMultiChannelNode)
        audioGraphToRender.connect(monoToMultiChannelNode, audioGraphToRender.outputNode)

        val sampleRate = audioPlayerNode.getSourceSampleRate()
        audioPlayerNode.position = 0.0
        audioPlayerNode.play()
        val offlineGraphRenderer = OfflineGraphRenderer()
        offlineGraphRenderer.setSampleRate(sampleRate)
        // The duration of additional silence (in seconds) added to the end of the audio playback.
        // This padding ensures that the tail of any applied audio effects has sufficient time to decay naturally,
        // preventing abrupt cutoffs and ensuring a smooth and natural fade-out of the effects.
        val effectTailPaddingSeconds = 1.0
        offlineGraphRenderer.setMaxNumberOfSecondsToRender(audioPlayerNode.getDuration() + effectTailPaddingSeconds)
        offlineGraphRenderer.processGraph(audioGraphToRender, mixedFilePath, audioFileFormat)
        audioPlayerNode.stop()

        return mixedFilePath
    }

    fun close() {
        audioEngine.close()
        audioGraph.close()
        voicemodNode.close()
        audioPlayerNode.close()
        recorderNode.close()
    }
}