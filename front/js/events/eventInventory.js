// recibo el id del evento
const event_id = window.eventId;

const select = document.getElementById("inventory");

async function loadDonations() {
    const table = document.getElementById("donations-list");

    fetch(`http://localhost:8080/events/getEventInventory/${parseInt(eventId)}`, {
        method: 'GET',
        headers: {
            "Content-Type": "application/json",
            'Accept': 'application/json'
        },
        credentials: 'include'
    })
    .then(response => {
        if (!response.ok) {
            throw new Error(response.statusText);
        }
        return response.json();
    })
    .then(data => {           
        data.donations.forEach(donation =>{
            const timestamp = donation.distribution_date.seconds * 1000;
            const date = new Date(timestamp + 3 * 60 * 60 * 1000);    

            table.innerHTML+=`
            <tr>
                <td>${donation.username}</td>
                <td>
                    ${date.toLocaleDateString('es-AR')}
                    <br>
                    ${
                        date.toLocaleTimeString('es-AR', {
                            hour: '2-digit',
                            minute: '2-digit',
                            hour12: false
                        })
                    }
                </td>
                <td>${donation.description}</td>
                <td>${donation.quantity}</td>
            </tr>
        `;
        })
    }).catch(error => {
        console.error('Error:', error);
    });
}

async function loadInventory() {
 
    fetch(`http://localhost:8080/inventories/available`, {
        method: 'GET',
        headers: {
            "Content-Type": "application/json",
            'Accept': 'application/json'
        },
        credentials: 'include'
    })
    .then(response => {
        if (!response.ok) {
            throw new Error(response.statusText);
        }
        return response.json();
    })
    .then(data => {           
        data.forEach( inventory => {
            const select = document.getElementById("inventory");
            select.innerHTML += `
                <option value="${inventory.id}"data-stock="${inventory.quantity}">${inventory.description}</option>
            `
        })
    }).catch(error => {
        console.error('Error:', error);
    });
}

window.addEventListener('DOMContentLoaded', () =>{
    loadDonations();
    loadInventory();
});

//cada vez que se selecciona una opción se muestra cuanto stock hay del inventario
// y se setea el máximo valor que se puede elegir
select.addEventListener('change', function () {
    const selectedOption = select.options[select.selectedIndex];
    const stock = selectedOption.dataset.stock;
    const quantityInput = document.getElementById("quantity");
    const stockSpan = document.getElementById("stock-available");

    quantityInput.max = stock;
    stockSpan.innerHTML=`(${stock} en inventario)`;
})

function registerDonation(event_id){
    const form = document.getElementById('form');
    const formData = new FormData(form);
    const inventory_id = formData.get("inventory")
    const quantity = formData.get("quantity");
    const data = {event_id, inventory_id, quantity};

    console.log(data);

    fetch(`http://localhost:8080/events/registerEventInventory`, {
        method: 'POST',
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify(data),
        credentials: 'include'
    })
    .then(response => {
        return response.json().then(data => {
            if (!response.ok) {
                throw new Error(data.error || response.statusText);
            }
            return data;
        });
    })
    .then(data => {
        alert("Donación registrada con éxito");
        location.reload();
    }).catch(error => {
        console.error('Error:', error.message);
        alert("Error al registrar la donación: " + error.message);
    });
}