function donate(organizationId) {
    window.location.href = "/transferDonation/create/"+organizationId;
}

async function getAllDonationRequests() {
    const request = {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json',
        },
        credentials: 'include'
    };
    const response = await fetch("http://localhost:8080/donationRequests/all", request);
    const data = await response.json();
    return data;
}

async function populateDonationRequestsTable() {
    let donationRequestTable = document.getElementById("donationRequestList");
    donationRequestTable.innerHTML = `
        <tr>
            <th>ID de la organización solicitante</th>
            <th>Categoría</th>
            <th>Descripción</th>
            <th>Acción</th>
        </tr>
    `;
    getAllDonationRequests().then(data => {
        const allDonationRequests = data.allDonationRequests;
        console.log(allDonationRequests);
        for(let i = 0 ; i < allDonationRequests.length ; i++) {
            let donationRequest = allDonationRequests[i];
            let organizationId = donationRequest.organization_id;
            let requestId = donationRequest.request_id;
            let categories = "";
            let descriptions = "";
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
                <td>${organizationId}</td> 
                <td>${categories}</td>
                <td>${descriptions}</td>
                <td><button onclick="donate(${organizationId})">Donar</button></td>
            `;
            //<td><button onclick="modifyUser('${user.userWithRolesDTO.username}')">Modificar</button></td>
            donationRequestTable.appendChild(row);
        }
    });
}

populateDonationRequestsTable();
