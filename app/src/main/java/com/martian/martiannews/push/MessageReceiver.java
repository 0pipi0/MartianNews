package com.martian.martiannews.push;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import com.tencent.android.tpush.XGPushBaseReceiver;
import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushRegisterResult;
import com.tencent.android.tpush.XGPushShowedResult;
import com.tencent.android.tpush.XGPushTextMessage;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author martian on 2017/9/26.
 */

public class MessageReceiver  extends XGPushBaseReceiver {
  public static final String LogTag = "TPushReceiver";

  private void show(Context context, String text) {
    Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
  }

  // 通知展示
  @Override
  public void onNotifactionShowedResult(Context context,
      XGPushShowedResult notifiShowedRlt) {
    if (context == null || notifiShowedRlt == null) {
      return;
    }
  }
  //反注册的回调
  @Override
  public void onUnregisterResult(Context context, int errorCode) {
    if (context == null) {
      return;
    }
    String text = "";
    if (errorCode == XGPushBaseReceiver.SUCCESS) {
      text = "反注册成功";
    } else {
      text = "反注册失败" + errorCode;
    }
    Log.d(LogTag, text);
    show(context, text);

  }
  //设置tag的回调
  @Override
  public void onSetTagResult(Context context, int errorCode, String tagName) {
    if (context == null) {
      return;
    }
    String text = "";
    if (errorCode == XGPushBaseReceiver.SUCCESS) {
      text = "\"" + tagName + "\"设置成功";
    } else {
      text = "\"" + tagName + "\"设置失败,错误码：" + errorCode;
    }
    Log.d(LogTag, text);
    show(context, text);

  }
  //删除tag的回调
  @Override
  public void onDeleteTagResult(Context context, int errorCode, String tagName) {
    if (context == null) {
      return;
    }
    String text = "";
    if (errorCode == XGPushBaseReceiver.SUCCESS) {
      text = "\"" + tagName + "\"删除成功";
    } else {
      text = "\"" + tagName + "\"删除失败,错误码：" + errorCode;
    }
    Log.d(LogTag, text);
    show(context, text);

  }

  // 通知点击回调 actionType=1为该消息被清除，actionType=0为该消息被点击
  @Override
  public void onNotifactionClickedResult(Context context,
      XGPushClickedResult message) {

    if (context == null || message == null) {
      return;
    }
    if (message.getActionType() == XGPushClickedResult.NOTIFACTION_CLICKED_TYPE) {
      // 通知在通知栏被点击啦。。。。。
      // APP自己处理点击的相关动作
      // 这个动作可以在activity的onResume也能监听，请看第3点相关内容
    } else if (message.getActionType() == XGPushClickedResult.NOTIFACTION_DELETED_TYPE) {
      // 通知被清除啦。。。。
      // APP自己处理通知被清除后的相关动作
    }
    //PushUtil.startActivity(message.getContent(),true);
  }

  //注册的回调
  @Override
  public void onRegisterResult(Context context, int errorCode,
      XGPushRegisterResult message) {
    // TODO Auto-generated method stub
    if (context == null || message == null) {
      return;
    }
    String text = "";
    if (errorCode == XGPushBaseReceiver.SUCCESS) {
      text = message + "注册成功";
      // 在这里拿token
      String token = message.getToken();
    } else {
      text = message + "注册失败，错误码：" + errorCode;
    }
    Log.d(LogTag, text);
    show(context, text);
  }

  // 消息透传的回调
  @Override
  public void onTextMessage(Context context, XGPushTextMessage message) {
    // TODO Auto-generated method stub
    String text = "收到消息:" + message.toString();

    // 获取自定义key-value
    String customContent = message.getCustomContent();
    if (customContent != null && customContent.length() != 0) {
      try {
        JSONObject obj = new JSONObject(customContent);
        // key1为前台配置的key
        if (!obj.isNull("key")) {
          String value = obj.getString("key");
          Log.d(LogTag, "get custom value:" + value);
        }
        // ...
      } catch (JSONException e) {
        e.printStackTrace();
      }
    }
    // APP自主处理消息的过程...
    Log.d(LogTag, text);
    show(context, text);
  }
}
