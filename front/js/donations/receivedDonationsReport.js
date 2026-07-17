const executeQuery = async () => {
    const res = await fetch('http://localhost:4000/graphql', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            query: `
                query {
                    donationsByMadeByOurselves(madeByOurselves: false) {
                        category
                        description
                        quantity
                        deleted
                        lastDonationDate
                    }
                }
            `,
        }),
    });
    const data = await res.json();
    return data.data.donationsByMadeByOurselves;
}
const populateDonationsTable = (donations) => {
    const tbody = document.querySelector('#donationsTable tbody');
    donations.forEach(donation => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${donation.category}</td>
            <td>${donation.description}</td>
            <td>${donation.quantity}</td>
            <td>${donation.deleted}</td>
            <td>${donation.lastDonationDate}</td>
        `;
        tbody.appendChild(row);
    });
}
const populateResumeTable = (donations) => {
    const summary = {};
    donations.forEach(donation => {
        if (!summary[donation.category]) {
            summary[donation.category] = 0;
        }
        summary[donation.category] += donation.quantity;
    });
    const resumeTbody = document.querySelector('#resumeTable tbody');
    for (const category in summary) {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${category}</td>
            <td>${summary[category]}</td>
        `;
        resumeTbody.appendChild(row);
    }
}
const clearFilters = () => {
    document.getElementById('category').value = 'TODAS';
    document.getElementById('dateFrom').value = '';
    document.getElementById('dateTo').value = '';
    document.getElementById('deleted').value = '';
    document.getElementById('saved-filters').value = '';
}
const clearFiltersButton = document.getElementById('clear-filters');
clearFiltersButton.addEventListener('click', clearFilters);
const donations = executeQuery();
donations.then(data => populateDonationsTable(data));
donations.then(data => populateResumeTable(data));
document.getElementById('filter-form').addEventListener('submit', async function(event) {
    event.preventDefault();
    const tbody = document.querySelector('#donationsTable tbody');
    tbody.innerHTML = '';
    const formData = new FormData(event.target);
    const formProps = Object.fromEntries(formData);
    const { category, dateFrom, dateTo, deleted } = formProps;
    const args = [];
    if (category && category !== "TODAS") args.push(`category: "${category}"`);
    if (dateFrom) args.push(`dateFrom: "${dateFrom}"`);
    if (dateTo) args.push(`dateTo: "${dateTo}"`);
    if (deleted !== "") args.push(`deleted: ${deleted}`);
    else args.push(`deleted: false`);
    args.push(`madeByOurselves: false`);

    let filterQuery = `
        query {
            donationsFiltered(${args.join(', ')}) {
                category
                description
                quantity
                deleted
                lastDonationDate
            }
        }
    `;
    const res = await fetch('http://localhost:4000/graphql', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            query: filterQuery,
        }),
    });
    const data = await res.json();
    const filteredDonations = data.data.donationsFiltered;
    populateDonationsTable(filteredDonations);
});