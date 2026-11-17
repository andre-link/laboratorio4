import java.time.LocalDate;

public class CafePergamino extends Lote {
    private double humedad;

    public CafePergamino(String codigo, String productor, LocalDate fechaIngreso, double pesoRecibido, Calidad calidad, double humedad) {
        super(codigo, productor, fechaIngreso, pesoRecibido, calidad);
        if (humedad < 8.0 || humedad > 14.0) {
            throw new IllegalArgumentException("La humedad del cafe debe estar entre 8% y 14%.");
        }
        this.humedad = humedad;
    }

    protected double calcularPagoBaseCafe() {
        double precioKg;
        if (getCalidad() == Calidad.A) {
            precioKg = 12.0;
        } else if (getCalidad() == Calidad.B) {
            precioKg = 9.0;
        } else {
            precioKg = 6.0;
        }
        double pagoBruto = getPesoRecibido() * precioKg;
        if (humedad > 12.0) {
            int puntosExceso = (int) Math.ceil(humedad - 12.0);
            double porcentajeDescuento = puntosExceso * 0.03;
            pagoBruto -= pagoBruto * porcentajeDescuento;
        }
        return pagoBruto;
    }

    @Override
    public double calcularPagoProductor() {
        return calcularPagoBaseCafe();
    }

    @Override
    public String obtenerCategoria() {
        return "Cafe en pergamino";
    }

    @Override
    public boolean esPerecedero() {
        return false;
    }

    @Override
    public LocalDate obtenerFechaCriterioOrden() {
        return getFechaIngreso();
    }

    public double getHumedad() {
        return humedad;
    }
}
