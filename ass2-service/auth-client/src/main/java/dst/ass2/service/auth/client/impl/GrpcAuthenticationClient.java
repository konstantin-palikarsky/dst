package dst.ass2.service.auth.client.impl;

import dst.ass2.service.api.auth.AuthenticationException;
import dst.ass2.service.api.auth.NoSuchUserException;
import dst.ass2.service.api.auth.proto.AuthServiceGrpc;
import dst.ass2.service.api.auth.proto.AuthenticationRequest;
import dst.ass2.service.api.auth.proto.TokenValidationRequest;
import dst.ass2.service.auth.client.AuthenticationClientProperties;
import dst.ass2.service.auth.client.IAuthenticationClient;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;

public class GrpcAuthenticationClient implements IAuthenticationClient {
    private final ManagedChannel channel;
    private final AuthServiceGrpc.AuthServiceBlockingStub service;

    public GrpcAuthenticationClient(AuthenticationClientProperties properties) {
        channel =
                ManagedChannelBuilder.forAddress(properties.getHost(), properties.getPort())
                        .usePlaintext()
                        .build();
        service = AuthServiceGrpc.newBlockingStub(channel);

    }

    @Override
    public String authenticate(String email, String password) throws NoSuchUserException, AuthenticationException {
        var request =
                AuthenticationRequest.newBuilder()
                        .setEmail(email)
                        .setPassword(password)
                        .build();

        try {

            return service.authenticate(request).getToken();

        }catch (Exception e){
            Status status = Status.fromThrowable(e);
            switch (status.getCode()){
                case NOT_FOUND:

                    throw new NoSuchUserException("Unknown user email");
                case UNAUTHENTICATED:
                    throw new AuthenticationException("Wrong password");
                default:
                    throw new RuntimeException("Unknown exception thrown by rpc server");
            }
        }

    }

    @Override
    public boolean isTokenValid(String token) {
        var request =
                TokenValidationRequest.newBuilder()
                        .setToken(token)
                        .build();

        return service.validateToken(request).getValid();
    }

    @Override
    public void close() {
        channel.shutdown();
    }
}
