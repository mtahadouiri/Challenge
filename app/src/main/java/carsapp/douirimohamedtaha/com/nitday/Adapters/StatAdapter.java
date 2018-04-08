package carsapp.douirimohamedtaha.com.nitday.Adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import carsapp.douirimohamedtaha.com.nitday.Entities.Ticket;
import carsapp.douirimohamedtaha.com.nitday.R;

/**
 * Created by PC on 08/04/2018.
 */

public class StatAdapter extends RecyclerView.Adapter<StatAdapter.ViewHolder> {

    private static final String IMAGE_SERVER_ADRESS = "http://192.168.1.211:8081/uploads/";
    private List<Ticket> items;
    private Context mContext;
    private boolean a;

    public StatAdapter(List<Ticket> items, Context context) {
        this.items = items;
        this.mContext = context;
    }

    @Override
    public StatAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.one_item_pleinte, parent, false);
        return new StatAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(StatAdapter.ViewHolder holder, int position) {
        Ticket item = items.get(position);
        holder.name.setText(item.getDescription());
        holder.details.setText(item.getDate());
        if (item.getEtat().contains("1")) {
            holder.time.setTextColor(ContextCompat.getColor(mContext, R.color.green_primary));
            holder.time.setText("Traitement en cours");

        } else{
            holder.time.setTextColor(ContextCompat.getColor(mContext, R.color.orange_primary));
            holder.time.setText("En attente");
        }
        Picasso.get().load(IMAGE_SERVER_ADRESS+item.getPic()).into(holder.image);

    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    public void add(Ticket item, int position) {
        items.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(Ticket item) {
        int position = items.indexOf(item);
        items.remove(position);
        notifyItemRemoved(position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public ImageView done;
        public TextView name;
        public TextView details;
        public TextView time;


        public ViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.imgTicket);
            done = (ImageView) itemView.findViewById(R.id.imgCall);
            name = (TextView) itemView.findViewById(R.id.txtDesc);
            details = (TextView) itemView.findViewById(R.id.txtDate);
            time = (TextView) itemView.findViewById(R.id.txtStat);
        }
    }
}
