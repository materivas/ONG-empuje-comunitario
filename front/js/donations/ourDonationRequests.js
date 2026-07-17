async function deleteRequest(organizationId, requestId, node) {
    if(!confirm("¿Está seguro de desear eliminar esta solicitud de donación?")) return;
    node.remove();
    const today = new Date();
    today.setHours(today.getHours() - 3);
    const request = {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json',
        },
        credentials: 'include',
        body: JSON.stringify({
            organizationId: organizationId,
            requestId: requestId,
            deleted_at: today.toISOString()
        })
    };
    const res = await fetch("http://localhost:8080/donationRequests/delete", request);
    const data = await res.json();
    return data;
}

async function getOurDonationRequests() {
    const request = {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json',
        },
        credentials: 'include'
    };
    const response = await fetch("http://localhost:8080/donationRequests/ours", request);
    const data = await response.json();
    return data;
}

async function populateDonationRequestsTable() {
    let donationRequestTable = document.getElementById("donationRequestList");
    donationRequestTable.innerHTML = `
        <tr>
            <th>Categoría</th>
            <th>Descripción</th>
            <th>Acción</th>
        </tr>
    `;
    getOurDonationRequests().then(data => {
        const ourDonationRequests = data.ourDonationRequests;
        console.log(ourDonationRequests);
        for(let i = 0 ; i < ourDonationRequests.length ; i++) {
            let donationRequest = ourDonationRequests[i];
            let categories = "";
            let descriptions = "";
            let organizationId = donationRequest.organization_id;
            let requestId = donationRequest.request_id;
            for(let j = 0 ; j < donationRequest.donations.length ; j++) {
                categories += donationRequest.donations[j].category;
                descriptions += donationRequest.donations[j].description;
                if(j != donationRequest.donations.length - 1) {
                    categories += ", ";
                    descriptions += ", ";
                }
            }
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${categories}</td>
                <td>${descriptions}</td>
                <th><button onclick="deleteRequest(${organizationId}, ${requestId}, this.parentElement.parentElement);">Eliminar</button></th>
            `;
            donationRequestTable.appendChild(row);
        }
    });
}

populateDonationRequestsTable();