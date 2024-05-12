package com.example.filmsparserapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.TextView;

import java.util.List;

public class WatchedFilmsAdapter extends RecyclerView.Adapter<WatchedFilmsAdapter.ViewHolder> {

    private List<WatchedMovie> watchedMovies;

    public WatchedFilmsAdapter(List<WatchedMovie> watchedMovies) {
        this.watchedMovies = watchedMovies;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_watched_film, parent, false);
        ViewHolder holder = new ViewHolder(view);
        holder.idTextView = view.findViewById(R.id.idTextView);
        holder.titleTextView = view.findViewById(R.id.titleTextView);
        holder.deleteButton = view.findViewById(R.id.deleteButton);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WatchedMovie movie = watchedMovies.get(position);
        holder.idTextView.setText(movie.getId());
        holder.titleTextView.setText(movie.getTitle());

        holder.deleteButton.setOnClickListener(v -> {
            // Удаление элемента из базы данных
            WatchedMoviesRepository.deleteWatchedMovie(movie.getId());

            // Удаление элемента из списка и уведомление адаптера об изменениях
            watchedMovies.remove(position);
            notifyItemRemoved(position);
        });
    }

    @Override
    public int getItemCount() {
        return watchedMovies.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View deleteButton;
        TextView idTextView;
        TextView titleTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            idTextView = itemView.findViewById(R.id.idTextView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
        }
    }
}