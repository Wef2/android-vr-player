package com.neointernet.neo360.adapter;

import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.neointernet.neo360.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

public class VideoFileAdapter extends RecyclerView.Adapter<VideoFileAdapter.ViewHolder> {
    private ArrayList<File> videos;
    private View.OnClickListener listener;

    public VideoFileAdapter(View.OnClickListener listener, Collection<File> videoModels) {
        this.listener = listener;
        videos = new ArrayList<>();
        videos.addAll(videoModels);
    }

    @Override
    public VideoFileAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                          int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_row, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.cardView.setTag(getFile(position));
        holder.imageView.setImageBitmap(ThumbnailUtils.createVideoThumbnail(getFile(position).getPath(), MediaStore.Video.Thumbnails.FULL_SCREEN_KIND));
        holder.nameText.setText(getFile(position).getName());
        holder.lengthText.setText(Long.toString(getFile(position).length()));
        holder.cardView.setOnClickListener(listener);
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    public File getFile(int position) {
        return videos.get(position);
    }

    public String getFilePath(int position) {
        return videos.get(position).getPath();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public CardView cardView;
        public TextView nameText, lengthText;
        public ImageView imageView;

        public ViewHolder(View v) {
            super(v);
            cardView = (CardView) v.findViewById(R.id.card_view);
            imageView = (ImageView) v.findViewById(R.id.video_image);
            nameText = (TextView) v.findViewById(R.id.video_name_text);
            lengthText = (TextView) v.findViewById(R.id.video_length_text);
        }
    }
}