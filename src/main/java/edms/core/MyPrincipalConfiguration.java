/*package edms.core;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component()
@Service({PrincipalConfiguration.class, SecurityConfiguration.class})
public class MyPrincipalConfiguration extends ConfigurationBase implements PrincipalConfiguration {

    public MyPrincipalConfiguration() {
        super();
    }

    public MyPrincipalConfiguration(SecurityProvider securityProvider) {
        super(securityProvider, securityProvider.getParameters(NAME));
    }

    @Activate
    private void activate(Map<String, Object> properties) {
        setParameters(ConfigurationParameters.of(properties));
    }


    //---------------------------------------------< PrincipalConfiguration >---
    @Nonnull
    @Override
    public PrincipalManager getPrincipalManager(Root root, NamePathMapper namePathMapper) {
        PrincipalProvider principalProvider = getPrincipalProvider(root, namePathMapper);
        return new PrincipalManagerImpl(principalProvider);
    }

    @Nonnull
    @Override
    public PrincipalProvider getPrincipalProvider(Root root, NamePathMapper namePathMapper) {
        return new MyPrincipalProvider(root, namePathMapper);
    }

    //----------------------------------------------< SecurityConfiguration >---
    @Nonnull
    @Override
    public String getName() {
        return NAME;
    }
}*/