const DonationFilter = require('../../models/DonationFilter');

module.exports = {
  Query: {
    filters: () => DonationFilter.getAll(),
    filtersByUser: (_, { user_id }) => DonationFilter.getByUser(user_id),
    filterById: async (_, { id }) => DonationFilter.getById(id)
  },

  Mutation: {
    addFilter: (_, { input }) => DonationFilter.create(input),
    updateFilter: (_, { id, input }) => DonationFilter.update(id, input),
    deleteFilter: async (_, { id }) => DonationFilter.delete(id)
  }
};