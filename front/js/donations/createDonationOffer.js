document.addEventListener('DOMContentLoaded', () => {
    const donationsContainer = document.getElementById('donations-container');
    const addDonationBtn = document.getElementById('add-donation-btn');
    const createOfferForm = document.getElementById('createOfferForm');

    const donationCategories = ["ROPA", "ALIMENTOS", "JUGUETES", "UTILES_ESCOLARES"];

    function addDonationRow() {
        const row = document.createElement('div');
        row.classList.add('donation-row');

        const categorySelect = document.createElement('select');
        categorySelect.name = 'category';
        donationCategories.forEach(cat => {
            const option = document.createElement('option');
            option.value = cat;
            option.textContent = cat.replace('_', ' ');
            categorySelect.appendChild(option);
        });

        const descriptionInput = document.createElement('input');
        descriptionInput.type = 'text';
        descriptionInput.name = 'description';
        descriptionInput.placeholder = 'Descripción (ej: Arroz)';
        descriptionInput.required = true;

        const quantityInput = document.createElement('input');
        quantityInput.type = 'text';
        quantityInput.name = 'quantity';
        quantityInput.placeholder = 'Cantidad (ej: 20kg)';
        quantityInput.required = true;

        const removeBtn = document.createElement('button');
        removeBtn.type = 'button';
        removeBtn.textContent = 'cancelar';
        removeBtn.classList.add('btn-remove');
        removeBtn.onclick = () => row.remove();

        row.appendChild(categorySelect);
        row.appendChild(descriptionInput);
        row.appendChild(quantityInput);
        row.appendChild(removeBtn);
        donationsContainer.appendChild(row);
    }

    addDonationBtn.addEventListener('click', addDonationRow);

    createOfferForm.addEventListener('submit', async (e) => {
        e.preventDefault();

        const donationRows = donationsContainer.querySelectorAll('.donation-row');
        const donations = [];

        donationRows.forEach(row => {
            donations.push({
                Categoria: row.querySelector('[name="category"]').value,
                Descripcion: row.querySelector('[name="description"]').value,
                Cantidad: row.querySelector('[name="quantity"]').value
            });
        });

        if (donations.length === 0) {
            alert('Debes agregar al menos un item a la oferta.');
            return;
        }

        try {
            const response = await fetch('/donation-offers/api/create', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ donations })
            });

            const result = await response.json();

            if (response.ok) {
                alert(result.message);
                window.location.href = '/donation-offers'; // Redirige a la lista de ofertas
            } else {
                throw new Error(result.message);
            }
        } catch (error) {
            alert(`Error al publicar la oferta: ${error.message}`);
        }
    });

    // Agregar la primera fila automáticamente al cargar la página
    addDonationRow();
});