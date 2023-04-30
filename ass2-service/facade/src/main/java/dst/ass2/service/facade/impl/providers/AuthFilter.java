package dst.ass2.service.facade.impl.providers;

import dst.ass2.service.auth.client.IAuthenticationClient;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

@PreMatching
@Provider
public class AuthFilter implements ContainerRequestFilter {
    @Inject
    IAuthenticationClient authClient;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String authHeader = requestContext.getHeaderString("Authorization");

        if (!requestContext.getUriInfo().getPath().startsWith("/trips")) {
            return;
        }

        if (authHeader == null || authHeader.isEmpty()) {
            requestContext.abortWith(Response.status(
                    Response.Status.UNAUTHORIZED).build());
            return;
        }
    }
}
