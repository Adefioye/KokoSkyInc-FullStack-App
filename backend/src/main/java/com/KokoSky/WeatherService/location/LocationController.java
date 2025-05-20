package com.KokoSky.WeatherService.location;

import jakarta.validation.Valid;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/locations")
public class LocationController {

    private final LocationService locationService;
    private final ModelMapper modelMapper;

    public LocationController(
            LocationService locationService,
            ModelMapper modelMapper
            ) {
        this.locationService = locationService;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    public ResponseEntity<LocationDTO> addLocation(@RequestBody @Valid LocationDTO dto) {
        Location newLocation = locationService.addLocation(dto2Entity(dto));

        URI uriLocation = URI.create("/api/v1/locations/%s".formatted(newLocation.getCode()));

        return ResponseEntity.created(uriLocation).body(entity2DTO(newLocation));
    }

    @GetMapping
    public ResponseEntity<List<LocationDTO>> getLocations() {
        List<Location> locations = locationService.getLocations();

        if (locations.size() == 0) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok().body(listEntity2ListDTO(locations));
    }

    @GetMapping("{code}")
    public ResponseEntity<LocationDTO> getLocations(@PathVariable String code) {

        Location locationByCode = locationService.getLocationByCode(code);
        return ResponseEntity.ok().body(entity2DTO(locationByCode));
    }

    @PutMapping
    public ResponseEntity<LocationDTO> updateLocation(@RequestBody @Valid LocationDTO dtoNewRequest) {

        Location updatedLocationByCode = locationService.updateLocationByCode(dto2Entity(dtoNewRequest));
        return ResponseEntity.ok().body(entity2DTO(updatedLocationByCode));
    }

    @DeleteMapping("{code}")
    public ResponseEntity<?> deleteLocation(@PathVariable String code) {
        locationService.deleteLocationByCode(code);

        return ResponseEntity.noContent().build();
    }

    private List<LocationDTO> listEntity2ListDTO(List<Location> listEntity) {

        return listEntity.stream().map(entity -> entity2DTO(entity))
                .collect(Collectors.toList());

    }

    private LocationDTO entity2DTO(Location entity) {
        return modelMapper.map(entity, LocationDTO.class);
    }

    private Location dto2Entity(LocationDTO dto) {
        return modelMapper.map(dto, Location.class);
    }
}
