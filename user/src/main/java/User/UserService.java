package User;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public String getUser(UserRequest userRequest) {
        User newUser = null;
        try {
            User user = userRepository.findByuserId(userRequest.userId());
            if (user != null) {
                return user.getUserId();
            } else
            {
                newUser = User.builder().name(userRequest.name()).userId(userRequest.userId()).userChatId(userRequest.userChatId()).build();

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        userRepository.save(newUser);
        return newUser.getUserId();

    }
}
