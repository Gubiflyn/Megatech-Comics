package cl.megatech.kafkaadmin.config;

// Única fuente de nombres de tópicos del proyecto. Ninguna otra clase
// debe escribir estos strings a mano: el resto del equipo (productores,
// consumidores) también debe referenciar estas constantes.
public final class KafkaTopicNames {

    private KafkaTopicNames() {
    }

    // Eventos de negocio: PEDIDO_CREADO y PEDIDO_PAGADO.
    public static final String PEDIDOS_EVENTOS = "pedidos.eventos";

    // Eventos de negocio: STOCK_ACTUALIZADO.
    public static final String INVENTARIO_EVENTOS = "inventario.eventos";

    // Dead Letter Topic de pedidos.eventos. Sufijo ".DLT": convención por
    // defecto de Spring Kafka (DeadLetterPublishingRecoverer).
    public static final String PEDIDOS_EVENTOS_DLT = "pedidos.eventos.DLT";

    // Dead Letter Topic de inventario.eventos.
    public static final String INVENTARIO_EVENTOS_DLT = "inventario.eventos.DLT";

    // Consumer groups: solo se documentan aquí, los usan otros servicios.
    public static final String GRUPO_ANALITICA_VENTAS = "analitica-ventas";
    public static final String GRUPO_ANALITICA_STOCK = "analitica-stock";
    public static final String GRUPO_CATALOGO_POPULARES = "catalogo-populares";

}
