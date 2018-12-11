package cn.heima.myutilslibrary.wxapi;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import cn.heima.myutilslibrary.pay.Constant;
import cn.krvision.toolmodule.LogUtils;


public class WXPayEntryActivity extends AppCompatActivity implements IWXAPIEventHandler{

    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        api = WXAPIFactory.createWXAPI(this, Constant.WEIXIN_APPID);
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
    }

    @Override
    public void onResp(BaseResp baseResp) {
        if (baseResp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {

            LogUtils.e("onResp=",baseResp.errCode+" "+baseResp.errStr);
            switch (baseResp.errCode) {
                case BaseResp.ErrCode.ERR_OK:
                    sendBroadcastToOrderInfoActivity();
                    break;
                case BaseResp.ErrCode.ERR_COMM:
                    //-1 错误 可能的原因：签名错误、未注册APPID、项目设置APPID不正确、注册的APPID与设置的不匹配、其他异常等。
                    sendBroadcastToOrderInfoActivity();
                    break;
                case BaseResp.ErrCode.ERR_USER_CANCEL:
                    //-2 用户取消 无需处理。发生场景：用户不支付了，点击取消，返回APP。
                    sendBroadcastToOrderInfoActivity();
                    break;
                default:
                    break;
            }
            finish();
        }
    }

    private void sendBroadcastToOrderInfoActivity() {
        Intent intent = new Intent("com.krvision.pay.wechat");
        sendBroadcast(intent);
    }

}