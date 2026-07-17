// recibo los roles del usuario
const userRoles = window.roles;

async function getEvents() {
  const res = await fetch('http://localhost:8080/events/getExternalEvents');
  const data = await res.json();

  const table = document.getElementById('eventsList');

  data.forEach(event => {

    const date = new Date(event.date);

    let deleteButton = '';
    
    if(event.organization_id == 1){
      if ( userRoles && (userRoles.includes("PRESIDENTE") || userRoles.includes("COORDINADOR"))){
        deleteButton = `<br><button onclick="deleteEvent(${event.event_id}, '${event.event_name}')">Eliminar</button>`;
      }
    }

    table.innerHTML += `
        <tr>
            <td>${event.event_name}</td>
            <td>${event.description}</td>
            <td>
                ${date.toLocaleDateString('es-AR')}
                <br>
                ${date.toLocaleTimeString('es-AR', {
                  hour: '2-digit',
                  minute: '2-digit',
                  hour12: false
                })}
            </td>
            <td>
              <button onclick="joinEvent(${event.organization_id}, ${event.event_id})">Solicitar Unirse</button>
              ${deleteButton}
            </td>
        </tr>
    `
  });
}

window.addEventListener('DOMContentLoaded', getEvents);

function joinEvent(organization_id, event_id) {
  const requestData = {
    organizationId: organization_id,
    eventId: event_id
  }

  fetch(`http://localhost:8080/events/joinExternalEvent`, {
    method: 'POST',
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(requestData),
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
      alert('Solicitud enviada con éxito');
    }).catch(error => {
      console.error('Error:', error.message);
      alert('Hubo un error al enviar la solicitud');
    });
}

function deleteEvent(eventId, eventName){
  const requestData = {
    eventId: eventId,
    eventName: eventName
  }

  fetch(`http://localhost:8080/events/deleteExternalEvent`, {
    method: 'POST',
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(requestData),
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
      alert('Evento eliminado con éxito');
      window.location.reload();
    }).catch(error => {
      console.error('Error:', error.message);
      alert('Hubo un error al eliminar el evento');
    });
}