import java.time.LocalDate;

public class QuesoFresco extends LotePerecedero {
    private double porcentajeGrasa;

    public QuesoFresco(String codigo, String productor, LocalDate fechaIngreso, double pesoRecibido, Calidad calidad, double porcentajeGrasa) {
        super(codigo, productor, fechaIngreso, pesoRecibido, calidad, 21);
        if (calidad == Calidad.C) {
            throw new IllegalArgumentException("No se acepta queso fresco de calidad C.");
        }
        if (porcentajeGrasa < 10.0 || porcentajeGrasa > 40.0) {
            throw new IllegalArgumentException("El porcentaje de grasa debe estar entre 10% y 40%.");
        }
        this.porcentajeGrasa = porcentajeGrasa;
    }

    @Override
    public double calcularPagoProductor() {
        double precioKg = (getCalidad() == Calidad.A) ? 38.0 : 30.0;
        return ( getPesoRecibido() * precioKg ) - ( getPesoRecibido() * 1.50 );
    }

    @Override
    public String obtenerCategoria() {
        return "Queso fresco";
    }

    @Override
    public boolean requiereCamaraFria() {
        return true;
    }

    public double getPorcentajeGrasa() {
        return porcentajeGrasa;
    }
}
