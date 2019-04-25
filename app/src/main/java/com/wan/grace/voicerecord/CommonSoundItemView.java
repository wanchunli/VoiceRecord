package com.wan.grace.voicerecord;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
public class CommonSoundItemView extends RelativeLayout implements AudioPlaybackManager.OnPlayingListener {

    /**
     * rootview
     */
    protected RelativeLayout layoutSoundItem;
    /*
       播放音频的父布局
     */
    protected LinearLayout voicePlayLayout;
    /*
       录制音频的父布局
     */
    protected LinearLayout voiceRecordLayout;
    /*
       录制音频结束按钮
     */
    protected ImageView ivVoiceEnd;
    /*
       录制音频动画的imageview
     */
    protected ImageView ivSoundRecord;
    protected WaveView recordWaveView;
    /*
      录制音频计时器
     */
    protected Chronometer mChronometer;
    /*
      录制音频动画
     */
    protected AnimationDrawable animationRecord;


    /*
    播放音频动画的imageview
     */
    protected ImageView ivSoundItemHorn;
    protected WaveView soundWaveView;
    /*
    播放音频的动画
     */
    protected AnimationDrawable animationDrawable;
    /*
    音频时长
     */
    protected TextView tvSoundDuration;
    /*
    删除音频的imageview
     */
    protected ImageView ivVoiceDelete;
    /*
    开始说话
     */
    protected TextView voiceRecordTv;
    /*
    音频播放的imageview
     */
    protected ImageView ivVoicePlay;
    /*
    音频的实体
     */
    protected AudioBean audioInfo;

    protected Context context;
    private String mFileName = null;
    private String mFilePath = null;
    private MediaRecorder mRecorder = null;
    private long mStartingTimeMillis = 0;
    private Timer timer;
    private TimerTask task;
    /*
    删除音频开放接口
     */
    private OnDeleteClickListener onDeleteClickListener;
    /*
    音频录制开放接口
     */
    private OnRecordClickListener onRecordClickListener;

    public CommonSoundItemView(Context context) {
        super(context);
        initMediaRecorder();
        initView(context);
    }

    public CommonSoundItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initMediaRecorder();
        initView(context);
    }

    public CommonSoundItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initMediaRecorder();
        initView(context);
    }

    public void initMediaRecorder() {
        setFileNameAndPath();

        mRecorder = new MediaRecorder();
        //设置用于录制的音源
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        //设置在录制过程中产生的输出文件的格式
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        //设置输出文件的路径
        mRecorder.setOutputFile(mFilePath);
        //设置audio的编码格式
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        //设置录制的音频通道数
        mRecorder.setAudioChannels(1);
        //设置录制的音频采样率（高质量）
        mRecorder.setAudioSamplingRate(44100);
        //设置录制的音频编码比特率
        mRecorder.setAudioEncodingBitRate(192000);
    }

    private void initView(Context context) {
        this.context = context;
        LayoutInflater inflater = LayoutInflater.from(context);
        View rootView = inflater.inflate(R.layout.sound_item, this);

        layoutSoundItem = (RelativeLayout) findViewById(R.id.layout_sound_item);
        voicePlayLayout = (LinearLayout) findViewById(R.id.voice_play_layout);
        voiceRecordLayout = (LinearLayout) findViewById(R.id.voice_record_layout);
        ivVoiceEnd = (ImageView) findViewById(R.id.iv_voice_end);
        ivSoundRecord = (ImageView) findViewById(R.id.iv_sound_record);
        recordWaveView = findViewById(R.id.siri_record_view);
        mChronometer = (Chronometer) findViewById(R.id.chronometer);

        animationRecord = (AnimationDrawable) ivSoundRecord.getDrawable();
        animationRecord.setOneShot(false);
        ivSoundItemHorn = (ImageView) findViewById(R.id.iv_sound_horn);
        soundWaveView = findViewById(R.id.sound_wave_view);
        animationDrawable = (AnimationDrawable) ivSoundItemHorn.getDrawable();
        animationDrawable.setOneShot(false);

        ivVoicePlay = findViewById(R.id.iv_sound_play);
        tvSoundDuration = (TextView) findViewById(R.id.tv_sound_duration);
        ivVoiceDelete = (ImageView) findViewById(R.id.iv_voice_delete);
        voiceRecordTv = (TextView) findViewById(R.id.voice_record_tv);
        ivVoicePlay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AudioPlaybackManager.getInstance().isPlaying()) {
                    ivVoicePlay.setImageResource(R.drawable.ic_voice_play);
                    AudioPlaybackManager.getInstance().pauseAudio();
                    soundWaveView.stopAnimation();
                } else {
                    ivVoicePlay.setImageResource(R.drawable.ic_voice_pause);
                    playSound();
                    soundWaveView.startAnimation();
                }
            }
        });
        voiceRecordTv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getOnRecordClickListener() != null) {
                    getOnRecordClickListener().onRecordStart(view);
                }
                voiceRecordTv.setVisibility(View.GONE);
                voicePlayLayout.setVisibility(View.GONE);
                voiceRecordLayout.setVisibility(View.VISIBLE);
                animationRecord.start();
                startRecording();
                recordWaveView.startAnimation();
            }
        });
        ivVoiceEnd.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getOnRecordClickListener() != null) {
                    getOnRecordClickListener().onRecordEnd(view);
                }
                animationRecord.stop();
                stopRecording();
                recordWaveView.stopAnimation();
            }
        });
        ivVoiceDelete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getOnRecordClickListener() != null) {
                    getOnRecordClickListener().onRecordDelete(view);
                }
                initRecord();
            }
        });
        audioDBChanged();
    }

    public void initRecord() {
        voiceRecordTv.setVisibility(View.VISIBLE);
        voicePlayLayout.setVisibility(View.GONE);
        voiceRecordLayout.setVisibility(View.GONE);
        animationDrawable.stop();
        animationRecord.stop();
        AudioPlaybackManager.getInstance().stopAudio();

    }

    public void startTimeRecord() {
        //start Chronometer
        mChronometer.setBase(SystemClock.elapsedRealtime());
        mChronometer.start();
        mChronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {

            }
        });
    }

    public void stopTimeRecord() {
        mChronometer.stop();
        mChronometer.setBase(SystemClock.elapsedRealtime());
        voiceRecordTv.setVisibility(View.GONE);
        voicePlayLayout.setVisibility(View.VISIBLE);
        voiceRecordLayout.setVisibility(View.GONE);
    }

    public void setSoundData(AudioBean audioInfo) {
        if (audioInfo == null) {
            return;
        }
        voicePlayLayout.setVisibility(View.VISIBLE);
        voiceRecordLayout.setVisibility(View.GONE);
        voiceRecordTv.setVisibility(View.GONE);
        this.audioInfo = audioInfo;
        this.tvSoundDuration.setText(formatTime((int) this.audioInfo.getDuration()));
        onComplete();
    }

    public static String formatTime(int recTime) {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(recTime);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(recTime)
                - TimeUnit.MINUTES.toSeconds(minutes);
        return String.format("%02d:%02d", minutes, seconds);
    }

    public void setHornDrawable(AnimationDrawable animationDrawable) {
        ivSoundItemHorn.setImageDrawable(animationDrawable);
        this.animationDrawable = animationDrawable;
    }

    public void setItemBackground(Drawable drawable) {
        layoutSoundItem.setBackgroundDrawable(drawable);
    }

    public String getSoundUrl() {
        return audioInfo.getUrl();
    }

    public AudioBean getAudioInfo() {
        return audioInfo;
    }

    public long getSoundDuration() {
        return audioInfo.getDuration();
    }

    public boolean hasData() {
        return audioInfo != null && !TextUtils.isEmpty(audioInfo.getUrl());
    }

    public void clearData() {
        this.audioInfo = null;
        AudioPlaybackManager.getInstance().stopAudio();
    }

    protected void playSound() {
        if (audioInfo != null && !TextUtils.isEmpty(audioInfo.getUrl())) {
            AudioPlaybackManager.getInstance().playAudio(audioInfo.getUrl(), this);
        }
    }

    @Override
    public void onStart() {
        resetDrawable();
        animationDrawable.start();
        invalidate();
    }

    @Override
    public void onStop() {
        animationDrawable.stop();
        resetDrawable();
        invalidate();
        soundWaveView.stopAnimation();
    }

    @Override
    public void onComplete() {
        animationDrawable.stop();
        soundWaveView.stopAnimation();
        ivVoicePlay.setImageResource(R.drawable.ic_voice_play);
        resetDrawable();
        tvSoundDuration.setText(formatTime((int) audioInfo.getDuration()) + "/" + formatTime((int) audioInfo.getDuration()));
        invalidate();
    }

    @Override
    public void onUpdate(int mCurrentPosition) {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(mCurrentPosition);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(mCurrentPosition)
                - TimeUnit.MINUTES.toSeconds(minutes) + 1;
        tvSoundDuration.setText(String.format("%02d:%02d", minutes, seconds) + "/" + formatTime((int) audioInfo.getDuration()));
    }

    protected void resetDrawable() {
        ivSoundItemHorn.clearAnimation();
        animationDrawable = (AnimationDrawable) context.getResources().getDrawable(
                R.drawable.ar_sound_play_animation);
        ivSoundItemHorn.setImageDrawable(animationDrawable);
        animationDrawable.stop();
        animationDrawable.setOneShot(false);
    }

    public interface OnDeleteClickListener {
        void onDeleteClickListener(View view);
    }

    public void setOnDeleteClickListener(OnDeleteClickListener onDeleteClickListener) {
        this.onDeleteClickListener = onDeleteClickListener;
    }

    public OnDeleteClickListener getOnDeleteClickListener() {
        return onDeleteClickListener;
    }

    /**
     * 控件全部的点击事件开放接口
     */
    public interface OnRecordClickListener {
        void onRecordStart(View view);

        void onRecordEnd(View view);

        void onRecordDelete(View view);
    }

    public void setOnRecordClickListener(OnRecordClickListener onRecordClickListener) {
        this.onRecordClickListener = onRecordClickListener;
    }

    public OnRecordClickListener getOnRecordClickListener() {
        return onRecordClickListener;
    }

    public void startRecording() {
        initMediaRecorder();
        try {
            //开始
            mRecorder.prepare();
            mRecorder.start();
            mStartingTimeMillis = System.currentTimeMillis();
        } catch (IOException e) {
        }
    }

    public void setFileNameAndPath() {
        int count = 0;
        File f;
        mFileName = System.currentTimeMillis() + ".voice";
        File audioDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +
                File.separator + "wan" + File.separator + "voices" + File.separator);
        if (!audioDir.exists()) {
            audioDir.mkdirs();
        }
        mFilePath = audioDir.getPath() + "/" + mFileName;

        f = new File(mFilePath);
    }

    public void stopRecording() {
        try {
            //结束
            mRecorder.stop();
        } catch (IllegalStateException e) {
            // TODO 如果当前java状态和jni里面的状态不一致，
            //e.printStackTrace();
            mRecorder = null;
            mRecorder = new MediaRecorder();
        }
        mRecorder.release();
        mRecorder = null;
        Toast.makeText(context, "录制完成" + " " + mFilePath, Toast.LENGTH_LONG).show();
        AudioBean audioBean = new AudioBean();
        audioBean.setUrl(mFilePath);
        int duration = AudioPlaybackManager.getInstance().getDuration(mFilePath);
        audioBean.setDuration(duration);
        setSoundData(audioBean);
    }

    /**
     * 获取录音时的环境音量来动态改变波形图的振幅
     */
    private void audioDBChanged() {
        timer = new Timer();
        task = new TimerTask() {                                                                                              //设置线程抽象类中的run（），这里更新value的值
            @Override
            //把value的值放到用于线程之间交流数据的Handler的message里
            public void run() {
                float level = 5;
                if (mRecorder != null) {
                    int db = mRecorder.getMaxAmplitude() / 600;
                    level = (db / 15) > 3 ? 3 : (db / 15);
                }
                Message message = mHandler.obtainMessage();
                message.obj = level;
                mHandler.sendMessage(message);                                                 //Handler类发出信息
            }
        };
        timer.schedule(task, 100);
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            float level = (float) msg.obj;
//            siriRecordWaveView.setWaveHeight(dipToPx(context, 5 + level * 5));
//            audioDBChanged();
        }
    };

    public static float dipToPx(Context context, float dip) {
        float density = context.getResources().getDisplayMetrics().density;
        return dip * density + 0.5F;
    }
}
