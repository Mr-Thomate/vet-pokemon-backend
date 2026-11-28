package vet_pokemon.vet_backend.config;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import vet_pokemon.vet_backend.model.Categoria;
import vet_pokemon.vet_backend.model.Comuna;
import vet_pokemon.vet_backend.model.DetalleVenta;
import vet_pokemon.vet_backend.model.Estado;
import vet_pokemon.vet_backend.model.ImagenProducto;
import vet_pokemon.vet_backend.model.MetodoEnvio;
import vet_pokemon.vet_backend.model.MetodoPago;
import vet_pokemon.vet_backend.model.Producto;
import vet_pokemon.vet_backend.model.Region;
import vet_pokemon.vet_backend.model.Rol;
import vet_pokemon.vet_backend.model.Tipo;
import vet_pokemon.vet_backend.model.Usuario;
import vet_pokemon.vet_backend.model.Venta;
import vet_pokemon.vet_backend.repository.CategoriaRepository;
import vet_pokemon.vet_backend.repository.ComunaRepository;
import vet_pokemon.vet_backend.repository.EstadoRepository;
import vet_pokemon.vet_backend.repository.MetodoEnvioRepository;
import vet_pokemon.vet_backend.repository.MetodoPagoRepository;
import vet_pokemon.vet_backend.repository.ProductoRepository;
import vet_pokemon.vet_backend.repository.RegionRepository;
import vet_pokemon.vet_backend.repository.RolRepository;
import vet_pokemon.vet_backend.repository.TipoRepository;
import vet_pokemon.vet_backend.repository.UsuarioRepository;
import vet_pokemon.vet_backend.repository.VentaRepository;

/**
 * Inserta datos de ejemplo solo si la tabla de usuarios está vacía.
 *
 * Regla para las relaciones: primero se guardan las tablas "padre"
 * (Rol, Region, Comuna, ...) y se usa el objeto que devuelve save()
 * para asignarlo a la tabla "hija" (Usuario, Producto, Venta, ...).
 * Al ser @Transactional, si algo falla no queda nada a medias.
 */
@Component
public class DatosIniciales implements CommandLineRunner {

    @Autowired private RolRepository rolRepository;
    @Autowired private RegionRepository regionRepository;
    @Autowired private ComunaRepository comunaRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private EstadoRepository estadoRepository;
    @Autowired private CategoriaRepository categoriaRepository;
    @Autowired private TipoRepository tipoRepository;
    @Autowired private ProductoRepository productoRepository;
    @Autowired private MetodoPagoRepository metodoPagoRepository;
    @Autowired private MetodoEnvioRepository metodoEnvioRepository;
    @Autowired private VentaRepository ventaRepository;

    @Override
    @Transactional
    public void run(String... args) {
        if (usuarioRepository.count() > 0) {
            return;
        }

        // ---------- Roles ----------
        Rol admin = rolRepository.save(new Rol(null, "ADMIN"));
        Rol veterinario = rolRepository.save(new Rol(null, "VETERINARIO"));
        Rol cliente = rolRepository.save(new Rol(null, "CLIENTE"));

        // ---------- Regiones y comunas (la comuna lleva su región) ----------
        Region metropolitana = regionRepository.save(new Region(null, "Región Metropolitana"));
        Region valparaiso = regionRepository.save(new Region(null, "Región de Valparaíso"));

        Comuna loEspejo = comunaRepository.save(new Comuna(null, "Lo espejo", metropolitana));
        Comuna puenteAlto = comunaRepository.save(new Comuna(null, "Puente Alto", metropolitana));
        Comuna maipu = comunaRepository.save(new Comuna(null, "Maipú", metropolitana));
        Comuna recoleta = comunaRepository.save(new Comuna(null, "Recoleta", valparaiso));
        Comuna lasCondes = comunaRepository.save(new Comuna(null, "Las Condes", valparaiso));

        // ---------- Usuarios (cada uno lleva su rol y su comuna) ----------
        Usuario matias = usuarioRepository.save(new Usuario(null, "Matias", "Carlos", "Oviedo", "Chavez",
                LocalDate.of(2005, 6, 17), "matias@huellitas.cl", admin, loEspejo));
        Usuario camila = usuarioRepository.save(new Usuario(null, "Camila", null, "Rojas", null,
                LocalDate.of(1995, 3, 8), "camila@huellitas.cl", veterinario, loEspejo));
        Usuario pedro = usuarioRepository.save(new Usuario(null, "Pedro", null, "Soto", "Vera",
                LocalDate.of(1990, 11, 2), "pedro@huellitas.cl", cliente, maipu));

        // ---------- Estados de una venta ----------
        Estado pendiente = estadoRepository.save(new Estado(null, "Pendiente"));
        estadoRepository.save(new Estado(null, "Pagado"));
        estadoRepository.save(new Estado(null, "Enviado"));
        estadoRepository.save(new Estado(null, "Entregado"));

        // ---------- Categorías y tipos ----------
        Categoria bayas = categoriaRepository.save(new Categoria(null, "Bayas"));
        Categoria pociones = categoriaRepository.save(new Categoria(null, "Pociones"));
        Categoria piedrasEvolutivas = categoriaRepository.save(new Categoria(null, "Piedras evolutivas"));

        Tipo fuego = tipoRepository.save(new Tipo(null, "Fuego"));
        Tipo agua = tipoRepository.save(new Tipo(null, "Agua"));
        Tipo planta = tipoRepository.save(new Tipo(null, "Planta"));
        Tipo electrico = tipoRepository.save(new Tipo(null, "Eléctrico"));

        // ---------- Productos (cada uno lleva categoría, tipo e imágenes) ----------
        Producto revivir = crearProducto("Revivir", "Piedra revivir para Pokémon debilitados",
                8990, 40, bayas, fuego, "https://placehold.co/400x300?text=Revivir");
        Producto bayaAranja = crearProducto("Baya Aranja", "Baya fresca que recupera energía",
                2990, 100, bayas, planta, "https://placehold.co/400x300?text=Baya+Aranja");
        Producto pocion = crearProducto("Poción", "Spray curativo para Pokémon heridos",
                6990, 25, pociones, electrico, "https://placehold.co/400x300?text=Poción");
        Producto piedraEvolutiva = crearProducto("Piedra Agua", "Piedra que permite evolucionar ciertos Pokémon de tipo agua",
                4990, 60, piedrasEvolutivas, agua, "https://placehold.co/400x300?text=Piedra+Agua");

        // ---------- Método de pago y de envío ----------
        MetodoPago debito = metodoPagoRepository.save(
                new MetodoPago(null, "Pago con tarjeta de débito", "Débito", 0));
        MetodoEnvio domicilio = metodoEnvioRepository.save(
                new MetodoEnvio(null, LocalDate.now(), LocalDate.now().plusDays(3),
                        "Despacho a domicilio", "Casa matriz"));

        // ---------- Una venta de ejemplo con su detalle ----------
        Venta venta = new Venta();
        venta.setFecha(LocalDateTime.now());
        venta.setTotal(8990);
        venta.setUsuario(pedro);
        venta.setEstado(pendiente);
        venta.setMetodoPago(debito);
        venta.setMetodoEnvio(domicilio);

        DetalleVenta detalle = new DetalleVenta();
        detalle.setDescripcion(revivir.getNombre() + " x1");
        detalle.setFechaVenta(LocalDate.now());
        detalle.setTotalVenta(8990);
        detalle.setSucursal("Casa matriz");
        detalle.setVenta(venta);          // el detalle apunta a su venta (es lo que guarda la FK)
        venta.getDetalles().add(detalle); // la venta conoce su detalle (cascade lo guarda junto con ella)

        ventaRepository.save(venta);
    }

    /** Crea un producto con una imagen y lo guarda. */
    private Producto crearProducto(String nombre, String descripcion, Integer precio, Integer stock,
                                   Categoria categoria, Tipo tipo, String urlImagen) {
        Producto producto = new Producto();
        producto.setNombre(nombre);
        producto.setDescripcion(descripcion);
        producto.setPrecio(precio);
        producto.setStock(stock);
        producto.setCategoria(categoria);
        producto.setTipo(tipo);

        ImagenProducto imagen = new ImagenProducto();
        imagen.setUrl(urlImagen);
        imagen.setProducto(producto);       // la imagen apunta a su producto
        producto.getImagenes().add(imagen); // el producto conoce su imagen

        return productoRepository.save(producto);
    }
}
