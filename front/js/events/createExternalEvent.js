
function createNewEvent() {
    const form = document.getElementById("form");
    const formData = new FormData(form);
    const name = formData.get("name");
    const description = formData.get("description");
    const date = formData.get("date");

    const eventData = {
        name: name,
        description: description,
        date: date,
    };

    fetch(`http://localhost:8080/events/createExternalEvent`, {
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