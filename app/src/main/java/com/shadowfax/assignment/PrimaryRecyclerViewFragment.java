package com.shadowfax.assignment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class PrimaryRecyclerViewFragment extends Fragment {

    private RecyclerView mPrimaryRecyclerView;
    private String[] mMoviesGenre, mActionMovies;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMoviesGenre = new String[]{
                "Action", "Adventure", "Comedy", "Crime", "Fantasy",
                "Historical", "Horror", "Magical", "Mystery", "Paranoid"
        };

        mActionMovies = new String[] {"Mission: Impossible – Rogue Nation",
                "Mad Max: Fury Road", "Star Wars: The Force Awakens",
                "Avengers: Age of Ultron", "Ant- Man","Terminator Genisys",        "Furious 7",              "Blackhat", "The Man from U.N.C.L.E",
                "Jurassic World"
        };
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.primary_recycler_view, container, false);

        // Creating the primary recycler view adapter
        PrimaryAdapter adapter = new PrimaryAdapter(mMoviesGenre);

        LinearLayoutManager layoutManager = new LinearLayoutManager(
                getActivity(),
                LinearLayoutManager.VERTICAL,
                false
        );

        mPrimaryRecyclerView = (RecyclerView) view.findViewById(R.id.primary_recycler_view);
        mPrimaryRecyclerView.setLayoutManager(layoutManager);
        mPrimaryRecyclerView.setAdapter(adapter);
        return view;
    }

    private class PrimaryViewHolder extends RecyclerView.ViewHolder {
        private TextView mPrimaryMovieGenre;
        private RecyclerView mSecondaryRecyclerView;

        public PrimaryViewHolder(View itemView) {
            super(itemView);
            mPrimaryMovieGenre = (TextView) itemView.findViewById(R.id.primary_movie_genre);
            mSecondaryRecyclerView = (RecyclerView) itemView.findViewById(R.id.secondary_recycler_view);
        }

        // This get called in PrimaryAdapter onBindViewHolder method
        public void bindViews(String genre, int position) {
            mPrimaryMovieGenre.setText(genre);

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                    getActivity(),
                    LinearLayoutManager.HORIZONTAL,
                    false
            );

            mSecondaryRecyclerView.setLayoutManager(linearLayoutManager);
            mSecondaryRecyclerView.setAdapter(getSecondaryAdapter(position));
        }
    }
    private class PrimaryAdapter extends RecyclerView.Adapter<PrimaryViewHolder> {
        private String[] mMovieGenre;

        public PrimaryAdapter(String[] moviesGenre) {
            mMovieGenre = moviesGenre;
        }

        @Override
        public PrimaryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.primary_recycler_view_item, parent, false);
            return new PrimaryViewHolder(view);
        }

        @Override
        public void onBindViewHolder(PrimaryViewHolder holder, int position) {
            String genre = mMovieGenre[position];
            holder.bindViews(genre, position);
        }

        @Override
        public int getItemCount() {
            return mMovieGenre.length;
        }
    }

    private class SecondaryViewHolder extends RecyclerView.ViewHolder {

        private TextView mTextView;

        public SecondaryViewHolder(View view) {
            super(view);
            mTextView = (TextView) itemView.findViewById(R.id.secondary_text_view);
        }

        public void bindView(String name) {
            mTextView.setText(name);
        }
    }

    private class SecondaryAdapter extends RecyclerView.Adapter<SecondaryViewHolder> {
        private String[] mMovies;

        public SecondaryAdapter(String[] movies) {
            mMovies = movies;
        }

        @Override
        public SecondaryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.secondary_recycler_view_item, parent, false);
            return new SecondaryViewHolder(view);
        }

        @Override
        public void onBindViewHolder(SecondaryViewHolder holder, int position) {
            String name = mMovies[position];
            holder.bindView(name);
        }

        @Override
        public int getItemCount() {
            return mMovies.length;
        }
    }

    private SecondaryAdapter getSecondaryAdapter(int position) {

        SecondaryAdapter adapter;
        switch (position) {
            case 0:
                return new SecondaryAdapter(mActionMovies);
            case 1:
                return null;
            case 2:
                return null;
            case 3:
                return null;
            case 4:
                return null;
            case 5:
                return null;
            case 6:
                return null;
            case 7:
                return null;
            case 8:
                return null;
            case 9:
                return null;
            default:
                return null;
        }
    }
}
