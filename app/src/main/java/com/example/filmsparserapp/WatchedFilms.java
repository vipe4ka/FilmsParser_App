package com.example.filmsparserapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.filmsparserapp.databinding.FragmentWatchedFilmBinding;

import java.util.List;

public class WatchedFilms extends Fragment {

    private FragmentWatchedFilmBinding binding;
    private RecyclerView recyclerView; // Добавленное поле
    private WatchedFilmsAdapter adapter; // Добавленное поле

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_watched_film, container, false);

        recyclerView = view.findViewById(R.id.watchedMoviesRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        WatchedMoviesRepository repository = new WatchedMoviesRepository(getContext());
        List<WatchedMovie> watchedMovies = repository.getWatchedMovies();

        adapter = new WatchedFilmsAdapter(watchedMovies);
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
