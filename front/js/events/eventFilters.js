// Configuración base de APIs
const FILTERS_API_URL = "http://localhost:9093/api/filters";
const EVENTS_API_URL = "http://localhost:9093/api/events/report";
const USER_ID = window.userId || 1;

// Al cargar la página
document.addEventListener("DOMContentLoaded", () => {
    listarFiltros();

    const form = document.getElementById("filtroForm");
    form.addEventListener("submit", (e) => {
        e.preventDefault();
        const nombre = document.getElementById("nombreFiltro").value.trim();

        if (!nombre) return alert("Ingresá un nombre para el filtro");

        const criterios = generarCriteriosDinamicos(nombre);
        const editId = form.dataset.editingId;

        if (editId) {
            actualizarFiltro(editId, nombre, criterios);
            delete form.dataset.editingId;
        } else {
            guardarFiltro(nombre, criterios);
        }

        form.reset();
    });
});

// Genera criterios según el nombre del filtro
function generarCriteriosDinamicos(nombreFiltro) {
    const hoy = new Date();
    const nombreLower = nombreFiltro.toLowerCase();

    // Lógica para "Próximo mes" 
    if (nombreLower.includes("próximo") || nombreLower.includes("proximo")) {
        const primerDia = new Date(hoy.getFullYear(), hoy.getMonth() + 1, 1);
        const ultimoDia = new Date(hoy.getFullYear(), hoy.getMonth() + 2, 0);

        return {
            fechaDesde: primerDia.toISOString().split("T")[0],
            fechaHasta: ultimoDia.toISOString().split("T")[0],
            repartoDonaciones: true
        };
    }

    // Lógica para "Eventos completados"
    else if (nombreLower.includes("completado") || nombreLower.includes("finalizado")) {
        return {
            completados: true,
            repartoDonaciones: true
        };
    }

    // Lógica para "Eventos pendientes"
    else if (nombreLower.includes("pendiente") || nombreLower.includes("incompleto")) {
        return {
            completados: false,
            repartoDonaciones: true
        };
    }

    // Lógica para "Eventos con reparto"
    else if (nombreLower.includes("reparto") || nombreLower.includes("donación") || nombreLower.includes("donacion")) {
        return {
            repartoDonaciones: true,
            fechaDesde: new Date(hoy.getFullYear(), hoy.getMonth(), 1).toISOString().split("T")[0],
            fechaHasta: new Date(hoy.getFullYear(), hoy.getMonth() + 1, 0).toISOString().split("T")[0]
        };
    }

    // Lógica para "Eventos sin reparto"
    else if (nombreLower.includes("sin reparto") || nombreLower.includes("sin donación")) {
        return {
            repartoDonaciones: false,
            fechaDesde: new Date(hoy.getFullYear() - 1, hoy.getMonth(), 1).toISOString().split("T")[0],
            fechaHasta: hoy.toISOString().split("T")[0]
        };
    }

    // Lógica para "Eventos de este mes"
    else if (nombreLower.includes("este mes") || nombreLower.includes("actual")) {
        const primerDia = new Date(hoy.getFullYear(), hoy.getMonth(), 1);
        const ultimoDia = new Date(hoy.getFullYear(), hoy.getMonth() + 1, 0);

        return {
            fechaDesde: primerDia.toISOString().split("T")[0],
            fechaHasta: ultimoDia.toISOString().split("T")[0],
            repartoDonaciones: true
        };
    }

    // Lógica para "Eventos del mes pasado"
    else if (nombreLower.includes("pasado") || nombreLower.includes("anterior")) {
        const primerDia = new Date(hoy.getFullYear(), hoy.getMonth() - 1, 1);
        const ultimoDia = new Date(hoy.getFullYear(), hoy.getMonth(), 0);

        return {
            fechaDesde: primerDia.toISOString().split("T")[0],
            fechaHasta: ultimoDia.toISOString().split("T")[0],
            repartoDonaciones: true
        };
    }

    // Lógica para "Eventos del último trimestre"
    else if (nombreLower.includes("trimestre") || nombreLower.includes("3 meses")) {
        const hace3Meses = new Date(hoy.getFullYear(), hoy.getMonth() - 3, hoy.getDate());

        return {
            fechaDesde: hace3Meses.toISOString().split("T")[0],
            fechaHasta: hoy.toISOString().split("T")[0],
            repartoDonaciones: true
        };
    }

    // Lógica para "Eventos del próximo trimestre"
    else if (nombreLower.includes("próximo trimestre") || nombreLower.includes("siguiente trimestre")) {
        const primerDia = new Date(hoy.getFullYear(), hoy.getMonth() + 1, 1);
        const ultimoDia = new Date(hoy.getFullYear(), hoy.getMonth() + 4, 0);

        return {
            fechaDesde: primerDia.toISOString().split("T")[0],
            fechaHasta: ultimoDia.toISOString().split("T")[0],
            repartoDonaciones: true
        };
    }

    // Lógica por defecto 
    else {
        const hace30 = new Date();
        hace30.setDate(hoy.getDate() - 30);

        return {
            fechaDesde: hace30.toISOString().split("T")[0],
            fechaHasta: hoy.toISOString().split("T")[0],
            repartoDonaciones: true
        };
    }
}

// Guardar nuevo filtro
async function guardarFiltro(nombre, criterios) {
    const filtro = {
        userId: USER_ID,
        filterName: nombre,
        criteriaJson: JSON.stringify(criterios)
    };

    try {
        const res = await fetch(FILTERS_API_URL, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(filtro)
        });

        if (!res.ok) throw new Error("No se pudo guardar");
        mostrarAviso(`Filtro "${nombre}" guardado correctamente`);
        listarFiltros();
    } catch {
        mostrarAviso("Error al guardar el filtro", true);
    }
}

// Listar filtros del usuario
async function listarFiltros() {
    const lista = document.getElementById("listaFiltros");
    lista.innerHTML = "<li>Cargando filtros...</li>";

    try {
        const res = await fetch(`${FILTERS_API_URL}/user/${USER_ID}`);
        if (!res.ok) throw new Error("Error de red");

        const data = await res.json();
        if (data.length === 0) {
            lista.innerHTML = "<li>No hay filtros guardados</li>";
            return;
        }

        lista.innerHTML = data.map(f => `
            <li>
                <strong>${f.filterName}</strong>
                <button onclick="aplicarFiltro(${f.id})">Aplicar</button>
                <button onclick="editarFiltro(${f.id}, '${f.filterName}', '${encodeURIComponent(f.criteriaJson)}')">Editar</button>
                <button onclick="eliminarFiltro(${f.id})">Eliminar</button>
            </li>
        `).join("");
    } catch {
        lista.innerHTML = "<li>Error al cargar filtros</li>";
    }
}

// Eliminar filtro
async function eliminarFiltro(id) {
    if (!confirm("¿Seguro que querés eliminar este filtro?")) return;

    try {
        const res = await fetch(`${FILTERS_API_URL}/${id}`, { method: "DELETE" });
        if (!res.ok) throw new Error();
        mostrarAviso("Filtro eliminado");
        listarFiltros();
    } catch {
        mostrarAviso("Error al eliminar filtro", true);
    }
}

// Editar filtro existente
function editarFiltro(id, nombre, criteriosCodificados) {
    const form = document.getElementById("filtroForm");
    const inputNombre = document.getElementById("nombreFiltro");
    inputNombre.value = nombre;
    form.dataset.editingId = id;

    const criterios = JSON.parse(decodeURIComponent(criteriosCodificados));
    console.log("Editando filtro:", criterios);

    mostrarAviso(`Editando filtro "${nombre}"`);
}

// Actualizar filtro
async function actualizarFiltro(id, nombre, criterios) {
    const filtro = {
        userId: USER_ID,
        filterName: nombre,
        criteriaJson: JSON.stringify(criterios)
    };

    try {
        const res = await fetch(`${FILTERS_API_URL}/${id}`, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(filtro)
        });

        if (!res.ok) throw new Error();
        mostrarAviso(`Filtro "${nombre}" actualizado`);
        listarFiltros();
    } catch {
        mostrarAviso("Error al actualizar filtro", true);
    }
}

// Aplicar filtro y mostrar resultados
async function aplicarFiltro(id) {
    try {
        const resFiltro = await fetch(`${FILTERS_API_URL}/${id}`);
        if (!resFiltro.ok) throw new Error("Filtro no encontrado");
        const filtro = await resFiltro.json();
        const criterios = JSON.parse(filtro.criteriaJson);

        // se construye la query con todos los parametros
        const params = new URLSearchParams();
        if (criterios.fechaDesde) params.append("fechaDesde", criterios.fechaDesde);
        if (criterios.fechaHasta) params.append("fechaHasta", criterios.fechaHasta);
        if (criterios.repartoDonaciones !== undefined) params.append("repartoDonaciones", criterios.repartoDonaciones);
        if (criterios.completados !== undefined) params.append("completados", criterios.completados); // ← NUEVO

        console.log("Solicitando eventos con URL:", `${EVENTS_API_URL}?${params}`);

        const eventosRes = await fetch(`${EVENTS_API_URL}?${params}`);
        if (!eventosRes.ok) throw new Error("Error al obtener eventos");

        const eventos = await eventosRes.json();
        console.log("Eventos recibidos del backend:", eventos);

        actualizarTablaEventos(eventos);
        mostrarAviso(`Filtro "${filtro.filterName}" aplicado. ${eventos.length} eventos encontrados.`);
    } catch (e) {
        console.error("Error completo:", e);
        mostrarAviso("Error al aplicar filtro", true);
    }
}


// Actualizar tabla con eventos filtrados 
function actualizarTablaEventos(eventos) {
    console.log("Eventos para mostrar:", eventos);
    
     console.log("=== DEBUG DETALLADO DE EVENTOS ===");
    
    eventos.forEach((evento, index) => {
        console.log(`Evento ${index}: ${evento.nombreEvento || evento.name}`);
        console.log('  - evento.isCompleted:', evento.isCompleted);
        console.log('  - tipo:', typeof evento.isCompleted);
        console.log('  - valor convertido:', evento.isCompleted !== undefined ? evento.isCompleted : false);
        console.log('  - mostrará en tabla:', evento.isCompleted ? "Sí" : "No");
        console.log('---');
    });
    
    const tabla = document.getElementById("eventsList");
    
    const nuevaTabla = `
        <thead>
            <tr>
                <th>Nombre</th>
                <th>Descripción</th>
                <th>Fecha</th>
                <th>Finalizado</th>
                <th>Opciones</th>
            </tr>
        </thead>
        <tbody>
            ${!eventos || eventos.length === 0 ? `
                <tr>
                    <td colspan="5" style="text-align: center; padding: 20px;">
                        No se encontraron eventos con los filtros aplicados
                    </td>
                </tr>
            ` : eventos.map(evento => {
                const id = evento.eventoId || evento.id;
                const nombre = evento.nombreEvento || evento.name;
                const descripcion = evento.descripcion || evento.description;
                const fechaRaw = evento.date || evento.fecha || evento.Date || evento.Fecha;
                const fecha = fechaRaw ? new Date(fechaRaw).toLocaleDateString("es-AR") : "Sin fecha";
                const estaCompletado = evento.isCompleted !== undefined ? evento.isCompleted : false;
                const tieneReparto = evento.repartoDonaciones !== undefined ? evento.repartoDonaciones : true;

                return `
                    <tr>
                        <td><strong>${nombre}</strong></td>
                        <td>${descripcion}</td>
                        <td>${fecha}</td>
                        <td>${estaCompletado ? "Sí" : "No"}</td>
                        <td>
                            <button onclick="verDetallesEvento(${id})">Ver detalles</button>
                        </td>
                    </tr>
                `;
            }).join('')}
        </tbody>
    `;
    
    tabla.innerHTML = nuevaTabla;
}


// Detalle de evento (placeholder)
function verDetallesEvento(id) {
    alert(`Detalles del evento ${id} (pendiente de implementación).`);
}

// Mensaje visual reutilizable
function mostrarAviso(mensaje, esError = false) {
    const alerta = document.createElement("div");
    alerta.textContent = mensaje;
    alerta.style = `
        background: ${esError ? "#f8d7da" : "#d4edda"};
        color: ${esError ? "#721c24" : "#155724"};
        padding: 10px;
        border-radius: 6px;
        margin: 8px 0;
        border: 1px solid ${esError ? "#f5c6cb" : "#c3e6cb"};
        text-align: center;
        font-weight: 500;
    `;
    document.body.prepend(alerta);
    setTimeout(() => alerta.remove(), 4000);
}