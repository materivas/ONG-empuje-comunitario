const pool = require('../db/connection');

const DonationFilter = {
    mapDate(date) {
        return date ? `${String(date.getDate()).padStart(2, '0')}/` +
        `${String(date.getMonth() + 1).padStart(2, '0')}/` +
        `${date.getFullYear()}`
        : null;
    },
    mapRows(rows) {
        return rows.map(donationFilter => ({
            ...donationFilter,
            dateFrom: (donationFilter.date_from) ? DonationFilter.mapDate(donationFilter.date_from) : null,
            dateTo: (donationFilter.date_from) ? DonationFilter.mapDate(donationFilter.date_to) : null,
            deleted: (donationFilter.deleted) ? Boolean(donationFilter.deleted[0]) : null,
        }));
    },
    //queries
    getAll: async () => {
        try {
            const [rows] = await pool.query('SELECT * FROM donation_filter');
            return DonationFilter.mapRows(rows);
        } catch(error) {
            console.error("Error fetching donation filters:", error);
        }
    },
    getByUser: async (user_id) => {
        try {
            const query = 'SELECT * FROM donation_filter WHERE user_id = ?';
            const params = [user_id];
            const [rows] = await pool.query(query, params);
            return DonationFilter.mapRows(rows);
        } catch(error) {
            console.error("Error fetching donation filters by user:", error);
        }
    },
    getById: async (id) => {
        try {
            const query = 'SELECT * FROM donation_filter WHERE id = ?';
            const params = [id];
            const [rows] = await pool.query(query, params);
            const mappedRows = DonationFilter.mapRows(rows);
            return mappedRows.length > 0 ? mappedRows[0] : null;
        } catch(error) {
            console.error("Error fetching donation filter by id:", error);
        }
    },
    //mutations
    create: async ({ name, category, dateFrom, dateTo, deleted, user_id }) => {
        try {
            const query = 'INSERT INTO donation_filter (name, category, date_from, date_to, deleted, user_id) VALUES (?, ?, ?, ?, ?, ?)';
            const params = [name, category, dateFrom, dateTo, deleted, user_id];
            const [result] = await pool.query(query, params);
            const [rows] = await pool.query('SELECT * FROM donation_filter WHERE id = ?', [result.insertId]);
            return DonationFilter.mapRows(rows)[0];
        } catch(error) {
            console.error("Error creating donation filter:", error);
        }
    },
    update: async ( id, { name, category, dateFrom, dateTo, deleted }) => {
        try {
            const query = 'UPDATE donation_filter SET name = ?, category = ?, date_from = ?, date_to = ?, deleted = ? WHERE id = ?';
            const params = [name, category, dateFrom, dateTo, deleted, id];
            await pool.query(query, params);
            const [rows] = await pool.query('SELECT * FROM donation_filter WHERE id = ?', [id]);
            return DonationFilter.mapRows(rows)[0];
        } catch(error) {
            console.error("Error updating donation filter:", error);
        }
    },
    delete: async (id) => {
        try {
            const query = 'DELETE FROM donation_filter WHERE id = ?';
            const params = [id];
            await pool.query(query, params);
            return true;
        } catch(error) {
            console.error("Error deleting donation filter:", error);
            return false;
        }
    }
}

module.exports = DonationFilter;