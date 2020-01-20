package com.space.controller;

import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.service.ShipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class ShipController {

    @Autowired
    private ShipService shipService;

    @GetMapping("/rest/ships")
    @ResponseBody
    public List<Ship> findAll(@RequestParam(value = "name", required = false) String name,
                              @RequestParam(value = "planet", required = false) String planet,
                              @RequestParam(value = "shipType", required = false) ShipType shipType,
                              @RequestParam(value = "after", required = false) Long after,
                              @RequestParam(value = "before", required = false) Long before,
                              @RequestParam(value = "isUsed", required = false) Boolean isUsed,
                              @RequestParam(value = "minSpeed", required = false) Double minSpeed,
                              @RequestParam(value = "maxSpeed", required = false) Double maxSpeed,
                              @RequestParam(value = "minCrewSize", required = false) Integer minCrewSize,
                              @RequestParam(value = "maxCrewSize", required = false) Integer maxCrewSize,
                              @RequestParam(value = "minRating", required = false) Double minRating,
                              @RequestParam(value = "maxRating", required = false) Double maxRating,
                              @RequestParam(value = "order", required = false) ShipOrder order,
                              @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                              @RequestParam(value = "pageSize", required = false) Integer pageSize) {

        return  shipService.findAll(name, planet, shipType, after, before, isUsed, minSpeed, maxSpeed, minCrewSize,
                maxCrewSize, minRating, maxRating, order, pageNumber, pageSize).getPageList();
    }

    @GetMapping("/rest/ships/count")
    @ResponseBody
    public Integer getShipsCount(@RequestParam(value = "name", required = false) String name,
                                 @RequestParam(value = "planet", required = false) String planet,
                                 @RequestParam(value = "shipType", required = false) ShipType shipType,
                                 @RequestParam(value = "after", required = false) Long after,
                                 @RequestParam(value = "before", required = false) Long before,
                                 @RequestParam(value = "isUsed", required = false) Boolean isUsed,
                                 @RequestParam(value = "minSpeed", required = false) Double minSpeed,
                                 @RequestParam(value = "maxSpeed", required = false) Double maxSpeed,
                                 @RequestParam(value = "minCrewSize", required = false) Integer minCrewSize,
                                 @RequestParam(value = "maxCrewSize", required = false) Integer maxCrewSize,
                                 @RequestParam(value = "minRating", required = false) Double minRating,
                                 @RequestParam(value = "maxRating", required = false) Double maxRating) {

        return shipService.getShipsCount(name, planet, shipType, after, before, isUsed, minSpeed, maxSpeed, minCrewSize,
                maxCrewSize, minRating, maxRating);
    }

    @PostMapping("/rest/ships/")
    @ResponseBody
    public ResponseEntity<Ship> createShip(@RequestBody() Ship ship){
        if(ship == null) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return shipService.createShip(ship);
    }

    @GetMapping(path = "/rest/ships/{id}")
    @ResponseBody
    public ResponseEntity<Ship> getShip(@PathVariable(value = "id") Long id) {
        return shipService.getShipById(id);
    }

    @PostMapping(path = "/rest/ships/{id}")
    @ResponseBody
    public ResponseEntity<Ship> updateShip(@PathVariable(value = "id") Long id, @RequestBody Ship ship) {
        return shipService.updateShip(id, ship);
    }

    @DeleteMapping(path = "/rest/ships/{id}")
    @ResponseBody
    public ResponseEntity deleteShip(@PathVariable(value = "id") Long id) {
        return shipService.deleteShip(id);
    }
}