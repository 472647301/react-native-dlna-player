// RNByronDLNAPackage.java

package byron.dlna;

import androidx.annotation.NonNull;

import java.util.Collections;
import java.util.List;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;

import byron.dlna.ijkplayer.RNByronVlcManager;

public class RNByronDLNAPackage implements ReactPackage {

    @NonNull
    @Override
    public List<NativeModule> createNativeModules(@NonNull ReactApplicationContext reactContext) {
        return Collections.<NativeModule>singletonList(
                new RNByronDLNAModule(reactContext)
        );
    }

    @NonNull
    @Override
    public List<ViewManager> createViewManagers(@NonNull ReactApplicationContext reactContext) {
        return Collections.<ViewManager>singletonList(
                new RNByronVlcManager()
        );
    }
}
