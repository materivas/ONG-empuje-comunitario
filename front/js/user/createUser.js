async function createRequest(body) {
    let roles = [];
    if (body.presidente) roles.push({ name: body.presidente });
    if (body.coordinador) roles.push({ name: body.coordinador });
    if (body.vocal) roles.push({ name: body.vocal });
    if (body.voluntario) roles.push({ name: body.voluntario });
    const req = {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            username: body.username,
            name: body.name,
            surname: body.surname,
            phoneNumber: body.phoneNumber,
            email: body.email,
            isActive: (body.active ? true : false),
            roles: roles
        }),
        credentials: 'include'
    }
    const res = await fetch('http://localhost:8080/user/create', req);
    return await res.json();
}

function thereIsAnError(data) {
    return data.succeeded == false;
}

const deleteErrorDiv = (errorDiv) => {
    setTimeout(() => errorDiv.remove(), 5000);
}

document.getElementById('createUserForm').addEventListener('submit', async (e) => {
    e.preventDefault(); //con esto, al enviar el form, la pagina no se recarga sola
    const formData = new FormData(e.target);
    const body = Object.fromEntries(formData.entries());
    const data = await createRequest(body);
    if (thereIsAnError(data)) {
        if (document.getElementById('errorDiv') != null) {
            document.getElementById('errorDiv').remove();
        }
        let errorDiv = document.createElement('div');
        errorDiv.id = "errorDiv";
        errorDiv.className = "error";
        errorDiv.innerHTML = data.message;
        document.getElementById('error').appendChild(errorDiv);
        deleteErrorDiv(errorDiv);
        return;
    }
    window.location.href = "/user";
});

document.getElementById("btnCancel").onclick = () => {
    window.location.href = "/user";
}

populateForm();