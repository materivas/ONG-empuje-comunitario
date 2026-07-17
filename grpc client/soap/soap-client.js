const soap = require('soap');

const WSDL_URL = process.env.WSDL_URL || 'https://soap-app-latest.onrender.com/?wsdl';

const soapHeaders = {
    Auth: {
        Grupo: 'GrupoC-TM',
        Clave: 'clave-tm-c'
    }
};

async function list_presidents(orgIds) {
  try {
    const client = await soap.createClientAsync(WSDL_URL);

    client.addSoapHeader(soapHeaders, '', 'auth', 'auth.headers');

    const args = {
      org_ids: {
        string: orgIds
      }
    };

    const [result] = await client.list_presidentsAsync(args);
    return result;
  } catch (error) {
    console.error('Error en list_presidents:', error);
    throw error;
  }
}

async function list_associations(orgIds) {
  try {
    const client = await soap.createClientAsync(WSDL_URL);

    client.addSoapHeader(soapHeaders, '', 'auth', 'auth.headers');

    const args = {
      org_ids: {
        string: orgIds
      }
    };

    const [result] = await client.list_associationsAsync(args);
    return result;
  } catch (error) {
    console.error('Error en list_associations:', error);
    throw error;
  }
}

module.exports = { list_presidents, list_associations };