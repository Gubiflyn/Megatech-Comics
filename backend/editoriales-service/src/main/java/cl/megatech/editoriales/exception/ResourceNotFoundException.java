package cl.megatech.editoriales.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String recurso, Long id) {
        super(recurso + " no encontrado con ID: " + id);
    }
}