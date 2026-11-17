import java.time.LocalDate;

public class CafeOrganico extends CafePergamino {
    private String entidadCertificadora;
    private LocalDate fechaValidezSello;

    public CafeOrganico(String codigo, String productor, LocalDate fechaIngreso, double pesoRecibido, Calidad calidad, double humedad, String entidadCertificadora, LocalDate fechaValidezSello) {
        super(codigo, productor, fechaIngreso, pesoRecibido, calidad, humedad);
        if (entidadCertificadora == null || entidadCertificadora.trim().isEmpty()) {
            throw new IllegalArgumentException("La entidad certificadora no puede estar vacia.");
        }
        if (fechaValidezSello == null || fechaValidezSello.isBefore(fechaIngreso)) {
            throw new IllegalArgumentException("El sello organico debe ser valido en la fecha de ingreso.");
        }
        this.entidadCertificadora = entidadCertificadora.trim();
        this.fechaValidezSello = fechaValidezSello;
    }

    @Override
    public double calcularPagoProductor() {
        return super.calcularPagoBaseCafe() + (getPesoRecibido() * 0.75);
    }

    @Override
    public String obtenerCategoria() {
        return "Cafe organico";
    }

    public String getEntidadCertificadora() {
        return entidadCertificadora;
    }

    public LocalDate getFechaValidezSello() {
        return fechaValidezSello;
    }
}
