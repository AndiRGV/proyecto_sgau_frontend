package com.umg.sgauapp.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.umg.sgauapp.R;
import com.umg.sgauapp.data.ModuleConfig;
import java.util.List;

public class ModuleAdapter extends RecyclerView.Adapter<ModuleAdapter.Holder> {
    public interface OnModuleClick { void onClick(ModuleConfig module); }
    private final List<ModuleConfig> items;
    private final OnModuleClick listener;
    public ModuleAdapter(List<ModuleConfig> items, OnModuleClick listener) { this.items=items; this.listener=listener; }
    @NonNull @Override public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_module, parent, false));
    }
    @Override public void onBindViewHolder(@NonNull Holder h, int position) {
        ModuleConfig item=items.get(position); h.title.setText(item.title); h.subtitle.setText("Consultar · Crear · Editar · Eliminar");
        h.itemView.setOnClickListener(v -> listener.onClick(item));
    }
    @Override public int getItemCount() { return items.size(); }
    static class Holder extends RecyclerView.ViewHolder {
        TextView title, subtitle;
        Holder(View v) { super(v); title=v.findViewById(R.id.txtModuleTitle); subtitle=v.findViewById(R.id.txtModuleSubtitle); }
    }
}
