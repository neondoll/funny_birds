package com.example.funnybirds;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.CountDownTimer;
import android.view.SurfaceHolder;

import java.util.ArrayList;

public class DrawThread extends Thread {
    private SurfaceHolder surfaceHolder;

    private volatile boolean running = true;//флаг для остановки
    private Bitmap player, enemy, pb;
    private int towardPointX;
    private int towardPointY;

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

    public DrawThread(Context context, SurfaceHolder surfaceHolder) {
        player = BitmapFactory.decodeResource(context.getResources(), R.drawable.player);
        int w = player.getWidth() / 5;
        int h = player.getHeight() / 3;
        Rect firstFrame = new Rect(0, 0, w, h);
        playerBird = new Sprite(10, 0, 0, 100, firstFrame, player);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 4; j++) {
                if (i == 0 && j == 0) continue;
                if (i == 2 && j == 3) continue;
                playerBird.addFrame(new Rect(j * w, i * h, j * w + w, i * w + w));
            }
        }

        enemy = BitmapFactory.decodeResource(context.getResources(), R.drawable.enemy);
        w = enemy.getWidth() / 5;
        h = enemy.getHeight() / 3;
        firstFrame = new Rect(4 * w, 0, 5 * w, h);
        enemyBirds = new ArrayList<Sprite>();
        for (int k = 0; k < countEnemyBirds; k++) {
            enemyBirds.add(new Sprite(2000, 250, -300, 0, firstFrame, enemy));
            for (int i = 0; i < 3; i++) {
                for (int j = 4; j >= 0; j--) {
                    if (i == 0 && j == 4) continue;
                    if (i == 2 && j == 0) continue;
                    enemyBirds.get(k).addFrame(new Rect(j * w, i * h, j * w + w, i * w + w));
                }
            }
        }

        pb = BitmapFactory.decodeResource(context.getResources(), R.drawable.butterfly);
        w = pb.getWidth() / 7;
        h = pb.getHeight() / 2;
        firstFrame = new Rect(0, 0, w, h);
        pointBonus = new Sprite(2000, 500, -300, 0, firstFrame, pb);
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 7; j++) {
                if (i == 0 && j == 0) continue;
                if (i == 1 && j == 6) continue;
                pointBonus.addFrame(new Rect(j * w, i * h, j * w + w, i * w + w));
            }
        }

        DrawThread.Timer t = new DrawThread.Timer();
        t.start();

        this.surfaceHolder = surfaceHolder;
    }

    public void requestStop() {
        running = false;
    }

    public void setTowardPoint(int x, int y) {
        towardPointX = x;
        towardPointY = y;

        boolean check = true;
        for (int k = 0; k < countEnemyBirds; k++) {
            if (
                    x >= enemyBirds.get(k).getBoundingBoxRect().left &&
                            x <= enemyBirds.get(k).getBoundingBoxRect().right &&
                            y >= enemyBirds.get(k).getBoundingBoxRect().top &&
                            y <= enemyBirds.get(k).getBoundingBoxRect().bottom
            ) {
                teleportEnemy(k);
                check = false;
            }
        }
        if (check) {
            if (y < playerBird.getBoundingBoxRect().top) {
                playerBird.setVy(-100);
                points--;
            } else if (y > (playerBird.getBoundingBoxRect().bottom)) {
                playerBird.setVy(100);
                points--;
            }
        }
    }

    @Override
    public void run() {
        while (running) {
            Canvas canvas = surfaceHolder.lockCanvas();
            if (canvas != null) {
                try {
                    canvas.drawARGB(250, 127, 199, 255);  // цвет фона
                    playerBird.draw(canvas);
                    for (int k = 0; k < countEnemyBirds; k++) enemyBirds.get(k).draw(canvas);
                    pointBonus.draw(canvas);

                    Paint p = new Paint();
                    p.setAntiAlias(true);
                    p.setTextSize(55.0f);
                    p.setColor(Color.WHITE);
                    canvas.drawText(points + "", viewWidth - 200, 70, p);
                    canvas.drawText("Уровень " + level, 100, 70, p);
                } finally {
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }

    protected void update() {
        if (play) {
            playerBird.update(timerInterval);
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
                    int w = enemy.getWidth() / 5;
                    int h = enemy.getHeight() / 3;
                    Rect firstFrame = new Rect(4 * w, 0, 5 * w, h);
                    enemyBirds.add(new Sprite(2000, 250, -300, 0, firstFrame, enemy));
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
                    int w = enemy.getWidth() / 5;
                    int h = enemy.getHeight() / 3;
                    Rect firstFrame = new Rect(4 * w, 0, 5 * w, h);
                    enemyBirds.add(new Sprite(2000, 250, -300, 0, firstFrame, enemy));
                }
                points = 0;
            }
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

    private void teleportEnemy(int k) {
        enemyBirds.get(k).setX(viewWidth + Math.random() * 500);
        enemyBirds.get(k).setY(Math.random() * (viewHeight - enemyBirds.get(k).getFrameHeight()));
    }

    private void teleportPoint() {
        pointBonus.setX(viewWidth + Math.random() * 500);
        pointBonus.setY(Math.random() * (viewHeight - pointBonus.getFrameHeight()));
    }

    protected void setWidth(int width) {
        viewWidth = width;
    }

    protected void setHeight(int height) {
        viewHeight = height;
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