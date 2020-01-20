package com.space.service;

import com.space.controller.ShipOrder;
import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.repository.ShipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.MutableSortDefinition;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.*;

@Service
public class ShipService {

    @Autowired
    private ShipRepository shipRepository;

    public PagedListHolder<Ship> findAll(String name, String planet, ShipType shipType, Long after, Long before,
                                         Boolean isUsed, Double minSpeed, Double maxSpeed, Integer minCrewSize,
                                         Integer maxCrewSize, Double minRating, Double maxRating, ShipOrder order,
                                         Integer pageNumber, Integer pageSize) {

        List<Ship> filteredShips =  getShipsByFilter(name, planet, shipType, after, before, isUsed, minSpeed, maxSpeed,
                minCrewSize, maxCrewSize, minRating, maxRating);

        PagedListHolder<Ship> listHolder = new PagedListHolder<>(filteredShips);

        if(order != null) {
            listHolder.setSort(new MutableSortDefinition(order.getFieldName(), false, true));
            listHolder.resort();
        }

        if(pageNumber != null)
            listHolder.setPage(pageNumber);
        else
            listHolder.setPage(0);

        if(pageSize != null)
            listHolder.setPageSize(pageSize);
        else
            listHolder.setPageSize(3);

        return listHolder;
    }

    public Integer getShipsCount(String name, String planet, ShipType shipType, Long after, Long before, Boolean isUsed,
                                 Double minSpeed, Double maxSpeed, Integer minCrewSize, Integer maxCrewSize,
                                 Double minRating, Double maxRating) {

        return getShipsByFilter(name, planet, shipType, after, before, isUsed, minSpeed, maxSpeed, minCrewSize, maxCrewSize,
                minRating, maxRating).size();
    }

    public ResponseEntity<Ship> getShipById(Long id) {

        if(id < 1) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        Ship result = null;

        Optional<Ship> optionalShip= shipRepository.findById(id);

        if(optionalShip.isPresent())
            result = optionalShip.get();
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    public ResponseEntity<Ship> createShip(Ship ship) {

        String name = ship.getName();
        String planet = ship.getPlanet();
        ShipType shipType = ship.getShipType();
        Date prodDate = ship.getProdDate();
        Boolean isUsed = ship.getUsed();
        Double speed = ship.getSpeed();
        Integer crewSize = ship.getCrewSize();

        if(name == null || name.equals("") || planet == null || shipType == null || prodDate == null
                || speed == null || crewSize == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        Long startProdDate = new Date(2800 - 1900, 0, 1).getTime();
        Long endProdDate = new Date(3019 - 1900, 0, 1).getTime();

        if(name.length() > 50 || planet.length() > 50 || prodDate.getTime() < startProdDate
                || prodDate.getTime() > endProdDate || speed < 0.01 || speed > 0.99 || crewSize < 1 || crewSize > 9999)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        if(isUsed == null) {
            ship.setUsed(false);
            isUsed = ship.getUsed();
        }

        DecimalFormat df = new DecimalFormat("#.##");
        ship.setRating(Double.parseDouble(df.format((80 * speed * (isUsed ? 0.5 : 1)) /
                (3019 - (prodDate.getYear() + 1900) + 1)).replace(',', '.')));

        shipRepository.save(ship);

        return new ResponseEntity<>(ship, HttpStatus.OK);
    }

    public ResponseEntity<Ship> updateShip(Long id, Ship ship) {

        Ship updatableShip;

        if(getShipById(id).getStatusCodeValue() != 200)
            return getShipById(id);
        else
            updatableShip = getShipById(id).getBody();

        String name = ship.getName();
        String planet = ship.getPlanet();
        ShipType shipType = ship.getShipType();
        Date prodDate = ship.getProdDate();
        Boolean isUsed = ship.getUsed();
        Double speed = ship.getSpeed();
        Integer crewSize = ship.getCrewSize();
        Long startProdDate = new Date(2800 - 1900, 0, 1).getTime();
        Long endProdDate = new Date(3019 - 1900, 0, 1).getTime();

        if(name != null && (name.length() == 0 || name.length() > 50))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        if(planet != null && (planet.equals("") || planet.length() > 50))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        if(prodDate != null && (prodDate.getTime() < startProdDate || prodDate.getTime() > endProdDate))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        if(speed != null && (speed < 0.01 || speed > 0.99))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        if(crewSize != null && (crewSize < 1 || crewSize > 9999))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        if(name != null)
            updatableShip.setName(name);
        if(planet != null)
            updatableShip.setPlanet(planet);
        if(shipType != null)
            updatableShip.setShipType(shipType);
        if(prodDate != null)
            updatableShip.setProdDate(prodDate);
        if(isUsed != null)
            updatableShip.setUsed(isUsed);
        if(speed != null)
            updatableShip.setSpeed(speed);
        if(crewSize != null)
            updatableShip.setCrewSize(crewSize);

        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        updatableShip.setRating(Double.parseDouble(decimalFormat.format((80 * updatableShip.getSpeed() *
                (updatableShip.getUsed() ? 0.5 : 1)) / (3019 - (updatableShip.getProdDate().getYear() + 1900) + 1))
                .replace(',', '.')));

        shipRepository.save(updatableShip);

        return new ResponseEntity<>(updatableShip, HttpStatus.OK);
    }

    public ResponseEntity deleteShip(Long id) {

        if(getShipById(id).getStatusCodeValue() != 200)
            return getShipById(id);

        shipRepository.deleteById(id);

        return new ResponseEntity(HttpStatus.OK);
    }

    private List<Ship> getShipsByFilter(String name, String planet, ShipType shipType, Long after, Long before, Boolean isUsed,
                                        Double minSpeed, Double maxSpeed, Integer minCrewSize, Integer maxCrewSize,
                                        Double minRating, Double maxRating) {

        List<Ship> allShipsFromDataBase = shipRepository.findAll();

        if(name == null && planet == null && shipType == null && after == null && before == null && isUsed == null
                && minSpeed == null && maxSpeed == null && minCrewSize == null && maxCrewSize == null && minRating == null
                && maxRating == null) return allShipsFromDataBase;

        List<Ship> result = new ArrayList<>();

        for(Ship ship : allShipsFromDataBase) {

            String shipName = ship.getName();
            String shipPlanet = ship.getPlanet();
            ShipType typeOfShip = ship.getShipType();
            long prodTime = ship.getProdDate().getTime();
            Boolean isShipUsed = ship.getUsed();
            Double shipSpeed = ship.getSpeed();
            Integer shipCrewSize = ship.getCrewSize();
            Double shipRating = ship.getRating();

            if(name != null)
                if(!shipName.contains(name)) continue;

            if(planet != null)
                if(!shipPlanet.contains(planet)) continue;

            if(shipType != null)
                if (!typeOfShip.equals(shipType)) continue;


            if(after != null && before != null)
                if(prodTime <= after || prodTime >= before) continue;

            if(isUsed != null) {
                if (!isShipUsed.equals(isUsed)) {
                    continue;
                }
            }

            if(minSpeed != null && maxSpeed != null) {
                if (shipSpeed <= minSpeed || shipSpeed >= maxSpeed) continue;
            } else if(minSpeed != null || maxSpeed != null) {
                if (minSpeed != null) {
                    if (shipSpeed <= minSpeed) continue;
                }
                else if(maxSpeed != null)
                    if(shipSpeed >= maxSpeed) continue;
            }

            if(minCrewSize != null && maxCrewSize != null) {
                if (shipCrewSize <= minCrewSize || shipCrewSize >= maxCrewSize) continue;
            } else if(minCrewSize != null || maxCrewSize != null) {
                if (minCrewSize != null) {
                    if (shipCrewSize <= minCrewSize) continue;
                }
                else if(maxCrewSize != null)
                    if(shipCrewSize >= maxCrewSize) continue;
            }

            if(minRating != null && maxRating != null) {
                if (shipRating <= minRating || shipRating >= maxRating){
                    continue;
                }
            } else if(minRating != null || maxRating != null) {
                if (minRating != null) {
                    if (shipRating <= minRating) continue;
                }
                else if(maxRating != null)
                    if(shipRating >= maxRating) continue;
            }

            result.add(ship);

        }

        return result;
    }
}