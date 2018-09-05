package com.training.crudmakananapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.training.crudmakananapp.R;
import com.training.crudmakananapp.helper.MyConstant;
import com.training.crudmakananapp.model.DataMakananItem;

import java.util.List;




public class ListMakananAdapter extends RecyclerView.Adapter<ListMakananAdapter.ViewHolder> {
    Context c;
    List<DataMakananItem> datamakanan;


    private aksiklikitem clicked;

    public interface aksiklikitem{
        void onItemClick(int position);
    }
    public void setOnClick(aksiklikitem onClick){
        clicked =onClick;
    }


    public ListMakananAdapter(Context c, List<DataMakananItem> listdatamakanan) {
        this.c=c;
        datamakanan =listdatamakanan;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
     View v = LayoutInflater.from(c).inflate(R.layout.tampilanlistmakanan,parent,false);
     ViewHolder holder =new ViewHolder(v);
        return holder;
    }
//isi tampilan  view
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.txtnamamakanan.setText(datamakanan.get(position).getMakanan());
        Picasso.with(c).load(MyConstant.IMAGE_URL+datamakanan.get(position).getFotoMakanan()).
                error(R.drawable.noimage).placeholder(R.drawable.noimage).into(holder.imgmakanan);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clicked.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return datamakanan.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
       ImageView imgmakanan;
       TextView txtnamamakanan;
        public ViewHolder(View itemView) {
            super(itemView);
            imgmakanan =(ImageView)itemView.findViewById(R.id.imgmakanan);
            txtnamamakanan =(TextView) itemView.findViewById(R.id.txtmakanan);

        }
    }
}
