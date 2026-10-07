package com.umg.sgauapp.data;

import java.util.Arrays;
import java.util.List;

public class ModuleConfig {
    public final String title;
    public final String endpoint;
    public final List<String> fields;
    public final List<String> labels;
    public ModuleConfig(String title, String endpoint, List<String> fields, List<String> labels) {
        this.title = title; this.endpoint = endpoint; this.fields = fields; this.labels = labels;
    }
    private static ModuleConfig m(String title, String endpoint, String... pairs) {
        java.util.ArrayList<String> fields = new java.util.ArrayList<>();
        java.util.ArrayList<String> labels = new java.util.ArrayList<>();
        for (int i=0; i<pairs.length; i+=2) { fields.add(pairs[i]); labels.add(pairs[i+1]); }
        return new ModuleConfig(title, endpoint, fields, labels);
    }
    public static List<ModuleConfig> all() {
        return Arrays.asList(
            m("Usuarios", "usuarios", "username","Usuario", "password","Contraseña", "email","Correo", "nombre","Nombre", "apellido","Apellido", "activo","Activo (true/false)"),
            m("Estudiantes", "estudiantes", "usuarioId","ID de usuario", "codigoEstudiante","Código de estudiante", "telefono","Teléfono", "direccion","Dirección", "fechaNacimiento","Fecha de nacimiento (AAAA-MM-DD)"),
            m("Docentes", "docentes", "usuarioId","ID de usuario", "codigoDocente","Código docente", "especialidad","Especialidad", "telefono","Teléfono"),
            m("Carreras", "carreras", "nombre","Nombre", "descripcion","Descripción", "duracionAnios","Duración en años", "activa","Activa (true/false)"),
            m("Cursos", "cursos", "carreraId","ID de carrera", "codigo","Código", "nombre","Nombre", "descripcion","Descripción", "creditos","Créditos", "docenteId","ID de docente"),
            m("Inscripciones", "inscripciones", "estudianteId","ID de estudiante", "carreraId","ID de carrera", "cicloAcademico","Ciclo académico", "fechaInscripcion","Fecha de inscripción (AAAA-MM-DD)", "estado","Estado"),
            m("Colegiaturas", "colegiaturas", "inscripcionId","ID de inscripción", "mes","Mes", "monto","Monto", "fechaVencimiento","Fecha de vencimiento (AAAA-MM-DD)", "fechaPago","Fecha de pago (opcional, AAAA-MM-DD)", "estado","Estado"),
            m("Notas", "notas", "inscripcionId","ID de inscripción", "cursoId","ID de curso", "docenteId","ID de docente", "calificacion","Calificación", "periodo","Período", "observacion","Observación")
        );
    }
    public static ModuleConfig byEndpoint(String endpoint) {
        for (ModuleConfig m : all()) if (m.endpoint.equals(endpoint)) return m;
        return all().get(0);
    }
}
