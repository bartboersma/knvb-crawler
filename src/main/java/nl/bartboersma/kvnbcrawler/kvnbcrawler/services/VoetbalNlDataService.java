package nl.bartboersma.kvnbcrawler.kvnbcrawler.services;

import lombok.extern.slf4j.Slf4j;
import nl.bartboersma.kvnbcrawler.kvnbcrawler.models.Club;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class VoetbalNlDataService {

  @Value("${voetbalnl.teams.url}")
  private String voetbalnlTeamsUrl;

  @Value("${voetbalnl.session.value}")
  private String voetbalnlSessionValue;

  private RestTemplate restTemplate;

  public VoetbalNlDataService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  List<Club> executeSearch(List<Club> listOfClubs) {
    HttpHeaders httpHeaders = new HttpHeaders();

    //ToDo replace value with current session cookie
    httpHeaders.add("Cookie", voetbalnlSessionValue);

    HttpEntity entity = new HttpEntity(httpHeaders);


    for (Club club : listOfClubs) {
      voetbalnlTeamsUrl = StringUtils.replace(voetbalnlTeamsUrl, "{}", club.getVoetbalNlId());

      ResponseEntity<String> response = restTemplate.exchange(voetbalnlTeamsUrl, HttpMethod.GET, entity, String.class);


      if (response != null && response.getBody() != null) {
         extractData(club, response.getBody());
      }
      voetbalnlTeamsUrl = StringUtils.replace(voetbalnlTeamsUrl, club.getVoetbalNlId(), "{}");
    }

    cleanListOfClubs(listOfClubs);
    return listOfClubs;
  }

  private void extractData(final Club club, final String response) {
    log.info("Currently fetching teams for club: {}", club.getName());

    Document htmlDom = Jsoup.parse(response);

    Elements clubName = htmlDom.getElementsByClass("dashboard-header-maintitle");
    Elements elements = htmlDom.getElementsByClass("team-follow-name");

    if (clubName != null && clubName.get(0) != null) {
      club.setVoetbalNlName(clubName.get(0).text());
    } else {
      log.error("No club name found");
    }

    List<String> listOfTeams = new ArrayList<>();
    for(Element element : elements) {
      listOfTeams.add(element.text());
    }
    club.setTeams(listOfTeams);
  }

  private void cleanListOfClubs(final List<Club> listOfClubs) {
    log.info("Number of clubs before cleaning: {}", listOfClubs.size());
    for (Club club : listOfClubs) {
      if (club.getTeams().isEmpty()) {
        listOfClubs.remove(club);
      }
    }
    log.info("Number of clubs before after: {}", listOfClubs.size());
  }
}
