package audio.switchboard.voicemodlocalplayback.playerwithvoicemod

import android.content.Context
import com.synervoz.switchboard.sdk.Codec
import com.synervoz.switchboard.sdk.audioengine.AudioEngine
import com.synervoz.switchboard.sdk.audiograph.AudioGraph
import com.synervoz.switchboard.sdk.audiographnodes.AudioPlayerNode
import com.synervoz.switchboardvoicemod.audiographnodes.VoicemodNode

class PlayerWithVoicemodAudioEngine(context: Context) {
    val audioGraph = AudioGraph()
    val playerNode = AudioPlayerNode()
    val voicemodNode = VoicemodNode()
    val audioEngine = AudioEngine(context)

    init {
        playerNode.isLoopingEnabled = true

        audioGraph.addNode(playerNode)
        audioGraph.addNode(voicemodNode)

        audioGraph.connect(playerNode, voicemodNode)
        audioGraph.connect(voicemodNode, audioGraph.outputNode)

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