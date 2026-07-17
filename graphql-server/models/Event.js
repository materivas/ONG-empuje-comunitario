const pool = require('../db/connection');

// querys a la bd para la entidad event 
// similar a un repository en java
const Event = {
    getAllEvents: async () => {
        const [rows] = await pool.query('SELECT * FROM events');
        return rows.map(event => ({
            ...event,
            // conversión Buffer -> Boolean
            is_completed: Boolean(event.is_completed),
            // conversión Datetime -> String
            date: new Date(event.date).toISOString() 
        }));
    },
    getAll: async () => {
        const [events] = await pool.query('SELECT * FROM events');
        const [participants] = await pool.query(`
            SELECT eu.id_event, u.id, u.username
            FROM event_user eu
            JOIN users u ON eu.id_user = u.id
        `);
        const [inventory] = await pool.query(`
            SELECT ei.id_event, i.category, i.description, ei.quantity_distributed, ei.distribution_date
            FROM event_inventory ei
            JOIN inventories i ON i.id = ei.id_inventory
        `);

        const grouped = events.map(event => ({
            ...event,
            is_completed: Boolean(event.is_completed),
            date: new Date(event.date).toISOString(),
            participants: participants
            .filter(p => p.id_event === event.id)
            .map(p => ({
                id: p.id,
                username: p.username,
            })),
            inventory: inventory
            .filter( i => i.id_event === event.id)
            .map(i => ({
                category: i.category,
                description: i.description,
                quantity: i.quantity_distributed,
                distributionDate: new Date(i.distribution_date).toISOString(),
            }))
        }));

        return grouped;
    },
    getById: async (id) => {
        const [rows] = await pool.query('SELECT * FROM events WHERE id = ?', [id]);
        const event = rows[0];

        const [participants] = await pool.query(`
            SELECT u.id, u.username
            FROM event_user eu
            JOIN users u ON eu.id_user = u.id
            WHERE eu.id_event = ? `, [id]
        );
        const [inventory] = await pool.query(`
            SELECT i.category, i.description, ei.quantity_distributed, ei.distribution_date
            FROM event_inventory ei
            JOIN inventories i ON i.id = ei.id_inventory
            WHERE ei.id_event = ? `, [id]
        );

        return {
            ...event,
            is_completed: Boolean(event.is_completed),
            date: new Date(event.date).toISOString(),
            participants: participants
            .map(p => ({
                id: p.id,
                username: p.username,
            })),
            inventory: inventory
            .map(i => ({
                category: i.category,
                description: i.description,
                quantity: i.quantity_distributed,
                distributionDate: new Date(i.distribution_date).toISOString(),
            }))
        };
    }
}

module.exports = Event;