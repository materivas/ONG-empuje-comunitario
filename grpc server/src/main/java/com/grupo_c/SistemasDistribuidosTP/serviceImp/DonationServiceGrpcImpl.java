package com.grupo_c.SistemasDistribuidosTP.serviceImp;

import com.grupo_c.SistemasDistribuidosTP.entity.Donation;
import com.grupo_c.SistemasDistribuidosTP.exception.donation.DonationException;
import com.grupo_c.SistemasDistribuidosTP.mapper.DonationMapper;
import com.grupo_c.SistemasDistribuidosTP.service.DonationServiceClass;
import com.grupo_c.SistemasDistribuidosTP.service.DonationServiceGrpc;
import com.grupo_c.SistemasDistribuidosTP.service.IDonationService;
import com.grupo_c.SistemasDistribuidosTP.service.UtilsServiceClass;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DonationServiceGrpcImpl extends DonationServiceGrpc.DonationServiceImplBase {
    private final IDonationService donationService;
    public DonationServiceGrpcImpl(IDonationService donationService) {
        this.donationService = donationService;
    }

    @Override
    public void getDonationList(UtilsServiceClass.Empty request, StreamObserver<DonationServiceClass.DonationListResponse> responseObserver) {
        try {
            List<Donation> donations = donationService.findAll();
            DonationServiceClass.DonationListResponse donationListResponses =
                    DonationMapper.toDonationListResponse(donations);
            responseObserver.onNext(donationListResponses);
            responseObserver.onCompleted();
        } catch (Exception e) {
            e.printStackTrace();
            responseObserver.onError(
                    Status.INTERNAL
                            .withDescription("Error obteniendo lista de donaciones: " + e.getMessage())
                            .withCause(e)
                            .asRuntimeException()
            );
        }
    }

    @Override
    public void createDonation(DonationServiceClass.DonationDTO request, StreamObserver<UtilsServiceClass.Response> responseObserver) {
        try {
            Donation donation = DonationMapper.toDonation(request);
            donationService.save(donation);
        } catch (DonationException donationException) {
            donationException.printStackTrace();
            sendResponse(responseObserver, donationException.getMessage(), false);
            return;
        }
        sendResponse(responseObserver, "Donación persistida correctamente.", true);
    }

    @Override
    public void updateStock(DonationServiceClass.UpdateStockRequest request, StreamObserver<UtilsServiceClass.Response> responseObserver) {
        try {
            List<Donation> donations = DonationMapper.toDonations(request.getItemsList());
            donationService.updateStock(donations);
        } catch (DonationException donationException) {
            donationException.printStackTrace();
            sendResponse(responseObserver, donationException.getMessage(), false);
            return;
        }
        sendResponse(responseObserver, "Stocks actualizados correctamente.", true);
    }

    private static void sendResponse(StreamObserver<UtilsServiceClass.Response> responseObserver, String message, boolean succeeded) {
        responseObserver.onNext(
                UtilsServiceClass.Response.newBuilder()
                        .setMessage(message)
                        .setSucceeded(succeeded)
                        .build()
        );
        responseObserver.onCompleted();
    }
}
