package src.main.java.cn.udesk.voice;import android.content.Context;import android.media.AudioManager;import android.media.MediaPlayer;import android.media.MediaPlayer.OnCompletionListener;import android.media.MediaPlayer.OnErrorListener;import android.media.MediaPlayer.OnPreparedListener;import android.text.TextUtils;import android.util.Log;import java.io.IOException;import cn.udesk.R;import cn.udesk.UdeskUtil;import cn.udesk.activity.UdeskChatActivity;import udesk.core.UdeskConst;import udesk.core.model.MessageInfo;import udesk.core.utils.UdeskUtils;public class RecordPlay implements RecordFilePlay, OnCompletionListener,        OnPreparedListener, OnErrorListener {    private volatile boolean isPlaying = false;    private Context mContent;    int mPosition;    String mMediaFilePath;    RecordPlayCallback mCallbak;    MediaPlayer mediaPlayer;    MessageInfo mCurrentMessage;    public RecordPlay(Context content) {        this.mContent = content;    }    @Override    public synchronized void click(MessageInfo message,                                   RecordPlayCallback callback) {        try {            mMediaFilePath=getLocalPath(message);            if (UdeskUtils.isNetworkConnected(mContent.getApplicationContext())){                if (TextUtils.isEmpty(mMediaFilePath)){                    mMediaFilePath = message.getMsgContent();                }                dealClick(message, callback);            }else {                if (TextUtils.isEmpty(mMediaFilePath)){                    UdeskUtils.showToast(mContent.getApplicationContext(),                            mContent.getResources().getString(R.string.udesk_has_wrong_net));                }else {                    dealClick(message, callback);                }            }        } catch (Exception e) {            e.printStackTrace();        }    }    private void dealClick(MessageInfo message, RecordPlayCallback callback) {        if (mediaPlayer == null) {            init();        }        if (mCurrentMessage != message) {            // 停止旧文件的播放            if (isPlaying) {                recycleRes();            }            // 对旧数据 进行回调            if (mCurrentMessage != null) {                mCurrentMessage.isPlaying = false;                if (mCallbak != null) {                    mCallbak.onPlayEnd(mCurrentMessage);                    mCurrentMessage = null;                }            }            // 新旧数据更新            mCallbak = callback;            mCurrentMessage = message;            try {                init();                startPlayer(mMediaFilePath);            } catch (Exception e) {                e.printStackTrace();            }        } else {            toggle();        }    }    private String getLocalPath(MessageInfo message) {        if (!TextUtils.isEmpty(message.getLocalPath()) && UdeskUtils.isExitFileByPath(message.getLocalPath())) {           return mMediaFilePath = message.getLocalPath();        } else if (UdeskUtils.fileIsExitByUrl(mContent, UdeskConst.FileAudio, message.getMsgContent())                && UdeskUtils.getFileSize(UdeskUtils.getFileByUrl(mContent, UdeskConst.FileAudio, message.getMsgContent())) > 0) {            return mMediaFilePath = UdeskUtils.getPathByUrl(mContent, UdeskConst.FileAudio, message.getMsgContent());        } else {            return "";        }    }    private void startPlayer(final String mediaFilePath) throws IOException {        try {            mPosition = 0;            mediaPlayer.reset();            mediaPlayer.setDataSource(mediaFilePath);            mediaPlayer.setLooping(false);            mediaPlayer.prepareAsync();// player只有调用了onpraparre（）方法后才会调用onstart（）        } catch (IOException e) {            e.printStackTrace();        } catch (IllegalArgumentException e) {            e.printStackTrace();        } catch (SecurityException e) {            e.printStackTrace();        } catch (IllegalStateException e) {            e.printStackTrace();        }    }    @Override    public String getMediaPath() {        return mMediaFilePath;    }    @Override    public void recycleRes() {        try {            if (mediaPlayer != null) {                if (isPlaying) {                    try {                        mediaPlayer.stop();                    } catch (Exception e) {                        e.printStackTrace();                    }                }                try {                    mediaPlayer.release();                } catch (Exception e) {                    e.printStackTrace();                }            }            isPlaying = false;            if (mCurrentMessage != null) {                mCurrentMessage.isPlaying = false;            }        } catch (Exception e) {            e.printStackTrace();        }    }    @Override    public void recycleCallback() {        try {            recycleRes();            mCurrentMessage = null;            mCallbak = null;        } catch (Exception e) {            e.printStackTrace();        }    }    @Override    public synchronized void toggle() {        try {            if (isPlaying) {                isPlaying = false;                try {                    mPosition = mediaPlayer.getCurrentPosition();                } catch (Exception e) {                    e.printStackTrace();                }                try {                    mediaPlayer.pause();                } catch (Exception e) {                    e.printStackTrace();                }                if (mCallbak != null) {                    mCallbak.onPlayPause(mCurrentMessage);                    mCallbak = null;                }                if (mCurrentMessage != null) {                    mCurrentMessage.isPlaying = false;                }            } else {                init();                try {                    startPlayer(mMediaFilePath);                } catch (Exception e) {                    e.printStackTrace();                }            }        } catch (Exception e) {            e.printStackTrace();        }    }    @Override    public synchronized void onCompletion(MediaPlayer mp) {        mPosition = 0;        try {            mp.stop();            isPlaying = false;            if (mCurrentMessage != null) {                mCurrentMessage.isPlaying = false;            }            if (mCallbak != null) {                mCallbak.endAnimation();                mCallbak.onPlayEnd(mCurrentMessage);                mCurrentMessage = null;                mCallbak = null;            }        } catch (Exception e) {            e.printStackTrace();        }    }    @Override    public synchronized void onPrepared(MediaPlayer arg0) {        try {            mediaPlayer.start();            isPlaying = true;            if (mCallbak != null) {                mCallbak.onPlayStart(mCurrentMessage);            }            if (mCurrentMessage != null) {                mCurrentMessage.isPlaying = true;            }        } catch (IllegalStateException e) {            e.printStackTrace();        }    }    private void init() {        try {            mediaPlayer = new MediaPlayer();            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);            mediaPlayer.setOnCompletionListener(this);            mediaPlayer.setOnPreparedListener(this);            mediaPlayer.setOnErrorListener(this);        } catch (Exception e) {            e.printStackTrace();        }    }    @Override    public synchronized boolean onError(MediaPlayer arg0, int arg1, int arg2) {        try {            arg0.reset();            isPlaying = false;            if (mCurrentMessage != null) {                mCurrentMessage.isPlaying = false;            }        } catch (Exception e) {            e.printStackTrace();        }        return true;    }    @Override    public synchronized MessageInfo getPlayAduioMessage() {        try {            if (mCurrentMessage != null) {                return mCurrentMessage;            }        } catch (Exception e) {            e.printStackTrace();        }        return null;    }}