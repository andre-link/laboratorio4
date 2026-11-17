import java.time.LocalDate;

public class Hortaliza extends LotePerecedero {
    public Hortaliza(String codigo, String productor, LocalDate fechaIngreso, double pesoRecibido, Calidad calidad, int vidaUtilDias) {
        super(codigo, productor, fechaIngreso, pesoRecibido, calidad, vidaUtilDias);
        if (vidaUtilDias < 3 || vidaUtilDias > 14) {
            throw new IllegalArgumentException("La vida util de la hortaliza debe estar entre 3 y 14 dias.");
        }
    }

    @Override
    public double calcularPagoProductor() {
        double precioKg;
        if (getCalidad() == Calidad.A) {
            precioKg = 4.0;
        } else if (getCalidad() == Calidad.B) {
            precioKg = 3.0;
        } else {
            precioKg = 2.0;
        }
        return (getPesoRecibido() * 0.95) * precioKg;
    }

    @Override
    public String obtenerCategoria() {
        return "Hortaliza";
    }

    @Override
    public boolean requiereCamaraFria() {
        return true;
    }
}
