package com.empujecomunitario.rest_server.repository;

import com.empujecomunitario.rest_server.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface IEventRepository extends JpaRepository<Event, Long> {

        @Query("SELECT e FROM Event e WHERE " +
                        "(:fechaDesde IS NULL OR e.date >= :fechaDesde) AND " +
                        "(:fechaHasta IS NULL OR e.date <= :fechaHasta) AND " +
                        "(:repartoDonaciones IS NULL OR COALESCE(e.hasDonationDistribution, false) = :repartoDonaciones) AND "
                        +
                        "(:completados IS NULL OR COALESCE(e.isCompleted, false) = :completados)")
        List<Event> findWithFilters(@Param("fechaDesde") LocalDateTime fechaDesde,
                        @Param("fechaHasta") LocalDateTime fechaHasta,
                        @Param("repartoDonaciones") Boolean repartoDonaciones,
                        @Param("completados") Boolean completados);
}
