package restaurante.estadisticas;

import restaurante.dominio.Categoria;
import restaurante.dominio.LineaPedido;
import restaurante.dominio.Mesa;
import restaurante.dominio.Pago;
import restaurante.dominio.Restaurante;
import restaurante.dominio.Visita;
import restaurante.excepciones.DatoInvalidoException;
import restaurante.util.Formato;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// servicio de consultas estadisticas por rango de fechas (Seccion 3.6 del enunciado)
public class ServicioEstadisticas {

    public static final class ItemMasSolicitado {
        private final String nombreOpcion;
        private final String nombreCategoria;
        private final int unidadesVendidas;
        private final int montoTotal;

        public ItemMasSolicitado(String nombreOpcion, String nombreCategoria, int unidadesVendidas, int montoTotal) {
            this.nombreOpcion = nombreOpcion;
            this.nombreCategoria = nombreCategoria;
            this.unidadesVendidas = unidadesVendidas;
            this.montoTotal = montoTotal;
        }

        public String getNombreOpcion() { return nombreOpcion; }
        public String getNombreCategoria() { return nombreCategoria; }
        public int getUnidadesVendidas() { return unidadesVendidas; }
        public int getMontoTotal() { return montoTotal; }
    }

    public static final class ReporteVentas {
        private final int totalCobrado;
        private final int cantidadPagos;
        private final Map<String, Integer> cobradoPorMetodo;
        private final Map<String, Integer> pagosPorMetodo;
        private final String explicacion;

        public ReporteVentas(int totalCobrado, int cantidadPagos,
                             Map<String, Integer> cobradoPorMetodo,
                             Map<String, Integer> pagosPorMetodo,
                             String explicacion) {
            this.totalCobrado = totalCobrado;
            this.cantidadPagos = cantidadPagos;
            this.cobradoPorMetodo = Map.copyOf(cobradoPorMetodo);
            this.pagosPorMetodo = Map.copyOf(pagosPorMetodo);
            this.explicacion = explicacion;
        }

        public int getTotalCobrado() { return totalCobrado; }
        public int getCantidadPagos() { return cantidadPagos; }
        public Map<String, Integer> getCobradoPorMetodo() { return cobradoPorMetodo; }
        public Map<String, Integer> getPagosPorMetodo() { return pagosPorMetodo; }
        public String getExplicacion() { return explicacion; }
    }

    public static final class RendimientoMesero {
        private final String mesero;
        private final int visitasAtendidas;
        private final int comensalesAtendidos;
        private final int totalFacturado;

        public RendimientoMesero(String mesero, int visitasAtendidas, int comensalesAtendidos, int totalFacturado) {
            this.mesero = mesero;
            this.visitasAtendidas = visitasAtendidas;
            this.comensalesAtendidos = comensalesAtendidos;
            this.totalFacturado = totalFacturado;
        }

        public String getMesero() { return mesero; }
        public int getVisitasAtendidas() { return visitasAtendidas; }
        public int getComensalesAtendidos() { return comensalesAtendidos; }
        public int getTotalFacturado() { return totalFacturado; }
    }

    public static final class ReporteVisitas {
        private final int visitasCerradas;
        private final int totalComensales;
        private final int totalFacturado;
        private final int ticketPromedio;
        private final long duracionPromedioMinutos;
        private final List<RendimientoMesero> porMesero;
        private final String explicacion;

        public ReporteVisitas(int visitasCerradas, int totalComensales, int totalFacturado,
                              int ticketPromedio, long duracionPromedioMinutos,
                              List<RendimientoMesero> porMesero, String explicacion) {
            this.visitasCerradas = visitasCerradas;
            this.totalComensales = totalComensales;
            this.totalFacturado = totalFacturado;
            this.ticketPromedio = ticketPromedio;
            this.duracionPromedioMinutos = duracionPromedioMinutos;
            this.porMesero = List.copyOf(porMesero);
            this.explicacion = explicacion;
        }

        public int getVisitasCerradas() { return visitasCerradas; }
        public int getTotalComensales() { return totalComensales; }
        public int getTotalFacturado() { return totalFacturado; }
        public int getTicketPromedio() { return ticketPromedio; }
        public long getDuracionPromedioMinutos() { return duracionPromedioMinutos; }
        public List<RendimientoMesero> getPorMesero() { return porMesero; }
        public String getExplicacion() { return explicacion; }
    }

    public static final class IngresoCategoria {
        private final String categoria;
        private final int unidades;
        private final int monto;
        private final double porcentaje;

        public IngresoCategoria(String categoria, int unidades, int monto, double porcentaje) {
            this.categoria = categoria;
            this.unidades = unidades;
            this.monto = monto;
            this.porcentaje = porcentaje;
        }

        public String getCategoria() { return categoria; }
        public int getUnidades() { return unidades; }
        public int getMonto() { return monto; }
        public double getPorcentaje() { return porcentaje; }
    }

    private final Restaurante restaurante;

    public ServicioEstadisticas(Restaurante restaurante) {
        if (restaurante == null) {
            throw new IllegalArgumentException("El restaurante no puede ser nulo.");
        }
        this.restaurante = restaurante;
    }

    public ReporteVentas ventasCobradas(LocalDate desde, LocalDate hasta) throws DatoInvalidoException {
        validarFechas(desde, hasta);
        int total = 0;
        int cantidad = 0;
        Map<String, Integer> montoMetodo = new LinkedHashMap<>();
        Map<String, Integer> conteoMetodo = new LinkedHashMap<>();

        for (Visita v : todasLasVisitas()) {
            for (Pago p : v.getPagos()) {
                if (enRango(p.getFecha().toLocalDate(), desde, hasta)) {
                    total += p.getMonto();
                    cantidad++;
                    String metodo = p.getMetodo().getNombre();
                    montoMetodo.put(metodo, montoMetodo.getOrDefault(metodo, 0) + p.getMonto());
                    conteoMetodo.put(metodo, conteoMetodo.getOrDefault(metodo, 0) + 1);
                }
            }
        }

        String explicacion = cantidad == 0
                ? "No se registraron pagos cobrados en el rango seleccionado."
                : "Cálculo: suma de los montos de todos los pagos cuya fecha de registro está dentro del rango ("
                + cantidad + " pagos por un total de " + Formato.colones(total) + "), agrupados por método de pago.";

        return new ReporteVentas(total, cantidad, montoMetodo, conteoMetodo, explicacion);
    }

    public List<ItemMasSolicitado> articulosMasSolicitados(LocalDate desde, LocalDate hasta, Categoria categoriaFiltro)
            throws DatoInvalidoException {
        validarFechas(desde, hasta);
        Map<String, Integer> unidadesPorOpcion = new LinkedHashMap<>();
        Map<String, Integer> montoPorOpcion = new LinkedHashMap<>();
        Map<String, String> categoriaPorOpcion = new LinkedHashMap<>();

        for (Visita v : todasLasVisitas()) {
            for (LineaPedido l : v.getPedidosActivos()) {
                if (!enRango(l.getFecha().toLocalDate(), desde, hasta)) continue;
                if (categoriaFiltro != null && !l.getCategoria().equals(categoriaFiltro)) continue;

                String nombre = l.getOpcion().getNombre();
                unidadesPorOpcion.put(nombre, unidadesPorOpcion.getOrDefault(nombre, 0) + l.getCantidad());
                montoPorOpcion.put(nombre, montoPorOpcion.getOrDefault(nombre, 0) + l.getSubtotal());
                categoriaPorOpcion.putIfAbsent(nombre, l.getCategoria().getNombre());
            }
        }

        List<ItemMasSolicitado> resultado = new ArrayList<>();
        for (String nombre : unidadesPorOpcion.keySet()) {
            resultado.add(new ItemMasSolicitado(
                    nombre,
                    categoriaPorOpcion.get(nombre),
                    unidadesPorOpcion.get(nombre),
                    montoPorOpcion.get(nombre)
            ));
        }

        resultado.sort(Comparator.comparingInt(ItemMasSolicitado::getUnidadesVendidas).reversed()
                .thenComparing(Comparator.comparingInt(ItemMasSolicitado::getMontoTotal).reversed())
                .thenComparing(ItemMasSolicitado::getNombreOpcion));
        return resultado;
    }

    public ReporteVisitas resumenVisitasCerradas(LocalDate desde, LocalDate hasta) throws DatoInvalidoException {
        validarFechas(desde, hasta);
        int visitasCount = 0;
        int comensalesTotal = 0;
        int facturadoTotal = 0;
        long minutosAcumulados = 0;

        Map<String, int[]> datosMesero = new LinkedHashMap<>();

        for (Visita v : restaurante.getHistorial()) {
            if (v.getCierre() == null) continue;
            if (!enRango(v.getCierre().toLocalDate(), desde, hasta)) continue;

            visitasCount++;
            int comensales = v.getComensales().size();
            int totalVisita = v.totalMesa();
            comensalesTotal += comensales;
            facturadoTotal += totalVisita;
            minutosAcumulados += Math.max(1, Duration.between(v.getInicio(), v.getCierre()).toMinutes());

            String mesero = v.getMesero().getNombre();
            int[] arr = datosMesero.computeIfAbsent(mesero, k -> new int[3]);
            arr[0] += 1;
            arr[1] += comensales;
            arr[2] += totalVisita;
        }

        int ticketPromedio = visitasCount == 0 ? 0 : facturadoTotal / visitasCount;
        long duracionPromedio = visitasCount == 0 ? 0 : minutosAcumulados / visitasCount;

        List<RendimientoMesero> listaMeseros = new ArrayList<>();
        for (Map.Entry<String, int[]> e : datosMesero.entrySet()) {
            int[] d = e.getValue();
            listaMeseros.add(new RendimientoMesero(e.getKey(), d[0], d[1], d[2]));
        }
        listaMeseros.sort(Comparator.comparingInt(RendimientoMesero::getTotalFacturado).reversed()
                .thenComparing(RendimientoMesero::getMesero));

        String explicacion = visitasCount == 0
                ? "No hay visitas cerradas dentro del rango de fechas seleccionado."
                : "Cálculo: analiza las visitas con fecha de cierre en el rango (" + visitasCount
                + " visitas). Ticket promedio = Total facturado / Visitas cerradas ("
                + Formato.colones(ticketPromedio) + ").";

        return new ReporteVisitas(visitasCount, comensalesTotal, facturadoTotal,
                ticketPromedio, duracionPromedio, listaMeseros, explicacion);
    }

    public List<IngresoCategoria> ingresosPorCategoria(LocalDate desde, LocalDate hasta) throws DatoInvalidoException {
        validarFechas(desde, hasta);
        Map<String, Integer> unidadesCat = new LinkedHashMap<>();
        Map<String, Integer> montoCat = new LinkedHashMap<>();
        int granTotal = 0;

        for (Visita v : todasLasVisitas()) {
            for (LineaPedido l : v.getPedidosActivos()) {
                if (!enRango(l.getFecha().toLocalDate(), desde, hasta)) continue;
                String cat = l.getCategoria().getNombre();
                unidadesCat.put(cat, unidadesCat.getOrDefault(cat, 0) + l.getCantidad());
                montoCat.put(cat, montoCat.getOrDefault(cat, 0) + l.getSubtotal());
                granTotal += l.getSubtotal();
            }
        }

        List<IngresoCategoria> resultado = new ArrayList<>();
        for (String cat : montoCat.keySet()) {
            int monto = montoCat.get(cat);
            double pct = granTotal == 0 ? 0.0 : (monto * 100.0) / granTotal;
            resultado.add(new IngresoCategoria(cat, unidadesCat.get(cat), monto, pct));
        }
        resultado.sort(Comparator.comparingInt(IngresoCategoria::getMonto).reversed()
                .thenComparing(IngresoCategoria::getCategoria));
        return resultado;
    }

    private List<Visita> todasLasVisitas() {
        List<Visita> todas = new ArrayList<>(restaurante.getHistorial());
        for (Mesa m : restaurante.getMesas()) {
            if (m.getVisitaActual() != null) {
                todas.add(m.getVisitaActual());
            }
        }
        return todas;
    }

    private static void validarFechas(LocalDate desde, LocalDate hasta) throws DatoInvalidoException {
        if (desde != null && hasta != null && desde.isAfter(hasta)) {
            throw new DatoInvalidoException("La fecha 'Desde' no puede ser posterior a la fecha 'Hasta'.");
        }
    }

    private static boolean enRango(LocalDate fecha, LocalDate desde, LocalDate hasta) {
        if (fecha == null) return false;
        if (desde != null && fecha.isBefore(desde)) return false;
        if (hasta != null && fecha.isAfter(hasta)) return false;
        return true;
    }
}
