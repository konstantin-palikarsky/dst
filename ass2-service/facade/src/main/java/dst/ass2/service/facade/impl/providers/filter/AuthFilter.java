package dst.ass2.service.facade.impl.providers.filter;

import dst.ass2.service.auth.client.IAuthenticationClient;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@Provider
@Priority(Priorities.AUTHORIZATION)
@RequireAuth
public class AuthFilter implements ContainerRequestFilter {
    @Inject
    IAuthenticationClient authClient;

    @Override
    public void filter(ContainerRequestContext requestContext) {
        String authHeader = requestContext.getHeaderString("Authorization");

        if (authHeader == null || authHeader.isEmpty()) {
            requestContext.abortWith(Response.status(
                    Response.Status.UNAUTHORIZED).build());
            return;
        }

        var authToken = parseAuthHeader(authHeader);


        if (!authClient.isTokenValid(authToken) || authToken == null) {
            requestContext.abortWith(Response.status(
                    Response.Status.UNAUTHORIZED).build());
        }
    }

    private String parseAuthHeader(String authHeader) {
        var headerArray = authHeader.split(" ");
        if (headerArray.length != 2) {
            return null;
        }

        return headerArray[1];
    }
}
