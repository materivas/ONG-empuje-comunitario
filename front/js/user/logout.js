async function logout() {
    const request = {
        method: "POST",
        headers: {
            'Content-Type': 'application/json'
        }
    };
    await fetch("http://localhost:8080/user/logout", request);
    window.location.href = "/";
}