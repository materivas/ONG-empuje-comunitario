const pool = require('../db/connection');

// querys a la bd para la entidad donation
/* 
Esta entidad todavia no existe en la bd, sus atributos son:
- category: string
- description: string
- quantity: int
- deleted: boolean
*/
// similar a un repository en java
const Donation = {
    getAll: async () => {
        const [rows] = await pool.query('SELECT * FROM donations');
        return rows.map(donation => ({
            ...donation,
            deleted: Boolean(donation.is_deleted[0]),
            //con esto solo formateo la fecha a dd/mm/yyyy d:
            //un poco feo pero haciendolo asi no dependemos de librerias externas para hacerlo
            lastDonationDate: donation.last_donation_date
                ? `${String(donation.last_donation_date.getDate()).padStart(2, '0')}/` +
                `${String(donation.last_donation_date.getMonth() + 1).padStart(2, '0')}/` +
                `${donation.last_donation_date.getFullYear()}`
                : null,
            madeByOurselves: Boolean(donation.made_by_ourselves[0])
        }));
    },

    getById: async (id) => {
        const [rows] = await pool.query('SELECT * FROM donations WHERE id = ?', [id]);
        const donation = rows[0];
        if (!donation) return null;
        donation.deleted = Boolean(donation.is_deleted[0]);
        donation.lastDonationDate = donation.last_donation_date
            ? `${String(donation.last_donation_date.getDate()).padStart(2, '0')}/` +
            `${String(donation.last_donation_date.getMonth() + 1).padStart(2, '0')}/` +
            `${donation.last_donation_date.getFullYear()}`
            : null;
        donation.madeByOurselves = Boolean(donation.made_by_ourselves[0]);
        return donation;
    },

    getByMadeByOurselves: async (madeByOurselves) => {
        const [rows] = await pool.query('SELECT * FROM donations WHERE made_by_ourselves = ?', [madeByOurselves]);
        return rows.map(donation => ({  
            ...donation,
            deleted: Boolean(donation.is_deleted[0]),
            lastDonationDate: donation.last_donation_date
                ? `${String(donation.last_donation_date.getDate()).padStart(2, '0')}/` +
                `${String(donation.last_donation_date.getMonth() + 1).padStart(2, '0')}/` +
                `${donation.last_donation_date.getFullYear()}`
                : null,
            madeByOurselves: Boolean(donation.made_by_ourselves[0])
        }));
    },

    getFiltered: async (category, dateFrom, dateTo, deleted, madeByOurselves) => {
        let query = 'SELECT * FROM donations WHERE is_deleted = ? AND made_by_ourselves = ?';
        const params = [deleted, madeByOurselves];
        //como category, dateFrom y dateTo son opcionales, hago un checkeo de nulos
        //basicamente si se pasaron correctamente como parametros, los meto en la query. sino lkos ignoro
        if (category) {
            query += ' AND category = ?';
            params.push(category);
        }
        if (dateFrom) {
            query += ' AND last_donation_date >= ?';
            params.push(dateFrom);
        }
        if (dateTo) {
            query += ' AND last_donation_date <= ?';
            params.push(dateTo);
        }
        const [rows] = await pool.query(query, params);
        return rows.map(donation => ({
            ...donation,
            deleted: Boolean(donation.is_deleted[0]),
            lastDonationDate: donation.last_donation_date
                ? `${String(donation.last_donation_date.getDate()).padStart(2, '0')}/` +
                `${String(donation.last_donation_date.getMonth() + 1).padStart(2, '0')}/` +
                `${donation.last_donation_date.getFullYear()}`
                : null,
            madeByOurselves: Boolean(donation.made_by_ourselves[0])
        }));
    },

    // métodos de prueba, el enunciado no los pide para esta entidad
    create: async ({ category, description, quantity, deleted }) => {
        const [result] = await pool.query(
            'INSERT INTO donations (category, description, quantity, deleted) VALUES (?, ?, ?, ?)',
            [category, description, quantity, deleted]
        );
        return {
            id: result.insertId,
            category,
            description,
            quantity,
            deleted
        };
    },
    update: async ( id, { category, description, quantity, deleted }) => {
        await pool.query(
            'UPDATE donations SET category = ?, description = ?, quantity = ?, deleted = ? WHERE id = ?',
            [category, description, quantity, deleted, id]
        );
        return {
            id,
            category,
            description,
            quantity,
            deleted
        };
    },
    delete: async (id) => {
        const [result] = await pool.query('UPDATE donations SET deleted = true WHERE id = ?', [id]);
        return result.affectedRows > 0;
    }
}

module.exports = Donation;