package com.example.filmsparserapp.ui.gallery;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.filmsparserapp.FilmData;
import com.example.filmsparserapp.R;
import com.example.filmsparserapp.databinding.FragmentGalleryBinding;
import com.squareup.picasso.Picasso;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GalleryFragment extends Fragment {

    private FragmentGalleryBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentGalleryBinding.inflate(inflater, container, false);
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
            String id = binding.editTextId.getText().toString();
            fetchFilmData(id);
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