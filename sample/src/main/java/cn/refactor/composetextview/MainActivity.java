package cn.refactor.composetextview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import cn.refactor.library.ComposeTextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ComposeTextView tv1 = (ComposeTextView) findViewById(R.id.tv1);
        ComposeTextView tv2 = (ComposeTextView) findViewById(R.id.tv2);
        ComposeTextView tv3 = (ComposeTextView) findViewById(R.id.tv3);
        tv1.setText("10111222");
        tv2.setText("1011112222");
        tv3.setText("Prefix: #10111122223333# Suffix.", "#");
    }

}
