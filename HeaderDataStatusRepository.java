package com.tridel.tems_data_service.repository;

import com.tridel.tems_data_service.dao.CustomHeaderStatusDao;
import com.tridel.tems_data_service.entity.HeaderDataStatus;
import com.tridel.tems_data_service.model.response.CentricDataResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface HeaderDataStatusRepository extends JpaRepository<HeaderDataStatus,Integer>, CustomHeaderStatusDao {
    HeaderDataStatus findFirstByStationIdOrderByDateTimeDesc(Integer station);

    @Query("SELECT stationId FROM HeaderDataStatus WHERE CAST(dateTime AS DATE)=CAST(GETDATE() AS DATE)")
    List<Integer> getAllOnlineStations();

    @Query("""
            SELECT new com.tridel.tems_data_service.model.response.CentricDataResponse(
            stationId, FORMAT(dateTime, 'dd/MM/yyyy HH:mm')) FROM HeaderDataStatus
            """)
    List<CentricDataResponse> getAllStationStatus();

    @Query("""
            SELECT count(hds) FROM HeaderDataStatus hds WHERE CAST(dateTime AS DATE)=CAST(GETDATE() AS DATE)
            AND stationId IN (:ids)
            """)
    Integer getAllOnlineStationCount(List<Integer> ids);
}
