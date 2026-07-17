import { UNAUTHENTICATED, INVALID_ARGUMENT, NOT_FOUND, FAILED_PRECONDITION } from "../constants/rpc-codes.js";

const deleteErrorDiv = (errorDiv) => {
    setTimeout(() => errorDiv.remove(), 5000);
}

document.getElementById('loginForm').addEventListener('submit', async (e) => {
    e.preventDefault(); //con esto, al enviar el form, la pagina no se recarga sola
    const formData = new FormData(e.target);
    const body = Object.fromEntries(formData.entries());
    const data = await loginRequest(body);
    if (thereIsAnError(data)) {
        if (document.getElementById('errorDiv') != null) {
            document.getElementById('errorDiv').remove();
        }
        let errorDiv = document.createElement('div');
        errorDiv.id = "errorDiv";
        errorDiv.className = "error";
        errorDiv.innerHTML = data.details;
        document.getElementById('error').appendChild(errorDiv);
        deleteErrorDiv(errorDiv);
        return;
    }
    window.location.href = "/home";
});

async function loginRequest(body) {
    const res = await fetch('http://localhost:8080/user/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body),
        credentials: 'include'
    });
    return await res.json();
}

function thereIsAnError(data) {
    return data.code == UNAUTHENTICATED || data.code == INVALID_ARGUMENT || data.code == NOT_FOUND || data.code == FAILED_PRECONDITION;
}