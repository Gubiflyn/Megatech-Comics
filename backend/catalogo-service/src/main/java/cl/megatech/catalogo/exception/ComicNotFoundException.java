package cl.megatech.catalogo.exception;

public class ComicNotFoundException extends RuntimeException {

    public ComicNotFoundException(Long id) {
        super("Comic no encontrado con ID: " + id);
    }
}