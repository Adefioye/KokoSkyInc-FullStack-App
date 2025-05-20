package com.KokoSky.WeatherService.location;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import java.util.List;
import java.util.Optional;


public interface LocationRepository extends CrudRepository<Location, String> {

    boolean existsLocationByCode(String code);

    @Query("select l from Location l where l.trashed = false")
    List<Location> findAllUntrashedLocations();

    @Query("select l from Location l where l.code = ?1 and l.trashed = false")
    Optional<Location> findUntrashedLocationsByCode(String code);

    @Modifying
    @Query("UPDATE Location l SET l.trashed = true WHERE l.code = ?1")
    void softDeleteByCode(String code);

    @Query("SELECT l FROM Location l WHERE l.trashed = false AND l.code = ?1")
    Location findByCode(String code);

    @Query("SELECT l from Location l WHERE l.trashed = false AND l.countryCode = ?1 AND l.cityName = ?2")
    Location findByCountryCodeAndCityName(String countryCode, String cityName);
}
