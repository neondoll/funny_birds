package com.example.funnybirds;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {
    //private GameView game;
    private GameViewNew game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //game = new GameView(this);
        game = new GameViewNew(this);
        setContentView(game);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Раздуть меню; это добавляет элементы на панель действий, если она присутствует.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Здесь обрабатываются щелчки по элементам панели действий.
        // Панель действий будет автоматически обрабатывать нажатия
        // кнопки «Домой/Вверх», если вы укажете родительское действие
        // в AndroidManifest.xml.
        int id = item.getItemId();

        // noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.action_pause) {
            game.pause();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}