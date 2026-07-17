const deleteFilter = async (filterId) => {
    if(!confirm("¿Está seguro de desear eliminar este filtro?")) return;
    try {
        const res = await fetch('http://localhost:4000/graphql', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                query: `
                    mutation {
                        deleteFilter(id: ${filterId})
                    }
                `,
            }),
        });
        const data = await res.json();
        const userId = document.getElementById('userId').value;
        getMyFilters(userId).then(filters => populateFiltersTable(filters));
        showSuccess("Filtro eliminado correctamente.");
        return data.data;
    } catch (error) {
        showError("Error al eliminar el filtro.");
    }
}
const getMyFilters = async (userId) => {
    const res = await fetch('http://localhost:4000/graphql', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            query: `
                query {
                    filtersByUser(user_id: ${userId}) {
                        id,
                        name,
                        category,
                        dateFrom,
                        dateTo,
                        deleted,
                        user_id
                    }
                }
            `,
        }),
    });
    const data = await res.json();
    console.log(data.data.filtersByUser);
    return data.data.filtersByUser;
}
const populateFiltersTable = (filters) => {
    const tbody = document.querySelector('#filtersTable tbody');
    tbody.innerHTML = '';
    filters.forEach(filter => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${filter.name}</td>
            <td>${filter.category}</td>
            <td>${filter.dateFrom}</td>
            <td>${filter.dateTo}</td>
            <td>${filter.deleted}</td>
            <td>
                <a href="/donations/update-filter/${filter.id}">Modificar</a>
                <button type="button" onclick="deleteFilter(${filter.id})">Eliminar</button>
            </td>
        `;
        tbody.appendChild(row);
    });
}
const userId = document.getElementById('userId').value;
getMyFilters(userId).then(filters => populateFiltersTable(filters));

const deleteErrorDiv = (errorDiv) => {
    setTimeout(() => errorDiv.remove(), 5000);
}

const showError = (message) => {
    if (document.getElementById('errorDiv') != null) {
        document.getElementById('errorDiv').remove();
    }
    let errorDiv = document.createElement('div');
    errorDiv.id = "errorDiv";
    errorDiv.className = "error";
    errorDiv.innerHTML = message;
    document.getElementById('error').appendChild(errorDiv);
    deleteErrorDiv(errorDiv);
}

const showSuccess = (message) => {
    if (document.getElementById('sucessDiv') != null) {
        document.getElementById('sucessDiv').remove();
    }
    let sucessDiv = document.createElement('div');
    sucessDiv.id = "sucessDiv";
    sucessDiv.className = "success";
    sucessDiv.innerHTML = message;
    document.getElementById('success').appendChild(sucessDiv);
    deleteErrorDiv(sucessDiv);
}
