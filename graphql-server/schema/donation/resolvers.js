const Donation = require('../../models/Donation');

// resolvers para la entidad donation
// similar a un service en java
module.exports = {
  Query: {
    donations: () => Donation.getAll(),
    donation: (_, { id }) => Donation.getById(id),
    donationsByMadeByOurselves: (_, { madeByOurselves }) => Donation.getByMadeByOurselves(madeByOurselves),
    donationsFiltered: (_, { category, dateFrom, dateTo, deleted, madeByOurselves }) => Donation.getFiltered(category, dateFrom, dateTo, deleted, madeByOurselves)
  },
  // métodos de prueba, el enunciado no los pide para esta entidad
  Mutation: {
    addDonation: (_, { input }) => Donation.create(input),
    updateDonation: (_, { id, input }) => Donation.update(id, input),
    deleteDonation: async (_, { id }) => Donation.delete(id)
  }
};