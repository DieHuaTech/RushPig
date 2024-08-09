package com.klotski.game;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

public class RushActivity extends AppCompatActivity {

    private int cur_layout[][];
    private View.OnTouchListener m_lisetner;
    private int pass = 1;
    private int step = 0;
    private TextView mPassName;
    private TextView mStep;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mPassName = findViewById(R.id.game_pass);
        mStep = findViewById(R.id.game_step);
        Intent intent = getIntent();
        pass = intent.getIntExtra(LevelActivity.EXTRA_PASS, 1);
        String pass_name = intent.getStringExtra(LevelActivity.EXTRA_PASS_NAME);
        mPassName.setText(pass_name);
        mStep.setText(Integer.toString(step));
        initPass();
        initLisentner();
    }

    private void finishCheck() {
        if (cur_layout[4][1] == 2 && cur_layout[4][2] == 2) {
            SharedPreferences settings = getSharedPreferences("step", Context.MODE_PRIVATE);
            int store_step = settings.getInt("pass"+pass, -1);
            String title = "Success";
            if (store_step == -1 || step < store_step) {
                SharedPreferences.Editor editor = settings.edit();
                editor.putInt("pass"+pass, step);
                editor.apply();
                title = "New Record";
            }
            AlertDialog dialog=new AlertDialog.Builder(RushActivity.this)
                    .setTitle(title)
                    .setMessage("Total " + step + " Steps")
                    .setCancelable(false)
                    .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .show();
        }
    }

    private boolean moveCheck(int figure, int direction) {
        int[] position = new int[4];
        boolean get_strat = false;
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 4; j++) {
                if (cur_layout[i][j] == figure) {
                    if (!get_strat) {
                        position[0] = i;
                        position[1] = j;
                        get_strat = true;
                    }
                    position[2] = i;
                    position[3] = j;
                }
            }
        }
        for (int i = position[0]; i <= position[2]; i++) {
            for (int j = position[1]; j <= position[3]; j++) {
                switch (direction) {
                    case 0: {
                        if (i - 1 < 0) {
                            return false;
                        } else if (cur_layout[i-1][j] != figure && cur_layout[i-1][j] != 0) {
                            return false;
                        }
                        break;
                    }
                    case 1: {
                        if (i + 1 > 4) {
                            return false;
                        } else if (cur_layout[i+1][j] != figure && cur_layout[i+1][j] != 0) {
                            return false;
                        }
                        break;
                    }
                    case 2: {
                        if (j - 1 < 0) {
                            return false;
                        } else if (cur_layout[i][j-1] != figure && cur_layout[i][j-1] != 0) {
                            return false;
                        }
                        break;
                    }
                    case 3: {
                        if (j + 1 > 3) {
                            return false;
                        } else if (cur_layout[i][j+1] != figure && cur_layout[i][j+1] != 0) {
                            return false;
                        }
                        break;
                    }
                }
            }
        }
        return true;
    }

    private void moveView(int figure, int direction, int view_id, int margin_start, int margin_top) {
        if (!moveCheck(figure, direction)) return;
        ConstraintLayout game_area_layout = findViewById(R.id.GameArea);
        TransitionManager.beginDelayedTransition(game_area_layout);
        ConstraintSet set = new ConstraintSet();
        set.clone(game_area_layout);
        int distance = getResources().getDimensionPixelSize(R.dimen.game_spacing_one);
        switch (direction) {
            case 0: {
                set.setMargin(view_id, ConstraintSet.TOP, margin_top - distance);
                for (int i = 0; i < 5; i++) {
                    for (int j = 0; j < 4; j++) {
                        if (cur_layout[i][j] == figure) {
                            cur_layout[i-1][j] = figure;
                            cur_layout[i][j] = 0;
                        }
                    }
                }
                break;
            }
            case 1: {
                set.setMargin(view_id, ConstraintSet.TOP, margin_top + distance);
                for (int i = 4; i >= 0; i--) {
                    for (int j = 0; j < 4; j++) {
                        if (cur_layout[i][j] == figure) {
                            cur_layout[i+1][j] = figure;
                            cur_layout[i][j] = 0;
                        }
                    }
                }
                break;
            }
            case 2: {
                set.setMargin(view_id, ConstraintSet.START, margin_start - distance);
                for (int i = 0; i < 5; i++) {
                    for (int j = 0; j < 4; j++) {
                        if (cur_layout[i][j] == figure) {
                            cur_layout[i][j-1] = figure;
                            cur_layout[i][j] = 0;
                        }
                    }
                }
                break;
            }
            case 3: {
                set.setMargin(view_id, ConstraintSet.START, margin_start + distance);
                for (int i = 0; i < 5; i++) {
                    for (int j = 3; j >= 0; j--) {
                        if (cur_layout[i][j] == figure) {
                            cur_layout[i][j+1] = figure;
                            cur_layout[i][j] = 0;
                        }
                    }
                }
                break;
            }
        }
        set.applyTo(game_area_layout);
        step++;
        mStep.setText(Integer.toString(step));
        finishCheck();
    }

    private void initLisentner() {
        m_lisetner = new View.OnTouchListener() {
            float start_x = (float) 0;
            float start_y = (float) 0;
            int figure = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    start_x = event.getX();
                    start_y = event.getY();

                    switch (v.getId()) {
                        case R.id.Zhangf:
                            figure = 1;
                            break;
                        case R.id.Caoc:
                            figure = 2;
                            break;
                        case R.id.Mac:
                            figure = 3;
                            break;
                        case R.id.Huangz:
                            figure = 4;
                            break;
                        case R.id.Guany:
                            figure = 5;
                            break;
                        case R.id.Zhaoy:
                            figure = 6;
                            break;
                        case R.id.Zu1:
                            figure = 7;
                            break;
                        case R.id.Zu2:
                            figure = 8;
                            break;
                        case R.id.Zu3:
                            figure = 9;
                            break;
                        case R.id.Zu4:
                            figure = 10;
                            break;
                    }
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    float distance_x = event.getX() - start_x;
                    float distance_y = event.getY() - start_y;

                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
                    int margin_start = params.getMarginStart();
                    int margin_top = params.topMargin;

                    if (distance_y < 0 && distance_x*distance_x < distance_y*distance_y) {
                        moveView(figure, 0, v.getId(), margin_start, margin_top);
                    } else if (distance_y > 0 && distance_x*distance_x < distance_y*distance_y) {
                        moveView(figure, 1, v.getId(), margin_start, margin_top);
                    } else if (distance_x < 0 && distance_x*distance_x > distance_y*distance_y) {
                        moveView(figure, 2, v.getId(), margin_start, margin_top);
                    } else if (distance_x > 0 && distance_x*distance_x > distance_y*distance_y) {
                        moveView(figure, 3, v.getId(), margin_start, margin_top);
                    }
                }
                return true;
            }
        };

        findViewById(R.id.Zhangf).setOnTouchListener(m_lisetner);
        findViewById(R.id.Caoc).setOnTouchListener(m_lisetner);
        findViewById(R.id.Mac).setOnTouchListener(m_lisetner);
        findViewById(R.id.Huangz).setOnTouchListener(m_lisetner);
        findViewById(R.id.Guany).setOnTouchListener(m_lisetner);
        findViewById(R.id.Zhaoy).setOnTouchListener(m_lisetner);
        findViewById(R.id.Zu1).setOnTouchListener(m_lisetner);
        findViewById(R.id.Zu2).setOnTouchListener(m_lisetner);
        findViewById(R.id.Zu3).setOnTouchListener(m_lisetner);
        findViewById(R.id.Zu4).setOnTouchListener(m_lisetner);
    }

    private void initPass() {
        int zero = getResources().getDimensionPixelSize(R.dimen.game_spacing_zero);
        int one = getResources().getDimensionPixelSize(R.dimen.game_spacing_one);
        int two = getResources().getDimensionPixelSize(R.dimen.game_spacing_two);
        int three = getResources().getDimensionPixelSize(R.dimen.game_spacing_three);
        int four = getResources().getDimensionPixelSize(R.dimen.game_spacing_four);
        switch (pass) {
            case 1: {
                cur_layout = new int[][]{{1, 2, 2, 3}, {1, 2, 2, 3}, {4, 5, 5, 6}, {4, 8, 9, 6}, {7, 0, 0, 10}};
                break;
            }
            case 2: {
                cur_layout = new int[][]{{1, 2, 2, 3}, {1, 2, 2, 3}, {7, 5, 5, 10}, {4, 8, 9, 6}, {4, 0, 0, 6}};
                ConstraintLayout game_area_layout = findViewById(R.id.GameArea);
                ConstraintSet set = new ConstraintSet();
                set.clone(game_area_layout);
                set.setMargin(R.id.Zhangf, ConstraintSet.START, zero);
                set.setMargin(R.id.Zhangf, ConstraintSet.TOP, zero);
                set.setMargin(R.id.Caoc, ConstraintSet.START, one);
                set.setMargin(R.id.Caoc, ConstraintSet.TOP, zero);
                set.setMargin(R.id.Mac, ConstraintSet.START, three);
                set.setMargin(R.id.Mac, ConstraintSet.TOP, zero);
                set.setMargin(R.id.Huangz, ConstraintSet.START, zero);
                set.setMargin(R.id.Huangz, ConstraintSet.TOP, three);
                set.setMargin(R.id.Guany, ConstraintSet.START, one);
                set.setMargin(R.id.Guany, ConstraintSet.TOP, two);
                set.setMargin(R.id.Zhaoy, ConstraintSet.START, three);
                set.setMargin(R.id.Zhaoy, ConstraintSet.TOP, three);
                set.setMargin(R.id.Zu1, ConstraintSet.START, zero);
                set.setMargin(R.id.Zu1, ConstraintSet.TOP, two);
                set.setMargin(R.id.Zu2, ConstraintSet.START, one);
                set.setMargin(R.id.Zu2, ConstraintSet.TOP, three);
                set.setMargin(R.id.Zu3, ConstraintSet.START, two);
                set.setMargin(R.id.Zu3, ConstraintSet.TOP, three);
                set.setMargin(R.id.Zu4, ConstraintSet.START, three);
                set.setMargin(R.id.Zu4, ConstraintSet.TOP, two);
                set.applyTo(game_area_layout);
                break;
            }
            case 3: {
                cur_layout = new int[][]{{1, 2, 2, 3}, {1, 2, 2, 3}, {7, 8, 9, 10}, {4, 5, 5, 6}, {4, 0, 0, 6}};
                ConstraintLayout game_area_layout = findViewById(R.id.GameArea);
                ConstraintSet set = new ConstraintSet();
                set.clone(game_area_layout);
                set.setMargin(R.id.Zhangf, ConstraintSet.START, zero);
                set.setMargin(R.id.Zhangf, ConstraintSet.TOP, zero);
                set.setMargin(R.id.Caoc, ConstraintSet.START, one);
                set.setMargin(R.id.Caoc, ConstraintSet.TOP, zero);
                set.setMargin(R.id.Mac, ConstraintSet.START, three);
                set.setMargin(R.id.Mac, ConstraintSet.TOP, zero);
                set.setMargin(R.id.Huangz, ConstraintSet.START, zero);
                set.setMargin(R.id.Huangz, ConstraintSet.TOP, three);
                set.setMargin(R.id.Guany, ConstraintSet.START, one);
                set.setMargin(R.id.Guany, ConstraintSet.TOP, three);
                set.setMargin(R.id.Zhaoy, ConstraintSet.START, three);
                set.setMargin(R.id.Zhaoy, ConstraintSet.TOP, three);
                set.setMargin(R.id.Zu1, ConstraintSet.START, zero);
                set.setMargin(R.id.Zu1, ConstraintSet.TOP, two);
                set.setMargin(R.id.Zu2, ConstraintSet.START, one);
                set.setMargin(R.id.Zu2, ConstraintSet.TOP, two);
                set.setMargin(R.id.Zu3, ConstraintSet.START, two);
                set.setMargin(R.id.Zu3, ConstraintSet.TOP, two);
                set.setMargin(R.id.Zu4, ConstraintSet.START, three);
                set.setMargin(R.id.Zu4, ConstraintSet.TOP, two);
                set.applyTo(game_area_layout);
                break;
            }
            case 4: {
                cur_layout = new int[][]{{7, 2, 2, 10}, {1, 2, 2, 3}, {1, 5, 5, 3}, {4, 8, 9, 6}, {4, 0, 0, 6}};
                ConstraintLayout game_area_layout = findViewById(R.id.GameArea);
                ConstraintSet set = new ConstraintSet();
                set.clone(game_area_layout);
                set.setMargin(R.id.Zhangf, ConstraintSet.START, zero);
                set.setMargin(R.id.Zhangf, ConstraintSet.TOP, one);
                set.setMargin(R.id.Caoc, ConstraintSet.START, one);
                set.setMargin(R.id.Caoc, ConstraintSet.TOP, zero);
                set.setMargin(R.id.Mac, ConstraintSet.START, three);
                set.setMargin(R.id.Mac, ConstraintSet.TOP, one);
                set.setMargin(R.id.Huangz, ConstraintSet.START, zero);
                set.setMargin(R.id.Huangz, ConstraintSet.TOP, three);
                set.setMargin(R.id.Guany, ConstraintSet.START, one);
                set.setMargin(R.id.Guany, ConstraintSet.TOP, two);
                set.setMargin(R.id.Zhaoy, ConstraintSet.START, three);
                set.setMargin(R.id.Zhaoy, ConstraintSet.TOP, three);
                set.setMargin(R.id.Zu1, ConstraintSet.START, zero);
                set.setMargin(R.id.Zu1, ConstraintSet.TOP, zero);
                set.setMargin(R.id.Zu2, ConstraintSet.START, one);
                set.setMargin(R.id.Zu2, ConstraintSet.TOP, three);
                set.setMargin(R.id.Zu3, ConstraintSet.START, two);
                set.setMargin(R.id.Zu3, ConstraintSet.TOP, three);
                set.setMargin(R.id.Zu4, ConstraintSet.START, three);
                set.setMargin(R.id.Zu4, ConstraintSet.TOP, zero);
                set.applyTo(game_area_layout);
                break;
            }
            case 5: {
                cur_layout = new int[][]{{2, 2, 1, 3}, {2, 2, 1, 3}, {5, 5, 7, 8}, {4, 6, 9, 10}, {4, 6, 0, 0}};
                ConstraintLayout game_area_layout = findViewById(R.id.GameArea);
                ConstraintSet set = new ConstraintSet();
                set.clone(game_area_layout);
                set.setMargin(R.id.Zhangf, ConstraintSet.START, two);
                set.setMargin(R.id.Zhangf, ConstraintSet.TOP, zero);
                set.setMargin(R.id.Caoc, ConstraintSet.START, zero);
                set.setMargin(R.id.Caoc, ConstraintSet.TOP, zero);
                set.setMargin(R.id.Mac, ConstraintSet.START, three);
                set.setMargin(R.id.Mac, ConstraintSet.TOP, zero);
                set.setMargin(R.id.Huangz, ConstraintSet.START, zero);
                set.setMargin(R.id.Huangz, ConstraintSet.TOP, three);
                set.setMargin(R.id.Guany, ConstraintSet.START, zero);
                set.setMargin(R.id.Guany, ConstraintSet.TOP, two);
                set.setMargin(R.id.Zhaoy, ConstraintSet.START, one);
                set.setMargin(R.id.Zhaoy, ConstraintSet.TOP, three);
                set.setMargin(R.id.Zu1, ConstraintSet.START, two);
                set.setMargin(R.id.Zu1, ConstraintSet.TOP, two);
                set.setMargin(R.id.Zu2, ConstraintSet.START, three);
                set.setMargin(R.id.Zu2, ConstraintSet.TOP, two);
                set.setMargin(R.id.Zu3, ConstraintSet.START, two);
                set.setMargin(R.id.Zu3, ConstraintSet.TOP, three);
                set.setMargin(R.id.Zu4, ConstraintSet.START, three);
                set.setMargin(R.id.Zu4, ConstraintSet.TOP, three);
                set.applyTo(game_area_layout);
                break;
            }
            case 6: {
                cur_layout = new int[][]{{1, 2, 2, 3}, {1, 2, 2, 3}, {0, 4, 6, 0}, {7, 4, 6, 8}, {9, 5, 5, 10}};
                ConstraintLayout game_area_layout = findViewById(R.id.GameArea);
                ConstraintSet set = new ConstraintSet();
                set.clone(game_area_layout);
                set.setMargin(R.id.Zhangf, ConstraintSet.START, zero);
                set.setMargin(R.id.Zhangf, ConstraintSet.TOP, zero);
                set.setMargin(R.id.Caoc, ConstraintSet.START, one);
                set.setMargin(R.id.Caoc, ConstraintSet.TOP, zero);
                set.setMargin(R.id.Mac, ConstraintSet.START, three);
                set.setMargin(R.id.Mac, ConstraintSet.TOP, zero);
                set.setMargin(R.id.Huangz, ConstraintSet.START, one);
                set.setMargin(R.id.Huangz, ConstraintSet.TOP, two);
                set.setMargin(R.id.Guany, ConstraintSet.START, one);
                set.setMargin(R.id.Guany, ConstraintSet.TOP, four);
                set.setMargin(R.id.Zhaoy, ConstraintSet.START, two);
                set.setMargin(R.id.Zhaoy, ConstraintSet.TOP, two);
                set.setMargin(R.id.Zu1, ConstraintSet.START, zero);
                set.setMargin(R.id.Zu1, ConstraintSet.TOP, three);
                set.setMargin(R.id.Zu2, ConstraintSet.START, three);
                set.setMargin(R.id.Zu2, ConstraintSet.TOP, three);
                set.setMargin(R.id.Zu3, ConstraintSet.START, zero);
                set.setMargin(R.id.Zu3, ConstraintSet.TOP, four);
                set.setMargin(R.id.Zu4, ConstraintSet.START, three);
                set.setMargin(R.id.Zu4, ConstraintSet.TOP, four);
                set.applyTo(game_area_layout);
                break;
            }
        }
    }

}
