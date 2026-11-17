import java.time.LocalDate;

public interface Perecedero {
    LocalDate obtenerFechaVencimiento();
    boolean estaVencido(LocalDate fechaOperacion);
}
