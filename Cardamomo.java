import java.time.LocalDate;

public class Cardamomo extends LotePerecedero {
    private double porcentajeGranoVerde;

    public Cardamomo(String codigo, String productor, LocalDate fechaIngreso, double pesoRecibido, Calidad calidad, double porcentajeGranoVerde) {
        super(codigo, productor, fechaIngreso, pesoRecibido, calidad, 180);
        if (porcentajeGranoVerde < 0.0 || porcentajeGranoVerde > 100.0) {
            throw new IllegalArgumentException("El porcentaje de grano verde debe estar entre 0 y 100.");
        }
        this.porcentajeGranoVerde = porcentajeGranoVerde;
    }

    @Override
    public double calcularPagoProductor() {
        double precioKg;
        if (getCalidad() == Calidad.A) {
            precioKg = 60.0;
        } else if (getCalidad() == Calidad.B) {
            precioKg = 48.0;
        } else {
            precioKg = 35.0;
        }
        double pago = getPesoRecibido() * precioKg;
        if (porcentajeGranoVerde >= 85.0) {
            pago += pago * 0.10;
        }
        return pago;
    }

    @Override
    public String obtenerCategoria() {
        return "Cardamomo";
    }

    public double getPorcentajeGranoVerde() {
        return porcentajeGranoVerde;
    }
}
