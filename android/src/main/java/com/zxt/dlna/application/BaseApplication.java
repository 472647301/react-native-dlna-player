package com.zxt.dlna.application;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;

import org.fourthline.cling.android.AndroidUpnpService;
import org.fourthline.cling.support.model.DIDLContent;

import com.zxt.dlna.dmp.ContentItem;
import com.zxt.dlna.dmp.DeviceItem;

import android.app.Application;
import android.content.Context;

public class BaseApplication extends Application {

	public static DeviceItem deviceItem;

	public DIDLContent didl;

	public static DeviceItem dmrDeviceItem;
	
	public static boolean isLocalDmr = true;

	public ArrayList<ContentItem> listMusic;

	public ArrayList<ContentItem> listPhoto;

	public ArrayList<ContentItem> listPlayMusic = new ArrayList();

	public ArrayList<ContentItem> listVideo;

	public ArrayList<ContentItem> listcontent;

	public HashMap<String, ArrayList<ContentItem>> map;

	// public MediaUtils mediaUtils;

	public int position;

	public static AndroidUpnpService upnpService;

	public static Context mContext;

	private static InetAddress inetAddress;

	private static String hostAddress;

	private static String hostName;

	@Override
	public void onCreate() {
		super.onCreate();
		mContext = getApplicationContext();
	}

	public static Context getContext() {
		return mContext;
	}

	public static void setLocalIpAddress(InetAddress inetAddr) {
		inetAddress = inetAddr;

	}

	public static InetAddress getLocalIpAddress() {
		return inetAddress;
	}

	public static String getHostAddress() {
		return hostAddress;
	}

	public static void setHostAddress(String hostAddress) {
		BaseApplication.hostAddress = hostAddress;
	}

	public static String getHostName() {
		return hostName;
	}

	public static void setHostName(String hostName) {
		BaseApplication.hostName = hostName;
	}
	
}
