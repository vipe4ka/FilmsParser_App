package com.example.filmsparserapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

public class WatchedMoviesDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "WatchedMovies.db";

    public WatchedMoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_WATCHED_MOVIES_TABLE = "CREATE TABLE WatchedMovies (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " + // Добавлен идентификатор _id
                "movie_id TEXT UNIQUE NOT NULL, " + // movie_id объявлен как UNIQUE
                "title TEXT NOT NULL)";
        db.execSQL(SQL_CREATE_WATCHED_MOVIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
