package com.example.raffle_api.controllers;

import com.example.raffle_api.model.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.*;

@RestController
@Tag(name = "Raffle Controller", description = "HTTP-API for promo management")
@RequestMapping("/promo")
public class RaffleController {

    private Map<Long, Promo> activePromos;

    @PostConstruct
    private void init() {
        activePromos = new HashMap<>();
    }

    @Operation(summary = "Create a promo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Promo created (id returned)",
                    content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "400", description = "Invalid body (no name field)",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Book not found",
                    content = @Content) })
    @PostMapping
    public ResponseEntity<Long> createPromo(@RequestBody Map<String, String> payload) {
        if (!payload.containsKey("name")) return ResponseEntity.badRequest().build();
        Promo newPromo = new ExtendedPromo(payload.get("name"), payload.get("description"));
        activePromos.put(newPromo.getId(), newPromo);
        return ResponseEntity.ok(newPromo.getId());
    }

    @Operation(summary = "Get list of all active promos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Invalid body (no name field)",
                    content = {@Content(mediaType = "application/json")})})
    @GetMapping
    public ResponseEntity<?> getPromos() {
        List<Map<String, Object>> response = new ArrayList<>();
        for (Promo p: activePromos.values()) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", p.getId());
            map.put("name", p.getName());
            map.put("description", p.getDescription());
            response.add(map);
        }
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get information about promo")
    @Parameters(value = {
            @Parameter(name = "id", description = "id of a promo to get info about")
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Promo created (id returned)",
                    content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "400", description = "Invalid id",
                    content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "404", description = "No such promo (id not found)",
                    content = @Content) })
    @GetMapping("/{id}")
    public ResponseEntity<ExtendedPromo> getPromo(@PathVariable("id") long id) {
        return ResponseEntity.ok((ExtendedPromo) activePromos.get(id));
    }


    @Operation(summary = "Edit a promo")
    @Parameters(value = {
            @Parameter(name = "id", description = "id of a promo to edit")
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfuly edited",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid request (id or body)",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "No such promo (id not found)",
                    content = @Content) })
    @PutMapping("/{id}")
    public ResponseEntity<?> editPromo(@PathVariable("id") long id, @RequestBody Map<String, String> payload) {
        if (!activePromos.containsKey(id)) return ResponseEntity.notFound().build();
        if (payload.get("name") == null || payload.get("name").isEmpty()) return ResponseEntity.badRequest().build();
        Promo promo = activePromos.get(id);
        promo.setName(payload.get("name"));
        if (payload.containsKey("description")) promo.setDesc(payload.get("description"));
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Remove a promo")
    @Parameters(value = {
            @Parameter(name = "id", description = "id of a promo to remove"),
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully removed",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid request (id or body)",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "No such promo (id not found)",
                    content = @Content) })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePromo(@PathVariable("id") long id) {
        if (!activePromos.containsKey(id)) return ResponseEntity.notFound().build();
        activePromos.put(id, null);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Add a participant")
    @Parameters(value = {
            @Parameter(name = "id", description = "id of a promo to add a participant to")
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully added",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid request (id or body)",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "No such promo (id not found)",
                    content = @Content) })
    @PostMapping("/{id}/participant")
    public ResponseEntity<?> addParticipant(@PathVariable("id") long id, @RequestBody Map<String, String> payload) {
        if (!activePromos.containsKey(id)) return ResponseEntity.notFound().build();
        if (!payload.containsKey("name")) return ResponseEntity.badRequest().build();

        ExtendedPromo promo = (ExtendedPromo) activePromos.get(id);
        Participant participant = new Participant(payload.get("name"));
        promo.addParticipant(participant);

        return ResponseEntity.ok(participant.getId());
    }

    @Operation(summary = "Remove a participant")
    @Parameters(value = {
            @Parameter(name = "promoId", description = "id of a promo to remove a participant from"),
            @Parameter(name = "participantId", description = "id of a participant to remove from promo")
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully removed",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid request (id's or body)",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "No such promo or participant(id not found)",
                    content = @Content) })
    @DeleteMapping("/{promoId}/participant/{participantId}")
    public ResponseEntity<?> deleteParticipant(@PathVariable("promoId") long promoId,
                                               @PathVariable("participantId") long participantId) {
        if (!activePromos.containsKey(promoId)) return ResponseEntity.notFound().build();
        ExtendedPromo promo = (ExtendedPromo) activePromos.get(promoId);
        Participant p = promo.getParticipant(participantId);
        if (p == null) return ResponseEntity.notFound().build();
        promo.removeParticipant(participantId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Add a prize")
    @Parameters(value = {
            @Parameter(name = "id", description = "id of a promo to add a prize to")
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully added",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid request (id or body)",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "No such promo (id not found)",
                    content = @Content) })
    @PostMapping("/{id}/prize")
    public ResponseEntity<?> addPrize(@PathVariable("id") long id, @RequestBody Map<String, String> payload) {
        if (!activePromos.containsKey(id)) return ResponseEntity.notFound().build();
        if (!payload.containsKey("description")) return ResponseEntity.badRequest().build();

        ExtendedPromo promo = (ExtendedPromo) activePromos.get(id);
        Prize prize = new Prize(payload.get("description"));
        promo.addPrize(prize);
        return ResponseEntity.ok(prize.getId());
    }

    @Operation(summary = "Remove a prize")
    @Parameters(value = {
            @Parameter(name = "promoId", description = "id of a promo to remove prize from"),
            @Parameter(name = "prizeId", description = "id of a prize ro remove from promo")
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully removed",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid request (id's or body)",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "No such promo or prize (id not found)",
                    content = @Content) })
    @DeleteMapping("/{promoId}/prize/{prizeId}")
    public ResponseEntity<?> deletePrize(@PathVariable("promoId") long promoId,
                                               @PathVariable("prizeId") long prizeId) {
        if (!activePromos.containsKey(promoId)) return ResponseEntity.notFound().build();
        ExtendedPromo promo = (ExtendedPromo) activePromos.get(promoId);
        Prize p = promo.getPrize(prizeId);
        if (p == null) return ResponseEntity.notFound().build();
        promo.removePrize(prizeId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Run a raffle")
    @Parameters(value = {
            @Parameter(name = "id", description = "id of a promo to run")
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully run",
                    content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "400", description = "Invalid request (id or body)",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "No such promo (id not found)",
                    content = @Content),
            @ApiResponse(responseCode = "409", description = "Can't run a raffle (prize and participant sizes differ)",
                    content = @Content)})
    @PostMapping("/{id}/raffle")
    public ResponseEntity<?> raffle(@PathVariable("id") long id) {
        if (!activePromos.containsKey(id)) ResponseEntity.notFound().build();
        ExtendedPromo promo = (ExtendedPromo) activePromos.get(id);
        List<Participant> participants = promo.getParticipants();
        Collections.shuffle(participants);
        List<Prize> prizes = promo.getPrizes();
        if (participants.size() != prizes.size()) return ResponseEntity.status(HttpStatus.CONFLICT).build();

        List<RaffleResult> results = new ArrayList<>();
        for (int i = 0; i < participants.size(); i++) {
            results.add(new RaffleResult(participants.get(i), prizes.get(i)));
        }
        return ResponseEntity.ok(results);
    }

}
