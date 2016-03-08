package com.seastar.wasai.views.extendedcomponent;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.seastar.wasai.R;

/**
 * 杨腾
 * 升级对话框
 */
public class FindOrderDialog extends Dialog implements View.OnClickListener {
    private Button okBtn;
    private Context context;

    public FindOrderDialog(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.dialog_find_order);
        this.setCancelable(true);
        okBtn = (Button) findViewById(R.id.ok_button);
        okBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ok_button:
                Intent data = new Intent(Intent.ACTION_SENDTO);
                data.setData(Uri.parse("mailto:support@91wasai.com"));
                data.putExtra(Intent.EXTRA_SUBJECT, "订单申诉");
                data.putExtra(Intent.EXTRA_TEXT, "");
                context.startActivity(data);
                this.dismiss();
                break;
        }
    }
}