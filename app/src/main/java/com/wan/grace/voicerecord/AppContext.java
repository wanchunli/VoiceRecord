package com.wan.grace.voicerecord;
import android.app.Application;
import java.util.HashMap;

/**
 * Created by 开发部 on 2018/2/8.
 */

public class AppContext extends Application {

    private static AppContext sInstance;
    /**
     * 网络类型
     */
    private int netMobile;
    /**
     * 保存从config.properties文件中的参数配置
     */
    public static HashMap<String, String> mPropertiesMap = new HashMap<String, String>();

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
    }

    public static AppContext getInstance() {
        return sInstance;
    }

}
