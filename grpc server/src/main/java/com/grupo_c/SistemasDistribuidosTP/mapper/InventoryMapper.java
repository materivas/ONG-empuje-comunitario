package com.grupo_c.SistemasDistribuidosTP.mapper;

import com.grupo_c.SistemasDistribuidosTP.entity.Inventory;
import com.grupo_c.SistemasDistribuidosTP.entity.User;
import com.grupo_c.SistemasDistribuidosTP.service.InventoryServiceClass;
import com.grupo_c.SistemasDistribuidosTP.service.UserServiceClass;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;


public class InventoryMapper {

    public InventoryServiceClass.InventoryDTO toInventoryDTO(Inventory inventory,
                                                         UserServiceClass.UserSimpleDTO userCreate,
                                                         UserServiceClass.UserSimpleDTO userModify) {
        InventoryServiceClass.InventoryDTO.Builder builder = InventoryServiceClass.InventoryDTO.newBuilder();

        if (inventory.getId() != null) {
            builder.setIdInventory(inventory.getId());
        } else {
            builder.setIdInventory(0L);
        }

        if (userCreate != null) builder.setUserCreate(userCreate);
        if (userModify != null) builder.setUserModify(userModify);

        builder.setCategory(inventory.getCategory() != null ? inventory.getCategory().name() : "");
        builder.setDescription(inventory.getDescription() != null ? inventory.getDescription() : "");
        builder.setQuantity(inventory.getQuantity() != null ? inventory.getQuantity() : 0);
        builder.setIsDeleted(inventory.getisDeleted() != null ? inventory.getisDeleted() : false);

        if (inventory.getCreationDate() != null) {
            long epoch = inventory.getCreationDate().atZone(ZoneOffset.UTC).toEpochSecond();
            builder.setCreationDate(epoch);
        } else {
            builder.setCreationDate(0L);
        }

        if (inventory.getModificationDate() != null) {
            long epoch = inventory.getModificationDate().atZone(ZoneOffset.UTC).toEpochSecond();
            builder.setModificationDate(epoch);
        } else {
            builder.setModificationDate(0L);
        }

        return builder.build();
    }

    public Inventory toEntity(InventoryServiceClass.InventoryDTO dto, User userCreator, User userModify) {
        Inventory inventory = new Inventory();

        if (dto.getIdInventory() != 0) {
            inventory.setId(dto.getIdInventory());
        }

        inventory.setUserCreator(userCreator);
        inventory.setUserModify(userModify);

        if (dto.getCategory() != null && !dto.getCategory().isEmpty()) {
            inventory.setCategory(Inventory.Category.valueOf(dto.getCategory()));
        }

        inventory.setDescription(dto.getDescription() != null ? dto.getDescription() : "");
        inventory.setQuantity(dto.getQuantity());
        inventory.setisDeleted(dto.getIsDeleted());

        if (dto.getCreationDate() != 0) {
            inventory.setCreationDate(LocalDateTime.ofInstant(Instant.ofEpochSecond(dto.getCreationDate()), ZoneId.systemDefault()));
        }

        if (dto.getModificationDate() != 0) {
            inventory.setModificationDate(LocalDateTime.ofInstant(Instant.ofEpochSecond(dto.getModificationDate()), ZoneId.systemDefault()));
        }

        return inventory;
    }
    
    // ---- NUEVO MÉTODO PARA MAPEAD DE ITEM A ENTIDAD ----
    public Inventory grpcToInventory(InventoryServiceClass.InventoryItem grpcItem, User currentUser) {
        Inventory inventory = new Inventory();
        inventory.setCategory(Inventory.Category.valueOf(grpcItem.getCategory()));
        inventory.setDescription(grpcItem.getDescription());
        inventory.setQuantity(grpcItem.getQuantity());
        // El método save se encargará de los usuarios y fechas
        return inventory;
    }


    public UserServiceClass.UserSimpleDTO toUserSimpleDTO(User user) {
        UserServiceClass.UserSimpleDTO.Builder b = UserServiceClass.UserSimpleDTO.newBuilder();
        if (user != null) {
            if (user.getId() != null) b.setId(user.getId());
            b.setUsername(user.getUsername() != null ? user.getUsername() : "");
        } else {
            b.setId(0L).setUsername("");
        }
        return b.build();
    }

    public LocalDateTime toLocalDateTime(long epochSeconds) {
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(epochSeconds), ZoneId.systemDefault());
    }

    public long toEpochSeconds(LocalDateTime localDateTime) {
        if (localDateTime == null) return 0L;
        return localDateTime.atZone(ZoneOffset.UTC).toEpochSecond();
    }
}