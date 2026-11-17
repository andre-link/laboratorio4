import java.util.Iterator;
import java.util.TreeSet;

public class Bodega implements Iterable<Lote> {
    private String codigo;
    private double capacidadMaxima;
    private boolean camaraFria;
    private TreeSet<Lote> lotes;

    public Bodega(String codigo, double capacidadMaxima, boolean camaraFria) {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new IllegalArgumentException("El codigo de la bodega no puede estar vacio.");
        }
        if (capacidadMaxima <= 0) {
            throw new IllegalArgumentException("La capacidad maxima debe ser positiva.");
        }
        this.codigo = codigo.trim();
        this.capacidadMaxima = capacidadMaxima;
        this.camaraFria = camaraFria;
        this.lotes = new TreeSet<>();
    }

    public double calcularPesoOcupado() {
        double suma = 0;
        for (Lote l : lotes) {
            suma += l.getPesoDisponible();
        }
        return suma;
    }

    public double calcularEspacioDisponible() {
        return capacidadMaxima - calcularPesoOcupado();
    }

    public boolean esCompatible(Lote lote) {
        return this.camaraFria == lote.requiereCamaraFria();
    }

    public boolean puedeRecibir(Lote lote) {
        if (!esCompatible(lote)) {
            return false;
        }
        return (calcularPesoOcupado() + lote.getPesoRecibido()) <= capacidadMaxima + 1e-9;
    }

    public void almacenarLote(Lote lote) {
        if (!esCompatible(lote)) {
            throw new IllegalArgumentException("El lote no es compatible con el tipo de bodega (fria/seca).");
        }
        if (calcularPesoOcupado() + lote.getPesoRecibido() > capacidadMaxima + 1e-9) {
            throw new IllegalArgumentException("El lote excede la capacidad disponible de la bodega.");
        }
        lotes.add(lote);
    }

    public void removerLote(Lote lote) {
        lotes.remove(lote);
    }

    @Override
    public Iterator<Lote> iterator() {
        return lotes.iterator();
    }

    public String getCodigo() {
        return codigo;
    }

    public double getCapacidadMaxima() {
        return capacidadMaxima;
    }

    public boolean isCamaraFria() {
        return camaraFria;
    }

    @Override
    public String toString() {
        return String.format("Bodega [%s] | Tipo: %s | Ocupado: %.2f / %.2f kg",
                codigo, camaraFria ? "Camara Fria" : "Seca", calcularPesoOcupado(), capacidadMaxima);
    }
}
