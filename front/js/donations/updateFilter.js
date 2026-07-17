const getFilter = async (idFilter) => {
    try {
        const res = await fetch('http://localhost:4000/graphql', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                query: `
                    query ($id: ID!) {
                        filterById(id: $id) {
                            id
                            name
                            category
                            dateFrom
                            dateTo
                            deleted
                            user_id
                        }
                    }
                `,
                variables: { id: Number(idFilter) },
            }),
        });
        const data = await res.json();
        return data.data.filterById;
    } catch (error) {
        showError("Hubo un error al obtener el filtro. Por favor, intente nuevamente refrescando la página.");
        return;
    }
};

const populateForm = (filter) => {
    document.getElementById('name').value = filter.name;
    document.getElementById('category').value = filter.category ?? 'TODAS';
    if (filter.dateFrom) {
        const [day, month, year] = filter.dateFrom.split('/');
        const parsedDate = new Date(`${year}-${month}-${day}`);
        filter.dateFrom = parsedDate.toISOString().split('T')[0];
    } else {
        filter.dateFrom = null;
    }
    if (filter.dateTo) {
        const [day, month, year] = filter.dateTo.split('/');
        const parsedDate = new Date(`${year}-${month}-${day}`);
        filter.dateTo = parsedDate.toISOString().split('T')[0];
    } else {
        filter.dateTo = null;
    }
    document.getElementById('dateFrom').value = filter.dateFrom;
    document.getElementById('dateTo').value = filter.dateTo;

    if (filter.deleted === true)
        document.getElementById('deleted').checked = true;
    else if (filter.deleted === false)
        document.getElementById('deleted').checked = false;
    else
        document.getElementById('both').checked = true;
};

const idFilter = document.getElementById('idFilter').value;
const idUser = Number(document.getElementById('idUser').value);

getFilter(idFilter).then(populateForm);

document.getElementById('updateFilterForm').addEventListener('submit', async (e) => {
    e.preventDefault();

    const name = document.getElementById('name').value.trim();
    let category = document.getElementById('category').value;
    if (category === 'TODAS' || category === '') category = null;

    const dateFrom = document.getElementById('dateFrom').value || null;
    const dateTo = document.getElementById('dateTo').value || null;

    let deleted = null;
    if(document.getElementById('deleted').checked) deleted = true;
    else if(!document.getElementById('deleted').checked && !document.getElementById('both').checked) deleted = false;

    const mutationInput = { name, user_id: idUser };
    if (category !== null) mutationInput.category = category;
    if (dateFrom !== null) mutationInput.dateFrom = dateFrom;
    if (dateTo !== null) mutationInput.dateTo = dateTo;
    if (deleted !== null) mutationInput.deleted = deleted;

    try {
        await fetch('http://localhost:4000/graphql', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                query: `
                    mutation ($id: ID!, $input: DonationFilterInput!) {
                        updateFilter(id: $id, input: $input) {
                            id
                            name
                            category
                            dateFrom
                            dateTo
                            deleted
                            user_id
                        }
                    }
                `,
                variables: {
                    id: Number(idFilter),
                    input: mutationInput
                },
            }),
        });
        showSuccess("Filtro actualizado correctamente.");
    } catch (error) {
        showError("Hubo un error al actualizar el filtro. Por favor, intente nuevamente.");
    }
});

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