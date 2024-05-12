package com.example.filmsparserapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class WatchedMoviesRepository {
    private static WatchedMoviesDbHelper dbHelper;

    public WatchedMoviesRepository(Context context) {
        dbHelper = new WatchedMoviesDbHelper(context);
    }

    public void addWatchedMovie(String id, String title) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("movie_id", id);
        values.put("title", title);
        try {
            db.insertOrThrow("WatchedMovies", null, values);
        } catch (SQLiteConstraintException e) {
            // Если movie_id уже существует, не делаем ничего и продолжаем работу
            e.printStackTrace();
        }
        db.close();
    }

    public List<WatchedMovie> getWatchedMovies() {
        List<WatchedMovie> watchedMovies = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("WatchedMovies", new String[]{"movie_id", "title"}, null, null, null, null, null);
        while (cursor.moveToNext()) {
            String id = cursor.getString(cursor.getColumnIndexOrThrow("movie_id"));
            String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
            WatchedMovie movie = new WatchedMovie(id, title);
            watchedMovies.add(movie);
        }
        cursor.close();
        db.close();
        return watchedMovies;
    }

    public static void deleteWatchedMovie(String id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("WatchedMovies", "movie_id = ?", new String[]{String.valueOf(id)});
        db.close();
    }
}