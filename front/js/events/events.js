function modifyEvent(eventId) {
    window.location.replace(`/events/edit/${eventId}`);
}

function donationsRegistry(eventId, eventName){
    const url = `/events/donationsRegistry/${eventId}/${encodeURIComponent(eventName)}`
    window.location.href = url;
}

function deleteEvent(eventId) {

    if (!confirm("¿Está seguro que desea eliminar este evento?")) {
        return;
    }

    fetch(`http://localhost:8080/events/deleteEvent/${parseInt(eventId)}`, {
        method: 'DELETE',
        headers: {"Content-Type": "application/json"},
        credentials: 'include'
    })
    .then(response => {
        if (!response.ok) {
            throw new Error(response.statusText);
        }
        return response.json();
    })
    .then(data => {           
        alert('Evento eliminado correctamente');

        // recargo la pagina
        window.location.replace(`/events`);

    }).catch(error => {
        console.error('Error:', error);
        alert('Error al eliminar el evento: ' + error.message);
    });
}

//se necesita tener el usuario logueado
function joinEvent(eventId) {

    const requestData = {eventId};

    fetch(`http://localhost:8080/events/assignUserToEvent`, {
        method: 'POST',
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify(requestData),
        credentials: 'include'
    })
    .then(response => {
        if (!response.ok) {
            throw new Error(response.statusText);
        }
        return response.json();
    })
    .then(data => {
        // recargo la pagina
        window.location.replace(`/events`);
    }).catch(error => {
        console.error('Error:', error);
    });
}

function leaveEvent(eventId) {

    const requestData = {eventId};

    fetch(`http://localhost:8080/events/deleteUserFromEvent`, {
        method: 'POST',
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify(requestData),
        credentials: 'include'
    })
    .then(response => {
        if (!response.ok) {
            throw new Error(response.statusText);
        }
        return response.json();
    })
    .then(data => {
        // recargo la pagina
        window.location.replace(`/events`);
    }).catch(error => {
        console.error('Error:', error);
    });
}