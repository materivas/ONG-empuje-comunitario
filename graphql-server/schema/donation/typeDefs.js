const { gql } = require('graphql-tag');

// Esquema de la entidad donation
// Falta un campo date, pero no existe una clase en GraphQl para manejar fechas, se tiene que crear un tipo escalar personalizado
module.exports = gql`
    # define los campos que se pueden consultar
    type Donation {
        id: ID!
        category: String!
        description: String!
        quantity: Int!
        deleted: Boolean!
        lastDonationDate: String!
        madeByOurselves: Boolean!
    }

    # define los campos que se necesitan para la mutation
    input DonationInput {
        category: String!
        description: String!
        quantity: Int!
        deleted: Boolean!
    }

    # solo para consultas
    type Query {
        donations: [Donation!]!
        donation(id: ID!): Donation
        donationsByMadeByOurselves(madeByOurselves: Boolean!): [Donation!]!
        donationsFiltered(category: String, dateFrom: String, dateTo: String, deleted: Boolean!, madeByOurselves: Boolean!): [Donation!]!
    }

    # cambios en la base de datos
    # métodos de prueba, el enunciado no los pide para esta entidad
    type Mutation {
        addDonation(input: DonationInput!): Donation!
        updateDonation(id: ID!, input: DonationInput!): Donation!
        deleteDonation(id: ID!): Boolean!
    }
`;