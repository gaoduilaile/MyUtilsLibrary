package cn.heima.myutilslibrary.reflect;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.heima.myutilslibrary.R;
import cn.krvision.toolmodule.reflect.MyClick;
import cn.krvision.toolmodule.reflect.MyInject;
import cn.krvision.toolmodule.reflect.MyViewUtils;
import cn.krvision.toolmodule.utils.LogUtils;

/**2019/1/7  GaoQiong
 *  反射
 *  */

public class ReflectActivity extends AppCompatActivity {
    @MyInject(R.id.textView1)
    @BindView(R.id.textView1)
    TextView textView1;
    @MyInject(R.id.textView2)
    @BindView(R.id.textView2)
    TextView textView2;
    @MyInject(R.id.textView3)
    @BindView(R.id.textView3)
    TextView textView3;

//    @MyInject(R.id.textView1)
//    private TextView TextView1;
//    @MyInject(R.id.textView2)
//    private TextView TextView2;
//    @MyInject(R.id.textView3)
//    private TextView TextView3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reflect);
        ButterKnife.bind(this);
        MyViewUtils.inject(this);

        textView1.setText("hehehe");
    }

    @MyClick({R.id.textView1, R.id.textView2})
    public void submit(View view) {

        LogUtils.e("1111111",((TextView) view).getText().toString()+"");
        Toast.makeText(this, ((TextView) view).getText(), Toast.LENGTH_SHORT).show();
    }

    @OnClick({R.id.textView1, R.id.textView2, R.id.textView3})
    public void onViewClicked(View view) {
        LogUtils.e("22222222",((TextView) view).getText().toString()+"");
        switch (view.getId()) {
            case R.id.textView1:
                break;
            case R.id.textView2:
                break;
            case R.id.textView3:
                break;
        }
    }
}
