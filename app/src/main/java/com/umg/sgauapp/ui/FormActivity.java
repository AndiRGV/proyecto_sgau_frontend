package com.umg.sgauapp.ui;
import android.os.Bundle;
import android.text.InputType;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.umg.sgauapp.R;
import com.umg.sgauapp.data.ApiService;
import com.umg.sgauapp.data.ModuleConfig;
import com.umg.sgauapp.data.RetrofitClient;

import java.util.LinkedHashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FormActivity extends AppCompatActivity {

    private ModuleConfig module;
    private ApiService api;
    private JsonObject record;
    private LinearLayout container;
    private final Map<String, TextInputEditText> inputs = new LinkedHashMap<>();

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_form);

        api = RetrofitClient.getApi();
        module = ModuleConfig.byEndpoint(getIntent().getStringExtra("endpoint"));

        String json = getIntent().getStringExtra("record");
        if (json != null) {
            record = JsonParser.parseString(json).getAsJsonObject();
        }

        ((TextView) findViewById(R.id.txtFormTitle))
                .setText((record == null ? "Nuevo " : "Editar ") + module.title);

        container = findViewById(R.id.formFields);
        findViewById(R.id.btnFormBack).setOnClickListener(v -> finish());

        for (int i = 0; i < module.fields.size(); i++) {
            String key = module.fields.get(i);
            if (key.equals("password") && record != null) {
                continue;
            }

            String label = module.labels.get(i);
            TextInputLayout layout = new TextInputLayout(this);
            layout.setHint(label);
            layout.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));

            TextInputEditText edit = new TextInputEditText(this);
            edit.setSingleLine(false);
            edit.setInputType(inputType(key));

            if (record != null && record.has(key) && !record.get(key).isJsonNull()) {
                edit.setText(record.get(key).getAsString());
            }

            layout.addView(edit);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            lp.bottomMargin = 10;
            layout.setLayoutParams(lp);

            container.addView(layout);
            inputs.put(key, edit);
        }

        MaterialButton save = findViewById(R.id.btnSave);
        save.setText(record == null ? "Guardar registro" : "Guardar cambios");
        save.setOnClickListener(v -> save());
    }

    private int inputType(String key) {
        if (key.toLowerCase().contains("password")) {
            return InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD;
        }
        if (key.toLowerCase().contains("fecha")) {
            return InputType.TYPE_CLASS_DATETIME | InputType.TYPE_DATETIME_VARIATION_DATE;
        }
        if (key.toLowerCase().contains("email")) {
            return InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS;
        }
        if (key.toLowerCase().contains("telefono")) {
            return InputType.TYPE_CLASS_PHONE;
        }
        if (key.toLowerCase().contains("monto") || key.toLowerCase().contains("calificacion") ||
                key.toLowerCase().contains("duracion") || key.toLowerCase().contains("creditos") ||
                key.toLowerCase().endsWith("id")) {
            return InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL;
        }
        return InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES;
    }

    private void save() {
        JsonObject body = new JsonObject();
        for (Map.Entry<String, TextInputEditText> entry : inputs.entrySet()) {
            String value = entry.getValue().getText() == null ? "" : entry.getValue().getText().toString().trim();
            if (value.isEmpty()) {
                body.add(entry.getKey(), com.google.gson.JsonNull.INSTANCE);
                continue;
            }

            String key = entry.getKey();
            try {
                if (key.endsWith("Id") || key.equals("duracionAnios") || key.equals("creditos")) {
                    body.addProperty(key, Long.parseLong(value));
                } else if (key.equals("monto") || key.equals("calificacion")) {
                    body.addProperty(key, Double.parseDouble(value));
                } else if (key.equals("activo") || key.equals("activa")) {
                    body.addProperty(key, Boolean.parseBoolean(value));
                } else {
                    body.addProperty(key, value);
                }
            } catch (Exception ex) {
                Toast.makeText(this, "Revisa el formato del campo: " + key, Toast.LENGTH_LONG).show();
                return;
            }
        }

        MaterialButton button = findViewById(R.id.btnSave);
        button.setEnabled(false);

        Call<JsonElement> call;
        if (record == null) {
            call = api.crear("api/" + module.endpoint, body);
        } else {
            if (record.has("id") && !record.get("id").isJsonNull()) {
                body.add("id", record.get("id"));
            }
            call = api.actualizar("api/" + module.endpoint + "/" + record.get("id").getAsString(), body);
        }

        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> c, Response<JsonElement> r) {
                button.setEnabled(true);
                if (r.isSuccessful()) {
                    Toast.makeText(FormActivity.this, "Registro guardado correctamente", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(FormActivity.this, "No se pudo guardar (HTTP " + r.code() + "). Error.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<JsonElement> c, Throwable t) {
                button.setEnabled(true);
                Toast.makeText(FormActivity.this, "Error de conexión: " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}