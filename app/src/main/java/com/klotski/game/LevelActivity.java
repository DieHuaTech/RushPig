package com.klotski.game;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

public class LevelActivity extends AppCompatActivity {

    public static final String EXTRA_PASS = "com.klotski.game.extra.PASS";
    public static final String EXTRA_PASS_NAME = "com.klotski.game.extra.PASS_NAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        initStep();
    }
    private void initStep() {
        TextView pass1_step = findViewById(R.id.pass1_step);
        TextView pass2_step = findViewById(R.id.pass2_step);
        TextView pass3_step = findViewById(R.id.pass3_step);
        TextView pass4_step = findViewById(R.id.pass4_step);
        TextView pass5_step = findViewById(R.id.pass5_step);
        TextView pass6_step = findViewById(R.id.pass6_step);

        SharedPreferences settings = getSharedPreferences("step", Context.MODE_PRIVATE);
        int step1 = settings.getInt("pass1", -1);
        int step2 = settings.getInt("pass2", -1);
        int step3 = settings.getInt("pass3", -1);
        int step4 = settings.getInt("pass4", -1);
        int step5 = settings.getInt("pass5", -1);
        int step6 = settings.getInt("pass6", -1);

        pass1_step.setText(step1==-1?"Unsuccessful ":Integer.toString(step1));
        pass2_step.setText(step2==-1?"Unsuccessful ":Integer.toString(step2));
        pass3_step.setText(step3==-1?"Unsuccessful ":Integer.toString(step3));
        pass4_step.setText(step4==-1?"Unsuccessful ":Integer.toString(step4));
        pass5_step.setText(step5==-1?"Unsuccessful ":Integer.toString(step5));
        pass6_step.setText(step6==-1?"Unsuccessful ":Integer.toString(step6));
    }

    public void toPass(View view) {
        int pass = 1;
        String pass_name = getResources().getString(R.string.pass_1);
        switch (view.getId()) {
            case R.id.pass_1:
                pass = 1;
                pass_name = getResources().getString(R.string.pass_1);
                break;
            case R.id.pass_2:
                pass = 2;
                pass_name = getResources().getString(R.string.pass_2);
                break;
            case R.id.pass_3:
                pass = 3;
                pass_name = getResources().getString(R.string.pass_3);
                break;
            case R.id.pass_4:
                pass = 4;
                pass_name = getResources().getString(R.string.pass_4);
                break;
            case R.id.pass_5:
                pass = 5;
                pass_name = getResources().getString(R.string.pass_5);
                break;
            case R.id.pass_6:
                pass = 6;
                pass_name = getResources().getString(R.string.pass_6);
                break;
        }
        Intent intent = new Intent(this, RushActivity.class);
        intent.putExtra(EXTRA_PASS, pass);
        intent.putExtra(EXTRA_PASS_NAME, pass_name);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initStep();
    }
}
