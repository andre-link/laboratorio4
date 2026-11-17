import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class Cooperativa {
    private LocalDate fechaOperacion;
    private Map<String, Bodega> bodegas;
    private Set<String> codigosLotesHist;
    private List<Lote> historialLotes;
    private Map<String, Double> pagosPorCategoria;
    private Map<String, Double> cobrosPorCategoria;
    private Map<String, Double> perdidasPorCategoria;

    public static final String[] CATEGORIAS = {
        "Cafe en pergamino",
        "Cafe organico",
        "Cardamomo",
        "Hortaliza",
        "Queso fresco"
    };

    public Cooperativa(LocalDate fechaInicial) {
        this.fechaOperacion = fechaInicial;
        this.bodegas = new LinkedHashMap<>();
        this.codigosLotesHist = new HashSet<>();
        this.historialLotes = new ArrayList<>();
        this.pagosPorCategoria = new LinkedHashMap<>();
        this.cobrosPorCategoria = new LinkedHashMap<>();
        this.perdidasPorCategoria = new LinkedHashMap<>();
        for (String cat : CATEGORIAS) {
            pagosPorCategoria.put(cat, 0.0);
            cobrosPorCategoria.put(cat, 0.0);
            perdidasPorCategoria.put(cat, 0.0);
        }
    }

    public void registrarBodega(Bodega bodega) {
        if (bodegas.containsKey(bodega.getCodigo())) {
            throw new IllegalArgumentException("Ya existe una bodega con el codigo: " + bodega.getCodigo());
        }
        bodegas.put(bodega.getCodigo(), bodega);
    }

    public void cargarLoteInicial(Lote lote, String codigoBodega) {
        if (lote.getFechaIngreso().isAfter(fechaOperacion)) {
            throw new IllegalArgumentException("La fecha de ingreso inicial no puede ser posterior a la fecha de operacion.");
        }
        validarCodigoLoteUnico(lote.getCodigo());
        Bodega b = obtenerBodega(codigoBodega);
        b.almacenarLote(lote);
        codigosLotesHist.add(lote.getCodigo());
        historialLotes.add(lote);
        asegurarCategoriaEnMapas(lote.obtenerCategoria());
    }

    public String preliquidarLote(Lote lote) {
        validarCodigoLoteUnico(lote.getCodigo());
        return String.format("Pre-liquidacion -> Categoria: %s | Pago al productor: Q%.2f | Vencimiento: %s",
                lote.obtenerCategoria(), lote.calcularPagoProductor(), lote.obtenerTextoVencimiento());
    }

    public void recibirLote(Lote lote, String codigoBodega) {
        validarCodigoLoteUnico(lote.getCodigo());
        Bodega b = obtenerBodega(codigoBodega);
        b.almacenarLote(lote);
        codigosLotesHist.add(lote.getCodigo());
        historialLotes.add(lote);
        String cat = lote.obtenerCategoria();
        asegurarCategoriaEnMapas(cat);
        double pago = lote.calcularPagoProductor();
        pagosPorCategoria.put(cat, pagosPorCategoria.get(cat) + pago);
    }

    public void avanzarFecha(int dias) {
        if (dias <= 0) {
            throw new IllegalArgumentException("Los dias a avanzar deben ser un entero positivo.");
        }
        fechaOperacion = fechaOperacion.plusDays(dias);
        descartarVencidos();
    }

    private void descartarVencidos() {
        for (Bodega b : bodegas.values()) {
            Iterator<Lote> it = b.iterator();
            while (it.hasNext()) {
                Lote lote = it.next();
                double perdida = lote.descartarSiVencido(fechaOperacion);
                if (lote.getEstado() == EstadoLote.DESCARTADO) {
                    it.remove();
                    String cat = lote.obtenerCategoria();
                    asegurarCategoriaEnMapas(cat);
                    perdidasPorCategoria.put(cat, perdidasPorCategoria.get(cat) + perdida);
                }
            }
        }
    }

    public double despacharPedido(String categoria, double kgSolicitados) {
        if (kgSolicitados <= 0) {
            throw new IllegalArgumentException("La cantidad solicitada debe ser mayor a cero.");
        }
        TreeSet<Lote> candidatos = new TreeSet<>();
        Map<Lote, Bodega> mapaBodega = new HashMap<>();
        double totalDisponible = 0;

        for (Bodega b : bodegas.values()) {
            for (Lote l : b) {
                if (l.obtenerCategoria().equalsIgnoreCase(categoria) && l.getEstado() == EstadoLote.DISPONIBLE) {
                    candidatos.add(l);
                    mapaBodega.put(l, b);
                    totalDisponible += l.getPesoDisponible();
                }
            }
        }

        if (candidatos.isEmpty()) {
            throw new IllegalArgumentException("No hay existencias disponibles para la categoria: " + categoria);
        }
        if (totalDisponible + 1e-9 < kgSolicitados) {
            throw new IllegalArgumentException(String.format("Existencias insuficientes para %s. Solicitado: %.2f kg, Disponible: %.2f kg.", categoria, kgSolicitados, totalDisponible));
        }

        double restante = kgSolicitados;
        double cobroTotal = 0;
        String catReal = null;

        for (Lote l : candidatos) {
            if (restante <= 1e-9) {
                break;
            }
            catReal = l.obtenerCategoria();
            double costoKg = l.calcularCostoPorKg();
            double tomado = l.descontarPeso(restante);
            restante -= tomado;
            cobroTotal += tomado * costoKg * 1.35;
            if (l.getEstado() == EstadoLote.AGOTADO) {
                mapaBodega.get(l).removerLote(l);
            }
        }

        if (catReal != null) {
            asegurarCategoriaEnMapas(catReal);
            cobrosPorCategoria.put(catReal, cobrosPorCategoria.get(catReal) + cobroTotal);
        }
        return cobroTotal;
    }

    public String reporteExistenciasYBodegas() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== EXISTENCIAS POR CATEGORIA (ORDEN DE SALIDA) ===
");
        for (String cat : pagosPorCategoria.keySet()) {
            TreeSet<Lote> lotesCat = new TreeSet<>();
            double sumaCat = 0;
            for (Bodega b : bodegas.values()) {
                for (Lote l : b) {
                    if (l.obtenerCategoria().equalsIgnoreCase(cat)) {
                        lotesCat.add(l);
                        sumaCat += l.getPesoDisponible();
                    }
                }
            }
            sb.append(String.format("Categoria: %s | Total disponible: %.2f kg
", cat, sumaCat));
            for (Lote l : lotesCat) {
                sb.append("   ").append(l.toString()).append("
");
            }
        }
        sb.append("
=== CONTENIDO POR BODEGA (ORDEN DE SALIDA) ===
");
        for (Bodega b : bodegas.values()) {
            sb.append(b.toString()).append("
");
            for (Lote l : b) {
                sb.append("   ").append(l.toString()).append("
");
            }
        }
        return sb.toString();
    }

    public String reporteProximosAVencer() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== LOTES PERECEDEROS QUE VENCEN EN LOS PROXIMOS 3 DIAS (Desde ").append(fechaOperacion).append(") ===
");
        TreeSet<Lote> proximos = new TreeSet<>();
        for (Bodega b : bodegas.values()) {
            for (Lote l : b) {
                if (l.venceEnProximosDias(fechaOperacion, 3)) {
                    proximos.add(l);
                }
            }
        }
        if (proximos.isEmpty()) {
            sb.append("No hay lotes que venzan en los proximos 3 dias.
");
        } else {
            for (Lote l : proximos) {
                sb.append("   ").append(l.toString()).append("
");
            }
        }
        return sb.toString();
    }

    public String reporteFinanciero() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== REPORTE FINANCIERO POR CATEGORIA ===
");
        for (String cat : pagosPorCategoria.keySet()) {
            sb.append(String.format("%-20s | Pagado: Q%10.2f | Cobrado: Q%10.2f | Perdidas: Q%10.2f
",
                    cat, pagosPorCategoria.get(cat), cobrosPorCategoria.get(cat), perdidasPorCategoria.get(cat)));
        }
        return sb.toString();
    }

    private void validarCodigoLoteUnico(String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new IllegalArgumentException("El codigo del lote no puede estar vacio.");
        }
        if (codigosLotesHist.contains(codigo.trim())) {
            throw new IllegalArgumentException("Ya existe un lote registrado con el codigo: " + codigo);
        }
    }

    private Bodega obtenerBodega(String codigoBodega) {
        Bodega b = bodegas.get(codigoBodega);
        if (b == null) {
            throw new IllegalArgumentException("No existe la bodega con codigo: " + codigoBodega);
        }
        return b;
    }

    private void asegurarCategoriaEnMapas(String cat) {
        pagosPorCategoria.putIfAbsent(cat, 0.0);
        cobrosPorCategoria.putIfAbsent(cat, 0.0);
        perdidasPorCategoria.putIfAbsent(cat, 0.0);
    }

    public LocalDate getFechaOperacion() {
        return fechaOperacion;
    }

    public Map<String, Bodega> getBodegas() {
        return Collections.unmodifiableMap(bodegas);
    }

    public double getPagoCategoria(String cat) {
        return pagosPorCategoria.getOrDefault(cat, 0.0);
    }

    public double getCobroCategoria(String cat) {
        return cobrosPorCategoria.getOrDefault(cat, 0.0);
    }

    public double getPerdidaCategoria(String cat) {
        return perdidasPorCategoria.getOrDefault(cat, 0.0);
    }
}
