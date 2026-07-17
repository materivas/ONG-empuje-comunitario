async function modifyRequest(body) {
    let roles = [];
    if(body.presidente) roles.push({name: body.presidente});
    if(body.coordinador) roles.push({name: body.coordinador});
    if(body.vocal) roles.push({name: body.vocal});
    if(body.voluntario) roles.push({name: body.voluntario});
    const req = {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            id: body.id,
            userWithRolesDTO: {
                username: body.username,
                name: body.name,
                surname: body.surname,
                phoneNumber: body.phoneNumber,
                email: body.email,
                isActive: (body.active ? true : false),
                roles: roles
            }
        }),
        credentials: 'include'
    }
    const res = await fetch('http://localhost:8080/user/modify', req);
    return await res.json();
}

function thereIsAnError(data) {
    return data.succeeded == false;
}

async function populateForm() {
    let originalUsername = document.getElementById("originalUsername").value;
    const getUserRequest = {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json',
        }
    };
    const getUserResponse = await fetch("http://localhost:8080/user/"+originalUsername, getUserRequest);
    const getUserData = await getUserResponse.json();

    document.getElementById("id").value = getUserData.id;
    document.getElementById("username").value = getUserData.userWithRolesDTO.username;
    document.getElementById("name").value = getUserData.userWithRolesDTO.name;
    document.getElementById("surname").value = getUserData.userWithRolesDTO.surname;
    document.getElementById("phoneNumber").value = getUserData.userWithRolesDTO.phoneNumber;
    document.getElementById("email").value = getUserData.userWithRolesDTO.email;
    document.getElementById("active").checked = getUserData.userWithRolesDTO.isActive;
    getUserData.userWithRolesDTO.roles.forEach(role => {
        if(role.name == "PRESIDENTE") document.getElementById("presidente").checked = true;
        if(role.name == "COORDINADOR") document.getElementById("coordinador").checked = true;
        if(role.name == "VOCAL") document.getElementById("vocal").checked = true;
        if(role.name == "VOLUNTARIO") document.getElementById("voluntario").checked = true;
    });
}

const deleteErrorDiv = (errorDiv) => {
    setTimeout(() => errorDiv.remove(), 5000);
}

document.getElementById('modifyUserForm').addEventListener('submit', async (e) => {
    e.preventDefault(); //con esto, al enviar el form, la pagina no se recarga sola
    const formData = new FormData(e.target);
    const body = Object.fromEntries(formData.entries());
    const data = await modifyRequest(body);
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