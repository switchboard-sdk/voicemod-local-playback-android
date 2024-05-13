package audio.switchboard.voicemodlocalplayback.utils

import android.content.Context
import androidx.fragment.app.Fragment
import audio.switchboard.voicemodlocalplayback.playerwithvoicemod.PlayerWithVoicemodFragment
import com.synervoz.switchboard.sdk.SwitchboardSDK
import com.synervoz.switchboardvoicemod.VoicemodExtension

object ExampleProvider {
    fun initialize(context: Context) {
        SwitchboardSDK.initialize(context, switchboardClientID, switchboardClientSecret)
        VoicemodExtension.initialize(context, voicemodLicenseKey)
    }

    fun examples(): List<Example> {
        return listOf(
            Example("Player with Voicemod", PlayerWithVoicemodFragment::class.java as Class<Fragment>),
        )
    }
}

data class Example (
    val title: String,
    var fragment: Class<Fragment>
)