const { gql } = require('graphql-tag');

module.exports = gql`
    # datos de la entidad
    type DonationFilter {
        id: ID!
        name: String!
        category: String
        dateFrom: String
        dateTo: String
        deleted: Boolean
        user_id: ID!
    }

    # datos para crear/modificar el filtro
    input DonationFilterInput {
        name: String!
        category: String
        dateFrom: String
        dateTo: String
        deleted: Boolean
        user_id: ID!
    }

    # solo para consultas
    type Query {
        filters: [DonationFilter!]!
        filtersByUser(user_id: ID!): [DonationFilter!]!
        filterById(id: ID!): DonationFilter!
    }

    # cambios en la base de datos
    type Mutation {
        addFilter(input: DonationFilterInput!): DonationFilter!
        updateFilter(id: ID!, input: DonationFilterInput!): DonationFilter!
        deleteFilter(id: ID!): Boolean!
    }
`;