package audio.switchboard.voicemodlocalplayback.voicemodafterrecording

import android.content.Context
import com.synervoz.switchboard.sdk.Codec
import com.synervoz.switchboard.sdk.SwitchboardSDK
import com.synervoz.switchboard.sdk.audioengine.AudioEngine
import com.synervoz.switchboard.sdk.audiograph.AudioGraph
import com.synervoz.switchboard.sdk.audiographnodes.AudioPlayerNode
import com.synervoz.switchboard.sdk.audiographnodes.RecorderNode
import com.synervoz.switchboardvoicemod.audiographnodes.VoicemodNode

class VoicemodAfterRecordingAudioEngine(context: Context) {
    val audioGraph = AudioGraph()
    val audioPlayerNode = AudioPlayerNode()
    val recorderNode = RecorderNode()
    val voicemodNode = VoicemodNode()
    val audioEngine = AudioEngine(context)

    var currentFormat: Codec = Codec.WAV
    lateinit var recordingFilePath : String

    init {
        audioGraph.addNode(audioPlayerNode)
        audioGraph.addNode(recorderNode)
        audioGraph.addNode(voicemodNode)

        audioGraph.connect(audioGraph.inputNode, recorderNode)
        audioGraph.connect(audioPlayerNode, voicemodNode)
        audioGraph.connect(voicemodNode, audioGraph.outputNode)

        audioEngine.start(audioGraph)
    }

    fun record() {
        stopPlayer()
        audioEngine.microphoneEnabled = true
        recorderNode.start()
    }

    fun stopRecord() {
        audioEngine.microphoneEnabled = false
        recordingFilePath = SwitchboardSDK.getTemporaryDirectoryPath() +  "test_recording"+ "." + currentFormat.fileExtension
        recorderNode.stop(recordingFilePath, currentFormat)
        audioPlayerNode.load(recordingFilePath, currentFormat)
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

    fun stop() {
        audioEngine.stop()
    }

    fun close() {
        audioEngine.close()
        audioGraph.close()
        voicemodNode.close()
        audioPlayerNode.close()
        recorderNode.close()
    }
}