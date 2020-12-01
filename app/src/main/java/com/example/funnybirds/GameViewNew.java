package com.example.funnybirds;

import android.content.Context;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameViewNew extends SurfaceView implements SurfaceHolder.Callback {
    private DrawThread drawThread;

    public GameViewNew(Context context) {
        super(context);
        getHolder().addCallback(this);

        /*Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.player);
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

        GameViewNew.Timer t = new GameViewNew.Timer();
        t.start();*/
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        drawThread = new DrawThread(getContext(), getHolder());
        drawThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        drawThread.setWidth(width);
        drawThread.setHeight(height);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        drawThread.requestStop();
        boolean retry = true;
        while (retry) {
            try {
                drawThread.join();
                retry = false;
            } catch (InterruptedException e) {
                //
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        drawThread.setTowardPoint((int) event.getX(), (int) event.getY());
        return false;
    }

    protected void pause() {
        drawThread.pause();
    }
}
