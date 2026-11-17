import java.time.LocalDate;

public abstract class LotePerecedero extends Lote implements Perecedero {
    private int vidaUtilDias;

    public LotePerecedero(String codigo, String productor, LocalDate fechaIngreso, double pesoRecibido, Calidad calidad, int vidaUtilDias) {
        super(codigo, productor, fechaIngreso, pesoRecibido, calidad);
        if (vidaUtilDias <= 0) {
            throw new IllegalArgumentException("La vida util debe ser positiva.");
        }
        this.vidaUtilDias = vidaUtilDias;
    }

    @Override
    public LocalDate obtenerFechaVencimiento() {
        return getFechaIngreso().plusDays(vidaUtilDias);
    }

    @Override
    public boolean estaVencido(LocalDate fechaOperacion) {
        return fechaOperacion.isAfter(obtenerFechaVencimiento());
    }

    @Override
    public boolean esPerecedero() {
        return true;
    }

    @Override
    public LocalDate obtenerFechaCriterioOrden() {
        return obtenerFechaVencimiento();
    }

    @Override
    public double descartarSiVencido(LocalDate fechaOperacion) {
        if (getEstado() == EstadoLote.DISPONIBLE && estaVencido(fechaOperacion)) {
            return procesarDescarte();
        }
        return 0;
    }

    @Override
    public boolean venceEnProximosDias(LocalDate fechaOperacion, int dias) {
        if (getEstado() != EstadoLote.DISPONIBLE) {
            return false;
        }
        LocalDate venc = obtenerFechaVencimiento();
        LocalDate limite = fechaOperacion.plusDays(dias);
        return !venc.isBefore(fechaOperacion) && !venc.isAfter(limite);
    }

    @Override
    public String obtenerTextoVencimiento() {
        return obtenerFechaVencimiento().toString();
    }

    public int getVidaUtilDias() {
        return vidaUtilDias;
    }
}
