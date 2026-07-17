async function createRequest(body, organizationId) {
    const req = {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            organizationId: organizationId,
            donations: body
        }),
        credentials: 'include'
    }
    const res = await fetch('http://localhost:8080/transferDonation/create', req);
    return await res.json();
}

function thereIsAnError(data) {
    return data.succeeded == false;
}

const displayError = (message) => {
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

const deleteErrorDiv = (errorDiv) => {
    setTimeout(() => errorDiv.remove(), 5000);
}

document.getElementById('transferDonationForm').addEventListener('submit', async (e) => {
    e.preventDefault(); //con esto, al enviar el form, la pagina no se recarga sola
    const formData = new FormData(e.target);
    const ids = formData.getAll('id');
    const donations = formData.getAll('donationSelected');
    const categories = formData.getAll('categorySelected');
    const quantities = formData.getAll('quantity');
    const organizationId = formData.get('organizationId');

    const body = [];
    const j = donations.length; //donations, categories, ids y quantities siempre tienen el mismo tamaño
    for(let i = 0 ; i < j ; i++) {
        if(quantities[i] <= 0) {
            displayError("ERROR: No se puede donar una cantidad menor o igual a cero.");
            return;
        }
        body[i] = {
            id: ids[i],
            category: categories[i],
            description: donations[i],
            quantity: quantities[i]
        };
    }
    
    const data = await createRequest(body, organizationId);
    if (thereIsAnError(data)) {
        displayError("ERROR: "+data.message);
        return;
    }

    if (document.getElementById('sucessDiv') != null) {
        document.getElementById('sucessDiv').remove();
    }
    let sucessDiv = document.createElement('div');
    sucessDiv.id = "sucessDiv";
    sucessDiv.className = "success";
    sucessDiv.innerHTML = data.message;
    document.getElementById('success').appendChild(sucessDiv);
    deleteErrorDiv(sucessDiv);
});

document.getElementById("btnCancel").onclick = () => {
    window.location.href = "/donationRequests/allRequests";
}

function removeEntireElement(node) {
    const children = document.getElementById("selectContainer").childElementCount;
    if(children == 1) {
        displayError("ERROR: Se debe registrar al menos una donación.");
        return;
    }
    node.parentElement.remove();
}

async function generateAnotherSelect() {
    const req = {
        method: 'GET',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include'
    };

    const res = await fetch('http://localhost:8080/inventories/availableByCategory', req);
    const inventories = await res.json();
    console.log(inventories);

    //contenedor principal
    const selectContainer = document.getElementById("selectContainer");

    //contenedor de los selects
    const wrapperDiv = document.createElement("div");
    wrapperDiv.classList.add("select-wrapper");

    //contenedor para los selects de donaciones
    const donationDiv = document.createElement("div");
    const donationLabel = document.createElement("label");
    donationLabel.textContent = "Donación";

    const donationSelect = document.createElement("select");
    donationSelect.name = "donationSelected";
    const defaultDonationOption = document.createElement("option");
    defaultDonationOption.value = "";
    defaultDonationOption.disabled = true;
    defaultDonationOption.selected = true;
    defaultDonationOption.textContent = "--Seleccionar--";
    donationSelect.appendChild(defaultDonationOption);

    //agregar los options con info del backend
    inventories.forEach(inventory => {
        const option = document.createElement("option");
        option.value = inventory.description;
        option.textContent = inventory.description;
        donationSelect.appendChild(option);
    });
    
    donationDiv.appendChild(donationLabel);
    donationDiv.appendChild(donationSelect);

    //contenedor para los selects de categoria
    const categoryDiv = document.createElement("div");
    const categoryLabel = document.createElement("label");
    categoryLabel.textContent = "Categoría";

    const categorySelect = document.createElement("select");
    categorySelect.name = "categorySelected";
    const defaultCategoryOption = document.createElement("option");
    defaultCategoryOption.value = "";
    //defaultCategoryOption.disabled = true;
    defaultCategoryOption.selected = true;
    defaultCategoryOption.textContent = "--Seleccionar--";
    categorySelect.appendChild(defaultCategoryOption);

    //adjunto los inputs para los ids
    const inputId = document.createElement("input");
    inputId.name = "id";
    inputId.hidden = true;
    donationSelect.appendChild(inputId);

    //modifico dinamicamente la categoria y id en base a la donacion elegida
    donationSelect.addEventListener("change", (e) => {
        const selected = inventories.find(i => i.description === e.target.value);
        defaultCategoryOption.value = selected.category;
        defaultCategoryOption.textContent = selected.category;
        inputId.value = selected.id;
    });

    categoryDiv.appendChild(categoryLabel);
    categoryDiv.appendChild(categorySelect);

    //boton para eliminar el select
    const btnRemove = document.createElement("button");
    btnRemove.type = "button";
    btnRemove.classList.add("btn-remove");
    btnRemove.textContent = "Eliminar";

    //input para indicar la cantidad a donar
    const inputQuantityDiv = document.createElement("div");
    inputQuantityDiv.classList.add("input-quantity");
    const labelInputQuantity = document.createElement("label");
    labelInputQuantity.textContent = "Cantidad"
    const inputQuantity = document.createElement("input");
    inputQuantity.name = "quantity";
    inputQuantity.type = "number";
    inputQuantityDiv.appendChild(labelInputQuantity);
    inputQuantityDiv.appendChild(inputQuantity);

    //agregar todo al contenedor
    wrapperDiv.appendChild(donationDiv);
    wrapperDiv.appendChild(categoryDiv);
    wrapperDiv.appendChild(inputQuantityDiv);
    wrapperDiv.appendChild(btnRemove);

    selectContainer.appendChild(wrapperDiv);
}

document.getElementById("selectContainer").addEventListener("click", (e) => {
    if (e.target.classList.contains("btn-remove")) {
        removeEntireElement(e.target);
    }
});

btnGenerateAnotherSelect.onclick = generateAnotherSelect;

generateAnotherSelect();