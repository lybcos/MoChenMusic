package com.example.administrator.mochenmusic.adapter;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.mochenmusic.Model.Music;
import com.example.administrator.mochenmusic.R;
import com.example.administrator.mochenmusic.constants.OnMoreClickListener;
import com.example.administrator.mochenmusic.service.AudioPlayer;
import com.example.administrator.mochenmusic.utils.binding.CoverLoader;
import com.example.administrator.mochenmusic.utils.binding.FileUtils;
import com.example.administrator.mochenmusic.utils.binding.ViewBinder;

import java.util.List;

import butterknife.Bind;

/**
 * 播放列表适配器
 */
public class PlaylistAdapter extends BaseAdapter {
    private List<Music>musicList;
    private boolean isPlaylist;
    private OnMoreClickListener onMoreClick;

    public void setOnMoreClick(OnMoreClickListener onMoreClick) {
        this.onMoreClick = onMoreClick;
    }

    public PlaylistAdapter(List<Music> musicList) {
        this.musicList = musicList;
    }



    public void setIsPlaylist(boolean isPlaylist) {
        this.isPlaylist = isPlaylist;
    }

    @Override
    public int getCount() {
        return musicList.size();
    }

    @Override
    public Object getItem(int position) {
        return musicList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_music, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder= (ViewHolder) convertView.getTag();
        }
        holder.vPlaying.setVisibility((isPlaylist && position == AudioPlayer.get().getPlayPosition()) ? View.VISIBLE : View.INVISIBLE);
        Music music = musicList.get(position);
        Bitmap cover = CoverLoader.get().loadThumb(music);
        holder.ivCover.setImageBitmap(cover);
        holder.tvTitle.setText(music.getTitle());
        String artist = FileUtils.getArtistAndAlbum(music.getArtist(), music.getAlbum());
        holder.tvArtist.setText(artist);
        holder.ivMore.setOnClickListener(v -> {
            if (onMoreClick != null) {
                onMoreClick.onMoreClick(position);
            }
        });
        holder.vDivider.setVisibility(isShowDivider(position) ? View.VISIBLE : View.GONE);
        return convertView;
    }

    private boolean isShowDivider(int position) {
        return position!=musicList.size()-1;
    }

    static class ViewHolder {
        @Bind(R.id.v_playing)
         View vPlaying;
        @Bind(R.id.iv_cover)
         ImageView ivCover;
        @Bind(R.id.tv_title)
         TextView tvTitle;
        @Bind(R.id.tv_artist)
         TextView tvArtist;
        @Bind(R.id.iv_more)
         ImageView ivMore;
        @Bind(R.id.v_divider)
         View vDivider;

        public ViewHolder(View view) {
//            ViewBinder.bind(this, view);
           vPlaying=view.findViewById(R.id.v_playing);
           ivCover=view.findViewById(R.id.iv_cover);
           ivMore=view.findViewById(R.id.iv_more);
           tvTitle=view.findViewById(R.id.tv_title);
           tvArtist=view.findViewById(R.id.tv_artist);
           vDivider=view.findViewById(R.id.v_divider);
        }
    }
}
