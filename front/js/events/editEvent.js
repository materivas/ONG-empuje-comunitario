// recibo el id del evento a modificar
const selectedEventId = window.eventId;

/*
al entrar a la página, cargo el evento a editar
*/
async function getEvent() {

    fetch(`http://localhost:8080/events/getEvent/${selectedEventId}`, {
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
        loadForm(data);
        loadActiveUsers(data.participants);
    }).catch(error => {
        console.error('Error:', error);
    });
}

async function loadForm(eventData) {
    const form = document.getElementById("form");
    form.name.value = eventData.name;
    form.description.value = eventData.description;

    // Convierte seconds y nanos a un objeto Date
    const eventDate = new Date(eventData.date.seconds * 1000 + Math.floor(eventData.date.nanos / 1000000));
    // Formatea la fecha y hora en formato 'YYYY-MM-DDTHH:MM' para el input tipo datetime-local
    form.date.value = eventDate.toISOString().slice(0,16);

    form.isCompleted.checked = eventData.is_completed;
}


async function loadActiveUsers(participants) {
    const usersSelect = document.getElementById('participants');
    usersSelect.innerHTML = ' '; // limpia el select
    //carga de usuarios activos
    
    fetch(`http://localhost:8080/user/active-list`, {
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
        data.users.forEach(user => {
            const isChecked = participants && participants.some(p => p.id == user.id);
            usersSelect.innerHTML += `     
            <div>
                <input type="checkbox" id="user${user.id}" name="participants" value="${user.id}" ${isChecked ? ' checked' : ''}>
                <label for="user${user.id}">${user.username}</label>
            </div> 
        `;
        })
    }).catch(error => {
        console.error('Error:', error);
    });

    
}

// se carga la lista de usuarios al cargar la página
window.addEventListener('DOMContentLoaded', getEvent);


function modifyEvent() {
    const form = document.getElementById("form");
    const formData = new FormData(form);
    const name = formData.get("name");
    const description = formData.get("description");
    const date = formData.get("date");
    const is_completed = formData.get("isCompleted") === "on";

    const participants = [];

    // obtengo los ids de los participantes seleccionados
    formData.getAll("participants").forEach(id => {
        participants.push({ id: parseInt(id)});
    });

    const eventData = {
        id: parseInt(selectedEventId),
        name: name,
        description: description,
        date: date,
        participants: participants,
        is_completed: is_completed
    };

    fetch(`http://localhost:8080/events/modifyEvent`, {
        method: 'PUT',
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify(eventData),
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
        alert("Evento modificado correctamente");
        //redirijo a la lista de eventos
        window.location.replace("/events")
    }).catch(error => {
        console.error('Error:', error.message);
        alert("Error al modificar el evento: " + error.message);
    });
}