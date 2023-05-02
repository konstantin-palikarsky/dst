package dst.ass2.service.auth.grpc;

import dst.ass2.service.auth.impl.AuthServiceGrpcImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@ManagedBean
public class GrpcServerRunner implements IGrpcServerRunner {
    // Ideally this will be injected from an env var,
    // to ensure execution doesn't time out in production
    private static final long MAX_TIMEOUT = Long.MAX_VALUE;

    public GrpcServerRunner() {
    }

    @Inject
    private AuthServiceGrpcImpl authService;

    @Inject
    private GrpcServerProperties grpcServerProperties;

    @Override
    public void run() throws IOException {
        Server server = ServerBuilder
                .forPort(grpcServerProperties.getPort())
                .addService(authService).build();

        server.start();
        try {
            server.awaitTermination(MAX_TIMEOUT, TimeUnit.SECONDS);
        } catch (InterruptedException ignored) {
        }

    }
}
