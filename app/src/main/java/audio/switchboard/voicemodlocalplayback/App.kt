package audio.switchboard.voicemodlocalplayback

import android.app.Application
import com.synervoz.switchboard.sdk.SwitchboardSDK
import com.synervoz.switchboardvoicemod.VoicemodExtension

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        SwitchboardSDK.initialize(this, "clientID", "clientSecret")
        VoicemodExtension.initialize(this, "voicemodClientKey")
    }
}