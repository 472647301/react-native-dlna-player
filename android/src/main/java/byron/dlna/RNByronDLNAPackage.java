// RNByronDLNAPackage.java

package byron.dlna;

import java.util.Arrays;
import java.util.List;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;

import byron.dlna.ijkplayer.RNByronVlcManager;
import byron.dlna.ijkplayer.RNByronVlcModule;

public class RNByronDLNAPackage implements ReactPackage {
    private RNByronVlcManager playerManager;

    @Override
    public List<NativeModule> createNativeModules(ReactApplicationContext reactContext) {
        if (playerManager == null) {
            playerManager = new RNByronVlcManager(reactContext);
        }
        return Arrays.<NativeModule>asList(
                new RNByronDLNAModule(reactContext),
                new RNByronVlcModule(reactContext, playerManager)
        );
    }

    @Override
    public List<ViewManager> createViewManagers(ReactApplicationContext reactContext) {
        if (playerManager == null) {
            playerManager = new RNByronVlcManager(reactContext);
        }
        return Arrays.<ViewManager>asList(
                playerManager
        );
    }
}
