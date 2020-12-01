package com.example.funnybirds;

import android.content.Context;
import android.view.View;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.CountDownTimer;
import android.view.MotionEvent;

import java.util.ArrayList;

public class GameView extends View {
    private Sprite playerBird;
    private ArrayList<Sprite> enemyBirds;
    private int countEnemyBirds = 2;
    private Sprite pointBonus;

    private int viewWidth;
    private int viewHeight;

    private boolean play = true;
    private int points = 0;
    private int level = 1;

    private final int timerInterval = 30;

    public GameView(Context context) {
        super(context);

        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.player);
        int w = b.getWidth() / 5;
        int h = b.getHeight() / 3;
        Rect firstFrame = new Rect(0, 0, w, h);
        playerBird = new Sprite(10, 0, 0, 100, firstFrame, b);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 4; j++) {
                if (i == 0 && j == 0) continue;
                if (i == 2 && j == 3) continue;
                playerBird.addFrame(new Rect(j * w, i * h, j * w + w, i * w + w));
            }
        }

        b = BitmapFactory.decodeResource(getResources(), R.drawable.enemy);
        w = b.getWidth() / 5;
        h = b.getHeight() / 3;
        firstFrame = new Rect(4 * w, 0, 5 * w, h);
        /*enemyBird = new Sprite(2000, 250, -300, 0, firstFrame, b);
        for (int i = 0; i < 3; i++) {
            for (int j = 4; j >= 0; j--) {
                if (i == 0 && j == 4) continue;
                if (i == 2 && j == 0) continue;
                enemyBird.addFrame(new Rect(j * w, i * h, j * w + w, i * w + w));
            }
        }*/
        enemyBirds = new ArrayList<Sprite>();
        for (int k = 0; k < countEnemyBirds; k++) {
            enemyBirds.add(new Sprite(2000, 250, -300, 0, firstFrame, b));
            for (int i = 0; i < 3; i++) {
                for (int j = 4; j >= 0; j--) {
                    if (i == 0 && j == 4) continue;
                    if (i == 2 && j == 0) continue;
                    enemyBirds.get(k).addFrame(new Rect(j * w, i * h, j * w + w, i * w + w));
                }
            }
        }

        b = BitmapFactory.decodeResource(getResources(), R.drawable.butterfly);
        w = b.getWidth() / 7;
        h = b.getHeight() / 2;
        firstFrame = new Rect(0, 0, w, h);
        pointBonus = new Sprite(2000, 500, -300, 0, firstFrame, b);
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 7; j++) {
                if (i == 0 && j == 0) continue;
                if (i == 1 && j == 6) continue;
                pointBonus.addFrame(new Rect(j * w, i * h, j * w + w, i * w + w));
            }
        }

        Timer t = new Timer();
        t.start();
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        viewWidth = w;
        viewHeight = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawARGB(250, 127, 199, 255);  // цвет фона
        playerBird.draw(canvas);
        //enemyBird.draw(canvas);
        for (int k = 0; k < countEnemyBirds; k++) enemyBirds.get(k).draw(canvas);
        pointBonus.draw(canvas);

        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setTextSize(55.0f);
        p.setColor(Color.WHITE);
        canvas.drawText(points + "", viewWidth - 200, 70, p);
        canvas.drawText("Уровень " + level, 100, 70, p);
    }

    protected void update() {
        if (play) {
            playerBird.update(timerInterval);
            //enemyBird.update(timerInterval);
            for (int k = 0; k < countEnemyBirds; k++) enemyBirds.get(k).update(timerInterval);
            pointBonus.update(timerInterval);

            if (playerBird.getY() + playerBird.getFrameHeight() > viewHeight) {
                playerBird.setY(viewHeight - playerBird.getFrameHeight());
                playerBird.setVy(-playerBird.getVy());
                points--;
            } else if (playerBird.getY() < 0) {
                playerBird.setY(0);
                playerBird.setVy(-playerBird.getVy());
                points--;
            }

            for (int k = 0; k < countEnemyBirds; k++) {
                if (enemyBirds.get(k).getX() < -enemyBirds.get(k).getFrameWidth()) {
                    teleportEnemy(k);
                    points += 10;
                }
                if (enemyBirds.get(k).intersect(playerBird)) {
                    teleportEnemy(k);
                    points -= 40;
                }
            }

            if (pointBonus.getX() < -pointBonus.getFrameWidth()) {
                teleportPoint();
            }
            if (pointBonus.intersect(playerBird)) {
                teleportPoint();
                points += 20;
            }

            if (points >= 200) {
                level++;
                if (level % 4 == 0) {
                    countEnemyBirds++;
                    Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.enemy);
                    int w = b.getWidth() / 5;
                    int h = b.getHeight() / 3;
                    Rect firstFrame = new Rect(4 * w, 0, 5 * w, h);
                    enemyBirds.add(new Sprite(2000, 250, -300, 0, firstFrame, b));
                    for (int i = 0; i < 3; i++) {
                        for (int j = 4; j >= 0; j--) {
                            if (i == 0 && j == 4) continue;
                            if (i == 2 && j == 0) continue;
                            enemyBirds.get(countEnemyBirds - 1).addFrame(new Rect(j * w, i * h, j * w + w, i * w + w));
                        }
                    }
                }
                for (int k = 0; k < countEnemyBirds; k++)
                    enemyBirds.get(k).setVx(-300 - (level - 1) * 100);
                points = 0;
            }
            if (points <= -200) {
                level = 1;
                countEnemyBirds = 2;
                enemyBirds = new ArrayList<Sprite>();
                for (int k = 0; k < countEnemyBirds; k++) {
                    Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.enemy);
                    int w = b.getWidth() / 5;
                    int h = b.getHeight() / 3;
                    Rect firstFrame = new Rect(4 * w, 0, 5 * w, h);
                    enemyBirds.add(new Sprite(2000, 250, -300, 0, firstFrame, b));
                }
                points = 0;
            }

            invalidate();
        }
    }

    protected void pause() {
        if (play) {
            playerBird.setVy(0);
            for (int k = 0; k < countEnemyBirds; k++) enemyBirds.get(k).setVx(0);
            pointBonus.setVx(0);

            play = false;
        } else {
            playerBird.setVy(100);
            for (int k = 0; k < countEnemyBirds; k++)
                enemyBirds.get(k).setVx(-300 - (level - 1) * 100);
            pointBonus.setVx(-300);

            play = true;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int eventAction = event.getAction();
        if (eventAction == MotionEvent.ACTION_DOWN) {
            boolean check = true;
            for (int k = 0; k < countEnemyBirds; k++) {
                if (
                        event.getX() >= enemyBirds.get(k).getBoundingBoxRect().left &&
                                event.getX() <= enemyBirds.get(k).getBoundingBoxRect().right &&
                                event.getY() >= enemyBirds.get(k).getBoundingBoxRect().top &&
                                event.getY() <= enemyBirds.get(k).getBoundingBoxRect().bottom
                ) {
                    teleportEnemy(k);
                    check = false;
                }
            }
            if (check) {
                if (event.getY() < playerBird.getBoundingBoxRect().top) {
                    playerBird.setVy(-100);
                    points--;
                } else if (event.getY() > (playerBird.getBoundingBoxRect().bottom)) {
                    playerBird.setVy(100);
                    points--;
                }
            }
        }

        return true;
    }

    private void teleportEnemy(int k) {
        enemyBirds.get(k).setX(viewWidth + Math.random() * 500);
        enemyBirds.get(k).setY(Math.random() * (viewHeight - enemyBirds.get(k).getFrameHeight()));
    }

    private void teleportPoint() {
        pointBonus.setX(viewWidth + Math.random() * 500);
        pointBonus.setY(Math.random() * (viewHeight - pointBonus.getFrameHeight()));
    }

    class Timer extends CountDownTimer {
        public Timer() {
            super(Integer.MAX_VALUE, timerInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            update();
        }

        @Override
        public void onFinish() {
        }
    }
}