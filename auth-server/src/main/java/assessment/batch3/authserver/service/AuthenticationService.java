package assessment.batch3.authserver.service;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import assessment.batch3.authserver.Utils;
import assessment.batch3.authserver.Exception.AuthErrorException;
import assessment.batch3.authserver.model.LoginForm;
import assessment.batch3.authserver.repo.AuthenticationRepo;

@Service
public class AuthenticationService {
    private static final Logger log = LoggerFactory.getLogger(AuthenticationService.class);
    private static final RestTemplate client = new RestTemplate();

    private static final String URL = "https://auth.chuklee.com/api/authenticate";

    @Autowired
    private AuthenticationRepo repo;

    /*
     * Builds URL for API call to endpoint
     * - throws error if authentication failed or endpoint is down
     */
    public void authenticate(LoginForm formData) {
        URI url = UriComponentsBuilder
                .fromHttpUrl(URL)
                .build().toUri();

        RequestEntity<?> request = RequestEntity
                .post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Utils.toJsonLogin(formData));

        // log.info(request.toString());

        try {
            ResponseEntity<String> response = client.exchange(request, String.class);

            if (response.getStatusCode() != HttpStatus.ACCEPTED)
                throw new AuthErrorException("Authentication unsuccessful.");

        } catch (RestClientException e) {
            log.info(">>> End point is down...");

            // TODO: Comment out this line to simulate successful authentication
            throw new AuthErrorException("Authentication not working.");
        }

    }

    public Integer setLoginAttempt(String username) {
        return repo.setLoginAttempt(username);
    }

    public void disableUser(String username) {
        repo.setUserDisabled(username);
    }

    public Boolean getUserDisabled(String username) {
        return repo.getUserDisabled(username);
    }
}
