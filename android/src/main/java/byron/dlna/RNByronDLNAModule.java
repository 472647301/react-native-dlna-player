// RNByronDLNAModule.java

package byron.dlna;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.zxt.dlna.application.BaseApplication;
import com.zxt.dlna.dmr.ZxtMediaRenderer;
import com.zxt.dlna.dms.MediaServer;

import org.fourthline.cling.android.AndroidUpnpService;
import org.fourthline.cling.android.AndroidUpnpServiceImpl;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class RNByronDLNAModule extends ReactContextBaseJavaModule {

    private final ReactApplicationContext reactContext;
    private AndroidUpnpService upnpService;
    private MediaServer mediaServer;
    private ZxtMediaRenderer mediaRenderer;
    public String friendlyName = "";

    private final ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            upnpService = (AndroidUpnpService) service;
            BaseApplication.upnpService = upnpService;
            Log.v("RNByronDLNAModule", "upnpService start");
            try {
                mediaServer = new MediaServer(reactContext, friendlyName);
                upnpService.getRegistry().addDevice(mediaServer.getDevice());
                mediaRenderer = new ZxtMediaRenderer(1, reactContext, friendlyName);
                upnpService.getRegistry().addDevice(mediaRenderer.getDevice());
                Log.v("RNByronDLNAModule", "start media device success");
            } catch (Exception ex) {
                Log.e("RNByronDLNAModule", "start media device failed", ex);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            upnpService = null;
            mediaServer = null;
            mediaRenderer = null;
            Log.v("RNByronDLNAModule", "upnpService close");
        }
    };

    public RNByronDLNAModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
        EventBus.getDefault().register(this);
        fetchInetAddress();
    }

    @NonNull
    @Override
    public String getName() {
        return "RNByronDLNA";
    }

    @ReactMethod
    public void startService(String name) {
        friendlyName = name;
        Intent intent = new Intent(reactContext, AndroidUpnpServiceImpl.class);
        reactContext.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        reactContext.startService(intent);
    }

    @ReactMethod
    public void closeService() {
        Intent intent = new Intent(reactContext, AndroidUpnpServiceImpl.class);
        reactContext.stopService(intent);
    }

    @SuppressWarnings("UnusedDeclaration")
    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    public void onNativeAsync(NativeAsyncEvent event) {
        WritableMap params = Arguments.createMap();
        params.putString("url", event.url);
        params.putString("title", event.title);
        params.putString("type", event.type);
        reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit("dlna-player", params);
    }

    private void fetchInetAddress() {
        new Thread(new Runnable() {
            @SuppressLint("DefaultLocale")
            @Override
            public void run() {
                WifiManager wifiManager = (WifiManager) reactContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                int ipAddress = wifiInfo.getIpAddress();
                InetAddress inetAddress;
                try {
                    inetAddress = InetAddress.getByName(String.format("%d.%d.%d.%d",
                            (ipAddress & 0xff), (ipAddress >> 8 & 0xff), (ipAddress >> 16 & 0xff),
                            (ipAddress >> 24 & 0xff)));
                    BaseApplication.setLocalIpAddress(inetAddress);
                    BaseApplication.setHostName(inetAddress.getHostName());
                    BaseApplication.setHostAddress(inetAddress.getHostAddress());
                } catch (UnknownHostException e) {
                    Log.e("RNByronDLNAModule", "inetAddress failed", e);
                }
            }
        }).start();
    }
}
