const { makeExecutableSchema } = require('@graphql-tools/schema');
const { mergeTypeDefs, mergeResolvers } = require('@graphql-tools/merge');

// ---------------- importar los typeDefs y resolvers de las entidades ----------------
const donation_TypeDefs = require('./donation/typeDefs');
const donation_Resolvers = require('./donation/resolvers');
const event_TypeDefs = require('./event/typeDefs');
const event_Resolvers = require('./event/resolvers');
const donationFilter_TypeDefs = require('./donationFilter/typeDefs');
const donationFilter_Resolvers = require('./donationFilter/resolvers');

// mergea los typeDefs y resolvers de las entidades
const typeDefs = mergeTypeDefs([
    donation_TypeDefs, 
    event_TypeDefs, 
    donationFilter_TypeDefs
]);
const resolvers = mergeResolvers([
    donation_Resolvers,
    event_Resolvers, 
    donationFilter_Resolvers
]);

// crea y exporta el esquema ejecutable
module.exports = makeExecutableSchema({ typeDefs, resolvers });