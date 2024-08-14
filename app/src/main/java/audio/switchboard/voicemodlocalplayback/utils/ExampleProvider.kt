package audio.switchboard.voicemodlocalplayback.utils

import android.content.Context
import androidx.fragment.app.Fragment
import audio.switchboard.voicemodlocalplayback.playerwithvoicemod.PlayerWithVoicemodFragment
import audio.switchboard.voicemodlocalplayback.voicemodafterrecording.VoicemodAfterRecordingFragment
import com.synervoz.switchboard.sdk.SwitchboardSDK
import com.synervoz.switchboardrnnoise.RNNoiseExtension
import com.synervoz.switchboardvoicemod.VoicemodExtension

object ExampleProvider {
    fun initialize(context: Context) {
        SwitchboardSDK.initialize(context, switchboardClientID, switchboardClientSecret)
        VoicemodExtension.initialize(context, voicemodLicenseKey)
        RNNoiseExtension.initialize()
    }

    fun examples(): List<Example> {
        return listOf(
            Example("Local audio file, Voicemod", PlayerWithVoicemodFragment::class.java as Class<Fragment>),
            Example("Record, Voicemod, Playback, Export", VoicemodAfterRecordingFragment::class.java as Class<Fragment>),
        )
    }
}

data class Example (
    val title: String,
    var fragment: Class<Fragment>
)