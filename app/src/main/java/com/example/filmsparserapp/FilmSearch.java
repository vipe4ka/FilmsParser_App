package com.example.filmsparserapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.filmsparserapp.databinding.FragmentSearchFilmBinding;
import com.squareup.picasso.Picasso;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import android.graphics.Color;
import android.graphics.PorterDuff;

import java.io.*;

public class FilmSearch extends Fragment {

    private FragmentSearchFilmBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSearchFilmBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        binding.button.setOnClickListener(v -> {
            int vis = binding.layoutFields.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE;
            binding.layoutFields.setVisibility(vis);
            binding.editTextId.setVisibility(vis);
            binding.buttonSearch.setVisibility(vis);
        });
        binding.buttonSearch.setOnClickListener(v -> {
            binding.layoutFields.setVisibility(View.GONE);
            binding.editTextId.setVisibility(View.GONE);
            binding.buttonSearch.setVisibility(View.GONE);
            binding.watchbutton.setVisibility(View.VISIBLE);
            String id = binding.editTextId.getText().toString();
            fetchFilmData(id);
        });
        binding.kinoru.setOnClickListener(v -> {
            String url = "https://www.kino.ru/";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        });
        binding.watchbutton.setOnClickListener(v -> {
            String id = binding.editTextId.getText().toString();
            TextView titleTextView = getView().findViewById(R.id.titleTextView);
            String title = titleTextView.getText().toString();

            WatchedMoviesRepository repository = new WatchedMoviesRepository(getContext());
            repository.addWatchedMovie(id, title);
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    private void fetchFilmData(String id) {
        new Thread(() -> run(id)).start();
    }

    private void updateUI(FilmData filmData) {
        // Обновление постера
        ImageView posterImageView = getView().findViewById(R.id.posterImageView);
        Picasso.get().load(filmData.getPosterUrl()).into(posterImageView);
        posterImageView.setVisibility(View.VISIBLE);
        // Обновление названия
        TextView titleTextView = getView().findViewById(R.id.titleTextView);
        titleTextView.setText(filmData.getTitle());
        titleTextView.setVisibility(View.VISIBLE);
        // Обновление рейтинга
        TextView ratingTextView = getView().findViewById(R.id.ratingTextView);
        ratingTextView.setText(String.valueOf(filmData.getRating()));
        ratingTextView.setVisibility(View.VISIBLE);
        Double rate = Double.parseDouble(filmData.getRating());
        // Меняем цвет рейтинга
        if (rate < 5) {
            ratingTextView.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
        } else if (rate < 7) {
            ratingTextView.getBackground().setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_IN);
        } else {
            ratingTextView.getBackground().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
        }
        // Обновление жанров
        TextView genresTextView = getView().findViewById(R.id.genresTextView);
        genresTextView.setText(filmData.getGenre());
        genresTextView.setVisibility(View.VISIBLE);
        // Обновление описания
        TextView descriptionTextView = getView().findViewById(R.id.descriptionTextView);
        descriptionTextView.setText(filmData.getDescription());
        descriptionTextView.setVisibility(View.VISIBLE);
    }

    private void run(String id) {
        try {
            // Загрузка страницы фильма
            Document page = Jsoup.connect("https://www.kino.ru/film/" + id).get();
            Log.d("MyLog", page.toString());
            //название
            Element titleEl = page.select("div[class=\"titles\"]").first();
            String title = titleEl.text();
            //рейтинг
            Element ratingEl = page.select("div[class=\"stars_in_page star_rate_film_" + id + "\"]").first();
            String rating = ratingEl.text();
            //жанр
            Element genreEl = page.select("a[itemprop=\"genre\"]").first();
            String genre = genreEl.text();
            //описание
            Element descriptionEl = page.select("div[itemprop=\"description\"]").first();
            String description = descriptionEl.text();
            //url картинки
            Element imageBlock = page.select("img[itemprop=\"image\"]").first();
            String posterUrl = imageBlock.attr("src");

            FilmData filmData = new FilmData();
            filmData.setPosterUrl(posterUrl);
            filmData.setTitle(title);
            filmData.setRating(rating);
            filmData.setGenre(genre);
            filmData.setDescription(description);

            // Обновление UI с полученной информацией о фильме
            getActivity().runOnUiThread(() ->
                    updateUI(filmData));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}