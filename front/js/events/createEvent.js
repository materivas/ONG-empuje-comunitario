// recibo el id del usuario creador
const userId = window.userId;

/*
al entrar al form se deben cargar los usuarios activos
para poder agregarlos como participantes
*/
async function loadActiveUsers() {
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
            //se incluye al usuario creador al evento
            usersSelect.innerHTML += `     
            <div>
                <input type="checkbox" id="user${user.id}" name="participants" value="${user.id}"${user.id == userId? 'checked' : ''}>
                <label for="user${user.id}">${user.username}</label>
            </div> 
        `;
        })
        
    }).catch(error => {
        console.error('Error:', error);
    });

    
}

// se carga la lista de usuarios al cargar la página
window.addEventListener('DOMContentLoaded', loadActiveUsers);


function createNewEvent() {
    const form = document.getElementById("form");
    const formData = new FormData(form);
    const name = formData.get("name");
    const description = formData.get("description");
    const date = formData.get("date");

    const participants = [];
    //guardo en una lista los ids de los participantes seleccionados
    formData.getAll("participants").forEach(id => {
        participants.push({ id: parseInt(id) });
    });

    const eventData = {
        name: name,
        description: description,
        date: date,
        participants: participants
    };

    fetch(`http://localhost:8080/events/create`, {
        method: 'POST',
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
        alert("Evento creado con éxito");
        form.reset();
    }).catch(error => {
        console.error('Error:', error.message);
        alert("Error al crear el evento: " + error.message);
    });
}