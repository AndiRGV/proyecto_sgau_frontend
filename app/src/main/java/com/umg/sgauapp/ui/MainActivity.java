package com.umg.sgauapp.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.umg.sgauapp.R;
import com.umg.sgauapp.data.ModuleConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recycler = findViewById(R.id.recyclerModules);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        recycler.setAdapter(new ModuleAdapter(ModuleConfig.all(), module -> {
            Intent intent = new Intent(this, ListActivity.class);
            intent.putExtra("endpoint", module.endpoint);
            startActivity(intent);
        }));

        MaterialButton logout = findViewById(R.id.btnLogout);
        logout.setOnClickListener(v -> {
            if (!FirebaseApp.getApps(this).isEmpty()) {
                FirebaseAuth.getInstance().signOut();
            }
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        // Verificamos si hay actualizaciones al iniciar la pantalla principal
        verificarActualizacion();
    }

    private void verificarActualizacion() {
        FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(0)
                .build();
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);

        android.util.Log.d("UpdateCheck", "Iniciando peticion a Firebase Remote Config...");

        mFirebaseRemoteConfig.fetchAndActivate().addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                String latestVersion = mFirebaseRemoteConfig.getString("latest_version");
                String releaseUrl = mFirebaseRemoteConfig.getString("release_url");
                String localVersion = com.umg.sgauapp.BuildConfig.VERSION_NAME;

                android.util.Log.d("UpdateCheck", "Fetch exitoso. Firebase: " + latestVersion + " | Local: " + localVersion);

                if (!latestVersion.isEmpty() && !latestVersion.equals(localVersion)) {
                    android.util.Log.d("UpdateCheck", "Versiones diferentes. Mostrando dialogo...");
                    mostrarDialogoActualizacion(releaseUrl);
                } else {
                    android.util.Log.d("UpdateCheck", "Las versiones coinciden o estan vacias.");
                }
            } else {
                android.util.Log.d("UpdateCheck", "Error en fetchAndActivate de Firebase.");
            }
        });
    }

    private void mostrarDialogoActualizacion(String url) {
        new AlertDialog.Builder(this)
                .setTitle("¡Nueva actualización disponible!")
                .setMessage("Hay una nueva versión de la aplicación disponible en GitHub. Por favor, actualiza para continuar.")
                .setCancelable(false)
                .setPositiveButton("Actualizar", (dialog, which) -> {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                })
                .show();
    }
}