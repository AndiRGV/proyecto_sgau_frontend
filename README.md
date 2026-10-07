# SGAU Android — Frontend Java/XML

Proyecto Android Studio en **Java**, vistas XML, Retrofit, Gson, RecyclerView y Adapter. Está preparado para consumir el backend Spring Boot del proyecto `SistemaGestionEducativa` mediante estos endpoints:

- `/api/usuarios`
- `/api/estudiantes`
- `/api/docentes`
- `/api/carreras`
- `/api/cursos`
- `/api/inscripciones`
- `/api/colegiaturas`
- `/api/notas`

Cada módulo incluye consulta/listado, formulario de creación, actualización y eliminación. Los formularios se construyen desde la configuración de campos de `ModuleConfig.java`. La navegación es Login → Panel de módulos → Listado → Formulario.

## Abrir en Android Studio
1. Extrae `SGAUAndroid.zip`.
2. En Android Studio elige **Open** y selecciona la carpeta `SGAUAndroid` (la que contiene `settings.gradle`).
3. Usa JDK 17 para Gradle y acepta sincronizar las dependencias.
4. Ejecuta con un emulador Android.

## Conectar al backend
El proyecto ya viene configurado con Retrofit para usar el backend publicado en Google Cloud Run:

`https://sgau-backend-api-188936370478.northamerica-northeast1.run.app/`

Retrofit utiliza esta URL como `BASE_URL`. La base de datos Neon/PostgreSQL no se conecta directamente desde Android. El flujo correcto es:

**Android Studio → Retrofit → API Spring Boot en Cloud Run → Neon/PostgreSQL**

La base de datos (incluida Neon/PostgreSQL si eso es a lo que te refieres con “Neo”) **no se conecta directamente desde Android**. El flujo correcto es Android → Retrofit → API Spring Boot publicada → PostgreSQL/Neon. No pongas las credenciales de la base de datos dentro de la app.

El backend ya está configurado para utilizar la API pública de Google Cloud Run indicada en `ApiConfig.java`. No necesitas ejecutar el backend localmente para probar Retrofit, siempre que el servicio de Cloud Run esté disponible. Para probar las rutas, consulta Swagger del backend si está habilitado.

## Firebase Authentication (integración preparada)
La pantalla de acceso incluye registro e inicio de sesión por correo/contraseña con Firebase Authentication. Firebase necesita una configuración propia de tu proyecto y no se puede incluir una configuración válida sin acceso a tu cuenta:
1. Crea un proyecto en Firebase Console.
2. Registra la app Android con el package `com.umg.sgauapp`.
3. Descarga `google-services.json` y cópialo en `app/google-services.json`.
4. En el `build.gradle` raíz agrega dentro de `plugins`: `id 'com.google.gms.google-services' version '4.4.2' apply false`. En `app/build.gradle`, agrega dentro de `plugins`: `id 'com.google.gms.google-services'`.
5. En Firebase Console → Authentication → Sign-in method, habilita **Email/Password**.
6. Sincroniza Gradle y vuelve a ejecutar.

Hasta que se configure Firebase, se puede usar **Continuar como invitado** para abrir el panel y probar la conexión con la API. Esto es un modo de demostración, no un mecanismo de autenticación seguro.

## Notas de integración
- Las rutas y nombres de campos corresponden al backend Spring Boot entregado previamente.
- Si cambias el puerto, el dominio o los endpoints del backend, actualiza `ApiConfig.java` o `ModuleConfig.java`.
- Los campos de fecha usan formato `AAAA-MM-DD`; los campos de relación reciben IDs numéricos existentes.
- El backend actual expone entidades y puede exigir campos/relaciones válidas. Si una operación devuelve HTTP 400/500, revisa el mensaje del backend y el esquema SQL.
- No se incluyó `google-services.json` porque es específico de tu proyecto Firebase.
