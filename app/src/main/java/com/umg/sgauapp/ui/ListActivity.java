package com.umg.sgauapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.umg.sgauapp.R;
import com.umg.sgauapp.data.ApiService;
import com.umg.sgauapp.data.ModuleConfig;
import com.umg.sgauapp.data.RetrofitClient;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListActivity extends AppCompatActivity {

    private ModuleConfig module;
    private ApiService api;
    private RecordAdapter adapter;
    private TextView status;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_list);

        module = ModuleConfig.byEndpoint(getIntent().getStringExtra("endpoint"));
        api = RetrofitClient.getApi();

        ((TextView) findViewById(R.id.txtTitle)).setText(module.title);
        status = findViewById(R.id.txtStatus);

        RecyclerView recycler = findViewById(R.id.recyclerRecords);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        adapter = new RecordAdapter(module, new RecordAdapter.Listener() {
            @Override
            public void edit(JsonObject item) {
                openForm(item);
            }

            @Override
            public void delete(JsonObject item) {
                confirmDelete(item);
            }
        });
        recycler.setAdapter(adapter);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnRefresh).setOnClickListener(v -> load());
        findViewById(R.id.btnAdd).setOnClickListener(v -> openForm(null));

        // Nota: load() se ejecuta también en onResume() al iniciar la pantalla
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null) {
            load();
        }
    }

    private void load() {
        status.setText("Consultando /api/" + module.endpoint + " ...");
        api.listar("api/" + module.endpoint).enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> c, Response<JsonElement> r) {
                if (!r.isSuccessful() || r.body() == null) {
                    status.setText("Error HTTP " + r.code() + ". Verifica que Spring Boot esté activo y la URL sea correcta.");
                    return;
                }

                JsonElement body = r.body();
                JsonArray arr = body.isJsonArray() ? body.getAsJsonArray() : new JsonArray();
                List<JsonObject> list = new ArrayList<>();

                for (JsonElement e : arr) {
                    if (e.isJsonObject()) {
                        list.add(e.getAsJsonObject());
                    }
                }

                adapter.setItems(list);
                status.setText("Registros encontrados: " + list.size());
            }

            @Override
            public void onFailure(Call<JsonElement> c, Throwable t) {
                status.setText("No se pudo conectar: " + t.getLocalizedMessage() + ". Revisa BASE_URL y la API.");
            }
        });
    }

    private void openForm(JsonObject item) {
        Intent i = new Intent(this, FormActivity.class);
        i.putExtra("endpoint", module.endpoint);
        if (item != null) {
            i.putExtra("record", item.toString());
        }
        startActivity(i);
    }

    private void confirmDelete(JsonObject item) {
        String id = item.has("id") && !item.get("id").isJsonNull() ? item.get("id").getAsString() : null;
        if (id == null) {
            Toast.makeText(this, "El registro no tiene ID", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Eliminar registro")
                .setMessage("¿Seguro que deseas eliminar el registro #" + id + "?")
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Eliminar", (d, w) ->
                        api.eliminar("api/" + module.endpoint + "/" + id).enqueue(new Callback<JsonElement>() {
                            @Override
                            public void onResponse(Call<JsonElement> c, Response<JsonElement> r) {
                                if (r.isSuccessful()) {
                                    Toast.makeText(ListActivity.this, "Registro eliminado", Toast.LENGTH_SHORT).show();
                                    load();
                                } else {
                                    Toast.makeText(ListActivity.this, "No se pudo eliminar (HTTP " + r.code() + ")", Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<JsonElement> c, Throwable t) {
                                Toast.makeText(ListActivity.this, "Error de conexión: " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                            }
                        })
                )
                .show();
    }
}