package nl.bartboersma.kvnbcrawler.kvnbcrawler.controllers;

import nl.bartboersma.kvnbcrawler.kvnbcrawler.models.Club;
import nl.bartboersma.kvnbcrawler.kvnbcrawler.models.KnvbSearchClubRequest;
import nl.bartboersma.kvnbcrawler.kvnbcrawler.services.KnvbDataService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class KnvbDataController {

  private KnvbDataService knvbDataService;

  public KnvbDataController(KnvbDataService knvbDataService) {
    this.knvbDataService = knvbDataService;
  }

  @PostMapping(value = "/search-for-club", consumes = "application/json")
  public ResponseEntity<List<Club>> searchForClub(@RequestBody KnvbSearchClubRequest request) {
    return ResponseEntity.ok(knvbDataService.executeSearch(request));
  }
}
