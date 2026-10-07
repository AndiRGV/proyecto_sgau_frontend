package com.umg.sgauapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.umg.sgauapp.R;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText email;
    private TextInputEditText password;
    private TextView info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.edtEmail);
        password = findViewById(R.id.edtPassword);
        info = findViewById(R.id.txtLoginInfo);

        Button login = findViewById(R.id.btnLogin);
        Button register = findViewById(R.id.btnRegister);
        Button guest = findViewById(R.id.btnGuest);

        boolean configured = !FirebaseApp.getApps(this).isEmpty();
        if (!configured) {
            info.setText("Puedes entrar como invitado");
        }

        login.setOnClickListener(v -> auth(false));
        register.setOnClickListener(v -> auth(true));
        guest.setOnClickListener(v -> openHome());

        if (configured && FirebaseAuth.getInstance().getCurrentUser() != null) {
            openHome();
        }
    }

    private void auth(boolean create) {
        if (FirebaseApp.getApps(this).isEmpty()) {
            Toast.makeText(this, "ERROR", Toast.LENGTH_LONG).show();
            return;
        }

        String e = email.getText() == null ? "" : email.getText().toString().trim();
        String p = password.getText() == null ? "" : password.getText().toString();

        if (e.isEmpty() || p.length() < 6) {
            Toast.makeText(this, "Escribe un correo y una contraseña de al menos 6 caracteres", Toast.LENGTH_LONG).show();
            return;
        }

        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (create) {
            auth.createUserWithEmailAndPassword(e, p).addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    openHome();
                } else {
                    String errorMsg = task.getException() == null ? "revisa los datos" : task.getException().getLocalizedMessage();
                    Toast.makeText(this, "No se pudo crear la cuenta: " + errorMsg, Toast.LENGTH_LONG).show();
                }
            });
        } else {
            auth.signInWithEmailAndPassword(e, p).addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    openHome();
                } else {
                    String errorMsg = task.getException() == null ? "revisa los datos" : task.getException().getLocalizedMessage();
                    Toast.makeText(this, "No se pudo ingresar: " + errorMsg, Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void openHome() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}