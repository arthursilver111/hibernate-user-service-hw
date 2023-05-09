package mate.academy.security;

import mate.academy.exception.AuthenticationException;
import mate.academy.exception.RegistrationException;
import mate.academy.lib.Inject;
import mate.academy.lib.Service;
import mate.academy.model.User;
import mate.academy.service.UserService;
import mate.academy.util.HashUtil;

import java.util.Optional;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    @Inject
    private UserService userService;

    @Override
    public void register(String email, String password) {
        Optional<User> userFomDbByLogin = userService.findByEmail(email);
        if (userFomDbByLogin.isPresent()) {
            throw new RegistrationException("Can not register user with email: " + email);
        }
        User user = new User(email, password);
        userService.add(user);
    }

    @Override
    public User login(String email, String password) {
        Optional<User> userFomDbByLogin = userService.findByEmail(email);
        if (userFomDbByLogin.isEmpty()) {
            throw new AuthenticationException("Can not login by email: " + email);
        }
        User userFromDb = userFomDbByLogin.get();
        String userHashedPassword = HashUtil.hashPassword(password, userFromDb.getSalt());
        if (userFromDb.getPassword().equals(userHashedPassword)) {
            return userFromDb;
        }
        throw new AuthenticationException("Email: " + email + " or password incorrect");
    }
}
