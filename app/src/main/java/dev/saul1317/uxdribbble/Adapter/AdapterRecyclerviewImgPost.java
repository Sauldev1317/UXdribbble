package dev.saul1317.uxdribbble.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import dev.saul1317.uxdribbble.R;
import dev.saul1317.uxdribbble.model.Gallery;

public class AdapterRecyclerviewImgPost extends RecyclerView.Adapter<AdapterRecyclerviewImgPost.AdapterRecyclerviewImgPostHolder>{

    private int resource;
    private List<Gallery> img_url;
    private Context context;
    private ImgPostOnItemClickListener imgPostOnItemClickListener;


    public AdapterRecyclerviewImgPost(int resource, List<Gallery> img_url, Context context, ImgPostOnItemClickListener imgPostOnItemClickListener) {
        this.resource = resource;
        this.img_url = img_url;
        this.context = context;
        this.imgPostOnItemClickListener = imgPostOnItemClickListener;
    }

    @NonNull
    @Override
    public AdapterRecyclerviewImgPostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(resource, parent,false);
        return new AdapterRecyclerviewImgPostHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterRecyclerviewImgPostHolder holder, int position) {
        final Gallery gallery = img_url.get(position);
        Picasso.get().load(gallery.getUrl()).into(holder.cardview_img_post);
        holder.cardview_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imgPostOnItemClickListener != null) {
                    imgPostOnItemClickListener.OnImageClickListener(gallery, view);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return img_url.size();
    }

    public class AdapterRecyclerviewImgPostHolder extends RecyclerView.ViewHolder{

        CardView cardview_post;
        ImageView cardview_img_post;


        public AdapterRecyclerviewImgPostHolder(View itemView) {
            super(itemView);
            cardview_post = (CardView) itemView.findViewById(R.id.cardview_post);
            cardview_img_post = (ImageView) itemView.findViewById(R.id.cardview_img_post);
        }
    }
}
