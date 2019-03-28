package cn.signit.www.wesignsdkdemo;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;


import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    public  static  boolean isValidURL(String string) {
        Pattern httpPattern;
        httpPattern = Pattern.compile("^([hH][tT]{2}[pP]://|[hH][tT]{2}[pP][sS]://)(([A-Za-z0-9-~]+).)+([A-Za-z0-9-~\\/])+$");
        if (httpPattern.matcher(string).matches()) {
            return  true;
        } else {
            return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        final EditText identity_text = (EditText)findViewById(R.id.identity_url);
        final EditText signature_text = (EditText)findViewById(R.id.signature_url);

        Button identifyBtn = (Button) findViewById(R.id.button_identity);
        identifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Log.i("identity_btn", "点击实名认证按钮");
               if (!MainActivity.isValidURL(identity_text.getText().toString())) {
                   new AlertDialog.Builder(MainActivity.this)
                           .setTitle("错误")
                           .setMessage("请输入一个合法的链接")
                           .setPositiveButton("确定",
                                   new DialogInterface.OnClickListener() {
                                       @Override
                                       public void onClick(DialogInterface dialog,
                                                           int which) {
                                       }
                                   }).create()
                           .show();
               }
               else {
                   Log.i("url", identity_text.getText().toString());
                   Intent intent = new Intent();
                   intent.setClass(MainActivity.this, WebViewActivity.class);
                   intent.putExtra("url", identity_text.getText().toString());
                   startActivityForResult(intent, 3);
               }
            }

        });

        final Button signature_button = (Button) findViewById(R.id.button_signature);
        signature_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("signature_btn","点击实名认证按钮");
                if (!MainActivity.isValidURL(signature_text.getText().toString())) {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("错误")
                            .setMessage("请输入一个合法的链接")
                            .setPositiveButton("确定",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog,
                                                            int which) {
                                        }
                                    }).create()
                            .show();
                }
                else {
                    Log.i("url", signature_text.getText().toString());
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, WebViewActivity.class);
                    intent.putExtra("url", signature_text.getText().toString());
                    startActivityForResult(intent, 3);
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
