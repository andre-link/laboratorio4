import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class Main {
    private static Cooperativa cooperativa;
    private static Scanner scanner;

    public static void main(String[] args) {
        scanner = new Scanner(System.in);
        inicializarDatosBase();
        ejecutarMenu();
    }

    private static void inicializarDatosBase() {
        LocalDate fechaInicial = LocalDate.of(2026, 3, 5);
        cooperativa = new Cooperativa(fechaInicial);

        cooperativa.registrarBodega(new Bodega("BS-01", 2500.0, false));
        cooperativa.registrarBodega(new Bodega("BS-02", 2500.0, false));
        cooperativa.registrarBodega(new Bodega("BF-01", 1500.0, true));

        cooperativa.cargarLoteInicial(new CafePergamino("CP-01", "Finca El Sauce", LocalDate.of(2026, 3, 1), 500.0, Calidad.A, 13.4), "BS-01");
        cooperativa.cargarLoteInicial(new CafePergamino("CP-02", "Finca La Loma", LocalDate.of(2026, 3, 1), 300.0, Calidad.B, 11.0), "BS-02");

        cooperativa.cargarLoteInicial(new CafeOrganico("CO-01", "Coop Atitlan", LocalDate.of(2026, 3, 2), 500.0, Calidad.A, 13.4, "Mayacert", LocalDate.of(2026, 12, 31)), "BS-01");
        cooperativa.cargarLoteInicial(new CafeOrganico("CO-02", "Finca Verde", LocalDate.of(2026, 3, 3), 200.0, Calidad.B, 10.5, "Agrequima", LocalDate.of(2026, 10, 15)), "BS-02");

        cooperativa.cargarLoteInicial(new Cardamomo("CA-01", "Alta Verapaz S.A.", LocalDate.of(2026, 2, 20), 80.0, Calidad.B, 90.0), "BS-01");
        cooperativa.cargarLoteInicial(new Cardamomo("CA-02", "Coban Export", LocalDate.of(2026, 2, 25), 120.0, Calidad.A, 80.0), "BS-02");

        cooperativa.cargarLoteInicial(new Hortaliza("HO-01", "Huertos Chimaltenango", LocalDate.of(2026, 3, 4), 200.0, Calidad.A, 3), "BF-01");
        cooperativa.cargarLoteInicial(new Hortaliza("HO-02", "Vegetales Almolonga", LocalDate.of(2026, 3, 5), 150.0, Calidad.B, 10), "BF-01");

        cooperativa.cargarLoteInicial(new QuesoFresco("QF-01", "Lacteos Oriente", LocalDate.of(2026, 3, 1), 40.0, Calidad.A, 25.0), "BF-01");
        cooperativa.cargarLoteInicial(new QuesoFresco("QF-02", "Granja Santa Rosa", LocalDate.of(2026, 3, 3), 60.0, Calidad.B, 30.0), "BF-01");
    }

    private static void ejecutarMenu() {
        boolean salir = false;
        while (!salir) {
            System.out.println("
=======================================================");
            System.out.println("   ACOPIOGT - SISTEMA DE GESTION (Fecha: " + cooperativa.getFechaOperacion() + ")");
            System.out.println("=======================================================");
            System.out.println("1. Pre-liquidar y recibir lote");
            System.out.println("2. Registrar nueva bodega");
            System.out.println("3. Registrar pedido y despachar");
            System.out.println("4. Avanzar fecha de operacion");
            System.out.println("5. Consultar lotes y bodegas (Orden de salida)");
            System.out.println("6. Generar reportes de gerencia");
            System.out.println("7. Ejecutar casos de prueba automaticos");
            System.out.println("8. Salir");
            System.out.print("Seleccione una opcion: ");

            String opcion = scanner.nextLine().trim();
            try {
                switch (opcion) {
                    case "1":
                        menuPreliquidarYRecibir();
                        break;
                    case "2":
                        menuRegistrarBodega();
                        break;
                    case "3":
                        menuDespacharPedido();
                        break;
                    case "4":
                        menuAvanzarFecha();
                        break;
                    case "5":
                        System.out.println(cooperativa.reporteExistenciasYBodegas());
                        break;
                    case "6":
                        menuReportes();
                        break;
                    case "7":
                        SuitePruebas.ejecutarTodas();
                        break;
                    case "8":
                        salir = true;
                        System.out.println("Saliendo del sistema...");
                        break;
                    default:
                        System.out.println("Opcion no valida. Intente nuevamente.");
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Operacion rechazada: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Error en la entrada de datos: " + e.getMessage());
            }
        }
    }

    private static void menuPreliquidarYRecibir() {
        System.out.println("
--- Categorias disponibles ---");
        System.out.println("1. Cafe en pergamino");
        System.out.println("2. Cafe organico");
        System.out.println("3. Cardamomo");
        System.out.println("4. Hortaliza");
        System.out.println("5. Queso fresco");
        System.out.print("Seleccione categoria (1-5): ");
        int tipo = Integer.parseInt(scanner.nextLine().trim());
        if (tipo < 1 || tipo > 5) {
            throw new IllegalArgumentException("Categoria invalida.");
        }

        System.out.print("Codigo del lote: ");
        String codigo = scanner.nextLine().trim();
        System.out.print("Nombre del productor: ");
        String productor = scanner.nextLine().trim();
        System.out.print("Peso recibido (kg): ");
        double peso = Double.parseDouble(scanner.nextLine().trim());
        System.out.print("Calidad (A, B, C): ");
        Calidad calidad = Calidad.valueOf(scanner.nextLine().trim().toUpperCase());

        LocalDate fechaIngreso = cooperativa.getFechaOperacion();
        Lote nuevoLote;

        switch (tipo) {
            case 1:
                System.out.print("Porcentaje de humedad (8 - 14): ");
                double hum = Double.parseDouble(scanner.nextLine().trim());
                nuevoLote = new CafePergamino(codigo, productor, fechaIngreso, peso, calidad, hum);
                break;
            case 2:
                System.out.print("Porcentaje de humedad (8 - 14): ");
                double humOrg = Double.parseDouble(scanner.nextLine().trim());
                System.out.print("Entidad certificadora: ");
                String entidad = scanner.nextLine().trim();
                System.out.print("Fecha de validez del sello (YYYY-MM-DD): ");
                LocalDate validez = LocalDate.parse(scanner.nextLine().trim());
                nuevoLote = new CafeOrganico(codigo, productor, fechaIngreso, peso, calidad, humOrg, entidad, validez);
                break;
            case 3:
                System.out.print("Porcentaje de grano verde (0 - 100): ");
                double verde = Double.parseDouble(scanner.nextLine().trim());
                nuevoLote = new Cardamomo(codigo, productor, fechaIngreso, peso, calidad, verde);
                break;
            case 4:
                System.out.print("Vida util en dias (3 - 14): ");
                int vida = Integer.parseInt(scanner.nextLine().trim());
                nuevoLote = new Hortaliza(codigo, productor, fechaIngreso, peso, calidad, vida);
                break;
            case 5:
                System.out.print("Porcentaje de grasa (10 - 40): ");
                double grasa = Double.parseDouble(scanner.nextLine().trim());
                nuevoLote = new QuesoFresco(codigo, productor, fechaIngreso, peso, calidad, grasa);
                break;
            default:
                throw new IllegalArgumentException("Categoria invalida.");
        }

        String preliquidacion = cooperativa.preliquidarLote(nuevoLote);
        System.out.println("
" + preliquidacion);
        System.out.print("¿El productor acepta la pre-liquidacion? (S/N): ");
        String resp = scanner.nextLine().trim().toUpperCase();

        if (resp.equals("S")) {
            System.out.print("Ingrese el codigo de la bodega destino: ");
            String codBodega = scanner.nextLine().trim();
            cooperativa.recibirLote(nuevoLote, codBodega);
            System.out.printf("Lote recibido exitosamente. Pago registrado: Q%.2f
", nuevoLote.calcularPagoProductor());
        } else {
            System.out.println("Recepcion cancelada por el productor. No se registro ningun cambio.");
        }
    }

    private static void menuRegistrarBodega() {
        System.out.print("Codigo de la nueva bodega: ");
        String codigo = scanner.nextLine().trim();
        System.out.print("Capacidad maxima (kg): ");
        double capacidad = Double.parseDouble(scanner.nextLine().trim());
        System.out.print("¿Es camara fria? (S/N): ");
        String friaStr = scanner.nextLine().trim().toUpperCase();
        if (!friaStr.equals("S") && !friaStr.equals("N")) {
            throw new IllegalArgumentException("Debe responder S o N.");
        }
        boolean esFria = friaStr.equals("S");
        cooperativa.registrarBodega(new Bodega(codigo, capacidad, esFria));
        System.out.println("Bodega registrada exitosamente.");
    }

    private static void menuDespacharPedido() {
        System.out.println("
--- Categorias ---");
        for (int i = 0; i < Cooperativa.CATEGORIAS.length; i++) {
            System.out.println((i + 1) + ". " + Cooperativa.CATEGORIAS[i]);
        }
        System.out.print("Seleccione el numero de categoria: ");
        int idx = Integer.parseInt(scanner.nextLine().trim()) - 1;
        if (idx < 0 || idx >= Cooperativa.CATEGORIAS.length) {
            throw new IllegalArgumentException("Categoria invalida.");
        }
        String categoria = Cooperativa.CATEGORIAS[idx];
        System.out.print("Kilogramos solicitados: ");
        double kg = Double.parseDouble(scanner.nextLine().trim());

        double cobro = cooperativa.despacharPedido(categoria, kg);
        System.out.printf("Pedido despachado exitosamente. Total cobrado al comprador: Q%.2f
", cobro);
    }

    private static void menuAvanzarFecha() {
        System.out.print("Ingrese la cantidad de dias a avanzar: ");
        int dias = Integer.parseInt(scanner.nextLine().trim());
        cooperativa.avanzarFecha(dias);
        System.out.println("Fecha actualizada a: " + cooperativa.getFechaOperacion());
    }

    private static void menuReportes() {
        System.out.println("
" + cooperativa.reporteExistenciasYBodegas());
        System.out.println(cooperativa.reporteProximosAVencer());
        System.out.println(cooperativa.reporteFinanciero());
    }
}
