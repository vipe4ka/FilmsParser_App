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
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GalleryFragment extends Fragment {

    private FragmentGalleryBinding binding;

    int selectedGenre = -1;
    String selectedRating;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Spinner spinnerGenres = root.findViewById(R.id.setgenresButton);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(), R.array.genres_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGenres.setAdapter(adapter);

        spinnerGenres.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedGenre = position + 1;
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        binding.button.setOnClickListener(v -> {
            int vis = binding.layoutFields.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE;
            binding.layoutFields.setVisibility(vis);
            binding.setgenresButton.setVisibility(vis);
            binding.setMinRating.setVisibility(vis);
        });
        binding.research.setOnClickListener(v -> {
            binding.layoutFields.setVisibility(View.GONE);
            binding.setgenresButton.setVisibility(View.GONE);
            binding.setMinRating.setVisibility(View.GONE);

            selectedRating = binding.setMinRating.getText().toString();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String url = "https://ru.kinorium.com/R2D2/?order=rating&page=1&perpage=20&genres%5B%5D=" + selectedGenre + "&imdb_rating_min=" + selectedRating;
                        Document page = Jsoup.connect(url).get();
                        Log.d("MyLog", page.toString());
                        Element lastPageElement = page.select(".lastPage").first();
                        String lastpageStr = lastPageElement.attr("data-page");
                        int lastPageInt = Integer.parseInt(lastpageStr);

                        /*Random rand = new Random();
                        int randPage = rand.nextInt(lastPageInt) + 1;
                        url = "https://ru.kinorium.com/R2D2/?order=rating&page="+randPage+"&perpage=20&genres%5B%5D=" + selectedGenre + "&imdb_rating_min=" + selectedRating;

                        page = Jsoup.connect(url).get();

                        Elements filmElements = page.select("div.filmList div.item");
                        ArrayList<String> filmList = new ArrayList<>();
                        for (Element el : filmElements) {
                            String rel = el.attr("rel");
                            filmList.add(rel);
                        }

                        String randFilm = filmList.get(rand.nextInt(filmList.size()));

                        fetchFilmData(randFilm);*/
                    } catch (IOException e) {
                        e.printStackTrace();
                        // Обработка ошибки, например, вывод в лог или уведомление пользователю
                    }
                }
            }).start();

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