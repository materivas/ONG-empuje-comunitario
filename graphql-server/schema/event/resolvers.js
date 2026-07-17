const Event = require('../../models/Event');

// resolvers para la entidad event
// similar a un service en java
module.exports = {
  Query: {
    events: () => Event.getAll(),
    event: (_, { id }) => Event.getById(id)
  }
};