import java.time.LocalDate;

public abstract class Lote implements Comparable<Lote>, Refrigerable {
    private String codigo;
    private String productor;
    private LocalDate fechaIngreso;
    private double pesoRecibido;
    private double pesoDisponible;
    private Calidad calidad;
    private EstadoLote estado;

    public Lote(String codigo, String productor, LocalDate fechaIngreso, double pesoRecibido, Calidad calidad) {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new IllegalArgumentException("El codigo no puede estar vacio.");
        }
        if (productor == null || productor.trim().isEmpty()) {
            throw new IllegalArgumentException("El productor no puede estar vacio.");
        }
        if (fechaIngreso == null) {
            throw new IllegalArgumentException("La fecha de ingreso es obligatoria.");
        }
        if (pesoRecibido <= 0) {
            throw new IllegalArgumentException("El peso recibido debe ser mayor a cero.");
        }
        if (calidad == null) {
            throw new IllegalArgumentException("La calidad es obligatoria.");
        }
        this.codigo = codigo.trim();
        this.productor = productor.trim();
        this.fechaIngreso = fechaIngreso;
        this.pesoRecibido = pesoRecibido;
        this.pesoDisponible = pesoRecibido;
        this.calidad = calidad;
        this.estado = EstadoLote.DISPONIBLE;
    }

    public abstract double calcularPagoProductor();
    public abstract String obtenerCategoria();
    public abstract boolean esPerecedero();
    public abstract LocalDate obtenerFechaCriterioOrden();

    public double calcularCostoPorKg() {
        return calcularPagoProductor() / pesoRecibido;
    }

    public double descontarPeso(double cantidad) {
        if (cantidad <= 0 || estado != EstadoLote.DISPONIBLE) {
            return 0;
        }
        double tomado = Math.min(pesoDisponible, cantidad);
        pesoDisponible -= tomado;
        if (pesoDisponible <= 1e-9) {
            pesoDisponible = 0;
            estado = EstadoLote.AGOTADO;
        }
        return tomado;
    }

    public double descartarSiVencido(LocalDate fechaOperacion) {
        return 0;
    }

    protected double procesarDescarte() {
        double perdida = pesoDisponible * calcularCostoPorKg();
        pesoDisponible = 0;
        estado = EstadoLote.DESCARTADO;
        return perdida;
    }

    public boolean venceEnProximosDias(LocalDate fechaOperacion, int dias) {
        return false;
    }

    public String obtenerTextoVencimiento() {
        return "No vence";
    }

    @Override
    public boolean requiereCamaraFria() {
        return false;
    }

    @Override
    public int compareTo(Lote otro) {
        if (this.esPerecedero() != otro.esPerecedero()) {
            return this.esPerecedero() ? -1 : 1;
        }
        int cmpFecha = this.obtenerFechaCriterioOrden().compareTo(otro.obtenerFechaCriterioOrden());
        if (cmpFecha != 0) {
            return cmpFecha;
        }
        int cmpCalidad = this.calidad.compareTo(otro.calidad);
        if (cmpCalidad != 0) {
            return cmpCalidad;
        }
        return this.codigo.compareTo(otro.codigo);
    }

    public String getCodigo() {
        return codigo;
    }

    public String getProductor() {
        return productor;
    }

    public LocalDate getFechaIngreso() {
        return fechaIngreso;
    }

    public double getPesoRecibido() {
        return pesoRecibido;
    }

    public double getPesoDisponible() {
        return pesoDisponible;
    }

    public Calidad getCalidad() {
        return calidad;
    }

    public EstadoLote getEstado() {
        return estado;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s | Prod: %s | Cal: %s | Ingreso: %s | Vence: %s | Disp: %.2f/%.2f kg | Costo/kg: Q%.2f | Estado: %s",
                codigo, obtenerCategoria(), productor, calidad, fechaIngreso, obtenerTextoVencimiento(),
                pesoDisponible, pesoRecibido, calcularCostoPorKg(), estado);
    }
}
