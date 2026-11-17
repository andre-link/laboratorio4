import java.time.LocalDate;

public class SuitePruebas {
    public static void ejecutarTodas() {
        System.out.println("=================================================================");
        System.out.println("          EJECUCION AUTOMATICA DE CASOS DE PRUEBA");
        System.out.println("=================================================================");

        probarReglasDePago();
        probarVencimientoYDescarte();
        probarRestriccionesBodega();
        probarOrdenSalidaYDespachoMultiple();
        probarOperacionesRechazadasYPreliquidacion();

        System.out.println("=================================================================");
        System.out.println("          FIN DE LA SUITE DE PRUEBAS AUTOMATICAS");
        System.out.println("=================================================================");
    }

    private static void probarReglasDePago() {
        System.out.println("
--- 1. PRUEBAS DE REGLAS DE PAGO ---");
        LocalDate f = LocalDate.of(2026, 3, 5);

        CafePergamino cp = new CafePergamino("P-CP1", "Juan", f, 500, Calidad.A, 13.4);
        imprimirCaso("Cafe pergamino 500kg Cal A Hum 13.4% (fraccion cuenta como 2 pts -> 6% desc)",
                "Q5640.00", String.format("Q%.2f", cp.calcularPagoProductor()));

        CafeOrganico co = new CafeOrganico("P-CO1", "Maria", f, 500, Calidad.A, 13.4, "Mayacert", f.plusDays(30));
        imprimirCaso("Cafe organico 500kg Cal A Hum 13.4% (+Q0.75/kg bono)",
                "Q6015.00", String.format("Q%.2f", co.calcularPagoProductor()));

        Cardamomo ca = new Cardamomo("P-CA1", "Pedro", f, 80, Calidad.B, 90.0);
        imprimirCaso("Cardamomo 80kg Cal B 90% grano verde (+10% bono)",
                "Q4224.00", String.format("Q%.2f", ca.calcularPagoProductor()));

        Hortaliza ho = new Hortaliza("P-HO1", "Ana", f, 200, Calidad.A, 7);
        imprimirCaso("Hortaliza 200kg Cal A (paga sobre 95% por merma)",
                "Q760.00", String.format("Q%.2f", ho.calcularPagoProductor()));

        QuesoFresco qf = new QuesoFresco("P-QF1", "Luis", f, 40, Calidad.A, 25.0);
        imprimirCaso("Queso fresco 40kg Cal A (-Q1.50/kg empaque)",
                "Q1460.00", String.format("Q%.2f", qf.calcularPagoProductor()));
    }

    private static void probarVencimientoYDescarte() {
        System.out.println("
--- 2. PRUEBAS DE VENCIMIENTO, DESCARTE Y PERDIDAS ---");
        LocalDate f = LocalDate.of(2026, 3, 5);
        Cooperativa coop = new Cooperativa(f);
        coop.registrarBodega(new Bodega("BF-1", 1000, true));

        Hortaliza ho = new Hortaliza("V-HO1", "Productor Hort", f, 200, Calidad.A, 7);
        coop.recibirLote(ho, "BF-1");
        coop.despacharPedido("Hortaliza", 150);

        imprimirCaso("Fecha vencimiento Hortaliza (ingreso 2026-03-05 + 7 dias)",
                "2026-03-12", ho.obtenerFechaVencimiento().toString());

        coop.avanzarFecha(7);
        imprimirCaso("Estado el dia 2026-03-12 (ultimo dia de vigencia)",
                "DISPONIBLE", ho.getEstado().toString());

        coop.avanzarFecha(1);
        imprimirCaso("Estado el dia 2026-03-13 (vencido y descartado, 50kg restantes a Q3.80/kg)",
                "DESCARTADO | Perdida: Q190.00",
                String.format("%s | Perdida: Q%.2f", ho.getEstado(), coop.getPerdidaCategoria("Hortaliza")));
    }

    private static void probarRestriccionesBodega() {
        System.out.println("
--- 3. PRUEBAS DE CAPACIDAD Y COMPATIBILIDAD DE BODEGA ---");
        LocalDate f = LocalDate.of(2026, 3, 5);
        Cooperativa coop = new Cooperativa(f);
        coop.registrarBodega(new Bodega("BS-1", 100, false));
        coop.registrarBodega(new Bodega("BF-1", 100, true));

        String resIncomp = "Aceptado";
        try {
            coop.recibirLote(new QuesoFresco("Q-1", "Prod", f, 50, Calidad.A, 20), "BS-1");
        } catch (IllegalArgumentException e) {
            resIncomp = "Rechazado: " + e.getMessage();
        }
        imprimirCaso("Almacenar Queso Fresco (requiere frio) en Bodega Seca",
                "Rechazado por incompatibilidad", resIncomp);

        String resCap = "Aceptado";
        try {
            coop.recibirLote(new CafePergamino("C-1", "Prod", f, 150, Calidad.A, 10), "BS-1");
        } catch (IllegalArgumentException e) {
            resCap = "Rechazado: " + e.getMessage();
        }
        imprimirCaso("Almacenar 150kg en Bodega de 100kg",
                "Rechazado por exceso de capacidad", resCap);
    }

    private static void probarOrdenSalidaYDespachoMultiple() {
        System.out.println("
--- 4. PRUEBAS DE ORDEN DE SALIDA, DESEMPATES Y DESPACHO MULTIBODEGA ---");
        LocalDate f = LocalDate.of(2026, 3, 5);
        Cooperativa coop = new Cooperativa(f);
        coop.registrarBodega(new Bodega("BS-1", 1000, false));
        coop.registrarBodega(new Bodega("BS-2", 1000, false));

        CafePergamino l1 = new CafePergamino("C-02", "P1", f, 100, Calidad.B, 11.0);
        CafePergamino l2 = new CafePergamino("C-01", "P2", f, 100, Calidad.A, 13.4);
        coop.recibirLote(l1, "BS-1");
        coop.recibirLote(l2, "BS-2");

        double cobro = coop.despacharPedido("Cafe en pergamino", 150);
        double esperado = (100 * 11.28 * 1.35) + (50 * 9.00 * 1.35);

        imprimirCaso("Despacho 150kg Cafe combinando BS-2 (100kg Cal A primero por desempate) y BS-1 (50kg Cal B)",
                String.format("Lote A: AGOTADO, Lote B: 50.00kg disp | Cobro: Q%.2f", esperado),
                String.format("Lote A: %s, Lote B: %.2fkg disp | Cobro: Q%.2f", l2.getEstado(), l1.getPesoDisponible(), cobro));
    }

    private static void probarOperacionesRechazadasYPreliquidacion() {
        System.out.println("
--- 5. PRUEBAS DE INMUTABILIDAD EN PRE-LIQUIDACION Y RECHAZOS ---");
        LocalDate f = LocalDate.of(2026, 3, 5);
        Cooperativa coop = new Cooperativa(f);
        coop.registrarBodega(new Bodega("BS-1", 500, false));

        CafePergamino cpValido = new CafePergamino("C-OK", "Prod", f, 100, Calidad.A, 10.0);
        coop.recibirLote(cpValido, "BS-1");

        double pagosAntes = coop.getPagoCategoria("Cafe en pergamino");
        double cobrosAntes = coop.getCobroCategoria("Cafe en pergamino");
        double dispAntes = cpValido.getPesoDisponible();

        CafePergamino cpPre = new CafePergamino("C-PRE", "Prod2", f, 200, Calidad.A, 11.0);
        coop.preliquidarLote(cpPre);

        try {
            coop.despacharPedido("Cafe en pergamino", 999);
        } catch (IllegalArgumentException ignored) {
        }

        try {
            new QuesoFresco("Q-MAL", "Prod3", f, 20, Calidad.C, 20.0);
        } catch (IllegalArgumentException ignored) {
        }

        try {
            new CafeOrganico("CO-MAL", "Prod4", f, 50, Calidad.A, 10.0, "Cert", f.minusDays(1));
        } catch (IllegalArgumentException ignored) {
        }

        double pagosDespues = coop.getPagoCategoria("Cafe en pergamino");
        double cobrosDespues = coop.getCobroCategoria("Cafe en pergamino");
        double dispDespues = cpValido.getPesoDisponible();

        imprimirCaso("Pre-liquidacion, pedido excesivo y lotes invalidos no alteran estado",
                String.format("Pagos: Q%.2f | Cobros: Q%.2f | Disp: %.2fkg", pagosAntes, cobrosAntes, dispAntes),
                String.format("Pagos: Q%.2f | Cobros: Q%.2f | Disp: %.2fkg", pagosDespues, cobrosDespues, dispDespues));
    }

    private static void imprimirCaso(String descripcion, String esperado, String obtenido) {
        System.out.println("Caso: " + descripcion);
        System.out.println("   Esperado : " + esperado);
        System.out.println("   Obtenido : " + obtenido);
    }
}
