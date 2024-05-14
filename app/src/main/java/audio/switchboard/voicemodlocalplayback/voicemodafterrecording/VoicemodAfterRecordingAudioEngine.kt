package audio.switchboard.voicemodlocalplayback.voicemodafterrecording

import android.content.Context
import com.synervoz.switchboard.sdk.Codec
import com.synervoz.switchboard.sdk.SwitchboardSDK
import com.synervoz.switchboard.sdk.audioengine.AudioEngine
import com.synervoz.switchboard.sdk.audiograph.AudioGraph
import com.synervoz.switchboard.sdk.audiograph.OfflineGraphRenderer
import com.synervoz.switchboard.sdk.audiographnodes.AudioPlayerNode
import com.synervoz.switchboard.sdk.audiographnodes.RecorderNode
import com.synervoz.switchboardvoicemod.audiographnodes.VoicemodNode

class VoicemodAfterRecordingAudioEngine(context: Context) {
    val audioGraph = AudioGraph()
    val audioPlayerNode = AudioPlayerNode()
    val recorderNode = RecorderNode()
    val voicemodNode = VoicemodNode()
    val offlineGraphRenderer = OfflineGraphRenderer()
    val audioEngine = AudioEngine(context)

    var audioFileFormat: Codec = Codec.MP3
    lateinit var rawRecordingFilePath: String
    private var mixedFilePath =
        SwitchboardSDK.getTemporaryDirectoryPath() + "mix." + audioFileFormat.fileExtension

    init {
        audioGraph.addNode(audioPlayerNode)
        audioGraph.addNode(recorderNode)
        audioGraph.addNode(voicemodNode)

        audioGraph.connect(audioGraph.inputNode, recorderNode)
        audioGraph.connect(audioPlayerNode, voicemodNode)
        audioGraph.connect(voicemodNode, audioGraph.outputNode)

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

        audioGraphToRender.connect(audioPlayerNode, voicemodNode)
        audioGraphToRender.connect(voicemodNode, audioGraphToRender.outputNode)

        val sampleRate = audioPlayerNode.getSourceSampleRate()
        audioPlayerNode.position = 0.0
        audioPlayerNode.play()
        offlineGraphRenderer.setSampleRate(sampleRate)
        offlineGraphRenderer.setMaxNumberOfSecondsToRender(audioPlayerNode.getDuration())
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