package audio.switchboard.voicemodlocalplayback.playerwithvoicemod

import android.content.Context
import com.synervoz.switchboard.sdk.Codec
import com.synervoz.switchboard.sdk.audioengine.AudioEngine
import com.synervoz.switchboard.sdk.audioengine.PerformanceMode
import com.synervoz.switchboard.sdk.audiograph.AudioGraph
import com.synervoz.switchboard.sdk.audiographnodes.AudioPlayerNode
import com.synervoz.switchboard.sdk.audiographnodes.MonoToMultiChannelNode
import com.synervoz.switchboard.sdk.audiographnodes.MultiChannelToMonoNode
import com.synervoz.switchboardvoicemod.audiographnodes.VoicemodNode

class PlayerWithVoicemodAudioEngine(context: Context) {
    val audioGraph = AudioGraph()
    val playerNode = AudioPlayerNode()
    val voicemodNode = VoicemodNode()
    val monoToMultiChannelNode = MonoToMultiChannelNode()
    val multiChannelToMonoNode = MultiChannelToMonoNode()
    val audioEngine = AudioEngine(context, performanceMode = PerformanceMode.LOW_LATENCY)

    init {
        playerNode.isLoopingEnabled = true

        audioGraph.addNode(playerNode)
        audioGraph.addNode(voicemodNode)
        audioGraph.addNode(monoToMultiChannelNode)
        audioGraph.addNode(multiChannelToMonoNode)

        audioGraph.connect(playerNode, multiChannelToMonoNode)
        audioGraph.connect(multiChannelToMonoNode, voicemodNode)
        audioGraph.connect(voicemodNode, monoToMultiChannelNode)
        audioGraph.connect(monoToMultiChannelNode, audioGraph.outputNode)

        audioEngine.start(audioGraph)
    }

    fun startEngine() {
        audioEngine.start(audioGraph)
    }

    fun stopEngine() {
        audioEngine.stop()
    }

    fun close() {
        audioEngine.close()
        audioGraph.close()
        voicemodNode.close()
        playerNode.close()
    }

    fun play() {
        playerNode.play()
    }

    fun stop() {
        playerNode.stop()
    }

    fun isPlayingAudio() = playerNode.isPlaying

    fun load(fileByteArray: ByteArray, codec: Codec) {
        playerNode.load(fileByteArray, codec)
    }
}