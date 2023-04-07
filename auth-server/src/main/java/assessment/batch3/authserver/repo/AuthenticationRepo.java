package assessment.batch3.authserver.repo;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class AuthenticationRepo {
    @Autowired
    private RedisTemplate<String, Object> template;

    public Integer setLoginAttempt(String username) {
        Integer attempts = getLoginAttempt(username);
        if (attempts == null)
            attempts = 0;
        attempts += 1;

        template.opsForValue().set(
                username, attempts, Duration.ofMinutes(30));

        return attempts;
    }

    public Integer getLoginAttempt(String username) {
        return (Integer) template.opsForValue().get(username);
    }

    // Disables user
    public void setUserDisabled(String username) {
        template.opsForValue().set(
                setTimeOutKey(username), true, Duration.ofMinutes(30));
    }

    public Boolean getUserDisabled(String username) {
        return (Boolean) template.opsForValue().get(
                setTimeOutKey(username));
    }

    private String setTimeOutKey(String username) {
        return "disabled-" + username;
    }
}
