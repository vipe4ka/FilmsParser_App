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
        // Обновление года
        TextView yearTextView = getView().findViewById(R.id.yearTextView);
        yearTextView.setText(String.valueOf(filmData.getYear()));
        yearTextView.setVisibility(View.VISIBLE);
        // Обновление рейтинга
        TextView ratingTextView = getView().findViewById(R.id.ratingTextView);
        ratingTextView.setText(String.valueOf(filmData.getRating()));
        ratingTextView.setVisibility(View.VISIBLE);
        // Обновление жанров
        TextView genresTextView = getView().findViewById(R.id.genresTextView);
        StringBuilder genresBuilder = new StringBuilder();
        for (String genre : filmData.getGenres()) {
            genresBuilder.append(genre).append(", ");
        }
        String genresText = genresBuilder.toString().trim();
        genresText = genresText.substring(0, genresText.length() - 1);
        genresTextView.setText(genresText);
        genresTextView.setVisibility(View.VISIBLE);
        // Обновление описания
        TextView descriptionTextView = getView().findViewById(R.id.descriptionTextView);
        descriptionTextView.setText(filmData.getDescription());
        descriptionTextView.setVisibility(View.VISIBLE);
    }

    private void run(String id) {
        try {
            // Загрузка страницы фильма
            Document page = Jsoup.connect("https://ru.kinorium.com/" + id + "/").get();
            Element titleEl = page.select("h1[class=\"film-page__title-text film-page__itemprop\"]").first();
            String title = titleEl.text();
            Element premiereAndMoney = page.select("td[class=\"data\"]").get(3);
            String pm = premiereAndMoney.text();
            Element ratingEl = page.select("div[class=\"film-page__title-buttons\"]").first();
            String rating = ratingEl.text();
            Elements genresEl = page.select("li[itemprop=\"genre\"]");
            List<String> genres = new ArrayList<>();
            for (Element g : genresEl) {
                String genre = g.attr("content");
                genres.add(genre);
            }
            Element descriptionEl = page.select("section[itemprop=\"description\"]").first();
            String description = descriptionEl.text();
            Element imageBlock = page.select("div.carousel_image-handler img").first();
            String posterUrl = imageBlock.attr("src");
            Log.d("MyLog", posterUrl);

            FilmData filmData = new FilmData();
            filmData.setPosterUrl(posterUrl);
            filmData.setTitle(title);
            filmData.setYear(pm);
            filmData.setRating(rating);
            filmData.setGenres(genres);
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