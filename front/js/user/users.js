const modifyUser = function modifyUser(username) {
    window.location.href = "/user/modify/"+username;
}
const restoreUser = async function restoreUser(username) {
    if(!confirm("¿Está seguro de desear reactivar al usuario? (Esta es una acción reversible)")) return;
    const getUserRequest = {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json',
        }
    };
    const getUserResponse = await fetch("http://localhost:8080/user/"+username, getUserRequest);
    const getUserData = await getUserResponse.json();
    console.log(getUserData);
    const restoreUserRequest = {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json',
        },
        body: JSON.stringify({
            id: getUserData.id,
            userWithRolesDTO: {
                email: getUserData.userWithRolesDTO.email,
                isActive: true,
                name: getUserData.userWithRolesDTO.name,
                phoneNumber: getUserData.userWithRolesDTO.phoneNumber,
                roles: getUserData.userWithRolesDTO.roles,
                surname: getUserData.userWithRolesDTO.surname,
                username: getUserData.userWithRolesDTO.username
            }
        })
    };
    const restoreUserResponse = await fetch("http://localhost:8080/user/modify", restoreUserRequest);
    const restoreUserData = await restoreUserResponse.json();
    populateUserTable();
    return restoreUserData;
};
const deleteUser = async function deleteUser(id) {
    if(!confirm("¿Está seguro de desear eliminar al usuario? (Esta es una acción reversible)")) return;
    const request = {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json'
        },
        body: JSON.stringify({
            id: id
        })
    };
    const response = await fetch("http://localhost:8080/user/delete", request);
    const data = await response.json();
    populateUserTable();
    return data;
};
window.modifyUser = modifyUser;
window.deleteUser = deleteUser;
window.restoreUser = restoreUser;
async function getUserList() {
    const request = {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json',
        },
        credentials: 'include'
    };
    const response = await fetch("http://localhost:8080/user/list", request);
    const data = await response.json();
    return data;
}
async function populateUserTable() {
    let userListTable = document.getElementById("userList");
    userListTable.innerHTML = `
        <tr>
            <th>Nombre de usuario</th>
            <th>Nombre</th>
            <th>Apellido</th>
            <th>Celular</th>
            <th>Email</th>
            <th>Activo</th>
            <th>Acciones</th>
        </tr>
    `;
    const users = await getUserList();
    console.log(users);
    users.users.forEach(user => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${user.userWithRolesDTO.username}</td> 
            <td>${user.userWithRolesDTO.name}</td>
            <td>${user.userWithRolesDTO.surname}</td>
            <td>${user.userWithRolesDTO.phoneNumber}</td>
            <td>${user.userWithRolesDTO.email}</td>
            <td>${user.userWithRolesDTO.isActive ? 'Sí' : 'No'}</td>
            <td>
                <button onclick="modifyUser('${user.userWithRolesDTO.username}')">Modificar</button>
                ${user.userWithRolesDTO.isActive
                ? `<button onclick="deleteUser('${user.id}')">Eliminar</button>`
                : `<button onclick="restoreUser('${user.userWithRolesDTO.username}')">Restaurar</button>`
            }
            </td>
        `;
        userListTable.appendChild(row);
    });
}

populateUserTable();
