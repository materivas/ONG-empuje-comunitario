package com.grupo_c.SistemasDistribuidosTP.serviceImp;

import com.grupo_c.SistemasDistribuidosTP.entity.Event;
import com.grupo_c.SistemasDistribuidosTP.entity.EventInventory;
import com.grupo_c.SistemasDistribuidosTP.entity.Inventory;
import com.grupo_c.SistemasDistribuidosTP.entity.User;
import com.grupo_c.SistemasDistribuidosTP.exception.user.UserNotFoundException;
import com.grupo_c.SistemasDistribuidosTP.mapper.EventInventoryMapper;
import com.grupo_c.SistemasDistribuidosTP.mapper.EventMapper;
import com.grupo_c.SistemasDistribuidosTP.repository.IEventRepository;
import com.grupo_c.SistemasDistribuidosTP.repository.IUserRepository;
import com.grupo_c.SistemasDistribuidosTP.service.*;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

@Service
public class EventServiceGrpcImpl extends EventServiceGrpc.EventServiceImplBase{

    @Autowired
    private final EventServiceImpl eventService;
    private final IEventRepository eventRepository;
    private final IUserRepository userRepository;
    private final IUserService userService;
    private final IInventoryService inventoryService;
    private final EventMapper eventMapper;
    private final EventInventoryMapper eventInventoryMapper;

    @Autowired
    public EventServiceGrpcImpl(
            EventServiceImpl eventService,
            IEventRepository eventRepository,
            IUserRepository userRepository,
            IUserService userService,
            IInventoryService inventoryService
    ) {
        this.eventService = eventService;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.userService = userService;
        this.inventoryService = inventoryService;
        this.eventMapper = new EventMapper();
        this.eventInventoryMapper = new EventInventoryMapper();
    }


    //recibe el evento y la lista de ids de los participantes
    @Override
    public void createEvent (EventServiceClass.EventDto event,
                             StreamObserver<UtilsServiceClass.Response> responseStreamObserver){

        Event e = null;
        try {
            e = eventMapper.toEntity(event);
            e.setIsCompleted(false);
        } catch (Exception exception) {
            responseStreamObserver.onError(new StatusRuntimeException(Status.INVALID_ARGUMENT.withDescription(exception.getMessage())));
            return;
        }

        List<Long> ids = new ArrayList<>();
        for (EventServiceClass.ParticipantDto participant : event.getParticipantsList()){
            ids.add(participant.getId());
        }

        Set<User> participants = new HashSet<>(userRepository.findAllById(ids));
        e.setParticipants(participants);

        eventRepository.save(e);

        UtilsServiceClass.Response response = UtilsServiceClass.Response
                .newBuilder()
                .setMessage("Evento Creado")
                .setSucceeded(true)
                .build();
        responseStreamObserver.onNext(response);
        responseStreamObserver.onCompleted();
    }

    @Override
    public void modifyEvent (EventServiceClass.EventWithParticipantsDto event,
                             StreamObserver<UtilsServiceClass.Response> responseStreamObserver){

        Event e = eventRepository.findById(event.getId()).orElse(null);

        if(e == null){
            responseStreamObserver.onError(new StatusRuntimeException(Status.NOT_FOUND.withDescription("El evento no existe.")));
            return;
        }

        List<Long> ids = new ArrayList<>();
        for (EventServiceClass.ParticipantDto participant : event.getParticipantsList()){
            ids.add(participant.getId());
        }

        Set<User> participants = new HashSet<>(userRepository.findAllById(ids));

        e.setName(event.getName());
        e.setDescription(event.getDescription());

        LocalDateTime newDate = eventMapper.toLocalDateTime(event.getDate());
        //si la fecha es distinta se cambia
        if (!e.getDate().equals(newDate)){
            //si la nueva fecha es anterior a hoy tira error
            if (newDate.isBefore(LocalDateTime.now(ZoneOffset.ofHours(-3)))){
                responseStreamObserver.onError(new StatusRuntimeException(Status.INVALID_ARGUMENT.withDescription("La fecha del evento no puede ser en el pasado.")));
                return;
            }
            e.setDate(newDate);
        }

        e.setIsCompleted(event.getIsCompleted());
        e.setParticipants(participants);

        eventRepository.save(e);

        UtilsServiceClass.Response response = UtilsServiceClass.Response
                .newBuilder()
                .setMessage("Evento Modificado")
                .setSucceeded(true)
                .build();
        responseStreamObserver.onNext(response);
        responseStreamObserver.onCompleted();
    }

    @Override
    public void deleteEvent (EventServiceClass.EventRequest request,
                             StreamObserver<UtilsServiceClass.Response> responseStreamObserver){

        Event event = eventRepository.findById(request.getId()).orElse(null);

        if(event == null){
            responseStreamObserver.onError(new StatusRuntimeException(Status.NOT_FOUND.withDescription("El evento no existe.")));
            return;
        }

        if (event.getIsCompleted()){
            responseStreamObserver.onError(new StatusRuntimeException(Status.FAILED_PRECONDITION.withDescription("El evento ya finalizó.")));
            return;
        }

        eventRepository.delete(event);

        UtilsServiceClass.Response response = UtilsServiceClass.Response
                .newBuilder()
                .setMessage("Evento Eliminado")
                .setSucceeded(true)
                .build();
        responseStreamObserver.onNext(response);
        responseStreamObserver.onCompleted();

    }

    @Override
    public void assignUserToEvent (EventServiceClass.EventAssignOrDeleteRequest request,
                                   StreamObserver<UtilsServiceClass.Response> responseStreamObserver){

        Event e = eventRepository.findByIdJoinParticipants(request.getEventId()).orElse(null);
        User u = userRepository.findById(request.getUserId()).orElse(null);

        if(e==null){
            responseStreamObserver.onError(new StatusRuntimeException(Status.NOT_FOUND.withDescription("No se encontró el evento.")));
            return;
        }
        if(u==null){
            responseStreamObserver.onError(new StatusRuntimeException(Status.NOT_FOUND.withDescription("No se encontró el usuario.")));
            return;
        }

        e.getParticipants().add(u);

        eventRepository.save(e);

        UtilsServiceClass.Response response = UtilsServiceClass.Response
                .newBuilder()
                .setMessage("El usuario " + u.getUsername() + " fue añadido al evento " + e.getName())
                .setSucceeded(true)
                .build();
        responseStreamObserver.onNext(response);
        responseStreamObserver.onCompleted();
    }

    @Override
    public void deleteUserFromEvent (EventServiceClass.EventAssignOrDeleteRequest request,
                                     StreamObserver<UtilsServiceClass.Response> responseStreamObserver){

        Event e = eventRepository.findByIdJoinParticipants(request.getEventId()).orElse(null);
        User u = userRepository.findByIdJoinEvents(request.getUserId()).orElse(null);

        if(e==null){
            responseStreamObserver.onError(new StatusRuntimeException(Status.NOT_FOUND.withDescription("No se encontró el evento.")));
            return;
        }
        if(u==null){
            responseStreamObserver.onError(new StatusRuntimeException(Status.NOT_FOUND.withDescription("No se encontró el usuario.")));
            return;
        }

        e.getParticipants().removeIf(p->p.getId().equals(u.getId()));

        eventRepository.save(e);

        UtilsServiceClass.Response response = UtilsServiceClass.Response
                .newBuilder()
                .setMessage("El usuario " + u.getUsername() + " fue eliminado del evento " + e.getName())
                .setSucceeded(true)
                .build();
        responseStreamObserver.onNext(response);
        responseStreamObserver.onCompleted();
    }

    @Override
    public void getEvent (EventServiceClass.EventRequest request,
                          StreamObserver<EventServiceClass.EventWithParticipantsDto> responseStreamObserver){

        Event e = eventService.findByIdJoinParticipants(request.getId());

        if(e==null){
            responseStreamObserver.onError(new StatusRuntimeException(Status.NOT_FOUND.withDescription("No se encontró el evento.")));
            return;
        }

        EventServiceClass.EventWithParticipantsDto response = eventMapper.toEventWithParticipantsDto(e);

        responseStreamObserver.onNext(response);
        responseStreamObserver.onCompleted();
    }

    @Override
    public void getEventsWithParticipantsList (UtilsServiceClass.Empty request,
                                               StreamObserver<EventServiceClass.EventsWithParticipantsList> responseStreamObserver){

        //desactivado hasta solucionar problemas con la zona horaria de docker
        //eventService.markPastEventsAsCompleted();

        List<Event> events = eventRepository.findAllJoinParticipants();
        List<EventServiceClass.EventWithParticipantsDto> eventsWithParticipantsDto = new ArrayList<>();

        for(Event event: events){
            eventsWithParticipantsDto.add(eventMapper.toEventWithParticipantsDto(event));
        }

        EventServiceClass.EventsWithParticipantsList response = EventServiceClass.EventsWithParticipantsList.newBuilder()
                .addAllEvents(eventsWithParticipantsDto)
                .build();

        responseStreamObserver.onNext(response);
        responseStreamObserver.onCompleted();
    }

    @Override
    public void getEventsWithoutParticipantsList (EventServiceClass.EventRequest request,
                                               StreamObserver<EventServiceClass.EventsWithoutParticipantsList> responseStreamObserver){

        //desactivado hasta solucionar problemas con la zona horaria de docker
        eventService.markPastEventsAsCompletedWithDateTime(LocalDateTime.now(ZoneOffset.ofHours(-3)));

        List<Event> events = eventRepository.findAllJoinParticipants();
        List<EventServiceClass.EventWithoutParticipantsDto> eventsWithoutParticipantsDto = new ArrayList<>();

        for(Event event : events){
            //valida si el usuario que hizo el request forma parte del evento
            boolean joined = event.getParticipants().stream().anyMatch(p-> p.getId().equals(request.getId()));

            eventsWithoutParticipantsDto.add(eventMapper.toEventWithoutParticipantsDto(event, joined));
        }

        EventServiceClass.EventsWithoutParticipantsList response = EventServiceClass.EventsWithoutParticipantsList.
                newBuilder()
                .addAllEvents(eventsWithoutParticipantsDto)
                .build();
        responseStreamObserver.onNext(response);
        responseStreamObserver.onCompleted();
    }

    @Override
    public void registerEventInventory(EventServiceClass.RegisterEventInventoryRequest request,
                                       StreamObserver<UtilsServiceClass.Response> responseStreamObserver){

        Event event = eventService.findByIdJoinEventInventory(request.getEventId());
        Inventory inventory = inventoryService.findById(request.getInventoryId());
        User user = null;

        if (event == null){
            responseStreamObserver.onError(new StatusRuntimeException(Status.NOT_FOUND.withDescription("No existe el evento")));
            return;
        }
        if (inventory == null){
            responseStreamObserver.onError(new StatusRuntimeException(Status.NOT_FOUND.withDescription("No existe el inventario")));
            return;
        }
        try{
            user = userService.findById(request.getUserId());
        }catch (UserNotFoundException e){
            responseStreamObserver.onError(new StatusRuntimeException(Status.NOT_FOUND.withDescription(e.getMessage())));
            return;
        }
        if(!event.getIsCompleted()){
            responseStreamObserver.onError(new StatusRuntimeException(Status.FAILED_PRECONDITION.withDescription("El evento aún no finalizó")));
            return;
        }

        try {
            inventoryService.decreaseStock(inventory, user, request.getQuantity());
        }catch (Exception e){
            responseStreamObserver.onError(new StatusRuntimeException(Status.INVALID_ARGUMENT.withDescription(e.getMessage())));
            return;
        }

        event.addDistributedInventory(inventory, user, request.getQuantity());
        eventRepository.save(event);

        UtilsServiceClass.Response response = UtilsServiceClass.Response
                .newBuilder()
                .setMessage("Donación registrada exitosamente")
                .setSucceeded(true)
                .build();
        responseStreamObserver.onNext(response);
        responseStreamObserver.onCompleted();
    }

    @Override
    public void getEventInventory(EventServiceClass.EventRequest request,
                                  StreamObserver<EventServiceClass.EventInventoryList> responseStreamObserver){

        if (eventService.findById(request.getId()) == null){
            responseStreamObserver.onError(new StatusRuntimeException(Status.NOT_FOUND.withDescription("No existe el evento")));
            return;
        }

        List<EventInventory> inventories = eventService.findByEventIdOrdered(request.getId());

        List<EventServiceClass.DonationDto> donations = new ArrayList<>();

        for(EventInventory eventInventory: inventories){
            donations.add(eventInventoryMapper.toDonationDto(eventInventory));
        }

        EventServiceClass.EventInventoryList response = EventServiceClass.EventInventoryList.newBuilder()
                .addAllDonations(donations)
                .build();
        responseStreamObserver.onNext(response);
        responseStreamObserver.onCompleted();
    }

}
