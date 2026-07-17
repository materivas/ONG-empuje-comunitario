async function getPresidents(){
    const ids = document.getElementById('presidentsIds').value;          
    const parsedIds = parseIds(ids);

    try {
        const res = await fetch(`/queries/list-presidents?ids=${parsedIds}`);
        const data = await res.json();
        const table = document.getElementById('list-presidents');

        // Limpiar la tabla antes de agregar nuevos datos
        table.innerHTML = `
            <tr>
                <th>Id</th>
                <th>Nombre</th>
                <th>Dirección</th>
                <th>Teléfono</th>
                <th>Id de organización</th>
            </tr>
        `;
        data.presidentsList.forEach(president => {
            table.innerHTML += `
                <tr>
                    <td>${president.id}</td>
                    <td>${president.name}</td>
                    <td>${president.address}</td>
                    <td>${president.phone}</td>
                    <td>${president.organization_id}</td>
                </tr>
            `
        })
    } catch (error) {
        console.error('Error al consultar Organizaciones:', error);
    }
}

async function getOrganizations(){
    const ids = document.getElementById('organizationsIds').value;          
    const parsedIds = parseIds(ids);

    try {
        const res = await fetch(`/queries/list-associations?ids=${parsedIds}`);
        const data = await res.json();
        const table = document.getElementById('list-ONGs');

        // Limpiar la tabla antes de agregar nuevos datos
        table.innerHTML = `
            <tr>
                <th>Id</th>
                <th>Nombre</th>
                <th>Dirección</th>
                <th>Teléfono</th>
            </tr>
        `;
        data.associationsList.forEach(ong => {
            table.innerHTML += `
                <tr>
                    <td>${ong.id}</td>
                    <td>${ong.name}</td>
                    <td>${ong.address}</td>
                    <td>${ong.phone}</td>
                </tr>
            `;
        });
    } catch (error) {
        console.error('Error al consultar Organizaciones:', error);
    }
};

// Parsea los ids recibidos en una cadena, eliminando espacios y valores no numéricos
function parseIds(ids){
    return ids
        .split(',')                 
        .map(id => id.trim())       
        .filter(id => id !== '')    
        .map(Number)              
        .filter(Number.isFinite)    
        .join(',');              
}