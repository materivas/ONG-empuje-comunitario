const { gql } = require('graphql-tag');

// Esquema de la entidad event
// Falta hacer join con la tabla donaciones
module.exports = gql`
    # define los campos que se pueden consultar

    type User{
        id: ID!
        username: String!
    }

    type EventDonation{
        category: String!
        description: String!
        quantity: Int!
        distributionDate: String!
    }

    type Event {
        id: ID!
        name: String!
        description: String!
        is_completed: Boolean!
        date: String!
        participants: [User!]!
        inventory: [EventDonation!]!
    }

    # solo para consultas
    type Query {
        events: [Event!]!
        event(id: ID!): Event
    }
`;