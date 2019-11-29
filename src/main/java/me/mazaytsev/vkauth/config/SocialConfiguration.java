package me.mazaytsev.vkauth.config;

import lombok.RequiredArgsConstructor;
import me.mazaytsev.vkauth.config.env.SocialEncryptEnvironment;
import me.mazaytsev.vkauth.config.env.VkontakteEnvironment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.social.UserIdSource;
import org.springframework.social.config.annotation.ConnectionFactoryConfigurer;
import org.springframework.social.config.annotation.EnableSocial;
import org.springframework.social.config.annotation.SocialConfigurer;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.jdbc.JdbcUsersConnectionRepository;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.social.security.AuthenticationNameUserIdSource;
import org.springframework.social.vkontakte.connect.VKontakteConnectionFactory;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.util.Arrays;

import static org.springframework.security.crypto.encrypt.Encryptors.noOpText;
import static org.springframework.security.crypto.encrypt.Encryptors.text;
import static org.springframework.util.StringUtils.hasText;

@EnableSocial
@Configuration
@RequiredArgsConstructor
public class SocialConfiguration implements SocialConfigurer {
    private final DataSource dataSource;
    private final Environment environment;
    private final VkontakteEnvironment vkEnv;
    private final SocialEncryptEnvironment encryptEnv;
    private final ConnectionSignUp connectionSignUpService;

    @Value("${vk.scope}")
    private static String scope;

    @Override
    public void addConnectionFactories(ConnectionFactoryConfigurer connectionCfg, Environment env) {
        VKontakteConnectionFactory vkcf = new VKontakteConnectionFactory(vkEnv.getClientId(), vkEnv.getClientSecret());
        vkcf.setScope(scope);
        connectionCfg.addConnectionFactory(vkcf);
    }

    @Override
    public UserIdSource getUserIdSource() {
        return new AuthenticationNameUserIdSource();
    }

    @Override
    public UsersConnectionRepository getUsersConnectionRepository(ConnectionFactoryLocator locator) {
        if (Arrays.asList(environment.getActiveProfiles()).contains("prod")) {
            Assert.hasText(encryptEnv.getKey(), "in production auth tokens must be secured");
        }

        TextEncryptor encryptor = hasText(encryptEnv.getKey()) ? text(encryptEnv.getKey(), encryptEnv.getSalt()) : noOpText();
        JdbcUsersConnectionRepository connectionRepository = new JdbcUsersConnectionRepository(dataSource, locator, encryptor);
        connectionRepository.setConnectionSignUp(connectionSignUpService);
        return connectionRepository;
    }

    @Bean
    public ProviderSignInUtils providerSignInUtils(ConnectionFactoryLocator locator, UsersConnectionRepository repository) {
        return new ProviderSignInUtils(locator, repository);
    }

}
