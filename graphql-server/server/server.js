const express = require('express');
const cors = require('cors');
const { graphqlHTTP } = require('express-graphql');
const schema = require('../schema/mergeSchemas');

const app = express();
const port = 4000;

//cors habilitado para todas las rutas
app.use(cors());

app.use('/graphql', graphqlHTTP({
  schema,
  graphiql: true
}));

app.listen(port, () => {
  console.log(`Servidor GraphQL corriendo en http://localhost:${port}/graphql`);
});