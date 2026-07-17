const { Kafka } = require('kafkajs');
const clientId = "donation-requests-consumer";
const broker = "kafka:9092";
const groupId = `donation-requests-${Date.now()}`;
const topicName = "solicitud-donaciones";
const donationRequestsDeletedConsumer = require("./donationRequestsDeleted");

const kafka = new Kafka({
  clientId: clientId,
  brokers: [broker],
});

const donationRequests = [];

const donationRequestsConsumer = kafka.consumer({ 
  groupId: groupId 
});

const startConsuming = async () => {
  await donationRequestsConsumer.connect();
  await donationRequestsConsumer.subscribe({ topic: topicName, fromBeginning: true });
  await donationRequestsConsumer.run({
    eachMessage: async ({ topic, partition, message }) => {
      const donationRequest = JSON.parse(message.value.toString());
      donationRequests.push(donationRequest);
    },
  });
};

const donationRequestsProducer = kafka.producer();

const publish = async (message) => {
  await donationRequestsProducer.connect();
  await donationRequestsProducer.send({
    topic: topicName,
    messages: [
      { value: JSON.stringify(message) }
    ]
  })
};

const getAllDonationRequestsAndDeletedOnes = () => {
  const allDonationRequests = [];
  donationRequests.forEach(donationRequest => {
    allDonationRequests.push(donationRequest);
  });
  return allDonationRequests;
};
const getAllDonationRequests = () => {
  const deletedRequests = donationRequestsDeletedConsumer.getDeletedRequests();
  const allDonationRequests = [];
  donationRequests.forEach(donationRequest => {
    let wasntDeleted = true;
    deletedRequests.forEach(deleted => {
      if(donationRequest.request_id === deleted) {
        wasntDeleted = false;
      }
    });
    if(wasntDeleted) {
      allDonationRequests.push(donationRequest);
    }
  });
  return allDonationRequests;
};
const getAllDonationRequestsExceptOurs = () => {
  const deletedRequests = donationRequestsDeletedConsumer.getDeletedRequests();
  const allDonationRequests = [];
  const ourOrganizationID = 1;
  donationRequests.forEach(donationRequest => {
    let wasntDeleted = true;
    deletedRequests.forEach(deleted => {
      if(donationRequest.request_id === deleted) {
        wasntDeleted = false;
      }
    });
    if(wasntDeleted && donationRequest.organization_id !== ourOrganizationID) {
      allDonationRequests.push(donationRequest);
    }
  });
  return allDonationRequests;
};
const getOurDonationRequests = () => {
  const deletedRequests = donationRequestsDeletedConsumer.getDeletedRequests();
  const allDonationRequests = [];
  const ourOrganizationID = 1;
  donationRequests.forEach(donationRequest => {
    let wasntDeleted = true;
    deletedRequests.forEach(deleted => {
      if(donationRequest.request_id === deleted) {
        wasntDeleted = false;
      }
    });
    if(wasntDeleted && donationRequest.organization_id === ourOrganizationID) {
      allDonationRequests.push(donationRequest);
    }
  });
  return allDonationRequests;
};

module.exports = { 
  startConsuming,
  getAllDonationRequestsAndDeletedOnes,
  getAllDonationRequests, 
  getAllDonationRequestsExceptOurs, 
  getOurDonationRequests, 
  publish 
};