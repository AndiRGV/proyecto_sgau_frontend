package com.umg.sgauapp.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.google.gson.JsonObject;
import com.umg.sgauapp.R;
import com.umg.sgauapp.data.ModuleConfig;
import java.util.ArrayList;
import java.util.List;

public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.Holder> {

    public interface Listener {
        void edit(JsonObject item);
        void delete(JsonObject item);
    }

    private final List<JsonObject> items = new ArrayList<>();
    private final ModuleConfig module;
    private final Listener listener;

    public RecordAdapter(ModuleConfig module, Listener listener) {
        this.module = module;
        this.listener = listener;
    }

    public void setItems(List<JsonObject> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_record, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder h, int pos) {
        JsonObject item = items.get(pos);
        String title = "Registro #" + value(item, "id");

        String[] preferredKeys = new String[]{
                "nombre", "username", "codigoEstudiante",
                "codigoDocente", "codigo", "mes", "cicloAcademico"
        };

        for (String key : preferredKeys) {
            if (item.has(key) && !item.get(key).isJsonNull() && !item.get(key).getAsString().isEmpty()) {
                title = item.get(key).getAsString();
                break;
            }
        }
        h.title.setText(title);

        StringBuilder detail = new StringBuilder();
        for (String key : module.fields) {
            if (item.has(key) && !item.get(key).isJsonNull() && !key.equals("password")) {
                if (detail.length() > 0) {
                    detail.append(" · ");
                }
                int fieldIndex = module.fields.indexOf(key);
                detail.append(module.labels.get(fieldIndex))
                        .append(": ")
                        .append(item.get(key).getAsString());
            }
        }

        h.detail.setText(detail.length() == 0 ? "Sin detalles adicionales" : detail.toString());
        h.edit.setOnClickListener(v -> listener.edit(item));
        h.delete.setOnClickListener(v -> listener.delete(item));
    }

    private String value(JsonObject o, String k) {
        return o.has(k) && !o.get(k).isJsonNull() ? o.get(k).getAsString() : "?";
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class Holder extends RecyclerView.ViewHolder {
        TextView title;
        TextView detail;
        MaterialButton edit;
        MaterialButton delete;

        Holder(View v) {
            super(v);
            title = v.findViewById(R.id.txtRecordTitle);
            detail = v.findViewById(R.id.txtRecordDetail);
            edit = v.findViewById(R.id.btnEdit);
            delete = v.findViewById(R.id.btnDelete);
        }
    }
}
