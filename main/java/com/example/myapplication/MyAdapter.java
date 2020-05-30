package com.example.myapplication;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.storage.StorageManager;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> implements Filterable {
    RecyclerView recyclerView;
    Context context;
    ArrayList<String> items = new ArrayList<>();
    String Title = "", ForClass = "";
    ArrayList<String> uri = new ArrayList<>();
    ArrayList<String> uriFull = new ArrayList<>();
    ArrayList<String> itemsFull= new ArrayList<>() ;


    public void update(String name, String url) {
        items.add(name);
        itemsFull.add(name);
        uri.add(url);
        uriFull.add(url);
        notifyDataSetChanged();// refresh recyler view
    }



    public MyAdapter(RecyclerView recyclerView, Context context, ArrayList<String> items, ArrayList<String> uri, String Title, String ForClass) {
        this.recyclerView = recyclerView;
        this.context = context;
        this.items = items;
        this.uri = uri;
        this.Title = Title;
        this.ForClass = ForClass;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String fname= items.get(position);
        fname=fname.substring(0,fname.lastIndexOf("_"));
        fname=fname+"."+extenstion(items.get(position));
        holder.itemfileName.setText(fname);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public  Filter getFilter() {
        return filter;                                                     ///////For searching
    }
private  Filter filter =new Filter() {
    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        ArrayList<String> Filtereditems= new ArrayList<>();
        ArrayList<String> Filtereduri= new ArrayList<>();
        uri.clear();
        if(constraint == null|| constraint.length()==0)
        {
            Filtereditems.addAll(itemsFull);
            uri.addAll(uriFull);

        }
        else {
            String filterword = constraint.toString().toLowerCase().trim();

            for(int i=0;i<itemsFull.size();i++)
            {
                if(itemsFull.get(i).toLowerCase().contains(filterword)) {
                    Filtereditems.add(itemsFull.get(i));
                    uri.add(uriFull.get(i));
                }

            }

        }

        FilterResults results = new FilterResults();
        results.values= Filtereditems;
        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        items.clear();
        items.addAll((ArrayList<String>) results.values);
        notifyDataSetChanged();
    }
};
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView itemfileName;

        public ViewHolder(final View itemView) {    // repres idv items view
            super(itemView);
            itemfileName = itemView.findViewById(R.id.itemfilename);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final int position = recyclerView.getChildAdapterPosition(v);
                    Intent in = new Intent(Intent.ACTION_VIEW);
                    in.setData(Uri.parse(uri.get(position)));

                    Animation shake = AnimationUtils.loadAnimation(context,R.anim.nav_default_pop_enter_anim);
                    itemView.startAnimation(shake);

                    String ext = extenstion(items.get(position));
                    context.startActivity(in);

                    if (ext.equals("pdf") || ext.equals("img") || ext.equals("jpg")) {
                        try {
                            context.startActivity(in);
                        } catch (Exception e) {
                            chooserfun(in);
                        }
                    } else {
                        chooserfun(in);
                    }

                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (!ForClass.equals("ForStudent")) {
                        final int pos = recyclerView.getChildAdapterPosition(v);
                        AlertDialog.Builder dia = new AlertDialog.Builder(context);
                        dia.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                deleteData(items.get(pos), pos);

                            }
                        });
                        dia.setTitle("YOU WANT TO DELETE--" + items.get(pos));
                        dia.show();
                    }
                    return true;
                }
            });
        }
    }

    void chooserfun(Intent in) {
        Intent chooser = Intent.createChooser(in, "OPEN WITH");

        if (in.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(chooser);
        }

    }

    public String extenstion(String filename) {
        String result = filename;

        try {
            int ind = result.lastIndexOf('_');
            result = result.substring(ind + 1);
        } catch (Exception e) {

        }

        return result;
    }

    void deleteData(String filename, int pos) {
        final int poss = pos;
        final String file = filename;


        StorageReference Sref = FirebaseStorage.getInstance().getReferenceFromUrl(uri.get(pos));
        Sref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(context, " DELETED", Toast.LENGTH_SHORT).show();
                DatabaseReference Dref = FirebaseDatabase.getInstance().getReference().child(Title).child(file);
                Dref.removeValue();
                items.remove(poss);
                uri.remove(poss);
                notifyDataSetChanged();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "NOT DELETED", Toast.LENGTH_SHORT).show();
            }
        });
    }

}